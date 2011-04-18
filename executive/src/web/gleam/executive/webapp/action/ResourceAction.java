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
import gleam.executive.service.ServiceManager;
import gleam.executive.service.data.ResourceDefinitionSource;
import gleam.executive.webapp.form.ResourceForm;
import gleam.executive.webapp.form.RoleForm;

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
 * <a href="ResourceAction.java.html"><i>View Source</i></a>
 * </p>
 *
 * @author <a href="mailto:agaton@dcs.shef.ac.uk">Milan Agatonovic</a>
 *
 * @struts.action name="resourceForm" path="/resources" scope="request"
 *                validate="false" parameter="method" input="mainMenu"
 * @struts.action name="resourceForm" path="/editResource" scope="request"
 *                validate="false" parameter="method" input="list"
 * @struts.action name="resourceForm" path="/saveResource" scope="request"
 *                validate="false" parameter="method" input="edit"
 *
 * @struts.action-forward name="list" path="/WEB-INF/pages/resourceList.jsp"
 * @struts.action-forward name="edit" path="/WEB-INF/pages/resourceForm.jsp"
 */
public final class ResourceAction extends BaseAction {

  public ActionForward add(ActionMapping mapping, ActionForm form,
          HttpServletRequest request, HttpServletResponse response)
          throws Exception {
    if(log.isDebugEnabled()) {
      log.debug("Entering 'add resource' method");
    }

    Resource resource = new Resource();
    resource.addRole(new Role(Constants.USER_ROLE));
    ResourceForm resourceForm = (ResourceForm)convert(resource);
    updateFormBean(mapping, request, resourceForm);
    if(log.isDebugEnabled()) {
      log.debug("Leaving 'add resource' method");
    }
    return mapping.findForward("edit");
  }

  public ActionForward cancel(ActionMapping mapping, ActionForm form,
          HttpServletRequest request, HttpServletResponse response)
          throws Exception {
    if(log.isDebugEnabled()) {
      log.debug("Entering 'cancel resource' method");
    }

    if(!StringUtils.equals(request.getParameter("from"), "list")) {
      return mapping.findForward("viewResources");
    }
    else {
      return mapping.findForward("viewResources");
    }
  }

  public ActionForward delete(ActionMapping mapping, ActionForm form,
          HttpServletRequest request, HttpServletResponse response)
          throws Exception {
    if(log.isDebugEnabled()) {
      log.debug("Entering 'delete resource' method");
    }

    ActionMessages messages = new ActionMessages();
    ResourceForm resourceForm = (ResourceForm)form;
    ResourceManager mgr = (ResourceManager)getBean("resourceManager");

    try{
        mgr.removeResource(request.getParameter("id"));
        messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
          "resource.deleted", resourceForm.getUrl()));
        saveMessages(request.getSession(), messages);
        return mapping.findForward("viewResources");
    }catch(Exception e){
      log.debug("-------The resource to be deleted has serval roles related to-------");
      messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
              "errors.resource.taken", resourceForm.getUrl()));
      saveMessages(request.getSession(), messages);
      return mapping.findForward("viewResources");
    }
  }

  public ActionForward edit(ActionMapping mapping, ActionForm form,
          HttpServletRequest request, HttpServletResponse response)
          throws Exception {
    if(log.isDebugEnabled()) {
      log.debug("Entering 'edit resource' method");
    }

    ResourceForm resourceForm = (ResourceForm)form;
    ResourceManager mgr = (ResourceManager)getBean("resourceManager");
    Resource resource = mgr.getResource(request.getParameter("id"));

    if(request.getParameter("id") != null) {
      // lookup the resource using that id
      resource = mgr.getResource(resourceForm.getId());
      log.debug("Getting resource via resource Id "+resource.getId()+" "+resource.getUrl());
    }
    BeanUtils.copyProperties(resourceForm, convert(resource));
    updateFormBean(mapping, request, resourceForm);

    // return a forward to edit forward
    return mapping.findForward("edit");
  }

  public ActionForward save(ActionMapping mapping, ActionForm form,
          HttpServletRequest request, HttpServletResponse response)
          throws Exception {
    if(log.isDebugEnabled()) {
      log.debug("Entering 'resource save' method");
    }
    ActionMessages errors = form.validate(mapping, request);
    ActionMessages messages = new ActionMessages();
    ResourceForm resourceForm = (ResourceForm)form;
    boolean isNewResource = ("".equals(resourceForm.getId()));

    if(log.isDebugEnabled()) {
      log.debug("Saving resource: " + resourceForm);
    }

    Resource resource = new Resource();
    ResourceManager mgr = (ResourceManager)getBean("resourceManager");
    //Resource resource = (Resource)convert(resourceForm);
    BeanUtils.copyProperties(resource, resourceForm);
    ServiceManager serviceMgr = (ServiceManager)getBean("serviceManager");
    RoleManager roleMgr = (RoleManager)getBean("roleManager");
    String[] resourceRoles = request.getParameterValues("resourceRoles");
    for(int i = 0; resourceRoles != null && i < resourceRoles.length; i++) {
      String roleId = resourceRoles[i];
      resource.addRole(roleMgr.getRole(roleId));
    }
    String selectedServiceId = request.getParameter("service_id");
    resource.setService_id(new Long(selectedServiceId));
    resource.setService(serviceMgr.getService(selectedServiceId));
    try{
      mgr.saveResource(resource);
      FilterSecurityInterceptor interceptor=(FilterSecurityInterceptor)getBean("filterInvocationInterceptor");
      ResourceDefinitionSource rds =new ResourceDefinitionSource(mgr);
      interceptor.setObjectDefinitionSource(rds);
      if(isNewResource){
        messages.add(ActionMessages.GLOBAL_MESSAGE,new ActionMessage("resource.added"));
        //save messages in session to survive a redirect
        saveMessages(request.getSession(),messages);
        return mapping.findForward("viewResources");
      }else{
        messages.add(ActionMessages.GLOBAL_MESSAGE,new ActionMessage("resource.updated"));
        saveMessages(request.getSession(),messages);
        return mapping.findForward("viewResources");
      }
    }catch(Exception e){
      log.warn(e.getMessage());
      log.debug("------------Duplicate resource url------",e);
      errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
              "errors.existing.resource", resourceForm.getUrl()));
      saveErrors(request, errors);
      BeanUtils.copyProperties(resourceForm, convert(resource));
      updateFormBean(mapping, request, resourceForm);

      return mapping.findForward("edit");
    }

  }

  public ActionForward search(ActionMapping mapping, ActionForm form,
          HttpServletRequest request, HttpServletResponse response)
          throws Exception {
    if(log.isDebugEnabled()) {
      log.debug("Entering 'resource search' method");
    }

    ResourceForm resourceForm = (ResourceForm)form;

    // Exceptions are caught by ActionExceptionHandler
    ResourceManager mgr = (ResourceManager)getBean("resourceManager");
    Resource resource = (Resource)convert(resourceForm);
    List resources = mgr.getResources(resource);
    request.setAttribute(Constants.RESOURCE_LIST, resources);

    // return a forward to the resource list definition
    return mapping.findForward("list");
  }

  public ActionForward unspecified(ActionMapping mapping, ActionForm form,
          HttpServletRequest request, HttpServletResponse response)
          throws Exception {

    return search(mapping, form, request, response);
  }

}
