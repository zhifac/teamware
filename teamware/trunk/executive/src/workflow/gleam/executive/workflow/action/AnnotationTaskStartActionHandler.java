package gleam.executive.workflow.action;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.Token;

import gleam.executive.util.GATEUtil;
import gleam.executive.workflow.core.JbpmTaskInstance;
import gleam.executive.workflow.model.AnnotationStatusInfo;
import gleam.executive.workflow.sm.JbpmDataflowHandlerProxy;
import gleam.executive.workflow.util.AnnotationUtil;
import gleam.executive.workflow.util.JPDLConstants;
import gleam.executive.workflow.util.WorkflowUtil;

/*
 * <property name="callbackTaskId" type="out" scope="local" empty="true"></property>
 */


public class AnnotationTaskStartActionHandler extends JbpmDataflowHandlerProxy {

  protected final Log log = LogFactory.getLog(getClass());

  private Element targetProperties;



@Override
  public void execute(ExecutionContext executionContext) throws Exception {
	 log.debug("@@@@@ AnnotationTaskStartActionHandler START");
		Map<String, String> variableMap = fetchAndValidateVariables(executionContext,
             this.getClass().getName(),
             targetProperties);
	
	
	String callbackTaskId = null;
	long processInstanceId = executionContext.getProcessInstance().getId();
	//log.debug("current ProcessInstance ID " + processInstanceId);

	String processDefinitionName = executionContext.getProcessInstance().getProcessDefinition().getName();
	//log.debug("current ProcessDefinition Name " + processDefinitionName);

	String nodeName = executionContext.getNode().getName();
	//log.debug("current Node Name " + nodeName);

	long tokenId = executionContext.getToken().getId();
	//log.debug("current tokenId " + tokenId);
	String tokenName = executionContext.getToken().getName();
	//log.debug("current tokenName " + tokenName);

    Token token = executionContext.getToken();
    Map<String, Object> map = new HashMap<String, Object>();
    
	JbpmTaskInstance taskInstance = (JbpmTaskInstance)executionContext.getTaskInstance();
	if(taskInstance!=null){
	
	
	log.debug("current taskInstance " + taskInstance.getId());
	String documentId = taskInstance.getDocumentId();
    log.debug("documentId " + documentId);
	
    
   
	 // lets try to identify how GAS is invoked on system or human task
    int priority = taskInstance.getPriority();
    //log.debug("priority: "+ priority);
    //log.debug("actorId: "+ taskInstance.getActorId());
   
    if(WorkflowUtil.isAnnotationTask(priority)){
		callbackTaskId = WorkflowUtil.createCallbackTaskId(processDefinitionName, processInstanceId, documentId, nodeName, taskInstance.getId(), JPDLConstants.HUMAN_ANNOTATION_SUFFIX);	
	}

	else {
		callbackTaskId = WorkflowUtil.createCallbackTaskId(processDefinitionName, processInstanceId, documentId, nodeName, taskInstance.getId(), JPDLConstants.GAS_SUFFIX);
		String statusVariableName = WorkflowUtil.createAnnotationStatusVariableName(processDefinitionName, processInstanceId, documentId);
		AnnotationStatusInfo annotationStatusInfo = (AnnotationStatusInfo)executionContext.getVariable(statusVariableName);
		annotationStatusInfo = AnnotationUtil.markDocumentAsTaken(taskInstance.getActorId(), annotationStatusInfo);
		map.put(statusVariableName, annotationStatusInfo);
	}
	}
	else {
	    // note that in case of review, tokenId is sent inside callbackId, and not taskInstanceId
		// also it is not related to any particular document, therefore pass "" as documentId
	    callbackTaskId = WorkflowUtil.createCallbackTaskId(processDefinitionName, processInstanceId, "", nodeName, tokenId, JPDLConstants.REVIEW_SUFFIX);	
	}
    
    map.put(JPDLConstants.CALLBACK_TASK_ID, callbackTaskId);
	 

	submitTokenVariables(executionContext,this.getClass().getName(), targetProperties, map, token);
		
  }

  public Element getTargetProperties() {
		return targetProperties;
	}

	public void setTargetProperties(Element targetProperties) {
		this.targetProperties = targetProperties;
	}
}
