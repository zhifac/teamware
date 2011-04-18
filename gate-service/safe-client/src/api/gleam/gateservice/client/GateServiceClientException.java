/*
 * This file is part of SAFE, licenced under the GNU Lesser General Public
 * Licence.
 *
 * (c) 2006 The University of Sheffield
 * 
 * $Id$
 */
package gleam.gateservice.client;

public class GateServiceClientException extends Exception {

  public GateServiceClientException() {
    super();
  }

  public GateServiceClientException(String message) {
    super(message);
  }

  public GateServiceClientException(Throwable cause) {
    super(cause);
  }

  public GateServiceClientException(String message, Throwable cause) {
    super(message, cause);
  }

}
