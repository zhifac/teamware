/*
 *  JbpmGraphSession.java
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
package gleam.executive.workflow.core;

import org.jbpm.db.GraphSession;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;

import java.util.Iterator;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.jbpm.JbpmException;
import org.jbpm.taskmgmt.def.Swimlane;
import org.jbpm.taskmgmt.def.Task;

/**
 * are the graph related database operations.
 */
public class JbpmGraphSession extends GraphSession {


  Session session = null;


  public JbpmGraphSession(Session session) {
        super(session);
        this.session = session;
  }


 /**
   * NEW METHOD
   * saves or updates Task
   * @author agaton
   */
  public void saveOrUpdateTask(Task task) {
    try {
      session.saveOrUpdate(task);
    } catch (Exception e) {
      e.printStackTrace(); log.error(e);
      //jbpmSession.handleException();
      throw new JbpmException("couldn't save task '" + task + "'", e);
    }
  }

  /**
   * NEW METHOD
   * saves or updates Swimlane
   * @author agaton
   */
  public void saveOrUpdateSwimlane(Swimlane swimlane) {
    try {
      session.saveOrUpdate(swimlane);
    } catch (Exception e) {
      e.printStackTrace(); log.error(e);
      //jbpmSession.handleException();
      throw new JbpmException("couldn't save swimlane '" + swimlane + "'", e);
    }
  }

  

 /**
   * NEW METHOD
   * fetches all available processInstances
   * The returned list of process instances is sorted start date, latest 1st
   * @author agaton
   */

  public List findAllProcessInstances() {
    List processInstances = null;
    try {
      Query query = session.getNamedQuery("GraphSession.findAllProcessInstances");
      processInstances = query.list();
    } catch (Exception e) {
      e.printStackTrace(); log.error(e);
      //jbpmSession.handleException();
      throw new JbpmException("couldn't load all process instances", e);
    }
    return processInstances;
  }

  /**
   * NEW METHOD
   * fetches all available processInstances
   * The returned list of process instances is sorted start date, latest 1st
   * @author agaton
   */

  public List findAllProcessInstancesExcludingSubProcessInstances() {
    List processInstances = null;
    try {
      Query query = session.getNamedQuery("GraphSession.findAllProcessInstancesExcludingSubProcessInstances");
      processInstances = query.list();
    } catch (Exception e) {
      e.printStackTrace(); log.error(e);
      //jbpmSession.handleException();
      throw new JbpmException("couldn't load all process instances", e);
    }
    return processInstances;
  }
  
  
  /**
   * fetches all available processInstances excluding subprocesses with the same key and name
   * The returned list of process instances is sorted start date, latest 1st
   * @author agaton
   */

  public List findProcessInstancesExcludingSubProcessInstancesByKeyAndName(String key, String name) {
    List processInstances = null;
    try {
      Query query = session.getNamedQuery("GraphSession.findProcessInstancesExcludingSubProcessInstancesByKeyAndName");
      query.setString("key", key);     
      query.setString("name", name);  
      processInstances = query.list();
    } catch (Exception e) {
      e.printStackTrace(); log.error(e);
      //jbpmSession.handleException();
      throw new JbpmException("couldn't load all process instances", e);
    }
    return processInstances;
  }
  
  /**
   * fetches all available processInstances excluding subprocesses with the same key
   * The returned list of process instances is sorted start date, latest 1st
   * @author agaton
   */

  public List findProcessInstancesExcludingSubProcessInstancesByKey(String key) {
    List processInstances = null;
    try {
      Query query = session.getNamedQuery("GraphSession.findProcessInstancesExcludingSubProcessInstancesByKey");
      query.setString("key", key);     
      processInstances = query.list();
    } catch (Exception e) {
      e.printStackTrace(); log.error(e);
      //jbpmSession.handleException();
      throw new JbpmException("couldn't load all process instances", e);
    }
    return processInstances;
  }
  
  
  /**
   * NEW METHOD
   * fetches all available processInstances for spec processdefinition
   * The returned list of process instances is sorted start date, latest 1st
   * @author agaton
   */

  public List findAllProcessInstancesByProcessDefinitionIdExcludingSubProcessInstances(long processDefinitionId) {
    List processInstances = null;
    try {
      Query query = session.getNamedQuery("GraphSession.findAllProcessInstancesByProcessDefinitionIdExcludingSubProcessInstances");
      query.setLong("processDefinitionId", processDefinitionId);
      processInstances = query.list();
    } catch (Exception e) {
      e.printStackTrace(); log.error(e);
      //jbpmSession.handleException();
      throw new JbpmException("couldn't load all process instances", e);
    }
    return processInstances;
  }
  
  /**
   * NEW METHOD
   * fetches all available processInstances for all process definitions with the same name
   * The returned list of process instances is sorted start date, latest 1st
   * @author agaton
   */

