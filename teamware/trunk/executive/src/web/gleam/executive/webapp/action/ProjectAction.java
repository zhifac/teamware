/*
 *  ProjectAction.java
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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.upload.FormFile;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.taskmgmt.exe.TaskInstance;

import gleam.executive.model.Corpus;
import gleam.executive.Constants;
import gleam.executive.model.LabelValue;
import gleam.executive.model.Project;
import gleam.executive.model.User;
import gleam.executive.model.WebAppBean;
import gleam.executive.service.DocServiceManager;
import gleam.executive.service.ProjectManager;
import gleam.executive.service.UserManager;
import gleam.executive.webapp.form.ProjectForm;
import gleam.executive.workflow.manager.WorkflowManager;
import gleam.executive.workflow.util.JPDLConstants;

/**
 * Implementation of <strong>Action</strong> that interacts with the
 * {@link ProjectForm} and retrieves values. It interacts with the
 * {@link ProjectManager} to retrieve/persist values to the database.
 * 
 * @struts.action name="projectForm" path="/projects" scope="request"
 *                validate="false" parameter="method" input="mainMenu"
 * @struts.action name="projectForm" path="/editProject" scope="request"
 *                validate="false" parameter="method" input="list"
 * @struts.action name="projectForm" path="/saveProject" scope="request"
 *                validate="true" parameter="method" input="edit"
 * @struts.action name="processInstanceForm" path="/loadProject" scope="request"
 *                validate="false" parameter="method" input="list"
 * @struts.action-set-property property="cancellable" value="true"
 * @struts.action-forward name="list" path="/WEB-INF/pages/projectList.jsp"
 * @struts.action-forward name="edit" path="/WEB-INF/pages/projectForm.jsp"
 * @struts.action-forward name="show"
 *                        path="/WEB-INF/pages/projectInstanceForm.jsp"
 * @struts.action-forward name="load" path="/WEB-INF/pages/projectLoad.jsp"
 */
public final class ProjectAction extends BaseAction {

	/*
	 * redirects to create project form
	 */
	public ActionForward add(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String userName = request.getRemoteUser();
		if (log.isDebugEnabled()) {
			log.debug("Entering 'add Project' method - user: " + userName);
		}

		Project project = new Project();

		ProjectForm projectForm = (ProjectForm) convert(project);
		updateFormBean(mapping, request, projectForm);

		return mapping.findForward("edit");
	}

	/*
	 * redirects to projects list
	 */
	public ActionForward cancel(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		if (log.isDebugEnabled()) {
			log.debug("Entering 'cancel Project' method");
		}
		return mapping.findForward("viewAllProjects");

	}

