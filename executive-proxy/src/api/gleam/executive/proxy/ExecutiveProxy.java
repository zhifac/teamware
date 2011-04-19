/*
 *  ExecutiveProxy.java
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
package gleam.executive.proxy;

/**
 * Interface for access to an executive from Java. To be expanded as
 * need for new operations arises.
 */
public interface ExecutiveProxy {
  /**
   * Notify this executive that the given system task has completed
   * successfully.
   *
   * @param tokenId the token Id.
   */
  public void taskFinished(String tokenId) throws ExecutiveProxyException;

  /**
   * Notify this executive that the given task has Completed
   * successfully.
   *
   * @param taskID the task ID.
   */
  public void taskCompleted(String taskID) throws ExecutiveProxyException;
  
  /**
   * Notify this executive that the given task has been saved.
   *
   * @param taskID the task ID.
   */
  public void taskSaved(String taskID) throws ExecutiveProxyException;
  
  /**
   * Notify this executive that the given task has canceled
   * successfully.
   *
   * @param taskID the task ID.
   */
  public void taskCanceled(String taskID) throws ExecutiveProxyException;

  /**
   * Notify this executive that the given task has failed to complete.
   *
   * @param taskID the task ID.
   * @param reason the reason for failure (may be null)
   */
  public void taskFailed(String taskID, String reason)
          throws ExecutiveProxyException;

  /**
   * Ask the executive for the next annotation task for a given annotator.
   *
   * @param annotatorID the ID of the annotator.
   */
  public AnnotatorTask getNextTask(String annotatorID)
          throws ExecutiveProxyException;
  
  
  /**
   * Notifies executive that annotator is ready to accept next annotation task
   *
   * @param annotatorID the ID of the annotator.
   */
  public boolean checkForNextTask(String annotatorID)
          throws ExecutiveProxyException;
}
