/*
 *  SaltWithIterations.java
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

import java.util.Random;

/**
 * A compound salt that specifies the number of iterations of
 * the underlying message digest that should be performed.
 */
public class SaltWithIterations {
  private static Random rand = new Random();
  
  /**
   * The actual salt.
   */
  private String salt;
  
  /**
   * The number of iterations.
   */
  private int iterations;

  public SaltWithIterations(String salt, int iterations) {
    super();
    this.salt = salt;
    this.iterations = iterations;
  }

  public String getSalt() {
    return salt;
  }

  public int getIterations() {
    return iterations;
  }
  
  /**
   * Utility method to create a random salt/iterations pair.
   */
  public static SaltWithIterations createRandom() {
    return new SaltWithIterations(Integer.toHexString(rand.nextInt()),
            10000 + rand.nextInt(2000));
  }
}
