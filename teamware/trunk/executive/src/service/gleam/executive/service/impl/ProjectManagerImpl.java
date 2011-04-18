package gleam.executive.service.impl;

import java.util.List;
import gleam.executive.dao.ProjectDao;
import gleam.executive.model.Project;
import gleam.executive.service.ProjectManager;
import gleam.executive.service.SafeManagerException;

/**
 * Implementation of ProjectManager interface. </p>
 * 
 * <p>
 * <a href="ProjectManagerImpl.java.html"><i>View Source</i></a>
 * </p>
 * 
 * @author <a href="M.Agatonovic@dcs.shef.ac.uk">Milan Agatonovic</a>
 */
public class ProjectManagerImpl extends BaseManager implements ProjectManager {
	private ProjectDao dao;

	public void setProjectDao(ProjectDao dao) {
		this.dao = dao;
	}

	public List getProjects() throws SafeManagerException {
		try {
			return dao.getProjects();
		} catch (Exception e) {
			e.printStackTrace();
			throw new SafeManagerException(e.getMessage());
		}
	}

	public List getProjectsByUserId(Long userId) throws SafeManagerException {
		try {
			return dao.getProjectsByUserId(userId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new SafeManagerException(e.getMessage());
		}
	}

	public Project getProject(Long id) throws SafeManagerException {
		try {
			return dao.getProject(id);
		} catch (Exception e) {
			e.printStackTrace();
			throw new SafeManagerException(e.getMessage());
		}
	}

	public Project getProjectByName(String name) throws SafeManagerException {
		try {
			return dao.getProjectByName(name);
		} catch (Exception e) {
			e.printStackTrace();
			throw new SafeManagerException(e.getMessage());
		}
	}

	public void saveProject(Project project) throws SafeManagerException {
		try {
			dao.saveProject(project);
		} catch (Exception e) {
			e.printStackTrace();
			throw new SafeManagerException(e.getMessage());
		}
	}

	public void removeProject(Long id) throws SafeManagerException {
		try {
			dao.removeProject(id);
		} catch (Exception e) {
			e.printStackTrace();
			throw new SafeManagerException(e.getMessage());
		}
	}
	public List getAvailableProjects(Long userId) throws SafeManagerException {
		try {
			return dao.getAvailableProjects(userId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new SafeManagerException(e.getMessage());
		}
	}

}
