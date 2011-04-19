/*
 *  GasWorker.java
 *
 *  Copyright (c) 2006-2011, The University of Sheffield.
 *
 *  This file is part of GATE Teamware (see http://gate.ac.uk/teamware/), 
 *  and is free software, licenced under the GNU Affero General Public License,
 *  Version 3, November 2007 (also included with this distribution as file 
 *  LICENCE-AGPL3.html).
 *
 *  A commercial licence is also available for organisations whose business
 *  models preclude the adoption of open source and is subject to a licence
 *  fee charged by the University of Sheffield. Please contact the GATE team
 *  (see http://gate.ac.uk/g8/contact) if you require a commercial licence.
 *
 *  $Id$
 */
package gleam.gateservice.worker;

import static gleam.gateservice.GasConstants.PARAM_DEFAULTS_FEATURE;
import gate.Annotation;
import gate.AnnotationSet;
import gate.Corpus;
import gate.CorpusController;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.GateConstants;
import gate.ProcessingResource;
import gate.corpora.DocumentStaxUtils;
import gate.creole.Parameter;
import gate.creole.ParameterList;
import gate.creole.ResourceData;
import gate.creole.ResourceInstantiationException;
import gate.util.Benchmark;
import gate.util.Benchmarkable;
import gate.util.GateException;
import gleam.docservice.proxy.DSProxyException;
import gleam.docservice.proxy.DocServiceProxy;
import gleam.docservice.proxy.DocServiceProxyFactory;
import gleam.executive.proxy.ExecutiveProxy;
import gleam.executive.proxy.ExecutiveProxyException;
import gleam.executive.proxy.ExecutiveProxyFactory;
import gleam.gateservice.definition.GateServiceDefinition;
import gleam.gateservice.definition.MissingParameterException;
import gleam.gateservice.definition.ServiceDefinitionUtils;
import gleam.gateservice.message.ErrorMessageBody;
import gleam.gateservice.message.GATEModeMessageBody;
import gleam.gateservice.message.GLEAMModeMessageBody;
import gleam.gateservice.message.InlineAnnotationMessageBody;
import gleam.gateservice.message.InlineAnnotationSuccessMessageBody;
import gleam.gateservice.message.RequestMessageBody;
import gleam.gateservice.message.SuccessMessageBody;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class is a MessageListener that consumes JMS messages from a GaS
 * controller.
 */
public class GasWorker implements MessageListener {

  private static final Log log = LogFactory.getLog(GasWorker.class);

  /**
   * The GATE application to run.
   */
  private CorpusController controller;

  /**
   * The GATE Corpus used to pass a document to the application.
   */
  private Corpus corpus;

  /**
   * The JMS messaging session used to communicate with the GaS
   * endpoint.
   */
  private Session jmsSession;

  /**
   * Proxy factory to obtain proxies to access the executive.
   */
  private ExecutiveProxyFactory executiveProxyFactory;

  /**
   * Proxy factory to obtain proxies to access the doc service.
   */
  private DocServiceProxyFactory docServiceProxyFactory;

  /**
   * Parameters definition specifying how GaS parameters map to
   * parameters of the PRs in the application and features on the
   * document.
   */
  private GateServiceDefinition serviceDefinition;

  /**
   * The mime type used to parse the input document content in GATE
   * mode. For a GaS this should always be left as the default of
   * <code>text/xml</code>, but can be overridden to allow using the
   * GaS Worker infrastructure for other non-GaS services that may take
   * plain text or HTML input rather than XML.
   */
  private String gateModeInputMimeType;

  /**
   * Whether we should use repositioning info and preserve original
   * content when parsing the document content for inline annotation
   * mode. Should typically be left at the default value (true) for
   * closest matching between the original and annotated content, but
   * can be set to false if this doesn't matter to your application, or
   * if the documents don't parse properly with repositioning info
   * turned on.
   */
  private boolean useRepositioning = true;

  private static XMLOutputFactory xmlOutputFactory =
          XMLOutputFactory.newInstance();

