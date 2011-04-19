/*
 *  SetPerformerAssignmentHandler.java
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
import org.jbpm.taskmgmt.exe.Assignable;
import gleam.executive.workflow.sm.JbpmHandlerProxy;

public class SetPerformerAssignmentHandler extends JbpmHandlerProxy {

	  private static final long serialVersionUID = 1L;
	  protected final Log log = LogFactory.getLog(getClass());

	  private String inVarPerformer;

	  public void assign(Assignable assignable, ExecutionContext executionContext) {

		  String performer = (String)executionContext.getContextInstance().getVariable(getInVarPerformer());
		  log.debug("Assign performer " +performer);
		  if(performer!=null && !performer.equals("") && performer.indexOf(",")!=-1){
			  String[] poolesActors = performer.split(",");
			  assignable.setPooledActors(poolesActors);
			  assignable.setActorId("");

		  }else{
			  assignable.setPooledActors(new String[]{});
			  assignable.setActorId(performer);
		  }
	  }

	public String getInVarPerformer() {
		return inVarPerformer;
	}

	public void setInVarPerformer(String inVarPerformer) {
		this.inVarPerformer = inVarPerformer;
	}

}
