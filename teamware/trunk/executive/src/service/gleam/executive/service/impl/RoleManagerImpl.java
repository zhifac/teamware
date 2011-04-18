package gleam.executive.service.impl;

import java.util.List;
import gleam.executive.dao.RoleDao;
import gleam.executive.model.Role;
import gleam.executive.service.RoleManager;

/**
 * Implementation of RoleManager interface.
 * </p>
 *
 * <p>
 * <a href="RoleManagerImpl.java.html"><i>View Source</i></a>
 * </p>
 *
 * @author <a href="mailto:dan@getrolling.com">Dan Kibler</a>
 * @author <a href="M.Agatonovic@dcs.shef.ac.uk">Milan Agatonovic</a>
 */
public class RoleManagerImpl extends BaseManager implements RoleManager {
  private RoleDao dao;

  public void setRoleDao(RoleDao dao) {
    this.dao = dao;
  }

  public List getRoles(Role role) {
    return dao.getRoles(role);
  }

  public Role getRole(String rolename) {
    return dao.getRoleByName(rolename);
  }

  public void saveRole(Role role) {
    dao.saveRole(role);
  }

  public void removeRole(String rolename) {
    dao.removeRole(rolename);
  }

  public Role getRoleByRolename(String rolename){
    return (Role)dao.getRoleByName(rolename);
  }

  public List getRolesWithResource(String url) {
    return (List)dao.getRolesWithResource(url);
  }
}
