/*
 *  xdoclet-ResourceForm.java
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
public String[] getResourceRoles() {
        gleam.executive.model.Role role;
        String[] resourceRoles = new String[roles.size()];
        int i = 0;
        for (java.util.Iterator iter = roles.iterator(); iter.hasNext();) {
          role = (gleam.executive.model.Role) iter.next();
          resourceRoles[i] = role.getName();
          i++;
      }
      return resourceRoles;
}

    /**
     * Note that this is not used - it's just needed by Struts.  If you look
     * in UserAction - you'll see that request.getParameterValues("resourceRoles")
     * is used instead.
     * 
     * @param roles
     */
    public void setResourceRoles(String[] roles) {}
    
