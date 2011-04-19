/*
 *  WorkflowManager.java
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
 * Milan Agatonovic and Haotian Sun
 *
 *  $Id$
 */
package gleam.executive.workflow.manager;

import gleam.executive.workflow.model.AnnotationMetricMatrix;
import gleam.executive.workflow.model.AnnotationStatusInfo;
import gleam.executive.workflow.model.SwimlaneBean;
import gleam.executive.workflow.util.WorkflowException;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.jbpm.file.def.FileDefinition;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.taskmgmt.exe.TaskInstance;

public interface WorkflowManager {

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#cancelProcessInstance(long),
	 */
	public void cancelProcessInstance(long processInstanceId)
			throws WorkflowException;
	
	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#endProcessInstance(long),
	 */
	public void endProcessInstance(long processInstanceId)
			throws WorkflowException;

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#cancelTaskInstance(long),
	 */
	public void cancelTaskInstance(long taskInstanceId)
			throws WorkflowException;


	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#createProcessInstance(long, java.util.Map, String),
	 */
	public ProcessInstance createProcessInstance(long definitionId, Map variableMap, String key)
	throws WorkflowException;
	
	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#addCommentToRootToken(org.jbpm.graph.exe.ProcessInstance, String)
	 */
	public void addCommentToRootToken(ProcessInstance processInstance, final String comment)throws WorkflowException;
	
	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#createStartProcessInstance(long, java.util.Map, String),
	 */
	public ProcessInstance createStartProcessInstance(long definitionId, Map variableMap, String key)
	throws WorkflowException;



	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#deployProcessFromArchive(java.io.InputStream),
	 */
	public void deployProcessFromArchive(InputStream inputStream)
			throws WorkflowException;


	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#deployProcessDefinition(org.jbpm.graph.def.ProcessDefinition),
	 */
	public void deployProcessDefinition(ProcessDefinition processDefinition)
			throws WorkflowException;

  /*
  public void redeployProcessDefinition(final ProcessDefinition oldProcessDefinition,List tasks)
  throws WorkflowException;*/



	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#findAllPooledTaskInstances(String),
	 */
	public List findAllPooledTaskInstances(String actorId)
			throws WorkflowException;

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#findAllProcessInstances(),
	 */
	public List findAllProcessInstances() throws WorkflowException;

	/*
	 * public void createProcessInstance(long definitionId) throws
	 * WorkflowException;
	 *
	 *
	 * public void startProcessInstance(long processInstanceId) throws
	 * WorkflowException;
	 */
	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#findAllProcessInstances(long),
	 */
	public List findAllProcessInstances(long definitionId)
			throws WorkflowException;

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#findAllProcessInstancesByProcessDefinitionIdExcludingSubProcessInstances(),
	 */
	public List<ProcessInstance> findAllProcessInstancesExcludingSubProcessInstances()
			throws WorkflowException;

	/**
    *
	 * fetches all available processInstances excluding subprocesses with the same key and name
	 * @return list of process instances is sorted start date, latest 1st
	 */

    public List<ProcessInstance> findProcessInstancesExcludingSubProcessInstancesByKeyAndName(String key, String name)
	throws WorkflowException;
	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#findAllSubProcessInstances(org.jbpm.graph.exe.ProcessInstance),
	 */
	public List<ProcessInstance> findAllSubProcessInstances(final ProcessInstance processInstance) throws WorkflowException;
		
	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#findAllProcessInstancesByProcessDefinitionIdExcludingSubProcessInstances(long),
	 */
	public List<ProcessInstance> findAllProcessInstancesByProcessDefinitionIdExcludingSubProcessInstances(long processDefinitionId) throws WorkflowException;
	
	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#findAllProcessInstancesByProcessDefinitionIdExcludingSubProcessInstances(long),
	 */
	public List<ProcessInstance> findAllProcessInstancesByProcessDefinitionNameExcludingSubProcessInstances(String processDefinitionName) throws WorkflowException;
	
	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#findAllTaskInstances(String),
	 */
	public List findAllTaskInstances(String actorId) throws WorkflowException;

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#findPooledTaskInstances(String),
	 */
	public List findPooledTaskInstances(String actorId)
			throws WorkflowException;
	
	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#findPendingPooledAnnotationTaskInstances(String),
	 */
	public List findPendingPooledAnnotationTaskInstances(String actorId)
			throws WorkflowException;

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#findProcessDefinition(long),
	 */
	public ProcessDefinition findProcessDefinition(long definitionId)
			throws WorkflowException;
	

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#loadProcessDefinition(long),
	 */
	public ProcessDefinition loadProcessDefinition(long definitionId)
			throws WorkflowException;

