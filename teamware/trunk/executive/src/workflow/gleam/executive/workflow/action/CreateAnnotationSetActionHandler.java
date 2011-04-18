package gleam.executive.workflow.action;

import gleam.executive.service.DocServiceManager;
import gleam.executive.workflow.util.AnnotationUtil;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.jbpm.graph.exe.ExecutionContext;
import gleam.executive.workflow.util.JPDLConstants;
import gleam.executive.workflow.core.JbpmTaskInstance;
import gleam.executive.workflow.sm.JbpmDataflowHandlerProxy;

/*
 *
 *  CreateAnnotationSetActionHandler.java
 *
 *  Copyright (c) 1998-2006, The University of Sheffield.
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  @author <a href="mailto:M.Agatonovic@dcs.shef.ac.uk">Milan Agatonovic</a>
 *
 *
 */

/*
 * <property name="mode" type="in" scope="global" empty="true"></property>
 * <property name="anonymousAnnotation" type="in" scope="global" empty="true"></property>
 */
public class CreateAnnotationSetActionHandler extends JbpmDataflowHandlerProxy {

	private static final long serialVersionUID = 1L;

	private Element targetProperties;


	// beans
	private DocServiceManager docServiceManager;

	protected final Log log = LogFactory.getLog(getClass());

	public void execute(ExecutionContext context) throws Exception {
		log.debug("@@@@@@ CreateAnnotationSetActionHandler START");
		// obtain targetProperties

		Map<String, String> variableMap = fetchAndValidateVariables(context,
                this.getClass().getName(),
                targetProperties);

		String mode = (String) variableMap.get(JPDLConstants.MODE);
        String anonymousAnnotation = (String) variableMap.get(JPDLConstants.ANONYMOUS_ANNOTATION);

        JbpmTaskInstance taskInstance = (JbpmTaskInstance)context.getTaskInstance();
        String documentId = taskInstance.getDocumentId();

		//log.debug("@@@@@@@ mode = " + mode + "; documentId = " + "; anonymousAnnotation = "+anonymousAnnotation);

		boolean anonymous = true;
		if (anonymousAnnotation == null || "".equals(anonymousAnnotation)) {
			anonymous = false;
		}

		String performer = context.getTaskInstance().getActorId();
		log.debug("&&&&&&&&&&&&& performer " + performer);

		String annotationSetName = "";
		boolean created = false;

		// 1st check if somebody cancelled and make name available
		List<String> existingAnnotationSetNames = null;
		while (!created) {
			if(!JPDLConstants.TEST_MODE.equals(mode)){
			existingAnnotationSetNames = docServiceManager
					.listAnnotationSetNames(documentId);
			}
			else {
				existingAnnotationSetNames = AnnotationUtil.generateRandomAnnotationSets();
			}
			/*
			   log.debug("Found '" + existingAnnotationSetNames.size()
			    + "' annotation sets for document '" + documentId);
			*/
			if (anonymous) {
				annotationSetName = AnnotationUtil
						.resolveNextAvailableAnnotationSetName(
								existingAnnotationSetNames, null);
			} else {
				annotationSetName = AnnotationUtil
						.resolveNextAvailableAnnotationSetName(
								existingAnnotationSetNames, performer);
				;
			}
			if(!JPDLConstants.TEST_MODE.equals(mode)){
			  // now try to create it in docservice
			  created = getDocServiceManager().createAnnotationSet(documentId,
					annotationSetName);
			}
			else {
				//log.debug("Do not create AS physically, since we are in TEST mode!");
				created = true;
			}

		}


	    taskInstance.setAnnotationSetName(annotationSetName);
	    //log.debug("set annotationSetName in taskInstance "+taskInstance.getAnnotationSetName());

		log.debug("CreateAnnotationSetActionHandler END");
		log.debug("@@@@@@@@@@@@@@@@@@@@@@@@");
	}


	public DocServiceManager getDocServiceManager() {
		return docServiceManager;
	}

	public void setDocServiceManager(DocServiceManager docServiceManager) {
		this.docServiceManager = docServiceManager;
	}


	public Element getTargetProperties() {
		return targetProperties;
	}

	public void setTargetProperties(Element targetProperties) {
		this.targetProperties = targetProperties;
	}

}
