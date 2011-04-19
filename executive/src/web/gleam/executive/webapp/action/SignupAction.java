/*
 *  SignupAction.java
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

import java.util.HashMap;
import java.util.Map;

import javax.jms.ObjectMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acegisecurity.Authentication;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.providers.ProviderManager;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.acegisecurity.providers.encoding.PasswordEncoder;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.util.MessageResources;
import gleam.executive.Constants;
import gleam.executive.model.User;
import gleam.executive.security.SaltWithIterations;
import gleam.executive.service.RoleManager;
import gleam.executive.service.UserExistsException;
import gleam.executive.service.UserManager;
import gleam.executive.util.StringUtil;
import gleam.executive.webapp.form.UserForm;
import gleam.executive.webapp.util.RequestUtil;
import gleam.executive.workflow.jms.EmailProducer;
import gleam.executive.workflow.jms.MessageProducer;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.mail.SimpleMailMessage;

/**
 * Action class to allow users to self-register.
 *
 * <p/> <a href="SignupAction.java.html"><i>View Source</i></a>
 * </p>
 *
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 *
 * @struts.action name="userForm" path="/signup" scope="request"
 *                validate="false" input="failure" 
 *
 * @struts.action-forward name="failure"
 *                        path="/WEB-INF/pages/signup.jsp"
 * @struts.action-forward name="success" path="/mainMenu.html"
 *                        redirect="true"
 */
public final class SignupAction extends BaseAction {

  public ActionForward execute(ActionMapping mapping, ActionForm form,
          HttpServletRequest request, HttpServletResponse response)
          throws Exception {

    // if it's an HTTP GET, simply forward to jsp
    if(request.getMethod().equals("GET")) {
      return mapping.findForward("failure");
      // user clicked cancel button
    }
    else if(isCancelled(request)) {
      return new ActionForward("/");
      // run validation
    }
    else {
      // run validation rules on this form
      ActionMessages errors = form.validate(mapping, request);
      if(!errors.isEmpty()) {
        saveErrors(request, errors);
        return mapping.findForward("failure");
      }
    }

    if(log.isDebugEnabled()) {
      log.debug("registering user...");
    }

    ActionMessages errors = new ActionMessages();
    UserForm userForm = (UserForm)form;
    User user = (User)convert(form);

    // Set the default user role on this new user
    RoleManager roleMgr = (RoleManager)getBean("roleManager");
    user.addRole(roleMgr.getRole(Constants.ANNOTATOR_ROLE));

    try {
      UserManager mgr = (UserManager)getBean("userManager");
      mgr.setUserPassword(user, user.getPassword());
      user.setEnabled(true);
      mgr.saveUser(user);
    }
    catch(UserExistsException e) {
      log.warn(e.getMessage());
      errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
              "errors.existing.user", userForm.getUsername(), userForm
                      .getEmail()));
      saveErrors(request, errors);
      return mapping.getInputForward();
    }

    ActionMessages messages = new ActionMessages();
    MessageResources resources = getResources(request);

    messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
            "user.registered", userForm.getUsername()));

    saveMessages(request.getSession(), messages);
    request.getSession().setAttribute(Constants.REGISTERED, Boolean.TRUE);

    // log user in automatically
    Authentication auth = new UsernamePasswordAuthenticationToken(user
            .getUsername(), user.getConfirmPassword());
    try {
      ProviderManager authenticationManager = (ProviderManager)getBean("authenticationManager");
      SecurityContextHolder.getContext().setAuthentication(
              authenticationManager.doAuthentication(auth));
    }
    catch(NoSuchBeanDefinitionException n) {
      // ignore, should only happen when testing
    }

    // Send user an e-mail
    if(log.isDebugEnabled()) {
      log.debug("Sending user '" + userForm.getUsername()
              + "' an account information e-mail");
    }
    
    EmailProducer mProducer = (EmailProducer)getBean("emailProducer");
	
    SimpleMailMessage message = (SimpleMailMessage)getBean("mailMessage");
    
    message.setTo(user.getFullName() + "<" + user.getEmail() + ">");
    /*
    StringBuffer msg = new StringBuffer();
    msg.append(resources.getMessage("signup.email.message"));
    msg.append("\n\n" + resources.getMessage("userForm.username"));
    msg.append(": " + userForm.getUsername() + "\n");
    msg.append(resources.getMessage("userForm.password") + ": ");
    msg.append(userForm.getPassword());
    msg.append("\n\nLogin at: " + RequestUtil.getAppURL(request));
    message.setText(msg.toString());
    */
    message.setSubject(resources.getMessage("signup.email.subject"));
    Map<String,String> model=new HashMap<String,String>();
    model.put("message",resources.getMessage("signup.email.message"));
    model.put("userName",userForm.getUsername());
    model.put("confirmPassword",userForm.getPassword());
    model.put("applicationURL", request.getSession().getServletContext().getAttribute("urlbase").toString());
    
    mProducer.sendMessage(message,"accountCreated.vm",model);
    return mapping.findForward("success");
  }

}
