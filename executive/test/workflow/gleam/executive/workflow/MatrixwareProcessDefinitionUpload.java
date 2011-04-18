package gleam.executive.workflow;

import gleam.executive.Constants;
import gleam.executive.model.WebAppBean;
import gleam.executive.workflow.manager.WorkflowManager;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.jbpm.taskmgmt.exe.TaskMgmtInstance;
import gleam.executive.workflow.util.JPDLConstants;
import gleam.executive.workflow.util.WorkflowUtil;


public class MatrixwareProcessDefinitionUpload extends BaseWorkflowServiceTestCase {

	 private static WorkflowManager workflowManager = null;
	 private static ProcessDefinition processDefinition = null;
	 private static ProcessDefinition setupProcessDefinition = null;
	 private static ProcessDefinition automaticAnnotationProcessDefinition = null;
	 private static ProcessDefinition manualAnnotationProcessDefinition = null;
	 private static ProcessDefinition reviewProcessDefinition = null;
	 private static long processInstanceId;
	 /*
	 public void testCorpusProviderProcessDefinition() throws Exception {
		  log.debug("#######################################");
		  log.debug("#stestCorpusProviderProcessDefinition()#");
		  log.debug("#######################################");
		  String processesDirLocation = processesDir + "/";
		  String processArchiveLocation = processesDirLocation + WorkflowTestData.SUB_CORPUS_PROVIDER_ARCHIVE;
		  log.debug("processesArchiveLocation "+ processArchiveLocation);
		  InputStream inputStream = new FileInputStream(processArchiveLocation);
		  ZipInputStream zipInputStream = new ZipInputStream(inputStream);
		  corpusProviderProcessDefinition = ProcessDefinition.parseParZipInputStream(zipInputStream);
		  log.debug("corpusProviderProcessDefinition name " + corpusProviderProcessDefinition.getName());
		  if(zipInputStream!=null) zipInputStream.close();
		  if(inputStream!=null) inputStream.close();
		  workflowManager = (WorkflowManager) applicationContext.getBean("workflowManager");
		  workflowManager.deployProcessDefinition(corpusProviderProcessDefinition);
		  log.debug(" deployed specifyCorpusProcessDefinition ID " + corpusProviderProcessDefinition.getId());
		  setComplete();
	  }
	  */

	 public void testSetupProcessDefinition() throws Exception {
		  log.debug("#######################################");
		  log.debug("#testSetupProcessDefinition()#");
		  log.debug("#######################################");
		  String processesDirLocation = processesDir + "/";
		  String processArchiveLocation = processesDirLocation + WorkflowTestData.SUB_PROJECT_SETUP_ARCHIVE;
		  log.debug("processesArchiveLocation "+ processArchiveLocation);
		  InputStream inputStream = new FileInputStream(processArchiveLocation);
		  ZipInputStream zipInputStream = new ZipInputStream(inputStream);
		  setupProcessDefinition = ProcessDefinition.parseParZipInputStream(zipInputStream);
		  log.debug("setupProcessDefinition name " + setupProcessDefinition.getName());
		  if(zipInputStream!=null) zipInputStream.close();
		  if(inputStream!=null) inputStream.close();
		  workflowManager = (WorkflowManager) applicationContext.getBean("workflowManager");
		  workflowManager.deployProcessDefinition(setupProcessDefinition);
		  log.debug(" deployed setupProcessDefinition ID " + setupProcessDefinition.getId());
		  setComplete();
	  }

	 public void testAutomaticAnnotationProcessDefinition() throws Exception {
		  log.debug("#######################################");
		  log.debug("#testAutomaticAnnotationProcessDefinition()#");
		  log.debug("#######################################");
		  String processesDirLocation = processesDir + "/";
		  String processArchiveLocation = processesDirLocation + WorkflowTestData.SUB_AUTOMATIC_ANNOTATION_ARCHIVE;
		  log.debug("processesArchiveLocation "+ processArchiveLocation);
		  InputStream inputStream = new FileInputStream(processArchiveLocation);
		  ZipInputStream zipInputStream = new ZipInputStream(inputStream);
		  automaticAnnotationProcessDefinition = ProcessDefinition.parseParZipInputStream(zipInputStream);
		  log.debug("automaticAnnotationProcessDefinition name " + automaticAnnotationProcessDefinition.getName());
		  if(zipInputStream!=null) zipInputStream.close();
		  if(inputStream!=null) inputStream.close();
		  workflowManager = (WorkflowManager) applicationContext.getBean("workflowManager");
		  workflowManager.deployProcessDefinition(automaticAnnotationProcessDefinition);
		  log.debug(" deployed automaticAnnotationProcessDefinition ID " + automaticAnnotationProcessDefinition.getId());
		  setComplete();
	  }



