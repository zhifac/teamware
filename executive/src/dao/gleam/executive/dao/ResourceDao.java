package gleam.executive.dao;

import java.util.List;
import gleam.executive.model.Resource;
import gleam.executive.model.Service;

/**
 * Resource Data Access Object (Dao) interface.
 *
 * <p>
 * <a href="ResourceDao.java.html"><i>View Source</i></a>
 * </p>
 *
 * @author <a href="mailto:agaton@dcs.shef.ac.uk">Milan Agatonovic</a>
 */
public interface ResourceDao extends Dao {
  /**
   * Gets users information based on resource id.
   *
   * @param resourceId the resource's id
   * @return SecuredResource populated SecuredResource object
   */
  public Resource getResource(Long resourceId);

  /**
   * Gets a list of resources based on parameters passed in.
   *
   * @return List populated list of resources
   */
  public List getResources(Resource resource);

  /**
   * 
   * @return a list of all the resources in DB.
   */
  public List getResources();
  
  /**
   * Saves a resource's information
   *
   * @param resource the object to be saved
   */
  public void saveResource(Resource resource);

  /**
   * Removes a resource from the database by id
   *
   * @param resourceId the resource's id
   */
  public void removeResource(Long resourceId);

  /**
   * Fetches all resources from the database with specified role
   *
   * @param roleName
   *          the role name
   * @return List of resources
   */
  public List getResourcesWithRole(String roleName);
  
  /**
   * Fetches all roles from the database with specified resource url
   *
   * @param url
   *          the resource url
   * @return List of roles
   */
  public List getRolesWithResource(String url);
  
  /**
   * Fetches the relevant service from the database with specified resource
   *
   * @param resourceId
   *          the resource id
   * @return List of services
   */
  public Service getService(Long resourceId);
  
  /**
   * Fetches the relevant services from the database with specified service id
   *
   * @param serviceId
   * @return List of resources
   */
  public List getResourcesWithService(Long serviceId);

}