  /**
   * Initialise this GasWorker. Called after dependencies have been
   * injected.
   */
  public void init() throws ResourceInstantiationException {
    storeRuntimeDefaults();
    if(corpus == null) {
      corpus = Factory.newCorpus("GaS corpus");
    }
  }

  /**
   * Iterates over the PRs in the application, saving the pristine value
   * for each of their runtime parameters (as specified in the .gapp
   * file) to serve as defaults.
   * 
   * @throws ResourceInstantiationException if the value of a parameter
   *           cannot be extracted from a PR
   */
  @SuppressWarnings("unchecked")
  private void storeRuntimeDefaults() throws ResourceInstantiationException {
    for(Object prObj : controller.getPRs()) {
      ProcessingResource pr = (ProcessingResource)prObj;
      log.debug("Storing defaults for PR " + pr);
      ResourceData rd =
              (ResourceData)Gate.getCreoleRegister().get(
                      pr.getClass().getName());
      log.debug("Resource data: " + rd);
      FeatureMap defaultsForPR = Factory.newFeatureMap();
      ParameterList paramList = rd.getParameterList();
      if(paramList != null) {
        List<List<Parameter>> paramDisjunctions =
                paramList.getRuntimeParameters();
        for(List<Parameter> disj : paramDisjunctions) {
          for(Parameter param : disj) {
            log.debug("Storing default for parameter " + param.getName());
            defaultsForPR.put(param.getName(), pr.getParameterValue(param
                    .getName()));
          }
        }
      }

      pr.getFeatures().put(PARAM_DEFAULTS_FEATURE, defaultsForPR);
    }
  }

  /**
   * MessageListener method to handle messages for this worker.
   * Determines the message type and hands off to either
   * {@link #handleGATEModeMessage(MapMessage)} or
   * {@link #handleGLEAMModeMessage(MapMessage)} as appropriate.
   */
  public void onMessage(Message message) {
    if(log.isTraceEnabled()) {
      log.trace("Entering onMessage: " + message);
    }
    try {
      if(!(message instanceof ObjectMessage)) {
        log.error("Malformed message - not an ObjectMessage: " + message);
        return;
      }
      if(!(((ObjectMessage)message).getObject() instanceof RequestMessageBody)) {
        log.error("Malformed message - body is not a RequestMessageBody");
        return;
      }

      RequestMessageBody body =
              (RequestMessageBody)((ObjectMessage)message).getObject();

      // determine whether this is a GATE or GLEAM mode message

      if(body instanceof GLEAMModeMessageBody) {
        handleGLEAMModeMessage((GLEAMModeMessageBody)body);
      }
      else if(body instanceof GATEModeMessageBody) {
        handleGATEModeMessage((GATEModeMessageBody)body, message
                .getJMSReplyTo(), message.getJMSMessageID());
      }
      else if(body instanceof InlineAnnotationMessageBody) {
        handleInlineAnnotationMessage((InlineAnnotationMessageBody)body,
                message.getJMSReplyTo(), message.getJMSMessageID());
      }
      else {
        log.error("Malformed message - unrecognised body type "
                + body.getClass());
      }
    }
    catch(JMSException jmsx) {
      log.error("JMS exception", jmsx);
    }
    finally {
      log.trace("Leaving onMessage");
    }
  }

  // /////// GATE Mode handling //////////

