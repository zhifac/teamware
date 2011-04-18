package gleam.executive.dao.ldap;

import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.GrantedAuthorityImpl;

import org.acegisecurity.ldap.InitialDirContextFactory;
import org.acegisecurity.ldap.LdapTemplate;

import org.acegisecurity.providers.ldap.LdapAuthoritiesPopulator;

import org.acegisecurity.userdetails.ldap.LdapUserDetails;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.util.Assert;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;


/**
 * The default strategy for obtaining user role information from the directory.<p>It obtains roles by
 * performing a search for "groups" the user is a member of.
 *  </p>
 *  <p>A typical group search scenario would be where each group/role is specified using the <tt>groupOfNames</tt>
 * (or <tt>groupOfUniqueNames</tt>) LDAP objectClass and the user's DN is listed in the <tt>member</tt> (or
 * <tt>uniqueMember</tt>) attribute to indicate that they should be assigned that role. The following LDIF sample has
 * the groups stored under the DN <tt>ou=groups,dc=acegisecurity,dc=org</tt> and a group called "developers" with
 * "ben" and "marissa" as members:
 * <pre>dn: ou=groups,dc=acegisecurity,dc=orgobjectClass: top
 * objectClass: organizationalUnitou: groupsdn: cn=developers,ou=groups,dc=acegisecurity,dc=org
 * objectClass: groupOfNamesobjectClass: topcn: developersdescription: Acegi Security Developers
 * member: uid=ben,ou=people,dc=acegisecurity,dc=orgmember: uid=marissa,ou=people,dc=acegisecurity,dc=orgou: developer
 * </pre>
 * </p>
 *  <p>The group search is performed within a DN specified by the <tt>groupSearchBase</tt> property, which should
 * be relative to the root DN of its <tt>InitialDirContextFactory</tt>. If the search base is null, group searching is
 * disabled. The filter used in the search is defined by the <tt>groupSearchFilter</tt> property, with the filter
 * argument {0} being the full DN of the user. You can also specify which attribute defines the role name by setting
 * the <tt>groupRoleAttribute</tt> property (the default is "cn").</p>
 *  <p>The configuration below shows how the group search might be performed with the above schema.<pre>
 * &lt;bean id="ldapAuthoritiesPopulator" class="org.acegisecurity.providers.ldap.populator.DefaultLdapAuthoritiesPopulator">
 *   &lt;constructor-arg>&lt;ref local="initialDirContextFactory"/>&lt;/constructor-arg>
 *   &lt;constructor-arg>&lt;value>ou=groups&lt;/value>&lt;/constructor-arg>
 *   &lt;property name="groupRoleAttribute">&lt;value>ou&lt;/value>&lt;/property>
 * &lt;!-- the following properties are shown with their default values -->
 *   &lt;property name="searchSubTree">&lt;value>false&lt;/value>&lt;/property>
 *   &lt;property name="rolePrefix">&lt;value>ROLE_&lt;/value>&lt;/property>
 *   &lt;property name="convertToUpperCase">&lt;value>true&lt;/value>&lt;/property>
 * &lt;/bean>
 * </pre>A search for
 * roles for user "uid=ben,ou=people,dc=acegisecurity,dc=org" would return the single granted authority
 * "ROLE_DEVELOPER".</p>
 *
 * @author Luke Taylor
 * @version $Id$
 */
public class SafeLdapAuthoritiesPopulator implements LdapAuthoritiesPopulator {
    //~ Static fields/initializers =====================================================================================

    private static final Log logger = LogFactory.getLog(SafeLdapAuthoritiesPopulator.class);

    //~ Instance fields ================================================================================================

    /** A default role which will be assigned to all authenticated users if set */
    private GrantedAuthority defaultRole = null;

    /** An initial context factory is only required if searching for groups is required. */
    private InitialDirContextFactory initialDirContextFactory = null;
    private LdapTemplate ldapTemplate;

    /**
     * Controls used to determine whether group searches should be performed over the full sub-tree from the
     * base DN. Modified by searchSubTree property
     */
    private SearchControls searchControls = new SearchControls();

 

    /** Attributes of the User's LDAP Object that contain role name information. */

//    private String[] userRoleAttributes = null;
    private String rolePrefix = "";
    private boolean convertToUpperCase = false;

    //~ Constructors ===================================================================================================

    /**
     * Constructor for group search scenarios. <tt>userRoleAttributes</tt> may still be
     * set as a property.
     *
     * @param initialDirContextFactory supplies the contexts used to search for user roles.
     * @param groupSearchBase if this is an empty string the search will be performed from the root DN of the
     * context factory.
     */
    public SafeLdapAuthoritiesPopulator(InitialDirContextFactory initialDirContextFactory) {
        this.setInitialDirContextFactory(initialDirContextFactory);
    }

    //~ Methods ========================================================================================================

    /**
     * This method should be overridden if required to obtain any additional
     * roles for the given user (on top of those obtained from the standard
     * search implemented by this class).
     *
     *
     * @param ldapUser the user who's roles are required
     * @return the extra roles which will be merged with those returned by the group search
     */

    protected Set getAdditionalRoles(LdapUserDetails ldapUser) {
        return null;
    }

    /**
     * Obtains the authorities for the user who's directory entry is represented by
     * the supplied LdapUserDetails object.
     *
     * @param userDetails the user who's authorities are required
     *
     * @return the set of roles granted to the user.
     */
    public final GrantedAuthority[] getGrantedAuthorities(LdapUserDetails userDetails) {
        String userDn = userDetails.getDn();
        if (logger.isDebugEnabled()) {
            logger.debug("Getting authorities for user " + userDn);
        }
        Attributes attributes = userDetails.getAttributes();
        Attribute memberOfAttibute = attributes.get("memberOf");
        Set roles = new HashSet();
        if(memberOfAttibute!=null){
        
        
        try{
        NamingEnumeration<?> enumeration = memberOfAttibute.getAll();
        while(enumeration.hasMoreElements()){
        	Object value = enumeration.nextElement();
        	if(value!=null){
        		roles.add(new GrantedAuthorityImpl(value.toString()));
        	}
        	logger.debug(value);
        }
        }
        

        
        catch(NamingException e){
        	e.printStackTrace();
        }
        
        }

        return (GrantedAuthority[]) roles.toArray(new GrantedAuthority[roles.size()]);
    }



    
    

    protected InitialDirContextFactory getInitialDirContextFactory() {
        return initialDirContextFactory;
    }

    /**
     * Set the {@link InitialDirContextFactory}
     * 
     * @param initialDirContextFactory supplies the contexts used to search for user roles.
     */
    private void setInitialDirContextFactory(InitialDirContextFactory initialDirContextFactory) {
        Assert.notNull(initialDirContextFactory, "InitialDirContextFactory must not be null");
        this.initialDirContextFactory = initialDirContextFactory;

        ldapTemplate = new LdapTemplate(initialDirContextFactory);
        ldapTemplate.setSearchControls(searchControls);
    }

    
    public void setConvertToUpperCase(boolean convertToUpperCase) {
        this.convertToUpperCase = convertToUpperCase;
    }


    public void setRolePrefix(String rolePrefix) {
        //Assert.notNull(rolePrefix, "rolePrefix must not be null");
        this.rolePrefix = rolePrefix;
    }

    public void setSearchSubtree(boolean searchSubtree) {
        int searchScope = searchSubtree ? SearchControls.SUBTREE_SCOPE : SearchControls.ONELEVEL_SCOPE;
        searchControls.setSearchScope(searchScope);
    }

	
}
