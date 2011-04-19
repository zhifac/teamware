/*
 *  NestedFormTest.java
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
package gleam.executive.webapp.form;

import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import gleam.executive.model.Address;
import gleam.executive.model.User;
import gleam.executive.util.ConvertUtil;

/**
 * @author mraible
 * 
 * Test to verify that BeanUtils.copyProperties is working for nested
 * POJOs and Forms.
 */
public class NestedFormTest extends TestCase {
  protected final Log log = LogFactory.getLog(getClass());

  private User user = null;

  private UserForm userForm = null;

  protected void setUp() throws Exception {
    super.setUp();
  }

  protected void tearDown() throws Exception {
    super.tearDown();
  }

  public void testCopyPOJOToFormAndBack() throws Exception {
    // pojo -> form
    user = new User();
    user.setUsername("testuser");
    Address address = new Address();
    address.setCity("Denver");
    user.setAddress(address);
    userForm = (UserForm)ConvertUtil.convert(user);
    assertEquals(userForm.getUsername(), "testuser");
    // log.debug(userForm);
    assertEquals(userForm.getAddressForm().getCity(), "Denver");
    // form -> pojo
    user = new User();
    user = (User)ConvertUtil.convert(userForm);
    assertEquals(user.getUsername(), "testuser");
    assertEquals(user.getAddress().getCity(), "Denver");
  }
}
