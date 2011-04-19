/*
 *  ReloadAction.java
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

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import gleam.executive.webapp.listener.StartupListener;

/**
 * This class is used to reload the drop-downs initialized in the
 * StartupListener.
 * 
 * <p>
 * <a href="ReloadAction.java.html"><i>View Source</i></a>
 * </p>
 * 
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 * 
 * @struts.action path="/reload" validate="false"
 */
public final class ReloadAction extends BaseAction {

  public ActionForward execute(ActionMapping mapping, ActionForm form,
          HttpServletRequest request, HttpServletResponse response)
          throws Exception {
    if(log.isDebugEnabled()) {
      log.debug("Entering 'execute' method");
    }

    StartupListener.setupContext(getServlet().getServletContext());

    String referer = request.getHeader("Referer");

    if(referer != null) {
      log.debug("reload complete, reloading user back to: " + referer);

      ActionMessages messages = new ActionMessages();
      messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
              "reload.succeeded"));
      saveMessages(request.getSession(), messages);

      response.sendRedirect(response.encodeRedirectURL(referer));
      return null;
    }
    else {
      response.setContentType("text/html");

      PrintWriter out = response.getWriter();

      out.println("<html>");
      out.println("<head>");
      out.println("<title>Context Reloaded</title>");
      out.println("</head>");
      out.println("<body bgcolor=\"white\">");
      out.println("<script type=\"text/javascript\">");
      out.println("alert('Context Reload Succeeded! Click OK to continue.');");
      out.println("history.back();");
      out.println("</script>");
      out.println("</body>");
      out.println("</html>");
    }

    return null;
  }
}
