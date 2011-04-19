/*
 *  ResourceManager.java
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

import gleam.executive.model.Resource;
import gleam.executive.model.Service;
import java.util.List;

/**
 * Business Service Interface to handle communication between web and
 * persistence layer.
 *
 * <p>
 * <a href="ResourceManager.java.html"><i>View Source</i></a>
 * </p>
 * @author <a href="mailto:agaton@dcs.shef.ac.uk">Milan Agatonovic</a>
 * @author <a href="mailto:H.Sun@dcs.shef.ac.uk">Haotian Sun </a>
 */
public interface ResourceManager extends Manager {
  public List getResources(Resource resource);

  public List getResources();

  public Resource getResource(String resourceId);

  public void saveResource(Resource resource);

  public void removeResource(String resourceId);

  public List getResourcesWithRole(String rolename);

  public List getRolesWithResource(String url);

  public List getResourcesWithService(String serviceId);

  public Service getService(String resourceId);

}
