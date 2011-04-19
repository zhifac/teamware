/*
 *  TaskInstanceAction.java
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
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
import org.apache.struts.upload.FormFile;
import org.jbpm.context.def.VariableAccess;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.springframework.util.StringUtils;
import gleam.executive.Constants;
import gleam.executive.model.WebAppBean;
import gleam.executive.service.DocServiceManager;
import gleam.executive.service.ResourceManager;
import gleam.executive.webapp.form.FileUploadForm;
import gleam.executive.webapp.wrapp.ProcessInstanceWrapper;
import gleam.executive.webapp.wrapp.TaskInstanceWrapper;
import gleam.executive.workflow.manager.WorkflowManager;
import gleam.executive.workflow.model.TaskFormParameterBean;
import gleam.executive.workflow.util.JPDLConstants;
import gleam.executive.workflow.util.WorkflowUtil;

//TODO Javadoc
/**
 * @author agaton
 * 
 * @struts.action name="fileUploadForm" path="/taskInstanceList" scope="request"
 *                validate="false" parameter="method" input="taskInstanceList"
 * @struts.action-forward name="list" path="/WEB-INF/pages/taskInstanceList.jsp"
 * @struts.action-forward name="task" path="/WEB-INF/pages/taskInstance.jsp"
 */
public class TaskInstanceAction extends BaseAction {
	/**
	 * List all tasks assigned to user
	 * and Task Instances assigned to logged user
	 * 
	 * @author agaton
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return ActionForward
	 * @throws Exception
	 */
	public ActionForward listByActor(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		if (log.isDebugEnabled())
			log.debug("TaskInstanceAction - Entering 'listByActor' method");
		// Exceptions are caught by ActionExceptionHandler
		WorkflowManager wfMgr = (WorkflowManager) getBean("workflowManager");
		if (log.isDebugEnabled())
			log.debug("Remote user : " + request.getRemoteUser());
		// Fetch pooled task instances
		// List pooledTaskInstances =
		// wfMgr.findPooledTaskInstances(request.getRemoteUser());
		// Fetch task instances assigned to logged user
		List taskInstances = wfMgr
				.findAllTaskInstances(request.getRemoteUser());
		
		List<TaskInstanceWrapper> tasks = new ArrayList<TaskInstanceWrapper>();
		// merge all task instances that logged user can perform
		// taskInstances.addAll(pooledTaskInstances);
		Iterator it = taskInstances.iterator();
		while (it.hasNext()) {
			TaskInstance task = (TaskInstance) it.next();
			TaskInstanceWrapper tiw = new TaskInstanceWrapper(task);
			tasks.add(tiw);
		}
		request.setAttribute(Constants.TASK_INSTANCES_LIST, tasks);
		// return a forward to the task instances list
		if (log.isDebugEnabled())
			log.debug("TaskInstanceAction - Exit 'list task instances' method");
		return mapping.findForward("list");
	}
	
	/**
	 * List all tasks assigned to user - Pooled Task Instances for logged user
	 * and Task Instances assigned to logged user
	 * 
	 * @author agaton
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return ActionForward
	 * @throws Exception
	 */
	public ActionForward listPendingConfigurationSteps(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		if (log.isDebugEnabled())
			log.debug("TaskInstanceAction - Entering 'listPendingConfigurationSteps' method");
		// Exceptions are caught by ActionExceptionHandler
		WorkflowManager wfMgr = (WorkflowManager) getBean("workflowManager");
		if (log.isDebugEnabled())
			log.debug("Remote user : " + request.getRemoteUser());
		// Fetch pooled task instances
		// List pooledTaskInstances =
		// wfMgr.findPooledTaskInstances(request.getRemoteUser());
		// Fetch task instances assigned to logged user
		List taskInstances = wfMgr
				.findPendingConfigurationOptionsByActorId(request.getRemoteUser());
		List<TaskInstanceWrapper> tasks = new ArrayList<TaskInstanceWrapper>();
		// merge all task instances that logged user can perform
		// taskInstances.addAll(pooledTaskInstances);
		Iterator it = taskInstances.iterator();
		while (it.hasNext()) {
			TaskInstance task = (TaskInstance) it.next();
			TaskInstanceWrapper tiw = new TaskInstanceWrapper(task);
			tasks.add(tiw);
		}
		request.setAttribute(Constants.TASK_INSTANCES_LIST, tasks);
		// return a forward to the task instances list
		request.setAttribute("wizard", true);
		if (log.isDebugEnabled())
			log.debug("TaskInstanceAction - Exit 'list task instances' method");
		return mapping.findForward("list");
	}

