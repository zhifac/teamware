/*
 *  SetCurrentUserAsPerformerAssignmentHandler.java
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
package gleam.executive.workflow.assignment;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.taskmgmt.def.AssignmentHandler;
import org.jbpm.taskmgmt.exe.Assignable;
import gleam.executive.workflow.sm.JbpmHandlerProxy;

public class SetCurrentUserAsPerformerAssignmentHandler extends JbpmHandlerProxy {

	  private static final long serialVersionUID = 1L;
	  protected final Log log = LogFactory.getLog(getClass());


	  public void assign(Assignable assignable, ExecutionContext executionContext) {
          log.debug("@@@@@ ASSIGNMENT HANDLER START");
		  String performer = (String)executionContext.getContextInstance().getVariable("performer");
          log.debug("performer: "+performer);
          if(performer!=null && !performer.equals("")){
			  assignable.setActorId(performer);
			  //assignable.setPooledActors(new String[]{});
		  }
	  }
}
