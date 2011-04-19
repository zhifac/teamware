/*
 *  AddAnnotationActionHandler.java
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

import gleam.executive.service.DocServiceManager;
import gleam.executive.workflow.util.WorkflowUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.graph.exe.ExecutionContext;
import gleam.executive.workflow.util.JPDLConstants;
import gleam.executive.workflow.sm.JbpmHandlerProxy;


public class AddAnnotationActionHandler extends JbpmHandlerProxy {

	private String inVarPerformer;

	private String inVarDocumentId;

	private String inVarAnnotationSetName;

	private DocServiceManager docServiceManager;

	protected final Log log = LogFactory.getLog(getClass());

	public void execute(ExecutionContext context) throws Exception {
		log.debug("@@@@@@@@@@@@@@@@@@@@@@@@");
		log.debug("AddAnnotationActionHandler START");
		// obtain targetProperties

		String performer = (String) context.getContextInstance().getVariable(
				getInVarPerformer(), context.getToken());
		log.debug("&&&&&&&&&&&&& performer " + performer);
		String documentId = (String) context.getContextInstance().getVariable(
				getInVarDocumentId(), context.getToken());
		log.debug("&&&&&&&&&&&&& documentId " + documentId);

		String annotSetName = (String) context.getContextInstance()
				.getVariable(getInVarAnnotationSetName(), context.getToken());
		log.debug("&&&&&&&&&&&&& annotationSetName " + annotSetName);

		String currentDocumentFinishedAnnotationCSVString = (String) context
				.getVariable(JPDLConstants.FINISHED_BY_PREFIX + documentId);
		String updatedDocumentFinishedAnnotationCSVString = WorkflowUtil
				.markDocumentAsAnnotated(performer,
						currentDocumentFinishedAnnotationCSVString);
		log.debug("Set variable "
				+ JPDLConstants.FINISHED_BY_PREFIX
				+ documentId
				+ " to: "
				+ (String) context.getVariable(JPDLConstants.FINISHED_BY_PREFIX
						+ documentId) + " to value "
				+ updatedDocumentFinishedAnnotationCSVString);
		context.setVariable(JPDLConstants.FINISHED_BY_PREFIX + documentId,
				updatedDocumentFinishedAnnotationCSVString);

		log.debug("Adding document feature with key \"safe.asname." + annotSetName
				+ "\" and the user who annotated the document \"" + performer
				+ "\"");
		docServiceManager.setDocumentFeature(documentId, "safe.asname."+annotSetName,
				performer);

		log.debug("AddAnnotationActionHandler END");

		log.debug("@@@@@@@@@@@@@@@@@@@@@@@@");
	}

	public String getInVarPerformer() {
		return inVarPerformer;
	}

	public void setInVarPerformer(String inVarPerformer) {
		this.inVarPerformer = inVarPerformer;
	}

	public String getInVarDocumentId() {
		return inVarDocumentId;
	}

	public void setInVarDocumentId(String inVarDocumentId) {
		this.inVarDocumentId = inVarDocumentId;
	}

	public String getInVarAnnotationSetName() {
		return inVarAnnotationSetName;
	}

	public void setInVarAnnotationSetName(String inVarAnnotationSetName) {
		this.inVarAnnotationSetName = inVarAnnotationSetName;
	}

	public DocServiceManager getDocServiceManager() {
		return docServiceManager;
	}

	public void setDocServiceManager(DocServiceManager docServiceManager) {
		this.docServiceManager = docServiceManager;
	}

}
