package gleam.executive.webapp.action;

import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import gleam.executive.Constants;

public class UserActionTest extends BaseStrutsTestCase {
  public UserActionTest(String name) {
    super(name);
  }

  protected void setUp() throws Exception {
    super.setUp();
    getMockRequest().setUserRole("admin");
    String className = this.getClass().getName();
    try {
      rb = ResourceBundle.getBundle(className);
    }
    catch(MissingResourceException mre) {
      log.warn("No resource bundle found for: " + className);
    }
  }

  public void testCancel() throws Exception {
    setRequestPathInfo("/editUser");
    addRequestParameter("method", "Cancel");
    actionPerform();
    verifyForward("mainMenu");
    verifyNoActionErrors();
  }

  public void testEdit() throws Exception {
    // set requestURI so getRequestURI() doesn't fail in UserAction
    getMockRequest().setRequestURI("/editUser.html");
    setRequestPathInfo("/editUser");
    addRequestParameter("method", "Edit");
    addRequestParameter("username", "tomcat");
    actionPerform();
    verifyForward("edit");
    assertTrue(getRequest().getAttribute(Constants.USER_KEY) != null);
    verifyNoActionErrors();
  }

  public void testSuperAdminNotEditable() throws Exception {
    // set requestURI so getRequestURI() doesn't fail in UserAction
    getMockRequest().setRequestURI("/editUser.html");
    setRequestPathInfo("/editUser");
    addRequestParameter("method", "Edit");
    addRequestParameter("username", "superadmin");
    // testing that superadmin user is not editable by non-superadmin
    getMockRequest().setRemoteUser("tomcat");
    // capture the error response code
    final int[] responseCodeHolder = new int[1];
    setResponseWrapper(new HttpServletResponseWrapper(getMockResponse()) {
      public void sendError(int sc, String message) {
        sendError(sc);
      }
      public void sendError(int sc) {
        responseCodeHolder[0] = sc;
      }
    });
    actionPerform();
    assertEquals(
        "Non-superadmin attempt to edit superadmin returned wrong status code",
        HttpServletResponse.SC_FORBIDDEN, responseCodeHolder[0]);
  }

  /*
   * public void testSave() throws Exception { UserForm userForm = new
   * UserForm(); BeanUtils.copyProperties(userForm, user);
   * userForm.setPassword("tomcat");
   * userForm.setConfirmPassword(userForm.getPassword());
   * getRequest().setAttribute(Constants.USER_KEY, userForm);
   * 
   * setRequestPathInfo("/saveUser"); addRequestParameter("encryptPass",
   * "true"); addRequestParameter("method", "Save");
   * addRequestParameter("from", "list"); actionPerform();
   * 
   * verifyForward("edit");
   * assertTrue(getRequest().getAttribute(Constants.USER_KEY) != null);
   * verifyNoActionErrors(); }
   */
  public void testSearch() throws Exception {
    setRequestPathInfo("/users");
    addRequestParameter("method", "Search");
    actionPerform();
    verifyForward("list");
    assertTrue(getRequest().getAttribute(Constants.USER_LIST) != null);
    verifyNoActionErrors();
  }
  /*
   * Problematic method public void testRemove() throws Exception {
   * setRequestPathInfo("/editUser"); addRequestParameter("method",
   * "Delete"); addRequestParameter("id", "2"); actionPerform();
   * verifyForward("viewUsers"); verifyNoActionErrors(); }
   */
}
