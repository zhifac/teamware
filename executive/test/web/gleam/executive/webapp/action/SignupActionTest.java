package gleam.executive.webapp.action;

import gleam.executive.Constants;
import gleam.executive.model.User;
import gleam.executive.service.UserManager;
import com.dumbster.smtp.SimpleSmtpServer;

public class SignupActionTest extends BaseStrutsTestCase {
  public SignupActionTest(String name) {
    super(name);
  }

  public void testExecute() throws Exception {
    /*
    setRequestPathInfo("/signup");
    //SimpleSmtpServer server = SimpleSmtpServer.start(2525);
    addRequestParameter("username", "self-registered");
    addRequestParameter("password", "Password1");
    addRequestParameter("confirmPassword", "Password1");
    addRequestParameter("firstName", "First");
    addRequestParameter("lastName", "Last");
    addRequestParameter("addressForm.city", "Denver");
    addRequestParameter("addressForm.province", "Colorado");
    addRequestParameter("addressForm.country", "USA");
    addRequestParameter("addressForm.postalCode", "80210");
    addRequestParameter("email", "self-registered@dcs.shef.ac.uk");
    addRequestParameter("website", "http://raibledesigns.com");
    addRequestParameter("passwordHint", "Password is one with you.");
    actionPerform();
    // verify an account information e-mail was sent
    //server.stop();
    // assertTrue(server.getReceivedEmailSize() == 1);
    //verifyForward("mainMenu");
    //verifyNoActionErrors();
    // verify that success messages are in the request
    //assertTrue(getSession().getAttribute(Constants.REGISTERED) != null);
    UserManager userMgr = (UserManager)ctx.getBean("userManager");
    User user = userMgr.getUserByUsername("self-registered");
    if(user!=null)
    userMgr.removeUser(user.getId().toString());
    */
  }
}
