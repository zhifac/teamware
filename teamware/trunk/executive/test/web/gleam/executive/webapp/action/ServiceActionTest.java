package gleam.executive.webapp.action;

import java.util.MissingResourceException;
import java.util.ResourceBundle;
import gleam.executive.Constants;

public class ServiceActionTest extends BaseStrutsTestCase {
  public ServiceActionTest(String name) {
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
      log.warn("No service bundle found for: " + className);
    }
  }

  public void testSearch() throws Exception {
    setRequestPathInfo("/services");
    addRequestParameter("method", "Search");
    actionPerform();
    verifyForward("list");
    assertTrue(getRequest().getAttribute(Constants.SERVICE_LIST) != null);
    verifyNoActionErrors();
  }
  
  /*No way to test the save action here as it is not a normal one, 
   * but it is tested in jsp-test indirectly.
  public void testSave() throws Exception { 
    setRequestPathInfo("/services");
    addRequestParameter("method", "Search");
    setRequestPathInfo("/saveService"); 
    addRequestParameter("method", "Save");
    actionPerform();
  }*/
  
}
