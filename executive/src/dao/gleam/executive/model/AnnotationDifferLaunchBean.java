/*
 *  AnnotationDifferLaunchBean.java
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
public class AnnotationDifferLaunchBean {

	
		
	final static String  DEFAULT_METHOD = "searchAnnSetNames";
	final static String  DEFAULT_CORPUS_ID = "";
	final static String  DEFAULT_DOCUMENT_ID = "";
	final static String  DEFAULT_DOCUMENT_NAME = "";
	final static String  DEFAULT_SHOW = "false";
	
	String method;
	String corpusId;
	String documentId;
	String documentName;
	String show;
	
	

	public AnnotationDifferLaunchBean(){
		method = DEFAULT_METHOD;
		corpusId = DEFAULT_CORPUS_ID;
		documentId = DEFAULT_DOCUMENT_ID;
		documentName = DEFAULT_DOCUMENT_NAME;
		show = DEFAULT_SHOW;
	}
	
	
	/*
	 * method=searchAnnSetNames&corpusID={0}&documentID={1}&documentName={2}
	 */
	
	 public String toString() {
		    StringBuilder sb = new StringBuilder()
		            .append("?method=")
		            .append(method)
		            .append("&corpusID=")
		            .append(corpusId)
		            .append("&documentID=")
		            .append(documentId)
		            .append("&documentName=")
		            .append(documentName)
		            .append("&show=")
		            .append(show);
		    
		    return sb.toString();
  }
	
	public String getCorpusId() {
		return corpusId;
	}
	public void setCorpusId(String corpusId) {
		this.corpusId = corpusId;
	}
	public String getDocumentId() {
		return documentId;
	}
	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}
	public String getDocumentName() {
		return documentName;
	}
	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	
	public String getShow() {
		return show;
	}

	public void setShow(String show) {
		this.show = show;
	}
	
}
