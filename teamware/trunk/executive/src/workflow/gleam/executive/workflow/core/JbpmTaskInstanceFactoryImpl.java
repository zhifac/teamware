package gleam.executive.workflow.core;

import gleam.executive.workflow.util.JPDLConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.taskmgmt.TaskInstanceFactory;
import org.jbpm.taskmgmt.exe.TaskInstance;



/**
 * a custom task instance factory.
 */
public class JbpmTaskInstanceFactoryImpl implements TaskInstanceFactory {

  private static final long serialVersionUID = 1L;
  private static final Log log = LogFactory.getLog(JbpmTaskInstanceFactoryImpl.class);


  public TaskInstance createTaskInstance(ExecutionContext context) {
      log.debug("Create JbpmTaskInstance");
      JbpmTaskInstance taskInstance = new JbpmTaskInstance();
      String documentId = (String)context.getContextInstance().getVariable(JPDLConstants.DOCUMENT_ID, context.getToken());
	   log.debug("documentId in token "+documentId);
	   if(documentId!=null){
		   taskInstance.setDocumentId(documentId);
		   log.debug("set document "+taskInstance.getDocumentId() + "in taskInstance");
	   }
	   
	  return taskInstance;
  }
}
