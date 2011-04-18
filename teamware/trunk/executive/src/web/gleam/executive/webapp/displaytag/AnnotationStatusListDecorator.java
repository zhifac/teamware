package gleam.executive.webapp.displaytag;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import gleam.executive.util.DateUtil;
import gleam.executive.util.GATEUtil;
import gleam.executive.util.StringUtil;
import gleam.executive.workflow.model.AnnotationStatusInfo;
import gleam.executive.workflow.util.JPDLConstants;
import gleam.executive.workflow.util.WorkflowUtil;
import org.displaytag.decorator.TotalTableDecorator;
import org.springframework.util.StringUtils;

/**
 *A custom list wrapper that override the TableDecorator Copyright (c)
 * 1998-2007, The University of Sheffield.
 * 
 * This file is part of GATE (see http://gate.ac.uk/), and is free software,
 * licenced under the GNU Library General Public License, Version 2, June 1991
 * (in the distribution as file licence.html, and also available at
 * http://gate.ac.uk/gate/licence.html).
 * 
 * @author <a href="mailto:agaton@dcs.shef.ac.uk">Milan Agatonovic</a>
 * 
 */
public class AnnotationStatusListDecorator extends TotalTableDecorator {

	String startDateFormatted;

	String endDateFormatted;

	String takenByFormatted;

	String annotatedByFormatted;

	String rejectedByFormatted;
	
	String takenByFormattedWithoutLinks;

	String annotatedByFormattedWithoutLinks;

	String rejectedByFormattedWithoutLinks;
	
	String time;
	
	String documentName;

	public String getDocumentName() {
		AnnotationStatusInfo annotationStatusInfo = (AnnotationStatusInfo) this.getCurrentRowObject();
		String documentId= annotationStatusInfo.getDocumentId();
		return GATEUtil.extractNameOfGATEEntity(documentId);
	}

	/**
	 * Creates a new Wrapper decorator who's job is to reformat some of the data
	 * located in our forms.
	 */

	public AnnotationStatusListDecorator() {
		super();
	}

	public String getStartDateFormatted() {
		AnnotationStatusInfo annotationStatusInfo = (AnnotationStatusInfo) this.getCurrentRowObject();
		return StringUtil.formatDate(annotationStatusInfo.getStartDate());
	}

	public String getEndDateFormatted() {
		AnnotationStatusInfo annotationStatusInfo = (AnnotationStatusInfo) this.getCurrentRowObject();
		return StringUtil.formatDate(annotationStatusInfo.getEndDate());
	}

	public String getTakenByFormatted() {
		AnnotationStatusInfo annotationStatusInfo = (AnnotationStatusInfo) this.getCurrentRowObject();
		String result = WorkflowUtil.collectionToFormattedCSVString(((HttpServletRequest)getPageContext().getRequest()).getContextPath(), annotationStatusInfo.getTakenByList(), JPDLConstants.ANNOTATOR_RECORD_LINK_FORMAT, getPageContext().getRequest().getParameter("id"));
		return result;
	}

	public String getAnnotatedByFormatted() {
		AnnotationStatusInfo annotationStatusInfo = (AnnotationStatusInfo) this.getCurrentRowObject();
		String result = WorkflowUtil.collectionToFormattedCSVString(((HttpServletRequest)getPageContext().getRequest()).getContextPath(),annotationStatusInfo.getAnnotatedByList(), JPDLConstants.ANNOTATOR_RECORD_LINK_FORMAT, getPageContext().getRequest().getParameter("id"));
		return result;
  }

	public String getRejectedByFormatted() {
		AnnotationStatusInfo annotationStatusInfo = (AnnotationStatusInfo) this.getCurrentRowObject();
		String result = WorkflowUtil.collectionToFormattedCSVString(((HttpServletRequest)getPageContext().getRequest()).getContextPath(),annotationStatusInfo.getRejectedByList(), JPDLConstants.ANNOTATOR_RECORD_LINK_FORMAT, getPageContext().getRequest().getParameter("id"));
		return result;
	}
	
	
	public String getTakenByFormattedWithoutLinks() {
		AnnotationStatusInfo annotationStatusInfo = (AnnotationStatusInfo) this.getCurrentRowObject();
		return StringUtils.collectionToCommaDelimitedString(annotationStatusInfo.getTakenByList());
	}

	public String getAnnotatedByFormattedWithoutLinks() {
		AnnotationStatusInfo annotationStatusInfo = (AnnotationStatusInfo) this.getCurrentRowObject();
		return StringUtils.collectionToCommaDelimitedString(annotationStatusInfo.getAnnotatedByList());
    }

	public String getRejectedByFormattedWithoutLinks() {
		AnnotationStatusInfo annotationStatusInfo = (AnnotationStatusInfo) this.getCurrentRowObject();
		return StringUtils.collectionToCommaDelimitedString(annotationStatusInfo.getRejectedByList());
	}
	
	public String getTime() {
		AnnotationStatusInfo annotationStatusInfo = (AnnotationStatusInfo) this.getCurrentRowObject();
		String time = "N/A"; 
		long executionTime = annotationStatusInfo.getTimeWorkedOn();
		time = DateUtil.getElapsedTimeHoursMinutesSecondsString(executionTime);
	    return time;
	}
	
	public String addRowClass() {
		  String className = "";
		  AnnotationStatusInfo annotationStatusInfo = (AnnotationStatusInfo) this.getCurrentRowObject();

		  if(annotationStatusInfo.getStatus().equals(AnnotationStatusInfo.STATUS_ANNOTATED)){
			  className = "done";
		  }
	      else  if(annotationStatusInfo.getStatus().equals(AnnotationStatusInfo.STATUS_FAILED)){
			  className = "error";
		  }
	      else  if(annotationStatusInfo.getStatus().equals(AnnotationStatusInfo.STATUS_IN_PROGRESS)){
			  className = "progress";
		  }
	      else  if(annotationStatusInfo.getStatus().equals(AnnotationStatusInfo.STATUS_ABORTED)){
			  className = "aborted";
		  }
	    	  
	      return className;
	  }
	
}
