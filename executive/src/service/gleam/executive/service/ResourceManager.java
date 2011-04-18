package gleam.executive.service;

import gleam.executive.model.Resource;
import gleam.executive.model.Service;
import java.util.List;

/**
 * Business Service Interface to handle communication between web and
 * persistence layer.
 *
 * <p>
 * <a href="ResourceManager.java.html"><i>View Source</i></a>
 * </p>
 * @author <a href="mailto:agaton@dcs.shef.ac.uk">Milan Agatonovic</a>
 * @author <a href="mailto:H.Sun@dcs.shef.ac.uk">Haotian Sun </a>
 */
public interface ResourceManager extends Manager {
  public List getResources(Resource resource);

  public List getResources();

  public Resource getResource(String resourceId);

  public void saveResource(Resource resource);

  public void removeResource(String resourceId);

  public List getResourcesWithRole(String rolename);

  public List getRolesWithResource(String url);

  public List getResourcesWithService(String serviceId);

  public Service getService(String resourceId);

}