	 public void testManualAnnotationProcessDefinition() throws Exception {
		  log.debug("#######################################");
		  log.debug("#testManualAnnotationProcessDefinition()#");
		  log.debug("#######################################");
		  String processesDirLocation = processesDir + "/";
		  String processArchiveLocation = processesDirLocation + WorkflowTestData.SUB_MANUAL_ANNOTATION_ARCHIVE;
		  log.debug("processesArchiveLocation "+ processArchiveLocation);
		  InputStream inputStream = new FileInputStream(processArchiveLocation);
		  ZipInputStream zipInputStream = new ZipInputStream(inputStream);
		  manualAnnotationProcessDefinition = ProcessDefinition.parseParZipInputStream(zipInputStream);
		  log.debug("manualAnnotationProcessDefinition name " + manualAnnotationProcessDefinition.getName());
		  if(zipInputStream!=null) zipInputStream.close();
		  if(inputStream!=null) inputStream.close();
		  workflowManager = (WorkflowManager) applicationContext.getBean("workflowManager");
		  workflowManager.deployProcessDefinition(manualAnnotationProcessDefinition);
		  log.debug(" deployed manualAnnotationProcessDefinition ID " + manualAnnotationProcessDefinition.getId());
		  setComplete();
	  }
	 
	 public void testReviewProcessDefinition() throws Exception {
		  log.debug("#######################################");
		  log.debug("#testReviewProcessDefinition()#");
		  log.debug("#######################################");
		  String processesDirLocation = processesDir + "/";
		  String processArchiveLocation = processesDirLocation + WorkflowTestData.SUB_REVIEW_ARCHIVE;
		  log.debug("processesArchiveLocation "+ processArchiveLocation);
		  InputStream inputStream = new FileInputStream(processArchiveLocation);
		  ZipInputStream zipInputStream = new ZipInputStream(inputStream);
		  reviewProcessDefinition = ProcessDefinition.parseParZipInputStream(zipInputStream);
		  log.debug("reviewProcessDefinition name " + reviewProcessDefinition.getName());
		  if(zipInputStream!=null) zipInputStream.close();
		  if(inputStream!=null) inputStream.close();
		  workflowManager = (WorkflowManager) applicationContext.getBean("workflowManager");
		  workflowManager.deployProcessDefinition(reviewProcessDefinition);
		  log.debug(" deployed reviewProcessDefinition ID " + reviewProcessDefinition.getId());
		  setComplete();
	  }


