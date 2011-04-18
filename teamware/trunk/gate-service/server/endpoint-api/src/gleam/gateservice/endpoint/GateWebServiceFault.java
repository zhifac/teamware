/*
 * This file is part of GLEAM, licenced under the GNU Lesser General Public
 * Licence.
 *
 * (c) 2006 The University of Sheffield
 * 
 * $Id$
 */
package gleam.gateservice.endpoint;

public class GateWebServiceFault extends Exception {

  public GateWebServiceFault() {
  }

  public GateWebServiceFault(String message) {
    super(message);
  }

  public GateWebServiceFault(Throwable cause) {
    super(cause);
  }

  public GateWebServiceFault(String message, Throwable cause) {
    super(message, cause);
  }

}
