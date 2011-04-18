package gleam.executive.workflow.core;


import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.jbpm.JbpmException;
import org.jbpm.db.TaskMgmtSession;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.taskmgmt.exe.TaskInstance;

public class JbpmTaskMgmtSession extends TaskMgmtSession implements Serializable {

  private static final long serialVersionUID = 1L;


  Session session = null;



  public JbpmTaskMgmtSession(Session session) {
   super(session);
   this.session = session;
  }



   /* added by agaton */
  
  /**
   * get the pending task list for a given actor.
   */
  public List findPendingTaskInstancesByActorId(String actorId) {
    List result = null;
    try {
      Query query = session.getNamedQuery("TaskMgmtSession.findPendingTaskInstancesByActorId");
      query.setString("actorId", actorId);
      result = query.list();
    } catch (Exception e) {
      log.error(e);
      //jbpmSession.handleException();
      throw new JbpmException("couldn't get task instances list for actor '"+actorId+"'", e);
    }
    return result;
  }
  /**
   * get task list for a given process instances.
   */
  public List findTaskInstancesForProcessInstance(ProcessInstance processInstance) {
    List result = null;
    try {
	  Query query = session.getNamedQuery("GraphSession.findTaskInstanceIdsForProcessInstance");
	  query.setEntity("processInstance", processInstance);
	  result = query.list();

    } catch (Exception e) {
	 e.printStackTrace();
	  //jbpmSession.handleException();
	  throw new JbpmException("couldn't get task instances list for process instance '"+processInstance.getId()+"'", e);
    }
    return result;
  }

  /**
   * get task list for a given process instance with specified name.
   */
  public List findPendingTaskInstancesByProcessInstanceAndName(ProcessInstance processInstance, String name) {
    List result = null;
    try {
	  Query query = session.getNamedQuery("TaskMgmtSession.findPendingTaskInstancesByProcessInstanceAndName");
	  query.setEntity("processInstance", processInstance);
	  query.setString("name", name);
	  result = query.list();

    } catch (Exception e) {
	 e.printStackTrace();
	  //jbpmSession.handleException();
	  throw new JbpmException("couldn't get pending task instances '"+name+"'", e);
    }
    return result;
  }
  


  /* added by agaton */
  /**
   * get the first opened task from the list for a given actor.
   */
  public TaskInstance getNextAnnotationTask(String actorId) {
    TaskInstance taskInstance = null;
    try {
      //Query query = session.getNamedQuery("TaskMgmtSession.findTaskInstancesByActorId");
      Query query = session.getNamedQuery("TaskMgmtSession.findStartedAnnotationTaskInstancesByActorIdWithAssignedDocumentId");
      query.setString("actorId", actorId);
      query.setMaxResults(1);
      taskInstance = (TaskInstance)query.uniqueResult();

    } catch (Exception e) {
      log.error(e);
      //jbpmSession.handleException();
      throw new JbpmException("couldn't get next task instance for actor '"+actorId+"'", e);
    }
    return taskInstance;
  }


  /* added by agaton */
  /**
   * get the task list for a given actor.
   */
  public List findAllTaskInstances(String actorId) {
    List result = null;
    try {
      Query query = session.getNamedQuery("TaskMgmtSession.findAllTaskInstancesByActorId");
      query.setString("actorId", actorId);
      result = query.list();
    } catch (Exception e) {
      log.error(e);
      //jbpmSession.handleException();
      throw new JbpmException("couldn't get task instances list for actor '"+actorId+"'", e);
    }
    return result;
  }

  /* added by agaton */
  
  
  /**
   * get the taskinstances for which the given actor is in the pool.
   */
  public List findPendingPooledTaskInstances(String actorId) {
    List result = null;
    try {
      Query query = session.getNamedQuery("TaskMgmtSession.findAllPooledTaskInstancesByActorId");
      query.setString("swimlaneActorId", actorId);
      result = query.list();
    } catch (Exception e) {
      log.error(e);
      //jbpmSession.handleException();
      throw new JbpmException("couldn't get pooled task instances list for actor '"+actorId+"'", e);
    }
    return result;
  }
  
