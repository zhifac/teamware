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
