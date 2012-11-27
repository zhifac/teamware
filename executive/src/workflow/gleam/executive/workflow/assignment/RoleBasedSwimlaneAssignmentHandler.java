/*
 *  RoleBasedSwimlaneAssignmentHandler.java
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

import java.util.Iterator;
import java.util.List;

import gleam.executive.model.User;
import gleam.executive.service.UserManager;
import gleam.executive.workflow.sm.JbpmDataflowHandlerProxy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.taskmgmt.exe.Assignable;
import org.jbpm.taskmgmt.exe.SwimlaneInstance;
import org.springframework.util.StringUtils;

public class RoleBasedSwimlaneAssignmentHandler extends JbpmDataflowHandlerProxy {

	  private static final long serialVersionUID = 1L;
	  protected final Log log = LogFactory.getLog(getClass());

	  private UserManager userManager;

	  public void assign(Assignable assignable, ExecutionContext executionContext) {
          log.debug("@@@@@ RoleBasedSwimlaneAssignmentHandler START");
          if(assignable instanceof SwimlaneInstance){
        	  SwimlaneInstance swimlane = (SwimlaneInstance)assignable;
        	  String roleName = swimlane.getName();
        	  String[] pooledActors = null;
        	 // set corresponding variables
        	  ProcessInstance processInstance = executionContext.getProcessInstance();
        	  String pooledActorCSVList = (String)processInstance.getContextInstance().getVariable(roleName + "CSVList");
        	  if(pooledActorCSVList==null){
        		  List<User> users = userManager.getUsersWithRole(roleName);
            	  pooledActors = new String[users.size()];
            	  int i=0;
            	  Iterator<User> it = users.iterator();
            	  while(it.hasNext()){
            		  pooledActors[i] = it.next().getUsername();
            		  i++;
            	  }
            	  pooledActorCSVList = StringUtils.arrayToCommaDelimitedString(pooledActors);

        		processInstance.getContextInstance().createVariable(roleName + "CSVList", pooledActorCSVList);
        	    log.debug("pooled actor list: '"+roleName + "' is empty. Create new one with value: '"+pooledActorCSVList + "'");
        	  }
        	  else {
        		  pooledActors = StringUtils.commaDelimitedListToStringArray(pooledActorCSVList);
        		  log.debug("pooled actor list: '"+roleName + "' already exists and has value: '"+pooledActorCSVList + "'");
        	  }
        	  assignable.setPooledActors(pooledActors);
          }
          else {
        	  log.debug("Assignable is not swimlane - dont do anything");
          }
          log.debug("@@@@@ RoleBasedSwimlaneAssignmentHandler END");
	  }

	public UserManager getUserManager() {
		return userManager;
	}

	public void setUserManager(UserManager userManager) {
		this.userManager = userManager;
	}


}