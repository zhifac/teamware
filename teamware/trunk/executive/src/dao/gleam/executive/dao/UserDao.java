package gleam.executive.dao;

import java.util.List;
import org.acegisecurity.userdetails.UserDetails;
import org.acegisecurity.userdetails.UsernameNotFoundException;
import gleam.executive.model.User;

/**
 * User Data Access Object (Dao) interface.
 *
 * <p>
 * <a href="UserDao.java.html"><i>View Source</i></a>
 * </p>
 *
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 */
public interface UserDao extends Dao {
  /**
   * Gets users information based on user id.
   *
   * @param userId the user's id
   * @return user populated user object
   */
  public User getUser(Long userId);

  /**
   * Gets users information based on login name.
   *
   * @param username the user's username
   * @return userDetails populated userDetails object
   */
  public UserDetails loadUserByUsername(String username)
          throws UsernameNotFoundException;

  /**
   * Gets a list of users, not including the superadmin.
   *
   * @return List populated list of users
   */
  public List<User> getUsers();
  
  /**
   * Gets a list of all users, including the superadmin.
   *
   * @return List populated list of users
   */
  public List<User> getAllUsers();

  /**
   * Saves a user's information
   *
   * @param user the object to be saved
   */
  public void saveUser(User user);

  /**
   * Removes a user from the database by id
   *
   * @param userId the user's id
   */
  public void removeUser(Long userId);
  
  
  /**
   * Enable a user or not directly via setting the boolean
   * @param userId
   */
  public void enableUser(Long userId,boolean enabled);

  /**
   * Fetches all users from the database with specified role
   *
   * @param roleName
   *          the role name
   * @return List of users
   */
  public List getUsersWithRole(String roleName);
  
  /**
   * Fetches all project from the database belonging to specified user
   *
   * @param userId
   * @return List of projects
   */
  public List getUserProjects(Long userId);
  
  /**
   * Gets a list of user ids
   *
   * @return List populated list of users
   */
  public List<String> getUserIds();



}
