/*
 * This file is part of GLEAM, licenced under the GNU Lesser General Public
 * Licence.
 *
 * (c) 2006 The University of Sheffield
 *
 * $Id$
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
