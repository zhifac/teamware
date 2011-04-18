package gleam.executive.workflow.action.common;


import gleam.executive.workflow.util.WorkflowUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.Token;
import gleam.executive.workflow.util.JPDLConstants;
import gleam.executive.workflow.sm.JbpmHandlerProxy;

public class ProduceTrainingTasksActionHandler extends JbpmHandlerProxy {

	/*
	       <inVarDocumentId>documentId</inVarDocumentId>
		      <inVarAnnotationSetName>annotationSetName</inVarAnnotationSetName>
              <inOutVarInvokeTraining>invokeTraining</inOutVarInvokeTraining>
              <inOutVarTrainingTaskQueue>trainingTaskQueue</inOutVarTrainingTaskQueue>
	 *
	 */

	// target variables picked from JPDL
	String inVarDocumentId;
	String inVarAnnotationSetName;
	String inOutVarInvokeTraining;
	String inOutVarTrainingTaskQueue;


	protected final Log log = LogFactory.getLog(getClass());

	public void execute(ExecutionContext context) throws Exception {
		log.debug("@@@@@@@@@@@@@@@@@@@@@@@@");
		log.debug("ProduceTrainingTasksActionHandler START");
		// obtain targetProperties
		String invokeTraining = (String) context.getContextInstance()
				.getVariable(getInOutVarInvokeTraining());
		log.debug("&&&&&&&&&&&&& invokeTraining " + invokeTraining);

		String documentId = (String) context.getContextInstance().getVariable(getInVarDocumentId(),context.getToken());
		log.debug("&&&&&&&&&&&&& documentId " + documentId);

		String annotationSetName = (String) context.getContextInstance().getVariable(getInVarAnnotationSetName(), context.getToken());
		log.debug("&&&&&&&&&&&&& annotationSetName " + annotationSetName);

		// prepare training tasks queue
		String trainingTaskQueue = (String) context.getVariable(getInOutVarTrainingTaskQueue());

		trainingTaskQueue = WorkflowUtil.produceTrainingTask(trainingTaskQueue,
				                                             documentId,
				                                             annotationSetName);

		// set trainingTaskQueue as global variable;
		context.setVariable(JPDLConstants.TRAINING_TASK_QUEUE, trainingTaskQueue);

	    if (invokeTraining !=null) {
				// create new token
	    	    context.getContextInstance().setVariable(getInOutVarInvokeTraining(), null);
				final Token currentToken = context.getToken();
				log.debug("@@@@@@@ now, we are in the token: " + currentToken.getName());
				final Node node = context.getNode();
				log.debug("@@@@@@@ node " + node.getName());
				final Token newToken = new Token(currentToken, "Training");
				log.debug("@@@@@@@ created new token ID " + newToken.getId()
						+ "   NAME: " + newToken.getName());
				newToken.setTerminationImplicit(true);
				context.getJbpmContext().getSession().save(newToken);
				final ExecutionContext newExecutionContext = new ExecutionContext(
						newToken);

				newExecutionContext.getJbpmContext().getSession()
						.save(newToken);

				node.leave(newExecutionContext,
						JPDLConstants.TRANSITION_TRAINING);

				log.debug("NODE LEFT BY NEW TOKEN " + newToken.getName());
			}


			log.debug("Leave node with default transition ");
			context.leaveNode();



		log.debug("ProduceTrainingTasksActionHandler END");
		log.debug("@@@@@@@@@@@@@@@@@@@@@@@@");
	}

	public String getInOutVarInvokeTraining() {
		return inOutVarInvokeTraining;
	}

	public void setInOutVarInvokeTraining(String inOutVarInvokeTraining) {
		this.inOutVarInvokeTraining = inOutVarInvokeTraining;
	}

	public String getInOutVarTrainingTaskQueue() {
		return inOutVarTrainingTaskQueue;
	}

	public void setInOutVarTrainingTaskQueue(String inOutVarTrainingTaskQueue) {
		this.inOutVarTrainingTaskQueue = inOutVarTrainingTaskQueue;
	}

	public String getInVarAnnotationSetName() {
		return inVarAnnotationSetName;
	}

	public void setInVarAnnotationSetName(String inVarAnnotationSetName) {
		this.inVarAnnotationSetName = inVarAnnotationSetName;
	}

	public String getInVarDocumentId() {
		return inVarDocumentId;
	}

	public void setInVarDocumentId(String inVarDocumentId) {
		this.inVarDocumentId = inVarDocumentId;
	}


}
