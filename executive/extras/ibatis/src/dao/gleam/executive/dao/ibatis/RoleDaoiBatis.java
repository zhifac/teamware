/*
 *  RoleDaoiBatis.java
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
package gleam.executive.dao.ibatis;

import java.util.List;

import gleam.executive.dao.RoleDao;
import gleam.executive.model.Role;

/**
 * This class interacts with iBatis's SQL Maps to save/delete and
 * retrieve Role objects.
 *
 * <p>
 * <a href="RoleDaoiBatis.java.html"><i>View Source</i></a>
 * </p>
 *
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 */
public class RoleDaoiBatis extends BaseDaoiBATIS implements RoleDao {

    public List getRoles(Role role) {
        return getSqlMapClientTemplate().queryForList("getRoles", null);
    }
    
    public Role getRoleByName(String name) {
        return (Role) getSqlMapClientTemplate().queryForObject("getRoleByName", name);
    }

    public void saveRole(final Role role) {
        if (role.getId() == null) {
            getSqlMapClientTemplate().update("addRole", role);
        } else {
            getSqlMapClientTemplate().update("updateRole", role);
        }
    }

    public void removeRole(String rolename) {
        getSqlMapClientTemplate().update("deleteRole", rolename);
    }

}
