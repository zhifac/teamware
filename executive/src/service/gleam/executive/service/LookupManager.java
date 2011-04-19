/*
 *  LookupManager.java
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
import gleam.executive.dao.LookupDao;

/**
 * Business Service Interface to talk to persistence layer and retrieve
 * values for drop-down choice lists.
 *
 * <p>
 * <a href="LookupManager.java.html"><i>View Source</i></a>
 * </p>
 *
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 * @author <a href="mailto:agaton@dcs.shef.ac.uk">Milan Agatonovic</a>
 * @author <a href="mailto:hsun@dcs.shef.ac.uk">Haotian Sun</a>
 */
public interface LookupManager extends Manager {
  // ~ Methods
  // ================================================================
  public void setLookupDao(LookupDao dao);

  
  public List getRoles();
  /**
   * Retrieves all possible roles from persistence layer
   *
   * @return List
   */
  public List getAllRoles();

  /**
   * Retrieves all possible services from persistence layer
   *
   * @return List
   */
  public List getAllServices();
}
