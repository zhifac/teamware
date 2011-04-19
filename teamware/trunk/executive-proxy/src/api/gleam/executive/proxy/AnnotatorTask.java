/*
 *  AnnotatorTask.java
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

import java.net.URI;
import java.util.Date;

public interface AnnotatorTask {
  /**
   * Gets the URL of the doc service
   *
   * @return
   */
  public URI getDocServiceLocation();

  /**
   * Gets the id of the document to be annotated.
   *
   * @return
   */
  public String getDocumentID();

  /**
   * Gets the id of the task
   *
   * @return
   */
  public String getTaskID();

  public Date getStartDate();

  public Date getDueDate();
  
  public Date getLastOpenedDate();

  public String getPerformer();

  /**
   * Gets the task name
   *
   * @return
   */
  public String getTaskName();

  /**
   * This is the CSV list of loaded annotation schemas
   *
   * @return
   */
  public String getAnnotationSchemasCSVURLs();

  /**
   * Gets the owlim service URL
   *
   * @return
   */
  public URI getOwlimServiceURL();

  /**
   * This is the actual location of the ontology.
   *
   * @return
   */
  public URI getOntologyLocation();

  /**
   * This method returns the name of the repository that should be used to store
   * the ontology data in.
   *
   * @return
   */
  public String getOwlimRepositoryName();

  /**
   * This is the CSV list of loaded plugins
   *
   * @return
   */
  public String getPluginCSVList();

  /**
   *
   * @return annotation Set Name to display
   */
  public String getAnnotationSetName();
  
  /**
   * Is the user allowed to cancel this task?
   */
  public boolean isCancelAllowed();

}
