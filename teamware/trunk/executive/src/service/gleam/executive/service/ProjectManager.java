/*
 *  ProjectManager.java
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

import gleam.executive.model.Project;
import java.util.List;

/**
 * Business Service Interface to handle communication between web and
 * persistence layer.
 *
 * <p>
 * <a href="ProjectManager.java.html"><i>View Source</i></a>
 * </p>
 *
 * @author <a href="mailto:agaton@dcs.shef.ac.uk">Milan Agatonovic</a>
 */
public interface ProjectManager extends Manager {
  
  public List getProjects() throws SafeManagerException;

  public List getProjectsByUserId(Long userId) throws SafeManagerException;
  
  public Project getProjectByName(String name) throws SafeManagerException;
  
  public Project getProject(Long id) throws SafeManagerException;

  public void saveProject(Project project) throws SafeManagerException;

  public void removeProject(Long id) throws SafeManagerException;
  
  public List<Project> getAvailableProjects(Long userId) throws SafeManagerException;

}

