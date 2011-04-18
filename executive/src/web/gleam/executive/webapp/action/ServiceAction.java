package gleam.executive.webapp.action;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import gleam.executive.Constants;
import gleam.executive.model.Resource;
import gleam.executive.model.Service;
import gleam.executive.service.ResourceManager;
import gleam.executive.service.ServiceManager;
import gleam.executive.service.RoleManager;
import gleam.executive.webapp.form.ServiceForm;

/**
 * Implementation of <strong>Action</strong> that interacts with the
 * {@link ServiceForm} and retrieves values. It interacts with the {@link
 * RoleManager} to retrieve/persist values to the database.
 * Copyright (c) 1998-2007, The University of Sheffield.
 *
 * This file is part of GATE (see http://gate.ac.uk/), and is free
 * software, licenced under the GNU Library General Public License,
 * Version 2, June 1991 (in the distribution as file licence.html, and
 * also available at http://gate.ac.uk/gate/licence.html).
 * <p>
 * <a href="ServiceAction.java.html"><i>View Source</i></a>
 * </p>
 *
 *
 * @struts.action name="serviceForm" path="/services" scope="request"
 *                validate="false" parameter="method" input="mainMenu"
 * @struts.action name="serviceForm" path="/saveService" scope="request"
 *                validate="false" parameter="method" input="list"
 *
 * @struts.action-forward name="list" path="/WEB-INF/pages/serviceList.jsp"
 */
public final class ServiceAction extends BaseAction {

  public ActionForward save(ActionMapping mapping, ActionForm form,
          HttpServletRequest request, HttpServletResponse response)
          throws Exception {
    if(log.isDebugEnabled()) {
      log.debug("Entering 'service save' method");
    }
    ActionMessages messages = new ActionMessages();

    ServiceManager mgr = (ServiceManager)getBean("serviceManager");
    ResourceManager resourceMgr = (ResourceManager)getBean("resourceManager");

    Enumeration en= request.getParameterNames();
    Service serv = new Service();

    List allServices = mgr.getServices(serv);
    while(en.hasMoreElements()){
      String serviceId=(String)en.nextElement();
      String serviceEnabled = (String)request.getParameter(serviceId);

      if(serviceEnabled.equals("on")){
        Service service = mgr.getService(serviceId);
        service.setEnabled(true);
        mgr.saveService(service);
        allServices.remove(service);
      }
    }

    if(allServices.size()!=0){
      for(int i=0;i<allServices.size();i++){
        Service se = (Service)allServices.get(i);
        se.setEnabled(false);
        mgr.saveService(se);
        List resources = resourceMgr.getResourcesWithService(se.getId().toString());
        Iterator it = resources.iterator();
        while(it.hasNext()){
          Resource resource = (Resource)it.next();
          resource.setRoles(null);
          resourceMgr.saveResource(resource);
        }
      }

    }
    messages.add(ActionMessages.GLOBAL_MESSAGE,new ActionMessage("service.updated"));
    saveMessages(request.getSession(),messages);
    return mapping.findForward("viewServices");
  }

  public ActionForward search(ActionMapping mapping, ActionForm form,
          HttpServletRequest request, HttpServletResponse response)
          throws Exception {
    if(log.isDebugEnabled()) {
      log.debug("Entering 'service search' method");
    }

    ServiceForm serviceForm = (ServiceForm)form;

    // Exceptions are caught by ActionExceptionHandler
    ServiceManager mgr = (ServiceManager)getBean("serviceManager");
    Service service = (Service)convert(serviceForm);
    List services = mgr.getServices(service);
    request.setAttribute(Constants.SERVICE_LIST, services);

    // return a forward to the service list definition
    return mapping.findForward("list");
  }

  public ActionForward unspecified(ActionMapping mapping, ActionForm form,
          HttpServletRequest request, HttpServletResponse response)
          throws Exception {

    return search(mapping, form, request, response);
  }

}
