/*
 *  RoleManagerTest.java
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
