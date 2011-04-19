/*
 *  IteratingPasswordEncoder.java
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

import org.acegisecurity.providers.encoding.MessageDigestPasswordEncoder;

/**
 * PasswordEncoder that expects {@link SaltWithIterations} as its salt values,
 * and uses the iteration count specified by the salt to run the underlying
 * encoder repeatedly.
 */
public class IteratingPasswordEncoder extends MessageDigestPasswordEncoder {
  
  public IteratingPasswordEncoder() {
    this("SHA-1");
  }
  
  public IteratingPasswordEncoder(String algorithm) {
    super(algorithm);
  }

  @Override
  public String encodePassword(String rawPass, Object salt) {
    int iterations = 1;
    Object realSalt = salt;
    if(salt instanceof SaltWithIterations) {
      iterations = ((SaltWithIterations)salt).getIterations();
      realSalt = ((SaltWithIterations)salt).getSalt();
    }
    String encoded = rawPass;
    for(int i = 0; i < iterations; i++) {
      encoded = super.encodePassword(encoded, realSalt);
    }
    return encoded;
  }
}
