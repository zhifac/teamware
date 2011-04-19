/*
 *  ServiceManagerTest.java
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

import gleam.executive.dao.ServiceDao;
import gleam.executive.model.Service;
import gleam.executive.service.impl.ServiceManagerImpl;
import org.jmock.Mock;

public class ServiceManagerTest extends BaseManagerTestCase {
  // ~ Instance fields
  // ========================================================
  private ServiceManagerImpl serviceManager = new ServiceManagerImpl();
  private Mock serviceDao = null;

  // ~ Methods
  // ================================================================
  protected void setUp() throws Exception {
    super.setUp();
    serviceDao = new Mock(ServiceDao.class);
    serviceManager.setServiceDao((ServiceDao)serviceDao.proxy());
  }
  
  public void testGetService() throws Exception {
    Service testData = new Service();
    testData.setId(new Long(8));
    testData.setName("test service");
    //  set expected behavior on dao
    serviceDao.expects(once()).method("getService").with(eq(new Long(8))).will(
            returnValue(testData));
    // set expected behavior on dao
    Service service = serviceManager.getService(testData.getId().toString());
    assertTrue( service!= null);
    serviceDao.verify();
    System.out.println("Finish testGetService()!");
  }
  
  public void testUpdateService() throws Exception {
    Service testData = new Service();
    testData.setId(new Long(100));
    testData.setName("test service");
    // set expected behavior on role dao
    serviceDao.expects(once()).method("getService").with(eq(new Long(100))).will(
            returnValue(testData));
    Service service = serviceManager.getService(testData.getId().toString());
    service.setName("another service");
    service.setEnabled(true);
    serviceDao.verify();
    // set expected behavior on service dao
    serviceDao.expects(once()).method("saveService").with(same(service));
    serviceManager.saveService(service);
    assertTrue(service.getName().equals("another service"));
    serviceDao.verify();
  }
  
}
