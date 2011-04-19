/*
 *  ProcessInitActionHandler.java
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

import gleam.executive.model.WebAppBean;
import gleam.executive.service.DocServiceManager;
import gleam.executive.service.GosManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.graph.exe.ExecutionContext;
import gleam.executive.workflow.util.JPDLConstants;
import gleam.executive.workflow.sm.JbpmHandlerProxy;


public class ProcessInitActionHandler  extends JbpmHandlerProxy {

	private static final long serialVersionUID = 1L;

	/*
	 * <inVarMode>mode</inVarMode>
	 * <inVarOntologyRepositoryName>ontologyRepositoryName</inVarOntologyRepositoryName>
	 * <outVarDocServiceURL>docserviceURL</outVarDocServiceURL>
	 * <outVarTemplateAnnotatorGUIURL>templateAnnotatorGUIURL</outVarTemplateAnnotatorGUIURL>
	 * <outVarTemplateAnnotationDifferURL>templateAnnotationDifferURL</outVarTemplateAnnotationDifferURL>
	 * <outVarGosURL>gosURL</outVarGosURL>
	 */

	// target variables picked from JPDL

	String inVarMode;

	String inVarOntologyRepositoryName;

	String outVarDocServiceURL;

	String outVarTemplateAnnotatorGUIURL;

	String outVarTemplateAnnotationDifferURL;

	String outVarGosURL;
	// beans and temp variables
	DocServiceManager docServiceManager;

	GosManager gosManager;

	WebAppBean webAppBean;


	protected final Log log = LogFactory.getLog(getClass());

	public void execute(ExecutionContext context) throws Exception {
		log.debug("@@@@@@@@@@@@@@@@@@@@@@@@");
		log.debug("ProcessInitActionHandler START");
		// obtain targetProperties
		String ontologyRepositoryName = "";
		String docServiceURL = "";
		String annotatorTemplateGUIURL = "";
		String annotationDifferTemplateURL = "";
		String gosURL = "";

		String mode = (String) context.getVariable(getInVarMode());
	    log.debug("@@@@@@@ mode " + mode);

		if(!JPDLConstants.TEST_MODE.equals(mode)){
		ontologyRepositoryName = (String) context.getVariable(getInVarOntologyRepositoryName());
		log.debug("&&&&&&&&&&&&& ontologyRepositoryName " + ontologyRepositoryName);

		// now set docservice, ontoservice, ontology respository and annotatorGUIURL
		docServiceURL = docServiceManager.getDocServiceURL();
		log.debug("docServiceURL " + docServiceURL);
		context.setVariable(getOutVarDocServiceURL(), docServiceURL);

		annotatorTemplateGUIURL = docServiceManager.getAnnotatorGUIURL();
		log.debug("annotatorTemplateGUIURL " + annotatorTemplateGUIURL);
		context.setVariable(getOutVarTemplateAnnotatorGUIURL(), annotatorTemplateGUIURL);

		annotationDifferTemplateURL = webAppBean.getAnnoDiffURL();
		log.debug("annotationDifferTemplateURL " + annotationDifferTemplateURL);
		context.setVariable(getOutVarTemplateAnnotationDifferURL(), annotationDifferTemplateURL);

		/*
		gosURL = gosManager.getGosURL();
		log.debug("gosURL " + gosURL);
		if(getOutVarGosURL()!=null){
		   context.setVariable(getOutVarGosURL(), gosURL);
		}
		*/
		}
	    log.debug("ProcessInitActionHandler END");
		log.debug("@@@@@@@@@@@@@@@@@@@@@@@@");
	}

	public DocServiceManager getDocServiceManager() {
		return docServiceManager;
	}

	public void setDocServiceManager(DocServiceManager docServiceManager) {
		this.docServiceManager = docServiceManager;
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

	public String getInVarMode() {
		return inVarMode;
	}

	public void setInVarMode(String inVarMode) {
		this.inVarMode = inVarMode;
	}
}
