/*
 *  JBPMCallbackServiceImpl.java
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
package gleam.executive.workflow.callback;

import java.net.URI;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;

import javax.jws.WebService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.taskmgmt.exe.TaskInstance;

import gleam.executive.workflow.util.JPDLConstants;
import gleam.executive.service.callback.AnnotationTask;
import gleam.executive.service.callback.ExecutiveCallbackService;
import gleam.executive.service.callback.ExecutiveServiceCallbackException;
import gleam.executive.workflow.manager.WorkflowManager;
import gleam.executive.workflow.util.WorkflowException;
import gleam.executive.workflow.util.WorkflowUtil;

@WebService(endpointInterface = "gleam.executive.service.callback.ExecutiveCallbackService", targetNamespace = "http://gate.ac.uk/ns/safe/1.1/executive-callback-service")
public class JBPMCallbackServiceImpl implements ExecutiveCallbackService {

	protected final Log log = LogFactory.getLog(getClass());

	private WorkflowManager workflowManager;

	public WorkflowManager getWorkflowManager() {
		return workflowManager;
	}

	public void setWorkflowManager(WorkflowManager workflowManager) {
		this.workflowManager = workflowManager;
	}

	/**
	 * @see gleam.executive.service.callback.ExecutiveCallbackService#taskFinished
	 *      (String)
	 */
	public void taskFinished(String taskInstanceId) throws ExecutiveServiceCallbackException {
		    log.debug("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
		    log.debug("@@@ taskFinished fired ");
			int i = 100;
			Matcher m = JPDLConstants.TASK_PATTERN.matcher(taskInstanceId);
			long id = -1;
			if (m.find()) {
				// isToken = !m.group(1).equals("task");
				id = Long.parseLong(m.group(2));
				log.debug("@@@taskInstanceId: "+id);
			try{
				log.debug("@@@workflowManager.getTaskInstance(id): "+ workflowManager.getTaskInstance(id));
				while(workflowManager.getTaskInstance(id)==null && i>0){
					Thread.sleep(5000);
					log.debug("taskInstance '"+ taskInstanceId + "' still not updated in DB. Waiting next 5s ");
					i--;
				}
				if(workflowManager.getTaskInstance(id)!=null){
				workflowManager.executeCallback(taskInstanceId,
						JPDLConstants.CALLBACK_ERROR_EMPTY);
				}
				else {
					throw new ExecutiveServiceCallbackException ("Process has not yet reached wait state, Retry!");
				}
				log.debug("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
			}
			catch(Exception e){
				log.error(e);
			}
			}
	}

	/**
	 * @see gleam.executive.service.callback.ExecutiveCallbackService#taskFinished
	 *      (String)
	 */
	public void taskCompleted(String taskInstanceId) throws ExecutiveServiceCallbackException{
		try {
			long id = Long.parseLong(taskInstanceId);
			log.debug("@@@ taskCompleted fired id: "+id);
			TaskInstance ti = workflowManager.getTaskInstance(id);
			if(ti!=null && !ti.hasEnded()){
			   workflowManager.completeTask(id, null, null);
		    }
			else throw new ExecutiveServiceCallbackException("Task has been already completed");
		} catch (WorkflowException e) {
			throw new ExecutiveServiceCallbackException (e);
		}
	}

	/**
	 * @see gleam.executive.service.callback.ExecutiveCallbackService#taskSaved
	 *      (String)
	 */
	public void taskSaved(String taskInstanceId) throws ExecutiveServiceCallbackException {
		try {
			long id = Long.parseLong(taskInstanceId);
			log.debug("@@@ taskSaved fired id: " + id);
			TaskInstance ti = workflowManager.getTaskInstance(id);
			if(ti != null && !ti.hasEnded()) {
				workflowManager.saveTask(id, null, "Save");
			}
		} catch (WorkflowException e) {
			throw new ExecutiveServiceCallbackException (e);
		}
	}

	/**
	 * @see gleam.executive.service.callback.ExecutiveCallbackService#taskFinished
	 *      (String)
	 */
	public void taskCanceled(String taskInstanceId) throws ExecutiveServiceCallbackException {
		try {
			log.debug("@@@ taskCanceled fired ");
            workflowManager.rejectTaskInstance(Long.parseLong(taskInstanceId));
		} catch (WorkflowException e) {
			throw new ExecutiveServiceCallbackException (e);
		}
	}

	/**
	 * @see gleam.executive.service.callback.ExecutiveCallbackService#taskFailed
	 *      (String, String)
	 */
	public void taskFailed(String taskInstanceId, String reason) throws ExecutiveServiceCallbackException {
		try {
			log.debug("@@@ taskFailed fired with reason "
							+ reason);
			workflowManager.executeCallback(taskInstanceId,
					reason);
		} catch (WorkflowException e) {
			throw new ExecutiveServiceCallbackException (e);
		}
	}

	public AnnotationTask getNextTask(String actorId) throws ExecutiveServiceCallbackException {
		AnnotationTask annotationTask = null;
		try {
			log.debug("@@@ getNextTask called for actor "
							+ actorId);
		Map<String, Object> map = workflowManager.getNextTaskAsMap(actorId);
		if(map!=null){
			annotationTask = new AnnotationTask();
			Long tokenId = (Long)map.get(JPDLConstants.TOKEN_ID);
			log.debug("@@@  tokenId " + tokenId);
			String taskName = (String)map.get(JPDLConstants.TASK_NAME);
			log.debug("@@@  taskName " + taskName);
            Long taskInstanceId = (Long)map.get(JPDLConstants.TASK_INSTANCE_ID);
			log.debug("@@@  taskId " + taskInstanceId);
            Calendar startDate = null;
			if(map.get(JPDLConstants.START_DATE)!=null){
				startDate = Calendar.getInstance();
			    startDate.setTime((Date)map.get(JPDLConstants.START_DATE));
			    log.debug("@@@  startDate " + startDate);
			}

		    Calendar dueDate = null;
		    if(map.get(JPDLConstants.DUE_DATE)!=null){
		    	dueDate = Calendar.getInstance();
		    	dueDate.setTime((Date)map.get(JPDLConstants.DUE_DATE));
			    log.debug("@@@  dueDate " + dueDate);
			}
		    
		    String ontologyRepositoryName = (String)map.get(JPDLConstants.ONTOLOGY_REPOSITORY_NAME);
		    log.debug("@@@  ontologyRepositoryName " + ontologyRepositoryName);

		    URI docServiceURI = (URI)map.get(JPDLConstants.DOCSERVICE_URL);
		    log.debug("@@@  docserviceURI " + docServiceURI);

		    URI owlimServiceURI = (URI)map.get(JPDLConstants.OWLIM_SERVICE_URL);
		    log.debug("@@@  owlimServiceURL " + owlimServiceURI);

		    String documentId = (String)map.get(JPDLConstants.DOCUMENT_ID);
		    log.debug("@@@  documentId " + documentId);

		    String annotationSetName = (String)map.get(JPDLConstants.ANNOTATION_SET_NAME);
		    log.debug("@@@  annotationSetName " + annotationSetName);

		    String annotationSchemaCSVURLs = (String)map.get(JPDLConstants.ANNOTATION_SCHEMA_CSV_URLS);
		    log.debug("@@@  annotationSchemaCSVURLs " + annotationSchemaCSVURLs);

		    String baseAnnotationSchemaURL = (String)map.get(JPDLConstants.BASE_ANNOTATION_SCHEMA_URL);
		    log.debug("@@@  baseAnnotationSchemaURL " + baseAnnotationSchemaURL);

		    String pluginCSVList = (String)map.get(JPDLConstants.PLUGIN_CSV_LIST);
		    log.debug("@@@  pluginCSVList " + pluginCSVList);

		    String canCancel = (String)map.get(JPDLConstants.CAN_CANCEL);
		    log.debug("@@@  canCancel " + canCancel);

		    annotationTask.setTaskInstanceId(taskInstanceId);
		    annotationTask.setTokenId(String.valueOf(tokenId));
		    annotationTask.setTaskName(taskName);
		    annotationTask.setPerformer(actorId);
		    annotationTask.setStartDate(startDate);
		    annotationTask.setDueDate(dueDate);
		    annotationTask.setDocumentId(documentId);
		    annotationTask.setAnnotationSetName(annotationSetName);
		    annotationTask.setDocserviceURL(docServiceURI);
		    annotationTask.setOwlimServiceURL(owlimServiceURI);
		    annotationTask.setOwlimRepositoryName(ontologyRepositoryName);
		    if(annotationSchemaCSVURLs != null || !"".equals(annotationSchemaCSVURLs)){
		    	annotationTask.setAnnotationSchemaCSVURLs(WorkflowUtil.createAnnotationSchemaCSV(annotationSchemaCSVURLs, baseAnnotationSchemaURL));
			}
		    annotationTask.setPluginCSVList(pluginCSVList);
		    annotationTask.setCancelAllowed(canCancel != null && !"".equals(canCancel));
		    log.debug("@@@@@@ ANNOTATION TASK @@@@@@");
		    log.debug(annotationTask.toString());
		    log.debug("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
		  }
		else {
			log.debug("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
			log.debug("@ CANNOT FIND ANNOTATION TASK @");
			log.debug("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
		}
		} catch (WorkflowException e) {
			throw new ExecutiveServiceCallbackException (e);
		}
		return annotationTask;
	}

	public boolean checkForNextTask(String actorId) throws ExecutiveServiceCallbackException{
		log.debug("@@@ checkForNextTask called for actor "
				+ actorId);
	    boolean flag = false;
		try {
	      // final in order to be accessible from inner class
	    	flag = workflowManager.checkForNextTask(actorId);
	    }
		catch (WorkflowException e) {
			throw new ExecutiveServiceCallbackException (e);
		}
	    return flag;
	  }

}
