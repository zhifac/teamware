/*
 * This file is part of GLEAM, licenced under the GNU Lesser General Public
 * Licence.
 *
 * (c) 2006 The University of Sheffield
 * 
 * $Id$
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
