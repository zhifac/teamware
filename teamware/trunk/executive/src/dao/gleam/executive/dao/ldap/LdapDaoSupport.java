/*
 *  LdapDaoSupport.java
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

import gleam.executive.dao.Dao;
import gleam.executive.model.Role;

import java.util.List;
import java.io.Serializable;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.ldap.LdapTemplate;
import org.springframework.ldap.ContextMapper;
import org.springframework.ldap.support.DirContextAdapter;

/**
 * @author mraible
 */
public class LdapDaoSupport implements Dao {
    protected final Log log = LogFactory.getLog(getClass());
    protected LdapTemplate ldapTemplate;

    public void setLdapTemplate(LdapTemplate ldapTemplate) {
        this.ldapTemplate = ldapTemplate;
    }

    public List getObjects(Class clazz) {
        throw new NotImplementedException();
    }

    public Object getObject(Class clazz, Serializable id) {
        throw new NotImplementedException();
    }

    public void saveObject(Object o) {
        throw new NotImplementedException();
    }

    public void removeObject(Class clazz, Serializable id) {
        throw new NotImplementedException();
    }

    protected ContextMapper getRoleContextMapper() {
        return new RoleContextMapper();
    }

    private static class RoleContextMapper implements ContextMapper {
        public Object mapFromContext(Object ctx) {
            DirContextAdapter context = (DirContextAdapter) ctx;
            Role role = new Role(context.getStringAttribute("cn"));
            role.setDescription(context.getStringAttribute("description"));
            role.setMembers(context.getStringAttributes("uniqueMember"));
            return role;
        }
    }
}
