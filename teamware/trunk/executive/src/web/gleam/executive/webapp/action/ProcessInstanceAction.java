package gleam.executive.webapp.action;

import java.util.ArrayList;
import java.util.Collections;
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
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;
import org.springframework.util.StringUtils;

import gleam.executive.Constants;
import gleam.executive.model.Corpus;
import gleam.executive.model.Document;
import gleam.executive.model.Project;
import gleam.executive.model.WebAppBean;
import gleam.executive.service.DocServiceManager;
import gleam.executive.service.ProjectManager;
import gleam.executive.service.RoleManager;
import gleam.executive.service.UserManager;
import gleam.executive.util.XstreamUtil;
import gleam.executive.webapp.form.ProcessInstanceForm;
import gleam.executive.webapp.wrapp.ProcessInstanceWrapper;
import gleam.executive.workflow.command.ProcessStartCommand;
import gleam.executive.workflow.jms.ProcessStartCommandProducer;
import gleam.executive.workflow.manager.WorkflowManager;
import gleam.executive.workflow.util.JPDLConstants;
import gleam.executive.workflow.util.WorkflowException;

/**
 * @author <a href="mailto:agaton@dcs.shef.ac.uk">Milan Agatonovic</a>
 * 
 * @struts.action path="/processInstanceList" scope="request" validate="false"
 *                parameter="method" input="processInstanceList"
 * 
 * @struts.action name="processInstanceForm" path="/saveProcessInstance"
 *                scope="request" validate="true" parameter="method"
 *                input="edit"
 * @struts.action name="processInstanceForm" path="/editProcessInstance"
 *                scope="request" validate="false" parameter="method"
 *                input="edit"
 * @struts.action-forward name="edit"
 *                        path="/WEB-INF/pages/processInstanceForm.jsp"
 * @struts.action-forward name="list"
 *                        path="/WEB-INF/pages/processInstanceList.jsp"
 * @struts.action-forward name="info" 
 *                        path="/WEB-INF/pages/processStartInfo.jsp"
 */
public class ProcessInstanceAction extends BaseAction {
	/**
	 * List process instances for specified process definition
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

		// INVOKED from processDefinitionList.jsp param id processDefinitionId

		if (log.isDebugEnabled())
			log
					.debug("ProcessInstanceAction - Entering 'list process instances' method");

		WorkflowManager mgr = (WorkflowManager) getBean("workflowManager");

		String processDefinitionId = request.getParameter("id");
		if (processDefinitionId == null) {
			processDefinitionId = (String) request.getAttribute("id");
		}
		if (log.isDebugEnabled())
			log.debug("processDefinitionId " + processDefinitionId);
		if (processDefinitionId == null) {

			String processInstanceId = request
					.getParameter("processInstanceId");
			if (processInstanceId == null) {
				processInstanceId = (String) request.getAttribute("id");
			}
			log.debug("processInstanceId: " + processInstanceId);

			ProcessInstance pi = mgr.getProcessInstance(new Long(
					processInstanceId));
			processDefinitionId = String.valueOf(pi.getProcessDefinition()
					.getId());
		}

		// Exceptions are caught by ActionExceptionHandler

		List processInstances = mgr
				.findAllProcessInstancesByProcessDefinitionIdExcludingSubProcessInstances(Long
						.parseLong(processDefinitionId));
		List<ProcessInstanceWrapper> processInstanceWrappers = new ArrayList<ProcessInstanceWrapper>();
		Iterator it = processInstances.iterator();
		while (it.hasNext()) {
			ProcessInstance processInstance = (ProcessInstance) it.next();
			ProcessInstanceWrapper piw = new ProcessInstanceWrapper(
					processInstance);
			processInstanceWrappers.add(piw);
		}
		request.setAttribute(Constants.PROCESS_INSTANCES_LIST,
				processInstanceWrappers);

		request.setAttribute(Constants.PARAMETER_FROM,
				Constants.FORWARD_PROCESSES_FOR_PROCESSDEFINITION);

		// return a forward to the process instances list
		if (log.isDebugEnabled())
			log
					.debug("ProcessInstanceAction - Exit 'list process instances' method");
		return mapping.findForward("list");
	}

	// 
	/**
	 * List top processes (excluding subprocesses) with the same key
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return ActionForward
	 * @throws Exception
	 */
	public ActionForward listTopProcessesWithTheSameKeyAndName(
			ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// invoked from projectList.jsp param projectName
		// ActorAction method save param projectName
		// Process Instance Action method save param projectName
		if (log.isDebugEnabled())
			log
					.debug("ProcessInstanceAction - Entering 'listTopProcessesWithTheSameKeyAndName' method");
		String projectName = request.getParameter("projectName");
		if (projectName == null) {
			projectName = (String) request.getAttribute("projectName");
		}
		WorkflowManager mgr = (WorkflowManager) getBean("workflowManager");
		if (projectName == null) {
			String processInstanceId = request
					.getParameter("processInstanceId");
			if (processInstanceId == null) {
				processInstanceId = (String) request
						.getAttribute("processInstanceId");
			}
			log.debug("processInstanceId: " + processInstanceId);

			ProcessInstance pi = mgr.getProcessInstanceForUpdate(new Long(
					processInstanceId));

			// find the parrent
			if (pi != null) {
				Token token = pi.getSuperProcessToken();
				while (token != null) {
					pi = token.getProcessInstance();
					token = pi.getSuperProcessToken();
				}
				projectName = pi.getKey();
			}
		}

		log.debug("projectName: " + projectName);
		if (projectName != null) {

			WebAppBean webAppBean = (WebAppBean) getBean("webAppBean");

			// Exceptions are caught by ActionExceptionHandler
			List processInstances = mgr
					.findProcessInstancesExcludingSubProcessInstancesByKeyAndName(
							projectName, Constants.MAIN_PROCESS_DEFINITION_NAME);
			List<ProcessInstanceWrapper> processInstanceWrappers = new ArrayList<ProcessInstanceWrapper>();
			Iterator it = processInstances.iterator();
			while (it.hasNext()) {
				ProcessInstance processInstance = (ProcessInstance) it.next();
				ProcessInstanceWrapper piw = new ProcessInstanceWrapper(
						processInstance);
				processInstanceWrappers.add(piw);
			}
			request.setAttribute(Constants.PROCESS_INSTANCES_LIST,
					processInstanceWrappers);
			request.setAttribute(Constants.PARAMETER_FROM,
					Constants.FORWARD_PROCESSES_IN_PROJECT);
			// return a forward to the process instances list
			request.setAttribute("projectName", projectName);
			if (log.isDebugEnabled())
				log
						.debug("ProcessInstanceAction - Exit 'list process instances' method");
		}
		return mapping.findForward("list");
	}

