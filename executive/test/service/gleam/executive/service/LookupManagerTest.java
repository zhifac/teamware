package gleam.executive.service;

import java.util.ArrayList;
import java.util.List;
import gleam.executive.dao.LookupDao;
import gleam.executive.model.Role;
import gleam.executive.service.impl.LookupManagerImpl;
import org.jmock.Mock;

public class LookupManagerTest extends BaseManagerTestCase {
  private LookupManager mgr = new LookupManagerImpl();

  private Mock lookupDao = null;

  protected void setUp() throws Exception {
    super.setUp();
    lookupDao = new Mock(LookupDao.class);
    mgr.setLookupDao((LookupDao)lookupDao.proxy());
  }

  public void testGetAllRoles() {
    if(log.isDebugEnabled()) {
      log.debug("entered 'testGetAllRoles' method");
    }
    // set expected behavior on dao
    Role role = new Role("admin");
    List<Role> testData = new ArrayList<Role>();
    testData.add(role);
    lookupDao.expects(once()).method("getAllRoles").withNoArguments().will(
            returnValue(testData));
    List roles = mgr.getAllRoles();
    assertTrue(roles.size() > 0);
    // verify expectations
    lookupDao.verify();
  }
}
