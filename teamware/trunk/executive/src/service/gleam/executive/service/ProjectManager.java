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

