/*
 *  CheckDocumentActionHandler.java
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


import gleam.executive.workflow.util.WorkflowUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.Token;
import gleam.executive.workflow.util.CSVUtil;
import gleam.executive.workflow.util.JPDLConstants;
import gleam.executive.workflow.sm.JbpmHandlerProxy;

public class CheckDocumentActionHandler extends JbpmHandlerProxy {

	/*
	 * <inOutVarDocumentCSVList>documentCSVList</inOutVarDocumentCSVList>
	 * <inOutVarDocumentId>documentId</inOutVarDocumentId>
	 * <inVarAnnotatorsPerDocument>annotatorsPerDocument</inVarAnnotatorsPerDocument>
	 * <outVarAnnotatedBy>annotatedBy</outVarAnnotatedBy>
	 * <outVarCanceledBy>canceledBy</outVarCanceledBy>
	 * <outVarSuperAnnotatedBy>superAnnotatedBy</outVarSuperAnnotatedBy>
	 *
	 */

	// target variables picked from JPDL
	String inOutVarDocumentId;

	String inOutVarDocumentCSVList;

	String inVarAnnotatorsPerDocument;

	String outVarAnnotatedBy;

	String outVarCanceledBy;

	String outVarSuperAnnotatedBy;

	protected final Log log = LogFactory.getLog(getClass());

	public void execute(ExecutionContext context) throws Exception {
		log.debug("@@@@@@@@@@@@@@@@@@@@@@@@");
		log.debug("CheckDocumentActionHandler START");
		// obtain targetProperties
		String documentCSVList = (String) context.getContextInstance()
				.getVariable(getInOutVarDocumentCSVList());
		log.debug("&&&&&&&&&&&&& documentCSVList " + documentCSVList);

		String documentId = (String) context
				.getVariable(getInOutVarDocumentId());
		log.debug("&&&&&&&&&&&&& documentId " + documentId);

		String superAnnotationCSVString = (String) context
				.getVariable(JPDLConstants.SUPERANNOTATED_BY_PREFIX
						+ documentId);
		log.debug("superAnnotationCSVString " + superAnnotationCSVString);

		String annotatorsPerDocument = (String) context
		.getContextInstance().getVariable(
				getInVarAnnotatorsPerDocument());
		log.debug("annotatorsPerDocument " + annotatorsPerDocument);

		if (superAnnotationCSVString != null
				&& !"".equals(superAnnotationCSVString)) {

			// create superannotatedBy
			log
			.debug("Set variable "
					+ JPDLConstants.SUPERANNOTATED_BY_PREFIX
					+ documentId + " to value "
					+ superAnnotationCSVString);

	         context.setVariable(
			JPDLConstants.SUPERANNOTATED_BY_PREFIX
					+ documentId, superAnnotationCSVString);

			context.leaveNode(JPDLConstants.DOCUMENT_SUPER_ANNOTATED);
			log.debug("Leave node with transition "
					+ JPDLConstants.DOCUMENT_SUPER_ANNOTATED);
		} else {

			String annotatedByCSVString = (String) context
			.getVariable(JPDLConstants.ANNOTATED_BY_PREFIX
					+ documentId);
	        log.debug("annotatedByCSVString " + annotatedByCSVString);

	        String canceledByCSVString = (String) context
			.getVariable(JPDLConstants.CANCELED_BY_PREFIX
					+ documentId);
	        log.debug("canceledByCSVString " + canceledByCSVString);


	        String finishedByCSVString = (String) context
			.getVariable(JPDLConstants.FINISHED_BY_PREFIX
					+ documentId);
	        log.debug("finishedByCSVString " + finishedByCSVString);


			int numberOfAnnotatorsLeftInt = Integer.parseInt(annotatorsPerDocument) - CSVUtil.getNumberOfTokens(finishedByCSVString);

			/* the condition for creating new "document centric" token is that:
			 * 1. document is annotated specified number of times
			 * 2. the "document-centric" token has not been already created
			 */


			if (numberOfAnnotatorsLeftInt == 0 && !tokenCreated(context, documentId)) {
				// create new token

				// create new token with the name = document id

				final Token rootToken = context.getProcessInstance().getRootToken();
				log.debug("@@@@@@@ now, we are in the token: " + rootToken.getName());
				final Node node = context.getNode();
				log.debug("@@@@@@@ node " + node.getName());
				final Token newToken = new Token(rootToken, documentId);
				log.debug("@@@@@@@ created new token ID " + newToken.getId()
						+ "   NAME: " + newToken.getName());
				newToken.setTerminationImplicit(true);
				context.getJbpmContext().getSession().save(newToken);
				final ExecutionContext newExecutionContext = new ExecutionContext(
						newToken);
				// create new performer local variable

				log.debug("Set document id " + documentId + " for token "
						+ newToken.getName());

				documentCSVList = WorkflowUtil.removeDocumentFromList(
						documentId, documentCSVList);
				log.debug("updatedDocumentCSVList " + documentCSVList);
				newExecutionContext.getContextInstance().createVariable(
						getInOutVarDocumentId(), documentId, newToken);

				log.debug("@@@@@@@ created variable " + documentId
						+ " for token " + newToken.getName());
				// create annotatedBy and canceledBy variables

				newExecutionContext.getContextInstance().createVariable(
						getOutVarAnnotatedBy(), annotatedByCSVString, newToken);

				log.debug("@@@@@@@ created variable " + annotatedByCSVString
						+ " for token " + newToken.getName());

				newExecutionContext.getContextInstance().createVariable(
						getOutVarCanceledBy(), canceledByCSVString, newToken);

				log.debug("@@@@@@@ created variable " + canceledByCSVString
						+ " for token " + newToken.getName());

				log.debug("Set variable " + getInOutVarDocumentCSVList()
						+ " to value " + documentCSVList);

				context.setVariable(getInOutVarDocumentCSVList(),
						documentCSVList);


				newExecutionContext.getJbpmContext().getSession()
						.save(newToken);

				// node.leave(newExecutionContext);
				node.leave(newExecutionContext,
						JPDLConstants.DOCUMENT_ANNOTATED);

				log.debug("NODE LEFT BY NEW TOKEN " + newToken.getName());
			}
			log.debug("Leave node with transition "
					+ JPDLConstants.TRANSITION_BACK_TO_POOL);
			context.leaveNode(JPDLConstants.TRANSITION_BACK_TO_POOL);

		}

		log.debug("CheckDocumentActionHandler END");
		log.debug("@@@@@@@@@@@@@@@@@@@@@@@@");
	}

	public String getInOutVarDocumentCSVList() {
		return inOutVarDocumentCSVList;
	}

	public void setInOutVarDocumentCSVList(String inOutVarDocumentCSVList) {
		this.inOutVarDocumentCSVList = inOutVarDocumentCSVList;
	}

	public String getInVarAnnotatorsPerDocument() {
		return inVarAnnotatorsPerDocument;
	}

	public void setInVarAnnotatorsPerDocument(String inVarAnnotatorsPerDocument) {
		this.inVarAnnotatorsPerDocument = inVarAnnotatorsPerDocument;
	}

	public String getInOutVarDocumentId() {
		return inOutVarDocumentId;
	}

	public void setInOutVarDocumentId(String inOutVarDocumentId) {
		this.inOutVarDocumentId = inOutVarDocumentId;
	}

	public String getOutVarAnnotatedBy() {
		return outVarAnnotatedBy;
	}

	public void setOutVarAnnotatedBy(String outVarAnnotatedBy) {
		this.outVarAnnotatedBy = outVarAnnotatedBy;
	}

	public String getOutVarCanceledBy() {
		return outVarCanceledBy;
	}

	public void setOutVarCanceledBy(String outVarCanceledBy) {
		this.outVarCanceledBy = outVarCanceledBy;
	}

	public String getOutVarSuperAnnotatedBy() {
		return outVarSuperAnnotatedBy;
	}

	public void setOutVarSuperAnnotatedBy(String outVarSuperAnnotatedBy) {
		this.outVarSuperAnnotatedBy = outVarSuperAnnotatedBy;
	}

	private boolean tokenCreated(ExecutionContext context, String documentId){
		boolean found = false;
		/*

		List<Token> existingTokens = context.getProcessInstance().findAllTokens();
		if(existingTokens!=null){
			log.debug("found existingTokens "+existingTokens.size());
		Iterator<Token> it = existingTokens.iterator();
		while(!found){
			Token token = it.next();
			if(token.getName()!=null && token.getName().startsWith(documentId)){
				found = true;
			}
		}
		}
		else {
			log.warn("existingTokens list IS NULL!");
		}
		*/
		// first find root token#
		if (context.getProcessInstance().findToken(documentId) != null){
			found = true;
		}

		return found;
	}
}
