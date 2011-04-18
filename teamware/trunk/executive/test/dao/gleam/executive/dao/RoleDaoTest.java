package gleam.executive.dao;

import java.util.List;

import gleam.executive.Constants;
import gleam.executive.model.Role;

public class RoleDaoTest extends BaseDaoTestCase {
  private RoleDao dao;

  public void setRoleDao(RoleDao dao) {
    this.dao = dao;
  }

  public void testGetRoleInvalid() throws Exception {
    Role role = dao.getRoleByName("badrolename");
    assertNull(role);
  }

  public void testGetRole() throws Exception {
    Role role = dao.getRoleByName(Constants.ADMIN_ROLE);
    assertNotNull(role);
  }

  public void testUpdateRole() throws Exception {
    Role role = dao.getRoleByName(Constants.ADMIN_ROLE);
    log.debug(role);
    role.setDescription("test descr");
    dao.saveRole(role);
    assertEquals(role.getDescription(), "test descr");
  }

  public void testAddAndRemoveRole() throws Exception {
    System.out.println("----------------Testing add and remove ROLE----------------");
    Role role = new Role("testrole");
    role.setDescription("new role descr");
    dao.saveRole(role);
    setComplete(); // change behavior from rollback to commit
    endTransaction();
    startNewTransaction();
    role = dao.getRoleByName("testrole");
    assertNotNull(role.getDescription());
    dao.removeRole("testrole");
    setComplete();
    endTransaction(); // deletes role from database
    role = dao.getRoleByName("testrole");
    assertNull(role);
  }
  
  public void testGetRolesWithResource() throws Exception{
    List roles=dao.getRolesWithResource("/clickstreams.jsp*");
    for(int i=0;i<roles.size();i++){
      Role role=(Role)roles.get(i);
    }
    assertEquals(2,roles.size());
  }
}