	/**
	 * List all process instances which started specified user
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return ActionForward
	 * @throws Exception
	 */
	public ActionForward listByUser(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		throw new Exception("Invalid method");
		/*
		 * String userName = request.getRemoteUser(); if (log.isDebugEnabled())
		 * { log.debug("Entering 'listByUserId' method - user: " + userName); }
		 * WorkflowManager mgr = (WorkflowManager) getBean("workflowManager");
		 * // List processInstances = mgr.findAllProcessInstances();
		 * 
		 * List processInstances = mgr.
		 * findAllProcessInstancesByProcessDefinitionNameExcludingSubProcessInstances
		 * (Constants.MAIN_PROCESS_DEFINITION_NAME);
		 * 
		 * List<ProcessInstanceWrapper> processInstanceWrappers = new
		 * ArrayList<ProcessInstanceWrapper>(); Iterator it =
		 * processInstances.iterator(); while (it.hasNext()) { ProcessInstance
		 * processInstance = (ProcessInstance) it.next(); ProcessInstanceWrapper
		 * piw = new ProcessInstanceWrapper( processInstance); if
		 * (userName.equals(piw.getUsername())) {
		 * processInstanceWrappers.add(piw); } }
		 * request.setAttribute(Constants.PROCESS_INSTANCES_LIST,
		 * processInstanceWrappers);
		 * 
		 * request.setAttribute(Constants.PARAMETER_FROM,
		 * Constants.FORWARD_MY_PROCESSES);
		 * 
		 * // return a forward to the process instances list if
		 * (log.isDebugEnabled()) log.debug(
		 * "ProcessInstanceAction - Exit 'listByUser process instances' method"
		 * ); return mapping.findForward("list");
		 */
	}

