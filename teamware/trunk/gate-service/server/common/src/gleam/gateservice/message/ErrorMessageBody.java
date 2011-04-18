/*
 * This file is part of GLEAM, licenced under the GNU Lesser General Public
 * Licence.
 *
 * (c) 2006 The University of Sheffield
 * 
 * $Id$
 */
package gleam.gateservice.message;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;

/**
 * Message body object for error messages returned to the endpoint by a
 * worker in GATE mode. An error message has a message string and
 * optionally a Throwable that gave rise to the error.
 */
public class ErrorMessageBody implements Serializable {
  static final long serialVersionUID = -1969016382635048490L;

  /**
   * The error message.
   */
  private String message;

  /**
   * The (possibly null) exception that caused this error.
   */
  private Throwable exception;

  /**
   * Create an error message with the given message string and
   * exception.
   */
  public ErrorMessageBody(String message, Throwable exception) {
    this.message = message;
    this.exception = exception;
  }

  /**
   * Create an error message with the given message string.
   */
  public ErrorMessageBody(String message) {
    this(message, null);
  }

  public Throwable getException() {
    return exception;
  }

  public void setException(Throwable exception) {
    this.exception = exception;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
  
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(this.getClass().getName());
    sb.append(": message = ");
    if(message == null) {
      sb.append("null");
    }
    else {
      sb.append('"');
      sb.append(message);
      sb.append('"');
    }
    
    if(exception != null) {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      sb.append(", exception = ");
      exception.printStackTrace(pw);
      sb.append(pw.toString());
    }
    
    return sb.toString();
  }
}
