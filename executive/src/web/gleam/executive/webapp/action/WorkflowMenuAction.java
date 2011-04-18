package gleam.executive.webapp.action;

import gleam.executive.Constants;
import gleam.executive.service.ResourceManager;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.util.StringUtils;

/**
 * Copyright (c) 1998-2006, The University of Sheffield.
 *
 * This file is part of GATE (see http://gate.ac.uk/), and is free
 * software, licenced under the GNU Library General Public License,
 * Version 2, June 1991 (in the distribution as file licence.html, and
 * also available at http://gate.ac.uk/gate/licence.html).
 * <p>
 * <a href="WorkflowMenuAction.java.html"><i>View Source</i></a>
 * </p>
 *
 * @author <a href="mailto:agaton@dcs.shef.ac.uk">Milan Agatonovic</a>
 * 
 * @struts.action path="/workflowMenu" validate="false"
 * @struts.action-forward name="workflowLinks" path="/WEB-INF/pages/workflowMenu.jsp" redirect="false"
 */
public class WorkflowMenuAction extends BaseAction{
  public ActionForward execute(ActionMapping mapping, ActionForm form,
          HttpServletRequest request, HttpServletResponse response)
          throws Exception {

    /*
	WorkflowManager wfMgr = (WorkflowManager)getBean("workflowManager");
    List tasks = wfMgr.findTaskInstances(request.getRemoteUser());
    int pendingTasksNo = tasks.size();
	log.debug("The size of pending tasks is "+pendingTasksNo);
	request.setAttribute("pendingTasksNo",pendingTasksNo);  
    */
    ResourceManager resourceManager = (ResourceManager)getBean("resourceManager");
    
    // processInstanceManagingRoles
    List processInstanceManagingRoles = resourceManager.getRolesWithResource(Constants.PROCESS_INSTANCE_PATTERN);
    String processInstanceManagingRolesString = StringUtils.collectionToCommaDelimitedString(processInstanceManagingRoles);
    log.debug("processInstanceManagingRoles "+processInstanceManagingRoles);
    request.setAttribute("processInstanceManagingRoles",processInstanceManagingRolesString);
    
    // processInstancesUsingRoles
    /*
    List processInstancesUsingRoles = resourceManager.getRolesWithResource(Constants.USER_PROCESS_INSTANCE_PATTERN);
    String processInstancesUsingRolesString = StringUtils.collectionToCommaDelimitedString(processInstancesUsingRoles);
    log.debug("processInstancesUsingRoles "+processInstancesUsingRoles);
    request.setAttribute("processInstancesUsingRoles",processInstancesUsingRolesString);
    */
    //  processInstanceManagingRoles
    List processDefinitionManagingRoles = resourceManager.getRolesWithResource(Constants.PROCESS_DEFINITION_PATTERN);
    String processDefinitionManagingRolesString = StringUtils.collectionToCommaDelimitedString(processDefinitionManagingRoles);
    log.debug("processDefinitionManagingRoles "+processDefinitionManagingRoles);
    request.setAttribute("processDefinitionManagingRoles",processDefinitionManagingRolesString);
    
    //  taskInstanceExecutingRoles
    List taskInstanceExecutingRoles = resourceManager.getRolesWithResource(Constants.TASKS_PATTERN);
    String taskInstanceExecutingRolesString = StringUtils.collectionToCommaDelimitedString(taskInstanceExecutingRoles);
    log.debug("taskInstanceExecutingRoles "+taskInstanceExecutingRoles);
    request.setAttribute("taskInstanceExecutingRoles",taskInstanceExecutingRolesString);
    
    // projectRoles
    List projectRoles = resourceManager.getRolesWithResource(Constants.PROJECTS_PATTERN);
    String projectRolesString = StringUtils.collectionToCommaDelimitedString(projectRoles);
    log.debug("projectRoles "+projectRoles);
    request.setAttribute("projectRoles",projectRolesString);
    
    // projectUsingRoles
    /*
    List projectUsingRoles = resourceManager.getRolesWithResource(Constants.USER_PROJECTS_ACCESS_PATTERN);
    String projectUsingRolesString = StringUtils.collectionToCommaDelimitedString(projectUsingRoles);
    log.debug("projectUsingRoles "+projectUsingRoles);
    request.setAttribute("projectUsingRoles",projectUsingRolesString);
    */
    return mapping.findForward("workflowLinks");
  }
}

