/*
 *  UserAction.java
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

import gleam.executive.Constants;
import gleam.executive.model.Role;
import gleam.executive.model.User;
import gleam.executive.security.SaltWithIterations;
import gleam.executive.service.ProjectManager;
import gleam.executive.service.RoleManager;
import gleam.executive.service.UserExistsException;
import gleam.executive.service.UserManager;
import gleam.executive.util.StringUtil;
import gleam.executive.webapp.form.UserForm;
import gleam.executive.webapp.util.RequestUtil;
import gleam.executive.workflow.jms.EmailProducer;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acegisecurity.Authentication;
import org.acegisecurity.AuthenticationTrustResolver;
import org.acegisecurity.AuthenticationTrustResolverImpl;
import org.acegisecurity.context.SecurityContext;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.providers.encoding.PasswordEncoder;
import org.acegisecurity.userdetails.UsernameNotFoundException;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.util.MessageResources;
import org.springframework.mail.SimpleMailMessage;

/**
 * Implementation of <strong>Action</strong> that interacts with the
 * {@link UserForm} and retrieves values. It interacts with the {@link
 * UserManager} to retrieve/persist values to the database.
 * 
 * @struts.action name="userForm" path="/users" scope="request"
 *                validate="false" parameter="method" input="list"
 *                
 * @struts.action name="userForm" path="/editUser" scope="request"
 *                validate="false" parameter="method" input="list"
 * @struts.action name="userForm" path="/editProfile" scope="request"
 *                validate="false" parameter="method" input="mainMenu"
 * @struts.action name="userForm" path="/saveUser" scope="request"
 *                validate="false" parameter="method" input="edit"             
 * 
 * @struts.action-forward name="list" path="/WEB-INF/pages/userList.jsp"
 * @struts.action-forward name="edit" path="/WEB-INF/pages/userForm.jsp"
 */
public final class UserAction extends BaseAction {

  public ActionForward add(ActionMapping mapping, ActionForm form,
          HttpServletRequest request, HttpServletResponse response)
          throws Exception {
    if(log.isDebugEnabled()) {
      log.debug("Entering 'add' method");
    }

    User user = new User();
    user.addRole(new Role(Constants.USER_ROLE));
    UserForm userForm = (UserForm)convert(user);
    updateFormBean(mapping, request, userForm);

    //checkForRememberMeLogin(request);

    return mapping.findForward("edit");
  }

  public ActionForward cancel(ActionMapping mapping, ActionForm form,
          HttpServletRequest request, HttpServletResponse response)
          throws Exception {
    if(log.isDebugEnabled()) {
      log.debug("Entering 'cancel' method");
    }

    if(!StringUtils.equals(request.getParameter("from"), "list")) {
      return mapping.findForward("mainMenu");
    }
    else {
      return mapping.findForward("viewUsers");
    }
  }
/*
  public ActionForward delete(ActionMapping mapping, ActionForm form,
          HttpServletRequest request, HttpServletResponse response)
          throws Exception {
    if(log.isDebugEnabled()) {
      log.debug("Entering 'delete' method");
    }

    // Extract attributes and parameters we will need
    ActionMessages messages = new ActionMessages();
    UserForm userForm = (UserForm)form;

    // Exceptions are caught by ActionExceptionHandler
    UserManager mgr = (UserManager)getBean("userManager");
    mgr.removeUser(userForm.getId());

    messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
            "user.deleted", userForm.getFirstName() + ' '
                    + userForm.getLastName()));

    saveMessages(request.getSession(), messages);

    // return a forward to searching users
    return mapping.findForward("viewUsers");
  }
*/
  
