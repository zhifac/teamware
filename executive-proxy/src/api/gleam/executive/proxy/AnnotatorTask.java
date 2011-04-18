/*
 * This file is part of SAFE, licenced under the GNU Lesser General Public
 * Licence.
 *
 * (c) 2006 The University of Sheffield
 *
 * $Id$
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
