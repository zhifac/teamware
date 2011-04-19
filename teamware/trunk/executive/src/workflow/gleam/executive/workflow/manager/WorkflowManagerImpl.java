/*
 *  WorkflowManagerImpl.java
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
 *  Milan Agatonovic
 *
 *  $Id$
 */
package gleam.executive.workflow.manager;

import gleam.executive.model.WebAppBean;
import gleam.executive.service.DocServiceManager;
import gleam.executive.service.GosManager;
import gleam.executive.service.UserManager;
import gleam.executive.service.impl.BaseManager;
import gleam.executive.workflow.core.JbpmTaskInstance;
import gleam.executive.workflow.model.AnnotationMetricMatrix;
import gleam.executive.workflow.model.AnnotationStatusInfo;
import gleam.executive.workflow.model.SwimlaneBean;
import gleam.executive.workflow.model.TaskBean;
import gleam.executive.workflow.model.TaskFormParameterBean;
import gleam.executive.workflow.sm.JbpmTemplate;
import gleam.executive.workflow.util.JPDLConstants;
import gleam.executive.workflow.util.WorkflowException;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import org.jbpm.context.def.VariableAccess;
import org.jbpm.file.def.FileDefinition;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.taskmgmt.def.Swimlane;
import org.jbpm.taskmgmt.def.Task;
import org.jbpm.taskmgmt.exe.TaskInstance;

public class WorkflowManagerImpl extends BaseManager implements WorkflowManager {
	private JbpmTemplate jbpmTemplate;

	private UserManager userManager;

	private WebAppBean webAppBean;

	private DocServiceManager docServiceManager;

	private GosManager gosManager;

	/**
	 * Regular expression pattern that matches task_N and allows us to extract
	 * the N value.
	 */
	

	/**
	 * @see gleam.executive.workflow.manager.WorkflowManager#cancelProcessInstance(long)
	 */
	public void cancelProcessInstance(long processInstanceId)
			throws WorkflowException {
		try {
			jbpmTemplate.cancelProcessInstance(processInstanceId);
		} catch (Exception e) {
			throw new WorkflowException(e);
		}

	}
	
	/**
	 * @see gleam.executive.workflow.manager.WorkflowManager#endProcessInstance(long)
	 */
	public void endProcessInstance(long processInstanceId)
			throws WorkflowException {
		try {
			jbpmTemplate.endProcessInstance(processInstanceId);
		} catch (Exception e) {
			throw new WorkflowException(e);
		}

	}

	/**
	 * @see gleam.executive.workflow.manager.WorkflowManager#cancelTaskInstance(long)
	 */
	public void cancelTaskInstance(long taskInstanceId)
			throws WorkflowException {
		try {
			jbpmTemplate.cancelTaskInstance(taskInstanceId);
		} catch (Exception e) {
			throw new WorkflowException(e);
		}
	}
	
	/**
	 * @see gleam.executive.workflow.manager.WorkflowManager#endTaskInstance(long)
	 */
	public void endTaskInstance(long taskInstanceId)
			throws WorkflowException {
		try {
			jbpmTemplate.endTaskInstance(taskInstanceId);
		} catch (Exception e) {
			throw new WorkflowException(e);
		}
	}

	/**
	 * @see gleam.executive.workflow.manager.WorkflowManager#createProcessInstance(long,
	 *      java.util.Map, String)
	 */
	public ProcessInstance createProcessInstance(long definitionId,
			Map variableMap, String key) throws WorkflowException {
		ProcessInstance processInstance = null;
		try {
			processInstance = jbpmTemplate.createProcessInstance(definitionId,
					variableMap, key);
		} catch (Exception e) {
			throw new WorkflowException(e);
		}
		return processInstance;
	}
	
	/**
	 * @see gleam.executive.workflow.manager.WorkflowManager#startProcessInstance(startProcessInstance(org.jbpm.graph.exe.ProcessInstance))
	 */
	public ProcessInstance startProcessInstance(ProcessInstance processInstance) throws WorkflowException {
		ProcessInstance pi = null;
		try {
			pi = jbpmTemplate.startProcessInstance(processInstance);
		} catch (Exception e) {
			throw new WorkflowException(e);
		}
		return pi;
	}

	/**
	 * @see gleam.executive.workflow.manager.WorkflowManager#addCommentToRootToken(org.jbpm.graph.exe.ProcessInstance,
	 *      String)
	 */
	public void addCommentToRootToken(ProcessInstance processInstance,
			final String comment) throws WorkflowException {
		try {
			jbpmTemplate.addCommentToRootToken(processInstance, comment);
		} catch (Exception e) {
			throw new WorkflowException(e);
		}
	}

	/**
	 * @see gleam.executive.workflow.manager.WorkflowManager#createStartProcessInstance(long,
	 *      java.util.Map, String)
	 */
	public ProcessInstance createStartProcessInstance(long definitionId,
			Map variableMap, String key) throws WorkflowException {
		ProcessInstance processInstance = null;
		try {
			processInstance = jbpmTemplate.createStartProcessInstance(
					definitionId, variableMap, key);
		} catch (Exception e) {
			throw new WorkflowException(e);
		}
		return processInstance;
	}

	/**
	 * @see gleam.executive.workflow.manager.WorkflowManager#deployProcessFromArchive(java.io.InputStream)
	 */
	public void deployProcessFromArchive(InputStream inputStream)
			throws WorkflowException {
		try {
			jbpmTemplate.deployProcessFromArchive(inputStream);
		} catch (Exception e) {
			throw new WorkflowException(e);
		}
	}

