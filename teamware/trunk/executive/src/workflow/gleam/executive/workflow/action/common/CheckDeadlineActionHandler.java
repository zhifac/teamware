/*
 *  CheckDeadlineActionHandler.java
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
import gleam.executive.workflow.sm.JbpmHandlerProxy;

public class CheckDeadlineActionHandler extends JbpmHandlerProxy {

	private static final long serialVersionUID = 1L;
	protected final Log log = LogFactory.getLog(getClass());

	public void execute(ExecutionContext executionContext) throws Exception {

		log.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		log.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		log.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		log.debug(">>>>> TASK STARTED >>>>>>>>>>>>>>>>>>>>>");
		log.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		log.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		log.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		/*
		TaskInstance ti = executionContext.getTaskInstance();
		Date deadline = ti.getDueDate();

		if(deadline!=null){
			// check if it is not date before current date
			if(deadline.getTime()<System.currentTimeMillis()){
				// cancel proces instance and all its tasks
				//ti.end("End");
				System.out.println(">>>>> FIRE ACTION");
				//JbpmWorkflowUtil.sendEmail(process, ti);

			}
		}
		*/
	}
}
