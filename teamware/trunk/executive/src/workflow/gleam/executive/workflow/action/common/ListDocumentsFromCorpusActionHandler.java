/*
 *  ListDocumentsFromCorpusActionHandler.java
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
import java.util.Map;

import gleam.executive.model.Document;
import gleam.executive.service.DocServiceManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.jbpm.graph.exe.ExecutionContext;
import org.springframework.util.StringUtils;
import gleam.executive.workflow.util.JPDLConstants;
import gleam.executive.workflow.sm.JbpmDataflowHandlerProxy;


public class ListDocumentsFromCorpusActionHandler extends
		JbpmDataflowHandlerProxy {

	private static final long serialVersionUID = 1L;

	protected final Log log = LogFactory.getLog(getClass());

	private Element targetProperties;

	/*
	 * <property name="mode" type="in" scope="global" empty="true"></property>
	 * <property name="corpusId" type="in" scope="global" empty="false"></property>
	 * <property name="documentCSVList" type="out" scope="global" empty="true"></property>
	 */


	// beans
	DocServiceManager docServiceManager;

	public void execute(ExecutionContext context) throws Exception {
		log.debug("@@@@@ ListDocumentsFromCorpusActionHandler START");
		Map<String, String> variableMap = fetchAndValidateVariables(context,
                this.getClass().getName(),
                targetProperties);

		// obtain targetProperties
	    String mode = (String) variableMap.get(JPDLConstants.MODE);
		String corpusId = (String) variableMap.get(JPDLConstants.CORPUS_ID);

		log.debug("@@@@@@@ mode = " + mode + "; corpusId = " + corpusId);

		List<String> documentList = new ArrayList<String>();

		if(!JPDLConstants.TEST_MODE.equals(mode)){
			List<Document> documents = docServiceManager.listDocuments(corpusId);
			log.debug("found documents " + documents.size());

			Iterator<Document> it = documents.iterator();

			while (it.hasNext()) {
				Document doc = it.next();
				documentList.add(doc.getDocumentID());
			}
		}
		else {
			documentList.add("doc1");
			documentList.add("doc2");
		}

		String documentCSVList = StringUtils.collectionToCommaDelimitedString(documentList);
		log.debug("documentCSVList "+documentCSVList);
		context.setVariable(JPDLConstants.DOCUMENT_CSV_LIST, documentCSVList);
        log.debug("@@@@ ListDocumentsFromCorpusActionHandler END");
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


