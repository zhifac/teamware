/*
 *  UserContextMapper.java
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

import org.springframework.ldap.ContextMapper;
import org.springframework.ldap.LdapTemplate;
import org.springframework.ldap.support.DirContextAdapter;
import org.springframework.ldap.support.DistinguishedName;
import gleam.executive.model.User;
import gleam.executive.model.Role;

import java.util.List;
import java.util.LinkedHashSet;
import java.text.MessageFormat;

/**
 * @author mraible
 */
public class UserContextMapper extends LdapDaoSupport implements ContextMapper {
    private String usersDn;

    public UserContextMapper(LdapTemplate ldapTemplate, String usersDn) {
        this.ldapTemplate = ldapTemplate;
        this.usersDn = usersDn;
    }

    public Object mapFromContext(Object ctx) {
        DirContextAdapter context = (DirContextAdapter) ctx;
        User user = new User();
        user.setUsername(context.getStringAttribute("uid"));
        user.setPassword(mapPassword(context.getObjectAttribute("userPassword")));
        user.setFirstName(context.getStringAttribute("cn"));
        user.setLastName(context.getStringAttribute("sn"));
        user.setEmail(context.getStringAttribute("mail"));
        user.setPhoneNumber(context.getStringAttribute("telephoneNumber"));
        //user.setTitle(context.getStringAttribute("title"));
        //user.setDepartment(context.getStringAttribute("department"));
        user.setPasswordHint(context.getStringAttribute("passwordHint"));
        user.setVersion(Integer.valueOf(context.getStringAttribute("version")));
        user.setEnabled(Boolean.valueOf(context.getStringAttribute("accountEnabled")));
        user.setAccountExpired(Boolean.valueOf(context.getStringAttribute("accountExpired")));
        user.setAccountLocked(Boolean.valueOf(context.getStringAttribute("accountLocked")));
        user.setCredentialsExpired(Boolean.valueOf(context.getStringAttribute("credentialsExpired")));

        List roles = ldapTemplate.search(DistinguishedName.EMPTY_PATH,
                MessageFormat.format("(&(objectclass=groupOfUniqueNames)(uniquemember={0}))",
                MessageFormat.format("uid={0},{1}", user.getUsername(), usersDn)),
                getRoleContextMapper());
        //noinspection unchecked
        user.setRoles(new LinkedHashSet<Role>(roles));
        return user;
    }

    /**
     * Extension point to allow customized creation of the user's password from
     * the attribute stored in the directory.
     *
     * @param retrievedPassword the password as an object
     * @return a String representation of the password.
     */
    protected static String mapPassword(Object retrievedPassword) {
        if (retrievedPassword != null && !(retrievedPassword instanceof String)) {
            // Assume it's binary
            retrievedPassword = new String((byte[]) retrievedPassword);
        }

        return (String) retrievedPassword;

    }
}
