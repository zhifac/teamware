/*
 *  JbpmOperations.java
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
package gleam.executive.workflow.sm;

import gleam.executive.workflow.model.AnnotationMetricMatrix;
import gleam.executive.workflow.model.AnnotationStatusInfo;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.jbpm.file.def.FileDefinition;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;
import org.jbpm.taskmgmt.def.Swimlane;
import org.jbpm.taskmgmt.def.Task;
import org.jbpm.taskmgmt.exe.TaskInstance;

/**
 * @author Costin Leau
 * @author Milan Agatonovic
 */
public interface JbpmOperations {
	// added methods

	/**
	 * Cancel process instance
	 *
	 * @param processInstanceId
	 *            The processInstanceId to be canceled.
	 *
	 */
	public void cancelProcessInstance(final long processInstanceId);
	
	
	/**
	 * End process instance
	 *
	 * @param processInstanceId
	 *            The processInstanceId to be ended.
	 *
	 */
	public void endProcessInstance(final long processInstanceId);
	
	/**
	 * Accepts specified task instance
	 *
	 * @param taskInstanceId
	 */
	public void acceptTaskInstance(final long taskInstanceId, String actorId);

	/**
	 * Cancel specified task instance
	 *
	 * @param taskInstanceId
	 */
	public void cancelTaskInstance(final long taskInstanceId);

	/**
	 * Cancel specified task instance over specified transition
	 *
	 * @param taskInstanceId
	 * @param transitionName
	 */
	public void cancelTaskInstance(final long taskInstanceId, final String transitionName);

	/**
	 * Ends specified task instance
	 *
	 * @param taskInstanceId
	 */
	public void endTaskInstance(final long taskInstanceId);
	
	/**
	 * Completes task
	 *
	 * @param task
	 *            The taskId of task to be completed.
	 * @param variables.
	 *            Task variables map
	 * @return TaskInstance that is completed
	 */
	public TaskInstance completeTask(final long taskId, final Map variables,
			final String transition);

	/**
	 * Create process instance
	 *
	 * @param definitionId
	 *            the process definition Id to be started.
	 * @return process instance that is created
	 */
	public ProcessInstance createProcessInstance(final long processInstanceId);

	/**
	 * Creates process instance from process definition
	 *
	 * @param processName
	 *            the name of process definition
	 * @return process instance
	 */
	public ProcessInstance createProcessInstance(final String processName);

	
	/**
	 * Creates process instance from process definition
	 *
	 * @param definitionId
	 *            the id of process definition
	 * @param variablesMap = key value pairs of process variables
	 * @param key friendly name of process instance
	 * @return process instance
	 */
	public ProcessInstance createProcessInstance(final long definitionId,
			final Map variablesMap, final String key);


	/**
	 * Creates and starts process instance from process definition
	 *
	 * @param definitionId
	 *            the id of process definition
	 * @param variablesMap = key value pairs of process variables
	 * @param key friendly name of process instance
	 * @return process instance
	 */
	public ProcessInstance createStartProcessInstance(final long definitionId,
			final Map variablesMap, final String key);

	/**
	 * Creates process instance from process definition
	 *
	 * @param definitionId
	 *            the id of process definition
	 * @return process instance
	 */
	public ProcessInstance createStartProcessInstance(final long definitionId);
	/**
	 * Check if process definition exists
	 *
	 * @param definitionName
	 * @return Boolean - true if definition with specified name exists,
	 *         otherwise false
	 */
	public Boolean definitionExists(final String definitionName);

	/**
	 * Deploys process definition in DB
	 *
	 * @param newProcessDefinition
	 *            process definition to be uploaded
	 * @return process definition
	 */
	public ProcessDefinition deployProcessDefinition(
			final ProcessDefinition newProcessDefinition);

	
	/**
	 * Executes callback
	 *
	 * @param task instance
	 *            ID which will be ended
	 * @param error
	 */
	public void executeCallbackAndSignalToken(final long tokenId,
			final String error);
	
