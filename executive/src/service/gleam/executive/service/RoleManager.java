package gleam.executive.service;

import gleam.executive.model.Role;
import java.util.List;

/**
 * Business Service Interface to handle communication between web and
 * persistence layer.
 *
 * <p>
 * <a href="RoleManager.java.html"><i>View Source</i></a>
 * </p>
 *
 * @author <a href="mailto:agaton@dcs.shef.ac.uk">Milan Agatonovic</a>
 */
public interface RoleManager extends Manager {
  public List getRoles(Role role);

  public Role getRole(String rolename);

  public void saveRole(Role role);

  public void removeRole(String rolename);

  public Role getRoleByRolename(String rolename);

  public List getRolesWithResource(String url);
}
