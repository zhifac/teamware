package gleam.executive.workflow;

import gleam.executive.model.Project;
import gleam.executive.service.ProjectManager;
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
import gleam.executive.workflow.util.JPDLConstants;


public class PreprocessingAutomaticAnnotationTest extends BaseWorkflowServiceTestCase {

	 private static WorkflowManager workflowManager = null;

	 private static final String PROCESS_ARCHIVE = "automaticAnnotation.zip";

	 private static ProcessDefinition processDefinition = null;
	 private static long processInstanceId;

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
	 
  public void testAutomaticAnnotationProcessDefinition() throws Exception {
	  log.debug("#######################################");
	  log.debug("#  testAutomaticAnnotationProcessDefinition()            #");
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

  public void testAutomaticAnnotationProcessInstanceStart() throws Exception {
	  log.debug("#######################################");
	  log.debug("#  testAutomaticAnnotationProcessInstanceStart()         #");
	  log.debug("#######################################");
	  Map<String, Object> variableMap = new HashMap<String, Object>();
	  variableMap.put(JPDLConstants.INITIATOR, WorkflowTestData.INITIATOR);
	  variableMap.put(JPDLConstants.MODE, JPDLConstants.TEST_MODE);
	  variableMap.put(JPDLConstants.CORPUS_ID, "corpusX");
	  variableMap.put(JPDLConstants.GAS_PIPELINE_CSV_LIST, "2");
	  ProcessInstance processInstance = workflowManager.createStartProcessInstance(processDefinition.getId(), variableMap, null);
      processInstanceId = processInstance.getId();
      assertNotNull("Instance ID should not be null", processInstanceId);
      int numOfActiveProcessInstances = workflowManager.findUncompletedProcessInstancesForProcessDefinition(processDefinition.getId());
      assertEquals(1, numOfActiveProcessInstances);
      setComplete();
  }
  
  public void testCreateAnotherProcess() throws Exception {
	  log.debug("#######################################");
	  log.debug("#  testCreateAnotherProcess()         #");
	  log.debug("#######################################");
	  
	  boolean isAnyRunning= workflowManager.isAnyProcessInstanceRunningAgainstTheCorpus(processDefinition.getName(), "corpusX");
	  log.debug("isAnyRunning: "+ isAnyRunning);
	  assertEquals(true, isAnyRunning);
      setComplete();
  }




  public void testAutomaticAnnotationCreatingTasks() throws Exception {
	  log.debug("#######################################");
	  log.debug("#  testAutomaticAnnotationCreatingTasks()                #");
	  log.debug("#######################################");
	  ProcessInstance processInstance = loadProcessInstance();
      List<TaskInstance> taskInstances = workflowManager.findTaskInstancesForProcessInstance(processInstance.getId());
      assertEquals(2, taskInstances.size());
      setComplete();
  }


  public void testAutomaticAnnotationGASCallback1() throws Exception {
	  log.debug("#######################################");
	  log.debug("#  testAutomaticAnnotationGASCallback1()                #");
	  log.debug("#######################################");
	  ProcessInstance processInstance = loadProcessInstance();
	  log.debug("Start waiting for callback 1");
      Thread.sleep(5000);
      setComplete();
  }

  public void testAutomaticAnnotationGASCallback2() throws Exception {
	  log.debug("#######################################");
	  log.debug("#  testAutomaticAnnotationGASCallback2()                #");
	  log.debug("#######################################");
	  ProcessInstance processInstance = loadProcessInstance();
	  log.debug("Start waiting for callback 2");
      Thread.sleep(5000);
      setComplete();
  }

  

  
  public void testAutomaticAnnotationProcessEnd() throws Exception {
	  log.debug("#######################################");
	  log.debug("#  testProcessEnd()  #");
	  log.debug("#######################################");
	  ProcessInstance processInstance = loadProcessInstance();
	  log.debug("process instance id: "+processInstance.getId());
	  log.debug("process instance end: "+processInstance.getEnd());
	  assertNotNull(processInstance.getEnd());
	  setComplete();
  }


  public ProcessInstance loadProcessInstance()throws Exception {
	  return workflowManager.getProcessInstanceForUpdate(processInstanceId);
  }

/*
  public void testClean() throws Exception {
      // wait for the callback
	  ProcessDefinition def = workflowManager.findLatestProcessDefinition("document-centric-test");
      assertNotNull("Definition should not be null", def);

	  List<ProcessInstance> processInstances = workflowManager.findAllProcessInstances(def.getId());
      Iterator<ProcessInstance> it = processInstances.iterator();
      while(it.hasNext()){
    	  long processInstanceId = it.next().getId();
    	  workflowManager.cancelProcessInstance(processInstanceId);
    	  log.debug("deleted process instance " + processInstanceId);
      }

    	  workflowManager.undeployProcessDefinition(def);

      setComplete();
  }

*/

}




