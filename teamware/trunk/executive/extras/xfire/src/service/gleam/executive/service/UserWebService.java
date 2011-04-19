/*
 *  UserWebService.java
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
package gleam.executive.service;

import java.util.List;

import gleam.executive.dao.UserDao;
import gleam.executive.model.User;


/**
 * WebService Interface
 *
 * <p><a href="UserWebService.java.html"><i>View Source</i></a></p>
 *
 * @author <a href="mailto:mikagoeckel@codehaus.org">Mika Goeckel</a>
 * @aegis.mapping 
 */
public interface UserWebService {
    //~ Methods ================================================================

    /**
     * Retrieves a user by username.  An exception is thrown if now user 
     * is found.
     *
     * @param username
     * @return User
     */
    public User getUser(String username);

    /**
     * Retrieves a list of users, filtering with parameters on a user object
     * @param user parameters to filter on
     * @return List
     * 
     * Tell xfire, which objects it will find in the List
     * @aegis.method
     * @aegis.method-return-type componentType="gleam.executive.model.User"
     */
    public List getUsers(User user);

    /**
     * Saves a user's information
     *
     * @param user the user's information
     * @throws UserExistsException
     */
    public void saveUser(User user) throws UserExistsException;

    /**
     * Removes a user from the database by their username
     *
     * @param username the user's username
     */
    public void removeUser(String username);
}
