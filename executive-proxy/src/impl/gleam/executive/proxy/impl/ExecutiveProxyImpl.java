/*
 *  ExecutiveProxyImpl.java
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
package gleam.executive.proxy.impl;

import gleam.executive.proxy.AnnotatorTask;
import gleam.executive.proxy.ExecutiveProxy;
import gleam.executive.proxy.ExecutiveProxyException;
import gleam.executive.service.callback.AnnotationTask;
import gleam.executive.service.callback.ExecutiveCallbackService;

import java.net.URI;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Executive proxy implementation that talks to a real callback service
 * endpoint on a running executive application.
 */
public class ExecutiveProxyImpl implements ExecutiveProxy {

  private static final Log log = LogFactory.getLog(ExecutiveProxyImpl.class);

  /**
   * The Axis stub used for calling the callback service.
   */
  private ExecutiveCallbackService ecs;

  ExecutiveProxyImpl(ExecutiveCallbackService ecs) {
    this.ecs = ecs;
  }

  /**
   * Notify this executive that the given task has failed. This
   * implementation passes the call onto the callback service, wrapping
   * any exceptions generated.
   *
   * @param taskID the task ID.
   * @param reason the reason for failure
   */
  public void taskFailed(String taskID, String reason)
          throws ExecutiveProxyException {
    try {
      ecs.taskFailed(taskID, reason);
    }
    catch(Exception e) {
      log.error("Exception accessing executive", e);
      throw new ExecutiveProxyException(e);
    }
  }

  /**
   * Notify this executive that the given system task has been finished
   * successfully.
   *
   * @param taskID the task ID.
   */
  public void taskCompleted(String taskID) throws ExecutiveProxyException {
    try {
      ecs.taskCompleted(taskID);
    }
    catch(Exception e) {
      log.error("Exception accessing executive", e);
      throw new ExecutiveProxyException(e);
    }
  }
  
  /**
   * Notify this executive that the given system task has been saved
   *
   * @param taskID the task ID.
   */
  public void taskSaved(String taskID) throws ExecutiveProxyException {
    try {
      ecs.taskSaved(taskID);
    }
    catch(Exception e) {
      log.error("Exception accessing executive", e);
      throw new ExecutiveProxyException(e);
    }
  }

  /**
   * Notify this executive that the given system task has been finished
   * successfully.
   *
   * @param taskID the task ID.
   */
  public void taskFinished(String taskID) throws ExecutiveProxyException {
    try {
      ecs.taskFinished(taskID);
    }
    catch(Exception e) {
      log.error("Exception accessing executive", e);
      throw new ExecutiveProxyException(e);
    }
  }

  /**
   * Notify this executive that the given task has completed
   * successfully.
   *
   * @param taskID the task ID.
   */
  public void taskCanceled(String taskID) throws ExecutiveProxyException {
    try {
      ecs.taskCanceled(taskID);
    }
    catch(Exception e) {
      log.error("Exception accessing executive", e);
      throw new ExecutiveProxyException(e);
    }
  }

  /**
   * Get the next annotation task for a given annotator.
   *
   * @param annotatorID the ID of the annotator
   */
  public AnnotatorTask getNextTask(String annotatorID)
          throws ExecutiveProxyException {
    // final in order to be accessible from inner class
    final AnnotationTask atFromExec;
    try {
      atFromExec = ecs.getNextTask(annotatorID);
    }
    catch(Exception e) {
      log.error("Exception accessing executive", e);
      throw new ExecutiveProxyException(e);
    }

    if(atFromExec == null) {
      return null;
    }
    else {
      return new AnnotatorTask() {
        public URI getDocServiceLocation() {
          return atFromExec.getDocserviceURL();
        }

        public URI getOwlimServiceURL() {
          return atFromExec.getOwlimServiceURL();
        }

        public String getOwlimRepositoryName() {
          return atFromExec.getOwlimRepositoryName();
        }

        public String getDocumentID() {
          return atFromExec.getDocumentId();
        }

        public URI getOntologyLocation() {
          return atFromExec.getOntologyLocation();
        }

        public Date getDueDate() {
          Calendar dueDate = atFromExec.getDueDate();
          if(dueDate == null) {
            return null;
          }
          else {
            return dueDate.getTime();
          }
        }
        
        public Date getLastOpenedDate() {
        	Calendar lastOpenedDate = atFromExec.getLastOpenedDate();
        	if(lastOpenedDate == null) {
        		return null;
        	} else {
        		return lastOpenedDate.getTime();
        	}
        }

        public String getPerformer() {
          return atFromExec.getPerformer();
        }

        public Date getStartDate() {
          Calendar startDate = atFromExec.getStartDate();
          if(startDate == null) {
            return null;
          }
          else {
            return startDate.getTime();
          }
        }

        public String getTaskID() {
          return String.valueOf(atFromExec.getTaskInstanceId());
        }

        public String getTaskName() {
          return atFromExec.getTaskName();
        }

        public String getAnnotationSchemasCSVURLs() {
          return atFromExec.getAnnotationSchemaCSVURLs();
        }

        public String getPluginCSVList() {
          return atFromExec.getPluginCSVList();
        }

        public String getAnnotationSetName() {
          return atFromExec.getAnnotationSetName();
        }
        
        public boolean isCancelAllowed() {
          return atFromExec.isCancelAllowed();
        }

      };
    }
  }

  public boolean checkForNextTask(String annotatorID)
          throws ExecutiveProxyException {
    try {
      return ecs.checkForNextTask(annotatorID);
    }
    catch(Exception e) {
      log.error("Exception accessing executive", e);
      throw new ExecutiveProxyException("Exception accessing executive.  " +
          "Failed method checkForNextTask(\"" + annotatorID + "\")", e);
    }
  }
}
