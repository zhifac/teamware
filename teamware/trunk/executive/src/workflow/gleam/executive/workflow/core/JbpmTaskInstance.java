/*
 *  JbpmTaskInstance.java
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
package gleam.executive.workflow.core;


import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.graph.def.Event;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.jbpm.taskmgmt.log.TaskAssignLog;

public class JbpmTaskInstance extends TaskInstance{

	private static final long serialVersionUID = -80542689260843581L;

	private static final Log log = LogFactory.getLog(JbpmTaskInstance.class);

	private String documentId;
	private String annotationSetName;
	private Date lastOpened;
	private Date lastSaved;
	private long timeWorkedOn;

	public String getDocumentId() {
		return documentId;
	}

	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

	public String getAnnotationSetName() {
		return annotationSetName;
	}

	public void setAnnotationSetName(String annotationSetName) {
		this.annotationSetName = annotationSetName;
	}
	
	public Date getLastOpened() {
		return lastOpened;
	}

	public void setLastOpened(Date lastOpened) {
		this.lastOpened = lastOpened;
	}

	public Date getLastSaved() {
		return lastSaved;
	}

	public void setLastSaved(Date lastSaved) {
		this.lastSaved = lastSaved;
	}

	public long getTimeWorkedOn() {
		return timeWorkedOn;
	}

	public void setTimeWorkedOn(long timeWorkedOn) {
		this.timeWorkedOn = timeWorkedOn;
	}
	
	public void addTimeWorkedOn(long timeToAdd) {
		this.timeWorkedOn += timeToAdd;
	}

	public void setActorId(String actorId, boolean overwriteSwimlane){
	    // do the actual assignment
	    log.debug("Setting actor: "+ actorId + " for taskInstance "+ getId());
		this.previousActorId = this.actorId;
	    this.actorId = actorId;
	    if ( (swimlaneInstance!=null)
	         && (overwriteSwimlane) ) {
	      log.debug("assigning task '"+name+"' to '"+actorId+"'");
	      swimlaneInstance.setActorId(actorId);
	    }

	    // fire the event
	    if ( (task!=null)
	         && (token!=null)
	       ) {
	      ExecutionContext executionContext = new ExecutionContext(token);
	      executionContext.setTask(task);
	      executionContext.setTaskInstance(this);

	      // WARNING: The events create and assign are fired in the right order, but
	      // the logs are still not ordered properly.
	      // See also: TaskMgmtInstance.createTaskInstance
	      if(actorId!=null){
	       task.fireEvent(Event.EVENTTYPE_TASK_ASSIGN, executionContext);
	      }
	      else {

	      }
	    }

	    // add the log
	    if (token!=null) {
	      // log this assignment
	      token.addLog(new TaskAssignLog(this, previousActorId, actorId));
	    }
	  }

}
