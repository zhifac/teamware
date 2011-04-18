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
