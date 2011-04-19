/*
 *  ForkAndInitDocumentFlowsActionHandler.java
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
package gleam.executive.workflow.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.Token;

import gleam.executive.model.Document;
import gleam.executive.service.DocServiceManager;
import gleam.executive.workflow.util.JPDLConstants;
import gleam.executive.workflow.util.WorkflowUtil;
import gleam.executive.workflow.model.AnnotationStatusInfo;
import gleam.executive.workflow.sm.JbpmDataflowHandlerProxy;

public class ForkAndInitDocumentFlowsActionHandler extends
		JbpmDataflowHandlerProxy {

	private static final long serialVersionUID = 1L;

	protected final Log log = LogFactory.getLog(getClass());

	private Element targetProperties;

	// beans
	DocServiceManager docServiceManager;

	/*
	 * <property name="mode" type="in" scope="global" empty="true"></property>
	 * <property name="corpusId" type="in" scope="global"
	 * empty="false"></property> <property name="documentId" type="out"
	 * scope="local" empty="false"></property>
	 */

	/* (non-Javadoc)
	 * @see gleam.executive.workflow.sm.JbpmDataflowHandlerProxy#execute(org.jbpm.graph.exe.ExecutionContext)
	 */
	public void execute(ExecutionContext context) throws Exception {
		log.debug("@@@@@ ForkAndInitDocumentFlowsActionHandler START");

		Map<String, String> variableMap = fetchAndValidateVariables(context,
				this.getClass().getName(), targetProperties);

		Token token = context.getToken();
		Map<String, Object> map = new HashMap<String, Object>();

		// obtain targetProperties
		String mode = (String) variableMap.get(JPDLConstants.MODE);
		String corpusId = (String) variableMap.get(JPDLConstants.CORPUS_ID);

		log.debug("@@@@@@@ mode = " + mode + "; corpusId = " + corpusId);

		long processInstanceId = context.getProcessInstance().getId();
		log.debug("current ProcessInstance ID " + processInstanceId);

		String processDefinitionName = context.getProcessInstance()
				.getProcessDefinition().getName();
		log.debug("current ProcessDefinition Name " + processDefinitionName);

		List<String> documentList = new ArrayList<String>();

		if (!JPDLConstants.TEST_MODE.equals(mode)) {
			List<Document> documents = docServiceManager
					.listDocuments(corpusId);
			log.debug("found documents " + documents.size());

			Iterator<Document> it = documents.iterator();

			while (it.hasNext()) {
				Document doc = it.next();
				documentList.add(doc.getDocumentID());
			}
		} else {
			documentList.add("doc1");
			documentList.add("doc2");
		}

		int numberOfDocuments = documentList.size();
		log.debug("numberOfDocuments: " + numberOfDocuments);

		final Token rootToken = context.getToken();
		log.debug("@@@@@@@ root token " + rootToken.getName());
		final Node node = context.getNode();
		log.debug("@@@@@@@ node " + node.getName());

		if (numberOfDocuments > 0) {
			// if (numberOfDocuments > 1) {
			for (int i = 0; i < numberOfDocuments; i++) {
				String documentId = documentList.get(i);
				// build a new token name
				String tokenName = JPDLConstants.DOCUMENT_PREFIX + documentId;
				final Token newToken = new Token(rootToken, tokenName);
				/*
				log.debug("@@@@@@@ created new token ID " + newToken.getId()
						+ "   NAME: " + newToken.getName());
				*/
				newToken.setTerminationImplicit(true);
				context.getJbpmContext().getSession().save(newToken);
				final ExecutionContext newExecutionContext = new ExecutionContext(
						newToken);
				// create new documentId variable
                /*
				log.debug("@@@@@@@ created variable " + documentId
						+ " for token " + newToken.getName());
				*/
				map.put(JPDLConstants.DOCUMENT_ID, documentId);

				String statusVariableName = WorkflowUtil
						.createAnnotationStatusVariableName(
								processDefinitionName, processInstanceId,
								documentId);
				AnnotationStatusInfo annotationStatusInfo = new AnnotationStatusInfo(
						documentId);
				/*
				log.debug("Create annotation status info under name '"
						+ statusVariableName + "'");
				*/
				map.put(statusVariableName, annotationStatusInfo);

				newExecutionContext.getJbpmContext().getSession()
						.save(newToken);

				submitTokenVariables(context, this.getClass().getName(),
						targetProperties, map, token);

				node.leave(newExecutionContext);
				//log.debug("NODE LEFT");

			}
			/*
			 * } else { // do not fork log.debug("do not fork"); String
			 * documentId = documentList.get(0);
			 * context.setVariable(JPDLConstants.DOCUMENT_ID, documentId);
			 * String statusVariableName =
			 * WorkflowUtil.createAnnotationStatusVariableName
			 * (processDefinitionName, processInstanceId, documentId);
			 * AnnotationStatusInfo annotationStatusInfo = new
			 * AnnotationStatusInfo(documentId);
			 * context.getContextInstance().createVariable(statusVariableName,
			 * annotationStatusInfo);
			 * log.debug("Create annotation status info under name '" +
			 * statusVariableName + "'" ); node.leave(context);
			 * log.debug("NODE LEFT"); }
			 */
		} else {
			log.debug("corpus is empty");
			node.leave(context, JPDLConstants.TRANSITION_ERROR);
		}

		log.debug("@@@@ ForkAndInitDocumentFlowsActionHandler END");
	}

	public Element getTargetProperties() {
		return targetProperties;
	}

	public void setTargetProperties(Element targetProperties) {
		this.targetProperties = targetProperties;
	}

	public DocServiceManager getDocServiceManager() {
		return docServiceManager;
	}

	public void setDocServiceManager(DocServiceManager docServiceManager) {
		this.docServiceManager = docServiceManager;
	}

}
