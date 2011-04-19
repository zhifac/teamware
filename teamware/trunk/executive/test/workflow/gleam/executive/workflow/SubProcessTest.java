/*
 *  SubProcessTest.java
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
package gleam.executive.workflow;

import gleam.executive.model.Project;
import gleam.executive.service.ProjectManager;
import gleam.executive.workflow.core.JbpmTaskInstance;
import gleam.executive.workflow.manager.WorkflowManager;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.springframework.util.StringUtils;
import gleam.executive.workflow.util.JPDLConstants;

public class SubProcessTest extends BaseWorkflowServiceTestCase {

	private static WorkflowManager workflowManager = null;

	private static ProcessDefinition manualAnnotationProcessDefinition = null;
	private static ProcessDefinition automaticAnnotationProcessDefinition = null;
	private static long manualProcessInstanceId;
	private static long automaticProcessInstanceId;

	private static ProcessDefinition processDefinition = null;
	private static long processInstanceId;

	private static String[] annotators = { "agaton", "thomas", "niraj" };

	private static ProjectManager projectManager = null;

	public void testSubProcessCleanAll() throws Exception {
		// wait for the callback
		// ProcessDefinition def =
		// workflowManager.findLatestProcessDefinition("simple process");
		// assertNotNull("Definition should not be null", def);
		workflowManager = (WorkflowManager) applicationContext
				.getBean("workflowManager");
		projectManager = (ProjectManager) applicationContext
				.getBean("projectManager");

		List<ProcessInstance> processInstances = workflowManager
				.findAllProcessInstancesExcludingSubProcessInstances();
		Iterator<ProcessInstance> it = processInstances.iterator();
		while (it.hasNext()) {
			long processInstanceId = it.next().getId();
			log.debug("try to delete process instance " + processInstanceId);
			workflowManager.cancelProcessInstance(processInstanceId);
			log.debug("deleted process instance " + processInstanceId);
		}

		List processDefinitions = workflowManager.findAllProcessDefinitions();
		Iterator<ProcessDefinition> itr = processDefinitions.iterator();
		while (itr.hasNext()) {
			workflowManager.undeployProcessDefinition(itr.next());
		}

		// delete projects
		List<Project> projects = projectManager.getProjects();
		Iterator<Project> it1 = projects.iterator();
		while (it1.hasNext()) {
			long projectId = it1.next().getId();
			log.debug("try to delete project " + projectId);
			projectManager.removeProject(projectId);
			log.debug("deleted project " + projectId);
		}
		setComplete();
	}

	public void testSubProcessMainProcessDefinition() throws Exception {
		log.debug("#######################################");
		log.debug("#  testSubProcessMainProcessDefinition()  #");
		log.debug("#######################################");
		String processesDirLocation = processesDir + "/";
		String processArchiveLocation = processesDirLocation
				+ WorkflowTestData.PROCESS_ARCHIVE;
		log.debug("processesArchiveLocation " + processArchiveLocation);
		InputStream inputStream = new FileInputStream(processArchiveLocation);
		ZipInputStream zipInputStream = new ZipInputStream(inputStream);
		processDefinition = ProcessDefinition
				.parseParZipInputStream(zipInputStream);
		log.debug("processDefinition " + processDefinition.getName());
		if (zipInputStream != null)
			zipInputStream.close();
		if (inputStream != null)
			inputStream.close();
		// processDefinition.setName(Constants.MAIN_PROCESS_DEFINITION_NAME);
		workflowManager.deployProcessDefinition(processDefinition);
		log.debug("deployed processDefinition ID " + processDefinition.getId());
		log.debug("deployed processDefinition NAME "
				+ processDefinition.getName());
		setComplete();
	}

	public void testSubProcessManualAnnotationProcessDefinition()
			throws Exception {
		log.debug("#######################################");
		log.debug("#testSubProcessManualAnnotationProcessDefinition()#");
		log.debug("#######################################");
		String processesDirLocation = processesDir + "/";
		String processArchiveLocation = processesDirLocation
				+ WorkflowTestData.SUB_MANUAL_ANNOTATION_ARCHIVE;
		log.debug("processesArchiveLocation " + processArchiveLocation);
		InputStream inputStream = new FileInputStream(processArchiveLocation);
		ZipInputStream zipInputStream = new ZipInputStream(inputStream);
		manualAnnotationProcessDefinition = ProcessDefinition
				.parseParZipInputStream(zipInputStream);
		log.debug("manualAnnotationProcessDefinition name "
				+ manualAnnotationProcessDefinition.getName());
		if (zipInputStream != null)
			zipInputStream.close();
		if (inputStream != null)
			inputStream.close();
		workflowManager
				.deployProcessDefinition(manualAnnotationProcessDefinition);
		log.debug(" deployed manualAnnotationProcessDefinition ID "
				+ manualAnnotationProcessDefinition.getId());
		setComplete();
	}

	public void testSubProcessAutomaticAnnotationProcessDefinition()
			throws Exception {
		log.debug("#######################################");
		log.debug("#testSubProcessAutomaticAnnotationProcessDefinition()#");
		log.debug("#######################################");
		String processesDirLocation = processesDir + "/";
		String processArchiveLocation = processesDirLocation
				+ WorkflowTestData.SUB_AUTOMATIC_ANNOTATION_ARCHIVE;
		log.debug("processesArchiveLocation " + processArchiveLocation);
		InputStream inputStream = new FileInputStream(processArchiveLocation);
		ZipInputStream zipInputStream = new ZipInputStream(inputStream);
		automaticAnnotationProcessDefinition = ProcessDefinition
				.parseParZipInputStream(zipInputStream);
		log.debug("automaticAnnotationProcessDefinition name "
				+ automaticAnnotationProcessDefinition.getName());
		if (zipInputStream != null)
			zipInputStream.close();
		if (inputStream != null)
			inputStream.close();
		workflowManager
				.deployProcessDefinition(automaticAnnotationProcessDefinition);
		log.debug(" deployed automaticAnnotationProcessDefinition ID "
				+ automaticAnnotationProcessDefinition.getId());
		setComplete();
	}

	

	public void testSubProcessProcessInstanceStart() throws Exception {
		log.debug("#######################################");
		log.debug("#  testSubProcessProcessInstanceStart()         #");
		log.debug("#######################################");
		Map<String, Object> variableMap = new HashMap<String, Object>();
		variableMap.put(JPDLConstants.INITIATOR, WorkflowTestData.INITIATOR);
		variableMap.put(JPDLConstants.MODE, JPDLConstants.TEST_MODE);
		// variableMap.put(JPDLConstants.MODE, JPDLConstants.TEST_MODE);
		variableMap.put(JPDLConstants.CORPUS_ID, "corpusX");
		variableMap.put(JPDLConstants.ANNOTATORS_PER_DOCUMENT, "2");
		variableMap.put(JPDLConstants.ANNOTATOR_HAS_TO_BE_UNIQUE_FOR_DOCUMENT,
				"on");
		variableMap.put(JPDLConstants.ANONYMOUS_ANNOTATION, "on");
		variableMap.put(JPDLConstants.DO_MANUAL, "on");
		variableMap.put(JPDLConstants.DO_AUTOMATIC, "off");
		variableMap.put(JPDLConstants.DO_POST_PROCESSING, "off");
		variableMap.put(JPDLConstants.DO_REVIEW, "off");
		variableMap.put(JPDLConstants.DO_POST_MANUAL, "off");
		variableMap.put(JPDLConstants.CAN_CANCEL, "on");
		variableMap.put(JPDLConstants.PRE_MANUAL_SERVICE_ID, "1");
		String annotatorCSVList = StringUtils
				.arrayToCommaDelimitedString(annotators);
		variableMap.put(JPDLConstants.ANNOTATOR_CSV_LIST, annotatorCSVList);
		// variableMap.put(JPDLConstants.DO_SETUP, WorkflowTestD.ata.OFF);
		log.debug("starting instance from definition: "
				+ processDefinition.getId());
		ProcessInstance processInstance = workflowManager
				.createStartProcessInstance(processDefinition.getId(),
						variableMap, null);
		processInstanceId = processInstance.getId();
		log.debug("processInstanceId: " + processInstanceId);

		assertNotNull("Instance ID should not be null", processInstanceId);
		int numOfActiveProcessInstances = workflowManager
				.findUncompletedProcessInstancesForProcessDefinition(processDefinition
						.getId());
		log
				.debug("numOfActiveProcessInstances: "
						+ numOfActiveProcessInstances);
		assertEquals(1, numOfActiveProcessInstances);
		List<ProcessInstance> subProcessInstances = workflowManager
				.findAllSubProcessInstances(processInstance);
		int numOfActiveSubProcessInstances = subProcessInstances.size();
		log.debug("numOfActiveSubProcessInstances: "
				+ numOfActiveSubProcessInstances);
		assertEquals(1, numOfActiveSubProcessInstances);
		manualProcessInstanceId = subProcessInstances.get(0).getId();
		log.debug("manualProcessInstanceId: " + manualProcessInstanceId);
		setComplete();
	}

	public void testSubProcessManualAnnotationCreatingTasks() throws Exception {
		log.debug("#######################################");
		log
				.debug("#  testSubProcessManualAnnotationCreatingTasks()                #");
		log.debug("#######################################");
		ProcessInstance processInstance = loadProcessInstance();
		ProcessInstance manualSubProcessInstance = loadManualSubProcessInstance();
		List<TaskInstance> taskInstances = workflowManager
				.findTaskInstancesForProcessInstance(manualSubProcessInstance
						.getId());
		assertEquals(4, taskInstances.size());
		
		setComplete();
	}

	public void testSubProcessManualAnnotationGetTaskDoc1Annotator1()
			throws Exception {
		log.debug("#######################################");
		log.debug("#  testManualAnnotationGetTaskDoc1Annotator1()        #");
		log.debug("#######################################");
		ProcessInstance processInstance = loadProcessInstance();
		ProcessInstance manualSubProcessInstance = loadManualSubProcessInstance();
		List<TaskInstance> taskInstances = workflowManager
				.findPendingPooledAnnotationTaskInstances(annotators[0]);
		assertEquals(4, taskInstances.size());
		boolean found = workflowManager.checkForNextTask(annotators[0]);
		assertEquals(found, true);
		setComplete();
	}

	public void testSubProcessManualAnnotation11GASCallback() throws Exception {
		log.debug("#######################################");
		log
				.debug("#  testSubProcessManualAnnotation11GASCallback()                #");
		log.debug("#######################################");
		ProcessInstance processInstance = loadProcessInstance();
		ProcessInstance manualSubProcessInstance = loadManualSubProcessInstance();
		log.debug("Start waiting for callback ");
		Thread.sleep(10000);
		setComplete();
	}

	public void testSubProcessManualAnnotationTaskStartedDoc1Annotator1()
			throws Exception {
		log.debug("#######################################");
		log
				.debug("#  testSubProcessManualAnnotationTaskStartedDoc1Annotator1()    #");
		log.debug("#######################################");
		ProcessInstance processInstance = loadProcessInstance();
		ProcessInstance manualSubProcessInstance = loadManualSubProcessInstance();
		// check if the callback is made (task is started)
		Map map = workflowManager.getNextTaskAsMap(annotators[0]);
		assertNotNull(map);
		// it should be "doc1"
		long taskInstanceId = (Long) map.get(JPDLConstants.TASK_INSTANCE_ID);
		log.debug("TASK INSTANCE ID: " + taskInstanceId);
		JbpmTaskInstance taskInstance = (JbpmTaskInstance) workflowManager
				.getTaskInstance(taskInstanceId);
		assertEquals(taskInstance.getDocumentId(), "doc1");
		setComplete();
	}

	public void testSubProcessSuspend() throws Exception {
		log.debug("#######################################");
		log.debug("#  testSubProcessSuspend()        #");
		log.debug("#######################################");
		workflowManager.suspendProcessInstance(processInstanceId);
		ProcessInstance processInstance = loadProcessInstance();
		ProcessInstance manualSubProcessInstance = loadManualSubProcessInstance();

		assertEquals(processInstance.isSuspended(), true);

		assertEquals(manualSubProcessInstance.isSuspended(), true);
		setComplete();
	}

	public void testSubProcessManualAnnotationTaskSuspendedDoc1Annotator1()
			throws Exception {
		log.debug("#######################################");
		log
				.debug("#  testSubProcessManualAnnotationTaskSuspendedDoc1Annotator1()    #");
		log.debug("#######################################");
		ProcessInstance processInstance = loadProcessInstance();
		ProcessInstance manualSubProcessInstance = loadManualSubProcessInstance();

		// check if the callback is made (task is started)
		Map map = workflowManager.getNextTaskAsMap(annotators[0]);
		assertNull(map);
		setComplete();
	}

	public void testSubProcessResume() throws Exception {
		log.debug("#######################################");
		log.debug("#  testSubProcessResume()        #");
		log.debug("#######################################");
		workflowManager.resumeProcessInstance(processInstanceId);
		ProcessInstance processInstance = loadProcessInstance();
		ProcessInstance manualSubProcessInstance = loadManualSubProcessInstance();

		assertEquals(processInstance.isSuspended(), false);

		assertEquals(manualSubProcessInstance.isSuspended(), false);
		setComplete();
	}

	public void testSubProcessManualAnnotationTaskResumedDoc1Annotator1()
			throws Exception {
		log.debug("#######################################");
		log
				.debug("#  testSubProcessManualAnnotationTaskResumedDoc1Annotator1()    #");
		log.debug("#######################################");
		ProcessInstance processInstance = loadProcessInstance();
		ProcessInstance manualSubProcessInstance = loadManualSubProcessInstance();

		// check if the callback is made (task is started)
		Map map = workflowManager.getNextTaskAsMap(annotators[0]);
		assertNotNull(map);
		setComplete();
	}

	public ProcessInstance loadProcessInstance() throws Exception {
		return workflowManager.getProcessInstanceForUpdate(processInstanceId);
	}

	public ProcessInstance loadManualSubProcessInstance() throws Exception {
		return workflowManager.getProcessInstanceForUpdate(manualProcessInstanceId);
	}

	public ProcessInstance loadAutomaticSubProcessInstance() throws Exception {
		return workflowManager.getProcessInstanceForUpdate(automaticProcessInstanceId);
	}


}
