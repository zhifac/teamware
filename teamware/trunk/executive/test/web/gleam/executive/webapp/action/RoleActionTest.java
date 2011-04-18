package gleam.executive.webapp.action;

import java.util.MissingResourceException;
import java.util.ResourceBundle;
import gleam.executive.Constants;
import gleam.executive.webapp.form.RoleForm;

public class RoleActionTest extends BaseStrutsTestCase {
  public RoleActionTest(String name) {
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
    setRequestPathInfo("/editRole");
    addRequestParameter("method", "Cancel");
    actionPerform();
    verifyForward("viewRoles");
    verifyNoActionErrors();
  }

  public void testEdit() throws Exception {
    setRequestPathInfo("/editRole");
    addRequestParameter("method", "Edit");
    addRequestParameter("from", "list");
    addRequestParameter("name", "admin");
    actionPerform();
    verifyForward("edit");
    assertTrue(getRequest().getAttribute(Constants.ROLE_KEY) != null);
    verifyNoActionErrors();
  }

  public void testSearch() throws Exception {
    setRequestPathInfo("/roles");
    addRequestParameter("method", "Search");
    actionPerform();
    verifyForward("list");
    assertTrue(getRequest().getAttribute(Constants.ROLE_LIST) != null);
    verifyNoActionErrors();
  }
  /*
  public void testSave() throws Exception { 
    System.out.println("**********Testing saveRole**************");
    addRequestParameter("oldRoleName","manager");
    RoleForm roleForm = new RoleForm(); 
    roleForm.setName("role1");
    roleForm.setDescription("A test role");
    getRequest().setAttribute(Constants.ROLE_KEY, roleForm);
    setRequestPathInfo("/saveRole"); 
    addRequestParameter("method", "save");
    actionPerform();
    //verifyForward("viewRoles");
    assertTrue(getRequest().getAttribute(Constants.ROLE_KEY) != null);
    verifyNoActionErrors(); 
  }
  */
  public void testRemove() throws Exception {
    System.out.println("************Test remove a role**************");
    setRequestPathInfo("/roles");
    addRequestParameter("method", "delete");
    addRequestParameter("name","role1");
    actionPerform();
    verifyForward("viewRoles");
    verifyNoActionErrors();
  }

  public void testRemoveSuperadmin() throws Exception {
    System.out.println("************Superadmin role delete should fail**************");
    setRequestPathInfo("/roles");
    addRequestParameter("method", "delete");
    addRequestParameter("name","superadmin");
    actionPerform();
    verifyForward("viewRoles");
    verifyActionMessages(new String[] {"errors.role.delete"});
  }
}
