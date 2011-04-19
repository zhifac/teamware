/*
 *  PasswordHintAction.java
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.util.MessageResources;
import gleam.executive.model.User;
import gleam.executive.service.MailEngine;
import gleam.executive.service.UserManager;
import gleam.executive.webapp.util.RequestUtil;
import org.springframework.mail.SimpleMailMessage;

/**
 * Action class to send password hints to registered users.
 * 
 * <p>
 * <a href="PasswordHintAction.java.html"><i>View Source</i></a>
 * </p>
 * 
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 * 
 * @struts.action path="/passwordHint" validate="false"
 * @struts.action-forward name="previousPage" path="/"
 */
public final class PasswordHintAction extends BaseAction {

  public ActionForward execute(ActionMapping mapping, ActionForm form,
          HttpServletRequest request, HttpServletResponse response)
          throws Exception {
    MessageResources resources = getResources(request);
    ActionMessages errors = new ActionMessages();
    String username = request.getParameter("username");

    // ensure that the username has been sent
    if(username == null) {
      log
              .warn("Username not specified, notifying user that it's a required field.");

      errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
              "errors.required", resources.getMessage("userForm.username")));
      saveErrors(request, errors);
      return mapping.findForward("previousPage");
    }

    if(log.isDebugEnabled()) {
      log.debug("Processing Password Hint...");
    }

    ActionMessages messages = new ActionMessages();

    // look up the user's information
    try {
      UserManager userMgr = (UserManager)getBean("userManager");
      User user = userMgr.getUserByUsername(username);

      StringBuffer msg = new StringBuffer();
      msg.append("Your password hint is: " + user.getPasswordHint());
      msg.append("\n\nLogin at: " + request.getSession().getServletContext().getAttribute("urlbase"));

      SimpleMailMessage message = (SimpleMailMessage)getBean("mailMessage");
      message.setTo(user.getEmail());
      String subject = '[' + resources.getMessage("webapp.name") + "] "
              + resources.getMessage("userForm.passwordHint");
      message.setSubject(subject);
      message.setText(msg.toString());

      MailEngine mailEngine = (MailEngine)getBean("mailEngine");
      mailEngine.send(message);

      messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
              "login.passwordHint.sent", username, user.getEmail()));
      saveMessages(request.getSession(), messages);
    }
    catch(Exception e) {
      e.printStackTrace();
      // If exception is expected do not rethrow
      errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
              "login.passwordHint.error", username));
      saveErrors(request, errors);
    }

    return mapping.findForward("previousPage");
  }
}