	/**
	 * @see gleam.executive.workflow.manager.WorkflowManager#deployProcessDefinition(org.jbpm.graph.def.ProcessDefinition)
	 */
	public void deployProcessDefinition(ProcessDefinition processDefinition)
			throws WorkflowException {

		try {
			jbpmTemplate.deployProcessDefinition(processDefinition);
		} catch (Exception e) {
			throw new WorkflowException(e);
		}
	}

	/**
	 * @see gleam.executive.workflow.manager.WorkflowManager#findAllPooledTaskInstances(String)
	 */
	public List findAllPooledTaskInstances(String actorId)
			throws WorkflowException {
		List list = null;
		try {
			list = jbpmTemplate.findAllPooledTaskInstances(actorId);
		} catch (Exception e) {
			throw new WorkflowException(e);
		}
		return list;
	}

	/**
	 * @see gleam.executive.workflow.manager.WorkflowManager#findAllProcessInstances()
	 */
	public List findAllProcessInstances() throws WorkflowException {
		List list = null;
		try {
			list = jbpmTemplate.findAllProcessInstances();
		} catch (Exception e) {
			throw new WorkflowException(e);
		}
		return list;
	}

	/**
	 * @see gleam.executive.workflow.manager.WorkflowManager#findAllProcessInstancesExcludingSubProcessInstances()
	 */
	public List<ProcessInstance> findAllProcessInstancesExcludingSubProcessInstances()
			throws WorkflowException {
		List<ProcessInstance> list = null;
		try {
			list = jbpmTemplate
					.findAllProcessInstancesExcludingSubProcessInstances();
		} catch (Exception e) {
			throw new WorkflowException(e);
		}
		return list;
	}

	/**
	 * @see gleam.executive.workflow.manager.WorkflowManager#findProcessInstancesExcludingSubProcessInstancesByKey(java.lang.String)
	 */
	public List<ProcessInstance> findProcessInstancesExcludingSubProcessInstancesByKeyAndName(
			String key, String name) throws WorkflowException {
		List<ProcessInstance> list = null;
		try {
			list = jbpmTemplate
					.findProcessInstancesExcludingSubProcessInstancesByKeyAndName(
							key, name);
		} catch (Exception e) {
			throw new WorkflowException(e);
		}
		return list;
	}

	/**
	 * @see gleam.executive.workflow.manager.WorkflowManager#findAllProcessInstancesByProcessDefinitionIdExcludingSubProcessInstances(long)
	 */
	public List<ProcessInstance> findAllProcessInstancesByProcessDefinitionIdExcludingSubProcessInstances(
			long processDefinitionId) throws WorkflowException {
		List<ProcessInstance> list = null;
		try {
			list = jbpmTemplate
					.findAllProcessInstancesByProcessDefinitionIdExcludingSubProcessInstances(processDefinitionId);
		} catch (Exception e) {
			throw new WorkflowException(e);
		}
		return list;
	}
	
	/**
	 * @see gleam.executive.workflow.manager.WorkflowManager#findAllProcessInstancesByProcessDefinitionNameExcludingSubProcessInstances(String)
	 */
	public List<ProcessInstance> findAllProcessInstancesByProcessDefinitionNameExcludingSubProcessInstances(
			String processDefinitionName) throws WorkflowException {
		List<ProcessInstance> list = null;
		try {
			list = jbpmTemplate
					.findAllProcessInstancesByProcessDefinitionNameExcludingSubProcessInstances(processDefinitionName);
		} catch (Exception e) {
			throw new WorkflowException(e);
		}
		return list;
	}

	/**
	 * @see gleam.executive.workflow.manager.WorkflowManager#findAllProcessInstances(long)
	 */
	public List findAllProcessInstances(long definitionId)
			throws WorkflowException {
		List list = null;
		try {
			list = jbpmTemplate.findAllProcessInstances(definitionId);
		} catch (Exception e) {
			throw new WorkflowException(e);
		}
		return list;
	}

	/**
	 * @see gleam.executive.workflow.manager.WorkflowManager#findAllTaskInstances(String)
	 */
	public List findAllTaskInstances(String actorId) throws WorkflowException {
		List list = null;
		try {
			list = jbpmTemplate.findAllTaskInstances(actorId);
			log.debug("task size: "+list.size() + " for actor: "+actorId);
		} catch (Exception e) {
			throw new WorkflowException(e);
		}
		return list;
	}

	/**
	 * @see org.springmodules.workflow.jbpm31.WorkflowManager#findFileDefinition(long)
	 *      ,
	 */
	public FileDefinition findFileDefinition(final long processDefinitionId)
			throws WorkflowException {
		FileDefinition fileDefinition = null;
		try {
			fileDefinition = jbpmTemplate
					.findFileDefinition(processDefinitionId);
		} catch (Exception e) {
			throw new WorkflowException(e);
		}
		return fileDefinition;
	}

	/**
	 * @see gleam.executive.workflow.manager.WorkflowManager#findPooledTaskInstances(String)
	 */
	public List findPooledTaskInstances(String actorId)
			throws WorkflowException {
		List list = null;
		try {
			list = jbpmTemplate.findPooledTaskInstances(actorId);
		} catch (Exception e) {
			throw new WorkflowException(e);
		}
		return list;
	}

	/**
	 * @see gleam.executive.workflow.manager.WorkflowManager#findPooledTaskInstances(String)
	 */
	public List findPendingPooledAnnotationTaskInstances(String actorId)
			throws WorkflowException {
		List list = null;
		try {
			list = jbpmTemplate
					.findPendingPooledAnnotationTaskInstances(actorId);
		} catch (Exception e) {
			throw new WorkflowException(e);
		}
		return list;
	}

