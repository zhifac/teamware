/*
 *  RoleDaoHibernate.java
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
import gleam.executive.dao.RoleDao;
import gleam.executive.model.Role;

/**
 * This class interacts with Spring's HibernateTemplate to save/delete
 * and retrieve Role objects.
 * 
 * <p>
 * <a href="RoleDaoHibernate.java.html"><i>View Source</i></a>
 * </p>
 * 
 * @author <a href="mailto:dan@getrolling.com">Dan Kibler</a>
 */
public class RoleDaoHibernate extends BaseDaoHibernate implements RoleDao {
  public List getRoles(Role role) {
    return getHibernateTemplate().find("from Role where id>1");
  }

  public Role getRole(Long roleId) {
    return (Role)getHibernateTemplate().get(Role.class, roleId);
  }

  public Role getRoleByName(String rolename) {
    List roles = getHibernateTemplate()
            .find("from Role where name=?", rolename);
    if(roles.isEmpty()) {
      return null;
    }
    else {
      return (Role)roles.get(0);
    }
  }

  public void saveRole(Role role) {
    getHibernateTemplate().merge(role);
    getHibernateTemplate().flush();
    //getHibernateTemplate().saveOrUpdate(role);
  }

  public void removeRole(String rolename) {
    Object role = getRoleByName(rolename);
    getHibernateTemplate().delete(role);
  }

  public List getRolesWithResource(String url) {
    StringBuffer sb = new StringBuffer();
    sb.append("SELECT DISTINCT role from ")
      .append (Role.class.getName() + " role ")
      .append (" JOIN role.resources resources ")
      .append (" WHERE resources.url = '" + url +"' ");
    return getHibernateTemplate().find(sb.toString());
  }
  
}
