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
