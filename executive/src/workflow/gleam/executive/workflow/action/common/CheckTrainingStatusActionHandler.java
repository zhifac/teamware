package gleam.executive.workflow.action.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.graph.exe.ExecutionContext;
import gleam.executive.workflow.util.JPDLConstants;
import gleam.executive.workflow.sm.JbpmHandlerProxy;

public class CheckTrainingStatusActionHandler extends JbpmHandlerProxy {

	/*
	   <inVarAnnotatorsPerDocument>annotatorsPerDocument</inVarAnnotatorsPerDocument>
	   <inVarNumberOfDocuments>numberOfDocuments</inVarNumberOfDocuments>
	   <inVarNumberOfTrainingTasks>numberOfTrainingTasks</inVarNumberOfTrainingTasks>
	 */

	// target variables picked from JPDL
	String inVarAnnotatorsPerDocument;

	String inVarNumberOfDocuments;

	String inVarNumberOfTrainingTasks;


	protected final Log log = LogFactory.getLog(getClass());

	public void execute(ExecutionContext context) throws Exception {
		log.debug("@@@@@@@@@@@@@@@@@@@@@@@@");
		log.debug("CheckTrainingStatusActionHandler START");
		// obtain targetProperties
		String annotatorsPerDocument = (String) context.getContextInstance()
				.getVariable(getInVarAnnotatorsPerDocument());
		log.debug("&&&&&&&&&&&&& annotatorsPerDocument " + annotatorsPerDocument);

		Integer annotatorsPerDocumentInt = Integer.parseInt(annotatorsPerDocument);

		Integer numberOfDocuments = (Integer) context.getContextInstance()
		.getVariable(getInVarNumberOfDocuments());
		log.debug("&&&&&&&&&&&&& numberOfDocuments " + numberOfDocuments);

		Integer numberOfTrainingTasks = (Integer) context.getContextInstance()
		.getVariable(getInVarNumberOfTrainingTasks());
		log.debug("&&&&&&&&&&&&& numberOfTrainingTasks " + numberOfTrainingTasks);
        if(numberOfTrainingTasks == null) numberOfTrainingTasks =0;

		if (numberOfTrainingTasks < annotatorsPerDocumentInt * numberOfDocuments) {

			// continue with training
			log.debug("Leave node with transition "
					+ JPDLConstants.TRANSITION_NO);
			context.leaveNode(JPDLConstants.TRANSITION_NO);

	     }
		else {
			log.debug("Leave node with transition "
					+ JPDLConstants.TRANSITION_YES);
			context.leaveNode(JPDLConstants.TRANSITION_YES);

		}

		log.debug("CheckTrainingStatusActionHandler END");
		log.debug("@@@@@@@@@@@@@@@@@@@@@@@@");
	}

	public String getInVarAnnotatorsPerDocument() {
		return inVarAnnotatorsPerDocument;
	}

	public void setInVarAnnotatorsPerDocument(String inVarAnnotatorsPerDocument) {
		this.inVarAnnotatorsPerDocument = inVarAnnotatorsPerDocument;
	}

	public String getInVarNumberOfDocuments() {
		return inVarNumberOfDocuments;
	}

	public void setInVarNumberOfDocuments(String inVarNumberOfDocuments) {
		this.inVarNumberOfDocuments = inVarNumberOfDocuments;
	}

	public String getInVarNumberOfTrainingTasks() {
		return inVarNumberOfTrainingTasks;
	}

	public void setInVarNumberOfTrainingTasks(String inVarNumberOfTrainingTasks) {
		this.inVarNumberOfTrainingTasks = inVarNumberOfTrainingTasks;
	}


}
