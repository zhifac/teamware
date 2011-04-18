package gleam.executive.workflow.action.common;

import gleam.docservice.proxy.IAAAlgorithm;
import gleam.docservice.proxy.IAAResult;
import gleam.executive.service.DocServiceManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.graph.exe.ExecutionContext;
import org.springframework.util.StringUtils;
import gleam.executive.workflow.util.JPDLConstants;
import gleam.executive.workflow.sm.JbpmHandlerProxy;

import bsh.Interpreter;

/**
 * <p>
 * A workflow action handler to perform a set of IAA calculations and
 * fire one of two transitions based on the result.
 * </p>
 *
 * <p>
 * The handler expects the following inputs:
 * <dl>
 * <dt>inVarDocumentId</dt>
 * <dd>The variable name containing the ID of the document</dd>
 * <dt>iaaAlgorithm</dt>
 * <dd>The variable name containing the IAA algorithm</dd>
 * <dt>inVarIAAConfig</dt>
 * <dd>The variable name containing a configuration string describing
 * the calculations to perform (see below)</dd>
 * <dt>inVarConditionScript</dt>
 * <dd>The variable name containing the BeanShell script that defines
 * the condition to test. It should return a boolean value when
 * <code>eval</code>ed</dd>
 * </dl>
 * </p>
 *
 * <p>
 * The configuration string looks like:
 * </p>
 *
 * <pre>
 *   type=Classification; feature=relationship; algorithm=pairwise-kappa,
 *   type=DiscourseSupport; algorithm=pairwise-f-measure; variable=dsResult,
 *   ...
 * </pre>
 *
 * <p>
 * i.e. a comma separated list of items, where each item is a
 * semicolon-separated list of key=value pairs. Whitespace around the
 * equals, commas and semicolons is ignored. Each item in the CSV list
 * defines a single IAA calculation, to be performed over the annotation
 * sets named for the annotators who have annotated this document (i.e.
 * the JPDLConstants.ANNOTATED_BY_PREFIX<i>docID</i> variable value). Supported key/value pairs are:
 * </p>
 *
 * <dl>
 * <dt>type (required)</dt>
 * <dd>The annotation type over which to calculate the IAA</dd>
 * <dt>feature (optional)</dt>
 * <dd>The feature name for a classification-type calculation. If
 * specified, the IAA code calculates the annotators' agreement over
 * each possible value of the feature (e.g. if the annotation type is
 * "Mention" this would compute agreement over Mention.class="Person",
 * Mention.class="Location", etc.). If omitted, only the annotation
 * spans are considered.</dd>
 * <dt>variable (optional, defaults to <i>type</i>)</dt>
 * <dd>The calculation results are made available to the condition
 * script in BeanShell variables. By default the variable is named after
 * the annotation type, but this can be overridden using
 * <code>variable=x</code>, which is useful if the annotation type is
 * not a valid variable name, or if you are doing two calculations over
 * the same annotation type (but different algorithms).</dd>
 * <dt>algorithm (optional, defaults to pairwise-f-measure)</dt>
 * <dd>The IAA algorithm to use, as defined by
 * {@link IAAAlgorithm#algorithmName()}</dd>
 * </dl>
 *
 * <p>
 * When executed, the handler calls the doc-service
 * <code>calculateIAA</code> method, once for each item in the config
 * string. It then creates a BeanShell interpreter and sets the
 * variables defined by the config string to contain the
 * {@link IAAResult} objects returned. It also sets the variable
 * "annotators" to a <code>String[]</code> containing the annotation
 * set names used for the calculations. It then evaluates the condition
 * script in the presence of these variables.
 * </p>
 *
 * <p>
 * The condition script should be a BeanShell fragment that returns
 * <code>true</code> if the document requires manual review (typically
 * if agreement is low), and <code>false</code> if it does not
 * (agreement is high). The script can be a simple boolean expression,
 * e.g.
 * </p>
 *
 * <pre>
 * Classification.agreement &lt; 0.85
 * </pre>
 *
 * <p>
 * or more complex, including function definitions, e.g.
 * </p>
 *
 * <pre>
 * boolean anyPairBelow(float min) {
 *   for(String a1 : annotators) {
 *     for(String a2 : annotators) {
 *       if(dsResult.getFMeasureForPair(a1, a2).f1() &lt; min) {
 *         return true;
 *       }
 *     }
 *   }
 *   return false;
 * }
 *
 * anyPairBelow(0.8);
 * </pre>
 *
 * <p>
 * The intention of the above is to send the document for manual review
 * if any pair of annotators agree by less than 80% F-measure.
 * </p>
 *
 * <p>
 * If the condition script indicates that the document requires review,
 * the action will leave the node via the transition
 * {@link JPDLConstants#TRANSITION_NEEDS_REVIEW}, otherwise it will
 * leave via {@link JPDLConstants#TRANSITION_NO_REVIEW}.
 * </p>
 */

