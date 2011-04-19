/*
 *  ResourceManagerTest.java
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

import gleam.executive.dao.ResourceDao;
import gleam.executive.dao.RoleDao;
import gleam.executive.model.Resource;
import gleam.executive.service.impl.ResourceManagerImpl;
import gleam.executive.service.impl.RoleManagerImpl;
import org.jmock.Mock;

public class ResourceManagerTest extends BaseManagerTestCase {
  // ~ Instance fields
  // ========================================================

  private RoleManagerImpl roleManager = new RoleManagerImpl();
  private ResourceManagerImpl resourceManager = new ResourceManagerImpl();
  private Mock roleDao = null;
  private Mock resourceDao = null;

  // ~ Methods
  // ================================================================
  protected void setUp() throws Exception {
    super.setUp();
    roleDao = new Mock(RoleDao.class);
    resourceDao = new Mock(ResourceDao.class);
    roleManager.setRoleDao((RoleDao)roleDao.proxy());
    resourceManager.setResourceDao((ResourceDao)resourceDao.proxy());
  }
  
  public void testGetResource() throws Exception {
    Resource testData = new Resource();
    testData.setId(new Long(8));
    testData.setUrl("/corpora.html*");
    //  set expected behavior on dao
    resourceDao.expects(once()).method("getResource").with(eq(new Long(8))).will(
            returnValue(testData));
    // set expected behavior on dao
    Resource resource = resourceManager.getResource(testData.getId().toString());
    assertTrue( resource!= null);
    resourceDao.verify();
    System.out.println("Finish testGetResource()!");
  }
  
  
  public void testAddAndRemoveResource() throws Exception {
    Resource testData = new Resource();
    testData.setId(new Long(100));
    testData.setUrl("/corpora.html*");
    // set expected behavior on role dao
    resourceDao.expects(once()).method("getResource").with(eq(new Long(100))).will(
            returnValue(testData));
    Resource resource = resourceManager.getResource(testData.getId().toString());
    resource.setUrl("/test.html*");
    resource.setDescription("A Test Page");
    resourceDao.verify();
    // set expected behavior on role dao
    resourceDao.expects(once()).method("saveResource").with(same(resource));
    resourceManager.saveResource(resource);
    assertTrue(resource.getDescription().equals("A Test Page"));
    resourceDao.verify();
    // reset expectations
    resourceDao.reset();
    resourceDao.expects(once()).method("removeResource").with(eq(new Long(100)));
    resourceManager.removeResource("100");
    resourceDao.verify();
    System.out.println("Finish test Add and Remove Resource");
  }
  
}
