package gleam.executive.workflow.action;

import gleam.executive.model.AnnotationService;
import gleam.executive.service.AnnotationServiceManager;
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
 *  ServiceLookupActionHandler.java
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
 * <property name="serviceId" type="in" scope="local" empty="true" ref="item"></property>
 */

public class ServiceLookupActionHandler extends JbpmDataflowHandlerProxy {

	private static final long serialVersionUID = 1L;

	private Element targetProperties;

	// beans
	private AnnotationServiceManager annotationServiceManager;

	protected final Log log = LogFactory.getLog(getClass());

	public void execute(ExecutionContext context) throws Exception {
		log.debug("@@@@@@ ServiceLookupActionHandler START");
		// obtain targetProperties

		Map<String, String> variableMap = fetchAndValidateVariables(context,
				this.getClass().getName(), targetProperties);

		String serviceId = (String) variableMap.get(JPDLConstants.SERVICE_ID);
		//log.debug("@@@@@@@ serviceId " + serviceId);
		JbpmTaskInstance taskInstance = (JbpmTaskInstance) context
				.getTaskInstance();
		String actor = context.getTaskInstance().getActorId();
		//log.debug("&&&&&&&&&&&&& actor " + actor);

		if (!"0".equals(serviceId)) {

			AnnotationService annotationService = annotationServiceManager
					.getAnnotationService(new Long(serviceId));

			String serviceName = annotationService.getName();

			taskInstance.setActorId(serviceName);
			//log.debug("set setActorId in taskInstance "
			//		+ taskInstance.getActorId());

			// now start the task:
			taskInstance.start();
			//log.debug("started taskInstance " + taskInstance.getId());
		} else {
			// no GAS is selected -> complete the task
			taskInstance.end();
		}
		log.debug("ServiceLookupActionHandler END");
		log.debug("@@@@@@@@@@@@@@@@@@@@@@@@");
	}

	public AnnotationServiceManager getAnnotationServiceManager() {
		return annotationServiceManager;
	}

	public void setAnnotationServiceManager(
			AnnotationServiceManager annotationServiceManager) {
		this.annotationServiceManager = annotationServiceManager;
	}

	public Element getTargetProperties() {
		return targetProperties;
	}

	public void setTargetProperties(Element targetProperties) {
		this.targetProperties = targetProperties;
	}

}