	/**
	 * Executes callback
	 *
	 * @param task instance
	 *            ID which will be ended
	 * @param error
	 */
	public void executeCallbackAndEndTask(final long taskInstanceId,
			final String error);

	/**
	 * Fetch all task instances with specified user as pooled actor
	 *
	 * @param actorId
	 * @return List of all task instances with specified user as pooled actor
	 */
	public List findAllPooledTaskInstances(final String actorId);

	/**
	 * Fetch all available process instances in DB
	 *
	 * @return List of all process instances
	 */
	public List findAllProcessInstances();

	/**
	 * Fetch all process instances in DB excluding subprocess instances
	 *
	 * @return List of all process instances excluding subprocess instances
	 */
	public List findAllProcessInstancesExcludingSubProcessInstances();

	
	/**
     *
	 * fetches all available processInstances excluding subprocesses with the same key and name
	 * @return list of process instances is sorted start date, latest 1st
	 */

     public List findProcessInstancesExcludingSubProcessInstancesByKeyAndName(String key, String name);
	   
	/**
	 * Fetch all process instances in DB excluding subprocess instances for specified process definition id
	 *
	 * @return List of all process instances excluding subprocess instances
	 */
	public List findAllProcessInstancesByProcessDefinitionIdExcludingSubProcessInstances(long processDefinitionId);
	
	/**
	 * Fetch all process instances in DB excluding subprocess instances for specified process definition name
	 *
	 * @return List of all process instances excluding subprocess instances
	 */
	public List findAllProcessInstancesByProcessDefinitionNameExcludingSubProcessInstances(String processDefinitionName);
	
	
	/**
	 * Fetch all available process instances for specified process definition
	 *
	 * @param definitionId
	 *            the process definition Id
	 * @return List of all process instances
	 */
	public List findAllProcessInstances(final long definitionId);

	/**
	 * Fetch all task instances with specified user as actor.
	 * It does not fetch pooled tasks
	 *
	 * @param actorId
	 * @return List of all task instances with specified user as actor
	 */
	public List findAllTaskInstances(final String actorId);

	/**
	 * Fetch file definition
	 *
	 * @param processDefinitionId.
	 *            The Id of the process, file definition belongs to
	 * @return FileDefinition
	 */
	public FileDefinition findFileDefinition(final long processDefinitionId);

	/**
	 * Finds latest process definition (the newest version)
	 *
	 * @param processName
	 *            the name of process definition
	 * @return process definition
	 */
	public ProcessDefinition findLatestProcessDefinition(
			final String processName);

	/**
	 * Fetch all available pooled task instances for group of actor
	 *
	 * @param actorIds
	 *            List of actor IDs.
	 * @return List of task instances
	 */
	public List findPooledTaskInstances(final List actorIds);

	/**
	 * Fetch all available task instances (excluding annotation task instances) for pooled actor.
	 * it does not fetch tasks with NOT NULL actorId field
	 *
	 * @param actorId
	 * @return List of task instances
	 */
	public List findPooledTaskInstances(final String actorId);
	
	
	/**
	 * Fetch all available annotation task instances for pooled actor.
	 * it does not fetch tasks with NOT NULL actorId field
	 *
	 * @param actorId
	 * @return List of task instances
	 */
	public List findPendingPooledAnnotationTaskInstances(final String actorId);

	/**
	 * Fetch proces definition
	 *
	 * @param definitionId.
	 *            The process definition Id to be fetched.
	 * @return ProcessDefinition
	 */
	public ProcessDefinition findProcessDefinition(final long definitionId);
	
	/**
	 * Fetch proces definition
	 *
	 * @param processDefinitionName.
	 *            The process definition name
	 * @param version.
	 *            The version         
	 * @return ProcessDefinition
	 */
	public ProcessDefinition findProcessDefinitionByNameAndVersion(final String processDefinitionName, final Integer version); 
	
	/**
	 * Fetch proces definition
	 *
	 * @param processDefinitionName.
	 *            The process definition name    
	 * @return ProcessDefinition
	 */
	public List<ProcessDefinition> findProcessDefinitionsByName(final String processDefinitionName); 
	