/*
 * <inVarDocumentId>documentId</inVarDocumentId>
 * <inVarIAAAlgorithm>iaaAlgorithm</inVarIAAAlgorithm>
 * <inVarIAAConfig>iaaConfig</inVarIAAConfig>
 * <inVarConditionScript>conditionScript</inVarConditionScript>
 * <inVarAnnotatedBy>annotatedBy</inVarAnnotatedBy>
 */
public class IAAActionHandler extends JbpmHandlerProxy {

  private static final long serialVersionUID = 1L;

  protected static final Log log = LogFactory.getLog(IAAActionHandler.class);

  /**
   * The default algorithm to use when none is specified.
   */
  public static final IAAAlgorithm DEFAULT_ALGORITHM =
          IAAAlgorithm.PAIRWISE_F_MEASURE;

  private String inVarDocumentId;

  private String inVarIAAConfig;

  private String inVarConditionScript;

  private String inVarAnnotatedBy;

  private DocServiceManager docServiceManager;

  @Override
  public void execute(ExecutionContext context) throws Exception {
    log.debug("IAAActionHandler START");
    // should we send this document for review? Yes unless the script
    // says no.
    boolean sendForReview = true;

    // document ID
    String documentID = (String)context.getVariable(getInVarDocumentId());
    log.debug("document ID = " + documentID);
    String[] documentIDArray = new String[]{documentID};

    // List of annotators who we want to calculate IAA between
    String annotatedByString = (String)context.getVariable(getInVarAnnotatedBy());
    log.debug("annotated by = " + annotatedByString);
    String[] annotatedBy =
            StringUtils.commaDelimitedListToStringArray(annotatedByString);

    if(annotatedBy!=null && annotatedBy.length >1){
    log.debug("Found that more than 1 annotator annotated this doc");
    // parse the config string
    String iaaConfig = (String)context.getVariable(getInVarIAAConfig());
    log.debug("Parsing configuration from \"" + iaaConfig + "\"");
    String[] iaaRuns = StringUtils.commaDelimitedListToStringArray(iaaConfig);
    String[] annotationTypes = new String[iaaRuns.length];
    IAAAlgorithm[] algorithms = new IAAAlgorithm[iaaRuns.length];
    String[] bshVariables = new String[iaaRuns.length];
    String[] featureNames = new String[iaaRuns.length];

    Pattern nameEqualsValuePattern =
            Pattern.compile("^(.*?\\S)\\s*=\\s*(\\S.*)$");

    for(int i = 0; i < iaaRuns.length; i++) {
      String[] runSplit = iaaRuns[i].trim().split("\\s*;\\s*");
      for(String token : runSplit) {
        Matcher m = nameEqualsValuePattern.matcher(token);
        if(m.matches()) {
          String key = m.group(1);
          String value = m.group(2);
          if("type".equals(key)) {
            annotationTypes[i] = value;
          }
          else if("algorithm".equals(key)) {
            algorithms[i] = getAlgorithm(value);
          }
          else if("variable".equals(key)) {
            bshVariables[i] = value;
          }
          else if("feature".equals(key)) {
            featureNames[i] = value;
          }
        }
        else {
          throw new IllegalArgumentException(
                  "Config string should be a comma-separated list "
                          + "of items of the form name=value;name=value[;name=value...], "
                          + "but got item: " + token);
        }
      }

      if(annotationTypes[i] == null) {
        throw new IllegalArgumentException("\"type\" must be specified: "
                + iaaRuns[i]);
      }

      if(bshVariables[i] == null) {
        // default to the same as the annotation type
        bshVariables[i] = annotationTypes[i];
      }

      if(algorithms[i] == null) {
        algorithms[i] = DEFAULT_ALGORITHM;
      }
    }

    if(log.isDebugEnabled()) {
      log.debug("Configuration:");
      for(int i = 0; i < annotationTypes.length; i++) {
        log.debug("Run " + i);
        log.debug("  annotation type: " + annotationTypes[i]);
        log.debug("  feature name:    " + featureNames[i]);
        log.debug("  algorithm:       " + algorithms[i]);
        log.debug("  result variable: " + bshVariables[i]);
      }
    }

    // do the calculations
    IAAResult[] iaaResults = new IAAResult[annotationTypes.length];
    for(int i = 0; i < annotationTypes.length; i++) {
      log.debug("Calling calculateIAA for run " + i);
      iaaResults[i] =
              docServiceManager.calculateIAA(documentIDArray, annotatedBy,
                      annotationTypes[i], featureNames[i], algorithms[i]);
    }


    // if we have a BeanShell script to determine whether to send the
    // document for review, run that now. We make the IAA results
    // available under the variable names supplied in the config.
    String conditionScript =
            (String)context.getVariable(getInVarConditionScript());
    if(conditionScript != null) {
      log.debug("Invoking script:");
      log.debug(conditionScript);
      Interpreter interpreter = new Interpreter();
      for(int i = 0; i < bshVariables.length; i++) {
        interpreter.set(bshVariables[i], iaaResults[i]);
      }

      interpreter.set("annotators", annotatedBy);

      sendForReview = (Boolean)interpreter.eval(conditionScript);
    }
    }
    else {
    	log.debug("Found that not more than 1 annotator annotated this doc");
    	// don't need to review single-annotated document
    	sendForReview = false;
    }

    if(sendForReview) {
      log.debug("Document needs review");
      context.leaveNode(JPDLConstants.TRANSITION_NEEDS_REVIEW);
    }
    else {
      log.debug("Document does not need review");
      context.leaveNode(JPDLConstants.TRANSITION_NO_REVIEW);
    }

  }

