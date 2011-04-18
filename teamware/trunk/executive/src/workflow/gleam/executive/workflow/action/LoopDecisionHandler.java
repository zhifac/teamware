package gleam.executive.workflow.action;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.Token;
import org.springframework.util.StringUtils;

import gleam.executive.workflow.util.AnnotationUtil;
import gleam.executive.workflow.util.JPDLConstants;
import gleam.executive.workflow.util.WorkflowUtil;
import gleam.executive.workflow.model.AnnotationStatusInfo;
import gleam.executive.workflow.sm.JbpmDataflowHandlerProxy;


public class LoopDecisionHandler extends
		JbpmDataflowHandlerProxy {

	private static final long serialVersionUID = 1L;

	protected final Log log = LogFactory.getLog(getClass());

	private Element targetProperties;
	/*
	 * <property name="counter" type="inout" scope="local" empty="true"></property>
	 * <property name="CSVList" type="in" scope="global" empty="true" ref=something></property>
	 * <property name="item" type="out" scope="local" empty="true"></property>
	 */

	public String decide(ExecutionContext context) throws Exception {
		log.debug("@@@@@ LoopDecisionHandler START");
		Map<String, String> variableMap = fetchAndValidateVariables(context,
                this.getClass().getName(),
                targetProperties);

		// obtain targetProperties
		String csvList = (String) variableMap.get(JPDLConstants.CSV_LIST);
		String counterString = (String) variableMap.get(JPDLConstants.COUNTER);

		log.debug("@@@@@@@ counterString = " + counterString + "; csvList = " + csvList);

		String transitionName = "";
		Token token = context.getToken();
		int counter = 0;
		if(counterString!=null){
			counter = Integer.parseInt(counterString);
		}
        Map<String, Object> map = new HashMap<String, Object>();
		String[] array = StringUtils.commaDelimitedListToStringArray(csvList);
		log.debug("@@@@@ counter: "+counter);
		if(counter < array.length){
			String item = array[counter];
			log.debug("@@@@@ item: "+item);
			map.put(JPDLConstants.ITEM, item);
			counter++;
			map.put(JPDLConstants.COUNTER, String.valueOf(counter));
			transitionName = JPDLConstants.TRANSITION_REPEAT;
			//String documentId = (String)context.getContextInstance().getVariable(JPDLConstants.DOCUMENT_ID, context.getToken());
			String documentId = "999"; 
			log.debug("documentId in token "+documentId);
			 long processInstanceId = context.getProcessInstance().getId();
			 String processDefinitionName = context.getProcessInstance().getProcessDefinition().getName();
				
			String statusVariableName = WorkflowUtil.createAnnotationStatusVariableName(processDefinitionName, processInstanceId, documentId);
			AnnotationStatusInfo annotationStatusInfo = (AnnotationStatusInfo)context.getVariable(statusVariableName);
			annotationStatusInfo = AnnotationUtil.resetDocument(annotationStatusInfo);
		}
		else {
			map.put(JPDLConstants.COUNTER, null);
			log.debug("@@@@@ LoopDecisionHandler END");
			transitionName = JPDLConstants.TRANSITION_FINISH;
		}
		
		submitTokenVariables(context,this.getClass().getName(), targetProperties, map, token);
	    return transitionName;
		
	}


	public Element getTargetProperties() {
		return targetProperties;
	}

	public void setTargetProperties(Element targetProperties) {
		this.targetProperties = targetProperties;
	}






}


