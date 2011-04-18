package gleam.executive.service;

import gleam.executive.model.Service;
import java.util.List;

/**
 * Business Service Interface to handle communication between web and
 * persistence layer.
 * 
 * <p>
 * <a href="ServiceManager.java.html"><i>View Source</i></a>
 * </p>
 * 
 * @author <a href="mailto:H.Sun@dcs.shef.ac.uk">Haotian Sun </a>
 */
public interface ServiceManager extends Manager {
  
  public List getServices(Service service);
  
  public Service getService(String serviceId);

  public void saveService(Service service);
  
}
