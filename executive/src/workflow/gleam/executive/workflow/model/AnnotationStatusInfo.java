/*
 *  AnnotationStatusInfo.java
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
package gleam.executive.workflow.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AnnotationStatusInfo implements Serializable{

	private static final long serialVersionUID = -2481118775260732517L;

	/*
	 * List of usernames, who are currently working on particular document
	 */
	private List<String> takenByList;

	/*
	 * List of usernames, who finished the particular document
	 */
	private List<String> annotatedByList;

	/*
	 * List of usernames, who rejected the particular document
	 */
	private List<String> rejectedByList;

	/*
	 * integer denoting how many times document must be annotated
	 */
	private int numberOfIterations;

	/*
	 * boolean flag denoting if annotator can annotate the same document only once
	 * true if unique
	 * false if not
	 */
	private boolean uniqueAnnotator;

	/*
	 * the date when document changed status from NOT_STARTED to IN_PROGRESS
	 */
    private Date startDate;

	/*
	 * the date when document changed status from IN_PROGRESS to FINISHED
	 */
	private Date endDate;

	/*
	 * The convenient status string denoting
	 * in which condition document is in the moment. Possible values are:
	 * - NOT_STARTED
	 * - IN_PROGRESS
	 * - ANNOTATED
	 * - FAILED: + generated error message
	 */
	private String status;
	
	private String documentId;
	
	private long timeWorkedOn;

	public List<String> getAnnotatedByList() {
		return annotatedByList;
	}

	public void setAnnotatedByList(List<String> annotatedByList) {
		this.annotatedByList = annotatedByList;
	}

	public List<String> getRejectedByList() {
		return rejectedByList;
	}

	public void setRejectedByList(List<String> rejectedByList) {
		this.rejectedByList = rejectedByList;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public int getNumberOfIterations() {
		return numberOfIterations;
	}

	public void setNumberOfIterations(int numberOfIterations) {
		this.numberOfIterations = numberOfIterations;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<String> getTakenByList() {
		return takenByList;
	}

	public void setTakenByList(List<String> takenByList) {
		this.takenByList = takenByList;
	}

	public boolean isUniqueAnnotator() {
		return uniqueAnnotator;
	}

	public void setUniqueAnnotator(boolean uniqueAnnotator) {
		this.uniqueAnnotator = uniqueAnnotator;
	}

	public String getDocumentId() {
		return documentId;
	}

	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}
	
	public long getTimeWorkedOn() {
		return timeWorkedOn;
	}

	public void setTimeWorkedOn(long timeWorkedOn) {
		this.timeWorkedOn = timeWorkedOn;
	}

	public static final String STATUS_NOT_STARTED = "Not Started";
	public static final String STATUS_IN_PROGRESS = "In Progress";
	public static final String STATUS_ANNOTATED = "Annotated";
	//public static final String STATUS_FINISHED = "Finished";
	public static final String STATUS_FAILED = "Failed";
	public static final String STATUS_CANCELED = "Canceled";
	public static final String STATUS_ABORTED = "Aborted";
	public static boolean DEFAULT_UNIQUE_ANNOTATOR = true;
	public static int DEFAULT_NUMBER_OF_ITERATIONS = 1;

	public AnnotationStatusInfo(String documentId) {
		this.documentId = documentId;
		this.takenByList = new ArrayList<String>();
		this.annotatedByList = new ArrayList<String>();
		this.rejectedByList = new ArrayList<String>();
		this.numberOfIterations = DEFAULT_NUMBER_OF_ITERATIONS;
		this.status = STATUS_NOT_STARTED;
		this.uniqueAnnotator = DEFAULT_UNIQUE_ANNOTATOR;
		this.timeWorkedOn = 0;
	}

	public AnnotationStatusInfo(String documentId, int numberOfIterations, boolean uniqueAnnotator) {
		this.documentId = documentId;
		this.takenByList = new ArrayList<String>();
		this.annotatedByList = new ArrayList<String>();
		this.rejectedByList = new ArrayList<String>();
		this.numberOfIterations = numberOfIterations;
		this.status = STATUS_NOT_STARTED;
		this.uniqueAnnotator = uniqueAnnotator;
		this.timeWorkedOn = 0;
	}

}