	/**
	 * Loads proces definition
	 *
	 * @param definitionId.
	 *            The process definition Id to be loaded.
	 * @return ProcessDefinition
	 */
	public ProcessDefinition loadProcessDefinition(final long definitionId);
	

	// public ProcessDefinition redeployProcessDefinition(final long
	// definitionId,
	// final String filePath);
	/**
	 * Fetch proces definition of process instance
	 *
	 * @param processInstanceId.
	 *            The process instance Id.
	 * @return ProcessDefinition
	 */
	public ProcessDefinition findProcessDefinitionByProcessInstanceId(
			final Long processInstanceId);

	/**
	 * Fetch proces definition for task
	 *
	 * @param taskId.
	 *            The Id of task .
	 * @return process definition
	 */
	public ProcessDefinition findProcessDefinitionByTaskInstanceId(final long taskId);

	/**
	 * Fetch process instance from DB
	 *
	 * @param processInstanceId
	 *            The processInstanceId .
	 * @return ProcessInstance that is started
	 */
	public ProcessInstance findProcessInstance(final Long processInstanceId);

	/**
	 * Fetch all available process instances in context
	 *
	 * @return List of all process instances
	 */
	public List findProcessInstances();

	/**
	 * Fetch the latest versions of process definitions that can be started
	 *
	 * @return array of ProcessDefinitions
	 */
	public List findStartableProcessDefinitions();

	/**
	 * Fetch swimlanenames by task id
	 *
	 * @param taskId
	 * @return string array of swimlane names
	 */
	public String[] findSwimlaneNames(final Long taskId);

	/**
	 * Fetch process instance swimlanes
	 *
	 * @param process
	 *            instance Id
	 * @return array of Swimlanes
	 */
	public Swimlane[] findSwimlanes(final Long instanceId);

	/**
	 * Fetch all available task instances for actor
	 *
	 * @param actorIds
	 *            List of actor IDs.
	 * @return List of task instances
	 */
	public List findTaskInstances(final List actorIds);

	/**
	 * Fetch all available task instances for actor
	 *
	 * @param actorId
	 * @return List of task instances
	 */
	public List findPendingTaskInstancesByActorId(final String actorId);

	/**
	 * Fetch all available task instances for actor
	 *
	 * @param actorIds
	 *            Arrray of actor IDs.
	 * @return List of task instances
	 */
	public List findTaskInstances(final String[] actorIds);

	/**
	 * Fetch all task instances for token
	 *
	 * @param token
	 *            Id
	 * @return List of task instances
	 */
	public List findTaskInstancesByToken(final long tokenId);

	/**
	 * Fetch all task instances for token
	 *
	 * @param Token
	 * @return List of task instances
	 */
	public List findTaskInstancesByToken(Token token);

	/**
	 * Find all available tasks by specified process definition
	 *
	 * @param definitionId
	 * @return Map - (taskName, Task)
	 */
	public Map findTasksByProcessDefinitionId(final long definitionId);

	/**
	 * Fetch task variables by name
	 *
	 * @param taskId
	 * @param variableName
	 * @return Map of name-values
	 */
	public Object findVariable(final Long taskId, final String variableName);

	/**
	 * Fetch task variables' accesses
	 *
	 * @param task
	 *            Instance Id
	 * @return List of VariableAccess objects
	 */
	public List findVariableAccesses(final Long taskId);

	/**
	 * Fetch task variables' names for task
	 *
	 * @param taskId
	 * @return array of variables' names
	 */
	public String[] findVariableNames(final Long taskId);

	// end of edded by agaton
	// end of added methods

	/**
	 * Fetch task variables
	 *
	 * @param TaskInstance
	 * @return Map of name-values
	 */
	public Map findVariables(final TaskInstance taskInstance);

	/**
	 * Fetch available transitions for task instance
	 *
	 * @param task
	 *            Instance Id
	 * @return List of available Transition objects
	 */
	public List getAvailableTransitions(final Long taskInstanceId);