	/**
	 * List tasks for a given process instance
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward listTasksForProcessInstance(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		if (log.isDebugEnabled())
			log
					.debug("TaskInstanceAction - Entering 'listTasksForProcessInstance' method");
		// Exceptions are caught by ActionExceptionHandler
		WorkflowManager wfMgr = (WorkflowManager) getBean("workflowManager");
		if (log.isDebugEnabled())
			log.debug("Remote user : " + request.getRemoteUser());
		String processInstanceId = request.getParameter("processInstanceId");
		if (log.isDebugEnabled())
			log.debug("processInstanceId : " + processInstanceId);
		ProcessInstance processInstance = wfMgr.getProcessInstanceForUpdate(Long
						.parseLong(processInstanceId));
		ProcessInstanceWrapper piw = new ProcessInstanceWrapper(
				processInstance);
		// Fetch task instances assigned to logged user
		List<TaskInstance> taskInstances = (List<TaskInstance>) wfMgr
				.findTaskInstancesForProcessInstance(Long
						.parseLong(processInstanceId));
		List<TaskInstanceWrapper> tasks = new ArrayList<TaskInstanceWrapper>();
		// merge all task instances that logged user can perform
		Iterator it = taskInstances.iterator();
		while (it.hasNext()) {
			TaskInstance task = (TaskInstance) it.next();
			TaskInstanceWrapper tiw = new TaskInstanceWrapper(task);
			tasks.add(tiw);
		}
		
		ResourceManager resourceManager = (ResourceManager)getBean("resourceManager");
		    
		// processInstanceManagingRoles
		List processInstanceManagingRoles = resourceManager.getRolesWithResource(Constants.PROCESS_INSTANCE_PATTERN);
		// check if current user is rhe manager of this project
		if(request.getRemoteUser().equals(piw.getUsername())){
			log.debug("The user "+request.getRemoteUser() + " is manager. Allow him to execute tasks");
			processInstanceManagingRoles.add(Constants.MANAGER_ROLE);
		}
		
		String processInstanceManagingRolesString = StringUtils.collectionToCommaDelimitedString(processInstanceManagingRoles);
		log.debug("processInstanceManagingRoles "+processInstanceManagingRoles);
		request.setAttribute("processInstanceManagingRoles",processInstanceManagingRolesString);
		    
		request.setAttribute(Constants.TASK_INSTANCES_LIST, tasks);
		// return a forward to the task instances list
		if (log.isDebugEnabled())
			log
					.debug("TaskInstanceAction - Exit 'list task instances for a specific process instance' method");
		return mapping.findForward("list");
	}

	/**
	 * List all pooled tasks assigned to user
	 * 
	 * @author agaton
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
		if (log.isDebugEnabled())
			log.debug("TaskInstanceAction - Entering 'list' method");
		// Exceptions are caught by ActionExceptionHandler
		WorkflowManager wfMgr = (WorkflowManager) getBean("workflowManager");
		if (log.isDebugEnabled())
			log.debug("Remote user : " + request.getRemoteUser());
		// Fetch pooled task instances
		List pooledTaskInstances = wfMgr.findAllPooledTaskInstances(request
				.getRemoteUser());
		// Fetch task instances assigned to logged user
		// List taskInstances =
		// wfMgr.findAllTaskInstances(request.getRemoteUser());
		List<TaskInstanceWrapper> tasks = new ArrayList<TaskInstanceWrapper>();
		// merge all task instances that logged user can perform
		// taskInstances.addAll(pooledTaskInstances);
		Iterator it = pooledTaskInstances.iterator();
		
		while (it.hasNext()) {
			TaskInstance task = (TaskInstance) it.next();
			TaskInstanceWrapper tiw = new TaskInstanceWrapper(task);
			tasks.add(tiw);
		}
		request.setAttribute(Constants.TASK_INSTANCES_LIST, tasks);
		// return a forward to the task instances list
		if (log.isDebugEnabled())
			log.debug("TaskInstanceAction - Exit 'list task instances' method");
		return mapping.findForward("list");
	}

	/**
	 * Execute specified task
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return ActionForward
	 * @throws Exception
	 */
	public ActionForward start(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		if (log.isDebugEnabled())
			log.debug("TaskInstanceAction - Enter 'execute task' method");
		// Extract attributes and parameters we will need
		String taskInstanceId = request.getParameter("id");
		if (log.isDebugEnabled())
			log.debug("taskInstanceId " + taskInstanceId);
		Long tiId = Long.valueOf(taskInstanceId);
		// Exceptions are caught by ActionExceptionHandler
		WorkflowManager mgr = (WorkflowManager) getBean("workflowManager");
		log.debug("TaskInstanceAction - Enter 'execute task' method");
		TaskInstance ti = mgr.getTaskInstance(tiId);
		// set current user as a performer and remove pooled users
		// ti.setActorId(request.getRemoteUser());

		request.setAttribute("taskName", ti.getName());
		request.setAttribute("taskDescription", ti.getDescription());
		request.setAttribute("taskInstanceId", taskInstanceId);
		if(ti.getPriority()==JPDLConstants.PRIORITY_CONFIGURATION_STEP){
			// configuration task
			log.debug("Task inside wizard - configuration option");
			request.setAttribute("wizard", true);
		}
		else {
			log.debug("Normal Task");
		}
		
		// Get Task variables and their accesses
		List taskFormParameters = null;
		
		
		
		if(request.getAttribute(Constants.TASK_FORM_PARAMETERS)==null){
			taskFormParameters = mgr.populateTaskFormParameters(tiId);
		   request.setAttribute(Constants.TASK_FORM_PARAMETERS,
						taskFormParameters);
		}
		else {
			log.debug("task form parameters are already set in this request");
			// this is in case if validation fails in save method
		}
		if(!ti.hasEnded()){
		// Load available transitions
		List availableTransitions = mgr.getAvailableTransitions(tiId);
		List transitions = null;
		if (availableTransitions != null) {
			transitions = new ArrayList();
			Iterator iter = availableTransitions.iterator();
			while (iter.hasNext()) {
				Transition transition = (Transition) iter.next();
				if (transition.getName() == null
						|| "".equals(transition.getName())) {
					transition.setName(JPDLConstants.TRANSITION_FINISH);
				}
				transitions.add(transition.getName());
			}
			request.setAttribute("transitionsNum", new Integer(transitions
					.size()));
			Collections.sort(transitions);
			request.setAttribute(Constants.TASK_TRANSITIONS, transitions);
		}
		}
		else {
			ActionMessages errors = new ActionMessages();
			if(WorkflowUtil.isConfigurationStep(ti.getPriority())){
				  errors.add("errors.detail", new ActionMessage(
						"error.configurationStepAlreadyCompleted", ti.getName()));
				}
				else {
					 errors.add("errors.detail", new ActionMessage(
								"error.taskInstanceAlreadyCompleted", ti.getName()));
				}
			if (errors.size() != 0) {
				saveErrors(request, errors);
			}
			
		}
		// return a forward to specified task form
		if (log.isDebugEnabled())
			log.debug("TaskInstanceAction - Exit 'execute task' method");
		return mapping.findForward("task");
	}

