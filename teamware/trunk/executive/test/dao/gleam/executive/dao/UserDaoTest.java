/*
 *  UserDaoTest.java
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

import gleam.executive.Constants;
import gleam.executive.model.Address;
import gleam.executive.model.Role;
import gleam.executive.model.User;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;

public class UserDaoTest extends BaseDaoTestCase {
  private UserDao dao = null;

  private RoleDao rdao = null;

  public void setUserDao(UserDao dao) {
    this.dao = dao;
  }

  public void setRoleDao(RoleDao rdao) {
    this.rdao = rdao;
  }

  public void testGetUserInvalid() throws Exception {
    try {
      dao.getUser(new Long(1000));
      fail("'badusername' found in database, failing test...");
    }
    catch(DataAccessException d) {
      assertTrue(d != null);
    }
  }

  public void testGetUser() throws Exception {
    User user = dao.getUser(new Long(1));
    assertNotNull(user);
    assertEquals(1, user.getRoles().size());
    assertTrue(user.isEnabled());
  }
  
  public void testEnableUser() throws Exception {
    dao.enableUser(new Long(1), false);
    User user = dao.getUser(new Long(1));
    assertEquals(user.isEnabled(),false);
    dao.enableUser(new Long(1), true);
  }

  public void testUpdateUser() throws Exception {
    User user = dao.getUser(new Long(1));
    Address address = user.getAddress();
    address.setAddress("new address");
    dao.saveUser(user);
    assertEquals(user.getAddress(), address);
    assertEquals("new address", user.getAddress().getAddress());
    // verify that violation occurs when adding new user with same
    // username
    user.setId(null);
    endTransaction();
    try {
      dao.saveUser(user);
      fail("saveUser didn't throw DataIntegrityViolationException");
    }
    catch(DataIntegrityViolationException e) {
      assertNotNull(e);
      log.debug("expected exception: " + e.getMessage());
    }
  }

  public void testAddUserRole() throws Exception {
    User user = dao.getUser(new Long(1));
    assertEquals(1, user.getRoles().size());
    Role role = rdao.getRoleByName(Constants.SUPERADMIN_ROLE);
    user.addRole(role);
    dao.saveUser(user);
    assertEquals(1, user.getRoles().size());
    // add the same role twice - should result in no additional role
    user.addRole(role);
    dao.saveUser(user);
    assertEquals("more than 2 roles", 1, user.getRoles().size());
    user.getRoles().remove(role);
    dao.saveUser(user);
    assertEquals(0, user.getRoles().size());
  }
  
  
  public void testAddMultipleRoles() throws Exception {
	    User user = dao.getUser(new Long(1));
	    assertEquals(1, user.getRoles().size());
	    Role adminRole = rdao.getRoleByName(Constants.SUPERADMIN_ROLE);
	    user.addRole(adminRole);
	    dao.saveUser(user);
	    assertEquals(1, user.getRoles().size());
	    // add the same role twice - should result in no additional role
	    Role managerRole = rdao.getRoleByName(Constants.MANAGER_ROLE);
	    user.addRole(managerRole);
	    dao.saveUser(user);
	    assertEquals("more than 2 roles", 2, user.getRoles().size());
	    user.getRoles().remove(managerRole);
	    dao.saveUser(user);
	    assertEquals(1, user.getRoles().size());
	    user.getRoles().remove(adminRole);
	    dao.saveUser(user);
	    assertEquals(0, user.getRoles().size());
	  }

  public void testAddAndRemoveUser() throws Exception {
    User user = new User("testuser");
    user.setPassword("testpass");
    user.setFirstName("Test");
    user.setLastName("Last");
    Address address = new Address();
    address.setCity("Denver");
    address.setProvince("CO");
    address.setCountry("USA");
    address.setPostalCode("80210");
    user.setAddress(address);
    user.setEmail("testuser@appfuse.org");
    user.setWebsite("http://raibledesigns.com");
    Role role = rdao.getRoleByName(Constants.ADMIN_ROLE);
    assertNotNull(role.getId());
    user.addRole(role);
    dao.saveUser(user);
    assertNotNull(user.getId());
    assertEquals("testpass", user.getPassword());
    dao.removeUser(user.getId());
    try {
      user = dao.getUser(user.getId());
      fail("getUser didn't throw DataAccessException");
    }
    catch(DataAccessException d) {
      assertNotNull(d);
    }
  }
}