	/**
	 * @see gleam.executive.workflow.manager.WorkflowManager#findProcessDefinition(long)
	 */
	public ProcessDefinition findProcessDefinition(long definitionId)
			throws WorkflowException {
		ProcessDefinition processDefinition = null;
		try {
			processDefinition = jbpmTemplate
					.findProcessDefinition(definitionId);
		} catch (Exception e) {
			throw new WorkflowException(e);
		}
		return processDefinition;
	}

	/**
	 * @see gleam.executive.workflow.manager.WorkflowManager#findProcessDefinitionByNameAndVersion(String, Integer)
	 */
	public ProcessDefinition findProcessDefinitionByNameAndVersion(String name, Integer version)
			throws WorkflowException {
		ProcessDefinition processDefinition = null;
		try {
			processDefinition = jbpmTemplate
					.findProcessDefinitionByNameAndVersion(name, version);
		} catch (Exception e) {
			throw new WorkflowException(e);
		}
		return processDefinition;
	}
	
	/**
	 * @see gleam.executive.workflow.manager.WorkflowManager#findProcessDefinitionsByName(String)
	 */
	public List<ProcessDefinition> findProcessDefinitionsByName(String name) throws WorkflowException {
			List list = null;
			try {
				list = jbpmTemplate
						.findProcessDefinitionsByName(name);
			} catch (Exception e) {
				throw new WorkflowException(e);
			}
			return list;
	}

	
	/**
	 * @see gleam.executive.workflow.manager.WorkflowManager#loadProcessDefinition(long)
	 */
	public ProcessDefinition loadProcessDefinition(long definitionId)
			throws WorkflowException {
		ProcessDefinition processDefinition = null;
		try {
			processDefinition = jbpmTemplate
					.loadProcessDefinition(definitionId);
		} catch (Exception e) {
			throw new WorkflowException(e);
		}
		return processDefinition;
	}

	public ProcessDefinition findLatestProcessDefinition(String definitionName)
			throws WorkflowException {
		ProcessDefinition processDefinition = null;
		try {
			processDefinition = jbpmTemplate
					.findLatestProcessDefinition(definitionName);
		} catch (Exception e) {
			throw new WorkflowException(e);
		}
		return processDefinition;
	}

	/**
	 * @see gleam.executive.workflow.manager.WorkflowManager#findProcessDefinitionByTaskInstanceId(long)
	 */
	public ProcessDefinition findProcessDefinitionByTaskInstanceId(
			long taskInstanceId) throws WorkflowException {
		ProcessDefinition processDefinition = null;
		try {
			processDefinition = jbpmTemplate
					.findProcessDefinitionByTaskInstanceId(taskInstanceId);
		} catch (Exception e) {
			throw new WorkflowException(e);
		}
		return processDefinition;
	}

	/**
	 * @see gleam.executive.workflow.manager.WorkflowManager#findStartableProcessDefinitions()
	 */
	public List findStartableProcessDefinitions() throws WorkflowException {
		List list = null;
		try {
			list = jbpmTemplate.findStartableProcessDefinitions();
		} catch (Exception e) {
			throw new WorkflowException(e);
		}
		return list;
	}
	
	

	/**
	 * @see gleam.executive.workflow.manager.WorkflowManager#findPendingTaskInstancesByActorId(String)
	 */
	public List findPendingTaskInstancesByActorId(String actorId)
			throws WorkflowException {
		List list = null;
		try {
			list = jbpmTemplate.findPendingTaskInstancesByActorId(actorId);
		} catch (Exception e) {
			throw new WorkflowException(e);
		}
		return list;
	}

	/**
	 * @see gleam.executive.workflow.manager.WorkflowManager#findTasksByProcessDefinitionId(long)
	 */
	public List findTasksByProcessDefinitionId(long definitionId)
			throws WorkflowException {
		List<TaskBean> taskInfos = new ArrayList<TaskBean>();
		try {
			Map tasksMap = jbpmTemplate
					.findTasksByProcessDefinitionId(definitionId);
			Collection tasks = tasksMap.values();
			Iterator it = tasks.iterator();

			while (it.hasNext()) {
				Task task = (Task) it.next();
				TaskBean taskInfo = new TaskBean(task);
				// TODO set users form appropriate group(value set in property
				// named -
				// roleName) as possible performers
				/*
				 * log.debug("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
				 * log.debug("@@@ PooledActorsExpression: "+
				 * task.getPooledActorsExpression()); log.debug("@@@
				 * ActorsExpression: " + task.getActorIdExpression());
				 * log.debug("@@@ Swimlane: " + task.getSwimlane().getName());
				 * log.debug("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
				 */
				taskInfo.setPooledActors(task.getPooledActorsExpression());
				taskInfo.setActor(task.getActorIdExpression());

				taskInfo.setPossiblePerformers(userManager
						.getUsersWithRole(task.getSwimlane().getName()));
				taskInfos.add(taskInfo);
			}
		} catch (Exception e) {
			throw new WorkflowException(e);
		}
		return taskInfos;
	}

	/**
	 * @see gleam.executive.workflow.manager.WorkflowManager#findVariable(Long,
	 *      String)
	 */
	public Object findVariable(Long taskId, String variableName)
			throws WorkflowException {
		Object variable = null;
		log.debug("Lookup for variable ': "+variableName + "' in taskInstance: "+taskId);
		try {
			variable = jbpmTemplate.findVariable(taskId, variableName);
		} catch (Exception e) {
			throw new WorkflowException(e);
		}
		return variable;
	}

	/**
	 * @see gleam.executive.workflow.manager.WorkflowManager#findVariableAccesses(Long)
	 */
	public List findVariableAccesses(Long taskId) throws WorkflowException {
		List list = null;
		try {
			list = jbpmTemplate.findVariableAccesses(taskId);
		} catch (Exception e) {
			throw new WorkflowException(e);
		}
		return list;
	}

