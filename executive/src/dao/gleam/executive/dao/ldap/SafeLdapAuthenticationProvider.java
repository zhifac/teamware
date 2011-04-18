package gleam.executive.dao.ldap;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

import gleam.executive.dao.RoleDao;
import gleam.executive.dao.UserDao;
import gleam.executive.model.Address;
import gleam.executive.model.Role;
import gleam.executive.model.User;
import gleam.executive.util.StringUtil;

import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.providers.encoding.Md5PasswordEncoder;
import org.acegisecurity.providers.encoding.PasswordEncoder;
import org.acegisecurity.providers.ldap.LdapAuthenticationProvider;
import org.acegisecurity.providers.ldap.LdapAuthenticator;
import org.acegisecurity.providers.ldap.LdapAuthoritiesPopulator;

import org.acegisecurity.userdetails.UserDetails;
import org.acegisecurity.userdetails.UsernameNotFoundException;
import org.acegisecurity.userdetails.ldap.LdapUserDetails;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class SafeLdapAuthenticationProvider extends LdapAuthenticationProvider {
   //~ Static fields/initializers =====================================================================================

   private static final Log log = LogFactory.getLog(SafeLdapAuthenticationProvider.class);

	/**
    * Create an initialized instance to the values passed as arguments
    *
    * @param authenticator
    * @param authoritiesPopulator
    */
   public SafeLdapAuthenticationProvider(LdapAuthenticator authenticator, LdapAuthoritiesPopulator authoritiesPopulator) {
       super(authenticator, authoritiesPopulator);
   }

	/**
	 * Overriden createUserDetails() method, where we actually hook in
	 * to convert our LdapUserDetails to a UserDetails of type
	 * org.appfuse.model.User and enrich it with some properties
	 * and roles from the database.
	 * 
	 * @see LdapAuthenticationProvider#createUserDetails()
	 */
	protected UserDetails createUserDetails(LdapUserDetails ldapUser, String username, String password) {

		LdapUserDetails userDetails = (LdapUserDetails)super.createUserDetails(ldapUser, username, password);
	
		User user = null;
		
		if(userDetails!=null && 
		   userDetails.isAccountNonExpired() && 
		   userDetails.isAccountNonLocked() && 
		   userDetails.isCredentialsNonExpired() && 
		   userDetails.isEnabled()){
		try {
			user = (User) userDao.loadUserByUsername(username);
			log.debug("found user in DB: "+user.getUsername());
			// update password if necessary
			
			if (!passwordEncoder.isPasswordValid(user.getPassword(), password, null)) {
				log.debug("password is invalid");
				String encodedPassword = StringUtil.encodePassword(password, algorithm); 
				user.setPassword(encodedPassword);
				log.debug("updated to: "+encodedPassword);
				userDao.saveUser(user);
			}
			else {
				log.debug("password is valid");
			}
			
		} catch (UsernameNotFoundException e) {
			// user not found in db, create a new one
			log.debug("not found user in DB: "+username);
			
			// fetch all you need from userDetails;
			// attributes on AD
			// mail
			// givenName
			// sn
			Attributes attributes = userDetails.getAttributes();
	        Attribute givenNameAttribute = attributes.get("givenName");
	        Attribute snAttribute = attributes.get("sn");
	        Attribute mailAttribute = attributes.get("mail");
	        Attribute phoneNumberAttribute = attributes.get("telephoneNumber");
	        // roles from active directory
		
	        GrantedAuthority[] authorities = userDetails.getAuthorities();
			HashSet<String> adRoles = new HashSet<String>();
			for (int i = 0; i < authorities.length; i++) {

				adRoles.add(authorities[i].getAuthority());
			}
			log.debug("LDAP ROLES: "+adRoles);
			
			try{
			user = new User();
			user.setUsername(username);
			user.setPassword(StringUtil.encodePassword(password, algorithm));
			user.setPasswordHint("passwordHint");
			if(givenNameAttribute!=null){
			  user.setFirstName(givenNameAttribute.get(0).toString());
			}
			else {
				user.setFirstName("somebody");	
			}
			if(snAttribute!=null){
			  user.setLastName(snAttribute.get(0).toString());
			}
			else {
				user.setLastName("somebody");	
			}
	
			user.setWebsite("http://www.matrixware.com");
			user.setEnabled(true);
			user.setAccountExpired(false);
			user.setAccountLocked(false);
			user.setCredentialsExpired(false);
			if(mailAttribute!=null){
			  user.setEmail(mailAttribute.get(0).toString());
			}
			else {
			  user.setEmail(username + "matrixware.com");	
			}
			
			if(phoneNumberAttribute!=null){
				  user.setPhoneNumber(phoneNumberAttribute.get(0).toString());
				}
				else {
				  user.setPhoneNumber("0000000000000");	
				}
			
			Address address = new Address();
			address.setAddress("Lehargasse 11/8");
			address.setCity("Wien");
			address.setCountry("AT");
			address.setPostalCode("1060");
			address.setProvince("W");
			user.setAddress(address);
			
			Set<String> matchingRoles = roleMatcher.getMatchingRoles(username, adRoles);
			
			Set<Role> roles = new HashSet<Role>();
			
			Iterator<String> it =  matchingRoles.iterator();
			
			while(it.hasNext()){
              Role role = roleDao.getRoleByName(it.next());
              log.debug("adding role: "+ role.getName());
			  user.addRole(role);
			  userDao.saveUser(user);
			}
			
/*
			Role managerRole = roleDao.getRoleByName(Constants.MANAGER_ROLE); 
			log.debug("assign role: "+managerRole.getId());
			
			user.addRole(managerRole);
			userDao.saveUser(user);
			
			Role annotatorRole = roleDao.getRoleByName(Constants.ANNOTATOR_ROLE); 
			log.debug("assign role: "+annotatorRole.getId());
			user.addRole(annotatorRole);
			userDao.saveUser(user);
*/
			
			log.debug("saved user in DB: "+user.getUsername());
			}
			catch(NamingException ne){
				e.printStackTrace();
			}
			
		}
		
		}
		if(user!=null){
		// reset password
		user.setPassword(password);
		}
		return user;
	}


	/** A user DAO */
	private UserDao userDao;
	
	/** A role DAO */
	private RoleDao roleDao;
	
	/** The password encryption algorithm */
	private String algorithm = "SHA";
	
	private RoleMatcher roleMatcher;
	
	
	/** The encoder used to encrypt the passwords in the store */ 
	private PasswordEncoder passwordEncoder;
	
	/**
	 * Setter for the userDao
	 * 
	 * @param userDao user-DAO
	 */
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}
	
	/**
	 * Sets the password-encoder used for encrypting passes in
	 * the datastore.
	 * 
	 * @param passwordEncoder the needed password encoder
	 */
	public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
        if (passwordEncoder instanceof Md5PasswordEncoder) {
            algorithm = "MD5";
        }
	}

	public void setRoleMatcher(RoleMatcher roleMatcher) {
		this.roleMatcher = roleMatcher;
	}

	
	/**
	 * Setter for the roleDao
	 * 
	 * @param roleDao role-DAO
	 */
	public void setRoleDao(RoleDao roleDao) {
		this.roleDao = roleDao;
	}

	
	
	
}