	/**
	 * Save task variables and end specified task
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return ActionForward
	 * @throws Exception
	 */
	public ActionForward save(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		if (log.isDebugEnabled())
			log.debug("TaskInstanceAction - Entering 'save task' method");
		// this line is here for when the input page is upload-utf8.jsp,
		// it sets the correct character encoding for the response
		String encoding = request.getCharacterEncoding();
		if ((encoding != null) && (encoding.equalsIgnoreCase("utf-8"))) {
			response.setContentType("text/html; charset=utf-8");
		}
		// if file is uploaded
		FileUploadForm fileForm = (FileUploadForm) form;
		log.debug("upload form mpr handler: "+fileForm.getMultipartRequestHandler().getClass().getName());
		// Extract attributes and parameters we will need
		String taskInstanceId = request.getParameter("taskInstanceId");
		if (log.isDebugEnabled())
			log.debug("taskInstanceId " + taskInstanceId);
		// get specified transition
		String transition = request.getParameter("transition");
		if (log.isDebugEnabled())
			log.debug("transition " + transition);
		ActionMessages errors = new ActionMessages();
		ActionMessages messages = new ActionMessages();
		WorkflowManager mgr = (WorkflowManager) getBean("workflowManager");
		TaskInstance ti = mgr.getTaskInstance(Long.parseLong(taskInstanceId));

		request.setAttribute("taskName", ti.getName());
		request.setAttribute("taskDescription", ti.getDescription());
		request.setAttribute("taskInstanceId", taskInstanceId);
		if(WorkflowUtil.isConfigurationStep(ti.getPriority())){
			// configuration task
			log.debug("Task inside wizard - configuration option");
			request.setAttribute("wizard", true);
		}
		else {
			log.debug("Normal Task");
		}
	
		List variableAccesses = mgr.findVariableAccesses(Long
				.valueOf(taskInstanceId));
		List<TaskFormParameterBean> taskFormParameters = new ArrayList<TaskFormParameterBean>();

		Map<String, Object> variables = new HashMap();
		String name;
		VariableAccess va;
		String value = null;
		for (Iterator i = variableAccesses.iterator(); i.hasNext();) {
			va = (VariableAccess) i.next();
			name = va.getMappedName();
            log.debug("handling variable: "+name);
			if (fileForm != null && WorkflowUtil.isFile(name)) {
				
				// Upload file to temporary location and save file path in
				// task variable instance
				FormFile file = fileForm.getFile();
				
				/*
				 * After uploading a file and getting a FormFile object, I get 0
				 * when calling getFileSize() whenever the file uploaded has a
				 * big filesize (e.g. 2MB). But I get the correct filesize if
				 * the the file uploaded has a small filesize (e.g. 100Kb). I
				 * think the method getFileSize() should return a "long" value
				 * instead of the current "int" so that it can handle big sizes
				 * in bytes.
				 */

				if (file == null || (file.getFileSize() == 0)) {
					if (va.isRequired()) {
						// check if file exists
						String label = WorkflowUtil.getLabel(va.getMappedName());
						errors.add("errors.detail", new ActionMessage(
								"errors.required", label));
					}

				} else {
					String filePath = null;
					// the directory to upload to
					String uploadDir = servlet.getServletContext().getRealPath(
							"/resources")
							+ "/" + request.getRemoteUser() + "/";
					// write the file to the file specified
					File dirPath = new File(uploadDir);
					if (!dirPath.exists()) {
						dirPath.mkdirs();
					}
					// retrieve the file data
					InputStream stream = file.getInputStream();
					// write the file to the file specified
					String fileName = file.getFileName();
					OutputStream bos = new FileOutputStream(uploadDir
							+ fileName);
					int bytesRead = 0;
					byte[] buffer = new byte[8192];
					while ((bytesRead = stream.read(buffer, 0, 8192)) != -1) {
						bos.write(buffer, 0, bytesRead);
					}
					bos.close();
					filePath = dirPath.getAbsolutePath() + Constants.FILE_SEP
							+ file.getFileName();
					log.debug("PATH THAT IS STORED TO DATA BASE: "
							+ "/resources/" + request.getRemoteUser() + "/"
							+ fileName);
					variables.put(name, "/resources/" + request.getRemoteUser()
							+ "/" + fileName);
				}
			} else if (WorkflowUtil.isMultiBox(name)) {
				String[] array = request.getParameterValues(name);
				if (array != null) {
					log.debug("MULTIBOX: number of selected " + array.length);
					value = StringUtils.arrayToCommaDelimitedString(array);
					variables.put(name, value);
				} else {
					log.debug("MULTIBOX: NULL ");
					if (va.isRequired()) {
						String label = WorkflowUtil.getLabel(va.getMappedName());
						errors.add("errors.detail", new ActionMessage(
								"errors.required", label));
					}

				}
			} 
			else if (WorkflowUtil.isCheckBox(name)) {
				value = request.getParameter(name);
				if(!"on".equals(value)){
					value= "off";
				}
				log.debug("variable value: "+value);
				variables.put(name, value);
			}	
			else {
				value = request.getParameter(name);
				log.debug("variable value: "+value);
				if (va.isRequired()
						&& (value == null || value.trim().equals(""))) {
					String label = WorkflowUtil.getLabel(va.getMappedName());
					errors.add("errors.detail", new ActionMessage(
							"errors.required", label));
				}
				if (value != null
						&& (value.equals("true") || value.equals("false")))
					variables.put(name, Boolean.valueOf(value));
				else if (checkIfValidDate(value)) {
					DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
					variables.put(name, df.parse(value));
				} else
					variables.put(name, value);
			}

			TaskFormParameterBean tfp = new TaskFormParameterBean(va, value);
			taskFormParameters.add(tfp);
		}

		

		long id = Long.parseLong(taskInstanceId);
		TaskInstance tis = mgr.getTaskInstance(id);
		if(!tis.hasEnded() && errors.size() == 0){
		   mgr.completeTask(id, variables, transition);
		}
		else if(tis.hasEnded()){
			if(WorkflowUtil.isConfigurationStep(tis.getPriority())){
			  errors.add("errors.detail", new ActionMessage(
					"error.configurationStepAlreadyCompleted", tis.getName()));
			}
			else {
				 errors.add("errors.detail", new ActionMessage(
							"error.taskInstanceAlreadyCompleted", tis.getName()));
			}
		}
		
		else if (errors.size() != 0) {
			saveErrors(request, errors);
			request.setAttribute(Constants.TASK_FORM_PARAMETERS,
					taskFormParameters);
			ActionForward actionForward = mapping.findForward("taskInstance");
			ActionForward newActionForward = new ActionForward(actionForward);
			newActionForward.setPath(actionForward.getPath() + "&id="
					+ taskInstanceId);
			return newActionForward;
		}
		// check if there is a pending task in this process instance
		// if so, automatically start the next task and redirect to taskFormPage,
		// otherwise go to pendingTaskList
		TaskInstance taskInstance = mgr.getTaskInstance(new Long(taskInstanceId));
		ProcessInstance processInstance = taskInstance.getProcessInstance();
	    Token token = processInstance.getSuperProcessToken();
	    
	    // first find the top most parent
		while(token!=null){
			processInstance = token.getProcessInstance();
			token =  processInstance.getSuperProcessToken();
		}
		
		
		
		log.debug("found top parent process: "+processInstance.getProcessDefinition().getName());
		// last process instance is the top most one
		
		
		List<ProcessInstance> processInstanceList = mgr.findAllSubProcessInstances(processInstance);
		log.debug("found subprocesses num: "+processInstanceList.size());
		// add parent process to the list of subprocesses
		processInstanceList.add(processInstance);
	
		ProcessInstance[] processInstances = (ProcessInstance[])processInstanceList.toArray(new ProcessInstance[processInstanceList.size()]);
		List<TaskInstance> pendingTaskInstancesForThisProcess = mgr.findPendingConfigurationOptionsByProcessInstancesAndActorId(processInstances, request.getRemoteUser());
		
		String forwardName = null;
		String paramName = null;
		String paramValue = null;
		if(pendingTaskInstancesForThisProcess!=null && pendingTaskInstancesForThisProcess.size()>0){
			TaskInstance nextTaskInstance = pendingTaskInstancesForThisProcess.get(0);
			forwardName = "startTask";
			paramName = "id";
			paramValue = String.valueOf(nextTaskInstance.getId());
			messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
					"workflowTaskInstance.completed", ti.getName()));
		}
		else {
			if(WorkflowUtil.isConfigurationStep(taskInstance.getPriority())){
				log.debug("this is a configuration step (inside process setup. " +
				  " redirect user to project list");	
				forwardName = "viewAllProjects";
				
				messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
						"project.saved", processInstance.getKey()));
				} else{
					paramName = "id";	
				    forwardName = "pendingTasksList";
				    messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
							"workflowTaskInstance.completed", ti.getName()));
				}
		}	
		
		// save messages in session to survive a redirect
		saveMessages(request.getSession(), messages);
		
		
		ActionForward actionForward = mapping.findForward(forwardName);
		ActionForward newActionForward = new ActionForward(actionForward);
		String path = actionForward.getPath();
		if(paramValue!=null){
			path = path + "&"+ paramName +"="+ paramValue;
		}
		log.debug("path: "+ path);
		newActionForward.setPath(path);
		newActionForward.setRedirect(true);
		if (log.isDebugEnabled())
			log.debug("TaskInstanceAction - Exit 'save task' method");
		return newActionForward;
	}

	/**
	 * Cancel specified task
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return ActionForward
	 * @throws Exception
	 */
	public ActionForward cancel(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		if (log.isDebugEnabled())
			log.debug("TaskInstanceAction - Enter 'cancel task' method");
		// Extract attributes and parameters we will need
		String taskInstanceId = request.getParameter("id");
		if (log.isDebugEnabled())
			log.debug("taskInstanceId " + taskInstanceId);
		// Exceptions are caught by ActionExceptionHandler
		WorkflowManager mgr = (WorkflowManager) getBean("workflowManager");
		mgr.cancelTaskInstance(Long.parseLong(taskInstanceId));
		// return a forward to specified task form
		if (log.isDebugEnabled())
			log.debug("TaskInstanceAction - Exit 'cancel task' method");
		return mapping.findForward("taskInstanceList");
	}
	
	/**
	 * Cancel specified task
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return ActionForward
	 * @throws Exception
	 */
	public ActionForward end(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		if (log.isDebugEnabled())
			log.debug("TaskInstanceAction - Enter 'end task' method");
		// Extract attributes and parameters we will need
		String taskInstanceId = request.getParameter("id");
		if (log.isDebugEnabled())
			log.debug("taskInstanceId " + taskInstanceId);
		// Exceptions are caught by ActionExceptionHandler
		WorkflowManager mgr = (WorkflowManager) getBean("workflowManager");
		mgr.endTaskInstance(Long.parseLong(taskInstanceId));
		// return a forward to specified task form
		if (log.isDebugEnabled())
			log.debug("TaskInstanceAction - Exit 'end task' method");
		return mapping.findForward("taskInstanceList");
	}

	/**
	 * Reject specified task
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return ActionForward
	 * @throws Exception
	 */
	public ActionForward reject(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		if (log.isDebugEnabled())
			log.debug("TaskInstanceAction - Enter 'reject task' method");
		// Extract attributes and parameters we will need
		String taskInstanceId = request.getParameter("id");
		ActionMessages messages = new ActionMessages();
		if (log.isDebugEnabled())
			log.debug("taskInstanceId " + taskInstanceId);
		// Exceptions are caught by ActionExceptionHandler
		WorkflowManager mgr = (WorkflowManager) getBean("workflowManager");
		if (taskInstanceId != null) {
			TaskInstance ti = mgr.getTaskInstance(Long
					.parseLong(taskInstanceId));
			mgr.rejectTaskInstance(Long.parseLong(taskInstanceId));
			// return a forward to specified task form
			if (log.isDebugEnabled())
				log.debug("TaskInstanceAction - Exit 'cancel task' method");
			request.setAttribute("taskName", ti.getName());
			request.setAttribute("taskDescription", ti.getDescription());
			request.setAttribute("taskInstanceId", taskInstanceId);
			messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
					"workflowTaskInstance.rejected", ti.getName()));
			saveMessages(request.getSession(), messages);
		}
		ActionForward af = mapping.findForward("taskInstanceList");
		ActionForward newaf = new ActionForward(af);
		newaf.setRedirect(true);
		return newaf;

	}

	/**
	 * Reject specified task
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return ActionForward
	 * @throws Exception
	 */
	public ActionForward accept(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		if (log.isDebugEnabled())
			log.debug("TaskInstanceAction - Enter 'accept task' method");
		// Extract attributes and parameters we will need
		String taskInstanceId = request.getParameter("id");
		if (log.isDebugEnabled())
			log.debug("taskInstanceId " + taskInstanceId);
		// Exceptions are caught by ActionExceptionHandler
		WorkflowManager mgr = (WorkflowManager) getBean("workflowManager");
		ActionMessages messages = new ActionMessages();
		if (taskInstanceId != null) {
			TaskInstance ti = mgr.getTaskInstance(Long
					.parseLong(taskInstanceId));
			mgr.acceptTaskInstance(Long.parseLong(taskInstanceId), request
					.getRemoteUser());
			// return a forward to specified task form
			if (log.isDebugEnabled())
				log.debug("TaskInstanceAction - Exit 'accept task' method");
			request.setAttribute("taskName", ti.getName());
			request.setAttribute("taskDescription", ti.getDescription());
			request.setAttribute("taskInstanceId", taskInstanceId);
			messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
					"workflowTaskInstance.accepted", ti.getName()));
			saveMessages(request, messages);
		}
		return mapping.findForward("pendingTasksList");
	}

	/**
	 * Suspend specified task
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return ActionForward
	 * @throws Exception
	 */
	public ActionForward suspend(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		if (log.isDebugEnabled())
			log.debug("TaskInstanceAction - Enter 'suspend task' method");
		// Extract attributes and parameters we will need
		String taskInstanceId = request.getParameter("id");
		if (log.isDebugEnabled())
			log.debug("taskInstanceId " + taskInstanceId);
		// Exceptions are caught by ActionExceptionHandler
		WorkflowManager mgr = (WorkflowManager) getBean("workflowManager");
		mgr.suspendTaskInstance(Long.parseLong(taskInstanceId));
		// return a forward to specified task form
		if (log.isDebugEnabled())
			log.debug("TaskInstanceAction - Exit 'suspend task' method");
		return mapping.findForward("taskInstanceList");
	}

	/**
	 * Execute specified task
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return ActionForward
	 * @throws Exception
	 */
	public ActionForward view(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		if (log.isDebugEnabled())
			log.debug("TaskInstanceAction - Enter 'view' method");
		// Extract attributes and parameters we will need
		String taskInstanceId = request.getParameter("id");
		if (log.isDebugEnabled())
			log.debug("taskInstanceId " + taskInstanceId);
		Long tiId = Long.valueOf(taskInstanceId);
		// Exceptions are caught by ActionExceptionHandler
		WorkflowManager mgr = (WorkflowManager) getBean("workflowManager");
		WebAppBean webapp = (WebAppBean) getBean("webAppBean");
		request.setAttribute("webappname", webapp.getName());
		log.debug("Setting webapp name " + webapp.getName()
				+ " to request scope from start()");
		log.debug("TaskInstanceAction - Enter 'execute task' method");
		ProcessDefinition processDefinition = mgr
				.findProcessDefinitionByTaskInstanceId(Long.parseLong(taskInstanceId));
		TaskInstance ti = mgr.getTaskInstance(tiId);
		request.setAttribute("taskName", ti.getName());
		request.setAttribute("taskDescription", ti.getDescription());
		request.setAttribute("taskInstanceId", taskInstanceId);
		
		// Get Task variables and their accesses
		List taskFormParameters = mgr.populateTaskFormParameters(tiId);
		request
				.setAttribute(Constants.TASK_FORM_PARAMETERS,
						taskFormParameters);
		// Load available transitions

		request.setAttribute("transitionsNum", 0);

		// return a forward to specified task form
		if (log.isDebugEnabled())
			log.debug("TaskInstanceAction - Exit 'execute view' method");
		return mapping.findForward("task");
	}

	/**
	 * Resumw specified task
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return ActionForward
	 * @throws Exception
	 */
	public ActionForward resume(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		if (log.isDebugEnabled())
			log.debug("TaskInstanceAction - Enter 'resume task' method");
		// Extract attributes and parameters we will need
		String taskInstanceId = request.getParameter("id");
		if (log.isDebugEnabled())
			log.debug("taskInstanceId " + taskInstanceId);
		// Exceptions are caught by ActionExceptionHandler
		WorkflowManager mgr = (WorkflowManager) getBean("workflowManager");
		mgr.resumeTaskInstance(Long.parseLong(taskInstanceId));
		// return a forward to specified task form
		if (log.isDebugEnabled())
			log.debug("TaskInstanceAction - Exit 'resume task' method");
		return mapping.findForward("taskInstanceList");
	}
	
	
	public ActionForward deleteMultipleTaskInstances(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		if (log.isDebugEnabled()) {
			log.debug("Entering 'delete multiple task instances' method");
		}
		ActionMessages messages = new ActionMessages();

		// Exceptions are caught by ActionExceptionHandler
		WorkflowManager mgr = (WorkflowManager) getBean("workflowManager");
		
		String toDelete = request.getParameter("toDelete");

		
		String[] taskInstanceIds = request.getParameterValues("rowId");
		if (taskInstanceIds == null) {
			ActionMessages errors = new ActionMessages();
			errors.add("errors.detail", new ActionMessage(
					"error.noTaskInstanceSelected"));
			saveErrors(request, errors);
			return dispatchMethod(mapping, form, request, response, "listTasksForProcessInstance");
		}
		if (toDelete != null) {
			Collection<Long> col = new ArrayList<Long>();
			for (String taskInstanceId : taskInstanceIds) {
				log.debug("delete taskInstanceId: "+taskInstanceId);
				col.add(new Long(taskInstanceId));
			}
			mgr.deleteTaskInstances(col);
			messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
					"taskInstances.deleted"));
		}

		saveMessages(request.getSession(), messages);
		return dispatchMethod(mapping, form, request, response, "listTasksForProcessInstance");

	}

	/**
	 * Check ih string is in valid date format
	 * 
	 * @param s
	 * @return
	 */
	boolean checkIfValidDate(String s) {
		boolean isValid = true;
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
		Date myDate;
		try {
			df.setLenient(false);
			myDate = df.parse(s);
		} catch (Exception pe) {
			isValid = false;
			myDate = null;
		}
		return isValid;
	}
}