  public ActionForward delete(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		if (log.isDebugEnabled()) {
			log.debug("Entering 'delete' method");
		}
		ActionMessages messages = new ActionMessages();
		String[] userIds = request.getParameterValues("rowDeleteId");
		UserManager mgr = (UserManager)getBean("userManager");
		ProjectManager projectManager = (ProjectManager)getBean("projectManager");
		ActionMessages errors = new ActionMessages();
		if (userIds == null) {
			
			errors.add("errors.detail", new ActionMessage(
					"errors.userNotSelected"));
			saveErrors(request, errors);
			return dispatchMethod(mapping, form, request, response, "search");
		}

			for (String userId : userIds) {
				if(Constants.SUPERADMIN_USER.equals(userId)) {
					errors.add("errors.detail", new ActionMessage(
					"errors.superadminCannotBeDeleted"));
				}
				else if(projectManager.getAvailableProjects(Long.parseLong(userId)).size() == 0){
				  mgr.removeUser(userId);
				}
				else {
					errors.add("errors.detail", new ActionMessage(
					"errors.userCannotBeDeleted", mgr.getUser(userId).getUsername()));
			
				}
			}
			if(errors.size()>0){
			  saveErrors(request, errors);
			  return dispatchMethod(mapping, form, request, response, "search");
			}
			messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
					"users.deleted"));
			saveMessages(request.getSession(), messages);
	
		return mapping.findForward("viewUsers");
	}
  
  
  public ActionForward enable(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		if (log.isDebugEnabled()) {
			log.debug("Entering 'enable' method");
		}
		ActionMessages messages = new ActionMessages();
		UserManager mgr = (UserManager)getBean("userManager");
		String superadminId = null;
		try {
			User superadmin = mgr.getUserByUsername(Constants.SUPERADMIN_USER);
			superadminId = String.valueOf(superadmin.getId());
		}
		catch(UsernameNotFoundException ex) {
			// do nothing, superadminId = null
		}
		String[] paramEnableArray = request.getParameterValues("rowEnableId");
		for (String userId : paramEnableArray) {
			if(!userId.equals(superadminId)) {
				log.debug("Enabling user: "+ userId);
				mgr.enableUser(userId, true);
			}
		}
		List<String> paramEnableList = Arrays.asList(paramEnableArray);
		
                String[] paramUserIdArray = request.getParameterValues("rowUserId");
		List<String> userIds = Arrays.asList(paramUserIdArray);
		Collection<String> toBeRemoved = CollectionUtils.subtract(userIds, paramEnableList);
		
		for (String userId : toBeRemoved) {
			if(!userId.equals(superadminId)) {
				log.debug("Disabling user: "+ userId);
				mgr.enableUser(userId, false);
			}
		}
		 messages.add(ActionMessages.GLOBAL_MESSAGE,new ActionMessage("users.updated"));
		    saveMessages(request.getSession(), messages);
	
		return mapping.findForward("viewUsers");
	}

  

  public ActionForward edit(ActionMapping mapping, ActionForm form,
          HttpServletRequest request, HttpServletResponse response)
          throws Exception {
    if(log.isDebugEnabled()) {
      log.debug("Entering 'edit' method");
    }

    UserForm userForm = (UserForm)form;

    // if URL is "editProfile" - make sure it's the current user
    if(request.getRequestURI().indexOf("editProfile") > -1) {
      // reject if username passed in or "list" parameter passed in
      // someone that is trying this probably knows the AppFuse code
      // but it's a legitimate bug, so I'll fix it. ;-)
      if((request.getParameter("username") != null)
              || (request.getParameter("from") != null)) {
        response.sendError(HttpServletResponse.SC_FORBIDDEN);
        log.warn("User '" + request.getRemoteUser()
                + "' is trying to edit user '"
                + request.getParameter("username") + "'");

        return null;
      }
    }

    // Exceptions are caught by ActionExceptionHandler
    UserManager mgr = (UserManager)getBean("userManager");
    User user = null;

    // if a user's username is passed in
    if(request.getParameter("username") != null) {
      // Paranoid check - superadmin user cannot be edited by any other user
      if(Constants.SUPERADMIN_USER.equals(userForm.getUsername())
              && !Constants.SUPERADMIN_USER.equals(request.getRemoteUser())) {
        response.sendError(HttpServletResponse.SC_FORBIDDEN);
        log.warn("User '" + request.getRemoteUser()
                + "' is trying to edit superadmin user!");
        return null;
      }
      // lookup the user using that id
      user = mgr.getUserByUsername(userForm.getUsername());
    }
    else {
      // look it up based on the current user's id
      user = mgr.getUserByUsername(request.getRemoteUser());
    }

    BeanUtils.copyProperties(userForm, convert(user));
    userForm.setConfirmPassword(userForm.getPassword());
    updateFormBean(mapping, request, userForm);

    checkForRememberMeLogin(request);

    // return a forward to edit forward
    return mapping.findForward("edit");
  }

  public ActionForward save(ActionMapping mapping, ActionForm form,
          HttpServletRequest request, HttpServletResponse response)
          throws Exception {
    if(log.isDebugEnabled()) {
      log.debug("Entering 'save' method");
    }

    // run validation rules on this form
    // See https://appfuse.dev.java.net/issues/show_bug.cgi?id=128
    ActionMessages errors = form.validate(mapping, request);

    if(!errors.isEmpty()) {
      saveErrors(request, errors);
      return mapping.findForward("edit");
    }

    // Extract attributes and parameters we will need
    ActionMessages messages = new ActionMessages();
    UserForm userForm = (UserForm)form;
    boolean editingSuperAdmin = Constants.SUPERADMIN_USER.equals(
        userForm.getUsername());

    // security check - only superadmin can edit himself
    if(editingSuperAdmin
	    && !Constants.SUPERADMIN_USER.equals(request.getRemoteUser())) {
      response.sendError(HttpServletResponse.SC_FORBIDDEN);
      log.warn("User '" + request.getRemoteUser()
	      + "' is trying to edit superadmin user!");
      return null;
    }

    User user = new User();

    // Exceptions are caught by ActionExceptionHandler
    // all we need to persist is the parent object
    BeanUtils.copyProperties(user, userForm);

    UserManager mgr = (UserManager)getBean("userManager");
    RoleManager roleMgr = (RoleManager)getBean("roleManager");

    if(StringUtils.equals(request.getParameter("encryptPass"), "true")) {
      mgr.setUserPassword(user, user.getPassword());
    }

    String[] userRoles = request.getParameterValues("userRoles");

    if(editingSuperAdmin) {
      // superadmin is hard-coded to be in the superadmin role
      user.addRole(roleMgr.getRole(Constants.SUPERADMIN_ROLE));
    }
    else {
      for(int i = 0; userRoles != null && i < userRoles.length; i++) {
        String roleName = userRoles[i];
        // don't allow adding any other user to superadmin role
        if(Constants.SUPERADMIN_ROLE.equals(roleName)) {
          log.warn("Attempt to add non-superadmin user to superadmin role");
          errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
                  "errors.addToSuperAdminRole", userForm.getUsername()));
          saveErrors(request, errors);
          return mapping.findForward("list");
        }
        user.addRole(roleMgr.getRole(roleName));
      }
    }

    try {
      mgr.saveUser(user);
//      sendNewUserEmail(request, userForm);
    }
    catch(UserExistsException e) {
      log.warn(e.getMessage());
      errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
              "errors.existing.user", userForm.getUsername(), userForm
                      .getEmail()));
      saveErrors(request, errors);

      BeanUtils.copyProperties(userForm, convert(user));
      userForm.setConfirmPassword(userForm.getPassword());
      // reset the version # to what was passed in
      userForm.setVersion(request.getParameter("version"));
      updateFormBean(mapping, request, userForm);

      return mapping.findForward("edit");
    }

    BeanUtils.copyProperties(userForm, convert(user));
    userForm.setConfirmPassword(userForm.getPassword());
    updateFormBean(mapping, request, userForm);

    if(!StringUtils.equals(request.getParameter("from"), "list")) {
      // add success messages
      messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
              "user.saved"));
      saveMessages(request.getSession(), messages);

      // return a forward to main Menu
      return mapping.findForward("mainMenu");
    }
    else {
      // add success messages
      if("".equals(request.getParameter("version"))) {
        messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
                "user.added", user.getFullName()));
        saveMessages(request.getSession(), messages);
        sendNewUserEmail(request, userForm);
        return mapping.findForward("addUser");
      }
      else {
        messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
                "user.updated.byAdmin", user.getFullName()));
        saveMessages(request, messages);

        return mapping.findForward("edit");
      }
    }
  }

  public ActionForward search(ActionMapping mapping, ActionForm form,
          HttpServletRequest request, HttpServletResponse response)
          throws Exception {
    if(log.isDebugEnabled()) {
      log.debug("Entering 'search' method");
    }

    UserForm userForm = (UserForm)form;

    // Exceptions are caught by ActionExceptionHandler
    UserManager mgr = (UserManager)getBean("userManager");
    List users = mgr.getUsers();
    request.setAttribute(Constants.USER_LIST, users);

    // return a forward to the user list definition
    return mapping.findForward("list");
  }
  
  /*
  public ActionForward enable(ActionMapping mapping, ActionForm form,
           HttpServletRequest request, HttpServletResponse response)
           throws Exception{
    if(log.isDebugEnabled()){
      log.debug("Entering 'enable' method in UserAction");
    }
    ActionMessages messages = new ActionMessages();
    UserManager mgr = (UserManager)getBean("userManager");
    List allUsers=mgr.getUsers(new User());
    Enumeration en= request.getParameterNames();
    while(en.hasMoreElements()){
      String userId = (String)en.nextElement();
      if(!userId.equals("method.enable")){
        String userEnabled = (String)request.getParameter(userId);
        User user=mgr.getUser(userId);
        allUsers.remove(user);
        if(userEnabled.equals("on")){
          mgr.enableUser(userId, true);
        }
      }
    }
    if(allUsers.size()!=0){
      for(int i=0;i<allUsers.size();i++){
        User u=(User)allUsers.get(i);
        mgr.enableUser(u.getId().toString(), false);
      }
    }
    messages.add(ActionMessages.GLOBAL_MESSAGE,new ActionMessage("users.updated"));
    saveMessages(request.getSession(),messages);
    return mapping.findForward("viewUsers");
  }
  */

  public ActionForward unspecified(ActionMapping mapping, ActionForm form,
          HttpServletRequest request, HttpServletResponse response)
          throws Exception {

    return search(mapping, form, request, response);
  }

  private void sendNewUserEmail(HttpServletRequest request, UserForm userForm)
          throws Exception {
    // Send user an e-mail
    if(log.isDebugEnabled()) {
      log.debug("Sending user '" + userForm.getUsername()
              + "' an account information e-mail");
    }
    EmailProducer mProducer = (EmailProducer)getBean("emailProducer");
	
    MessageResources resources = getResources(request);
    SimpleMailMessage message = (SimpleMailMessage)getBean("mailMessage");
    
    message.setTo(userForm.getFullName() + "<" + userForm.getEmail() + ">");

    message.setSubject(resources.getMessage("newuser.email.subject"));
    Map<String,String> model=new HashMap<String,String>();
    model.put("title",resources.getMessage("newuser.email.title"));
    model.put("content",resources.getMessage("newuser.email.content"));
    model.put("userName",userForm.getUsername());
    model.put("confirmPassword",request.getParameter("password"));
    model.put("applicationURL", 
    		request.getSession().getServletContext().getAttribute("urlbase").toString());
    
    mProducer.sendMessage(message,"accountAdded.vm",model);

  }

  private void checkForRememberMeLogin(HttpServletRequest request) {
    // if user logged in with remember me, display a warning that they
    // can't change passwords
    log.debug("checking for remember me login...");

    AuthenticationTrustResolver resolver = new AuthenticationTrustResolverImpl();
    SecurityContext ctx = SecurityContextHolder.getContext();

    if(ctx != null) {
      Authentication auth = ctx.getAuthentication();

      if(resolver.isRememberMe(auth)) {
        request.getSession().setAttribute("cookieLogin", "true");

        // add warning message
        ActionMessages messages = new ActionMessages();
        messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
                "userProfile.cookieLogin"));
        saveMessages(request, messages);
      }
    }
  }
}