	/**
	 * Fetch process instance from current context
	 *
	 * @param processInstanceId The
	 *            processInstanceId .
	 * @return ProcessInstance
	 */
	public ProcessInstance getProcessInstance(final long processInstanceId);

	
	/**
	 * Fetch process instance from current context by locking parent row 
	 * Recommended to use when dealing with subprocesses
	 *
	 * @param processInstanceId The
	 *            processInstanceId .
	 * @return ProcessInstance
	 */
	public ProcessInstance getProcessInstanceForUpdate(final long processInstanceId);


	/**
	 * Fetch task instance
	 *
	 * @param task
	 *            Instance Id
	 * @return Task Instance
	 */
	public TaskInstance getTaskInstance(final Long taskId);

	/**
	 * Resume specified task instance
	 *
	 * @param taskInstanceId
	 */
	public void resumeTaskInstance(final long taskInstanceId);

	/**
	 * Save task
	 *
	 * @param Task
	 *            to be saved
	 */
	public void saveOrUpdateTask(final Task task);

	/**
	 * Updates the annotation status information
	 *
	 * @param taskInstanceId
	 */
	public void updateStatus(final long taskInstanceId);

	/**
	 * Save swimlane
	 *
	 * @param Swimlane
	 *            to be saved
	 */
	public void saveOrUpdateSwimlane(final Swimlane swimlane);


	/**
	 * Saves process instance in context
	 *
	 * @param processInstance
	 * @return process Instance Id
	 */
	public Long saveProcessInstance(final ProcessInstance processInstance);

	/**
	 * Signal process instance for default transition and root token
	 *
	 * @param processInstance
	 */
	public void signal(final ProcessInstance processInstance);

	/**
	 * Signal process instance for specified transition and root token
	 *
	 * @param processInstance
	 * @param transitionId
	 */
	public void signal(final ProcessInstance processInstance,
			final String transitionId);

	/**
	 * Signal process instance for specified transition and root token
	 *
	 * @param processInstance
	 * @param Transition
	 */
	public void signal(final ProcessInstance processInstance,
			final Transition transition);

	/**
	 * Signals a specific token in a process instance. Used to progress through
	 * execution paths other than the main one. If the token could not be found,
	 * the root token is signaled (main execution path).
	 *
	 * @param processInstance
	 *            process instance to progress through
	 * @param tokenName
	 *            name of the token to signal
	 */
	public void signalToken(final ProcessInstance processInstance,
			final String tokenName);

	/**
	 * Signals a specific token with the given transition to take.
	 *
	 * @param processInstance
	 *            process instance to progress through
	 * @param tokenName
	 *            name of the token to signal
	 * @param transitionId
	 *            transition to take in the execution path
	 */
	public void signalToken(final ProcessInstance processInstance,
			final String tokenName, final String transitionId);

	/**
	 * Start process instance
	 *
	 * @param definitionName
	 *            The process definition Name to be started.
	 * @return ProcessInstance that is started
	 */
	public ProcessInstance startProcessInstance(final String definitionName);

	/**
	 * Start process instance
	 *
	 * @param processInstance
	 *            The process instance to be started.
	 * @return ProcessInstance that is started
	 */
	public ProcessInstance startProcessInstance(final ProcessInstance processInstance);
	
	/**
	 * Start process instance
	 *
	 * @param processInstanceId
	 *            The process instance id to be started.
	 * @return ProcessInstance that is started
	 */
	public ProcessInstance startProcessInstance(final long processInstanceId);
	
	/**
	 * Suspend process instance
	 *
	 * @param processInstanceId
	 *            The processInstanceId to be suspended.
	 */
	public void suspendProcessInstance(final long processInstanceId);

	
	public List findAllSubProcessInstances(final ProcessInstance processInstance);
	
	/**
	 * Adds a comment to the root token
	 *
	 * @param comment comment
	 */
	public void addCommentToRootToken(final ProcessInstance processInstance, final String comment);
		
	/**
	 * Start specified task instance
	 *
	 * @param taskInstanceId
	 */
	public void startTaskInstance(final long taskInstanceId);


	/**
	 * Suspend specified task instance
	 *
	 * @param taskInstanceId
	 */
	public void suspendTaskInstance(final long taskInstanceId);

