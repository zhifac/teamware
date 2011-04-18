package gleam.executive.service;

import gleam.executive.dao.RoleDao;
import gleam.executive.model.Role;
import gleam.executive.service.impl.RoleManagerImpl;
import org.jmock.Mock;

public class RoleManagerTest extends BaseManagerTestCase {
  // ~ Instance fields
  // ========================================================

  private RoleManagerImpl roleManager = new RoleManagerImpl();

  private Mock roleDao = null;

  // ~ Methods
  // ================================================================
  protected void setUp() throws Exception {
    super.setUp();
    roleDao = new Mock(RoleDao.class);
    roleManager.setRoleDao((RoleDao)roleDao.proxy());
  }

  public void testGetRole() throws Exception {
    Role testData = new Role("user");
    //  set expected behavior on dao
    roleDao.expects(once()).method("getRoleByName").with(eq(new String("user"))).will(
            returnValue(testData));
    // set expected behavior on dao
    Role role = roleManager.getRole(testData.getName());
    assertTrue( role!= null);
    roleDao.verify();
  }
 
  public void testSaveRole() throws Exception {
    Role testData = new Role("manager");
    // set expected behavior on dao
    roleDao.expects(once()).method("getRoleByName").with(eq(new String("manager"))).will(
            returnValue(testData));
    Role role = roleManager.getRole(testData.getName());
    role.setDescription("A Manager Role");
    roleDao.verify();
    // reset expectations
    roleDao.reset();
    roleDao.expects(once()).method("saveRole").with(same(role));
    roleManager.saveRole(role);
    assertTrue(role.getDescription().equals("A Manager Role"));
    roleDao.verify();
  }
  
  public void testAddAndRemoveRole() throws Exception {
    Role testData = new Role("TeamLeader");
    // set expected behavior on role dao
    roleDao.expects(once()).method("getRoleByName").with(eq("TeamLeader")).will(
            returnValue(testData));
    Role role = roleManager.getRoleByRolename(testData.getName());
    role.setDescription("A Team Leader Role");
    roleDao.verify();
    // set expected behavior on role dao
    roleDao.expects(once()).method("saveRole").with(same(role));
    roleManager.saveRole(role);
    assertTrue(role.getDescription().equals("A Team Leader Role"));
    roleDao.verify();
    // reset expectations
    roleDao.reset();
    roleDao.expects(once()).method("removeRole").with(eq(new String("TeamLeader")));
    roleManager.removeRole("TeamLeader");
    roleDao.verify();
    // reset expectations
    roleDao.reset();
    roleDao.expects(once()).method("getRoleByName").will(returnValue(null));
    role = roleManager.getRole("TeamLeader");
    assertNull(role);
    roleDao.verify();
  }
}
