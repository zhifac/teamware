package gleam.executive.service;

import java.util.List;
import org.acegisecurity.userdetails.UsernameNotFoundException;
import gleam.executive.dao.UserDao;
import gleam.executive.model.User;

/**
 * Business Service Interface to handle communication between web and
 * persistence layer.
 *
 * <p>
 * <a href="UserManager.java.html"><i>View Source</i></a>
 * </p>
 *
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 * @author <a href="mailto:agaton@dcs.shef.ac.uk">Milan Agatonovic</a>
 */
public interface UserManager {
  public void setUserDao(UserDao userDao);

  /**
   * Retrieves a user by userId. An exception is thrown if user not
   * found
   *
   * @param userId
   * @return User
   */
  public User getUser(String userId);

  /**
   * Finds a user by their username.
   *
   * @param username
   * @return User a populated user object
   */
  public User getUserByUsername(String username)
          throws UsernameNotFoundException;

  /**
   * Retrieves a list of users, 
   *
   * @return List
   */
  public List<User> getUsers();
  
  /**
   * Retrieves a list of users, 
   *
   * @return List
   */
  public List<String> getUserIds();

  /**
   * Saves a user's information
   *
   * @param user the user's information
   * @throws UserExistsException
   */
  public void saveUser(User user) throws UserExistsException;

  /**
   * Removes a user from the database by their userId
   *
   * @param userId the user's id
   */
  public void removeUser(String userId);

  public void enableUser(String userId, boolean enabled);

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
  public List getUserProjects(String userId);
  
  /**
   * Set the given password on the given user object, salting and
   * encrypting it as required (and storing the salt and iteration
   * count as well).
   */
  public void setUserPassword(User user, String password);
}