  /**
   * Handles a GATE mode request, i.e. one where the document to process
   * is contained in the message, and the response goes to the
   * JMSReplyTo address.
   * 
   * @param message
   * @throws JMSException
   */
  private void handleGATEModeMessage(GATEModeMessageBody message,
          Destination replyTo, String correlationID) throws JMSException {
    log.trace("Processing GATE mode request");
    MessageProducer replyProducer = jmsSession.createProducer(replyTo);
    // request-response replies need not be persistent
    replyProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

    try {
      String docXML = message.getDocumentXml();
      if(docXML == null) {
        returnError(replyProducer, "No document XML supplied", correlationID);
        return;
      }

      log.debug("Parsing document from XML");
      Document doc = parseDocFromXML(docXML);
      try {
        // apply parameter values
        try {
          log.debug("Applying parameter values");
          ServiceDefinitionUtils.apply(serviceDefinition, message
                  .getParameterValues(), controller);
          ServiceDefinitionUtils.apply(serviceDefinition, message
                  .getParameterValues(), doc);
        }
        catch(GateException ge) {
          log.error("Exception processing parameters", ge);
          returnError(replyProducer, "Error processing parameters", ge,
                  correlationID);
          return;
        }
        // run the application over the document
        log.debug("Running application");
        runController(doc);

        // create the reply message
        SuccessMessageBody replyBody = new SuccessMessageBody();

        for(String outputASName : serviceDefinition
                .getOutputAnnotationSetNames()) {
          log.debug("Writing return annotation set " + outputASName);
          AnnotationSet outputAS = null;
          if(outputASName == null || outputASName.equals("")) {
            outputAS = doc.getAnnotations();
          }
          else {
            outputAS = doc.getAnnotations(outputASName);
          }

          try {
            // write the annotation set to UTF-8 encoded XML
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            XMLStreamWriter xsw =
                    xmlOutputFactory.createXMLStreamWriter(baos, "UTF-8");
            xsw.writeStartDocument();
            DocumentStaxUtils.writeAnnotationSet(outputAS, null, xsw, "");
            xsw.close();

            // add to the reply message
            replyBody.addAnnotationSet(outputASName, baos.toByteArray());
          }
          catch(XMLStreamException e) {
            log.error("Exception writing annotation sets for response message",
                    e);
            returnError(replyProducer, "Exception writing annotation set "
                    + outputASName + " to XML", e, correlationID);
            return;
          }
        }

        log.debug("Sending reply message");
        ObjectMessage replyMessage = jmsSession.createObjectMessage();
        replyMessage.setObject(replyBody);
        replyMessage.setJMSCorrelationID(correlationID);

        replyProducer.send(replyMessage);
      }
      finally {
        Factory.deleteResource(doc);
      }
    }
    catch(GateException gex) {
      log.error("Exception during GATE mode processing", gex);
      returnError(replyProducer, "Exception occurred during processing", gex,
              correlationID);
    }
    catch(RuntimeException rx) {
      log.error("Runtime exception during GATE mode processing", rx);
      returnError(replyProducer,
              "Runtime exception during GATE mode processing", rx,
              correlationID);
    }
    finally {
      replyProducer.close();
    }
  }

  // /////// Inline annotation handling //////////

  /**
   * Handles an inline annotation request, i.e. one where the document
   * to process is contained in the message, and the response is the
   * same document with inline markup and goes to the JMSReplyTo
   * address.
   * 
   * @param message
   * @throws JMSException
   */
  private void handleInlineAnnotationMessage(
          InlineAnnotationMessageBody message, Destination replyTo,
          String correlationID) throws JMSException {
    log.trace("Processing inline mode request");
    MessageProducer replyProducer = jmsSession.createProducer(replyTo);
    // request-response replies need not be persistent
    replyProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

    try {
      String docContent = message.getDocumentContent();
      if(docContent == null) {
        returnError(replyProducer, "No document content supplied",
                correlationID);
        return;
      }

      log.debug("Parsing document from XML");
      Document doc = parseDocFromContent(docContent);
      try {
        // apply parameter values
        try {
          log.debug("Applying parameter values");
          ServiceDefinitionUtils.apply(serviceDefinition, message
                  .getParameterValues(), controller);
          ServiceDefinitionUtils.apply(serviceDefinition, message
                  .getParameterValues(), doc);
        }
        catch(GateException ge) {
          log.error("Exception processing parameters", ge);
          returnError(replyProducer, "Error processing parameters", ge,
                  correlationID);
          return;
        }
        // run the application over the document
        log.debug("Running application");
        runController(doc);

        // create the reply message
        InlineAnnotationSuccessMessageBody replyBody =
                new InlineAnnotationSuccessMessageBody();
        Set<Annotation> annotationsToReturn = new HashSet<Annotation>();
        for(String outputASName : serviceDefinition
                .getOutputAnnotationSetNames()) {
          log.debug("Including annotation set " + outputASName
                  + " in returned document");
          AnnotationSet outputAS = null;
          if(outputASName == null || outputASName.equals("")) {
            outputAS = doc.getAnnotations();
          }
          else {
            outputAS = doc.getAnnotations(outputASName);
          }

          annotationsToReturn.addAll(outputAS);
        }

        String annotatedDocument = doc.toXml(annotationsToReturn);
        // TODO do we want to allow user to specify include features or
        // not?

        replyBody.setAnnotatedDocument(annotatedDocument);

        log.debug("Sending reply message");
        ObjectMessage replyMessage = jmsSession.createObjectMessage();
        replyMessage.setObject(replyBody);
        replyMessage.setJMSCorrelationID(correlationID);

        replyProducer.send(replyMessage);
      }
      finally {
        Factory.deleteResource(doc);
      }
    }
    catch(GateException gex) {
      log.error("Exception during GATE mode processing", gex);
      returnError(replyProducer, "Exception occurred during processing", gex,
              correlationID);
    }
    catch(RuntimeException rx) {
      log.error("Runtime exception during GATE mode processing", rx);
      returnError(replyProducer,
              "Runtime exception during GATE mode processing", rx,
              correlationID);
    }
    finally {
      replyProducer.close();
    }
  }