  private IAAAlgorithm getAlgorithm(String name) {
    for(IAAAlgorithm a : IAAAlgorithm.values()) {
      if(a.algorithmName().equals(name)) {
        return a;
      }
    }

    throw new IllegalArgumentException("No such algorithm: " + name);
  }

  public String getInVarDocumentId() {
    return inVarDocumentId;
  }

  public void setInVarDocumentId(String inVarDocumentId) {
    this.inVarDocumentId = inVarDocumentId;
  }

  public String getInVarIAAConfig() {
    return inVarIAAConfig;
  }

  public void setInVarIAAConfig(String inVarIAAConfig) {
    this.inVarIAAConfig = inVarIAAConfig;
  }

  public String getInVarConditionScript() {
    return inVarConditionScript;
  }

  public void setInVarConditionScript(String inVarConditionScript) {
    this.inVarConditionScript = inVarConditionScript;
  }

  public DocServiceManager getDocServiceManager() {
    return docServiceManager;
  }

  public void setDocServiceManager(DocServiceManager docServiceManager) {
    this.docServiceManager = docServiceManager;
  }

	public String getInVarAnnotatedBy() {
		return inVarAnnotatedBy;
	}

	public void setInVarAnnotatedBy(String inVarAnnotatedBy) {
		this.inVarAnnotatedBy = inVarAnnotatedBy;
	}


}
