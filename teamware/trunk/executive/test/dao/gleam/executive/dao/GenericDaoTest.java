/*
 *  GenericDaoTest.java
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

import gleam.executive.model.User;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.orm.ObjectRetrievalFailureException;

/**
 * This class tests the generic Dao and BaseDao implementation.
 */
public class GenericDaoTest extends BaseDaoTestCase {
  protected Dao dao;

  /**
   * This method is used instead of setDao b/c setDao uses autowire
   * byType <code>setPopulateProtectedVariables(true)</code> can also
   * be used, but it's a little bit slower.
   */
  public void onSetUpBeforeTransaction() throws Exception {
    dao = (Dao)applicationContext.getBean("dao");
  }

  public void onTearDownAfterTransaction() throws Exception {
    dao = null;
  }

  /**
   * Simple test to verify BaseDao works.
   */
  public void testCRUD() {
    User user = new User();
    // set required fields
    user.setUsername("foo");
    user.setPassword("bar");
    user.setFirstName("first");
    user.setLastName("last");
    user.getAddress().setCity("Denver");
    user.getAddress().setPostalCode("80465");
    user.setEmail("foo@bar.com");
    // create
    dao.saveObject(user);
    assertNotNull(user.getId());
    // retrieve
    user = (User)dao.getObject(User.class, user.getId());
    assertNotNull(user);
    assertEquals(user.getLastName(), "last");
    // update
    user.getAddress().setCountry("USA");
    dao.saveObject(user);
    assertEquals(user.getAddress().getCountry(), "USA");
    // delete
    dao.removeObject(User.class, user.getId());
    try {
      dao.getObject(User.class, user.getId());
      fail("User 'foo' found in database");
    }
    catch(ObjectRetrievalFailureException e) {
      assertNotNull(e.getMessage());
    }
    catch(InvalidDataAccessApiUsageException e) { // Spring 2.0 throws
                                                  // this
      // one
      assertNotNull(e.getMessage());
    }
  }
}
