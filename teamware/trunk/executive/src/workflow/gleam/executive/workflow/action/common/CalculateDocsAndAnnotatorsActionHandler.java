/*
 *  CalculateDocsAndAnnotatorsActionHandler.java
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
package gleam.executive.workflow.action.common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import gleam.executive.model.Document;
import gleam.executive.model.User;
import gleam.executive.model.WebAppBean;
import gleam.executive.service.DocServiceManager;
import gleam.executive.service.GosManager;
import gleam.executive.service.UserManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.taskmgmt.def.Task;
import org.jbpm.taskmgmt.def.TaskMgmtDefinition;
import org.springframework.util.StringUtils;
import gleam.executive.workflow.sm.JbpmHandlerProxy;

public class CalculateDocsAndAnnotatorsActionHandler extends JbpmHandlerProxy {

	private static final long serialVersionUID = 1L;

	/*
	 * <inVarTaskName>annotate</inVarTaskName> <inVarRoleName></inVarRoleName>
	 * <inVarCorpusId>corpusId</inVarCorpusId>
	 * <inVarOntologyRepositoryName>ontologyRepositoryName</inVarOntologyRepositoryName>
	 * <outVarNumberOfAnnotators>numberOfAnnotators</outVarNumberOfAnnotators>
	 * <outVarNumberOfDocuments>numberOfDocuments</outVarNumberOfDocuments>
	 * <outVarAnnotatorCSVList>annotatorCSVList</outVarAnnotatorCSVList>
	 * <outVarDocumentCSVList>documentCSVList</outVarDocumentCSVList>
	 * <outVarDocServiceURL>docserviceURL</outVarDocServiceURL>
	 * <outVarTemplateAnnotatorGUIURL>templateAnnotatorGUIURL</outVarTemplateAnnotatorGUIURL>
	 * <outVarTemplateAnnotationDifferURL>templateAnnotationDifferURL</outVarTemplateAnnotationDifferURL>
	 * <outVarGosURL>gosURL</outVarGosURL>
	 */

	// target variables picked from JPDL
	String inVarCorpusId;

	String inVarOntologyRepositoryName;

	String inVarRoleName;

	String inVarTaskName;

	String outVarNumberOfAnnotators;

	String outVarNumberOfDocuments;

	String outVarAnnotatorCSVList;

	String outVarDocumentCSVList;

	String outVarDocServiceURL;

	String outVarTemplateAnnotatorGUIURL;

	String outVarTemplateAnnotationDifferURL;

	String outVarGosURL;
	// beans and temp variables
	DocServiceManager docServiceManager;

	GosManager gosManager;

	WebAppBean webAppBean;

	UserManager userManager;

	int numberOfAnnotators = 0;

	int numberOfDocuments = 0;

	String annotatorCSVList = "";

	String documentCSVList = "";

	protected final Log log = LogFactory.getLog(getClass());

	public void execute(ExecutionContext context) throws Exception {
		log.debug("@@@@@@@@@@@@@@@@@@@@@@@@");
		log.debug("calculateDocsAndAnnotatorsActionHandler START");
		// obtain targetProperties
		String corpusId = (String) context.getVariable(getInVarCorpusId());
		log.debug("&&&&&&&&&&&&& corpusId " + corpusId);

		String ontologyRepositoryName = (String) context.getVariable(getInVarOntologyRepositoryName());
		log.debug("&&&&&&&&&&&&& ontologyRepositoryName " + ontologyRepositoryName);

		String taskName = (String) context.getVariable(getInVarTaskName());
		log.debug("&&&&&&&&&&&&& taskName " + taskName);
		String roleName = (String) context.getVariable(getInVarRoleName());
		log.debug("&&&&&&&&&&&&& roleName " + roleName);

		List<Document> documents = docServiceManager.listDocuments(corpusId);
		log.debug("found documents " + documents.size());
		context.setVariable(getOutVarNumberOfDocuments(), documents.size());

		Iterator<Document> it = documents.iterator();
		List<String> documentIdList = new ArrayList<String>();
		while (it.hasNext()) {
			Document doc = it.next();
			documentIdList.add(doc.getDocumentID());
			// create variable number of annotators per document whuch is equal
			// as specified number of annotators in curator task
		}
		documentCSVList = StringUtils
				.collectionToCommaDelimitedString(documentIdList);
		log.debug("documentCSVList " + documentCSVList);
		context.setVariable(getOutVarDocumentCSVList(), documentCSVList);
		// if taskName variable is not supplied, then look for roleName

		if (taskName != null && !"".equals(taskName)) {
			TaskMgmtDefinition tmd = (TaskMgmtDefinition) context
					.getDefinition(TaskMgmtDefinition.class);
			Task task = tmd.getTask(taskName);
			log.debug("obtained  task " + task.getName());
			String pooledActorsExpression = task.getPooledActorsExpression();
			String actorIdExpression = task.getActorIdExpression();
			log.debug("obtained  pooled actors string "
					+ pooledActorsExpression);
			log.debug("obtained   actors string " + actorIdExpression);
			if (pooledActorsExpression != null
					&& !"".equals(pooledActorsExpression)) {
				annotatorCSVList = pooledActorsExpression;
			} else if (actorIdExpression != null
					&& !"".equals(actorIdExpression)) {
				annotatorCSVList = actorIdExpression;
			} else {
				annotatorCSVList = "";
			}
			numberOfAnnotators = StringUtils
					.commaDelimitedListToStringArray(annotatorCSVList).length;
		} else if (roleName != null && !"".equals(roleName)) {
			List<User> users = userManager.getUsersWithRole(roleName);
			List<String> userNameList = new ArrayList<String>();
			Iterator<User> itt = users.iterator();
			while (itt.hasNext()) {
				User user = itt.next();
				userNameList.add(user.getUsername());

			}
			annotatorCSVList = StringUtils
					.collectionToCommaDelimitedString(userNameList);
			numberOfAnnotators = users.size();
		}
		log.debug("found users " + numberOfAnnotators);
		context.setVariable(getOutVarNumberOfAnnotators(), numberOfAnnotators);
		log.debug("annotatorCSVList " + annotatorCSVList);
		context.setVariable(getOutVarAnnotatorCSVList(), annotatorCSVList);

		// now set docservice, ontoservice, ontology respository and annotatorGUIURL
		String docServiceURL = docServiceManager.getDocServiceURL();
		log.debug("docServiceURL " + docServiceURL);
		context.setVariable(getOutVarDocServiceURL(), docServiceURL);

		String annotatorTemplateGUIURL = docServiceManager.getAnnotatorGUIURL();
		log.debug("annotatorTemplateGUIURL " + annotatorTemplateGUIURL);
		context.setVariable(getOutVarTemplateAnnotatorGUIURL(), annotatorTemplateGUIURL);

		String annotationDifferTemplateURL = webAppBean.getAnnoDiffURL();
		log.debug("annotationDifferTemplateURL " + annotationDifferTemplateURL);
		context.setVariable(getOutVarTemplateAnnotationDifferURL(), annotationDifferTemplateURL);

		/*
		String gosURL = gosManager.getGosURL();
		log.debug("gosURL " + gosURL);
		if(getOutVarGosURL()!=null){
		   context.setVariable(getOutVarGosURL(), gosURL);
		}
		*/
	    log.debug("calculateDocsAndAnnotatorsActionHandler END");
		log.debug("@@@@@@@@@@@@@@@@@@@@@@@@");
	}

	public DocServiceManager getDocServiceManager() {
		return docServiceManager;
	}

	public void setDocServiceManager(DocServiceManager docServiceManager) {
		this.docServiceManager = docServiceManager;
	}

	public UserManager getUserManager() {
		return userManager;
	}

	public void setUserManager(UserManager userManager) {
		this.userManager = userManager;
	}

	public String getInVarRoleName() {
		return inVarRoleName;
	}

	public void setInVarRoleName(String inVarRoleName) {
		this.inVarRoleName = inVarRoleName;
	}

	public String getInVarTaskName() {
		return inVarTaskName;
	}

	public void setInVarTaskName(String inVarTaskName) {
		this.inVarTaskName = inVarTaskName;
	}

	public String getOutVarAnnotatorCSVList() {
		return outVarAnnotatorCSVList;
	}

	public void setOutVarAnnotatorCSVList(String outVarAnnotatorCSVList) {
		this.outVarAnnotatorCSVList = outVarAnnotatorCSVList;
	}

	public String getOutVarDocumentCSVList() {
		return outVarDocumentCSVList;
	}

	public void setOutVarDocumentCSVList(String outVarDocumentCSVList) {
		this.outVarDocumentCSVList = outVarDocumentCSVList;
	}

	public String getOutVarNumberOfAnnotators() {
		return outVarNumberOfAnnotators;
	}

	public void setOutVarNumberOfAnnotators(String outVarNumberOfAnnotators) {
		this.outVarNumberOfAnnotators = outVarNumberOfAnnotators;
	}

	public String getOutVarNumberOfDocuments() {
		return outVarNumberOfDocuments;
	}

	public void setOutVarNumberOfDocuments(String outVarNumberOfDocuments) {
		this.outVarNumberOfDocuments = outVarNumberOfDocuments;
	}

	public String getInVarCorpusId() {
		return inVarCorpusId;
	}

	public void setInVarCorpusId(String inVarCorpusId) {
		this.inVarCorpusId = inVarCorpusId;
	}


	public String getOutVarTemplateAnnotatorGUIURL() {
		return outVarTemplateAnnotatorGUIURL;
	}

	public void setOutVarTemplateAnnotatorGUIURL(String outVarTemplateAnnotatorGUIURL) {
		this.outVarTemplateAnnotatorGUIURL = outVarTemplateAnnotatorGUIURL;
	}

	public String getOutVarDocServiceURL() {
		return outVarDocServiceURL;
	}

	public void setOutVarDocServiceURL(String outVarDocServiceURL) {
		this.outVarDocServiceURL = outVarDocServiceURL;
	}

	public String getOutVarTemplateAnnotationDifferURL() {
		return outVarTemplateAnnotationDifferURL;
	}

	public void setOutVarTemplateAnnotationDifferURL(
			String outVarTemplateAnnotationDifferURL) {
		this.outVarTemplateAnnotationDifferURL = outVarTemplateAnnotationDifferURL;
	}

	public WebAppBean getWebAppBean() {
		return webAppBean;
	}

	public void setWebAppBean(WebAppBean webAppBean) {
		this.webAppBean = webAppBean;
	}

	public GosManager getGosManager() {
		return gosManager;
	}

	public void setGosManager(GosManager gosManager) {
		this.gosManager = gosManager;
	}

	public String getInVarOntologyRepositoryName() {
		return inVarOntologyRepositoryName;
	}

	public void setInVarOntologyRepositoryName(String inVarOntologyRepositoryName) {
		this.inVarOntologyRepositoryName = inVarOntologyRepositoryName;
	}

	public String getOutVarGosURL() {
		return outVarGosURL;
	}

	public void setOutVarGosURL(String outVarGosURL) {
		this.outVarGosURL = outVarGosURL;
	}

}
