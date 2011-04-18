package gleam.executive.webapp.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acegisecurity.intercept.web.FilterSecurityInterceptor;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import gleam.executive.Constants;
import gleam.executive.model.Resource;
import gleam.executive.model.Role;
import gleam.executive.service.ResourceManager;
import gleam.executive.service.RoleManager;
import gleam.executive.service.UserManager;
import gleam.executive.service.data.ResourceDefinitionSource;
import gleam.executive.webapp.form.RoleForm;
import gleam.executive.webapp.listener.StartupListener;

/**
 * Implementation of <strong>Action</strong> that interacts with the
 * {@link RoleForm} and retrieves values. It interacts with the {@link
 * RoleManager} to retrieve/persist values to the database.
 * Copyright (c) 1998-2007, The University of Sheffield.
 *
 * This file is part of GATE (see http://gate.ac.uk/), and is free
 * software, licenced under the GNU Library General Public License,
 * Version 2, June 1991 (in the distribution as file licence.html, and
 * also available at http://gate.ac.uk/gate/licence.html).
 * <p>
 * <a href="RoleAction.java.html"><i>View Source</i></a>
 * </p>
 *
 *
 * @struts.action name="roleForm" path="/roles" scope="request"
 *                validate="false" parameter="method" input="mainMenu"
 * @struts.action name="roleForm" path="/editRole" scope="request"
 *                validate="false" parameter="method" input="list"
 * @struts.action name="roleForm" path="/saveRole" scope="request"
 *                validate="false" parameter="method" input="edit"
 *
 * @struts.action-forward name="list" path="/WEB-INF/pages/roleList.jsp"
 * @struts.action-forward name="edit" path="/WEB-INF/pages/roleForm.jsp"
 */
public final class RoleAction extends BaseAction {

  public ActionForward add(ActionMapping mapping, ActionForm form,
          HttpServletRequest request, HttpServletResponse response)
          throws Exception {
    if(log.isDebugEnabled()) {
      log.debug("Entering 'add Role' method");
    }

    Role role = new Role();

    RoleForm roleForm = (RoleForm)convert(role);
    updateFormBean(mapping, request, roleForm);

    return mapping.findForward("edit");
  }

  public ActionForward cancel(ActionMapping mapping, ActionForm form,
          HttpServletRequest request, HttpServletResponse response)
          throws Exception {
    if(log.isDebugEnabled()) {
      log.debug("Entering 'cancel Role' method");
    }

    if(!StringUtils.equals(request.getParameter("from"), "list")) {
      return mapping.findForward("viewRoles");
    }
    else {
      return mapping.findForward("viewRoles");
    }
  }

