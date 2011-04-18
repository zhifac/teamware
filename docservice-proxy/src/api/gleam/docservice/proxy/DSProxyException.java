/*
 * This file is part of GLEAM, licenced under the GNU Lesser General Public
 * Licence.
 *
 * (c) 2006 The University of Sheffield
 * 
 * $Id$
 */
package gleam.docservice.proxy;

/**
 * Exception signalling a problem when using the doc service proxy.
 */
public class DSProxyException extends Exception {

  public DSProxyException() {
    super();
  }

  public DSProxyException(String message) {
    super(message);
  }

  public DSProxyException(Throwable cause) {
    super(cause);
  }

  public DSProxyException(String message, Throwable cause) {
    super(message, cause);
  }

}
