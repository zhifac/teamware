/*
 * This file is part of GLEAM, licenced under the GNU Lesser General Public
 * Licence.
 *
 * (c) 2006 The University of Sheffield
 * 
 * $Id$
 */
package gleam.gateservice.endpoint;

public class GASAdminServiceFault extends Exception {

  public GASAdminServiceFault() {
  }

  public GASAdminServiceFault(String message) {
    super(message);
  }

  public GASAdminServiceFault(Throwable cause) {
    super(cause);
  }

  public GASAdminServiceFault(String message, Throwable cause) {
    super(message, cause);
  }

}