	/*
	 * deletes selected project and redirects to project list
	 */
	public ActionForward delete(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		if (log.isDebugEnabled()) {
			log.debug("Entering 'delete Project' method");
		}

		ActionMessages messages = new ActionMessages();
		String projectId = request.getParameter("id");
		String forward = request.getParameter("forward");
		if (log.isDebugEnabled()) {
			log.debug("projectId: " + projectId);
			log.debug("forward: " + forward);
		}
		ProjectManager projectManager = (ProjectManager) getBean("projectManager");
		// fetch all process instances related to configuration of this project
		Project project = projectManager.getProject(new Long(projectId));
		String projectName = project.getName();
		WorkflowManager workflowManager = (WorkflowManager) getBean("workflowManager");
		WebAppBean webAppBean = (WebAppBean) getBean("webAppBean");
		List<ProcessInstance> setupProcessInstances = workflowManager
				.findProcessInstancesExcludingSubProcessInstancesByKeyAndName(
						projectName, Constants.SETUP_PROCESS_DEFINITION_NAME);
		List<ProcessInstance> mainProcessInstances = workflowManager
				.findProcessInstancesExcludingSubProcessInstancesByKeyAndName(
						projectName, Constants.MAIN_PROCESS_DEFINITION_NAME);

		try {
			Iterator<ProcessInstance> it = setupProcessInstances.iterator();
			while (it.hasNext()) {
				ProcessInstance pi = it.next();
				workflowManager.cancelProcessInstance(pi.getId());
				log.debug("deleted setup process instance: " + pi.getId());
			}
			Iterator<ProcessInstance> itr = mainProcessInstances.iterator();
			while (itr.hasNext()) {
				ProcessInstance pin = itr.next();
				workflowManager.cancelProcessInstance(pin.getId());
				log.debug("deleted main process instance: " + pin.getId());
			}
			projectManager.removeProject(new Long(projectId));
			messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
					"project.deleted", projectId));
			saveMessages(request.getSession(), messages);
			return mapping.findForward(forward);

		} catch (Exception e) {
			log
					.debug("-------The project cannot be deleted because of exception "
							+ e.getMessage());
			messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
					"errors.project.delete", projectId));
			saveMessages(request.getSession(), messages);
			return mapping.findForward(forward);
		}
	}

	/*
	 * edits selected project and redirects to project form
	 */
	public ActionForward edit(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String userName = request.getRemoteUser();
		if (log.isDebugEnabled()) {
			log.debug("Entering 'edit Project' method - user: " + userName);
		}
		UserManager userManager = (UserManager) getBean("userManager");
		ProjectForm projectForm = (ProjectForm) form;
		String projectId = request.getParameter("id");
		if (log.isDebugEnabled()) {
			log.debug("projectId: " + projectId);
		}
		ProjectManager mgr = (ProjectManager) getBean("projectManager");
		Project project = mgr.getProject(new Long(projectId));

		if (log.isDebugEnabled()) {
			log.debug("project name: " + project.getName() + " ;version: "
					+ project.getVersion());
		}

		BeanUtils.copyProperties(projectForm, convert(project));
		log.debug("projectform name version: " + projectForm.getVersion());
		projectForm.setVersion(project.getVersion());
		request.setAttribute(Constants.MANAGER_LIST, userManager
				.getUsersWithRole(Constants.MANAGER_ROLE));
		updateFormBean(mapping, request, projectForm);
		// return a forward to edit forward
		return mapping.findForward("edit");
	}

	/*
	 * saves selected project and redirects to project form
	 */

	public ActionForward save(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		//String userName = request.getRemoteUser();
		/*
		if (log.isDebugEnabled()) {
			log.debug("Entering 'save Project' method - user: " + userName);
		}
		*/
		ActionMessages errors = form.validate(mapping, request);
		ActionMessages messages = new ActionMessages();
		ProjectForm projectForm = (ProjectForm) form;
		boolean isNewProject = ("".equals(projectForm.getId()));

		if (log.isDebugEnabled()) {
			log.debug("Saving project: " + projectForm);
		}

		String oldProjectName = request.getParameter("oldProjectName");
		log.debug("oldProjectName is " + oldProjectName);
		ProjectManager mgr = (ProjectManager) getBean("projectManager");

		Project project = (Project) convert(projectForm);
		log.debug("version: " + project.getVersion());
		log.debug("enabled: " + project.isEnabled());
		log.debug("userId: " + project.getUserId());
		// check if the project with that name already exists.
		ProjectManager projectManager = (ProjectManager) getBean("projectManager");

		if (!projectForm.getName().equals(oldProjectName)
				&& projectManager.getProjectByName(projectForm.getName()) != null) {
			log.debug("------------Duplicate projectname------");
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
					"errors.existing.project", projectForm.getName()));
			saveErrors(request, errors);
			return mapping.findForward("edit");
		}
		  
		
		FormFile file = projectForm.getFile();
		byte[] oldData = projectManager.getProjectByName(oldProjectName)
				.getData();
		if (file == null || file.getFileSize() == 0
				|| file.getFileData().length == 0) {
			log.debug("template file is empty!!");
			project.setData(oldData);
		} else {

			// retrieve the file data
			byte[] data = file.getFileData();
			log.debug("data length: " + data.length);

			project.setData(data);
		}
		project.setLastUpdate(new Date());
		// project.setEnabled(true);

		try {
			mgr.saveProject(project);
			if (isNewProject) {
				messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
						"project.added"));
				// save messages in session to survive a redirect
				saveMessages(request.getSession(), messages);
				return mapping.findForward("viewAllProjects");
			} else {
				messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
						"project.updated"));
				saveMessages(request.getSession(), messages);
				return mapping.findForward("viewAllProjects");
			}
		} catch (Exception e) {
			log.warn(e.getMessage());

			BeanUtils.copyProperties(projectForm, convert(project));
			updateFormBean(mapping, request, projectForm);

			return mapping.findForward("edit");
		}

	}

	/*
	 * list projects for user and redirects to project list
	 */
	public ActionForward search(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		throw new Exception("Invalid method");
		/*
		String userName = request.getRemoteUser();
		if (log.isDebugEnabled()) {
			log.debug("Entering 'search Project' method - user: " + userName);
		}

		UserManager userManager = (UserManager) getBean("userManager");
		User user = userManager.getUserByUsername(userName);

		ProjectManager mgr = (ProjectManager) getBean("projectManager");

		List projects = mgr.getProjectsByUserId(user.getId());
		request.setAttribute(Constants.PROJECT_LIST, projects);
		request.setAttribute(Constants.PARAMETER_FROM,
				Constants.FORWARD_ALL_PROJECTS);

		WorkflowManager workflowManager = (WorkflowManager) getBean("workflowManager");
		List<ProcessDefinition> processDefinitions = workflowManager
				.findProcessDefinitionsByName(Constants.MAIN_PROCESS_DEFINITION_NAME);
		List<Integer> versions = new ArrayList<Integer>();
		Iterator<ProcessDefinition> it = processDefinitions.iterator();
		while (it.hasNext()) {
			versions.add(it.next().getVersion());
		}
		request.setAttribute("versions", versions);
		// return a forward to the project list definition
		return mapping.findForward("list");
		*/
	}

	/*
	 * list projects for user and redirects to project list
	 */
	public ActionForward listAll(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		if (log.isDebugEnabled()) {
			log.debug("Entering 'search all Projects' method ");
		}

		ProjectManager mgr = (ProjectManager) getBean("projectManager");

		List projects = mgr.getProjects();
		request.setAttribute(Constants.PROJECT_LIST, projects);

		request.setAttribute(Constants.PARAMETER_FROM,
				Constants.FORWARD_ALL_PROJECTS);

		WorkflowManager workflowManager = (WorkflowManager) getBean("workflowManager");
		List<ProcessDefinition> processDefinitions = workflowManager
				.findProcessDefinitionsByName(Constants.MAIN_PROCESS_DEFINITION_NAME);
		List<Integer> versions = new ArrayList<Integer>();
		Iterator<ProcessDefinition> it = processDefinitions.iterator();
		while (it.hasNext()) {
			versions.add(it.next().getVersion());
		}
		request.setAttribute("versions", versions);

		// return a forward to the project list definition
		return mapping.findForward("list");
	}

	public ActionForward unspecified(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		return search(mapping, form, request, response);
	}

	/*
	 * public ActionForward start(ActionMapping mapping, ActionForm form,
	 * HttpServletRequest request, HttpServletResponse response) throws
	 * Exception { if (log.isDebugEnabled()) log.debug("Entering 'start project'
	 * method"); String projectId = request.getParameter("id"); if
	 * (log.isDebugEnabled()) { log.debug("projectId: " + projectId); }
	 * 
	 * ProjectManager mgr = (ProjectManager) getBean("projectManager"); Project
	 * project = mgr.getProject(Long.parseLong(projectId)); WorkflowManager
	 * workflowManager = (WorkflowManager) getBean("workflowManager");
	 * 
	 * byte[] data = project.getData(); Map<String, Object> variableMap =
	 * XstreamUtil.fromByteArrayToMap(data);
	 *  // get variableMap from Project, just overwrite DO_SETUP and INITIATOR
	 * 
	 * variableMap.put(JPDLConstants.DO_SETUP, "off");
	 * variableMap.put(JPDLConstants.INITIATOR, request.getRemoteUser());
	 * 
	 * ProcessDefinition processDefinition = workflowManager
	 * .findLatestProcessDefinition(Constants.SETUP_PROCESS_DEFINITION_NAME); if
	 * (processDefinition == null) { ActionMessages errors = new
	 * ActionMessages(); errors.add("errors.detail", new ActionMessage(
	 * "workflow.ProcessDefinition.notExists.error",
	 * Constants.SETUP_PROCESS_DEFINITION_NAME)); saveErrors(request, errors);
	 * return dispatchMethod(mapping, form, request, response, "search"); }
	 * 
	 * long processDefinitionId = processDefinition.getId();
	 * log.debug("processDefinition: " + processDefinitionId);
	 * 
	 * ProcessInstance processInstance = workflowManager
	 * .createStartProcessInstance(processDefinitionId, variableMap,
	 * project.getName()); // Save message if (log.isDebugEnabled()) log.debug("
	 * process instance started "); ActionMessages messages = new
	 * ActionMessages(); ProcessInstance[] processInstances = new
	 * ProcessInstance[1]; processInstances[0] = processInstance;
	 * 
	 * List<TaskInstance> pendingTaskInstancesForThisProcess = workflowManager
	 * .findPendingConfigurationOptionsByProcessInstancesAndActorId(
	 * processInstances, request.getRemoteUser());
	 * 
	 * String forwardName = null; String paramName = null; String paramValue =
	 * null; if (pendingTaskInstancesForThisProcess != null &&
	 * pendingTaskInstancesForThisProcess.size() > 0) { TaskInstance
	 * nextTaskInstance = pendingTaskInstancesForThisProcess .get(0);
	 * forwardName = "startTask"; paramName = "id"; paramValue =
	 * String.valueOf(nextTaskInstance.getId());
	 * messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
	 * "project.started", project.getName())); } else { forwardName = "show";
	 * paramName = "id"; paramValue = String.valueOf(project.getId());
	 * messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
	 * "error.project.start", project.getName())); }
	 * 
	 * log.debug("forward: " + forwardName); ActionForward actionForward =
	 * mapping.findForward(forwardName); ActionForward newActionForward = new
	 * ActionForward(actionForward);
	 * 
	 * saveMessages(request.getSession(), messages);
	 * 
	 * String path = actionForward.getPath(); if (paramValue != null) { path =
	 * path + "&" + paramName+ "=" + paramValue; }
	 * newActionForward.setPath(path); newActionForward.setRedirect(true); if
	 * (log.isDebugEnabled()) log.debug("Exit 'start project' method"); return
	 * newActionForward;
	 *  }
	 */
	public ActionForward create(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		if (log.isDebugEnabled())
			log.debug("Entering 'create project' method");
		ProjectForm projectForm = (ProjectForm) form;

		Project project = (Project) convert(projectForm);
		ProjectManager mgr = (ProjectManager) getBean("projectManager");
		ActionMessages errors = new ActionMessages();
		if (mgr.getProjectByName(projectForm.getName()) != null) {
			log.debug("------------Duplicate projectname------");
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
					"errors.existing.project", projectForm.getName()));
			saveErrors(request, errors);
			request.setAttribute("skipUpload", "true");
			return mapping.findForward("edit");
		}
		UserManager userManager = (UserManager) getBean("userManager");
		User user = userManager.getUserByUsername(request.getRemoteUser());
		project.setLastUpdate(new Date());
		project.setUserId(user.getId());
		project.setEnabled(false);
		// and finally save project

		WorkflowManager workflowManager = (WorkflowManager) getBean("workflowManager");

		int versionOfMainProcessDefinition = workflowManager
				.findLatestProcessDefinition(
						Constants.MAIN_PROCESS_DEFINITION_NAME).getVersion();
		project.setVersion(versionOfMainProcessDefinition);
		mgr.saveProject(project);

		// now retrieve projectId

		Map<String, Object> variableMap = new HashMap<String, Object>();
		variableMap.put(JPDLConstants.DO_SETUP, "on");
		variableMap.put(JPDLConstants.PROJECT_ID, mgr.getProjectByName(
				project.getName()).getId());
		variableMap.put(JPDLConstants.PROJECT_NAME, project.getName());
		variableMap.put(JPDLConstants.PROJECT_DESCRIPTION, project
				.getDescription());
		variableMap.put(JPDLConstants.INITIATOR, request.getRemoteUser());

		ProcessDefinition processDefinition = workflowManager
				.findLatestProcessDefinition(Constants.SETUP_PROCESS_DEFINITION_NAME);
		if (processDefinition == null) {

			errors.add("errors.detail", new ActionMessage(
					"workflow.ProcessDefinition.notExists.error",
					Constants.SETUP_PROCESS_DEFINITION_NAME));
			saveErrors(request, errors);
			request.setAttribute("skipUpload", "true");
			return mapping.findForward("viewAllProjects");
		}

		long processDefinitionId = processDefinition.getId();
		log.debug("processDefinition: " + processDefinitionId);

		ProcessInstance processInstance = workflowManager
				.createStartProcessInstance(processDefinitionId, variableMap,
						project.getName());

		// Save message
		if (log.isDebugEnabled())
			log.debug(" process instance started ");
		ActionMessages messages = new ActionMessages();

		ProcessInstance[] processInstances = new ProcessInstance[1];
		processInstances[0] = processInstance;

		List<TaskInstance> pendingTaskInstancesForThisProcess = workflowManager
				.findPendingConfigurationOptionsByProcessInstancesAndActorId(
						processInstances, request.getRemoteUser());

		String forwardName = null;
		String paramName = null;
		String paramValue = null;
		if (pendingTaskInstancesForThisProcess != null
				&& pendingTaskInstancesForThisProcess.size() > 0) {
			TaskInstance nextTaskInstance = pendingTaskInstancesForThisProcess
					.get(0);
			forwardName = "startTask";
			paramName = "id";
			paramValue = String.valueOf(nextTaskInstance.getId());
			messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
					"project.saved", project.getName()));
		} else {
			forwardName = "show";
			paramName = "id";
			paramValue = String.valueOf(project.getId());
			messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
					"error.project.create", project.getName()));
		}

		log.debug("forward: " + forwardName);
		ActionForward actionForward = mapping.findForward(forwardName);
		ActionForward newActionForward = new ActionForward(actionForward);

		saveMessages(request.getSession(), messages);

		String path = actionForward.getPath();
		if (paramValue != null) {
			path = path + "&" + paramName + "=" + paramValue;
		}
		newActionForward.setPath(path);
		newActionForward.setRedirect(true);
		if (log.isDebugEnabled())
			log.debug("Exit 'create project' method");
		return newActionForward;
	}

	public ActionForward resume(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		if (log.isDebugEnabled())
			log.debug("Entering 'resume project' method");
		String projectId = request.getParameter("id");
		if (log.isDebugEnabled()) {
			log.debug("projectId: " + projectId);
		}

		WorkflowManager workflowManager = (WorkflowManager) getBean("workflowManager");
		ProjectManager projectManager = (ProjectManager) getBean("projectManager");
		// assume that process definition is named after instance
		Project project = projectManager.getProject(new Long(projectId));
		String projectName = project.getName();
		log.debug("projectName: " + projectName);
		List<TaskInstance> taskInstanceList = workflowManager
				.findPendingConfigurationOptionsByProcessInstanceKeyAndActorId(
						projectName, request.getRemoteUser());

		ActionMessages messages = new ActionMessages();
		ActionMessages errors = new ActionMessages();
		if (taskInstanceList != null && taskInstanceList.size() > 0) {
			log.debug("task instances found: " + taskInstanceList.size());
			TaskInstance nextTaskInstance = taskInstanceList.get(0);

			String paramId = String.valueOf(nextTaskInstance.getId());

			messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
					"project.resumed", projectName));
			ActionForward actionForward = mapping.findForward("startTask");
			ActionForward newActionForward = new ActionForward(actionForward);
			String path = actionForward.getPath();

			path = path + "&id=" + paramId;

			newActionForward.setPath(path);
			newActionForward.setRedirect(true);
			saveMessages(request.getSession(), messages);
			return newActionForward;
		} else {
			log.debug("no task instances found!");
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
					"errors.cannotResumeProject", projectName));
			saveMessages(request.getSession(), errors);
			return mapping.findForward("viewAllProjects");
		}

	}

	/*
	 * list projects for user and redirects to project list
	 */
	public ActionForward show(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String projectId = request.getParameter("id");

		if (log.isDebugEnabled()) {
			log.debug("Entering 'show Project' method - projectId: "
					+ projectId);
		}

		ProjectManager projectManager = (ProjectManager) getBean("projectManager");
		Project project = projectManager.getProject(new Long(projectId));

		DocServiceManager documentManager = (DocServiceManager) getBean("docServiceManager");
		// find the corpora
		List<Corpus> corpora = documentManager.listCorpora();
		request.setAttribute("corpora", corpora);

		// return a forward to the project list definition
		return mapping.findForward("show");
	}

	public ActionForward load(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String username = request.getRemoteUser();
		if (log.isDebugEnabled()) {
			log.debug("Entering 'load Project' method - user: " + username);
		}
		String projectId = request.getParameter("projectId");
		log.debug("projectId: " + projectId);
		String corpusId = request.getParameter("corpusId");

		if (projectId == null) {
			// for the forst time
			ProjectManager projectManager = (ProjectManager) getBean("projectManager");

			UserManager userManager = (UserManager) getBean("userManager");
			User user = userManager.getUserByUsername(username);
			// find the available (READY) projects
			List<Project> availableProjects = projectManager
					.getAvailableProjects(user.getId());
			Project blankProject = new Project();
			blankProject.setId(new Long(0));
			Iterator<String> it = getMessages(request)
					.get("project.name.blank");
			String blankOption;
			if (it.hasNext()) {
				blankOption = it.next();
			} else {
				blankOption = "Blank Template";
			}
			blankProject.setName(blankOption);

			availableProjects.add(0, blankProject);
			request.setAttribute(Constants.PROJECT_LIST, availableProjects);

			return mapping.findForward("load");
		} else {

			if ("0".equals(projectId)) {
				// blank project
				log.debug("Add new");
				return mapping.findForward("addProject");
			} else {
				// existing project
				String url = "editProcessInstance.html?method=edit&projectId="
						+ projectId;
				log.debug("Found template");
				if (corpusId != null) {
					url = url + "&corpusId=" + corpusId;
				}
				response.sendRedirect(url);
				return null;
			}
		}
	}

}
