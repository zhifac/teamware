package gleam.executive.service;

import java.util.List;
import gleam.executive.dao.LookupDao;

/**
 * Business Service Interface to talk to persistence layer and retrieve
 * values for drop-down choice lists.
 *
 * <p>
 * <a href="LookupManager.java.html"><i>View Source</i></a>
 * </p>
 *
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 * @author <a href="mailto:agaton@dcs.shef.ac.uk">Milan Agatonovic</a>
 * @author <a href="mailto:hsun@dcs.shef.ac.uk">Haotian Sun</a>
 */
public interface LookupManager extends Manager {
  // ~ Methods
  // ================================================================
  public void setLookupDao(LookupDao dao);

  
  public List getRoles();
  /**
   * Retrieves all possible roles from persistence layer
   *
   * @return List
   */
  public List getAllRoles();

  /**
   * Retrieves all possible services from persistence layer
   *
   * @return List
   */
  public List getAllServices();
}
