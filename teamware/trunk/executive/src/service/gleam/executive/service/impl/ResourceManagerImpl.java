package gleam.executive.service.impl;

import java.util.List;

import gleam.executive.dao.ResourceDao;
import gleam.executive.dao.RoleDao;
import gleam.executive.model.Resource;
import gleam.executive.model.Service;
import gleam.executive.service.ResourceManager;


/**
 * Implementation of ResourceManager interface.
 * </p>
 *
 * <p>
 * <a href="ResourceManagerImpl.java.html"><i>View Source</i></a>
 * </p>
 * @author <a href="M.Agatonovic@dcs.shef.ac.uk">Milan Agatonovic</a>
 * @author <a href="mailto:H.Sun@dcs.shef.ac.uk">Haotian Sun</a>
 */
public class ResourceManagerImpl extends BaseManager implements ResourceManager {
  private ResourceDao dao;

  private RoleDao roleDao;

  public void setResourceDao(ResourceDao dao) {
    this.dao = dao;
  }

  public void setRoleDao(RoleDao rdao){
    this.roleDao = rdao;
  }

  public List getResources(Resource resource) {
    return dao.getResources(resource);
  }

  public List getResources() {
    return dao.getResources();
  }

  public Resource getResource(String resourceId) {
    return dao.getResource(new Long(resourceId));
  }

  public void saveResource(Resource resource) {
    dao.saveResource(resource);
  }

  public void removeResource(String resourceId) {
    if(log.isDebugEnabled()) {
      log.debug("removing resource: " + resourceId);
    }
    dao.removeResource(new Long(resourceId));
  }

  public List getResourcesWithRole(String rolename){
    if(log.isDebugEnabled()) {
      log.debug("searching resources with role name: " + rolename);
    }
    return (List)dao.getResourcesWithRole(rolename);
  }


  public List getRolesWithResource(String url) {
    if(log.isDebugEnabled()) {
      log.debug("searching roles with resource url: " + url);
    }
    return (List)roleDao.getRolesWithResource(url);
  }

  public List getResourcesWithService(String serviceId){
    if(log.isDebugEnabled()) {
      log.debug("searching resources with service id: " + serviceId);
    }
    return (List)dao.getResourcesWithService(new Long(serviceId));
  }

  public Service getService(String resourceId) {
    return (Service)dao.getService(new Long(resourceId));
  }
}
