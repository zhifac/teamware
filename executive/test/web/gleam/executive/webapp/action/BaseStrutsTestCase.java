package gleam.executive.webapp.action;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import gleam.executive.Constants;
import gleam.executive.model.Address;
import gleam.executive.model.Role;
import gleam.executive.model.User;
import gleam.executive.service.RoleManager;
import gleam.executive.service.UserManager;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mock.web.MockServletContext;
import org.springframework.util.ClassUtils;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import servletunit.struts.MockStrutsTestCase;

/**
 * This class is extended by all ActionTests. It basically contains
 * common methods that they might use.
 * 
 * <p>
 * <a href="BaseStrutsTestCase.java.html"><i>View Source</i></a>
 * 
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 */
public abstract class BaseStrutsTestCase extends MockStrutsTestCase {
  // ~ Instance fields
  // ========================================================
  protected final Log log = LogFactory.getLog(getClass());

  protected User user = null;
  
  protected UserManager userMgr = null;

  protected ResourceBundle rb = null;

  protected static WebApplicationContext ctx = null;
  
  protected boolean testUserExisted = false;

  // ~ Constructors
  // ===========================================================
  public BaseStrutsTestCase(String name) {
    super(name);
    // Since a ResourceBundle is not required for each class, just
    // do a simple check to see if one exists
    String className = this.getClass().getName();
    try {
      rb = ResourceBundle.getBundle(className);
    }
    catch(MissingResourceException mre) {
      // log.warn("No resource bundle found for: " + className);
    }
  }

  // ~ Methods
  // ================================================================
  protected void setUp() throws Exception {
    super.setUp();
    log.debug("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
    log.debug("@@@@@@@@@ BaseStrutsTestCase:setUp() START @@@@@@@@@");
    // initialize Spring
    if(ctx==null){
    String pkg = ClassUtils.classPackageAsResourcePath(Constants.class);
    log.debug("pkg: "+pkg);
    MockServletContext sc = new MockServletContext("");
    sc.addInitParameter(ContextLoader.CONFIG_LOCATION_PARAM, "classpath*:/"
            + pkg + "/dao/testApplicationContext-*.xml," 
            + pkg + "/service/applicationContext-*.xml,"
            + pkg + "/webapp/testApplicationContext-*.xml");
    ServletContextListener contextListener = new ContextLoaderListener();
    ServletContextEvent event = new ServletContextEvent(sc);
    contextListener.contextInitialized(event);
    // magic bridge to make StrutsTestCase aware of Spring's Context
    getSession()
            .getServletContext()
            .setAttribute(
                    WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE,
                    sc
                            .getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE));
    ctx = WebApplicationContextUtils
            .getRequiredWebApplicationContext(getSession().getServletContext());
  
    // populate the userForm and place into session
    userMgr = (UserManager)ctx.getBean("userManager");
    RoleManager roleMgr = (RoleManager)ctx.getBean("roleManager");
    try{
    user = userMgr.getUserByUsername("tomcat");
    }
    catch(Exception e){
    	log.warn("Test User does not exist, so we will create one.");
    }
    if(user==null){
    	// create 'tomcat' user
    	user = new User();
    	user.setAccountExpired(false);
    	user.setAccountLocked(false);
    	Address address = new Address();
    	address.setCity("Belgrade");
    	address.setCountry("Serbia");
    	address.setPostalCode("11000");
    	address.setProvince("Belgrade");
    	user.setAddress(address);
    	user.setConfirmPassword("tomcat");
    	user.setCredentialsExpired(false);
    	user.setEmail("agaton@dcs.shef.ac.uk");
    	user.setEnabled(true);
    	user.setFirstName("Tom");
    	user.setLastName("Cat");
    	user.setPassword("tomcat");
    	user.setPasswordHint("Where is my cat?");
    	user.setPhoneNumber("99979739343");
    	user.setUsername("tomcat");
    	user.setWebsite("http://gate.ac.uk");
    	Role role = roleMgr.getRoleByRolename("admin");
    	user.addRole(role);
    	userMgr.saveUser(user);
    	log.debug("Created Test User: "+user.getUsername());
    }
    else {
    	testUserExisted = true;
    }
    // change the port on the mailSender so it doesn't conflict with an
    // existing SMTP server on localhost
    JavaMailSenderImpl mailSender = (JavaMailSenderImpl)ctx
            .getBean("mailSender");
    mailSender.setPort(2525);
    mailSender.setHost("localhost");
    }
    else {
    	log.debug("SKIP SPRING INITIALIZATION");
    }
    log.debug("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
    log.debug("@@@@@@@@@ BaseStrutsTestCase:setUp() END   @@@@@@@@@");
  }

  public void tearDown() throws Exception {
    super.tearDown();
    log.debug("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
    log.debug("@@@@@@@@@ BaseStrutsTestCase:tearDown() START @@@@@@@@@");
    if(!testUserExisted){
       userMgr.removeUser(String.valueOf(user.getId()));
       log.debug("Removed Test User: "+user.getUsername());
    }
    ctx = null;
    log.debug("@@@@@@@@@ BaseStrutsTestCase:tearDown() END   @@@@@@@@@");
    log.debug("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
  }
}
