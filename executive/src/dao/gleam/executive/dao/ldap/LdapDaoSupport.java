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