  /**
   * Returns an error message to the reply queue in a GATE mode
   * invocation.
   * 
   * @param replyProducer the MessageProducer connected to the reply
   *          queue
   * @param message the error message string.
   * @throws JMSException
   */
  private void returnError(MessageProducer replyProducer, String message,
          String correlationID) throws JMSException {
    returnError(replyProducer, message, null, correlationID);
  }

  /**
   * Returns an error message to the reply queue in a GATE mode
   * invocation.
   * 
   * @param replyProducer the MessageProducer connected to the reply
   *          queue
   * @param message the error message string.
   * @param exception the exception giving rise to this error (may be
   *          null)
   * @throws JMSException
   */
  private void returnError(MessageProducer replyProducer, String message,
          Throwable exception, String correlationID) throws JMSException {
    ObjectMessage errorMessage = jmsSession.createObjectMessage();
    errorMessage.setJMSCorrelationID(correlationID);
    ErrorMessageBody body;
    if(exception == null) {
      body = new ErrorMessageBody(message);
    }
    else {
      body = new ErrorMessageBody(message, exception);
    }

    errorMessage.setObject(body);

    replyProducer.send(errorMessage);
  }

  // //////// GLEAM Mode handling ////////

  private void handleGLEAMModeMessage(GLEAMModeMessageBody message)
          throws JMSException {
    ExecutiveProxy execProxy = getExecutiveProxy(message);
    if(execProxy == null) {
      return;
    }

    String taskID = message.getTaskID();
    if(taskID == null || taskID.equals("")) {
      log.error("No task ID specified for GLEAM mode call");
      return;
    }

    Map<String, String> benchmarkFeatures = new HashMap<String, String>();
    long startTime = 0;

    // Phew! Any future problems can be reported to the executive
    try {
      DocServiceProxy dsProxy = getDocServiceProxy(message, execProxy, taskID);
      if(dsProxy == null) {
        return;
      }

      // get input and output annotation set names
      Set<String> inputASNames = serviceDefinition.getInputAnnotationSetNames();
      Set<String> outputASNames =
              serviceDefinition.getOutputAnnotationSetNames();

      try {
        try {
          corpus.clear();

          for(GLEAMModeMessageBody.Task t : message.getTasks()) {
            String docID = t.getDocID();
            if(docID == null || docID.equals("")) {
              log.error("No docID provided for GLEAM mode call: " + message);
              execProxy.taskFailed(taskID, "No doc ID provided");
              return;
            }

            // fetch the document content
            benchmarkFeatures.put("documentID", docID);
            startTime = Benchmark.startPoint();
            Document doc = dsProxy.getDocumentContentOnly(docID);
            Benchmark.checkPoint(startTime, Benchmark.createBenchmarkId(
                    "getDocumentContentOnly", taskID), this, benchmarkFeatures);
            corpus.add(doc);

            // get input annotation sets
            for(String inputASName : inputASNames) {
              if(!t.getAnnotationSetMappings().containsKey(inputASName)) {
                log.error("No mapping provided for annotation set "
                        + inputASName);
                execProxy
                        .taskFailed(taskID,
                                "No mapping provided for annotation set "
                                        + inputASName);
                return;
              }
              // fetch the set read only if it is not also required for
              // output
              benchmarkFeatures.put("gasASName", inputASName);
              benchmarkFeatures.put("docServiceASName", t
                      .getAnnotationSetMappings().get(inputASName));
              startTime = Benchmark.startPoint();
              dsProxy.getAnnotationSet(doc, t.getAnnotationSetMappings().get(
                      inputASName), inputASName, !(outputASNames
                      .contains(inputASName)));
              Benchmark.checkPoint(startTime, Benchmark.createBenchmarkId(
                      "loadInputAnnotationSet", taskID), this,
                      benchmarkFeatures);
            }
            benchmarkFeatures.remove("gasASName");
            benchmarkFeatures.remove("docServiceASName");

            // lock the output sets that aren't also input sets
            for(String outputASName : outputASNames) {
              if(!inputASNames.contains(outputASName)) {
                if(!t.getAnnotationSetMappings().containsKey(outputASName)) {
                  log.error("No mapping provided for annotation set "
                          + outputASName);
                  execProxy.taskFailed(taskID,
                          "No mapping provided for annotation set "
                                  + outputASName);
                  return;
                }
                benchmarkFeatures.put("docServiceASName", t
                        .getAnnotationSetMappings().get(outputASName));
                startTime = Benchmark.startPoint();
                dsProxy.lockAnnotationSet(doc, t.getAnnotationSetMappings()
                        .get(outputASName));
                Benchmark.checkPoint(startTime, Benchmark.createBenchmarkId(
                        "lockOutputAnnotationSet", taskID), this,
                        benchmarkFeatures);
              }
            }
          }

          benchmarkFeatures.clear();

          // apply parameter values
          try {
            startTime = Benchmark.startPoint();
            ServiceDefinitionUtils.apply(serviceDefinition, message
                    .getParameterValues(), controller);
            Iterator<Document> corpusIterator = corpus.iterator();
            while(corpusIterator.hasNext()) {
              ServiceDefinitionUtils.apply(serviceDefinition, message
                      .getParameterValues(), corpusIterator.next());
            }
            Benchmark.checkPoint(startTime, Benchmark.createBenchmarkId(
                    "applyParameters", taskID), this, benchmarkFeatures);
          }
          catch(GateException ge) {
            log.error("Exception processing parameters", ge);
            execProxy.taskFailed(taskID, "Error processing parameters");
            return;
          }
          catch(MissingParameterException mpe) {
            log.error("Exception processing parameters", mpe);
            execProxy.taskFailed(taskID, mpe.getMessage());
            return;
          }

          // process doc
          try {
            log.debug("Running application");
            controller.setCorpus(corpus);
            String benchmarkID =
                    Benchmark.createBenchmarkId("runApplication", taskID);
            Benchmark.executeWithBenchmarking(controller, benchmarkID, this,
                    benchmarkFeatures);
            log.debug("Application finished");
          }
          catch(Exception ge) {
            log.error("Exception during processing", ge);
            execProxy.taskFailed(taskID, "Error during processing");
            return;
          }

          Iterator<Document> corpusIterator = corpus.iterator();
          for(GLEAMModeMessageBody.Task t : message.getTasks()) {
            benchmarkFeatures.put("documentID", t.getDocID());
            Document doc = corpusIterator.next();
            // return output annotation set(s)
            for(String outputASName : outputASNames) {
              // we know at this point that there are valid mappings for
              // all the output sets, as we would have failed at the
              // reading stage otherwise.
              benchmarkFeatures.put("docServiceASName", t
                      .getAnnotationSetMappings().get(outputASName));
              benchmarkFeatures.put("gasASName", outputASName);
              startTime = Benchmark.startPoint();
              dsProxy.saveAnnotationSet(doc, outputASName, t
                      .getAnnotationSetMappings().get(outputASName));
              Benchmark.checkPoint(startTime, Benchmark.createBenchmarkId(
                      "saveOutputAnnotationSet", taskID), this,
                      benchmarkFeatures);
            }
          }
        }
        finally {
          // release locks and free documents
          // we start from the end and count down as deleteResource-ing
          // a document
          // removes it from the corpus, which would mess up the indices
          // if we
          // started from 0 and tried to count up...
          for(int i = corpus.size() - 1; i >= 0; i--) {
            Document doc = (Document)corpus.get(i);
            dsProxy.release(doc);
            Factory.deleteResource(doc);
          }
          corpus.clear();
        }
      }
      catch(DSProxyException dspe) {
        log.error("Error contacting doc service", dspe);
        execProxy.taskFailed(taskID, dspe.getMessage());
        return;
      }

      // inform executive of success - hooray!
      execProxy.taskFinished(taskID);
    }
    catch(ExecutiveProxyException epe) {
      // there's a problem with the executive, so we just log the error
      log.error("Exception contacting executive", epe);
    }

  }

