/*
 *  ServiceDao.java
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
package gleam.executive.dao;

import java.util.List;
import gleam.executive.model.Service;

/**
 * Resource Data Access Object (Dao) interface.
 *
 * <p>
 * <a href="ServiceDao.java.html"><i>View Source</i></a>
 * </p>
 *
 * @author <a href="mailto:H.Sun@dcs.shef.ac.uk">Haotian Sun</a>
 */
public interface ServiceDao extends Dao {
  /**
   * Gets service information based on service id.
   *
   * @param serviceId the resource's id
   * @return Service populated Service object
   */
  public Service getService(Long serviceId);

  /**
   * 
   * @return a list of all the services in DB.
   */
  public List getServices(Service service);
  
  /**
   * Save/update a service's information
   *
   * @param service the object to be saved
   */
  public void saveService(Service service);

}
