/*
 *  RoleDao.java
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
import gleam.executive.model.Role;

/**
 * Role Data Access Object (DAO) interface.
 * 
 * <p>
 * <a href="RoleDao.java.html"><i>View Source</i></a>
 * </p>
 * 
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 */
public interface RoleDao extends Dao {
  /**
   * Gets role information based on rolename
   * 
   * @param rolename the rolename
   * @return role populated role object
   */
  public Role getRoleByName(String rolename);

  /**
   * Gets a list of roles based on parameters passed in.
   * 
   * @return List populated list of roles
   */
  public List getRoles(Role role);

  /**
   * Saves a role's information
   * 
   * @param role the object to be saved
   */
  public void saveRole(Role role);

  /**
   * Removes a role from the database by name
   * 
   * @param rolename the role's rolename
   */
  public void removeRole(String rolename);
  
  /**
   * Get roles for a resource from the database by url
   * @param url the resource's url
   * @return
   */
  public List getRolesWithResource(String url);
}
