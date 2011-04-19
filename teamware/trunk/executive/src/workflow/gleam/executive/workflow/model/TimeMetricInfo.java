/*
 *  TimeMetricInfo.java
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

public class TimeMetricInfo {

	private String totalTime;

    private String averageTime;

	
	public TimeMetricInfo() {
		this.totalTime = JPDLConstants.INIT_TIME;
		this.averageTime = JPDLConstants.INIT_TIME;
	}

	public TimeMetricInfo(String totalTime, String averageTime) {
		this.totalTime = totalTime;
		this.averageTime = averageTime;
	}

	public String getTotalTime() {
		return totalTime;
	}

	public void setTotalTime(String totalTime) {
		this.totalTime = totalTime;
	}

	public String getAverageTime() {
		return averageTime;
	}

	public void setAverageTime(String averageTime) {
		this.averageTime = averageTime;
	}
	
}
