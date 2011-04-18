/*
 * This file is part of GLEAM, licenced under the GNU Lesser General Public
 * Licence.
 *
 * (c) 2006 The University of Sheffield
 * 
 * $Id$
 */
package gleam.gateservice.definition;

/**
 * Exception thrown when no values has been provided for a required
 * parameter in a GaS call.
 */
public class MissingParameterException extends RuntimeException {

  public MissingParameterException() {
  }

  public MissingParameterException(String message) {
    super(message);
  }
}