	/**
	 * @see gleam.executive.workflow.manager.WorkflowManager#findVariableNames(Long)
	 */
	public String[] findVariableNames(Long taskId) throws WorkflowException {
		String[] array = null;
		try {
			array = jbpmTemplate.findVariableNames(taskId);
		} catch (Exception e) {
			throw new WorkflowException(e);
		}
		return array;
	}

	/**
	 * @see gleam.executive.workflow.manager.WorkflowManager#getAvailableTransitions(Long)
	 */
	public List getAvailableTransitions(Long taskInstanceId)
			throws WorkflowException {
		List list = null;
		try {
			list = jbpmTemplate.getAvailableTransitions(taskInstanceId);
		} catch (Exception e) {
			throw new WorkflowException(e);
		}
		return list;
	}

	public JbpmTemplate getJbpmTemplate() {
		return jbpmTemplate;
	}

	/**
	 * @see gleam.executive.workflow.manager.WorkflowManager#getProcessInstance(long)
	 */
	public ProcessInstance getProcessInstance(long processInstanceId)
			throws WorkflowException {
		ProcessInstance processInstance = null;
		try {
			processInstance = jbpmTemplate
					.getProcessInstance(processInstanceId);
		} catch (Exception e) {
			throw new WorkflowException(e);
		}
		return processInstance;
	}

	/**
	 * @see gleam.executive.workflow.manager.WorkflowManager#getProcessInstanceForUpdate(long)
	 */
	public ProcessInstance getProcessInstanceForUpdate(long processInstanceId)
			throws WorkflowException {
		ProcessInstance processInstance = null;
		try {
			processInstance = jbpmTemplate
					.getProcessInstanceForUpdate(processInstanceId);
		} catch (Exception e) {
			throw new WorkflowException(e);
		}
		return processInstance;
	}

	/**
	 * @see gleam.executive.workflow.manager.WorkflowManager#getTaskInstance(Long)
	 */
	public TaskInstance getTaskInstance(Long taskId) throws WorkflowException {
		TaskInstance taskInstance = null;
		try {
			taskInstance = jbpmTemplate.getTaskInstance(taskId);
		} catch (Exception e) {
			throw new WorkflowException(e);
		}
		return taskInstance;
	}

	public UserManager getUserManager() {
		return userManager;
	}

	/**
	 * 
	 * @see gleam.executive.workflow.manager.WorkflowManager#populateTaskFormParameters(java.lang.Long)
	 */
	public List populateTaskFormParameters(Long taskInstanceId)
			throws WorkflowException {
		log.debug("%%%%%%%% populateTaskFormParameters");
		List<TaskFormParameterBean> taskFormParameters = new ArrayList<TaskFormParameterBean>();
		try {
			List variableAccesses = findVariableAccesses(taskInstanceId);

			// Load task parameters
			Iterator iter = variableAccesses.iterator();
			while (iter.hasNext()) {
				VariableAccess variableAccess = (VariableAccess) iter.next();
				log.debug("%%% variable name "
						+ variableAccess.getVariableName());
				String mappedName = variableAccess.getMappedName();
				Object value = findVariable(taskInstanceId, mappedName);
				if (value != null) {
					log.debug("%%% variable value " + value.toString());
				} else {
					log.debug("%%% variable value NULL");
				}
				TaskFormParameterBean tfp = new TaskFormParameterBean(
						variableAccess, value);
				taskFormParameters.add(tfp);
			}
		} catch (Exception e) {
			throw new WorkflowException(e);
		}
		return taskFormParameters;
	}

	/**
	 * @see gleam.executive.workflow.manager.WorkflowManager#resumeProcessInstance(long)
	 */
	public void resumeProcessInstance(long processInstanceId)
			throws WorkflowException {
		try {
			jbpmTemplate.resumeProcessInstance(processInstanceId);
		} catch (Exception e) {
			throw new WorkflowException(e);
		}
	}

	/**
	 * @see gleam.executive.workflow.manager.WorkflowManager#resumeTaskInstance(long)
	 */
	public void resumeTaskInstance(long taskInstanceId)
			throws WorkflowException {
		try {
			jbpmTemplate.resumeTaskInstance(taskInstanceId);
		} catch (Exception e) {
			throw new WorkflowException(e);
		}
	}

	public void setJbpmTemplate(JbpmTemplate jbpmTemplate) {
		this.jbpmTemplate = jbpmTemplate;
	}

	public void setUserManager(UserManager userManager) {
		this.userManager = userManager;
	}

	/**
	 * @see gleam.executive.workflow.manager.WorkflowManager#signalProcessInstance(String)
	 */
	public void signalProcessInstance(String pId) throws WorkflowException {
		try {
			Long processInstanceId = Long.valueOf(pId);

			ProcessInstance processInstance = jbpmTemplate
					.findProcessInstance(processInstanceId);
			if (processInstance != null) {
				log.debug("FOUND PROCESS INSTANCE WITH ID "
						+ processInstance.getId());
			} else {
				log.error("NOT FOUND PROCESS INSTANCE WITH ID "
						+ processInstanceId);
			}
			jbpmTemplate.signal(processInstance);
		} catch (Exception e) {
			throw new WorkflowException(e);
		}
	}

	/**
	 * @see gleam.executive.workflow.manager.WorkflowManager#suspendProcessInstance(long)
	 */
	public void suspendProcessInstance(long processInstanceId)
			throws WorkflowException {
		try {
			jbpmTemplate.suspendProcessInstance(processInstanceId);
		} catch (Exception e) {
			throw new WorkflowException(e);
		}
	}

	/**
	 * @see gleam.executive.workflow.manager.WorkflowManager#suspendTaskInstance(long)
	 */
	public void suspendTaskInstance(long taskInstanceId)
			throws WorkflowException {
		try {
			jbpmTemplate.suspendTaskInstance(taskInstanceId);
		} catch (Exception e) {
			throw new WorkflowException(e);
		}
	}

