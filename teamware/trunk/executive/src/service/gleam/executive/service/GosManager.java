package gleam.executive.service;

import java.util.List;

/*
 *  GosManager.java
 *
 *  Copyright (c) 1998-2007, The University of Sheffield.
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 * <p>
 * <a href="GosManager.java.html"><i>View Source</i></a>
 * </p>
 * @author <a href="mailto:agaton@dcs.shef.ac.uk">Milan Agatonovic</a>
 *  @author <a href="H.Sun@dcs.shef.ac.uk">Haotian Sun</a>
 */
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
