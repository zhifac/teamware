package gleam.executive.dao.hibernate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.acegisecurity.userdetails.UserDetails;
import org.acegisecurity.userdetails.UserDetailsService;
import org.acegisecurity.userdetails.UsernameNotFoundException;
import gleam.executive.dao.UserDao;
import gleam.executive.model.Project;
import gleam.executive.model.User;
import org.springframework.orm.ObjectRetrievalFailureException;

/**
 * This class interacts with Spring's HibernateTemplate to save/delete
 * and retrieve User objects.
 *
 * <p>
 * <a href="UserDaoHibernate.java.html"><i>View Source</i></a>
 * </p>
 *
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 *         Modified by <a href="mailto:dan@getrolling.com">Dan Kibler</a>
 *         Extended to implement Acegi UserDetailsService interface by
 *         David Carter david@carter.net
 */
public class UserDaoHibernate extends BaseDaoHibernate implements UserDao,
                                                      UserDetailsService {
  /**
   * @see gleam.executive.dao.UserDao#getUser(Long)
   */
  public User getUser(Long userId) {
    User user = (User)getHibernateTemplate().get(User.class, userId);
    if(user == null) {
      log.warn("uh oh, user '" + userId + "' not found...");
      throw new ObjectRetrievalFailureException(User.class, userId);
    }
    return user;
  }

  /**
   * @see gleam.executive.dao.UserDao#getUsers(gleam.executive.model.User)
   */
  public List<User> getUsers() {
    return getHibernateTemplate()
            .find("from User u where u.username != 'superadmin' order by upper(u.username)");
  }

  /**
   * @see gleam.executive.dao.UserDao#getAllUsers()
   */
  public List<User> getAllUsers() {
    return getHibernateTemplate()
            .find("from User u order by upper(u.username)");
  }
  
  /**
   * @see gleam.executive.dao.UserDao#saveUser(gleam.executive.model.User)
   */
  public void saveUser(final User user) {
    getHibernateTemplate().saveOrUpdate(user);
    // necessary to throw a DataIntegrityViolation and catch it in
    // UserManager
    getHibernateTemplate().flush();
  }
  
  public void enableUser(Long userId, boolean enabled){
    User user=getUser(userId);
    user.setEnabled(enabled);
    getHibernateTemplate().saveOrUpdate(user);
    getHibernateTemplate().flush();
  }

  /**
   * @see gleam.executive.dao.UserDao#removeUser(Long)
   */
  public void removeUser(Long userId) {
    getHibernateTemplate().delete(getUser(userId));
  }

  /**
   * @see org.acegisecurity.userdetails.UserDetailsService#loadUserByUsername(java.lang.String)
   */
  public UserDetails loadUserByUsername(String username)
          throws UsernameNotFoundException {
    List users = getHibernateTemplate().find("from User where username=?",
            username);
    if(users == null || users.isEmpty()) {
      throw new UsernameNotFoundException("user '" + username
              + "' not found...");
    }
    else {
      return (UserDetails)users.get(0);
    }
  }

  /**
   * @see gleam.executive.dao.UserDao#getUsersByRole(String)
   */
  public List getUsersWithRole(String roleName) {
    StringBuffer sb = new StringBuffer();
    sb.append("SELECT DISTINCT user from ")
      .append (User.class.getName() + " user ")
      .append (" JOIN user.roles roles ")
      .append (" WHERE roles.name = '" + roleName +"' order by user.username");
    log.debug("query: "+sb.toString());
    return getHibernateTemplate().find(sb.toString());
  }
  
  /**
   * @see gleam.executive.dao.UserDao#getUserProjects(String)
   */
  public List getUserProjects(Long userId) {
    StringBuffer sb = new StringBuffer();
    sb.append("SELECT DISTINCT project from ")
      .append (Project.class.getName() + " project ")
      .append (" JOIN user.projects users ")
      .append (" WHERE users.id = '" + userId +"' ");
    log.debug("query: "+sb.toString());
    return getHibernateTemplate().find(sb.toString());
  }
  
  /**
   * @see gleam.executive.dao.UserDao#getUserIds()
   */
  public List<String> getUserIds() {
	  List<User> users = getHibernateTemplate().find("from User u order by upper(u.username)");
      List<String> userIds = new ArrayList<String>();
      Iterator<User> it = users.iterator();
      while(it.hasNext()){
    	  userIds.add(it.next().getId().toString());
      }
      return userIds;
      
      
  }
}
