/*
 *  CheckTrainingStatusActionHandler.java
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
