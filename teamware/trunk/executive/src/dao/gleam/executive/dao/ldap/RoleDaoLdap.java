/*
 *  RoleDaoLdap.java
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
package gleam.executive.dao.ldap;

import java.io.Serializable;
import java.util.List;

import javax.naming.Name;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.ldap.EntryNotFoundException;
import org.springframework.ldap.support.DirContextAdapter;
import org.springframework.ldap.support.DistinguishedName;
import org.springframework.ldap.support.filter.EqualsFilter;

import gleam.executive.dao.RoleDao;
import gleam.executive.model.Role;

public class RoleDaoLdap extends LdapDaoSupport implements RoleDao {
    
    public List getRoles(Role role) {
        EqualsFilter filter = new EqualsFilter("objectclass", "groupOfUniqueNames");
        return ldapTemplate.search("ou=groups", filter.encode(), getRoleContextMapper());
    }
    
    public Role getRole(Long roleId) {
        throw new NotImplementedException();
    }

    public Role getRoleByName(String rolename) {
        log.debug("Loading from LDAP...");
        try {
            Name dn = buildDn(rolename);
            return (Role) ldapTemplate.lookup(dn, getRoleContextMapper());
        } catch (EntryNotFoundException enf) {
            log.debug("Entry not found, returning null...");
            return null;
        }
    }
    
    protected Name buildDn(String roleName) {
        DistinguishedName dn = new DistinguishedName();
        dn.add("ou", "groups");
        dn.add("cn", roleName);
        return dn;
    }

    /**
     * If a role doesn't have an id assigned to it, it's assumed to be a new role.  If it has an id
     * assigned to it, it's an existing role.  This logic is flawed because new roles don't get id's
     * assigned to them.
     * 
     * @param role The role information you want to update/save
     */
    public void saveRole(Role role) {
        Name dn = buildDn(role.getName());
        DirContextAdapter context = (DirContextAdapter) ldapTemplate.lookup(dn);
        mapToContext(role, context);
        ldapTemplate.modifyAttributes(dn, context.getModificationItems());
    }

    protected void mapToContext(Role role, DirContextAdapter context) {
        context.setAttributeValues("objectclass", new String[]{"top", "groupOfUniqueNames"});
        context.setAttributeValue("cn", role.getName());
        context.setAttributeValue("description", role.getDescription());
        context.setAttributeValues("uniqueMember", role.getMembers());
    }

    public void removeRole(String rolename) {
        ldapTemplate.unbind(buildDn(rolename));
    }
    
    public List getRolesWithResource(String url) {
    	  throw new NotImplementedException();
    }
}