	/**
	 * List all process instances
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return ActionForward
	 * @throws Exception
	 */
	public ActionForward listAll(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// invoked from main menu: all processes
		if (log.isDebugEnabled())
			log
					.debug("ProcessInstanceAction - Entering 'list process instances' method");
		// Exceptions are caught by ActionExceptionHandler
		WorkflowManager mgr = (WorkflowManager) getBean("workflowManager");
		// List processInstances = mgr.findAllProcessInstances();
		/*
		 * WebAppBean webAppBean = (WebAppBean) getBean("webAppBean");
		 * ProcessDefinition processDefinition = mgr
		 * .findLatestProcessDefinition(Constants.MAIN_PROCESS_DEFINITION_NAME);
		 * List processInstances = mgr
		 * .findAllProcessInstancesByProcessDefinitionIdExcludingSubProcessInstances
		 * (processDefinition .getId());
		 */
		List processInstances = mgr
				.findAllProcessInstancesByProcessDefinitionNameExcludingSubProcessInstances(Constants.MAIN_PROCESS_DEFINITION_NAME);

		List<ProcessInstanceWrapper> processInstanceWrappers = new ArrayList<ProcessInstanceWrapper>();
		Iterator it = processInstances.iterator();
		while (it.hasNext()) {
			ProcessInstance processInstance = (ProcessInstance) it.next();
			ProcessInstanceWrapper piw = new ProcessInstanceWrapper(
					processInstance);
			processInstanceWrappers.add(piw);
		}
		request.setAttribute(Constants.PROCESS_INSTANCES_LIST,
				processInstanceWrappers);
		// return a forward to the process instances list
		request.setAttribute(Constants.PARAMETER_FROM,
				Constants.FORWARD_ALL_PROCESSES);
		if (log.isDebugEnabled())
			log
					.debug("ProcessInstanceAction - Exit 'list process instances' method");
		return mapping.findForward("list");
	}

	/**
	 * List all process instances for process definition
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return ActionForward
	 * @throws Exception
	 */
	public ActionForward listSubProcessesForProcessInstance(
			ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// INVOKED from processInstanceList: icon sub: param id
		// processInstanceId
		// from all buttons in process monitoring pages
		if (log.isDebugEnabled())
			log
					.debug("ProcessInstanceAction - Entering 'listSubProcessesForProcessInstance' method");
		// Exceptions are caught by ActionExceptionHandler
		String processInstanceId = request.getParameter("id");

		if (log.isDebugEnabled()) {
			log.debug("processInstanceId " + processInstanceId);
		}

		// Exceptions are caught by ActionExceptionHandler
		WorkflowManager mgr = (WorkflowManager) getBean("workflowManager");
		ProcessInstance pi = mgr.getProcessInstanceForUpdate(Long
				.parseLong(processInstanceId));

		Map variableMap = pi.getContextInstance().getVariables();
		String name = (String) variableMap
				.get(JPDLConstants.PROCESS_INSTANCE_NAME);

		List processInstances = mgr.findAllSubProcessInstances(pi);
		List<ProcessInstanceWrapper> processInstanceWrappers = new ArrayList<ProcessInstanceWrapper>();
		Iterator it = processInstances.iterator();
		while (it.hasNext()) {
			ProcessInstance processInstance = (ProcessInstance) it.next();
			ProcessInstanceWrapper piw = new ProcessInstanceWrapper(
					processInstance);
			processInstanceWrappers.add(piw);
		}
		request.setAttribute(Constants.PROCESS_INSTANCES_LIST,
				processInstanceWrappers);
		request.setAttribute("name", name);
		request.setAttribute(Constants.PARAMETER_FROM,
				Constants.FORWARD_SUB_PROCESSES);
		// return a forward to the process instances list
		if (log.isDebugEnabled())
			log
					.debug("ProcessInstanceAction - Exit 'listSubProcessesForProcessInstance' method");
		return mapping.findForward("list");
	}