  /**
   * Extract the executive URL from the given message and return an
   * ExecutiveProxy instance for that executive. Any errors (no URL
   * specified, malformed URL, exception from the proxy factory) are
   * logged and <code>null</code> is returned in this case. We would
   * like to be able to report these errors back to the executive, etc.
   * but...
   * 
   * @param message the message to process
   * @return the executive proxy, or <code>null</code> in case of
   *         error
   * @throws JMSException
   */
  private ExecutiveProxy getExecutiveProxy(GLEAMModeMessageBody message)
          throws JMSException {
    URI executiveURI = message.getExecutiveLocation();
    if(executiveURI == null) {
      log.error("No executive URI provided for GLEAM mode call: " + message);
      return null;
    }

    ExecutiveProxy execProxy = null;
    try {
      execProxy = executiveProxyFactory.getExecutiveProxy(executiveURI);
    }
    catch(ExecutiveProxyException epe) {
      log
              .error(
                      "Exception getting proxy for executive at "
                              + executiveURI, epe);
      return null;
    }

    return execProxy;
  }

  /**
   * Extract the doc service URL from the given message and return a
   * DocServiceProxy instance for that service. Any errors (no URL
   * specified, malformed URL, exception from the proxy factory) are
   * logged and reported to the executive via
   * {@link ExecutiveProxy.taskFailed} and <code>null</code> is
   * returned in this case.
   * 
   * @param message the message
   * @param execProxy the executive proxy to use for failure messages
   * @param taskID the task ID for failure messages
   * @return the <code>DocServiceProxy</code>, or null in case of
   *         error.
   * @throws JMSException
   * @throws ExecutiveProxyException
   */
  private DocServiceProxy getDocServiceProxy(GLEAMModeMessageBody message,
          ExecutiveProxy execProxy, String taskID) throws JMSException,
          ExecutiveProxyException {
    URI docServiceURI = message.getDocServiceLocation();
    if(docServiceURI == null) {
      log.error("No doc service URI for GLEAM mode call: " + message);
      execProxy.taskFailed(taskID, "No doc service URI provided");
      return null;
    }

    DocServiceProxy dsProxy = null;
    try {
      dsProxy = docServiceProxyFactory.getDocServiceProxy(docServiceURI);
    }
    catch(DSProxyException dspe) {
      log.error("Exception getting proxy for doc service at " + docServiceURI);
      execProxy.taskFailed(taskID, "Could not create proxy for doc service at "
              + docServiceURI);
      return null;
    }
    return dsProxy;
  }

