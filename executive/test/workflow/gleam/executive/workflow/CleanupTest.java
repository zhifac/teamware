/*
 *  CleanupTest.java
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

import java.util.Iterator;
import java.util.List;

import gleam.executive.model.Project;
import gleam.executive.service.ProjectManager;
import gleam.executive.workflow.manager.WorkflowManager;

import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;

public class CleanupTest extends BaseWorkflowServiceTestCase {

	 private static WorkflowManager workflowManager = null;
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


}
