/*
 *  CreateAnnotationTasksActionHandler.java
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
package gleam.executive.workflow.action;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.Token;
import org.jbpm.graph.node.TaskNode;
import org.jbpm.taskmgmt.def.Task;
import org.jbpm.taskmgmt.exe.TaskMgmtInstance;

import gleam.executive.workflow.core.JbpmTaskInstance;
import gleam.executive.workflow.model.AnnotationStatusInfo;
import gleam.executive.workflow.sm.JbpmDataflowHandlerProxy;
import gleam.executive.workflow.util.JPDLConstants;
import gleam.executive.workflow.util.WorkflowUtil;


public class CreateAnnotationTasksActionHandler extends JbpmDataflowHandlerProxy {

	private static final long serialVersionUID = 1L;

	protected final Log log = LogFactory.getLog(getClass());

	private Element targetProperties;

/*
 * <property name="annotatorsPerDocument" type="in" scope="global" empty="false"></property>
 */
	public void execute(ExecutionContext context) throws Exception {

		log.debug("@@@@ CreateAnnotationTasksActionHandler START");

		Map<String, String> variableMap = fetchAndValidateVariables(context,
                this.getClass().getName(),
                targetProperties);

		Token token = context.getToken();
        Map<String, Object> map = new HashMap<String, Object>();
        
		String annotatorsPerDocument = (String) variableMap.get(JPDLConstants.ANNOTATORS_PER_DOCUMENT);
        log.debug("@@@@@@@ annotatorsPerDocument " + annotatorsPerDocument);
        if(annotatorsPerDocument==null) annotatorsPerDocument = "1";
        int annotatorsPerDocumentInt = Integer.parseInt(annotatorsPerDocument);
		
		TaskMgmtInstance tmi = context.getTaskMgmtInstance();
		TaskNode taskNode = (TaskNode) context.getNode();
		Set<Task> tasks = taskNode.getTasks();
		Task task= tasks.iterator().next();

		long processInstanceId = context.getProcessInstance().getId();
		//log.debug("current ProcessInstance ID " + processInstanceId);

		String processDefinitionName = context.getProcessInstance().getProcessDefinition().getName();
		//log.debug("current ProcessDefinition Name " + processDefinitionName);
		
		
		//log.debug("@@@@@@@ create task instances for " + task.getName());
		for(int i=0; i<annotatorsPerDocumentInt; i++){
		   JbpmTaskInstance taskInstance = (JbpmTaskInstance)tmi.createTaskInstance(task, context.getToken());
		   log.debug("@@@@@@@ created task instance: " + taskInstance.getId());
		   taskInstance.setDescription("Annotation task");
		   //taskInstance.setPriority(JPDLConstants.PRIORITY_ANNOTATION_TASK);
		   if(taskInstance.getPooledActors()!=null){
		       log.debug("pool size " + taskInstance.getPooledActors().size());
		   }
		   else {
			   log.debug("pool IS NULL ");
		   }
		   // set document property from variable in token scope
		   String documentId = (String)context.getContextInstance().getVariable(JPDLConstants.DOCUMENT_ID, context.getToken());
		   log.debug("documentId in token "+documentId);
		   if(documentId!=null){
			   taskInstance.setDocumentId(documentId);
			   taskInstance.setName(documentId);
			   log.debug("set document in taskInstance "+taskInstance.getDocumentId());
		   }
		   
		   String statusVariableName = WorkflowUtil.createAnnotationStatusVariableName(processDefinitionName, processInstanceId, documentId);
		   AnnotationStatusInfo annotationStatusInfo = (AnnotationStatusInfo)context.getVariable(statusVariableName);
           annotationStatusInfo.setNumberOfIterations(annotatorsPerDocumentInt);
           
       	   map.put(statusVariableName, annotationStatusInfo);
		   submitTokenVariables(context,this.getClass().getName(), targetProperties, map, token);
	
		}

		log.debug("@@@@ CreateAnnotationTasksActionHandler END");
    }

	public Element getTargetProperties() {
		return targetProperties;
	}
	public void setTargetProperties(Element targetProperties) {
		this.targetProperties = targetProperties;
	}
}

