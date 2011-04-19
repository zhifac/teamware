/*
 *  GosManager.java
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
package gleam.executive.service;

import java.util.List;

public interface GosManager {
  /**
   * Creates a new repository on the server.
   *
   * @param repositoryName -
   *          Name of the repository. This should be unique. If the repository
   *          is already available, this method throws an exception.
   * @param ontologyURL -
   *          This parameter is optional. If provided the ontology is loaded
   *          into the repository.
   * @return
   * @throws SafeManagerException
   */
  //public boolean addRepository(String repositoryName, String ontologyURL) throws SafeManagerException;

  /**
   * List all the repositories in this gos service.
   *
   * @return an array of {@link String} objects. The array may be empty but will
   *         not be <code>null</code>.
   * @throws SafeManagerException
   */
  //public List listRepositories() throws SafeManagerException;

  /**
   * This method deletes all the data from the repository. If the ontologyURL is
   * not null, the method will add new data into the ontology after clearning
   * it.
   *
   * @param repositoryName
   * @return true if the repository was emptied successfully, false otherwise.
   * @throws SafeManagerException
   */
  //public boolean clearRepository(String repositoryName) throws SafeManagerException;

  /**
   * This method deletes the repository from server.
   *
   * @param repositoryName
   * @return
   * @throws SafeManagerException
   */
  //public boolean deleteRepository(String repositoryName)throws SafeManagerException;

  /**
   * This method gets the ontology data from the repository
   *
   * @param repositoryName
   * @return
   * @throws SafeManagerException
   */
  //public String getOntologyData(String repositoryName) throws SafeManagerException;


  /**
   * This method returns GOS URL
   * @return gosURL
   * @throws SafeManagerException
   */
  //public String getGosURL();
}
