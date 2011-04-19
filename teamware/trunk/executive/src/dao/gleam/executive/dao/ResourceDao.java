/*
 *  ResourceDao.java
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
import gleam.executive.model.Resource;
import gleam.executive.model.Service;

/**
 * Resource Data Access Object (Dao) interface.
 *
 * <p>
 * <a href="ResourceDao.java.html"><i>View Source</i></a>
 * </p>
 *
 * @author <a href="mailto:agaton@dcs.shef.ac.uk">Milan Agatonovic</a>
 */
public interface ResourceDao extends Dao {
  /**
   * Gets users information based on resource id.
   *
   * @param resourceId the resource's id
   * @return SecuredResource populated SecuredResource object
   */
  public Resource getResource(Long resourceId);

  /**
   * Gets a list of resources based on parameters passed in.
   *
   * @return List populated list of resources
   */
  public List getResources(Resource resource);

  /**
   * 
   * @return a list of all the resources in DB.
   */
  public List getResources();
  
  /**
   * Saves a resource's information
   *
   * @param resource the object to be saved
   */
  public void saveResource(Resource resource);

  /**
   * Removes a resource from the database by id
   *
   * @param resourceId the resource's id
   */
  public void removeResource(Long resourceId);

  /**
   * Fetches all resources from the database with specified role
   *
   * @param roleName
   *          the role name
   * @return List of resources
   */
  public List getResourcesWithRole(String roleName);
  
  /**
   * Fetches all roles from the database with specified resource url
   *
   * @param url
   *          the resource url
   * @return List of roles
   */
  public List getRolesWithResource(String url);
  
  /**
   * Fetches the relevant service from the database with specified resource
   *
   * @param resourceId
   *          the resource id
   * @return List of services
   */
  public Service getService(Long resourceId);
  
  /**
   * Fetches the relevant services from the database with specified service id
   *
   * @param serviceId
   * @return List of resources
   */
  public List getResourcesWithService(Long serviceId);

}
