/*
 *  AnnotationDiffGUILaunchBean.java
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
package gleam.executive.model;


/**
 * @author agaton
 *
 */
public class AnnotationDiffGUILaunchBean {

	final static String DEFAULT_DOCSERVICE_URL = "";
	final static String DEFAULT_DOCUMENT_ID = "";
	final static String DEFAULT_AUTOCONNECT = "true";
	
	String docServiceURL;
	String documentId;
	String autoconnect;

	public AnnotationDiffGUILaunchBean(){
		docServiceURL = DEFAULT_DOCSERVICE_URL;
		documentId = DEFAULT_DOCUMENT_ID;
		autoconnect = DEFAULT_AUTOCONNECT;
   }


	public String getDocServiceURL() {
		return docServiceURL;
	}


	public void setDocServiceURL(String docServiceURL) {
		this.docServiceURL = docServiceURL;
	}


	public String getDocumentId() {
		return documentId;
	}


	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

	
	public String getAutoconnect() {
		return autoconnect;
	}


	public void setAutoconnect(String autoconnect) {
		this.autoconnect = autoconnect;
	}

	public String toString() {
		    StringBuilder sb = new StringBuilder()
		            .append("?docservice-url=")
		            .append(docServiceURL)
		            .append("&doc-id=")
		            .append(documentId)
		            .append("&autoconnect=")
		            .append(autoconnect);
		    
		    return sb.toString();
     }

}