  // //////// Shared utility methods ////////

  /**
   * Run our controller over the given document.
   * 
   * @param doc the document to process.
   * @throws GateException if an error occurs while running the
   *           application.
   */
  @SuppressWarnings("unchecked")
  private void runController(Document doc) throws GateException {
    corpus.clear();
    corpus.add(doc);
    try {
      controller.setCorpus(corpus);
      controller.execute();
    }
    finally {
      corpus.clear();
    }
  }

  /**
   * Create a document instance from the given XML string.
   * 
   * @param docXML the XML representation of the document
   * @return the document object
   * @throws ResourceInstantiationException
   */
  @SuppressWarnings("unchecked")
  private Document parseDocFromXML(String docXML)
          throws ResourceInstantiationException {
    // we use createResource rather than newDocument so we can force the
    // mime type to be text/xml
    FeatureMap docParams = Factory.newFeatureMap();
    docParams.put(Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME, docXML);
    docParams.put(Document.DOCUMENT_MIME_TYPE_PARAMETER_NAME,
            gateModeInputMimeType);

    // force format errors to be fatal
    FeatureMap docFeatures = Factory.newFeatureMap();
    docFeatures.put(GateConstants.THROWEX_FORMAT_PROPERTY_NAME, Boolean.TRUE);
    return (Document)Factory.createResource("gate.corpora.DocumentImpl",
            docParams, docFeatures);
  }

