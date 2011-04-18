package gleam.executive.dao.ldap;

import gleam.executive.Constants;
import gleam.executive.dao.RoleDao;
import gleam.executive.model.Role;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SafeADRoleMatcher implements RoleMatcher{

	private static final Log log = LogFactory.getLog(SafeADRoleMatcher.class);

	private String usernameMatcher;
	
	private String roleMatcher;
	

	public void setRoleMatcher(String roleMatcher) {
		this.roleMatcher = roleMatcher;
	}


	public void setUsernameMatcher(String usernameMatcher) {
		this.usernameMatcher = usernameMatcher;
	}
	
	public Set<String> getMatchingRoles(String username, Set<String> adRoles){
		Set<String> roles = new HashSet<String>();
		// iterate through AD roles and try to find roleMatcher string
		Iterator<String> it = adRoles.iterator();
		boolean matched=false;
		while(it.hasNext()){
			String roleString = it.next();
			log.debug("Checking role string: "+roleString);
			if(roleString.indexOf(roleMatcher)!=-1){
				matched = true;
				log.debug("Found match: "+roleString);
				roles.add(Constants.MANAGER_ROLE);
				roles.add(Constants.ANNOTATOR_ROLE);
				//roles.add(Constants.CURATOR_ROLE);
				break;
			}
		}
		if(!matched){
			log.debug("Match not found!");
		}
		
		return roles;
	}

	
	
	
	
	
}
