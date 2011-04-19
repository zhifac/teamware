/*
 *  ServiceDaoTest.java
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
import gleam.executive.model.Service;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;

public class ServiceDaoTest extends BaseDaoTestCase {
  private ServiceDao dao = null;

  public void setServiceDao(ServiceDao dao) {
    this.dao = dao;
  }

  public void testGetServiceInvalid() throws Exception {
    try {
      dao.getService(new Long(1000));
      fail("'badServicename' found in database, failing test...");
    }
    catch(DataAccessException d) {
      assertTrue(d != null);
    }
  }

  public void testGetService() throws Exception {
    Service service = dao.getService(new Long(1));
    assertNotNull(service);
    assertEquals("Admin Service", service.getName());
  }

  public void testGetServices() throws Exception{
    Service service = new Service();
    List<Service> services =(List<Service>) dao.getServices(service);
    for(int i=0;i<services.size();i++){
      Service se = services.get(i);
      System.out.println(se.getName());
    }
  }
  
  public void testUpdateService() throws Exception {
    Service service = dao.getService(new Long(1));
    dao.saveService(service);
    assertEquals(service.getName(), "Admin Service");
    // verify that violation occurs when adding new Service with same
    // Servicename
    service.setId(null);
    service.setEnabled(false);
    endTransaction();
    try {
      dao.saveService(service);
      fail("saveService didn't throw DataIntegrityViolationException");
    }
    catch(DataIntegrityViolationException e) {
      assertNotNull(e);
      log.debug("expected exception: " + e.getMessage());
    }
  }
  
}
