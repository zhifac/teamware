/*
 *  UserManagerImpl.java
 *
 *  Copyright (c) 2006-2011, The University of Sheffield.
 *
 *  This file is part of GATE Teamware (see http://gate.ac.uk/teamware/), 
 *  and is free software, licenced under the GNU Affero General Public License,
 *  Version 3, November 2007 (also included with this distribution as file 
 *  LICENCE-AGPL3.html).
 *
 *  A commercial licence is also available for organisations whose business
 *  models preclude the adoption of open source and is subject to a licence
 *  fee charged by the University of Sheffield. Please contact the GATE team
 *  (see http://gate.ac.uk/g8/contact) if you require a commercial licence.
 *
 *  $Id$
 */
package gleam.executive.service.impl;

import java.util.List;

import org.acegisecurity.providers.encoding.PasswordEncoder;
import org.acegisecurity.userdetails.UsernameNotFoundException;
import gleam.executive.dao.UserDao;
import gleam.executive.model.User;
import gleam.executive.security.SaltWithIterations;
import gleam.executive.service.UserExistsException;
import gleam.executive.service.UserManager;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.DataIntegrityViolationException;

/**
 * Implementation of UserManager interface.
 * </p>
 *
 * <p>
 * <a href="UserManagerImpl.java.html"><i>View Source</i></a>
 * </p>
 *
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 * @author <a href="M.Agatonovic@dcs.shef.ac.uk">Milan Agatonovic</a>
 */
public class UserManagerImpl extends BaseManager implements UserManager, InitializingBean {
  private UserDao dao;
  private PasswordEncoder encoder;

  /**
   * Set the Dao for communication with the data layer.
   *
   * @param dao
   */
  public void setUserDao(UserDao dao) {
    this.dao = dao;
  }

  /**
   * Set the password encoder for encrypting passwords.  If not
   * set, passwords will not be encrypted.
   */
  public void setPasswordEncoder(PasswordEncoder encoder) {
    this.encoder = encoder;
  }
  
  /**
   * @see gleam.executive.service.UserManager#getUser(java.lang.String)
   */
  public User getUser(String userId) {
    return dao.getUser(new Long(userId));
  }

  /**
   * @see gleam.executive.service.UserManager#getUsers()
   */
  public List<User> getUsers() {
    return dao.getUsers();
  }
  
  /**
   * @see gleam.executive.service.UserManager#getUserIds()
   */
  public List<String> getUserIds() {
    return dao.getUserIds();
  }

  /**
   * @see gleam.executive.service.UserManager#saveUser(gleam.executive.model.User)
   */
  public void saveUser(User user) throws UserExistsException {
    // if new user, lowercase userId
    if(user.getVersion() == null) {
      user.setUsername(user.getUsername().toLowerCase());
    }
    try {
      dao.saveUser(user);
    }
    catch(DataIntegrityViolationException e) {
      throw new UserExistsException("User '" + user.getUsername()
              + "' already exists!");
    }
  }

  public void enableUser(String userId, boolean enabled){
    dao.enableUser(new Long(userId), enabled);
  }

  /**
   * @see gleam.executive.service.UserManager#removeUser(java.lang.String)
   */
  public void removeUser(String userId) {
    if(log.isDebugEnabled()) {
      log.debug("removing user: " + userId);
    }
    dao.removeUser(new Long(userId));
  }

  public User getUserByUsername(String username)
          throws UsernameNotFoundException {
    return (User)dao.loadUserByUsername(username);
  }

  /**
  * @see gleam.executive.service.UserManager#getUsersByRole(String)
  */
  public List getUsersWithRole(String roleName){
    if(log.isDebugEnabled()) {
      log.debug("searching users with role name: " + roleName);
    }
    return dao.getUsersWithRole(roleName);
  }
  
  /**
   * @see gleam.executive.service.UserManager#getUserProjects(String)
   */
   public List getUserProjects(String userId){
     if(log.isDebugEnabled()) {
       log.debug("searching projects for user with ID: " + userId);
     }
     return dao.getUserProjects(new Long(userId));
   }
   
   /**
    * Set the given password on the given user object, encrypting it
    * if a {@link PasswordEncoder} has been provided.
    */
   public void setUserPassword(User user, String password) {
     if(encoder == null) {
       log.debug("No passwordEncoder property set - password not encrypted");
       user.setPassword(password);
       user.setSalt(null);
       user.setIterations(0);
     }
     else {
       SaltWithIterations swi = SaltWithIterations.createRandom();
       user.setSalt(swi.getSalt());
       user.setIterations(swi.getIterations());
       user.setPassword(encoder.encodePassword(password, swi));
     }
   }

  public void afterPropertiesSet() throws Exception {
    if(encoder != null && dao != null) {
      // encrypt non-encrypted passwords in the DB
      for(User u : dao.getAllUsers()) {
        if(u.getSalt() == null && u.getIterations() == 0) {
          log.debug("Found non-encrypted password for user "
                  + u.getUsername() + ", encrypting.");
          // password not encrypted, encrypt it
          setUserPassword(u, u.getPassword());
          saveUser(u);
        }
      }
    }
  }
   
}
