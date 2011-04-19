/*
 *  ProcessDefinitionAction.java
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

import java.util.HashMap;
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

import gleam.executive.Constants;
import gleam.executive.workflow.manager.WorkflowManager;
import gleam.executive.workflow.util.JPDLConstants;

//TODO Javadoc
/**
 *
 * @author <a href="mailto:agaton@dcs.shef.ac.uk">Milan Agatonovic</a>
 *
 * @struts.action path="/processDefinitionList" scope="request" validate="false"
 *                parameter="method" input="processDefinitionList"
 *
 * @struts.action-forward name="list"
 *                        path="/WEB-INF/pages/processDefinitionList.jsp"
 */
public class ProcessDefinitionAction extends BaseAction {
	/**
	 * List all startable Process Definitions
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
		if (log.isDebugEnabled()) {
			log
					.debug("ProcessDefinitionAction - Entering 'list process definitions' method");
		}
		// Exceptions are caught by ActionExceptionHandler
		WorkflowManager mgr = (WorkflowManager) getBean("workflowManager");
		List processDefinitions = mgr.findStartableProcessDefinitions();
		request.setAttribute(Constants.PROCESS_DEFINITION_LIST,
				processDefinitions);
		if (log.isDebugEnabled()) {
			log
					.debug("ProcessDefinitionAction - Exit 'list process definitions' method");
		}
		// return a forward to the process definition list
		return mapping.findForward("list");
	}

	/**
	 * Delete specified process definition (if there is no process any instances
	 * for specified definition)
	 *
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return ActionForward
	 * @throws Exception
	 */
	public ActionForward delete(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		if (log.isDebugEnabled())
			log
					.debug("ProcessDefinitionAction - Entering 'delete process definition' method");
		String processDefinitionId = request.getParameter("id");
		if (log.isDebugEnabled())
			log.debug("processDefinitionId " + processDefinitionId);
		long pdID = Long.parseLong(processDefinitionId);
		// Exceptions are caught by ActionExceptionHandler
		WorkflowManager mgr = (WorkflowManager) getBean("workflowManager");
		// Check if exists process instance for specified definition

		/*
		 * List proceessInstances = mgr.findAllProcessInstances(pdID);
		 * if(proceessInstances != null && proceessInstances.size() != 0) {
		 */

		// now allow deletion of process defnitions that have completed
		// instances
		int numOfUncompletedProcessInstances = mgr
				.findUncompletedProcessInstancesForProcessDefinition(pdID);
		if (numOfUncompletedProcessInstances != 0) {
			if (log.isDebugEnabled()) {

				log
						.warn(" There are uncompleted instances for this process definition. Display error message!");
			}
			ActionMessages errors = new ActionMessages();
			errors.add("errors.detail", new ActionMessage(
					"workflow.ProcessDefinition.canNotDelete.error", pdID));
			saveErrors(request, errors);
			// return a forward to list process definitions
			return mapping.findForward("processDefinitionList");
		}
		mgr.undeployProcessDefinition(pdID);
		if (log.isDebugEnabled())
			log.debug(" process deleted ");
		ActionMessages messages = new ActionMessages();
		messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
				"workflow.ProcessDefinition.deleted", pdID));
		saveMessages(request.getSession(), messages);
		// return a forward to process definition list
		if (log.isDebugEnabled())
			log
					.debug("ProcessDefinitionAction - Exit 'delete process definition' method");
		return mapping.findForward("processDefinitionList");
	}

	/**
	 * Delete specified process definition (if there is no process any instances
	 * for specified definition)
	 *
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return ActionForward
	 * @throws Exception
	 */
	public ActionForward deleteAll(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		if (log.isDebugEnabled())
			log
					.debug("ProcessDefinitionAction - Entering 'delete process definition' method");
		String processDefinitionId = request.getParameter("id");
		if (log.isDebugEnabled())
			log.debug("processDefinitionId " + processDefinitionId);
		long pdID = Long.parseLong(processDefinitionId);
		// Exceptions are caught by ActionExceptionHandler
		WorkflowManager mgr = (WorkflowManager) getBean("workflowManager");
		ProcessDefinition pd = mgr.findProcessDefinition(pdID);
		String pdName = pd.getName();
		while (mgr.findLatestProcessDefinition(pdName) != null) {
			ProcessDefinition definition = mgr
					.findLatestProcessDefinition(pdName);
			// Check if exists process instance for specified definition

			int numOfUncompletedProcessInstances = mgr
					.findUncompletedProcessInstancesForProcessDefinition(definition.getId());
			log
					.debug(" numOfUncompletedProcessInstances for processDefinition: '"
							+ pdID
							+ "' is '"
							+ numOfUncompletedProcessInstances + "'");
			if (numOfUncompletedProcessInstances != 0) {

				if (log.isDebugEnabled()) {

					log
							.warn(" There are uncompleted instances for this process definition. Display error message!");
				}
				ActionMessages errors = new ActionMessages();
				errors.add("errors.detail", new ActionMessage(
						"workflow.ProcessDefinition.canNotDelete.error", definition.getId()));
				saveErrors(request, errors);
				// return a forward to list process definitions
				return mapping.findForward("processDefinitionList");
			}
				mgr.undeployProcessDefinition(definition);
				if (log.isDebugEnabled())
					log.debug(" process deleted "+definition.getId());

				ActionMessages messages = new ActionMessages();
				messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
						"workflow.ProcessDefinition.deleted", definition.getId()));
				saveMessages(request.getSession(), messages);

		}

		// return a forward to process definition list
		if (log.isDebugEnabled())
			log
					.debug("ProcessDefinitionAction - Exit 'delete process definition' method");
		return mapping.findForward("processDefinitionList");
	}

	/**
	 * Start specified process definition
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
			log.debug("Entering 'start process definition' method");
		String processDefinitionId = request.getParameter("id");
		if (log.isDebugEnabled())
			log.debug("processDefinitionId " + processDefinitionId);
		// Exceptions are caught by ActionExceptionHandler
		WorkflowManager mgr = (WorkflowManager) getBean("workflowManager");
		Map<String, Object> variableMap = new HashMap<String, Object>();
		variableMap.put(JPDLConstants.INITIATOR, request.getRemoteUser());
		mgr.createStartProcessInstance(Long.parseLong(processDefinitionId),variableMap,null);
		// Save message
		if (log.isDebugEnabled())
			log.debug(" process instance started ");
		ActionMessages messages = new ActionMessages();
		messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
				"workflow.ProcessDefinition.started"));
		saveMessages(request.getSession(), messages);
		if (log.isDebugEnabled())
			log.debug("Exit 'start process definition' method");
		// return a forward to process definition list
		ActionForward actionForward = mapping
				.findForward("processInstanceListForProcessDefinition");
		ActionForward newActionForward = new ActionForward(actionForward);
		newActionForward.setPath(actionForward.getPath() + "&id="
				+ processDefinitionId);
		return newActionForward;
	}
}