	/**
	 * Cancel specified process instance
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
			log
					.debug("ProcessInstanceAction - Entering 'cancel process instance' method");
		String processInstanceId = request.getParameter("id");
		String from = request.getParameter("from");
		if (log.isDebugEnabled()) {
			log.debug("processInstanceId: " + processInstanceId);
			log.debug("from: " + from);
		}
		// Exceptions are caught by ActionExceptionHandler
		WorkflowManager mgr = (WorkflowManager) getBean("workflowManager");
		ProcessInstance procesInstance = mgr.getProcessInstance(Long
				.parseLong(processInstanceId));
		if (procesInstance != null) {
			// Check if specified process instance is already ended
			/*
			 * if(procesInstance.hasEnded()){ ActionMessages errors = new
			 * ActionMessages(); errors.add("errors.detail", new
			 * ActionMessage("workflow.ProcessInstance.canNotDelete.error"));
			 * saveErrors(request, errors); return
			 * mapping.findForward("processDefinitionList"); }
			 */
		} else { // check if specified process instance exists
			ActionMessages errors = new ActionMessages();
			errors.add("errors.detail", new ActionMessage(
					"workflow.ProcessInstance.notExists.error"));
			saveErrors(request, errors);
			return mapping.findForward("processDefinitionList");
		}
		mgr.cancelProcessInstance(Long.parseLong(processInstanceId));
		if (log.isDebugEnabled())
			log.debug(" process deleted ");
		ActionMessages messages = new ActionMessages();
		messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
				"workflow.ProcessInstance.canceled"));
		saveMessages(request.getSession(), messages);
		// return a forward to the list process definitions
		if (log.isDebugEnabled())
			log
					.debug("ProcessInstanceAction - Exit 'cancel process instance' method");
		return resolveActionForward(from, processInstanceId, mapping, form,
				request, response);

	}

	public ActionForward cancelAll(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		if (log.isDebugEnabled())
			log
					.debug("ProcessInstanceAction - Entering 'cancel process instance' method");
		// Exceptions are caught by ActionExceptionHandler
		WorkflowManager mgr = (WorkflowManager) getBean("workflowManager");
		String from = request.getParameter("from");
		log.debug("from: " + from);
		String[] processIds = request.getParameterValues("rowId");
		if (processIds == null) {
			ActionMessages errors = new ActionMessages();
			errors.add("errors.detail", new ActionMessage(
					"workflow.ProcessInstance.noInstanceSelected"));
			saveErrors(request, errors);
			// return mapping.findForward("processDefinitionList");
			return dispatchMethod(mapping, form, request, response, "listAll");
		}
		log.debug("@@@ DEL processIds.length " + processIds.length);

		// in case that you invoke delete methd from
		// 1. prosesses for process definition or
		// 2. processes in project
		// set the required attribute in request
		if (Constants.FORWARD_PROCESSES_IN_PROJECT.equals(from)) {
			String projectName = "";
			ProcessInstance pi = mgr.getProcessInstanceForUpdate(new Long(
					processIds[0]));

			// find the parrent
			if (pi != null) {
				Token token = pi.getSuperProcessToken();
				while (token != null) {
					pi = token.getProcessInstance();
					token = pi.getSuperProcessToken();
				}
				projectName = pi.getKey();
			}
			request.setAttribute("projectName", projectName);
		} else if (Constants.FORWARD_PROCESSES_FOR_PROCESSDEFINITION
				.equals(from)) {
			ProcessInstance pi = mgr.getProcessInstanceForUpdate(new Long(
					processIds[0]));
			String processDefinitionId = String.valueOf(pi
					.getProcessDefinition().getId());
			request.setAttribute("id", processDefinitionId);
		}

		for (int i = 0; i < processIds.length; i++) {
			ProcessInstance procesInstance = mgr
					.getProcessInstanceForUpdate(Long.parseLong(processIds[i]));
			if (procesInstance != null) {
				// Check if specified process instance is already ended
				/*
				 * if(procesInstance.hasEnded()){ ActionMessages errors = new
				 * ActionMessages(); errors.add("errors.detail", new
				 * ActionMessage
				 * ("workflow.ProcessInstance.canNotDelete.error"));
				 * saveErrors(request, errors); return
				 * mapping.findForward("processDefinitionList"); }
				 */
			} else { // check if specified process instance exists
				ActionMessages errors = new ActionMessages();
				errors.add("errors.detail", new ActionMessage(
						"workflow.ProcessInstance.notExists.error"));
				saveErrors(request, errors);
				return mapping.findForward(Constants.FORWARD_ALL_PROCESSES);
			}
			mgr.cancelProcessInstance(Long.parseLong(processIds[i]));
		}

		if (log.isDebugEnabled())
			log.debug(" process deleted ");
		ActionMessages messages = new ActionMessages();
		messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
				"workflow.ProcessInstance.canceled"));
		saveMessages(request.getSession(), messages);
		// return a forward to the list process definitions
		if (log.isDebugEnabled())
			log
					.debug("ProcessInstanceAction - Exit 'cancel process instance' method");

		return resolveActionForward(from, processIds[0], mapping, form,
				request, response);

	}

	/**
	 * Suspend specified process instance
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
		// INVOKED from processInstanceList: icon resume: param id
		// processInstanceId
		if (log.isDebugEnabled())
			log
					.debug("ProcessInstanceAction - Entering 'suspend process instance' method");
		String processInstanceId = request.getParameter("id");
		String from = request.getParameter("from");
		if (log.isDebugEnabled()) {
			log.debug("processInstanceId " + processInstanceId);
			log.debug("from " + from);
		}
		ActionMessages messages = new ActionMessages();
		// Exceptions are caught by ActionExceptionHandler
		WorkflowManager mgr = (WorkflowManager) getBean("workflowManager");
		ProcessInstance procesInstance = mgr.getProcessInstanceForUpdate(Long
				.parseLong(processInstanceId));
		ActionMessages errors = new ActionMessages();
		if (procesInstance != null) {
			// check if specified process instance is ended or suspended
			if (procesInstance.isSuspended() || procesInstance.hasEnded()) {

				errors.add("errors.detail", new ActionMessage(
						"workflow.ProcessInstance.canNotSuspend.error"));
				saveErrors(request, errors);
				return resolveActionForward(from, processInstanceId, mapping,
						form, request, response);

			}
		} else { // check if specified process instance exists
			errors.add("errors.detail", new ActionMessage(
					"workflow.ProcessInstance.notExists.error"));
			saveErrors(request, errors);
			return resolveActionForward(from, processInstanceId, mapping, form,
					request, response);

		}
		try {
			mgr.suspendProcessInstance(Long.parseLong(processInstanceId));
		} catch (WorkflowException e) {
			errors.add("errors.detail", new ActionMessage(
					"workflow.ProcessInstance.canNotSuspend.error"));
			saveErrors(request, errors);
			return resolveActionForward(from, processInstanceId, mapping, form,
					request, response);

		}

		messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
				"workflow.ProcessInstance.suspended"));
		saveMessages(request.getSession(), messages);
		// return a forward to the list process definitions
		if (log.isDebugEnabled())
			log
					.debug("ProcessInstanceAction - Exit 'suspend process instance' method");
		return resolveActionForward(from, processInstanceId, mapping, form,
				request, response);

	}

	/**
	 * Resumes specified process instance
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
		// INVOKED from processInstanceList: icon resume: param id
		// processInstanceId
		if (log.isDebugEnabled())
			log
					.debug("ProcessInstanceAction - Entering 'resume process instance' method");
		String processInstanceId = request.getParameter("id");
		String from = request.getParameter("from");
		if (log.isDebugEnabled()) {
			log.debug("processInstanceId " + processInstanceId);
			log.debug("from " + from);
		}
		ActionMessages messages = new ActionMessages();
		// Exceptions are caught by ActionExceptionHandler
		WorkflowManager mgr = (WorkflowManager) getBean("workflowManager");
		ProcessInstance procesInstance = mgr.getProcessInstanceForUpdate(Long
				.parseLong(processInstanceId));
		if (procesInstance != null) {
			// check if specified process instance is ended or suspended
			if (!procesInstance.isSuspended()) {
				ActionMessages errors = new ActionMessages();
				errors.add("errors.detail", new ActionMessage(
						"workflow.ProcessInstance.canNotResume.error"));
				saveErrors(request, errors);
				return resolveActionForward(from, processInstanceId, mapping,
						form, request, response);

			}
		} else { // check if specified process instance exists
			ActionMessages errors = new ActionMessages();
			errors.add("errors.detail", new ActionMessage(
					"workflow.ProcessInstance.notExists.error"));
			saveErrors(request, errors);
			return resolveActionForward(from, processInstanceId, mapping, form,
					request, response);

		}
		mgr.resumeProcessInstance(Long.parseLong(processInstanceId));
		if (log.isDebugEnabled())
			log.debug(" process resumed ");
		messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
				"workflow.ProcessInstance.resumed"));
		saveMessages(request.getSession(), messages);
		// return a forward to the list process definitions
		if (log.isDebugEnabled())
			log
					.debug("ProcessInstanceAction - Exit 'resume process instance' method");
		return resolveActionForward(from, processInstanceId, mapping, form,
				request, response);

	}

	public ActionForward end(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// INVOKED from processInstanceList: icon end: param id
		// processInstanceId
		if (log.isDebugEnabled())
			log
					.debug("ProcessInstanceAction - Entering 'end process instance' method");
		String processInstanceId = request.getParameter("id");
		String from = request.getParameter("from");
		if (log.isDebugEnabled()) {
			log.debug("processInstanceId " + processInstanceId);
			log.debug("from " + from);
		}
		ActionMessages messages = new ActionMessages();
		// Exceptions are caught by ActionExceptionHandler
		WorkflowManager mgr = (WorkflowManager) getBean("workflowManager");
		ProcessInstance procesInstance = mgr.getProcessInstanceForUpdate(Long
				.parseLong(processInstanceId));
		if (procesInstance != null) {
			// check if specified process instance is ended or suspended
			if (procesInstance.hasEnded()) {
				ActionMessages errors = new ActionMessages();
				errors.add("errors.detail", new ActionMessage(
						"workflow.ProcessInstance.canNotEnd.error"));
				saveErrors(request, errors);
				return resolveActionForward(from, processInstanceId, mapping,
						form, request, response);

			}
		} else { // check if specified process instance exists
			ActionMessages errors = new ActionMessages();
			errors.add("errors.detail", new ActionMessage(
					"workflow.ProcessInstance.notExists.error"));
			saveErrors(request, errors);
			return resolveActionForward(from, processInstanceId, mapping, form,
					request, response);

		}
		mgr.endProcessInstance(Long.parseLong(processInstanceId));
		if (log.isDebugEnabled())
			log.debug(" process ended ");
		messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
				"workflow.ProcessInstance.ended"));
		saveMessages(request.getSession(), messages);
		// return a forward to the list process definitions
		if (log.isDebugEnabled())
			log
					.debug("ProcessInstanceAction - Exit 'end process instance' method");
		return resolveActionForward(from, processInstanceId, mapping, form,
				request, response);

	}

	/*
	 * redirects to create process instance form
	 */
	public ActionForward edit(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String username = request.getRemoteUser();
		String projectId = request.getParameter("projectId");
		String corpusId = request.getParameter("corpusId");
		ProcessInstanceForm processInstanceForm = (ProcessInstanceForm) form;

		WorkflowManager workflowManager = (WorkflowManager) getBean("workflowManager");
		UserManager userManager = (UserManager) getBean("userManager");
		ProjectManager projectManager = (ProjectManager) getBean("projectManager");
		Project project = projectManager.getProject(new Long(projectId));
		byte[] data = project.getData();
		Map<String, Object> variableMap = XstreamUtil.fromByteArrayToMap(data);
		// check if doManual
		if (variableMap.get(JPDLConstants.DO_MANUAL) != null
				&& "on".equals((String) variableMap
						.get(JPDLConstants.DO_MANUAL))) {
			request.setAttribute(Constants.ANNOTATOR_LIST, userManager
					.getUsersWithRole(Constants.ANNOTATOR_ROLE));

			// request.setAttribute(Constants.CURATOR_LIST,
			// userManager.getUsersWithRole(Constants.CURATOR_ROLE));

		}
		/*
		 * else if (variableMap.get(JPDLConstants.DO_REVIEW) != null &&
		 * "on".equals((String) variableMap .get(JPDLConstants.DO_REVIEW))) {
		 * request.setAttribute(Constants.CURATOR_LIST, userManager
		 * .getUsersWithRole(Constants.CURATOR_ROLE)); }
		 */

		// set the managers in request
		request.setAttribute(Constants.MANAGER_LIST, userManager
				.getUsersWithRole(Constants.MANAGER_ROLE));
		DocServiceManager documentManager = (DocServiceManager) getBean("docServiceManager");
		// find the corpora
		List<Corpus> corpora = documentManager.listCorpora();
		List<Corpus> availableCorpora = new ArrayList<Corpus>();
		ProcessDefinition processDefinition = null;
		if (project.getVersion() != null && project.getVersion() > 0) {
			processDefinition = workflowManager
					.findProcessDefinitionByNameAndVersion(
							Constants.MAIN_PROCESS_DEFINITION_NAME, project
									.getVersion());
		}

		// filter the ones that are used by other projects.
		Iterator<Corpus> itr = corpora.iterator();
		while (itr.hasNext()) {
			Corpus corpus = itr.next();
			boolean locked = workflowManager
					.isAnyProcessInstanceRunningAgainstTheCorpus(
							processDefinition.getName(), corpus.getCorpusID());
			if (!locked) {
				availableCorpora.add(corpus);
			}
		}

		// now sort available corpora
		Collections.sort(availableCorpora);

		Corpus none = new Corpus();
		none.setCorpusID("none");
		Iterator<String> it = getMessages(request).get("label.noSelection");
		String blankOption;
		if (it.hasNext()) {
			blankOption = it.next();
		} else {
			blankOption = "No Selection";
		}
		none.setCorpusName(blankOption);

		availableCorpora.add(0, none);

		request.setAttribute(Constants.CORPUS_LIST, availableCorpora);
		if (corpusId != null) {
			processInstanceForm.setCorpusId(corpusId);
		}
		processInstanceForm.setManager(username);
		return mapping.findForward("edit");
	}

	/*
	 * redirects to create process instance form
	 */

	/*
	 * creates and starts process instance from project
	 */
	public ActionForward save(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		if (log.isDebugEnabled()) {
			log.debug("Entering 'save Process Instance' method");
		}
		
		ProcessInstanceForm processInstanceForm = (ProcessInstanceForm) form;
		String projectId = processInstanceForm.getProjectId();
		String name = processInstanceForm.getName();
		String corpusId = processInstanceForm.getCorpusId();
		String[] annotatorNames = request.getParameterValues(Constants.ANNOTATOR_ROLE + "s");

		String suffix = "CSVList";
		// fetch users and covert them to csv
		ProjectManager projectManager = (ProjectManager) getBean("projectManager");
		UserManager userManager = (UserManager) getBean("userManager");
		WorkflowManager workflowManager = (WorkflowManager) getBean("workflowManager");
		ProcessStartCommandProducer processStartCommandProducer = (ProcessStartCommandProducer) getBean("processStartCommandProducer");
		DocServiceManager documentManager = (DocServiceManager) getBean("docServiceManager");

		Project project = projectManager.getProject(new Long(projectId));
		byte[] data = project.getData();
		Map<String, Object> variableMap = XstreamUtil.fromByteArrayToMap(data);
		ActionMessages errors = new ActionMessages();
		
		// find out what to fetch and validate
		ProcessDefinition processDefinition = null;
		if (project.getVersion() != null && project.getVersion() > 0) {
			processDefinition = workflowManager.findProcessDefinitionByNameAndVersion(
							Constants.MAIN_PROCESS_DEFINITION_NAME, project.getVersion());
		}
		
		// check if doManual
		if (variableMap.get(JPDLConstants.DO_MANUAL) != null
				&& "on".equals((String) variableMap.get(JPDLConstants.DO_MANUAL))) {
			/*
			 * String[] curatorNames = request
			 * .getParameterValues(Constants.CURATOR_ROLE + "s"); if
			 * (curatorNames == null || curatorNames.length == 0) { // errors
			 * errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
			 * "errors.required", Constants.CURATOR_ROLE + "s")); } else {
			 * String csvList = StringUtils
			 * .arrayToCommaDelimitedString(curatorNames);
			 * variableMap.put(Constants.CURATOR_ROLE + suffix, csvList);
			 * 
			 * }
			 */
			
			if (annotatorNames == null || annotatorNames.length == 0) {
				// errors
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
						"errors.required", Constants.ANNOTATOR_ROLE + "s"));
			} else {
				String csvList = StringUtils.arrayToCommaDelimitedString(annotatorNames);
				variableMap.put(Constants.ANNOTATOR_ROLE + suffix, csvList);
			}

		}
		/*
		 * else if (variableMap.get(JPDLConstants.DO_REVIEW) != null &&
		 * "on".equals((String) variableMap .get(JPDLConstants.DO_REVIEW))) {
		 * String[] curatorNames = request
		 * .getParameterValues(Constants.CURATOR_ROLE + "s"); if (curatorNames
		 * == null || curatorNames.length == 0) { // errors
		 * errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
		 * "errors.required", Constants.CURATOR_ROLE + "s")); } else { String
		 * csvList = StringUtils .arrayToCommaDelimitedString(curatorNames);
		 * variableMap.put(Constants.CURATOR_ROLE + suffix, csvList);
		 * 
		 * } }
		 */

		String manager = processInstanceForm.getManager();
		variableMap.put(JPDLConstants.INITIATOR, manager);

		if (log.isDebugEnabled()) {
			log.debug("projectId: " + projectId);
			log.debug("name: " + name);
			log.debug("corpusId: " + corpusId);
		}

		// add corpus Id
		if (!"none".equals(corpusId)) {
			variableMap.put(JPDLConstants.CORPUS_ID, corpusId);
		} else {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
					"errors.corpus.required"));
		}
		if (name != null) {
			variableMap.put(JPDLConstants.PROCESS_INSTANCE_NAME, name);
		}

		long processDefinitionId = processDefinition.getId();
		log.debug("processDefinition: " + processDefinitionId);

		// check if there is running process on corpus
		if (workflowManager.isAnyProcessInstanceRunningAgainstTheCorpus(
				Constants.MAIN_PROCESS_DEFINITION_NAME, corpusId)) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
					"errors.someOtherProcessIsUsingThisCorpus"));
		}
		
		List<Document> documents = new ArrayList<Document>();
		if(!"none".equals(corpusId)) {
			// see how many docs are there
			documents = documentManager.listDocuments(corpusId);
			if (documents == null || documents.size() == 0) {
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
						"errors.emptyCorpus"));
			}
		}
		
		if (errors.size() > 0) {
			saveErrors(request, errors);

			// find the corpora
			List<Corpus> corpora = documentManager.listCorpora();
			List<Corpus> availableCorpora = new ArrayList<Corpus>();
			Iterator<Corpus> itr = corpora.iterator();
			while (itr.hasNext()) {
				Corpus corpus = itr.next();
				boolean locked = workflowManager
						.isAnyProcessInstanceRunningAgainstTheCorpus(
								processDefinition.getName(), corpus.getCorpusID());
				if (!locked) {
					availableCorpora.add(corpus);
				}
			}

			// now sort available corpora
			Collections.sort(availableCorpora);

			Corpus none = new Corpus();
			none.setCorpusID("none");
			Iterator<String> it = getMessages(request).get("label.noSelection");
			String blankOption;
			if (it.hasNext()) {
				blankOption = it.next();
			} else {
				blankOption = "No Selection";
			}
			none.setCorpusName(blankOption);
			availableCorpora.add(0, none);
			request.setAttribute(Constants.CORPUS_LIST, availableCorpora);
			
			// request.setAttribute(Constants.CORPUS_LIST, corpora);
			request.setAttribute(Constants.MANAGER_LIST, userManager
					.getUsersWithRole(Constants.MANAGER_ROLE));

			if (variableMap.get(JPDLConstants.DO_MANUAL) != null
					&& "on".equals((String) variableMap
							.get(JPDLConstants.DO_MANUAL))) {
				// request.setAttribute(Constants.CURATOR_LIST,
				// userManager.getUsersWithRole(Constants.CURATOR_ROLE));
				request.setAttribute(Constants.ANNOTATOR_LIST, userManager
						.getUsersWithRole(Constants.ANNOTATOR_ROLE));
			}
			/*
			 * else if (variableMap.get(JPDLConstants.DO_REVIEW) != null &&
			 * "on".equals((String) variableMap .get(JPDLConstants.DO_REVIEW)))
			 * { request.setAttribute(Constants.CURATOR_LIST, userManager
			 * .getUsersWithRole(Constants.CURATOR_ROLE)); }
			 */
			return mapping.findForward("edit");
		}
		
		ActionForward actionForward = null;

		ActionMessages messages = new ActionMessages();
		// if number of docs in corpus > 100, start process in async way.
		if (documents.size() > 100) {
			ProcessStartCommand processStartCommand = new ProcessStartCommand(
					project.getName(), variableMap, processDefinitionId);
			// now async start process Instance
			processStartCommandProducer.sendMessage(processStartCommand);
			actionForward = mapping.findForward("info");
			messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
					"processInstanceIsAboutToStart", name));
		} else {
			workflowManager.createStartProcessInstance(processDefinitionId,variableMap, 
					project.getName());
			ActionForward ai = mapping
			.findForward(Constants.FORWARD_PROCESSES_IN_PROJECT);
			actionForward = new ActionForward(ai);
			String path = ai.getPath() + "&projectName=" + project.getName() + "&from="
					+ Constants.FORWARD_PROCESSES_IN_PROJECT;
			actionForward.setPath(path);
			actionForward.setRedirect(true);
			messages.add(ActionMessages.GLOBAL_MESSAGE, 
					new ActionMessage("processInstance.is.started", name));
		}

		saveMessages(request.getSession(), messages);
		
		if (log.isDebugEnabled()) 
			log.debug("Exit 'save process instance' method");
		
		return actionForward;
	}

	private ActionForward resolveActionForward(String from,
			String processInstanceId, ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ActionForward actionForward = null;

		if (Constants.FORWARD_PROCESSES_FOR_PROCESSDEFINITION.equals(from)) {
			request.setAttribute("processInstanceId", processInstanceId);
			actionForward = dispatchMethod(mapping, form, request, response,
					"list");
		} else if (Constants.FORWARD_PROCESSES_IN_PROJECT.equals(from)) {
			request.setAttribute("processInstanceId", processInstanceId);
			actionForward = dispatchMethod(mapping, form, request, response,
					"listTopProcessesWithTheSameKeyAndName");
		} else {
			// by default go to my processes
			actionForward = dispatchMethod(mapping, form, request, response,
					"listAll");
		}

		return actionForward;
	}

}
