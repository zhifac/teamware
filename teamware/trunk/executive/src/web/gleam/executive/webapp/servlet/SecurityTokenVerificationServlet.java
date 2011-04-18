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