  public List findAllProcessInstancesByProcessDefinitionNameExcludingSubProcessInstances(String processDefinitionName) {
    List processInstances = null;
    try {
      Query query = session.getNamedQuery("GraphSession.findAllProcessInstancesByProcessDefinitionNameExcludingSubProcessInstances");
      query.setString("processDefinitionName", processDefinitionName);
      processInstances = query.list();
    } catch (Exception e) {
      e.printStackTrace(); log.error(e);
      //jbpmSession.handleException();
      throw new JbpmException("couldn't load all process instances", e);
    }
    return processInstances;
  }
  
  
  public List findAllSubProcessInstances(ProcessInstance processInstance) {
	    List processInstances = null;
	    try {
	      Query query = session.getNamedQuery("GraphSession.findSubProcessInstances");
	      query.setEntity("processInstance", processInstance);
	      processInstances = query.list();
	    } catch (Exception e) {
	      e.printStackTrace(); log.error(e);
	      //jbpmSession.handleException();
	      throw new JbpmException("couldn't find subprocess instances", e);
	    }
	    return processInstances;
	  }
  


  public int findUncompletedProcessInstancesForProcessDefinition(long processDefinitionId)
  {
      int numberOfLiveInstances = 0;
      List processInstances = null;
      try
      {
          Query query = session.getNamedQuery("GraphSession.findAllRunningProcessInstancesForProcessDefinition");
          query.setLong("processDefinitionId", processDefinitionId);
          processInstances = query.list();
          if(processInstances != null)
              numberOfLiveInstances = processInstances.size();
      }
      catch(Exception e)
      {
          e.printStackTrace();
          log.error(e);
          //jbpmSession.handleException();
          throw new JbpmException("couldn't load process instances for process definition '" + processDefinitionId + "'", e);
      }
      return numberOfLiveInstances;
  }


  public void deleteProcessInstance(long processInstanceId) {
	    deleteProcessInstance(loadProcessInstance(processInstanceId));
	  }

	  public void deleteProcessInstance(ProcessInstance processInstance) {
	    deleteProcessInstance(processInstance, true, true);
	  }

	  public void deleteProcessInstance(ProcessInstance processInstance, boolean includeTasks, boolean includeJobs) {
		    if (processInstance==null) throw new JbpmException("processInstance is null in JbpmSession.deleteProcessInstance()");
		    try {
		      // find the tokens
		      Query query = session.getNamedQuery("GraphSession.findTokensForProcessInstance");
		      query.setEntity("processInstance", processInstance);
		      List tokens = query.list();
		      log.debug("deleting process instance: "+processInstance.getId());
		      // deleteSubProcesses
		      Iterator iter = tokens.iterator();
		      while (iter.hasNext()) {
		        Token token = (Token) iter.next();

		        deleteSubProcesses(token);
		      }

		      // jobs
		      if (includeJobs) {
		        query = session.getNamedQuery("GraphSession.deleteJobsForProcessInstance");
		        query.setEntity("processInstance", processInstance);
		        query.executeUpdate();
		      }

		      // tasks
		      if (includeTasks) {
		        query = session.getNamedQuery("GraphSession.findTaskInstanceIdsForProcessInstance");
		        query.setEntity("processInstance", processInstance);
		        List taskInstanceIds = query.list();

		        query = session.getNamedQuery("GraphSession.deleteTaskInstancesById");
		        query.setParameterList("taskInstanceIds", taskInstanceIds);
		      }

		      // delete the logs for all the process instance's tokens
		      query = session.getNamedQuery("GraphSession.selectLogsForTokens");
		      query.setParameterList("tokens", tokens);
		      List logs = query.list();
		      iter = logs.iterator();
		      while (iter.hasNext()) {
		        session.delete(iter.next());
		      }

		      // then delete the process instance
		      session.delete(processInstance);

		    } catch (Exception e) {
		      e.printStackTrace(); log.error(e);

		      throw new JbpmException("couldn't delete process instance '" + processInstance.getId() + "'", e);
		    }
		  }

		  public void deleteSubProcesses(Token token) {
		    Query query = session.getNamedQuery("GraphSession.findSubProcessInstances");
		    query.setEntity("processInstance", token.getProcessInstance());
		    List processInstances = query.list();

		    if (processInstances == null || processInstances.isEmpty()) {
		      return;
		    }

		    Iterator iter = processInstances.iterator();
		    while (iter.hasNext()) {
		      ProcessInstance subProcessInstance = (ProcessInstance) iter.next();
		      log.debug("deleting subprocess: "+subProcessInstance.getId() + " in token: "+token);

		      subProcessInstance.setSuperProcessToken(null);
		      token.setSubProcessInstance(null);
		      deleteProcessInstance(subProcessInstance);
		    }

		    if (token.getChildren()!=null) {
		      iter = token.getChildren().values().iterator();
		      while (iter.hasNext()) {
		        Token child = (Token) iter.next();
		        deleteSubProcesses(child);
		      }
		    }
		  }
  private static final Log log = LogFactory.getLog(GraphSession.class);
}

