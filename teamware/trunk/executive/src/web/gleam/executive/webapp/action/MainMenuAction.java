/*
 *  MainMenuAction.java
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
 * Milan Agatonovic
 *
 *  $Id$
 */
package gleam.executive.webapp.action;

import gleam.executive.Constants;
import gleam.executive.model.AnnotatorGUILaunchBean;
import gleam.executive.model.Role;
import gleam.executive.model.User;
import gleam.executive.service.DocServiceManager;
import gleam.executive.service.ResourceManager;
import gleam.executive.service.UserManager;
import gleam.executive.workflow.manager.WorkflowManager;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.util.StringUtils;

/**
 * @struts.action path="/mainMenu" validate="false"
 * 
 * @struts.action-forward name="displayMainMenu"
 *                        path="/WEB-INF/pages/mainMenu.jsp" redirect="false"
 */
public class MainMenuAction extends BaseAction {
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		// obtain user roles
		String username = request.getRemoteUser();
		UserManager userManager = (UserManager) getBean("userManager");

		User user = userManager.getUserByUsername(username);
		Set<Role> roles = user.getRoles();
		String roleString = StringUtils.collectionToCommaDelimitedString(roles);
		log.debug("roleString " + roleString);
		ResourceManager resourceManager = (ResourceManager) getBean("resourceManager");
		WorkflowManager wfMgr = (WorkflowManager) getBean("workflowManager");

		boolean display = false;
		// annotatorRoles
		List annotatorRoles = resourceManager
				.getRolesWithResource(Constants.ANNOTATOR_GUI_URL_PATTERN);
		String annotatorRoleString = StringUtils
				.collectionToCommaDelimitedString(annotatorRoles);
		log.debug("annotatorRoleString " + annotatorRoleString);
		request.setAttribute("annotatorRoles", annotatorRoleString);
		display = roleMatch(roles, annotatorRoles);
		if (display) {

			DocServiceManager docMgr = (DocServiceManager) getBean("docServiceManager");
			String annotatorGUIURL = docMgr.getPoolModeAnnotatorGUIURL();
			log.debug("annotatorGUIURL " + annotatorGUIURL);
			request.setAttribute("annotatorGUIURL", annotatorGUIURL);

		}
		// projectAccessRoles
		List projectAccessRoles = resourceManager
				.getRolesWithResource(Constants.PROJECTS_PATTERN);
		String projectAccessRolesString = StringUtils
				.collectionToCommaDelimitedString(projectAccessRoles);
		log.debug("projectAccessRolesString " + projectAccessRolesString);
		request.setAttribute("projectAccessRoles", projectAccessRolesString);
		display = roleMatch(roles, projectAccessRoles);
		if (display) {

			List configurationSteps = wfMgr
					.findPendingConfigurationOptionsByActorId(username);
			int configurationStepsNo = configurationSteps.size();
			log.debug("The size of configuration steps is "
					+ configurationStepsNo);
			request.setAttribute("configurationStepsNo", configurationStepsNo);

		}
		// workflowAccessRoles
		List workflowAccessRoles = resourceManager
				.getRolesWithResource(Constants.WORKFLOW_ACCESS_PATTERN);

		String workflowAccessRolesString = StringUtils
				.collectionToCommaDelimitedString(workflowAccessRoles);
		log.debug("workflowAccessRolesString " + workflowAccessRolesString);
		request.setAttribute("workflowAccessRoles", workflowAccessRolesString);
		display = roleMatch(roles, workflowAccessRoles);
		if (display) {

			List tasks = wfMgr.findPendingTaskInstancesByActorId(request.getRemoteUser());
			List pooledTasks = wfMgr.findPooledTaskInstances(request
					.getRemoteUser());
			int pendingTasksNo = tasks.size();
			int groupTasksNo = pooledTasks.size();
			log.debug("The size of pending tasks is " + pendingTasksNo);
			log.debug("The size of group open tasks is " + groupTasksNo);
			request.setAttribute("pendingTasksNo", pendingTasksNo);
			request.setAttribute("groupTasksNo", groupTasksNo);
		}

		return mapping.findForward("displayMainMenu");
	}

	boolean roleMatch(Collection<Role> roles, Collection<Role> accessRoles) {
		boolean flag = false;
		Iterator<Role> it1 = roles.iterator();
		while (it1.hasNext() && !flag) {
			Role role1 = it1.next();
			Iterator<Role> it2 = accessRoles.iterator();
			while (it2.hasNext()) {
				Role role2 = it2.next();
				if (role1.getName().equals(role2.getName())) {
					flag = true;
					continue;
				}
			}
		}
		return flag;
	}
}