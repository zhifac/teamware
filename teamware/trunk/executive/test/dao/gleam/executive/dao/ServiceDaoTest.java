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
