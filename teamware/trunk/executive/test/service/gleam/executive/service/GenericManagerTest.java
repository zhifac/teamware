/*
 *  GenericManagerTest.java
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

import gleam.executive.dao.Dao;
import gleam.executive.model.User;
import gleam.executive.service.impl.BaseManager;
import org.jmock.Mock;
import org.springframework.orm.ObjectRetrievalFailureException;

/**
 * This class tests the generic Manager and BaseManager implementation.
 */
public class GenericManagerTest extends BaseManagerTestCase {
  protected Manager manager = new BaseManager();

  protected Mock dao;

  protected void setUp() throws Exception {
    super.setUp();
    dao = new Mock(Dao.class);
    manager.setDao((Dao)dao.proxy());
  }

  protected void tearDown() throws Exception {
    manager = null;
    dao = null;
  }

  /**
   * Simple test to verify BaseDao works.
   */
  public void testCRUD() {
    User user = new User();
    // set required fields
    user.setUsername("foo");
    // create
    // set expectations
    dao.expects(once()).method("saveObject").isVoid();
    manager.saveObject(user);
    dao.verify();
    // retrieve
    dao.reset();
    // expectations
    dao.expects(once()).method("getObject").will(returnValue(user));
    user = (User)manager.getObject(User.class, user.getUsername());
    dao.verify();
    // update
    dao.reset();
    dao.expects(once()).method("saveObject").isVoid();
    user.getAddress().setCountry("USA");
    manager.saveObject(user);
    dao.verify();
    // delete
    dao.reset();
    // expectations
    Exception ex = new ObjectRetrievalFailureException(User.class, "foo");
    dao.expects(once()).method("removeObject").isVoid();
    dao.expects(once()).method("getObject").will(throwException(ex));
    manager.removeObject(User.class, "foo");
    try {
      manager.getObject(User.class, "foo");
      fail("User 'foo' found in database");
    }
    catch(ObjectRetrievalFailureException e) {
      assertNotNull(e.getMessage());
    }
    dao.verify();
  }
}
