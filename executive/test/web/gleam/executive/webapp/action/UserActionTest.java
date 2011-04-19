/*
 *  UserActionTest.java
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
