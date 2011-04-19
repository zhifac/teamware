/*
 *  AnnotationMetricMatrix.java
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



import gleam.executive.workflow.util.JPDLConstants;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class AnnotationMetricMatrix {

	private SortedMap<String, AnnotationMetricInfo> metricMap;

	private TimeMetricInfo timeMetricInfo;

	private Integer totalNumber;
	
	private String initiator;
	
	private String annotationSchemaCSVList;



	public TimeMetricInfo getTimeMetricInfo() {
		return timeMetricInfo;
	}

    public void setTimeMetricInfo(TimeMetricInfo timeMetricInfo) {
		this.timeMetricInfo = timeMetricInfo;
	}
	
	public SortedMap<String, AnnotationMetricInfo> getMetricMap() {
		return metricMap;
	}

	public void setMetricMap(SortedMap<String, AnnotationMetricInfo> metricMap) {
		this.metricMap = metricMap;
	}
	
	public String getInitiator() {
		return initiator;
	}

	public void setInitiator(String initiator) {
		this.initiator = initiator;
	}



	public Integer getTotalNumber() {
		return totalNumber;
	}



	public void setTotalNumber(Integer totalNumber) {
		this.totalNumber = totalNumber;
	}


	
	public String getAnnotationSchemaCSVList() {
		return annotationSchemaCSVList;
	}


	public void setAnnotationSchemaCSVList(String annotationSchemaCSVList) {
		this.annotationSchemaCSVList = annotationSchemaCSVList;
	}

	

	public AnnotationMetricMatrix(Collection<String> users) {
		super();
		this.metricMap = new TreeMap<String, AnnotationMetricInfo>();
		Iterator<String> it = users.iterator();
		while(it.hasNext()){
			AnnotationMetricInfo annotationMetric = new AnnotationMetricInfo();
			this.metricMap.put(it.next(), annotationMetric);
		}
	}

	
	public AnnotationMetricMatrix() {
		this.metricMap = new TreeMap<String, AnnotationMetricInfo>();
		this.metricMap.put(AnnotationStatusInfo.STATUS_ANNOTATED, new AnnotationMetricInfo());
		this.metricMap.put(AnnotationStatusInfo.STATUS_FAILED, new AnnotationMetricInfo());
		this.metricMap.put(AnnotationStatusInfo.STATUS_IN_PROGRESS, new AnnotationMetricInfo());
		this.metricMap.put(AnnotationStatusInfo.STATUS_NOT_STARTED, new AnnotationMetricInfo());
		this.metricMap.put(AnnotationStatusInfo.STATUS_CANCELED, new AnnotationMetricInfo());
	    this.timeMetricInfo = new TimeMetricInfo();
	    this.totalNumber=0;
	}
	
	public String toString(){
		
		ToStringBuilder tsb = new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE);
		if(this.timeMetricInfo!=null){
		  tsb.append("totalTime", this.timeMetricInfo.getTotalTime());
		  tsb.append("globalAverageTime", this.timeMetricInfo.getAverageTime());
		}
		tsb.append("totalNumber", this.totalNumber);
		Iterator<Map.Entry<String, AnnotationMetricInfo>> it = metricMap.entrySet().iterator();
		while (it.hasNext()) {
		       Map.Entry<String, AnnotationMetricInfo> entry = it.next();
		       tsb.append(entry.getKey(), entry.getValue().getCount());
		}
		return tsb.toString();
	}
	
}
