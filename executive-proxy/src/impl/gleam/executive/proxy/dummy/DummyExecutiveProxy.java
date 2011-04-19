/*
 *  DummyExecutiveProxy.java
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
package gleam.executive.proxy.dummy;

import gleam.executive.proxy.AnnotatorTask;
import gleam.executive.proxy.ExecutiveProxy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Dummy implementation of an executive client.  This implementation simply
 * logs the finish/failure messages but doesn't act on them.
 */
public class DummyExecutiveProxy implements ExecutiveProxy {
  private static final Log log = LogFactory.getLog(DummyExecutiveProxy.class);

  public void taskFinished(String taskID) {
    log.info("Task finished: taskID=" + taskID);
  }
  
  public void taskCompleted(String taskID) {
    log.info("Task Completed: taskID=" + taskID);
  }
  
  public void taskSaved(String taskID) {
	  log.info("Task saved: taskID=" + taskID);
  }

  public void taskFailed(String taskID, String reason) {
    log.info("Task failed: taskID=" + taskID + ", reason=" + reason);
  }

  public void taskCanceled(String taskID) {
    log.info("Task canceled: taskID=" + taskID);
  }

  public AnnotatorTask getNextTask(String annotatorID) {
    log.info("Get next task: annotatorID=" + annotatorID);
    return null;
  }
  
  
  public boolean checkForNextTask(String annotatorID){
    log.info("Accept next task: annotatorID=" + annotatorID);
    return false;
  }
}
