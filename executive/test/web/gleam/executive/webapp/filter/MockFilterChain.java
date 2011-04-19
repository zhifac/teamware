/*
 *  MockFilterChain.java
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
package gleam.executive.webapp.filter;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Borrowed from the Display Tag project:
 * http://displaytag.sourceforge.net/xref-test/org/displaytag/filter/MockFilterSupport.html
 */
public class MockFilterChain implements FilterChain {
  private final Log log = LogFactory.getLog(MockFilterChain.class);

  public void doFilter(ServletRequest request, ServletResponse response)
          throws IOException, ServletException {
    String uri = ((HttpServletRequest)request).getRequestURI();
    String requestContext = ((HttpServletRequest)request).getContextPath();
    if(StringUtils.isNotEmpty(requestContext) && uri.startsWith(requestContext)) {
      uri = uri.substring(requestContext.length());
    }
    if(log.isDebugEnabled()) {
      log.debug("Redirecting to [" + uri + "]");
    }
    RequestDispatcher dispatcher = request.getRequestDispatcher(uri);
    dispatcher.forward(request, response);
  }
}
