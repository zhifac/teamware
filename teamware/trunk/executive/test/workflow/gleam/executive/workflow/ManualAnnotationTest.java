/*
 *  ManualAnnotationTest.java
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


public class ManualAnnotationTest extends BaseWorkflowServiceTestCase {

	 private static WorkflowManager workflowManager = null;

	 private static final String PROCESS_ARCHIVE = "manualAnnotation.zip";

	 private static ProcessDefinition processDefinition = null;
	 private static long processInstanceId;
	 private static String[] annotators = {"agaton", "thomas", "niraj"};

	 
	 private static ProjectManager projectManager = null;

	 public void testCleanAll() throws Exception {
	      // wait for the callback
		  //ProcessDefinition def = workflowManager.findLatestProcessDefinition("simple process");
	      //assertNotNull("Definition should not be null", def);
		  workflowManager = (WorkflowManager) applicationContext.getBean("workflowManager");
		  projectManager = (ProjectManager) applicationContext.getBean("projectManager");

          List<ProcessInstance> processInstances = workflowManager.findAllProcessInstancesExcludingSubProcessInstances();
	      Iterator<ProcessInstance> it = processInstances.iterator();
	      while(it.hasNext()){
	    	  long processInstanceId = it.next().getId();
	    	  log.debug("try to delete process instance " + processInstanceId);
	    	  workflowManager.cancelProcessInstance(processInstanceId);
	    	  log.debug("deleted process instance " + processInstanceId);
	      }

	      List processDefinitions = workflowManager.findAllProcessDefinitions();
	      Iterator<ProcessDefinition> itr = processDefinitions.iterator();
	      while(itr.hasNext()){
	    	  workflowManager.undeployProcessDefinition(itr.next());
	      }
	      
	      // delete projects
	      List<Project> projects = projectManager.getProjects();
	      Iterator<Project> it1 = projects.iterator();
	      while(it1.hasNext()){
	    	  long projectId = it1.next().getId();
	    	  log.debug("try to delete project " + projectId);
	    	  projectManager.removeProject(projectId);
	    	  log.debug("deleted project " + projectId);
	      }
	      setComplete();
	  }
	 
  public void testManualAnnotationProcessDefinition() throws Exception {
	  log.debug("#######################################");
	  log.debug("#  testManualAnnotationProcessDefinition()            #");
	  log.debug("#######################################");
	  String processesDirLocation = processesDir + "/";
	  String processArchiveLocation = processesDirLocation + PROCESS_ARCHIVE;
	  log.debug("processesArchiveLocation "+ processArchiveLocation);
	  InputStream inputStream = new FileInputStream(processArchiveLocation);
	  ZipInputStream zipInputStream = new ZipInputStream(inputStream);
	  processDefinition = ProcessDefinition.parseParZipInputStream(zipInputStream);
	  log.debug("processDefinition " + processDefinition.getName());
	  if(zipInputStream!=null) zipInputStream.close();
	  if(inputStream!=null) inputStream.close();
	  //workflowManager = (WorkflowManager) applicationContext.getBean("workflowManager");
	  workflowManager.deployProcessDefinition(processDefinition);
	  setComplete();
  }


  public void testManualAnnotationProcessInstanceStart() throws Exception {
	  log.debug("#######################################");
	  log.debug("#  testManualAnnotationProcessInstanceStart()         #");
	  log.debug("#######################################");
	  Map<String, Object> variableMap = new HashMap<String, Object>();
	  variableMap.put(JPDLConstants.INITIATOR, WorkflowTestData.INITIATOR);
	  variableMap.put(JPDLConstants.MODE, JPDLConstants.TEST_MODE);
	  variableMap.put(JPDLConstants.CORPUS_ID, "corpusX");
	  variableMap.put(JPDLConstants.ANNOTATORS_PER_DOCUMENT, "2");
	  variableMap.put(JPDLConstants.ANNOTATOR_HAS_TO_BE_UNIQUE_FOR_DOCUMENT, "on");
	  variableMap.put(JPDLConstants.ANONYMOUS_ANNOTATION, "on");
	  variableMap.put(JPDLConstants.ANNOTATOR_CSV_LIST, StringUtils.arrayToCommaDelimitedString(annotators));
	  variableMap.put(JPDLConstants.CAN_CANCEL, "on");
	  variableMap.put(JPDLConstants.PRE_MANUAL_SERVICE_ID, "1");
	  //variableMap.put(JPDLConstants.DO_SETUP, WorkflowTestData.OFF);
	  ProcessInstance processInstance = workflowManager.createStartProcessInstance(processDefinition.getId(), variableMap, null);
      processInstanceId = processInstance.getId();
      assertNotNull("Instance ID should not be null", processInstanceId);
      int numOfActiveProcessInstances = workflowManager.findUncompletedProcessInstancesForProcessDefinition(processDefinition.getId());
      assertEquals(1, numOfActiveProcessInstances);
      setComplete();
  }


  public ProcessInstance loadProcessInstance()throws Exception {
	  return workflowManager.getProcessInstanceForUpdate(processInstanceId);
  }

  
  public void testManualAnnotationCreatingTasks() throws Exception {
	  log.debug("#######################################");
	  log.debug("#  testManualAnnotationCreatingTasks()                #");
	  log.debug("#######################################");
	  ProcessInstance processInstance = loadProcessInstance();
      List<TaskInstance> taskInstances = workflowManager.findTaskInstancesForProcessInstance(processInstance.getId());
      assertEquals(4, taskInstances.size());
      setComplete();
  }

  public void testManualAnnotationGetTaskDoc1Annotator1() throws Exception {
	  log.debug("#######################################");
	  log.debug("#  testManualAnnotationGetTaskDoc1Annotator1()        #");
	  log.debug("#######################################");
	  ProcessInstance processInstance = loadProcessInstance();
      List<TaskInstance> taskInstances = workflowManager.findPendingPooledAnnotationTaskInstances(annotators[0]);
      assertEquals(4, taskInstances.size());
      boolean found = workflowManager.checkForNextTask(annotators[0]);
      assertEquals(found, true);
      setComplete();
  }


  public void testManualAnnotation11GASCallback() throws Exception {
	  log.debug("#######################################");
	  log.debug("#  testManualAnnotation11GASCallback()                #");
	  log.debug("#######################################");
	  ProcessInstance processInstance = loadProcessInstance();
	  log.debug("Start waiting for callback ");
      Thread.sleep(10000);
      setComplete();
  }

  public void testManualAnnotationTaskStartedDoc1Annotator1() throws Exception {
	  log.debug("#######################################");
	  log.debug("#  testManualAnnotationTaskStartedDoc1Annotator1()    #");
	  log.debug("#######################################");
	  ProcessInstance processInstance = loadProcessInstance();
      // check if the callback is made (task is started)
      Map map = workflowManager.getNextTaskAsMap(annotators[0]);
      assertNotNull(map);
      // it should be "doc1"
      long taskInstanceId = (Long)map.get(JPDLConstants.TASK_INSTANCE_ID);
      JbpmTaskInstance taskInstance = (JbpmTaskInstance)workflowManager.getTaskInstance(taskInstanceId);
      assertEquals(taskInstance.getDocumentId(), "doc1");
      setComplete();
  }

  public void testManualAnnotationTaskCompletedDoc1Annotator1() throws Exception {
	  log.debug("#######################################");
	  log.debug("#  testManualAnnotationTaskCompletedDoc1Annotator1()  #");
	  log.debug("#######################################");
	  ProcessInstance processInstance = loadProcessInstance();
      // check if the callback is made (task is started)
	  Map map = workflowManager.getNextTaskAsMap(annotators[0]);
      long taskInstanceId = (Long)map.get(JPDLConstants.TASK_INSTANCE_ID);
	  workflowManager.completeTask(taskInstanceId, null, null);
	  TaskInstance taskInstance = workflowManager.getTaskInstance(taskInstanceId);
	  assertNotNull(taskInstance.getEnd());
      setComplete();
  }

  public void testManualAnnotationGetTaskAnnotator1Doc2() throws Exception {
	  log.debug("#######################################");
	  log.debug("#  testManualAnnotationGetTaskAnnotator1Doc2()        #");
	  log.debug("#######################################");
	  ProcessInstance processInstance = loadProcessInstance();
      List<TaskInstance> taskInstances = workflowManager.findPendingPooledAnnotationTaskInstances(annotators[0]);
      assertEquals(3, taskInstances.size());
      boolean found = workflowManager.checkForNextTask(annotators[0]);
      assertEquals(found, true);
      setComplete();
  }

  public void testManualAnnotation12GASCallback() throws Exception {
	  log.debug("#######################################");
	  log.debug("#  testManualAnnotation12GASCallback()                #");
	  log.debug("#######################################");
	  ProcessInstance processInstance = loadProcessInstance();
	  log.debug("Start waiting for callback ");
      Thread.sleep(10000);
      setComplete();
  }


  public void testManualAnnotationTaskStartedAnnotator1Doc2() throws Exception {
	  log.debug("#######################################");
	  log.debug("#  testManualAnnotationTaskStartedAnnotator1Doc2()    #");
	  log.debug("#######################################");
	  ProcessInstance processInstance = loadProcessInstance();
      // check if the callback is made (task is started)
      Map map = workflowManager.getNextTaskAsMap(annotators[0]);
      assertNotNull(map);
      long taskInstanceId = (Long)map.get(JPDLConstants.TASK_INSTANCE_ID);
      log.debug("taskInstanceId " + taskInstanceId);
      JbpmTaskInstance taskInstance = (JbpmTaskInstance)workflowManager.getTaskInstance(taskInstanceId);
      assertEquals(taskInstance.getDocumentId(), "doc2");
      setComplete();
  }

  public void testManualAnnotationTaskCompletedAnnotator1Doc2() throws Exception {
	  log.debug("#######################################");
	  log.debug("#  testManualAnnotationTaskCompletedAnnotator1Doc2()  #");
	  log.debug("#######################################");
	  ProcessInstance processInstance = loadProcessInstance();
      // check if the callback is made (task is started)
	  Map map = workflowManager.getNextTaskAsMap(annotators[0]);
      long taskInstanceId = (Long)map.get(JPDLConstants.TASK_INSTANCE_ID);
	  workflowManager.completeTask(taskInstanceId, null, null);
	  TaskInstance taskInstance = workflowManager.getTaskInstance(taskInstanceId);
	  assertNotNull(taskInstance.getEnd());
      setComplete();
  }

  public void testManualAnnotationNotGettingTaskForAnnotator1() throws Exception {
	  log.debug("#######################################");
	  log.debug("#  testManualAnnotationNotGettingTaskForAnnotator1()  #");
	  log.debug("#######################################");
	  ProcessInstance processInstance = loadProcessInstance();
      List<TaskInstance> taskInstances = workflowManager.findPendingPooledAnnotationTaskInstances(annotators[0]);
      assertEquals(2, taskInstances.size());
      boolean found = workflowManager.checkForNextTask(annotators[0]);
      assertEquals(found, false);
      setComplete();
  }

  public void testManualAnnotationGetTaskAnnotator2Doc1() throws Exception {
	  log.debug("#######################################");
	  log.debug("#  testManualAnnotationGetTaskAnnotator2Doc1()        #");
	  log.debug("#######################################");
	  ProcessInstance processInstance = loadProcessInstance();
      List<TaskInstance> taskInstances = workflowManager.findPendingPooledAnnotationTaskInstances(annotators[0]);
      assertEquals(2, taskInstances.size());
      boolean found = workflowManager.checkForNextTask(annotators[1]);
      assertEquals(found, true);
      setComplete();
  }

  public void testManualAnnotation21GASCallback() throws Exception {
	  log.debug("#######################################");
	  log.debug("#  testManualAnnotation21GASCallback()                #");
	  log.debug("#######################################");
	  // wait for the callback
	  ProcessInstance processInstance = loadProcessInstance();
	  log.debug("Start waiting for callback ");
      Thread.sleep(10000);
      setComplete();
  }


  public void testManualAnnotationTaskStartedAnnotator2Doc1() throws Exception {
	  log.debug("#######################################");
	  log.debug("#  testManualAnnotationTaskStartedAnnotator2Doc1()    #");
	  log.debug("#######################################");
	  ProcessInstance processInstance = loadProcessInstance();
      // check if the callback is made (task is started)
      Map map = workflowManager.getNextTaskAsMap(annotators[1]);
      assertNotNull(map);
      long taskInstanceId = (Long)map.get(JPDLConstants.TASK_INSTANCE_ID);
      log.debug("taskInstanceId " + taskInstanceId);
      JbpmTaskInstance taskInstance = (JbpmTaskInstance)workflowManager.getTaskInstance(taskInstanceId);
      assertEquals(taskInstance.getDocumentId(), "doc1");
      setComplete();
  }

  public void testManualAnnotationGetTaskAnnotator3Doc2() throws Exception {
	  log.debug("#######################################");
	  log.debug("#  testManualAnnotationGetTaskAnnotator3Doc2()        #");
	  log.debug("#######################################");
	  ProcessInstance processInstance = loadProcessInstance();
      List<TaskInstance> taskInstances = workflowManager.findPendingPooledAnnotationTaskInstances(annotators[0]);
      assertEquals(1, taskInstances.size());
      boolean found = workflowManager.checkForNextTask(annotators[2]);
      assertEquals(found, true);
      setComplete();
  }

  public void testManualAnnotation32GASCallback() throws Exception {
	  log.debug("#######################################");
	  log.debug("#  testManualAnnotation32GASCallback()                #");
	  log.debug("#######################################");
	  ProcessInstance processInstance = loadProcessInstance();
	  log.debug("Start waiting for callback ");
      Thread.sleep(10000);
      setComplete();
  }


  public void testManualAnnotationTaskStartedAnnotator3Doc2() throws Exception {
	  log.debug("#######################################");
	  log.debug("#  testManualAnnotationTaskStartedAnnotator3Doc2()    #");
	  log.debug("#######################################");
	  ProcessInstance processInstance = loadProcessInstance();
      // check if the callback is made (task is started)
      Map map = workflowManager.getNextTaskAsMap(annotators[2]);
      assertNotNull(map);
      long taskInstanceId = (Long)map.get(JPDLConstants.TASK_INSTANCE_ID);
      log.debug("taskInstanceId " + taskInstanceId);
      JbpmTaskInstance taskInstance = (JbpmTaskInstance)workflowManager.getTaskInstance(taskInstanceId);
      assertEquals(taskInstance.getDocumentId(), "doc2");
      setComplete();
  }

  public void testManualAnnotationTaskRejectedAnnotator2Doc1() throws Exception {
	  log.debug("#######################################");
	  log.debug("#  testManualAnnotationTaskRejectedAnnotator2Doc1()   #");
	  log.debug("#######################################");
	  ProcessInstance processInstance = loadProcessInstance();
      // check if the callback is made (task is started)
	  Map map = workflowManager.getNextTaskAsMap(annotators[1]);
      long taskInstanceId = (Long)map.get(JPDLConstants.TASK_INSTANCE_ID);
	  workflowManager.rejectTaskInstance(taskInstanceId);
	  TaskInstance taskInstance = workflowManager.getTaskInstance(taskInstanceId);
	  assertNull(taskInstance.getEnd());
      setComplete();
  }

  public void testManualAnnotationNotGettingTaskForAnnotator2() throws Exception {
	  log.debug("#######################################");
	  log.debug("#  testManualAnnotationNotGettingTaskForAnnotator2()  #");
	  log.debug("#######################################");
	  ProcessInstance processInstance = loadProcessInstance();
      List<TaskInstance> taskInstances = workflowManager.findPendingPooledAnnotationTaskInstances(annotators[0]);
      assertEquals(1, taskInstances.size());
      boolean found = workflowManager.checkForNextTask(annotators[1]);
      assertEquals(found, false);
      setComplete();
  }

  public void testManualAnnotationTaskCompletedAnnotator3Doc2() throws Exception {
	  log.debug("#######################################");
	  log.debug("#  testManualAnnotationTaskCompletedAnnotator3Doc2()  #");
	  log.debug("#######################################");
	  ProcessInstance processInstance = loadProcessInstance();
      // check if the callback is made (task is started)
	  Map map = workflowManager.getNextTaskAsMap(annotators[2]);
      long taskInstanceId = (Long)map.get(JPDLConstants.TASK_INSTANCE_ID);
	  workflowManager.completeTask(taskInstanceId, null, null);
	  TaskInstance taskInstance = workflowManager.getTaskInstance(taskInstanceId);
	  assertNotNull(taskInstance.getEnd());
      setComplete();
  }

  public void testManualAnnotationGetTaskAnnotator3Doc1() throws Exception {
	  log.debug("#######################################");
	  log.debug("#  testManualAnnotationGetTaskAnnotator3Doc1()        #");
	  log.debug("#######################################");
	  ProcessInstance processInstance = loadProcessInstance();
      List<TaskInstance> taskInstances = workflowManager.findPendingPooledAnnotationTaskInstances(annotators[2]);
      assertEquals(1, taskInstances.size());
      boolean found = workflowManager.checkForNextTask(annotators[2]);
      assertEquals(found, true);
      setComplete();
  }

  public void testManualAnnotation31GASCallback() throws Exception {
      // wait for the callback
	  log.debug("#######################################");
	  log.debug("#  testManualAnnotation31GASCallback()                #");
	  log.debug("#######################################");
	  ProcessInstance processInstance = loadProcessInstance();
	  log.debug("Start waiting for callback ");
      Thread.sleep(10000);
      setComplete();
  }


  public void testManualAnnotationTaskStartedAnnotator3Doc1() throws Exception {
	  log.debug("#######################################");
	  log.debug("#  testManualAnnotationTaskStartedAnnotator3Doc1()    #");
	  log.debug("#######################################");
	  ProcessInstance processInstance = loadProcessInstance();
      // check if the callback is made (task is started)
      Map map = workflowManager.getNextTaskAsMap(annotators[2]);
      assertNotNull(map);
      long taskInstanceId = (Long)map.get(JPDLConstants.TASK_INSTANCE_ID);
      log.debug("taskInstanceId " + taskInstanceId);
      JbpmTaskInstance taskInstance = (JbpmTaskInstance)workflowManager.getTaskInstance(taskInstanceId);
      assertEquals(taskInstance.getDocumentId(), "doc1");
      setComplete();
  }

  public void testManualAnnotationTaskCompletedAnnotator3Doc1() throws Exception {
	  log.debug("#######################################");
	  log.debug("#  testManualAnnotationTaskCompletedAnnotator3Doc1()  #");
	  log.debug("#######################################");
	  ProcessInstance processInstance = loadProcessInstance();
      // check if the callback is made (task is started)
	  Map map = workflowManager.getNextTaskAsMap(annotators[2]);
      long taskInstanceId = (Long)map.get(JPDLConstants.TASK_INSTANCE_ID);
	  workflowManager.completeTask(taskInstanceId, null, null);
	  TaskInstance taskInstance = workflowManager.getTaskInstance(taskInstanceId);
	  assertNotNull(taskInstance.getEnd());
      setComplete();
  }


  public void testManualAnnotationNotGettingTaskForAnnotator3() throws Exception {
	  log.debug("#######################################");
	  log.debug("#  testManualAnnotationNotGettingTaskForAnnotator3()  #");
	  log.debug("#######################################");
	  ProcessInstance processInstance = loadProcessInstance();
      List<TaskInstance> taskInstances = workflowManager.findPendingPooledAnnotationTaskInstances(annotators[0]);
      assertEquals(0, taskInstances.size());
      boolean found = workflowManager.checkForNextTask(annotators[2]);
      assertEquals(found, false);
      setComplete();
  }


  public void testManualAnnotationProcessEnd() throws Exception {
	  log.debug("#######################################");
	  log.debug("#  testManualAnnotationProcessEnd()  #");
	  log.debug("#######################################");
	  ProcessInstance processInstance = loadProcessInstance();
	  assertNotNull(processInstance.getEnd());
  }


}




