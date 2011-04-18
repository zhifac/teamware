package gleam.executive.workflow.action.common;

import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.node.TaskNode;
import org.jbpm.taskmgmt.def.Task;
import org.jbpm.taskmgmt.exe.TaskMgmtInstance;
import org.springframework.util.StringUtils;

import gleam.executive.workflow.util.JPDLConstants;
import gleam.executive.workflow.util.WorkflowUtil;
import gleam.executive.workflow.core.JbpmTaskInstance;
import gleam.executive.workflow.model.AnnotationStatusInfo;
import gleam.executive.workflow.sm.JbpmDataflowHandlerProxy;

public class CreateGasTasksActionHandler extends
		JbpmDataflowHandlerProxy {

	private static final long serialVersionUID = 1L;

	protected final Log log = LogFactory.getLog(getClass());

	private Element targetProperties;


	/*
	 * <property name="mode" type="in" scope="global" empty="true"></property>
	 * <property name="documentCSVList" type="in" scope="global" empty="false"></property>
	 */

	public void execute(ExecutionContext context) throws Exception {
		log.debug("@@@@@ CreateGasTasksActionHandler START");

		Map<String, String> variableMap = fetchAndValidateVariables(context,
				this.getClass().getName(), targetProperties);

		// obtain targetProperties
		String documentCSVList = (String) variableMap.get(JPDLConstants.DOCUMENT_CSV_LIST);

		log.debug("@@@@@@@ documentCSVList = " + documentCSVList);

		long processInstanceId = context.getProcessInstance().getId();
		log.debug("current ProcessInstance ID " + processInstanceId);

		String processDefinitionName = context.getProcessInstance().getProcessDefinition().getName();
		log.debug("current ProcessDefinition Name " + processDefinitionName);
		
		TaskMgmtInstance tmi = context.getTaskMgmtInstance();
		TaskNode taskNode = (TaskNode) context.getNode();
		Set<Task> tasks = taskNode.getTasks();
		Task task= tasks.iterator().next();
		log.debug("TASK: "+task.getName());
		String[] documentArray = StringUtils.commaDelimitedListToStringArray(documentCSVList);
		
		int numberOfDocuments = documentArray.length;
		log.debug("numberOfDocuments: " + numberOfDocuments);


				for (int i = 0; i < numberOfDocuments; i++) {
					String documentId = documentArray[i];
					// create tasks
					// set documentId as variable since factory will look for it there
					context.getContextInstance().setVariable(JPDLConstants.DOCUMENT_ID, documentId);
					JbpmTaskInstance taskInstance = (JbpmTaskInstance)tmi.createTaskInstance(task, context.getToken());
					log.debug("@@@@@@@ created task instance: " + taskInstance.getId());
					taskInstance.setDescription("GAS task");
					taskInstance.setPriority(JPDLConstants.PRIORITY_SYSTEM_TASK);
					log.debug("taskInstance.getDocumentId(): "+taskInstance.getDocumentId());
					
					if(taskInstance.getDocumentId()==null){
						 taskInstance.setDocumentId(documentId);
						 log.debug("set document in taskInstance "+taskInstance.getDocumentId());
					}
					   
					
				
				}


		log.debug("@@@@ CreateGasTasksActionHandler END");
	}

	public Element getTargetProperties() {
		return targetProperties;
	}

	public void setTargetProperties(Element targetProperties) {
		this.targetProperties = targetProperties;
	}

	

}
