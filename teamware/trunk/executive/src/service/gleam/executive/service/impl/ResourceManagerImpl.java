/*
 *  ResourceManagerImpl.java
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
