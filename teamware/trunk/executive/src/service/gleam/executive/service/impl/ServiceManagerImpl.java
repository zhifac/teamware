package gleam.executive.service.impl;

import java.util.List;

import gleam.executive.dao.ServiceDao;
import gleam.executive.model.Service;
import gleam.executive.service.ServiceManager;

/**
 * Implementation of ServiceManager interface.
 * </p>
 *
 * <p>
 * <a href="ServiceManagerImpl.java.html"><i>View Source</i></a>
 * </p>
 *
 * @author <a href="M.Agatonovic@dcs.shef.ac.uk">Milan Agatonovic</a>
 * @author <a href="mailto:H.Sun@dcs.shef.ac.uk">Haotian Sun</a>
 */
public class ServiceManagerImpl extends BaseManager implements ServiceManager {
  private ServiceDao dao;

  public void setServiceDao(ServiceDao dao) {
    this.dao = dao;
  }

  public List getServices(Service service) {
    return dao.getServices(service);
  }

  public Service getService(String serviceId) {
    return dao.getService(new Long(serviceId));
  }

  public void saveService(Service service) {
    dao.saveService(service);
  }

}
