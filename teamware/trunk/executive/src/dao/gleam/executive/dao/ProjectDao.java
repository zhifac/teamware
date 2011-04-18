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
