package gleam.executive.service.jforum;


import gleam.executive.model.User;
import gleam.executive.service.UserManager;

import net.jforum.context.RequestContext;
import net.jforum.context.SessionContext;
import net.jforum.entities.UserSession;
import net.jforum.sso.SSO;
import net.jforum.util.preferences.ConfigKeys;
import net.jforum.util.preferences.SystemGlobals;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;




/**
 * Simple SSO authenticator. This class will try to validate an user by simple checking
 * <code>request.getRemoteUser()</code> is not null.
 *
 * @author agaton
 */
public class JForumSSO implements SSO, ApplicationContextAware
{
	protected final Log log = LogFactory.getLog(getClass());
	
	protected static UserManager userManager;
	/**
	 * @see net.jforum.sso.SSO#authenticateUser(net.jforum.context.RequestContext)
     * @param request AWebContextRequest     * @return String
	 */
	public String authenticateUser(RequestContext request)
	{
		String password = null;
		String email = null;
		String username = request.getRemoteUser();
		log.debug("££££££ authenticateUser:username " + username);
      
		if (username != null) {
			UserManager userManager = (UserManager)applicationContext.getBean("userManager");
			User user = userManager.getUserByUsername(username);
			password = user.getPassword();
			email = user.getEmail();
		}
		SessionContext session = request.getSessionContext();
		session.setAttribute("password", password);
		session.setAttribute("email", email);
	
		return username;
	}

	public boolean isSessionValid(UserSession userSession, RequestContext request)
	{

		String remoteUser = request.getRemoteUser();
		log.debug("££££££££ isSessionValid:REMOTE USER: "+remoteUser);

		// user has since logged out
		if (remoteUser == null && userSession.getUserId() != SystemGlobals.getIntValue(ConfigKeys.ANONYMOUS_USER_ID)) {
			return false;
		}
		// user has since logged in
		else if (remoteUser != null
				&& userSession.getUserId() == SystemGlobals.getIntValue(ConfigKeys.ANONYMOUS_USER_ID)) {
			return false;
		}
		// user has changed user
		else if (remoteUser != null && !remoteUser.equals(userSession.getUsername())) {
			return false;
		}

		return true;
	}
	
	public JForumSSO(){
		
	}
	
	public void setApplicationContext(ApplicationContext context){
		log.debug("@@@@@@ JForumSSO:::setApplicationContext");
		applicationContext = context;
	}
	
	private static ApplicationContext applicationContext = null;

	
}
