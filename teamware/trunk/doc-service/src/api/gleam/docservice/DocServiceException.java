/*
 *  DocServiceException.java
 *
 *  Copyright (c) 2006-2006, The University of Sheffield.
 *
 *  Andrey Shafirin, 25/Apr/2006
 *
 *  $Id$
 */

package gleam.docservice;

/**
 * Exception signalling a problem with the service.
 *
 * Old name was ServiceException.
 * Was renamed due to possible conflicts with javax.xml.rpc.ServiceException.
 */
public class DocServiceException extends Exception {
  public DocServiceException() {
    super();
  }
  
  public DocServiceException(String message) {
    super(message);
  }

  public DocServiceException(Throwable cause) {
    super(cause);
  }

  public DocServiceException(String message, Throwable cause) {
    super(message, cause);
  }
}
