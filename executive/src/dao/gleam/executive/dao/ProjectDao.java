/*
 *  ProjectDao.java
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
package gleam.executive.dao;

import java.util.List;

import gleam.executive.model.Project;

/**
 * ProjectDao Access Object (DAO) interface.
 * 
 * <p>
 * <a href="ProjectDao.java.html"><i>View Source</i></a>
 * </p>
 * 
 * @author <a href="mailto:agaton@dcs.shef.ac.uk">Milan Agatonovic</a>
 */
public interface ProjectDao extends Dao {
  
	
 /**
   * Gets project based on project id
   * 
   * @param project id
   * @return populated project object
   */
  public Project getProject(Long projectId);	
 /**
   * Gets project based on project name
   * 
   * @param project name
   * @return populated project object
   */
  public Project getProjectByName(String projectName);

  /**
   * Gets project s for specified user
   * 
   * @param userId
   * @return List populated with project objects
   */
  public List getProjectsByUserId(Long userId);
  
  /**
   * Gets a list of projects
   * 
   * @return List populated list of projects
   */
  public List getProjects();

  /**
   * Saves a project's information
   * 
   * @param project the object to be saved
   */
  public void saveProject(Project project);

  /**
   * Removes a project from the database by id
   * 
   * @param id the project's id
   */
  public void removeProject(Long id);
  
  
  /**
   * Gets available (READY) projects for specified user
   * 
   * @param userId
   * @return List populated with project objects
   */
  public List<Project> getAvailableProjects(Long userId);
  
}
