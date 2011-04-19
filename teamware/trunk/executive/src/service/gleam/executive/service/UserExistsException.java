/*
 *  UserExistsException.java
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

/**
 * An exception that is thrown by classes wanting to trap unique
 * constraint violations. This is used to wrap Spring's
 * DataIntegrityViolationException so it's checked in the web layer.
 * 
 * <p>
 * <a href="UserExistsException.java.html"><i>View Source</i></a>
 * </p>
 * 
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 */
public class UserExistsException extends Exception {
  private static final long serialVersionUID = 4050482305178810162L;

  /**
   * Constructor for UserExistsException.
   * 
   * @param message
   */
  public UserExistsException(String message) {
    super(message);
  }
}
