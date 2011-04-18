package gleam.executive.dao.hibernate;

import java.util.List;

import gleam.executive.dao.ServiceDao;
import gleam.executive.model.Service;
import org.springframework.orm.ObjectRetrievalFailureException;

/**
 * This class interacts with Spring's HibernateTemplate to save/delete
 * and retrieve Service objects.
 *
 * <p>
 * <a href="ServiceDaoHibernate.java.html"><i>View Source</i></a>
 * </p>
 *
 * @author <a href="mailto:H.Sun@dcs.shef.ac.uk">Haotian Sun</a>
 */
public class ServiceDaoHibernate extends BaseDaoHibernate implements ServiceDao{
  /**
   * @see gleam.executive.dao.ServiceDao#getService(Long)
   */
  public Service getService(Long serviceId) {
    Service service = (Service)getHibernateTemplate().get(Service.class, serviceId);
    if(service == null) {
      log.warn("uh oh, service '" + serviceId + "' not found...");
      throw new ObjectRetrievalFailureException(Service.class, serviceId);
    }
    return service;
  }
  
  /**
   * @see gleam.executive.dao.ServiceDao#getServices()
   */
  public List getServices(Service service){
    return getHibernateTemplate()
      .find("from Service s order by upper(s.name)");
  }
  
  /**
   * @see gleam.executive.dao.ServiceDao#saveService(gleam.executive.model.Service)
   */
  public void saveService(final Service service) {
    if(log.isDebugEnabled()) {
      log.debug("service's id: " + service.getId());
    }
    
    getHibernateTemplate().merge(service);
    // necessary to throw a DataIntegrityViolation and catch it in
    // SecuredServiceManager
    getHibernateTemplate().flush();
  }
  
}
