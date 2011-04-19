/*
 *  GetNextDocumentActionHandler.java
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
package gleam.executive.workflow.action.harvard;

import java.util.List;

import gleam.executive.service.DocServiceManager;
import gleam.executive.service.SafeManagerException;
import gleam.executive.workflow.util.AnnotationUtil;
import gleam.executive.workflow.util.WorkflowUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.graph.exe.ExecutionContext;
import org.springframework.util.StringUtils;
import gleam.executive.workflow.util.JPDLConstants;
import gleam.executive.workflow.sm.JbpmHandlerProxy;

public class GetNextDocumentActionHandler extends JbpmHandlerProxy {

	private static final long serialVersionUID = 1L;

	private DocServiceManager docServiceManager;

	/*
	 * <inVarDocumentCSVList>documentCSVList</inVarDocumentCSVList>
	 * <inVarPerformer>performer</inVarPerformer>
	 * <inVarAnnotatorHasToBeUniqueForDocument>annotatorHasToBeUniqueForDocument</inVarAnnotatorHasToBeUniqueForDocument>
	 * <inVarAnnotatorsPerDocument>annotatorsPerDocument</inVarAnnotatorsPerDocument>
	 * <inVarAnonymousAnnotation>anonymousAnnotation</inVarAnonymousAnnotation>
	 * <outVarDocumentId>documentId</outVarDocumentId>
	 * <outVarAnnotationSetName>annotationSetName</outVarAnnotationSetName>
	 */

	// target variables picked from JPDL
	String inVarDocumentCSVList;

	String inVarPerformer;

	String inVarAnnotatorsPerDocument;

	String inVarAnonymousAnnotation;

	String outVarDocumentId;

	String inVarAnnotatorHasToBeUniqueForDocument;

	String outVarAnnotationSetName;

	protected final Log log = LogFactory.getLog(getClass());

	public void execute(ExecutionContext context) throws Exception {
		log.debug("@@@@@@@@@@@@@@@@@@@@@@@@");
		log.debug("GetNextDocumentActionHandler START");
		// obtain targetProperties
		String documentCSVList = (String) context.getContextInstance()
				.getVariable(getInVarDocumentCSVList());
		log.debug("&&&&&&&&&&&&& documentCSVList " + documentCSVList);

		String annotatorHasToBeUniqueForDocument = (String) context
				.getContextInstance().getVariable(
						getInVarAnnotatorHasToBeUniqueForDocument());
		log.debug("&&&&&&&&&&&&& annotatorHasToBeUniqueForDocument "
				+ annotatorHasToBeUniqueForDocument);

		boolean unique = true;
		if (annotatorHasToBeUniqueForDocument == null
				|| "".equals(annotatorHasToBeUniqueForDocument)) {
			unique = false;
		}

		String anonymousAnnotation = (String) context.getContextInstance()
				.getVariable(getInVarAnonymousAnnotation());
		log.debug("&&&&&&&&&&&&& anonymousAnnotation " + anonymousAnnotation);

		boolean anonymous = true;
		if (anonymousAnnotation == null || "".equals(anonymousAnnotation)) {
			anonymous = false;
		}

		String performer = (String) context.getContextInstance().getVariable(
				getInVarPerformer(), context.getToken());
		log.debug("&&&&&&&&&&&&& performer " + performer);

		String annotatorsPerDocument = (String) context.getContextInstance()
				.getVariable(getInVarAnnotatorsPerDocument());
		log.debug("annotatorsPerDocument " + annotatorsPerDocument);

		/*
		 * now find the first available docId from CSV list check if there is a
		 * variable with that name check the value (CSV) and see if user already
		 * annotate it
		 */

		String[] documentIds = StringUtils
				.commaDelimitedListToStringArray(documentCSVList);

		int i = 0;
		String currentDocId = "";
		String currentDocumentSuperAnnotationCSVString = "";
		String currentDocumentAnnotationCSVString = "";
		String currentDocumentCancelationCSVString = "";

		String status = JPDLConstants.TRANSITION_FINISH;

		while (i < documentIds.length
				&& !status.equals(JPDLConstants.DOCUMENT_AVAILABLE)) {
			currentDocId = documentIds[i];
			log.debug("currentDocId " + currentDocId);

			// first check if there is atoken with that name - means that doc
			// already entered the review
			// so it should not be checked for annotation

			if (context.getProcessInstance().findToken(currentDocId) == null) {
				// check if there is a variable with that name
				currentDocumentSuperAnnotationCSVString = (String) context
						.getVariable(JPDLConstants.SUPERANNOTATED_BY_PREFIX
								+ currentDocId);
				log.debug("currentDocumentSuperAnnotationCSVString "
						+ currentDocumentSuperAnnotationCSVString);

				currentDocumentAnnotationCSVString = (String) context
						.getVariable(JPDLConstants.ANNOTATED_BY_PREFIX
								+ currentDocId);
				log.debug("currentDocumentAnnotationCSVString "
						+ currentDocumentAnnotationCSVString);

				currentDocumentCancelationCSVString = (String) context
						.getVariable(JPDLConstants.CANCELED_BY_PREFIX
								+ currentDocId);
				log.debug("currentDocumentCancelationCSVString "
						+ currentDocumentCancelationCSVString);

				status = WorkflowUtil.checkDocumentStatus(currentDocId,
						currentDocumentSuperAnnotationCSVString,
						currentDocumentAnnotationCSVString,
						currentDocumentCancelationCSVString, Integer
								.parseInt(annotatorsPerDocument), performer,
						unique);
			} else {
				log.debug("No need to check this doc. There is a token "
						+ currentDocId);
			}
			i++;

		}

		String oldTokenTransitionName = "";

		if (status.equals(JPDLConstants.DOCUMENT_AVAILABLE)) {
			// at least one status in iteration
			log.debug("Set document id " + currentDocId + " for token "
					+ context.getToken().getName());
			context.getContextInstance().createVariable(getOutVarDocumentId(),
					currentDocId, context.getToken());

			String updatedDocumentAnnotationCSVString = WorkflowUtil
					.markDocumentAsAnnotated(performer,
							currentDocumentAnnotationCSVString);
			log.debug("Set variable " + JPDLConstants.ANNOTATED_BY_PREFIX
					+ currentDocId + " to value "
					+ updatedDocumentAnnotationCSVString);
			context.setVariable(JPDLConstants.ANNOTATED_BY_PREFIX
					+ currentDocId, updatedDocumentAnnotationCSVString);

			createAnnotationSetForAnnotator(context, currentDocId, performer,
					anonymous);

			oldTokenTransitionName = JPDLConstants.DOCUMENT_AVAILABLE;
		} else if (status
				.equals(JPDLConstants.DOCUMENT_AVAILABLE_FOR_SUPERANNOTATION)) {
			// at least one status in iteration
			log.debug("Set document id " + currentDocId + " for token "
					+ context.getToken().getName());
			context.getContextInstance().createVariable(getOutVarDocumentId(),
					currentDocId, context.getToken());

			String updatedDocumentSuperAnnotationCSVString = WorkflowUtil
					.markDocumentAsAnnotated(performer,
							currentDocumentSuperAnnotationCSVString);
			log.debug("Set variable " + JPDLConstants.SUPERANNOTATED_BY_PREFIX
					+ currentDocId + " to value "
					+ updatedDocumentSuperAnnotationCSVString);
			context.setVariable(JPDLConstants.SUPERANNOTATED_BY_PREFIX
					+ currentDocId, updatedDocumentSuperAnnotationCSVString);

			oldTokenTransitionName = JPDLConstants.DOCUMENT_AVAILABLE;

			createAnnotationSetForAnnotator(context, currentDocId, performer,
					anonymous);
		}

		else {
			oldTokenTransitionName = status;
		}

		context.leaveNode(oldTokenTransitionName);
		log.debug("Leave node with transition " + oldTokenTransitionName);

		log.debug("GetNextDocumentActionHandler END");
		log.debug("@@@@@@@@@@@@@@@@@@@@@@@@");
	}

	public String getInVarPerformer() {
		return inVarPerformer;
	}

	public void setInVarPerformer(String inVarPerformer) {
		this.inVarPerformer = inVarPerformer;
	}

	public String getOutVarDocumentId() {
		return outVarDocumentId;
	}

	public void setOutVarDocumentId(String outVarDocumentId) {
		this.outVarDocumentId = outVarDocumentId;
	}

	public String getInVarDocumentCSVList() {
		return inVarDocumentCSVList;
	}

	public void setInVarDocumentCSVList(String inVarDocumentCSVList) {
		this.inVarDocumentCSVList = inVarDocumentCSVList;
	}

	public String getInVarAnnotatorHasToBeUniqueForDocument() {
		return inVarAnnotatorHasToBeUniqueForDocument;
	}

	public void setInVarAnnotatorHasToBeUniqueForDocument(
			String inVarAnnotatorHasToBeUniqueForDocument) {
		this.inVarAnnotatorHasToBeUniqueForDocument = inVarAnnotatorHasToBeUniqueForDocument;
	}

	public String getInVarAnnotatorsPerDocument() {
		return inVarAnnotatorsPerDocument;
	}

	public void setInVarAnnotatorsPerDocument(String inVarAnnotatorsPerDocument) {
		this.inVarAnnotatorsPerDocument = inVarAnnotatorsPerDocument;
	}

	public String getInVarAnonymousAnnotation() {
		return inVarAnonymousAnnotation;
	}

	public void setInVarAnonymousAnnotation(String inVarAnonymousAnnotation) {
		this.inVarAnonymousAnnotation = inVarAnonymousAnnotation;
	}

	public String getOutVarAnnotationSetName() {
		return outVarAnnotationSetName;
	}

	public void setOutVarAnnotationSetName(String outVarAnnotationSetName) {
		this.outVarAnnotationSetName = outVarAnnotationSetName;
	}

	public DocServiceManager getDocServiceManager() {
		return docServiceManager;
	}

	public void setDocServiceManager(DocServiceManager docServiceManager) {
		this.docServiceManager = docServiceManager;
	}

	// it should be a part of future assignment handler
	private void createAnnotationSetForAnnotator(ExecutionContext context,
			String currentDocId, String performer, boolean anonymous)
			throws SafeManagerException {
		String annotationSetName = "";
		boolean created = false;

		// 1st check if somebody cancelled and make name available

		while (!created) {
			List<String> existingAnnotationSetNames = docServiceManager
			.listAnnotationSetNames(currentDocId);
	        log.debug("Found '" + existingAnnotationSetNames.size()
			+ "' annotation sets for document '" + currentDocId);
			if (anonymous) {
				annotationSetName = AnnotationUtil
						.resolveNextAvailableAnnotationSetName(existingAnnotationSetNames, null);
			} else {
				annotationSetName = AnnotationUtil
				.resolveNextAvailableAnnotationSetName(existingAnnotationSetNames, performer);;
			}
			// now try to create it in docservice
			created = getDocServiceManager().createAnnotationSet(currentDocId,
					annotationSetName);

		}

		log.debug("Create annotation Set Name " + annotationSetName
				+ " for token " + context.getToken().getName());
		context.getContextInstance().createVariable(
				getOutVarAnnotationSetName(), annotationSetName,
				context.getToken());

	}

}
