package gleam.executive.dao;

import java.util.List;
import gleam.executive.model.Service;

/**
 * Resource Data Access Object (Dao) interface.
 *
 * <p>
 * <a href="ServiceDao.java.html"><i>View Source</i></a>
 * </p>
 *
 * @author <a href="mailto:H.Sun@dcs.shef.ac.uk">Haotian Sun</a>
 */
public interface ServiceDao extends Dao {
  /**
   * Gets service information based on service id.
   *
   * @param serviceId the resource's id
   * @return Service populated Service object
   */
  public Service getService(Long serviceId);

  /**
   * 
   * @return a list of all the services in DB.
   */
  public List getServices(Service service);
  
  /**
   * Save/update a service's information
   *
   * @param service the object to be saved
   */
  public void saveService(Service service);

}
