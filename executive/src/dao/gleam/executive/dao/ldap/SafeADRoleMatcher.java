/*
 *  SafeADRoleMatcher.java
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
