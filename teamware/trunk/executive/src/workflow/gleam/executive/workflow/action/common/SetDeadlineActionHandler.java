/*
 *  SetDeadlineActionHandler.java
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

import java.util.Date;

import org.jbpm.context.exe.ContextInstance;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.taskmgmt.exe.TaskInstance;
import gleam.executive.workflow.sm.JbpmHandlerProxy;

public class SetDeadlineActionHandler extends JbpmHandlerProxy {
	  private static final long serialVersionUID = 1L;

	  public void execute(ExecutionContext executionContext) throws Exception {
		  ContextInstance ci = executionContext.getContextInstance();
		  Date deadline = (Date)ci.getVariable("deadline");
		  TaskInstance currentTaskInstance = executionContext.getTaskInstance();
		  if(deadline!= null && currentTaskInstance != null)
			  currentTaskInstance.setDueDate(deadline);
	  }
}
