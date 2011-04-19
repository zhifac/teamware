/*
 *  ProjectDaoHibernate.java
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
package gleam.executive.dao.hibernate;

import java.util.Date;
import java.util.List;
import gleam.executive.dao.ProjectDao;
import gleam.executive.model.Project;

/**
 * This class interacts with Spring's HibernateTemplate to save/delete
 * and retrieve Project objects.
 * 
 * <p>
 * <a href="ProjectDaoHibernate.java.html"><i>View Source</i></a>
 * </p>
 * 
 * @author <a href="M.Agatonovic@dcs.shef.ac.uk">Milan Agatonovic</a>
 */
public class ProjectDaoHibernate extends BaseDaoHibernate implements ProjectDao {
  
	
  public List getProjects() {
    return getHibernateTemplate().find("from Project");
  }

  public Project getProject(Long projectId) {
    return (Project)getHibernateTemplate().get(Project.class, projectId);
  }

  public List getProjectsByUserId(Long userId) {
	  return getHibernateTemplate()
      .find("from Project where userId=?", userId);
  }
  
  public Project getProjectByName(String projectname) {
    List projects = getHibernateTemplate()
            .find("from Project where name=?", projectname);
    if(projects.isEmpty()) {
      return null;
    }
    else {
      return (Project)projects.get(0);
    }
  }

  public void saveProject(Project project) {
	project.setLastUpdate(new Date());
    getHibernateTemplate().merge(project);
    getHibernateTemplate().flush();
    //getHibernateTemplate().saveOrUpdate(project);
  }

  public void removeProject(Long id) {
    Object project = getProject(id);
    getHibernateTemplate().delete(project);
  }


  public List getAvailableProjects(Long userId) {
	  return getHibernateTemplate()
      .find("from Project where enabled='Y' and version>0 and userId=?", userId);
  }
  
}
