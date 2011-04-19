/*
 *  LogTaskPerformerActionHandler.java
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
import org.jbpm.taskmgmt.exe.TaskInstance;
import gleam.executive.workflow.sm.JbpmHandlerProxy;

/*
 * <outVarTaskPerformer>taskPerformer</outVarTaskPerformer>
 */

public class LogTaskPerformerActionHandler extends JbpmHandlerProxy {

	protected static final Log log = LogFactory
			.getLog(LogTaskPerformerActionHandler.class);

	String outVarTaskPerformer;

	public void execute(ExecutionContext context) throws Exception {
		log.debug("@@@@@@@@@@@@@@@@@@@@@@@@");
		log.debug("LogTaskPerformerActionHandler START");

		TaskInstance taskInstance = context.getTaskInstance();
		log.debug("obtained  taskInstance " + taskInstance.getName());
		String performer = taskInstance.getActorId();
		log.debug("obtained  performer " + performer);
		context.setVariable(getOutVarTaskPerformer(), performer);

		log.debug("LogTaskPerformerActionHandler END");
		log.debug("@@@@@@@@@@@@@@@@@@@@@@@@");
	}

	public String getOutVarTaskPerformer() {
		return outVarTaskPerformer;
	}

	public void setOutVarTaskPerformer(String outVarTaskPerformer) {
		this.outVarTaskPerformer = outVarTaskPerformer;
	}

}
