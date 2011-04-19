/*
 *  GASAdminService.java
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
package gleam.gateservice.endpoint;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

/**
 * Service endpoint interface for a service to perform admin operations
 * on one or more GAS endpoints.
 */
@WebService(targetNamespace = "http://gate.ac.uk/ns/safe/1.1/gate-service/admin")
public interface GASAdminService {

  /**
   * Refresh this admin service's GAS workers. The <code>newData</code>
   * parameter is a zip file which will be unpacked in the GaS top
   * directory before the service is restarted. It may be null, in which
   * case the service will restart unchanged.
   * 
   * @param newData an optional zip file containing new files which will
   *          be unpacked in the GaS top directory, replacing any
   *          existing files of the same name.
   * @throws GASAdminServiceFault if an error occurs sending the refresh
   *           request to the workers.
   */
  @WebMethod
  @WebResult(targetNamespace = "http://gate.ac.uk/ns/safe/1.1/gate-service/admin")
  public void refresh(
          @WebParam(name = "newData") byte[] newData) throws GASAdminServiceFault;
}