  /**
   * get the annotation task instances for which the given actor is in the pool.
   */
  public List findPendingPooledAnnotationTaskInstancesByActorId(String actorId) {
    List result = null;
    try {
      Query query = session.getNamedQuery("TaskMgmtSession.findPendingPooledAnnotationTaskInstancesByActorId");
      query.setString("swimlaneActorId", actorId);
      result = query.list();
    } catch (Exception e) {
      log.error(e);
      //jbpmSession.handleException();
      throw new JbpmException("couldn't get pooled task instances list for actor '"+actorId+"'", e);
    }
    return result;
  }
  
  
  /**
   * get the taskinstances for which the given actor is in the pool.
   */
  public List findAllPooledTaskInstances(String actorId) {
    List result = null;
    try {
      Query query = session.getNamedQuery("TaskMgmtSession.findPendingPooledTaskInstancesByActorId");
      query.setString("swimlaneActorId", actorId);
      result = query.list();
    } catch (Exception e) {
      log.error(e);
      //jbpmSession.handleException();
      throw new JbpmException("couldn't get pooled task instances list for actor '"+actorId+"'", e);
    }
    return result;
  }

//	 CONFIGURATION OPTIONS - MANAGER INTERFACE
  
  /**
   * get Configuration Options list for a given process instance with specified actor.
   */
  public List findPendingConfigurationOptionsByProcessInstancesAndActorId(ProcessInstance[] processInstances, String actorId) {
    List result = null;
    try {
	  Query query = session.getNamedQuery("TaskMgmtSession.findPendingConfigurationOptionsByProcessInstancesAndActorId");
	  query.setParameterList("processInstances", processInstances);
	  query.setString("actorId", actorId);
	  result = query.list();

    } catch (Exception e) {
	 e.printStackTrace();
	  //jbpmSession.handleException();
	  throw new JbpmException("couldn't get pending Configuration Options in process instances", e);
    }
    return result;
  }
 
  
  /**
   * get Configuration Options list for a given process instance key with specified actor.
   */
  public List findPendingConfigurationOptionsByProcessInstanceKeyAndActorId(String key, String actorId){
	  List result = null;
	    try {
		  Query query = session.getNamedQuery("TaskMgmtSession.findPendingConfigurationOptionsByProcessInstanceKeyAndActorId");
		  query.setString("key", key);
		  query.setString("actorId", actorId);
		  result = query.list();

	    } catch (Exception e) {
		 e.printStackTrace();
		  //jbpmSession.handleException();
		  throw new JbpmException("couldn't get pending Configuration Options in process instances", e);
	    }
	    return result;
  }

  
  /**
   * get Configuration Options list with specified actor.
   */
  public List findPendingConfigurationOptionsByActorId(String actorId) {
    List result = null;
    try {
	  Query query = session.getNamedQuery("TaskMgmtSession.findPendingConfigurationOptionsByActorId");
	  query.setString("actorId", actorId);
	  result = query.list();

    } catch (Exception e) {
	 e.printStackTrace();
	  //jbpmSession.handleException();
	  throw new JbpmException("couldn't get pending Configuration Options in process instances", e);
    }
    return result;
  }
  
  

  
  public List findPooledTaskInstancesByActorIdsAndProcessInstances(String[] actorIds, ProcessInstance[] processInstances) {
	    List result = null;
	    try {
		  Query query = session.getNamedQuery("TaskMgmtSession.findPooledTaskInstancesByActorIdsAndProcessInstances");
		  query.setParameterList("actorIds", actorIds);
		  query.setParameterList("processInstances", processInstances);
		  result = query.list();

	    } catch (Exception e) {
		 e.printStackTrace();
		  //jbpmSession.handleException();
		  throw new JbpmException("couldn't get pooled task instances in process instances", e);
	    }
	    return result;
	  }
  
  /*
  * get Configuration Options list with specified actor.
  */
 public List findCompletedTasksByActorIdAndProcessInstance(ProcessInstance processInstance, String actorId) {
   List result = null;
   try {
	  Query query = session.getNamedQuery("TaskMgmtSession.findCompletedTasksByActorIdAndProcessInstance");
	  query.setEntity("processInstance", processInstance);
	  query.setString("actorId", actorId);
	  result = query.list();
   } catch (Exception e) {
	 e.printStackTrace();
	  //jbpmSession.handleException();
	  throw new JbpmException("couldn't get completed task instances for actor in process instance", e);
   }
   return result;
 }
  
 
 public void deleteTaskInstances(Collection<Long> taskInstanceIds) {
	   try {
		  Query query = session.getNamedQuery("GraphSession.deleteTaskInstancesByIds");
		  query.setParameterList("taskInstanceIds", taskInstanceIds);
		  query.executeUpdate();
	   } catch (Exception e) {
		 e.printStackTrace();
		  //jbpmSession.handleException();
		  throw new JbpmException("couldn't delete task instances", e);
	   }
	 }
  private static final Log log = LogFactory.getLog(JbpmTaskMgmtSession.class);
}
