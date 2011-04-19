/*
 *  RoleDaoTest.java
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
