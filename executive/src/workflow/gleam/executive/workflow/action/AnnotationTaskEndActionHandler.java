package gleam.executive.workflow.action;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.ProcessInstance;

import gleam.executive.workflow.core.JbpmTaskInstance;
import gleam.executive.workflow.model.AnnotationStatusInfo;
import gleam.executive.workflow.sm.JbpmDataflowHandlerProxy;
import gleam.executive.workflow.util.JPDLConstants;
import gleam.executive.workflow.util.WorkflowUtil;

public class AnnotationTaskEndActionHandler extends JbpmDataflowHandlerProxy {

	protected final Log log = LogFactory.getLog(getClass());
	private Element targetProperties;

	/*
	 * <property name="serviceId" type="in" scope="global"
	 * empty="false"></property>
	 */

	@Override
	public void execute(ExecutionContext executionContext) throws Exception {
		log.debug("@@@@@ AnnotationTaskEndActionHandler START");
		Map<String, String> variableMap = fetchAndValidateVariables(
				executionContext, this.getClass().getName(), targetProperties);
		String gasId = (String) variableMap.get(JPDLConstants.SERVICE_ID);
		log.debug("@@@@@@@ gasId " + gasId);
		if (gasId!=null && !"0".equals(gasId)) {
			JbpmTaskInstance taskInstance = (JbpmTaskInstance) executionContext
					.getTaskInstance();
			ProcessInstance processInstance = taskInstance.getProcessInstance();
			ProcessDefinition processDefinition = processInstance
					.getProcessDefinition();
			String nodeName = executionContext.getNode().getName();
			String documentId = taskInstance.getDocumentId();

			String callbackTaskId = "";
			int priority = taskInstance.getPriority();
			if (!WorkflowUtil.isSystemTask(priority)) {
				// HUMAN TASK
				String annotationSetName = taskInstance.getAnnotationSetName();
				String taskPerformer = taskInstance.getActorId();

				callbackTaskId = WorkflowUtil.createCallbackTaskId(
						processDefinition.getName(), processInstance.getId(),
						documentId, nodeName, taskInstance.getId(),
						JPDLConstants.HUMAN_ANNOTATION_SUFFIX);

				/*
				Map<String, String> benchmarkFeatures = new HashMap<String, String>();
				benchmarkFeatures.put(JPDLConstants.ANNOTATION_SET_NAME,
						annotationSetName);
				benchmarkFeatures.put(JPDLConstants.PERFORMER, taskPerformer);

				// do the benchmark call
				GATEUtil.checkPoint(callbackTaskId, this, benchmarkFeatures);
			    */
			} else {
				// SYSTEM TASK
				callbackTaskId = WorkflowUtil.createCallbackTaskId(
						processDefinition.getName(), processInstance.getId(),
						documentId, nodeName, taskInstance.getId(),
						JPDLConstants.GAS_SUFFIX);
				String statusVariableName = WorkflowUtil
						.createAnnotationStatusVariableName(processDefinition
								.getName(), processInstance.getId(), documentId);
				AnnotationStatusInfo annotationStatusInfo = (AnnotationStatusInfo) taskInstance
						.getContextInstance().getVariable(statusVariableName);
				if (annotationStatusInfo != null
						&& annotationStatusInfo.getStatus().startsWith(
								AnnotationStatusInfo.STATUS_FAILED)) {
					// error occured
				/*
				GATEUtil.checkPoint(callbackTaskId, this, Collections
							.singletonMap("failureReason", annotationStatusInfo
									.getStatus()));
				*/
				} else {
					// no error
					//GATEUtil.checkPoint(callbackTaskId, this, Collections.emptyMap());
				}

			}

		} else {
			log.debug("gasId is 0. Do not do anything");
		}

	}

	public Element getTargetProperties() {
		return targetProperties;
	}

	public void setTargetProperties(Element targetProperties) {
		this.targetProperties = targetProperties;
	}
}
