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
