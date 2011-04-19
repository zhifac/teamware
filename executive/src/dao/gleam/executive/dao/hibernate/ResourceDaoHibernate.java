/*
 *  ResourceDaoHibernate.java
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

import java.util.List;

import gleam.executive.dao.ResourceDao;
import gleam.executive.model.Resource;
import gleam.executive.model.Service;
import org.springframework.orm.ObjectRetrievalFailureException;

/**
 * This class interacts with Spring's HibernateTemplate to save/delete
 * and retrieve Resource objects.
 *
 * <p>
 * <a href="ResourceDaoHibernate.java.html"><i>View Source</i></a>
 * </p>
 *
 * @author <a href="mailto:H.Sun@dcs.shef.ac.uk">Haotian Sun</a>
 */
public class ResourceDaoHibernate extends BaseDaoHibernate implements ResourceDao{
  /**
   * @see gleam.executive.dao.ResourceDao#getResource(Long)
   */
  public Resource getResource(Long resourceId) {
    Resource resource = (Resource)getHibernateTemplate().get(Resource.class, resourceId);
    if(resource == null) {
      log.warn("uh oh, resource '" + resourceId + "' not found...");
      throw new ObjectRetrievalFailureException(Resource.class, resourceId);
    }
    return resource;
  }

  /**
   * @see gleam.executive.dao.ResourceDao#getResources(gleam.executive.model.Resource)
   */
  public List getResources(Resource resource) {
    return getHibernateTemplate()
            .find("from Resource sr order by upper(sr.url)");
  }
  
  /**
   * @see gleam.executive.dao.ResourceDao#getResources()
   */
  public List getResources(){
    return getHibernateTemplate()
    .find("from Resource sr order by upper(sr.url)");
  }
  
  /**
   * @see gleam.executive.dao.ResourceDao#saveResource(gleam.executive.model.Resource)
   */
  public void saveResource(final Resource resource) {
    if(log.isDebugEnabled()) {
      log.debug("resource's id: " + resource.getId());
    }
    //getHibernateTemplate().getSessionFactory().getCurrentSession().merge(resource.getService());
    getHibernateTemplate().merge(resource);
   
    // necessary to throw a DataIntegrityViolation and catch it in
    // ResourceManager
    getHibernateTemplate().flush();
  }

  /**
   * @see gleam.executive.dao.ResourceDao#removeResource(Long)
   */
  public void removeResource(Long resourceId) {
    getHibernateTemplate().delete(getResource(resourceId));
    
  }

  /**
   * @see gleam.executive.dao.ResourceDao#getResourcesByRole(String)
   */
  public List getResourcesWithRole(String roleName) {
    StringBuffer sb = new StringBuffer();
    sb.append("SELECT DISTINCT resource from ")
      .append (Resource.class.getName() + " resource ")
      .append (" JOIN resource.roles roles ")
      .append (" WHERE roles.name = '" + roleName +"' ");
    return getHibernateTemplate().find(sb.toString());
  }

  public List getRolesWithResource(String url) {
    StringBuffer sb = new StringBuffer();
    System.out.println("Resource of the class name is "+Resource.class.getName());
    sb.append("SELECT DISTINCT resource from ")
      .append (Resource.class.getName() + " resource ")
      .append (" JOIN resource.roles roles ")
      .append (" WHERE roles.url = '" + url +"' ");
    return getHibernateTemplate().find(sb.toString());
  }
  
  /**
   * @see gleam.executive.dao.ResourceDao#getServiceWithResource(String)
   */
  public Service getService(Long resourceId) {
    Resource resource = (Resource)getHibernateTemplate().get(Resource.class, resourceId);
    if(resource == null) {
      log.warn("uh oh, resource '" + resourceId + "' not found in getService()...");
      throw new ObjectRetrievalFailureException(Resource.class, resourceId);
    }
    Service service = (Service)getHibernateTemplate().get(Service.class, resource.getService_id());
    if(service == null) {
      log.warn("uh oh, service '" + resource.getService_id() + "' not found...");
      throw new ObjectRetrievalFailureException(Service.class, resource.getService_id());
    }
    return service;
  }
  
  /**
   * @see gleam.executive.dao.ResourceDao#getResourcesWithService(Long)
   */
  public List getResourcesWithService(Long serviceId){
    StringBuffer sb = new StringBuffer();
    System.out.println("Resource of the class name is "+Resource.class.getName());
    sb.append("SELECT DISTINCT resource from ")
      .append (Resource.class.getName() + " resource ")
      .append (" WHERE service_id = '" + serviceId +"' ");
    return getHibernateTemplate().find(sb.toString());
  }
}