  public ProcessDefinition findLatestProcessDefinition (String definitionName)
      throws WorkflowException;

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#findFileDefinition(long),
	 */
	public FileDefinition findFileDefinition(final long processDefinitionId) throws WorkflowException;

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#findProcessDefinitionByTaskInstanceId(long),
	 */
	public ProcessDefinition findProcessDefinitionByTaskInstanceId(long taskInstanceId)
			throws WorkflowException;
	
	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#findProcessDefinitionByNameAndVersion(String, Integer)
	 */
	public ProcessDefinition findProcessDefinitionByNameAndVersion(final String processDefinitionName, final Integer version) throws WorkflowException; 
	
	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#findProcessDefinitionByName(String)
	 */
	public List<ProcessDefinition> findProcessDefinitionsByName(final String processDefinitionName) throws WorkflowException; 
	
	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#findTaskInstancesForProcessInstance(long),
	 */
  public List findTaskInstancesForProcessInstance(long processInstanceId) throws WorkflowException;

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#findStartableProcessDefinitions(),
	 */
	public List findStartableProcessDefinitions() throws WorkflowException;

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#findPendingTaskInstancesByActorId(String),
	 */
	public List findPendingTaskInstancesByActorId(String actorId) throws WorkflowException;

	/**
	 * Finds tasks in process definition
	 *
	 * @param definitionId
	 * @param List
	 *            of Task objects
	 */
	public List findTasksByProcessDefinitionId(long definitionId)
			throws WorkflowException;

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#findVariable(Long,
	 *      String),
	 */
	public Object findVariable(Long taskId, String variableName)
			throws WorkflowException;

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#findVariableAccesses(Long),
	 */
	public List findVariableAccesses(Long taskId) throws WorkflowException;

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#findVariableNames(Long),
	 */
	public String[] findVariableNames(Long taskId) throws WorkflowException;


	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#getAvailableTransitions(Long),
	 */
	public List getAvailableTransitions(Long taskInstanceId)
			throws WorkflowException;

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#getProcessInstance(long),
	 */
	public ProcessInstance getProcessInstance(long processInstanceId)
			throws WorkflowException;
	
	
	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#getProcessInstanceForUpdate(long),
	 */
	public ProcessInstance getProcessInstanceForUpdate(long processInstanceId)
			throws WorkflowException;

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#getTaskInstance(Long),
	 */
	public TaskInstance getTaskInstance(Long taskId) throws WorkflowException;

	/**
	 * Creates List of TaskFormParammeterBeans for displaying dynamic form
	 *
	 * @param taskInstanceId
	 * @return List of TaskFormParammeterBeans
	 */
	public List populateTaskFormParameters(Long taskInstanceId)
			throws WorkflowException;

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#resumeProcessInstance(long),
	 */
	public void resumeProcessInstance(long processInstanceId)
			throws WorkflowException;

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#cancelTaskInstance(long),
	 */
	public void resumeTaskInstance(long taskInstanceId)
			throws WorkflowException;

	/**
	 * Signal process instance for default transition and root token
	 *
	 * @param processInstanceId
	 */
	public void signalProcessInstance(String processInstanceId)
			throws WorkflowException;

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#suspendProcessInstance(long),
	 */
	public void suspendProcessInstance(long processInstanceId)
			throws WorkflowException;


	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#startTaskInstance(long),
	 */
	public void startTaskInstance(long taskInstanceId)
			throws WorkflowException;

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#suspendTaskInstance(long),
	 */
	public void suspendTaskInstance(long taskInstanceId)
			throws WorkflowException;

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#undeployProcessDefinition(long),
	 */
	public void undeployProcessDefinition(long definitionId)
			throws WorkflowException;

  public void undeployProcessDefinition(ProcessDefinition definition)
  throws WorkflowException;

	/**
	 * Updates process definition with modified Tasks
	 *
	 * @param definitionId
	 * @param List
	 *            of SwimlaneBean objects
	 */
	public void updateProcessDefinition(long definitionId, List<SwimlaneBean> swimlanes)
			throws WorkflowException;




	/*
	 * Methods called from ExecutiveCallbackService
	 */


	/**
	 * Executes callback
	 *
	 * @param callbackTaskID
	 *            ID passed into the callback method. This may contain
	 *            several parts separated by dots, in which case the part
	 *            after the last dot is the ID of the token to be signalled.
	 * @param error error message reported by GAS
	 */
	public void executeCallback(String callbackTaskID, String error) throws WorkflowException;


	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#completeTask(Long,
	 *      java.util.Map, String),
	 */
	public TaskInstance completeTask(long taskInstanceId, Map variables,
			String transition) throws WorkflowException;

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#saveTask(Long, java.util.Map, String),
	 */
	public TaskInstance saveTask(long taskInstanceId, Map variables, String transition) throws WorkflowException;


	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#cancelTaskInstance(long, java.lang.String),
	 */
	public void cancelTaskInstance(long taskInstanceId, String transitionName)
			throws WorkflowException;
	
	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#cancelTaskInstance(long)	 */
	public void endTaskInstance(long taskInstanceId)
			throws WorkflowException;

	/**
	 * Checks for new task

	 * @param actorId
	 *            the user name of performer
	 * @return true if there is a new task, otherwise false
	 */
	public boolean checkForNextTask (final String actorId) throws WorkflowException;


	/**
	 * Fetch Map of the following:
	 * - token Id, task Instance Name, actor username, start and due date

	 * @param actorId
	 *            the user name of performer
	 * @return Map
	 */
	public Map<String, Object> getNextTaskAsMap(final String actorId) throws WorkflowException;

	/**
	 * Finds number of uncompleted process instances.

	 * @param processDefinitionId
	 *            the process Definition Id
	 * @return number of uncompleted process instances
	 */
	public int findUncompletedProcessInstancesForProcessDefinition(long processDefinitionId) throws WorkflowException;

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#findAllProcessDefinitions()
	 */
	public List<ProcessDefinition> findAllProcessDefinitions() throws WorkflowException;

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#rejectTaskInstance(long)
	 */
	public void rejectTaskInstance(long taskInstanceId) throws WorkflowException;

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#acceptTaskInstance(long, String)
	 */
	public void acceptTaskInstance(final long taskInstanceId, String actorId) throws WorkflowException;

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#findPendingTaskInstancesByProcessInstanceAndName(long, String)
	 */
	public List<TaskInstance> findPendingTaskInstancesByProcessInstanceAndName(long processInstanceId, String taskName)throws WorkflowException;

	
	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#findSwimlanesByProcessDefinitionId(long)
	 */
	public List<SwimlaneBean> findSwimlanesByProcessDefinitionId(long processDefinitionId) throws WorkflowException;


	// CONFIGURATION OPTIONS - MANAGER INTERFACE
	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#findPendingConfigurationOptionsByProcessInstancesAndActorId(long, String)
	 */
	public List<TaskInstance> findPendingConfigurationOptionsByProcessInstancesAndActorId(final ProcessInstance[] processInstances, final String actorId) throws WorkflowException;

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#findPendingConfigurationOptionsByProcessInstanceKeyAndActorId(String, String)
	 */
	public List findPendingConfigurationOptionsByProcessInstanceKeyAndActorId(final String key, final String actorId) throws WorkflowException;
		
	
	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#findPendingConfigurationOptionsByActorId(String)
	 */	
	public List<TaskInstance> findPendingConfigurationOptionsByActorId (String actorId) throws WorkflowException;
	

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#getDocumentStatusMap(long)
	 */
	public List<AnnotationStatusInfo> getDocumentStatusList(long processInstanceId) throws WorkflowException;
		
	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#getDocumentMatrix(long)
	 */
	public AnnotationMetricMatrix getDocumentMatrix(long processInstanceId) throws WorkflowException;
		
	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#getAnnotatorMatrix(long, String)
	 */
	public AnnotationMetricMatrix getAnnotatorMatrix(long processInstanceId, String username) throws WorkflowException;
	
	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#getGlobalAnnotatorMatrix(long, String)
	 */
	public AnnotationMetricMatrix getGlobalAnnotatorMatrix(long processInstanceId, String roleName)  throws WorkflowException;
	
	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#getAnnotatorMatrixForAllProcesses(String)
	 */
	public AnnotationMetricMatrix getAnnotatorMatrixForAllProcesses(String username) throws WorkflowException;
	
	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#saveProcessInstance(org.jbpm.graph.exe.ProcessInstance)
	 */		                                                             
	public void saveProcessInstance(ProcessInstance processInstance) throws WorkflowException;

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#startProcessInstance(org.jbpm.graph.exe.ProcessInstance)
	 */	
	public ProcessInstance startProcessInstance(ProcessInstance processInstance) throws WorkflowException;
		
	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#updateVariables(org.jbpm.graph.exe.ProcessInstance, java.util.Map)
	 */	
	public void updateVariables(final ProcessInstance processInstance,
			final Map variableMap) throws WorkflowException;
	
	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#updatePooledActors(String, String[], org.jbpm.graph.exe.ProcessInstance, java.util.Map)
	 */	
	public void updatePooledActors (String variableName, String[] newActorIds, ProcessInstance processInstance) throws WorkflowException;

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#deleteTaskInstances(java.util.Collection)
	 */	
	public void deleteTaskInstances(final Collection<Long> taskInstanceIds) throws WorkflowException; 

	/**
	 * @see gleam.executive.workflow.sm.JbpmOperations#isAnyProcessInstanceRunningAgainstTheCorpus(String, String)
	 */	
	public boolean isAnyProcessInstanceRunningAgainstTheCorpus(String processDefinitionName, String corpusId) throws WorkflowException; 
	
}