  public ActionForward delete(ActionMapping mapping, ActionForm form,
          HttpServletRequest request, HttpServletResponse response)
          throws Exception {
    if(log.isDebugEnabled()) {
      log.debug("Entering 'delete Role' method");
    }

    ActionMessages messages = new ActionMessages();
    RoleForm roleForm = (RoleForm)form;
    RoleManager roleManager = (RoleManager)getBean("roleManager");
    ResourceManager resourceManager = (ResourceManager)getBean("resourceManager");
    log.debug("The name of roleForm is "+roleForm.getName());
    if(Constants.SUPERADMIN_ROLE.equals(roleForm.getName())) {
      log.warn("Attempt to delete superadmin role!");
      messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
              "errors.role.delete", roleForm.getName()));
      saveMessages(request.getSession(), messages);
      return mapping.findForward("viewRoles");
    }
    UserManager userManager = (UserManager)getBean("userManager");
    List users=(List)userManager.getUsersWithRole(roleForm.getName());
    log.debug("----------The number of users that has this role is "+users.size());
    List resources = resourceManager.getResourcesWithRole(roleForm.getName());
    log.debug("----------The number of resources that has this role is "+resources.size());
    try{
      if(users.size()==0 && resources.size()==0){
    	  roleManager.removeRole(roleForm.getName());
        messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
          "role.deleted", roleForm.getName() ));
        saveMessages(request.getSession(), messages);
        return mapping.findForward("viewRoles");
      }else if(users.size()>0) {
        log.debug("-------The role to be deleted has several users related to-------");
        messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
                "errors.role.taken", roleForm.getName()));
        saveMessages(request.getSession(), messages);
        return mapping.findForward("viewRoles");
      }
      else if(resources.size()>0) {
          log.debug("-------The role to be deleted has several resources related to-------");
          messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
                  "errors.role.assignedToResource", roleForm.getName()));
          saveMessages(request.getSession(), messages);
          return mapping.findForward("viewRoles");
        }
      else{
    	  log.debug("-------The role cannot be deleted ");
          messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
                  "errors.role.delete", roleForm.getName()));
          saveMessages(request.getSession(), messages);
          return mapping.findForward("viewRoles");
      }
    }catch(Exception e){
      log.debug("-------The role cannot be deleted because of exception "+e.getMessage());
      messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
              "errors.role.delete", roleForm.getName()));
      saveMessages(request.getSession(), messages);
      return mapping.findForward("viewRoles");
    }
  }

  public ActionForward edit(ActionMapping mapping, ActionForm form,
          HttpServletRequest request, HttpServletResponse response)
          throws Exception {
    if(log.isDebugEnabled()) {
      log.debug("Entering 'edit Role' method");
    }

    RoleForm roleForm = (RoleForm)form;
    RoleManager mgr = (RoleManager)getBean("roleManager");
    Role role = mgr.getRole(roleForm.getId());
    log.debug("The id of role is "+roleForm.getId());
    ResourceManager resourceMgr =(ResourceManager)getBean("resourceManager");
    List roleResources = (List)resourceMgr.getResourcesWithRole(roleForm.getName());
    log.debug("The length of roleResources is "+roleResources.size());

    if(request.getParameter("name") != null) {
      // lookup the role using that name
      role = mgr.getRoleByRolename(roleForm.getName());
    }
    BeanUtils.copyProperties(roleForm, convert(role));
    updateFormBean(mapping, request, roleForm);

    // return a forward to edit forward
    return mapping.findForward("edit");
  }

  public ActionForward save(ActionMapping mapping, ActionForm form,
          HttpServletRequest request, HttpServletResponse response)
          throws Exception {
    if(log.isDebugEnabled()) {
      log.debug("Entering 'Role save' method");
    }
    ActionMessages errors = form.validate(mapping, request);
    ActionMessages messages = new ActionMessages();
    RoleForm roleForm = (RoleForm)form;
    boolean isNewRole = ("".equals(roleForm.getId()));

    if(log.isDebugEnabled()) {
      log.debug("Saving role: " + roleForm);
      //log.debug("The size of resources in roleForm is "+roleForm.getResources().size());
    }
    String oldRoleName =request.getParameter("oldRoleName");
    log.debug("oldRoleName is "+oldRoleName);
    RoleManager mgr = (RoleManager)getBean("roleManager");

    Role role = (Role)convert(roleForm);
    ResourceManager resourceMgr =(ResourceManager)getBean("resourceManager");
    List roleResources = (List)resourceMgr.getResourcesWithRole(oldRoleName);
    for(int i = 0; roleResources != null && i < roleResources.size(); i++) {
      Resource resource=(Resource)roleResources.get(i);
      log.debug("The"+i+" resource "+resource.getUrl()+" related to current role "+role.getName());
      role.addResource(resource);
    }

    try{
      mgr.saveRole(role);
      FilterSecurityInterceptor interceptor=(FilterSecurityInterceptor)getBean("filterInvocationInterceptor");
      ResourceDefinitionSource rds =new ResourceDefinitionSource(resourceMgr);
      interceptor.setObjectDefinitionSource(rds);
      //Reload the drop-downs initialized in the StartupListener.
      StartupListener.setupContext(getServlet().getServletContext());
      if(isNewRole){
        messages.add(ActionMessages.GLOBAL_MESSAGE,new ActionMessage("role.added"));
        //save messages in session to survive a redirect
        saveMessages(request.getSession(),messages);
        return mapping.findForward("viewRoles");
      }else{
        messages.add(ActionMessages.GLOBAL_MESSAGE,new ActionMessage("role.updated"));
        saveMessages(request.getSession(),messages);
        return mapping.findForward("viewRoles");
      }
    }catch(Exception e){
      log.warn(e.getMessage());
      log.debug("------------Duplicate rolename------");
      errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
              "errors.existing.role", roleForm.getName()));
      saveErrors(request, errors);
      BeanUtils.copyProperties(roleForm, convert(role));
      updateFormBean(mapping, request, roleForm);

      return mapping.findForward("edit");
    }

  }

  public ActionForward search(ActionMapping mapping, ActionForm form,
          HttpServletRequest request, HttpServletResponse response)
          throws Exception {
    if(log.isDebugEnabled()) {
      log.debug("Entering 'Role search' method");
    }

    RoleForm roleForm = (RoleForm)form;

    // Exceptions are caught by ActionExceptionHandler
    RoleManager mgr = (RoleManager)getBean("roleManager");
    Role role = (Role)convert(roleForm);
    List roles = mgr.getRoles(role);
    request.setAttribute(Constants.ROLE_LIST, roles);

    // return a forward to the role list definition
    return mapping.findForward("list");
  }

  public ActionForward unspecified(ActionMapping mapping, ActionForm form,
          HttpServletRequest request, HttpServletResponse response)
          throws Exception {

    return search(mapping, form, request, response);
  }

}
