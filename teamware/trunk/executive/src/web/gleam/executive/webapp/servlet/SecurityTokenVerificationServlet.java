/*
 *  SecurityTokenVerificationServlet.java
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
package gleam.executive.webapp.servlet;

import gleam.executive.service.SecurityTokenManager;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Simple servlet to check security tokens.  Expects a security token as the
 * final element of the path at which it is called, and returns a 200 response
 * if the token is valid and a 404 if it isn't.
 */
public class SecurityTokenVerificationServlet extends HttpServlet {

  private SecurityTokenManager securityTokenManager;
  private static Log log = LogFactory.getLog(
          SecurityTokenVerificationServlet.class);
  
  @Override
  public void init() throws ServletException {
    ServletContext context = this.getServletContext();
    ApplicationContext ctx = WebApplicationContextUtils
        .getRequiredWebApplicationContext(context);
    this.securityTokenManager =
      (SecurityTokenManager)ctx.getBean("securityTokenManager");
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
          throws ServletException, IOException {
    String pathInfo = req.getPathInfo();
    String tokenId = pathInfo.substring(pathInfo.lastIndexOf('/') + 1);
    resp.setContentType("text/plain; charset=UTF-8");
    if(securityTokenManager.isValid(tokenId)) {
      log.debug("Security token " + tokenId + " is valid");
      resp.getWriter().write("OK\n");
      resp.getWriter().close();
    }
    else {
      log.debug("Security token " + tokenId + " is not valid");
      resp.sendError(HttpServletResponse.SC_NOT_FOUND,
              "Invalid security token");
    }
  }
}