	/**
	 * @see gleam.executive.workflow.manager.WorkflowManager#suspendTaskInstance(long)
	 */
	public void startTaskInstance(long taskInstanceId) throws WorkflowException {
		try {
			jbpmTemplate.startTaskInstance(taskInstanceId);
		} catch (Exception e) {
			throw new WorkflowException(e);
		}
	}

	/**
	 * @see gleam.executive.workflow.manager.WorkflowManager#undeployProcessDefinition(long)
	 */
	public void undeployProcessDefinition(final long definitionId)
			throws WorkflowException {
		try {
			jbpmTemplate.undeployProcessDefinition(definitionId);
		} catch (Exception e) {
			throw new WorkflowException(e);
		}
	}

	public void undeployProcessDefinition(final ProcessDefinition definition)
			throws WorkflowException {
		try {
			jbpmTemplate.undeployProcessDefinition(definition);
		} catch (Exception e) {
			throw new WorkflowException(e);
		}
	}

	/**
	 * @see gleam.executive.workflow.manager.WorkflowManager#updateProcessDefinition(long,
	 *      java.util.List)
	 */
	public void updateProcessDefinition(long definitionId,
			List<SwimlaneBean> swimlanes) throws WorkflowException {
		// Fetch all tasks for specified definition
		try {
			Map swimlaneMap = jbpmTemplate
					.findSwimlanesByProcessDefinitionId(definitionId);
			// Iterate through user managed tasks
			Iterator<SwimlaneBean> it = swimlanes.iterator();
			while (it.hasNext()) {
				SwimlaneBean swimlaneInfo = it.next();
				Swimlane swimlane = (Swimlane) swimlaneMap.get(swimlaneInfo
						.getName());
				swimlane.setPooledActorsExpression(swimlaneInfo
						.getPooledActors());
				swimlane.setActorIdExpression(swimlaneInfo.getActor());

				jbpmTemplate.saveOrUpdateSwimlane(swimlane);
			}
		} catch (Exception e) {
			throw new WorkflowException(e);
		}
	}

	/**
	 * @see gleam.executive.workflow.manager.WorkflowManager#findTaskInstancesForProcessInstance(long)
	 */
	public List findTaskInstancesForProcessInstance(long processInstanceId)
			throws WorkflowException {
		try {
			return jbpmTemplate
					.findTaskInstancesForProcessInstance(processInstanceId);
		} catch (Exception e) {
			throw new WorkflowException(e);
		}

	}

	/*
	 * Methods called from ExecutiveCallbackService
	 */

	/**
	 * @see gleam.executive.workflow.manager.WorkflowManager#executeCallback(String,
	 *      String, String)
	 */

