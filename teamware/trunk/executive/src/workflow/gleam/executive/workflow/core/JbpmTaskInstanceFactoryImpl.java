/*
 *  JbpmTaskInstanceFactoryImpl.java
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
