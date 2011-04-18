package gleam.executive.workflow.model;

public class AnnotationMetricInfo {

	
	
	private Integer count;
	
	private TimeMetricInfo timeMetricInfo;
	
	public TimeMetricInfo getTimeMetricInfo() {
		return timeMetricInfo;
	}

	public void setTimeMetricInfo(TimeMetricInfo timeMetricInfo) {
		this.timeMetricInfo = timeMetricInfo;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}
	
	
	public AnnotationMetricInfo(Integer count){
		this.count=count;
        this.timeMetricInfo = new TimeMetricInfo();
	}
	
	public AnnotationMetricInfo(){
		this.count=0;
        this.timeMetricInfo = new TimeMetricInfo();
	}
}
