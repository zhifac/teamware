/*
 *  UserSaltSource.java
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
package gleam.executive.security;

import gleam.executive.model.User;

import org.acegisecurity.providers.dao.SaltSource;
import org.acegisecurity.userdetails.UserDetails;

/**
 * SaltSource that generates a SaltWithIterations from a User's
 * salt and iterations properties.
 */
public class UserSaltSource implements SaltSource {

  public Object getSalt(UserDetails userDetails) {
    if(userDetails instanceof User) {
      String salt = ((User)userDetails).getSalt();
      int iterations = ((User)userDetails).getIterations();
      if(iterations < 0) iterations = 1;
      return new SaltWithIterations(salt, iterations);
    }
    else {
      return null;
    }
  }

}