	public void executeCallback(String callbackTaskID, String error)
			throws WorkflowException {

		log.debug("@@@@@@@@@@@@@ executeCallback START @@@@@@@@@@@@@");

		try {
			log.debug(callbackTaskID);
			Matcher m = JPDLConstants.TASK_PATTERN.matcher(callbackTaskID);
			long id = -1;
			if (m.find()) {
				// isToken = !m.group(1).equals("task");
				id = Long.parseLong(m.group(2));

				String taskTypeSuffix = callbackTaskID.substring(callbackTaskID
						.lastIndexOf(".") + 1);
				log.debug("detected taskTypeSuffix: " + taskTypeSuffix);
				if (taskTypeSuffix.startsWith(JPDLConstants.GAS_SUFFIX)) {
					log.debug("Try to end task " + id);
					jbpmTemplate.executeCallbackAndEndTask(id, error);
				/*	
				} else if(taskTypeSuffix.startsWith(JPDLConstants.REVIEW_SUFFIX)) {
					log.debug("Try to signal token: " + id);
					jbpmTemplate.executeCallbackAndSignalToken(id, error);
				*/
				}
				
				else {
					log.debug("Try to start task " + id);
					jbpmTemplate.executeCallbackAndStartTask(id, error);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new WorkflowException(e);
		}

		log.debug("@@@@@@@@@@@@@ executeCallback END @@@@@@@@@@@@@");
	}
	

	/**
	 * @see gleam.executive.workflow.manager.WorkflowManager#cancelTaskInstance(long)
	 */
	public void cancelTaskInstance(long taskInstanceId, String transitionName)
			throws WorkflowException {
		/*
		 * log.debug("--------------------------------------------------");
		 * log.debug("--------------------------------------------------");
		 * log.debug("--------------------------------------------------");
		 * log.debug("@@@@@@@@@@@@@ cancelTaskInstance START @@@@@@@@@@@@@");
		 */
		try {
			jbpmTemplate.cancelTaskInstance(taskInstanceId, transitionName);
		} catch (Exception e) {
			throw new WorkflowException(e);
		}
		/*
		 * log.debug("@@@@@@@@@@@@@ cancelTaskInstance END @@@@@@@@@@@@@");
		 * log.debug("--------------------------------------------------");
		 * log.debug("--------------------------------------------------");
		 * log.debug("--------------------------------------------------");
		 */
	}

	/**
	 * @see gleam.executive.workflow.manager.WorkflowManager#completeTask(long,
	 *      java.util.Map, String)
	 */
	public TaskInstance completeTask(long taskInstanceId, Map variables,
			String transition) throws WorkflowException {
		/*
		 * log.debug("--------------------------------------------------");
		 * log.debug("--------------------------------------------------");
		 * log.debug("--------------------------------------------------");
		 * log.debug("@@@@@@@@@@@@@ completeTask START @@@@@@@@@@@@@");
		 */
		JbpmTaskInstance taskInstance = null;
		try {
			taskInstance = (JbpmTaskInstance) getTaskInstance(taskInstanceId);
			taskInstance = (JbpmTaskInstance) jbpmTemplate.completeTask(taskInstanceId, variables, transition);
		} catch (Exception e) {
			throw new WorkflowException(e);
		}
		/*
		 * log.debug("@@@@@@@@@@@@@ completeTask END @@@@@@@@@@@@@");
		 * log.debug("--------------------------------------------------");
		 * log.debug("--------------------------------------------------");
		 * log.debug("--------------------------------------------------");
		 */
		return taskInstance;
	}
	
	/**
	 * @see gleam.executive.workflow.manager.WorkflowManager#completeTask(long,
	 *      java.util.Map, String)
	 */
	public TaskInstance saveTask(long taskInstanceId, Map variables,
			String transition) throws WorkflowException {
		/*
		 * log.debug("--------------------------------------------------");
		 * log.debug("--------------------------------------------------");
		 * log.debug("--------------------------------------------------");
		 * log.debug("@@@@@@@@@@@@@ saveTask START @@@@@@@@@@@@@");
		 */
		JbpmTaskInstance taskInstance = null;
		try {
			taskInstance = (JbpmTaskInstance) getTaskInstance(taskInstanceId);
			setWorktime(taskInstance);
			jbpmTemplate.saveOrUpdateTask(taskInstance.getTask());
			jbpmTemplate.updateStatus(taskInstanceId);
		} catch (Exception e) {
			throw new WorkflowException(e);
		}
		/*
		 * log.debug("@@@@@@@@@@@@@ completeTask END @@@@@@@@@@@@@");
		 * log.debug("--------------------------------------------------");
		 * log.debug("--------------------------------------------------");
		 * log.debug("--------------------------------------------------");
		 */
		return taskInstance;
	}

	private void setWorktime(JbpmTaskInstance taskInstance) {
		
		Date lastOpened = taskInstance.getLastOpened();
		Date lastSaved = taskInstance.getLastSaved();
		long now = Calendar.getInstance().getTimeInMillis();
		long timeToAdd = 0;
		
		if(lastOpened != null) {
			if(lastSaved == null || lastSaved.compareTo(lastOpened) < 0) {
				timeToAdd = now - lastOpened.getTime();
			} else {
				timeToAdd = now - lastSaved.getTime();
			}
			taskInstance.addTimeWorkedOn(timeToAdd);
			taskInstance.setLastSaved(new Date(now));
		}
	}

	/**
	 * @see gleam.executive.workflow.manager.WorkflowManager#findNextTaskInstance(String)
	 */
	public Map<String, Object> getNextTaskAsMap(final String actorId)
			throws WorkflowException {

		Map<String, Object> result = null;
		try {
			JbpmTaskInstance taskInstance = (JbpmTaskInstance) jbpmTemplate.getNextTask(actorId);
			if (taskInstance != null) {

				result = new HashMap<String, Object>();
				log.debug("TOKEN_ID " + taskInstance.getToken().getId());
				result.put(JPDLConstants.TASK_INSTANCE_ID, taskInstance.getId());
				log.debug("TASKINSTANCE_ID " + taskInstance.getId());
				result.put(JPDLConstants.TOKEN_ID, taskInstance.getToken().getId());
				log.debug("TASK_NAME " + taskInstance.getName());
				result.put(JPDLConstants.TASK_NAME, taskInstance.getName());
				log.debug("DUE_DATE " + taskInstance.getDueDate());
				result.put(JPDLConstants.DUE_DATE, taskInstance.getDueDate());
				log.debug("START_DATE " + taskInstance.getStart());
				result.put(JPDLConstants.START_DATE, taskInstance.getStart());

				String documentId = taskInstance.getDocumentId();
				log.debug("DOCUMENT_ID " + documentId);
				result.put(JPDLConstants.DOCUMENT_ID, documentId);

				String annotationSetName = taskInstance.getAnnotationSetName();
				log.debug("ANNOTATION_SET_NAME " + annotationSetName);
				result.put(JPDLConstants.ANNOTATION_SET_NAME, annotationSetName);

				/* get canCancel from global context */
				String canCancel = (String) taskInstance.getProcessInstance().getContextInstance()
						.getVariable(JPDLConstants.CAN_CANCEL);
				log.debug("CAN_CANCEL " + canCancel);
				result.put(JPDLConstants.CAN_CANCEL, canCancel);

				String annotationSchemaCSVURLs = (String) taskInstance
						.getVariable(JPDLConstants.ANNOTATION_SCHEMA_CSV_URLS);
				log.debug("ANNOTATION_SCHEMA_CSV_URLS " + annotationSchemaCSVURLs);
				result.put(JPDLConstants.ANNOTATION_SCHEMA_CSV_URLS, annotationSchemaCSVURLs);

				String ontologyRepositoryName = (String) taskInstance
						.getVariable(JPDLConstants.ONTOLOGY_REPOSITORY_NAME);
				log.debug("ONTOLOGY_REPOSITORY_NAME " + ontologyRepositoryName);
				result.put(JPDLConstants.ONTOLOGY_REPOSITORY_NAME, ontologyRepositoryName);

				String baseAnnotationSchemaURL = webAppBean.getBaseSchemaURL();
				log.debug("BASE_ANNOTATION_SCHEMA_URL " + baseAnnotationSchemaURL);
				result.put(JPDLConstants.BASE_ANNOTATION_SCHEMA_URL, baseAnnotationSchemaURL);

				String pluginCSVList = webAppBean.getPluginCSVList();
				log.debug("PLUGIN_CSV_LIST " + pluginCSVList);
				result.put(JPDLConstants.PLUGIN_CSV_LIST, pluginCSVList);
				
				// marking task as opened
				taskInstance.setLastOpened(Calendar.getInstance().getTime());
				log.debug("Marking task as opened at: " + taskInstance.getLastOpened().toString());
				jbpmTemplate.saveOrUpdateTask(taskInstance.getTask());

				//String owlimServiceURL = gosManager.getGosURL();
				
				//log.debug("@@@  owlimServiceURL " + owlimServiceURL);

				String docserviceURL = docServiceManager.getDocServiceURL();
				log.debug("@@@  docserviceURL " + docserviceURL);

				URI docServiceURI = null;
				URI owlimServiceURI = null;
				try {
					docServiceURI = new URI(docserviceURL);
					result.put(JPDLConstants.DOCSERVICE_URL, docServiceURI);
				} catch (URISyntaxException mue) {
					log.error("Docservice URL " + docserviceURL
							+ " is not a well formed URI", mue);
					throw new IllegalStateException("Illegal docservice URL");
				}
				
				/*
				try {
					owlimServiceURI = new URI(owlimServiceURL);
					result
							.put(JPDLConstants.OWLIM_SERVICE_URL,
									owlimServiceURI);
				} catch (URISyntaxException mue) {
					log.error("OWLIM Service URL " + owlimServiceURI
							+ " is not a well formed URI", mue);
					throw new IllegalStateException("Illegal OWLIM Service URL");
				}
				*/
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new WorkflowException(e);
		}

		return result;
	}

	/**
	 * @see gleam.executive.workflow.manager.WorkflowManager#checkForNextTask(String)
	 */
	public boolean checkForNextTask(final String actorId)
			throws WorkflowException {
		/*
		 * log.debug("--------------------------------------------------");
		 * log.debug("--------------------------------------------------");
		 * log.debug("--------------------------------------------------");
		 * log.debug("@@@@@@@@@@@@@ checkForNextTask START @@@@@@@@@@@@@");
		 */
		boolean flag = false;
		try {
			flag = jbpmTemplate.checkIfThereIsNextTaskInstanceForActor(actorId);
		} catch (Exception e) {
			throw new WorkflowException(e);
		}
		/*
		 * log.debug("@@@@@@@@@@@@@ checkForNextTask END @@@@@@@@@@@@@");
		 * log.debug("--------------------------------------------------");
		 * log.debug("--------------------------------------------------");
		 * log.debug("--------------------------------------------------");
		 */
		return flag;
	}

	/**
	 * @see gleam.executive.workflow.manager.WorkflowManager#findUncompletedProcessInstancesForProcessDefinition(long)
	 */
	public int findUncompletedProcessInstancesForProcessDefinition(
			long definitionId) throws WorkflowException {
		int num = 0;
		try {
			num = jbpmTemplate
					.findUncompletedProcessInstancesForProcessDefinition(definitionId);
		} catch (Exception e) {
			throw new WorkflowException(e);
		}
		return num;
	}

	/**
	 * @see gleam.executive.workflow.manager.WorkflowManager#findAllProcessDefinitions()
	 */
	public List<ProcessDefinition> findAllProcessDefinitions()
			throws WorkflowException {
		List list = null;
		try {
			list = jbpmTemplate.findAllProcessDefinitions();
		} catch (Exception e) {
			throw new WorkflowException(e);
		}
		return list;
	}

	/**
	 * @see gleam.executive.workflow.manager.WorkflowManager#rejectTaskInstance(long)
	 */
	public void rejectTaskInstance(long taskInstanceId)
			throws WorkflowException {
		try {
			jbpmTemplate.rejectTaskInstance(taskInstanceId);
		} catch (Exception e) {
			throw new WorkflowException(e);
		}
	}

	/**
	 * @see gleam.executive.workflow.manager.WorkflowManager#rejectTaskInstance(long)
	 */
	public void acceptTaskInstance(long taskInstanceId, String actorId)
			throws WorkflowException {
		try {
			jbpmTemplate.acceptTaskInstance(taskInstanceId, actorId);
		} catch (Exception e) {
			throw new WorkflowException(e);
		}
	}

	/**
	 * @see gleam.executive.workflow.manager.WorkflowManager#findPendingTaskInstancesByProcessInstanceAndName(long,
	 *      String)
	 */
	public List<TaskInstance> findPendingTaskInstancesByProcessInstanceAndName(
			final long processInstanceId, final String taskName)
			throws WorkflowException {
		List list = null;
		try {
			list = jbpmTemplate
					.findPendingTaskInstancesByProcessInstanceAndName(
							processInstanceId, taskName);
		} catch (Exception e) {
			throw new WorkflowException(e);
		}
		return list;
	}

	public List<ProcessInstance> findAllSubProcessInstances(
			ProcessInstance processInstance) throws WorkflowException {
		List list = null;
		try {
			list = jbpmTemplate.findAllSubProcessInstances(processInstance);
		} catch (Exception e) {
			throw new WorkflowException(e);
		}
		return list;

	}

	/**
	 * @see gleam.executive.workflow.manager.WorkflowManager#findSwimlanesByProcessDefinitionId(long)
	 */
	public List<SwimlaneBean> findSwimlanesByProcessDefinitionId(
			long processDefinitionId) throws WorkflowException {

		List<SwimlaneBean> swimlaneInfos = new ArrayList<SwimlaneBean>();
		try {
			Map swimlaneMap = jbpmTemplate
					.findSwimlanesByProcessDefinitionId(processDefinitionId);
			Collection swimlanes = swimlaneMap.values();
			Iterator it = swimlanes.iterator();

			while (it.hasNext()) {
				Swimlane swimlane = (Swimlane) it.next();
				SwimlaneBean swimlaneInfo = new SwimlaneBean(swimlane);
				swimlaneInfo.setPooledActors(swimlane
						.getPooledActorsExpression());
				swimlaneInfo.setActor(swimlane.getActorIdExpression());
				swimlaneInfo.setPossiblePerformers(userManager
						.getUsersWithRole(swimlane.getName()));
				swimlaneInfos.add(swimlaneInfo);
			}
		} catch (Exception e) {
			throw new WorkflowException(e);
		}
		return swimlaneInfos;
	}

	// CONFIGURATION OPTIONS - MANAGER INTERFACE

	public List<TaskInstance> findPendingConfigurationOptionsByProcessInstancesAndActorId(
			ProcessInstance[] processInstances, String actorId)
			throws WorkflowException {
		List list = null;
		try {
			list = jbpmTemplate
					.findPendingConfigurationOptionsByProcessInstancesAndActorId(
							processInstances, actorId);
		} catch (Exception e) {
			throw new WorkflowException(e);
		}
		return list;
	}

	public List<TaskInstance> findPendingConfigurationOptionsByProcessInstanceKeyAndActorId(
			String key, String actorId) throws WorkflowException {
		List list = null;
		try {
			list = jbpmTemplate
					.findPendingConfigurationOptionsByProcessInstanceKeyAndActorId(
							key, actorId);
		} catch (Exception e) {
			throw new WorkflowException(e);
		}
		return list;
	}

	public List<TaskInstance> findPendingConfigurationOptionsByActorId(
			String actorId) throws WorkflowException {
		List list = null;
		try {
			list = jbpmTemplate
					.findPendingConfigurationOptionsByActorId(actorId);
		} catch (Exception e) {
			throw new WorkflowException(e);
		}
		return list;
	}

	public List<AnnotationStatusInfo> getDocumentStatusList(
			long processInstanceId) throws WorkflowException {
		List list = null;
		try {
			list = jbpmTemplate.getDocumentStatusList(processInstanceId);
		} catch (Exception e) {
			throw new WorkflowException(e);
		}
		return list;
	}

	public AnnotationMetricMatrix getDocumentMatrix(long processInstanceId)
			throws WorkflowException {
		AnnotationMetricMatrix annotationMetricMatrix = null;
		try {
			annotationMetricMatrix = jbpmTemplate
					.getDocumentMatrix(processInstanceId);
		} catch (Exception e) {
			throw new WorkflowException(e);
		}
		return annotationMetricMatrix;
	}

	public AnnotationMetricMatrix getAnnotatorMatrix(long processInstanceId,
			String username) throws WorkflowException {
		AnnotationMetricMatrix annotationMetricMatrix = null;
		try {
			annotationMetricMatrix = jbpmTemplate.getAnnotatorMatrix(
					processInstanceId, username);
		} catch (Exception e) {
			throw new WorkflowException(e);
		}
		return annotationMetricMatrix;
	}
	
	public AnnotationMetricMatrix getAnnotatorMatrixForAllProcesses(String username) throws WorkflowException {
		AnnotationMetricMatrix annotationMetricMatrix = null;
		try {
			annotationMetricMatrix = jbpmTemplate.getAnnotatorMatrixForAllProcesses(username);
		} catch (Exception e) {
			throw new WorkflowException(e);
		}
		return annotationMetricMatrix;
	}
	

	public AnnotationMetricMatrix getGlobalAnnotatorMatrix(
			long processInstanceId, String roleName) throws WorkflowException {
		AnnotationMetricMatrix annotationMetricMatrix = null;
		try {
			annotationMetricMatrix = jbpmTemplate.getGlobalAnnotatorMatrix(
					processInstanceId, roleName);
		} catch (Exception e) {
			throw new WorkflowException(e);
		}
		return annotationMetricMatrix;
	}

	public void saveProcessInstance(ProcessInstance processInstance)
			throws WorkflowException {
		try {
			jbpmTemplate.saveProcessInstance(processInstance);
		} catch (Exception e) {
			throw new WorkflowException(e);
		}

	}

	public void updateVariables(final ProcessInstance processInstance,
			final Map variableMap) throws WorkflowException {
		try {
			jbpmTemplate.updateVariables(processInstance, variableMap);
		} catch (Exception e) {
			throw new WorkflowException(e);
		}

	}

	public void deleteTaskInstances(final Collection<Long> taskInstanceIds)
			throws WorkflowException {
		try {
			jbpmTemplate.deleteTaskInstances(taskInstanceIds);
		} catch (Exception e) {
			throw new WorkflowException(e);
		}

	}

	public void updatePooledActors(String variableName, String[] newActorIds,
			ProcessInstance processInstance) throws WorkflowException {
		try {
			jbpmTemplate.updatePooledActors(variableName, newActorIds,
					processInstance);
		} catch (Exception e) {
			throw new WorkflowException(e);
		}
	}

	public boolean isAnyProcessInstanceRunningAgainstTheCorpus(String processDefinitionName, String corpusId)
			throws WorkflowException {
		boolean flag = false;
		try {

			flag = jbpmTemplate.isAnyProcessInstanceRunningAgainstTheCorpus(processDefinitionName, corpusId);
			
		} catch (Exception e) {
			throw new WorkflowException(e);
		}
		return flag;
	}

	public WebAppBean getWebAppBean() {
		return webAppBean;
	}

	public void setWebAppBean(WebAppBean webAppBean) {
		this.webAppBean = webAppBean;
	}

	public DocServiceManager getDocServiceManager() {
		return docServiceManager;
	}

	public void setDocServiceManager(DocServiceManager docServiceManager) {
		this.docServiceManager = docServiceManager;
	}

	public GosManager getGosManager() {
		return gosManager;
	}

	public void setGosManager(GosManager gosManager) {
		this.gosManager = gosManager;
	}


}
