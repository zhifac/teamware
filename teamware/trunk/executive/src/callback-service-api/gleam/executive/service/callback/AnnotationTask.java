/*
 *  AnnotationTask.java
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
package gleam.executive.service.callback;

import java.io.Serializable;
import java.util.Calendar;
import java.net.URI;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;


public class AnnotationTask implements Serializable{


	private static final long serialVersionUID = -4314590877881012996L;

	private String tokenId;

	private Long taskInstanceId;

	private String taskName;

	private String performer;

	private Calendar startDate;

	private Calendar dueDate;
	
	private Calendar lastOpenedDate;

	private String documentId;

	private String annotationSetName;

	private URI docserviceURL;

	private URI owlimServiceURL;

	private String owlimRepositoryName;

	private URI ontologyLocation;

	private String annotationSchemaCSVURLs = "";

	private String pluginCSVList;
	
	private boolean cancelAllowed;


	public Long getTaskInstanceId() {
		return taskInstanceId;
	}

	public void setTaskInstanceId(Long taskInstanceId) {
		this.taskInstanceId = taskInstanceId;
	}

	public URI getDocserviceURL() {
		return docserviceURL;
	}

	public void setDocserviceURL(URI docserviceURL) {
		this.docserviceURL = docserviceURL;
	}

	public String getDocumentId() {
		return documentId;
	}

	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

	public Calendar getDueDate() {
		return dueDate;
	}

	public void setDueDate(Calendar dueDate) {
		this.dueDate = dueDate;
	}

	public Calendar getLastOpenedDate() {
		return lastOpenedDate;
	}

	public void setLastOpenedDate(Calendar lastOpenedDate) {
		this.lastOpenedDate = lastOpenedDate;
	}

	public String getPerformer() {
		return performer;
	}

	public void setPerformer(String performer) {
		this.performer = performer;
	}

	public Calendar getStartDate() {
		return startDate;
	}

	public void setStartDate(Calendar startDate) {
		this.startDate = startDate;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getTokenId() {
		return tokenId;
	}

	public void setTokenId(String tokenId) {
		this.tokenId = tokenId;
	}

	public String getAnnotationSchemaCSVURLs() {
		return annotationSchemaCSVURLs;
	}

	public void setAnnotationSchemaCSVURLs(String annotationSchemaCSVURLs) {
		this.annotationSchemaCSVURLs = annotationSchemaCSVURLs;
	}

	public URI getOntologyLocation() {
		return ontologyLocation;
	}

	public void setOntologyLocation(URI ontologyLocation) {
		this.ontologyLocation = ontologyLocation;
	}

	public String getOwlimRepositoryName() {
		return owlimRepositoryName;
	}

	public void setOwlimRepositoryName(String owlimRepositoryName) {
		this.owlimRepositoryName = owlimRepositoryName;
	}

	public URI getOwlimServiceURL() {
		return owlimServiceURL;
	}

	public void setOwlimServiceURL(URI owlimServiceURL) {
		this.owlimServiceURL = owlimServiceURL;
	}

	public String getPluginCSVList() {
		return pluginCSVList;
	}

	public void setPluginCSVList(String pluginCSVList) {
		this.pluginCSVList = pluginCSVList;
	}
	
	public boolean isCancelAllowed() {
	  return cancelAllowed;
	}
	
	public void setCancelAllowed(boolean cancelAllowed) {
	  this.cancelAllowed = cancelAllowed;
	}

	 public String toString() {
		    return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
		        .append("taskInstanceId", this.taskInstanceId)
		        .append("tokenId", this.tokenId)
		        .append("taskName", this.taskName)
		        .append("performer", this.performer)
		        .append("annotationSetName", this.annotationSetName)
		        .append("startDate", this.startDate)
		        .append("dueDate", this.dueDate)
		        .append("documentId", this.documentId)
		        .append("docserviceURL", this.docserviceURL)
		        .append("owlimServiceURL", this.owlimServiceURL)
		        .append("owlimRepositoryName", this.owlimRepositoryName)
		        .append("ontologyLocation", this.ontologyLocation)
		        .append("pluginCSVList", this.pluginCSVList)
		        .append("cancelAllowed", this.cancelAllowed)
		        .toString();
		  }

	public String getAnnotationSetName() {
		return annotationSetName;
	}

	public void setAnnotationSetName(String annotationSetName) {
		this.annotationSetName = annotationSetName;
	}


}