  /**
   * Create a document from the given content string, suitable for use
   * with the inline markup service.
   * 
   * @param docContent the content (XML/HTML/text/whatever).
   * @return the document object.
   * @throws ResourceInstantiationException if an error occurs parsing
   *           the document.
   */
  private Document parseDocFromContent(String docContent)
          throws ResourceInstantiationException {
    FeatureMap docParams = Factory.newFeatureMap();
    docParams.put(Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME, docContent);
    docParams.put(Document.DOCUMENT_PRESERVE_CONTENT_PARAMETER_NAME, Boolean
            .valueOf(useRepositioning));
    docParams.put(Document.DOCUMENT_REPOSITIONING_PARAMETER_NAME, Boolean
            .valueOf(useRepositioning));

    // force format errors to be fatal
    FeatureMap docFeatures = Factory.newFeatureMap();
    docFeatures.put(GateConstants.THROWEX_FORMAT_PROPERTY_NAME, Boolean.TRUE);
    return (Document)Factory.createResource("gate.corpora.DocumentImpl",
            docParams, docFeatures);
  }

  /**
   * @return the controller
   */
  public CorpusController getController() {
    return controller;
  }

  /**
   * @param controller the controller to set
   */
  public void setController(CorpusController controller) {
    this.controller = controller;
  }

  /**
   * @return the jmsSession
   */
  public Session getSession() {
    return jmsSession;
  }

  /**
   * @param jmsSession the jmsSession to set
   */
  public void setSession(Session jmsSession) {
    this.jmsSession = jmsSession;
  }

  public DocServiceProxyFactory getDocServiceProxyFactory() {
    return docServiceProxyFactory;
  }

  public void setDocServiceProxyFactory(
          DocServiceProxyFactory docServiceProxyFactory) {
    this.docServiceProxyFactory = docServiceProxyFactory;
  }

  public ExecutiveProxyFactory getExecutiveProxyFactory() {
    return executiveProxyFactory;
  }

  public void setExecutiveProxyFactory(
          ExecutiveProxyFactory executiveProxyFactory) {
    this.executiveProxyFactory = executiveProxyFactory;
  }

  public GateServiceDefinition getServiceDefinition() {
    return serviceDefinition;
  }

  public void setServiceDefinition(GateServiceDefinition serviceDefinition) {
    this.serviceDefinition = serviceDefinition;
  }

  public String getGateModeInputMimeType() {
    return gateModeInputMimeType;
  }

  public void setGateModeInputMimeType(String gateModeInputMimeType) {
    this.gateModeInputMimeType = gateModeInputMimeType;
  }

  public boolean getUseRepositioning() {
    return useRepositioning;
  }

  public void setUseRepositioning(boolean useRepositioning) {
    this.useRepositioning = useRepositioning;
  }

  /**
   * Closes the GATE application (and by extension all the PRs it
   * contains) and the corpus using
   * {@link Factory#deleteResource(gate.Resource) deleteResource}.
   */
  public void shutdown() {
    if(corpus != null) {
      Factory.deleteResource(corpus);
    }
    Factory.deleteResource(controller);
  }
}
