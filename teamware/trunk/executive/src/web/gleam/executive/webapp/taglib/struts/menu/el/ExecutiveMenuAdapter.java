/*
 *  ExecutiveMenuAdapter.java
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
package gleam.executive.webapp.taglib.struts.menu.el;

import gleam.executive.model.Role;
import gleam.executive.service.RoleManager;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.navigator.menu.MenuComponent;
import net.sf.navigator.menu.PermissionsAdapter;

public class ExecutiveMenuAdapter implements PermissionsAdapter {
  //~ Instance fields ========================================================
	protected final Log log = LogFactory.getLog(getClass());
  //private List<String> menuNames;
  private RoleManager roleManager;
  private String webappName;
  
  HttpServletRequest request;
  
  //~ Constructors ===========================================================

  /**
   * Creates a new instance of MenuAdapter
   */
  public ExecutiveMenuAdapter(HttpServletRequest request,RoleManager roleManager,String webappName) {
    this.roleManager=roleManager;
    this.request=request;
    this.webappName=webappName;
    //log.debug("webappName: " + webappName);
  }
  
  //~ Methods ================================================================

  /**
   * @return Returns the webappName.
   */
  public String getWebappName() {
    return webappName;
  }


  /**
   * @param webappName The webappName to set.
   */
  public void setWebappName(String webappName) {
    this.webappName = webappName;
  }


  /**
   * If the menu is allowed, this should return true.
   *
   * @return whether or not the menu is allowed.
   */
  public boolean isAllowed(MenuComponent menu) {
      if(menu.getUrl()==null){
        return true;
      }else{
        String url=menu.getUrl();
        //log.debug("menu url: "+ url);
        //log.debug("isAllowed?: "+url);
        url=url.substring(webappName.length()+1)+"*";
        //log.debug("normalised url: "+ url);
        List allowedRoles = roleManager.getRolesWithResource(url);
        for(int i=0;i<allowedRoles.size();i++){
          Role allowedRole = (Role)allowedRoles.get(i);
          if(request.isUserInRole(allowedRole.getName())){
        	//log.debug("user is in role: "+ allowedRole.getName()); 
            return true;
          }
        }
      }
      return false;
      //return menuNames.contains(menu.getName());
  }
  
}