	/**
	 * Undeploys process process definition
	 *
	 * @param definitionId
	 *            the process definition Id to be undeployed.
	 */
	public void undeployProcessDefinition(final long definitionId);


	/**
	 * Fetch all available task instances for process instance
	 *
	 * @param processInstanceId
	 *            processInstanceId
	 * @return List of task instances
	 */
	public List findTaskInstancesForProcessInstance(final long processInstanceId);

	/**
	 * Gets next TaskInstance for specified actor

	 * @param actorId
	 *            the user name of actor
	 * @return TaskInstance
	 */
	public TaskInstance getNextTask(final String actorId);

	/**
	 * Checks if there is new task for user.

	 * @param actorId
	 *            the user name of performer
	 * @return true or false
	 */
	public Boolean checkIfThereIsNextTaskInstanceForActor(final String actorId);

	/**
	 * Finds number of uncompleted process instances.

	 * @param processDefinitionId
	 *            the process Definition Id
	 * @return number of uncompleted process instances
	 */
	public int findUncompletedProcessInstancesForProcessDefinition(final long processDefinitionId);

	/**
	 * Finds all process definitions.
	 * @return List of process definitions
	 */
	public List<ProcessDefinition> findAllProcessDefinitions();

	/**
	 * Nulls actorId - returns task to the pool and updates annotation status variable
	 */
	public void rejectTaskInstance(final long taskInstanceId);

	/**
	 * Starts task instance, in case there is no error and updates annotation status variable accordingly
	 */
	public void executeCallbackAndStartTask(final long taskInstanceId, String error);

	/**
	 * Finds pending task instances for specified process Instance and task name
	 */
	public List findPendingTaskInstancesByProcessInstanceAndName (final long processInstanceId, final String taskName);

		
	/**
	 * Finds all swimlanes in specified process definition
	 */
	public Map findSwimlanesByProcessDefinitionId(final long processDefinitionId);

	
    //	 CONFIGURATION OPTIONS - MANAGER INTERFACE
	/**
	 * Finds pending Configuration Options instances for specified process Instances and actorId
	 */
	public List findPendingConfigurationOptionsByProcessInstancesAndActorId(final ProcessInstance[] processInstances, final String actorId);

	
	/**
     * get pending Configuration Options list for a given process instance key with specified actor.
     */
    public List findPendingConfigurationOptionsByProcessInstanceKeyAndActorId(final String key, final String actorId);
	
	
	/**
	   * get pending Configuration Options list with specified actor.
	   */	
	public List findPendingConfigurationOptionsByActorId (final String actorId);
	
	/**
	   * get map with document status
	   */
	public List<AnnotationStatusInfo> getDocumentStatusList(long processInstanceId);
		
	/**
	   * get map with document status
	   */
	public AnnotationMetricMatrix getDocumentMatrix(long processInstanceId);
	
	
	
	/**
	   * get map with personal annotator record
	   */
	public AnnotationMetricMatrix getAnnotatorMatrix(long processInstanceId, String username);
	
	/**
	   * get map with global annotator record
	   */
	public AnnotationMetricMatrix getGlobalAnnotatorMatrix(long processInstanceId, String roleName);
	
	/**
	   * get map with personal annotator record for all processes
	   */
	public AnnotationMetricMatrix getAnnotatorMatrixForAllProcesses(String username);

	
	/**
	   * updates variables in main process and all subprocesses recursively
	   */
	public void updateVariables(final ProcessInstance processInstance,
			final Map variableMap);
	
	/**
	   * updates pooled actors in main process and all subprocesses recursively
	   */
	public void updatePooledActors (String variableName, String[] newActorIds, ProcessInstance processInstance);


	/**
	   * deletes task instances with specified ids
	   */
	public void deleteTaskInstances(final Collection<Long> taskInstanceIds); 

	/**
	   * checks if there is running process instance against the specified corpus
	   */
	public Boolean isAnyProcessInstanceRunningAgainstTheCorpus(final String processDefinitionName, final String corpusId);
}
