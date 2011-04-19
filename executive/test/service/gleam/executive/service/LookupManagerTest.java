/*
 *  LookupManagerTest.java
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
