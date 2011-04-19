/*
 *  ProcessDefinitionPropertiesAction.java
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

import gleam.executive.model.User;
import gleam.executive.service.UserManager;
import gleam.executive.webapp.form.ProcessDefPropertiesForm;
import gleam.executive.workflow.manager.WorkflowManager;
import gleam.executive.workflow.model.SwimlaneBean;
import gleam.executive.workflow.model.TaskBean;
import gleam.executive.workflow.util.JPDLConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.taskmgmt.def.Swimlane;

//TODO Javadoc

/**
 * @author <a href="mailto:agaton@dcs.shef.ac.uk">Milan Agatonovic</a>
 *
 * @struts.action path="/processDefinitionProperties"
 *                name="processDefPropertiesForm" scope="request"
 *                validate="false" parameter="method" input="processDefinitionList"
 *
 * @struts.action-forward name="list"
 *                        path="/WEB-INF/pages/processDefinitionProperties.jsp"
 */
public class ProcessDefinitionPropertiesAction extends BaseAction {
  /**
   *
   * @param mapping
   * @param form
   * @param request
   * @param response
   * @return ActionForward
   * @throws Exception
   */
  public ActionForward list(ActionMapping mapping, ActionForm form,
          HttpServletRequest request, HttpServletResponse response)
          throws Exception {
    if(log.isDebugEnabled())
      log
              .debug("ProcessDefinitionPropertiesAction - Entering 'list process definition props.' method");
    ProcessDefPropertiesForm pdPropsForm = (ProcessDefPropertiesForm)form;
    String processDefinitionId = request.getParameter("id");
    if(log.isDebugEnabled())
      log.debug("processDefinitionId " + processDefinitionId);
    // Exceptions are caught by ActionExceptionHandler
    WorkflowManager mgr = (WorkflowManager)getBean("workflowManager");
    UserManager mgrUser = (UserManager)getBean("userManager");
    // Get all swimlanes in specified process definition
    List<SwimlaneBean> swimlanes = mgr.findSwimlanesByProcessDefinitionId(Long
            .parseLong(processDefinitionId));

    Iterator<SwimlaneBean> it = swimlanes.iterator();
    while(it.hasNext()){
      SwimlaneBean swimlane = it.next();
      List<User> inPooledActors = new ArrayList<User>();
      if(swimlane.getPooledActors()!=null&&!swimlane.getPooledActors().equals("")){
        String pooledActorsExpression = swimlane.getPooledActors();
        log.debug("pooledActorsExpression is "+pooledActorsExpression);
        String[] pooledActorsArray = pooledActorsExpression.split(",");
        for(int i=0;i<pooledActorsArray.length;i++){
          log.debug("The "+i +" annotator is "+pooledActorsArray[i]);
          User user = mgrUser.getUserByUsername(pooledActorsArray[i]);
          if(user!=null){
            inPooledActors.add(user);
          }
        }
      }
      if(swimlane.getActor()==null){
    	  swimlane.setActor("");
      }
      swimlane.setInPooledActors(inPooledActors);
    }

    Collections.sort(swimlanes, new Comparator(){
        public int compare(Object o1, Object o2){
          Object obj1 = (SwimlaneBean)o1;
          Object obj2 = (SwimlaneBean)o2;
          Object ob1 = ((SwimlaneBean)obj1).getId();
          Object ob2 = ((SwimlaneBean)obj2).getId();
          if(ob1 instanceof Comparable)
            return ((Comparable)ob1).compareTo(ob2);
          else{
            throw new RuntimeException("Non comparable!");
          }
        }

    });
    pdPropsForm.setSwimlanes(swimlanes);

    // pdPropertiesForm.setTasks(tasks);
    // request.setAttribute(Constants.TASK_LIST,tasks);
    ProcessDefinition pd = mgr.findProcessDefinition(Long
            .parseLong(processDefinitionId));
    request.setAttribute("processDefinition", pd.getName());
    request.setAttribute("processDefinitionId", processDefinitionId);
    // return a forward to the process instances list
    if(log.isDebugEnabled())
      log
              .debug("ProcessDefinitionPropertiesAction - Exit 'list process definition props.' method");
    return mapping.findForward("list");
  }

  public ActionForward save(ActionMapping mapping, ActionForm form,
          HttpServletRequest request, HttpServletResponse response)
          throws Exception {
    if(log.isDebugEnabled())
      log
              .debug("ProcessDefinitionPropertiesAction - Entering 'save process definition props.' method");
    String processDefinitionId = request.getParameter("processDefinitionId");
    // Exceptions are caught by ActionExceptionHandler
    WorkflowManager mgr = (WorkflowManager)getBean("workflowManager");
    ActionMessages errors = new ActionMessages();
    // Get all task in specified process definition
    List<SwimlaneBean> swimlanes = (List<SwimlaneBean>)mgr.findSwimlanesByProcessDefinitionId(Long
            .parseLong(processDefinitionId));
    Iterator<SwimlaneBean> it = swimlanes.iterator();
    while(it.hasNext()) {
      SwimlaneBean swimlane = it.next();
      String[] performers = request.getParameterValues("performer_"
              + String.valueOf(swimlane.getId()));
      // check if performers are set
      if(performers == null) {
        errors.add("errors.detail", new ActionMessage(
                "workflow.ProcessDefinition.noPerformers.error"));
        saveErrors(request, errors);
        ActionForward actionForward = mapping
                .findForward("processDefinitionProperties");
        ActionForward newActionForward = new ActionForward(actionForward);
        newActionForward.setPath(actionForward.getPath() + "&id="
                + processDefinitionId);
        return newActionForward;
      }
      if(performers.length > 1) {
        StringBuffer pooledActros = new StringBuffer();
        for(int i = 0; i < performers.length - 1; i++) {
          pooledActros.append(performers[i]);
          pooledActros.append(",");
        }
        pooledActros.append(performers[performers.length - 1]);
        swimlane.setPooledActors(pooledActros.toString());
        swimlane.setActor(null);
      }
      else {
    	  swimlane.setPooledActors(performers[0]);
    	  swimlane.setActor(performers[0]);
      }

    }
    String key = request.getParameter("key");
    log.debug("key: "+key);
    mgr.updateProcessDefinition(Long.parseLong(processDefinitionId), swimlanes);
    //mgr.redeployProcessDefinition(mgr.findProcessDefinition(Long.parseLong(processDefinitionId)),tasksClone);
	Map<String, Object> variableMap = new HashMap<String, Object>();
	variableMap.put(JPDLConstants.INITIATOR, request.getRemoteUser());
    mgr.createStartProcessInstance(Long.parseLong(processDefinitionId), variableMap, key);

    if(log.isDebugEnabled()) log.debug(" process instance started ");
    ActionMessages messages = new ActionMessages();
    messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
            "workflow.ProcessDefinition.started"));
    saveMessages(request.getSession(), messages);
    // return a forward to the process instances list
    if(log.isDebugEnabled())
      log.debug("ProcessDefinitionPropertiesAction - Exit 'save process definition props.' method");
    //return mapping.findForward("processDefinitionList");
    ActionForward actionForward = mapping.findForward("processInstanceListForProcessDefinition");
    ActionForward newActionForward = new ActionForward(actionForward);
    newActionForward.setPath(actionForward.getPath() + "&id="
            + processDefinitionId);
    newActionForward.setRedirect(true);
    return newActionForward;
  }
}