  public void testMainProcessDefinition() throws Exception {
	  log.debug("#######################################");
	  log.debug("#  testMainProcessDefinition()  #");
	  log.debug("#######################################");
	  String processesDirLocation = processesDir + "/";
	  String processArchiveLocation = processesDirLocation + WorkflowTestData.PROCESS_ARCHIVE;
	  log.debug("processesArchiveLocation "+ processArchiveLocation);
	  InputStream inputStream = new FileInputStream(processArchiveLocation);
	  ZipInputStream zipInputStream = new ZipInputStream(inputStream);
	  processDefinition = ProcessDefinition.parseParZipInputStream(zipInputStream);
	  log.debug("processDefinition " + processDefinition.getName());
	  if(zipInputStream!=null) zipInputStream.close();
	  if(inputStream!=null) inputStream.close();
	  workflowManager = (WorkflowManager) applicationContext.getBean("workflowManager");
	  processDefinition.setName(Constants.MAIN_PROCESS_DEFINITION_NAME);
	  workflowManager.deployProcessDefinition(processDefinition);
	  log.debug("deployed processDefinition ID " + processDefinition.getId());
	  log.debug("deployed processDefinition NAME " + processDefinition.getName());
	  setComplete();
  }
/*
  public void testMatrixwareProcessInstanceStart() throws Exception {
	  log.debug("#######################################");
	  log.debug("#  testMatrixwareProcessInstanceStart()#");
	  log.debug("#######################################");
	  log.debug("processDefinition ID: "+processDefinition.getId());
	  log.debug("corpusProvider ID: "+corpusProviderProcessDefinition.getId());
	  log.debug("setupProcessDefinition ID: "+setupProcessDefinition.getId());
	  log.debug("automaticAnnotationProcessDefinition ID: "+automaticAnnotationProcessDefinition.getId());
	  log.debug("manualAnnotationProcessDefinition ID: "+manualAnnotationProcessDefinition.getId());
	  Map<String, Object> variableMap = new HashMap<String, Object>();
	  variableMap.put(JPDLConstants.INITIATOR, WorkflowTestData.INITIATOR);
	  //variableMap.put(JPDLConstants.MODE, JPDLConstants.TEST_MODE);
	  variableMap.put(JPDLConstants.DO_SETUP, "on");
	  ProcessInstance processInstance = workflowManager.createStartProcessInstance(processDefinition.getId(), variableMap, null);
      processInstanceId = processInstance.getId();
      log.debug("processInstanceId: "+ processInstanceId);
      assertNotNull("Instance ID should not be null", processInstanceId);
      int numOfProcessInstances = workflowManager.findAllProcessInstances(processDefinition.getId()).size();
      assertEquals(1, numOfProcessInstances);
      int numOfActiveProcessInstances = workflowManager.findUncompletedProcessInstancesForProcessDefinition(processDefinition.getId());
      assertEquals(1, numOfActiveProcessInstances);
      setComplete();
  }
  
  

  public void testProjectSetupSelectAnnotationModules() throws Exception{
	  log.debug("#######################################");
	  log.debug("testProjectSetupSelectAnnotationModules()");
	  log.debug("#######################################");

	  ProcessInstance processInstance = loadProcessInstance();

	  assertEquals("Instance is in 'project setup' process state", processInstance.getRootToken().getNode().getName(), WorkflowTestData.SUB_PROCESS_SETUP_NAME);
	  ProcessInstance subProcessInstance = loadSubProcessInstance(setupProcessDefinition.getId());
	  //assertEquals(JPDLConstants.TEST_MODE, subProcessInstance.getContextInstance().getVariable(JPDLConstants.MODE) );
	  //
	  assertEquals("on", processInstance.getContextInstance().getVariable(JPDLConstants.DO_SETUP) );
	  assertEquals("agaton", processInstance.getContextInstance().getVariable(JPDLConstants.INITIATOR) );
	  TaskMgmtInstance taskMgmtInstance = subProcessInstance.getTaskMgmtInstance();
	  Collection<TaskInstance> unfinishedTasks = taskMgmtInstance.getUnfinishedTasks(subProcessInstance.getRootToken());
	  assertNotNull(unfinishedTasks);
	  assertEquals(1, unfinishedTasks.size());
	  List<TaskInstance> unfinishedTaskList = new ArrayList<TaskInstance>(unfinishedTasks);
	  TaskInstance taskInstance = (TaskInstance) unfinishedTaskList.get(0);
	  assertNotNull(taskInstance);
	  // check assignment
	  assertEquals(WorkflowTestData.INITIATOR, taskInstance.getActorId());
	  Map<String, Object> variableMap = new HashMap<String, Object>();
	  variableMap.put(JPDLConstants.DO_AUTOMATIC, WorkflowTestData.DO_AUTOMATIC);
	  variableMap.put(JPDLConstants.DO_MANUAL, WorkflowTestData.DO_MANUAL);
	  workflowManager.completeTask(taskInstance.getId(), variableMap, null);
      setComplete();
  }

  public void testProcessSetupGASConfiguration1() throws Exception{
	  log.debug("#######################################");
	  log.debug("testProcessSetupGASConfiguration1()");
	  log.debug("#######################################");

	  ProcessInstance processInstance = loadProcessInstance();

	  assertEquals("Instance is in 'project setup' process state", processInstance.getRootToken().getNode().getName(), WorkflowTestData.SUB_PROCESS_SETUP_NAME);
	  ProcessInstance subProcessInstance = loadSubProcessInstance(setupProcessDefinition.getId());
	 
	  TaskMgmtInstance taskMgmtInstance = subProcessInstance.getTaskMgmtInstance();
	  Collection<TaskInstance> unfinishedTasks = taskMgmtInstance.getUnfinishedTasks(subProcessInstance.getRootToken());
	  assertNotNull(unfinishedTasks);
	  assertEquals(1, unfinishedTasks.size());
	  List<TaskInstance> unfinishedTaskList = new ArrayList<TaskInstance>(unfinishedTasks);
	  TaskInstance taskInstance = (TaskInstance) unfinishedTaskList.get(0);
	  assertNotNull(taskInstance);
	  log.debug("task instance ID: "+taskInstance.getId());
	  // check assignment
	  assertEquals(WorkflowTestData.INITIATOR, taskInstance.getActorId());
	  Map<String, Object> variableMap = new HashMap<String, Object>();
	  //variableMap.put(JPDLConstants.SERVICE_NAME, WorkflowTestData.GAS_1_NAME);
	  //variableMap.put(JPDLConstants.ENDPOINT_URL, WorkflowTestData.GAS_1_URL);
	  variableMap.put("Service Name", WorkflowTestData.GAS_1_NAME);
	  variableMap.put("Service URL", WorkflowTestData.GAS_1_URL);
	  workflowManager.completeTask(taskInstance.getId(), variableMap, WorkflowTestData.TRANSITION_SPECIFY_NEXT_GAS);
      setComplete();
  }
  
  public void testProcessSetupGASConfiguration2() throws Exception{
	  log.debug("#######################################");
	  log.debug("testProcessSetupGASConfiguration2()");
	  log.debug("#######################################");

	  ProcessInstance processInstance = loadProcessInstance();

	  assertEquals("Instance is in 'project setup' process state", processInstance.getRootToken().getNode().getName(), WorkflowTestData.SUB_PROCESS_SETUP_NAME);
	  ProcessInstance subProcessInstance = loadSubProcessInstance(setupProcessDefinition.getId());

	  TaskMgmtInstance taskMgmtInstance = subProcessInstance.getTaskMgmtInstance();
	  Collection<TaskInstance> unfinishedTasks = taskMgmtInstance.getUnfinishedTasks(subProcessInstance.getRootToken());
	  assertNotNull(unfinishedTasks);
	  assertEquals(1, unfinishedTasks.size());
	  List<TaskInstance> unfinishedTaskList = new ArrayList<TaskInstance>(unfinishedTasks);
	  TaskInstance taskInstance = (TaskInstance) unfinishedTaskList.get(0);
	  assertNotNull(taskInstance);
	  log.debug("task instance ID: "+taskInstance.getId());
	  // check assignment
	  assertEquals(WorkflowTestData.INITIATOR, taskInstance.getActorId());
	  Map<String, Object> variableMap = new HashMap<String, Object>();
	  //variableMap.put(JPDLConstants.SERVICE_NAME, WorkflowTestData.GAS_2_NAME);
	  //variableMap.put(JPDLConstants.ENDPOINT_URL, WorkflowTestData.GAS_2_URL);
	  variableMap.put("Service Name", WorkflowTestData.GAS_2_NAME);
	  variableMap.put("Service URL", WorkflowTestData.GAS_2_URL);
	  workflowManager.completeTask(taskInstance.getId(), variableMap, WorkflowTestData.TRANSITION_FINISH);
      setComplete();
  }
  
  public void testProcessSetupAnnotationRules() throws Exception{
	  log.debug("#######################################");
	  log.debug("testProcessSetupAnnotationRules()");
	  log.debug("#######################################");

	  ProcessInstance processInstance = loadProcessInstance();

	  assertEquals("Instance is in 'project setup' process state", processInstance.getRootToken().getNode().getName(), WorkflowTestData.SUB_PROCESS_SETUP_NAME);
	  ProcessInstance subProcessInstance = loadSubProcessInstance(setupProcessDefinition.getId());

	  TaskMgmtInstance taskMgmtInstance = subProcessInstance.getTaskMgmtInstance();
	  Collection<TaskInstance> unfinishedTasks = taskMgmtInstance.getUnfinishedTasks(subProcessInstance.getRootToken());
	  assertNotNull(unfinishedTasks);
	  assertEquals(1, unfinishedTasks.size());
	  List<TaskInstance> unfinishedTaskList = new ArrayList<TaskInstance>(unfinishedTasks);
	  TaskInstance taskInstance = (TaskInstance) unfinishedTaskList.get(0);
	  assertNotNull(taskInstance);
	  // check assignment
	  assertEquals(WorkflowTestData.INITIATOR, taskInstance.getActorId());
	  Map<String, Object> variableMap = new HashMap<String, Object>();
	  variableMap.put(JPDLConstants.CURATOR_CSV_LIST, WorkflowTestData.CURATOR_CSV_LIST);
	  variableMap.put(JPDLConstants.ANNOTATOR_CSV_LIST, WorkflowTestData.ANNOTATOR_CSV_LIST);
	  variableMap.put(JPDLConstants.ANNOTATORS_PER_DOCUMENT, WorkflowTestData.ANNOTATORS_PER_DOCUMENT);
	 	
	  variableMap.put(JPDLConstants.CAN_CANCEL, WorkflowTestData.CAN_CANCEL);
	  variableMap.put(JPDLConstants.ANONYMOUS_ANNOTATION, WorkflowTestData.ANONYMOUS_ANNOTATION);
	  variableMap.put(JPDLConstants.ANNOTATION_SCHEMA_CSV_URLS, WorkflowTestData.ANNOTATION_SCHEMA_CSV_URLS);
	 	
	  workflowManager.completeTask(taskInstance.getId(), variableMap, null);
      setComplete();
  }
  

	 
  public void testCorpusProviderSelectCorpus() throws Exception{
	  log.debug("#######################################");
	  log.debug("#  testCorpusProviderSelectCorpus()   #");
	  log.debug("#######################################");
	  ProcessInstance processInstance = loadProcessInstance();
	  assertEquals("Instance is in 'corpus provider' process state", processInstance.getRootToken().getNode().getName(), WorkflowTestData.SUB_CORPUS_PROVIDER_NAME);

	  ProcessInstance subProcessInstance = loadSubProcessInstance(corpusProviderProcessDefinition.getId());
	  // check MODE variable
	  //subProcessInstance.getContextInstance().createVariable(JPDLConstants.MODE, JPDLConstants.TEST_MODE);
	  TaskMgmtInstance taskMgmtInstance = subProcessInstance.getTaskMgmtInstance();
	  log.debug("subprocess in node: " + subProcessInstance.getRootToken().getNode().getName());
	  TaskInstance taskInstance = (TaskInstance) taskMgmtInstance.getTaskInstances().iterator().next();
	  // check assignment
	  assertEquals(WorkflowTestData.INITIATOR, taskInstance.getActorId());
	  Map<String, Object> variableMap = new HashMap<String, Object>();
	  variableMap.put(JPDLConstants.CORPUS_ID, WorkflowTestData.CORPUS_ID);
      try{
	  workflowManager.completeTask(taskInstance.getId(), variableMap, null);
      }
      catch(Exception e){
    	  e.printStackTrace();
      }
	  setComplete();
  }

*/
  /*

  public void testProjectSettingsOverview() throws Exception{
	  log.debug("#######################################");
	  log.debug("#  testProjectSettingsOverview()      #");
	  log.debug("#######################################");
	  ProcessInstance processInstance = loadProcessInstance();
	  assertEquals("Instance is in 'project settings overview' task node", processInstance.getRootToken().getNode().getName(), WorkflowTestData.PROJECT_SETTINGS_OVERVIEW_TASK_NODE);

	  assertEquals(JPDLConstants.TEST_MODE, processInstance.getContextInstance().getVariable(JPDLConstants.MODE) );
	  assertEquals(WorkflowTestData.ON, processInstance.getContextInstance().getVariable(JPDLConstants.DO_AUTOMATIC) );
	  assertEquals(WorkflowTestData.ON, processInstance.getContextInstance().getVariable(JPDLConstants.DO_MANUAL) );
	  assertEquals(WorkflowTestData.DOCUMENT_CSV_LIST, processInstance.getContextInstance().getVariable(JPDLConstants.DOCUMENT_CSV_LIST) );

	  TaskMgmtInstance taskMgmtInstance = processInstance.getTaskMgmtInstance();
	  TaskInstance taskInstance = (TaskInstance) taskMgmtInstance.getTaskInstances().iterator().next();
	  assertEquals(WorkflowTestData.INITIATOR, taskInstance.getActorId());
	  try{
	  workflowManager.completeTask(taskInstance.getId(), null, WorkflowTestData.TRANSITION_RUN);
      }
      catch(Exception e){
    	  e.printStackTrace();
      }
	  setComplete();

  }


  public void testAutomaticAnnotationCreatingTasks() throws Exception {
	  log.debug("#######################################");
	  log.debug("#testAutomaticAnnotationCreatingTasks()#");
	  log.debug("#######################################");
	  ProcessInstance processInstance = loadProcessInstance();
	  String doAutomatic = (String)processInstance.getContextInstance().getVariable(JPDLConstants.DO_AUTOMATIC);
	  log.debug("@@@@@@@@@@@@@@@@@@@@ doAutomatic: "+doAutomatic);
	  if(doAutomatic.equals(WorkflowTestData.ON)){
	  assertEquals("Instance is in 'automatic annotation' process state", processInstance.getRootToken().getNode().getName(), WorkflowTestData.SUB_AUTOMATIC_ANNOTATION_NAME);
	  assertEquals(WorkflowTestData.INITIATOR, (String)processInstance.getContextInstance().getVariable(JPDLConstants.INITIATOR));
	  ProcessInstance subProcessInstance = loadSubProcessInstance(automaticAnnotationProcessDefinition.getId());
	  List<TaskInstance> taskInstances = workflowManager.findTaskInstancesForProcessInstance(subProcessInstance.getId());
      assertEquals(2, taskInstances.size());

      setComplete();
	  }
      else {
		 log.debug("AUTOMATIC ANNOTATION IS SWITCHED OFF. DO NOT DO ANYTHING!");
	  }

  }

  public void testAutomaticAnnotationCallbacks() throws Exception {
	  log.debug("#######################################");
	  log.debug("#  testAutomaticAnnotationCallbacks() #");
	  log.debug("#######################################");
	  ProcessInstance processInstance = loadProcessInstance();
	  String doAutomatic = (String)processInstance.getContextInstance().getVariable(JPDLConstants.DO_AUTOMATIC);
	  if(doAutomatic.equals(WorkflowTestData.ON)){
	  ProcessInstance subProcessInstance = loadSubProcessInstance(automaticAnnotationProcessDefinition.getId());
	  log.debug("Start waiting for callback ");
      Thread.sleep(10000);
      setComplete();
	  } else {
 		 log.debug("AUTOMATIC ANNOTATION IS SWITCHED OFF. DO NOT DO ANYTHING!");
 	  }
  }

  public void testAutomaticAnnotationProcessEnd() throws Exception {
	  log.debug("#######################################");
	  log.debug("#  testAutomaticAnnotationProcessEnd()#");
	  log.debug("#######################################");
	  ProcessInstance processInstance = loadProcessInstance();
	  String doAutomatic = (String)processInstance.getContextInstance().getVariable(JPDLConstants.DO_AUTOMATIC);

	  if(doAutomatic.equals(WorkflowTestData.ON)){
	  ProcessInstance subProcessInstance = loadSubProcessInstance(automaticAnnotationProcessDefinition.getId());
	  assertNotNull(subProcessInstance.getEnd());
	  setComplete();
	  } else {
			 log.debug("AUTOMATIC ANNOTATION IS SWITCHED OFF. DO NOT DO ANYTHING!");
		  }
  }


  public void testManualAnnotationCreatingTasks() throws Exception {
	  log.debug("#######################################");
	  log.debug("#testManualAnnotationCreatingTasks()#");
	  log.debug("#######################################");
	  ProcessInstance processInstance = loadProcessInstance();
	  String doManual = (String)processInstance.getContextInstance().getVariable(JPDLConstants.DO_MANUAL);
	  log.debug("@@@@@@@@@@@@@@@@@@@@ doManual: "+doManual);
	  if(doManual.equals(WorkflowTestData.ON)){
	  assertEquals("Instance is in 'manual annotation' process state", processInstance.getRootToken().getNode().getName(), WorkflowTestData.SUB_MANUAL_ANNOTATION_NAME);
	  assertEquals(WorkflowTestData.INITIATOR, (String)processInstance.getContextInstance().getVariable(JPDLConstants.INITIATOR));
	  assertEquals(WorkflowTestData.CURATOR_CSV_LIST, (String)processInstance.getContextInstance().getVariable(JPDLConstants.CURATOR_CSV_LIST));
	  assertEquals(WorkflowTestData.ANNOTATOR_CSV_LIST, (String)processInstance.getContextInstance().getVariable(JPDLConstants.ANNOTATOR_CSV_LIST));
	  assertEquals(WorkflowTestData.ANNOTATION_SCHEMA_CSV_URLS, (String)processInstance.getContextInstance().getVariable(JPDLConstants.ANNOTATION_SCHEMA_CSV_URLS));
	  assertEquals(WorkflowTestData.ANNOTATORS_PER_DOCUMENT, (String)processInstance.getContextInstance().getVariable(JPDLConstants.ANNOTATORS_PER_DOCUMENT));
	  assertEquals(WorkflowTestData.ANNOTATOR_HAS_TO_BE_UNIQUE_FOR_DOCUMENT, (String)processInstance.getContextInstance().getVariable(JPDLConstants.ANNOTATOR_HAS_TO_BE_UNIQUE_FOR_DOCUMENT));
	  assertEquals(WorkflowTestData.ANONYMOUS_ANNOTATION, (String)processInstance.getContextInstance().getVariable(JPDLConstants.ANONYMOUS_ANNOTATION));
	  assertEquals(WorkflowTestData.CAN_CANCEL, (String)processInstance.getContextInstance().getVariable(JPDLConstants.CAN_CANCEL));



	  ProcessInstance subProcessInstance = loadSubProcessInstance(manualAnnotationProcessDefinition.getId());
	  List<TaskInstance> taskInstances = workflowManager.findTaskInstancesForProcessInstance(subProcessInstance.getId());
      assertEquals(4, taskInstances.size());
      // just end the tasks, various scenarios including assignment strategy will be
      // tested in the separate test case
      Iterator<TaskInstance> it = taskInstances.iterator();
      while(it.hasNext()){
    	  TaskInstance taskInstance = it.next();
    	  taskInstance.end();
    	  log.debug("ended task instance: '"+taskInstance.getId() + "' by actor: '"+taskInstance.getActorId()+"'");
      }
      setComplete();
	  }
      else {
		 log.debug("MANUAL ANNOTATION IS SWITCHED OFF. DO NOT DO ANYTHING!");
	  }

  }

  public void testManualAnnotationProcessEnd() throws Exception {
	  log.debug("#######################################");
	  log.debug("#  testManualAnnotationProcessEnd()#");
	  log.debug("#######################################");
	  ProcessInstance processInstance = loadProcessInstance();
	  String doManual = (String)processInstance.getContextInstance().getVariable(JPDLConstants.DO_MANUAL);

	  if(doManual.equals(WorkflowTestData.ON)){
	  ProcessInstance subProcessInstance = loadSubProcessInstance(manualAnnotationProcessDefinition.getId());
	  assertNotNull(subProcessInstance.getEnd());
	  setComplete();
	  } else {
			 log.debug("MANUAL ANNOTATION IS SWITCHED OFF. DO NOT DO ANYTHING!");
		  }
  }

  public void testMainProcessEnd() throws Exception {
	  log.debug("#######################################");
	  log.debug("#  testMainProcessEnd()#");
	  log.debug("#######################################");
	  ProcessInstance processInstance = loadProcessInstance();
	  assertNotNull(processInstance.getEnd());
	  setComplete();

  }


  
*/
  
  public ProcessInstance loadProcessInstance()throws Exception {
	  return workflowManager.getProcessInstanceForUpdate(processInstanceId);
  }

  public ProcessInstance loadSubProcessInstance(long subProcessDefinitionId)throws Exception {
	  List<ProcessInstance> subProcessInstances = workflowManager.findAllProcessInstances(subProcessDefinitionId);
	  if(subProcessInstances==null || subProcessInstances.size()==0){
		  return null;
	  }
	  else {
		  return (ProcessInstance)subProcessInstances.iterator().next();
	  }
  }
}




