/*
 *  ActionExceptionHandler.java
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
package gleam.executive.webapp.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acegisecurity.AccessDeniedException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.action.ExceptionHandler;
import org.apache.struts.config.ExceptionConfig;

/**
 * Implementation of <strong>ExceptionHandler</strong> that handles any
 * Exceptions that are bubbled up to the Action layer. This allows us to
 * remove generic try/catch statements from our Action Classes.
 *
 * <p>
 * <a href="ActionExceptionHandler.java.html"><i>View Source</i></a>
 * </p>
 *
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 */
public final class ActionExceptionHandler extends ExceptionHandler {
  // ~ Instance fields
  // ========================================================

  private transient final Log log = LogFactory
          .getLog(ActionExceptionHandler.class);

  // ~ Methods
  // ================================================================

  /**
   * This method handles any java.lang.Exceptions that are not caught in
   * previous classes. It will loop through and get all the causes
   * (exception chain), create ActionErrors, add them to the request and
   * then forward to the input.
   *
   * @see org.apache.struts.action.ExceptionHandler#execute (
   *      java.lang.Exception, org.apache.struts.config.ExceptionConfig,
   *      org.apache.struts.action.ActionMapping,
   *      org.apache.struts.action.ActionForm,
   *      javax.servlet.http.HttpServletRequest,
   *      javax.servlet.http.HttpServletResponse )
   */
  public ActionForward execute(Exception ex, ExceptionConfig ae,
          ActionMapping mapping, ActionForm formInstance,
          HttpServletRequest request, HttpServletResponse response)
          throws ServletException {
    // if there's already errors in the request, don't process
    ActionErrors errors = (ActionErrors)request.getAttribute(Globals.ERROR_KEY);

    if(errors != null) {
        log.debug("errors size: "+errors.size());
      return null;
    }
   
    ActionForward forward = super.execute(ex, ae, mapping, formInstance,
            request, response);

    ActionMessage error = null;
    String property = null;

    if(ex instanceof AccessDeniedException && forward == null) {
      storeException(request, "", new ActionMessage("errors.detail", ex
              .getMessage()), forward);
      try {
        response.sendError(HttpServletResponse.SC_FORBIDDEN);
        return null;
      }
      catch(IOException io) {
        io.printStackTrace();
        log.error(io.getMessage());
      }
    }
    
    

    // Get the chained exceptions (causes) and add them to the
    // list of errors as well
    while(ex != null) {
      String msg = ex.getMessage();
      if(msg==null){
    	  msg = "Error occured: ";
      }
     
      ex = (Exception)ex.getCause();

      if((ex != null) && (ex.getMessage() != null)) {
    	  log.debug("cause exception found iterate more: "+ex.getMessage());
        // check to see if the child message is the same
        // if so, don't store it
        /*
    	if(msg.indexOf(ex.getMessage()) == -1) {
        	error = new ActionMessage("errors.detail", ex.getMessage());
            property = error.getKey(); 
          storeException(request, property, error, forward);
        }
        */
      }
      else {
    	  error = new ActionMessage("errors.detail", msg);
          property = error.getKey(); 
        storeException(request, property, error, forward);
      }
    }

    return forward;
  }

  /**
   * This method overrides the the ExceptionHandler's storeException
   * method in order to create more than one error message.
   *
   * @param request - The request we are handling
   * @param property - The property name to use for this error
   * @param error - The error generated from the exception mapping
   * @param forward - The forward generated from the input path (from
   *          the form or exception mapping)
   */
  protected void storeException(HttpServletRequest request, String property,
          ActionMessage error, ActionForward forward) {
    ActionMessages errors = (ActionMessages)request
            .getAttribute(Globals.ERROR_KEY);

    if(errors == null) {
      errors = new ActionMessages();
    }
    errors.add(property, error);

    request.setAttribute(Globals.ERROR_KEY, errors);
  }

  /**
   * Overrides logException method in ExceptionHandler to print the
   * stackTrace
   *
   * @see org.apache.struts.action.ExceptionHandler#logException(java.lang.Exception)
   */
  protected void logException(Exception ex) {
    StringWriter sw = new StringWriter();
    ex.printStackTrace(new PrintWriter(sw));
    log.error(sw.toString());
  }
}
