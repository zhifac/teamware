/*
 *  SecurityTokenManager.java
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

public interface SecurityTokenManager {
  
  /**
   * Requests the creation of a new token.
   * @return the ID for the new token.
   */
  public String newToken();
  
  /**
   * Checks if a token is still valid.
   * @param tokenID the ID for the token to be verified.
   * @return <code>true</code> iff the token with the specified ID was found and
   * is still valid.
   */
  public boolean isValid(String tokenID);
  
}
