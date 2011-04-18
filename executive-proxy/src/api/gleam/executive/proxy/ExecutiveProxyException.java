/*
 * This file is part of GLEAM, licenced under the GNU Lesser General Public
 * Licence.
 *
 * (c) 2006 The University of Sheffield
 * 
 * $Id$
 */
package gleam.executive.proxy;

public class ExecutiveProxyException extends Exception {

  public ExecutiveProxyException() {
    super();
  }

  public ExecutiveProxyException(String message) {
    super(message);
  }

  public ExecutiveProxyException(Throwable cause) {
    super(cause);
  }

  public ExecutiveProxyException(String message, Throwable cause) {
    super(message, cause);
  }

}
