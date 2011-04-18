package gleam.executive.webapp.taglib;

import gleam.executive.workflow.model.AnnotationMetricInfo;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;
import com.googlecode.charts4j.GCharts;
import com.googlecode.charts4j.PieChart;
import com.googlecode.charts4j.Slice;


/**
 * 
 * @author agaton
 * 
 * @jsp.tag name="googleChart" bodycontent="empty"
 * 
 */
public class GoogleChartTag extends TagSupport {

  protected final transient Log log = LogFactory.getLog(GoogleChartTag.class);
  
    private String title;

	private String chart;

	private String size;

	private Map metricMap;
	
	boolean enabled=true;


	/**
	 * @param size
	 *            The size in format x,y
	 * 
	 * @jsp.attribute required="false" rtexprvalue="true"
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}


	/**
	 * @param size
	 *            The size in format x,y
	 * 
	 * @jsp.attribute required="false" rtexprvalue="true"
	 */
	public void setSize(String size) {
		this.size = size;
	}

	
	/**
	 * @param metricMap
	 *           Map of params
	 * 
	 * @jsp.attribute required="true" rtexprvalue="true"
	 */
	public void setMetricMap(Map metricMap) {
		this.metricMap = metricMap;
	}

	/**
	 * @param title
	 *            The title to set.
	 * 
	 * @jsp.attribute required="true" rtexprvalue="true"
	 */
	public void setTitle(String title) {
		this.title = title;
	}


	/**
	 * Property used to detect the chart 
	 * 
	 * @param chart
	 * 
	 * @jsp.attribute required="true" rtexprvalue="true"
	 */
	public void setChart(String chart) {
		this.chart = chart;
	}

	
	/**
	 * Process the start of this tag.
	 * 
	 * @return int status
	 * 
	 * @exception JspException
	 *                if a JSP exception has occurred
	 * 
	 * @see javax.servlet.jsp.tagext.Tag#doStartTag()
	 */
	public int doStartTag() throws JspException {
	    String googleChartURL = "";
	    String result = "";
	    try{
	    String[] csvDimensions = StringUtils.commaDelimitedListToStringArray(this.size);
	    if(enabled){
	    // get google chart api url:
	    if("pie".equals(this.chart)){
	    	googleChartURL = generatePieChart(this.metricMap, this.title, csvDimensions);
	       
	    }
		StringBuffer resultBuffer = new StringBuffer();
		String link = "<img src=\"" 
				+ googleChartURL + "\" border=\"0\" />";

		resultBuffer.append(link);	
		result = resultBuffer.toString();
	    }
		pageContext.getOut().write(result);
	} catch (IOException io) {
		throw new JspException(io);
	}

	return super.doStartTag();
	}

	/**
	 * Release acquired resources to enable tag reusage.
	 * 
	 * @see javax.servlet.jsp.tagext.Tag#release()
	 */
	public void release() {
		super.release();
	}

	
	
	public String generatePieChart(Map metricMap, String title, String[] csvDimensions) {
		List<Slice> slices = new ArrayList<Slice>();
		Iterator<Map.Entry<String, AnnotationMetricInfo>> itr = metricMap.entrySet().iterator();
		Integer total = 0;
		while (itr.hasNext()) {
	        Map.Entry<String, AnnotationMetricInfo> entry = itr.next();
	        total = total + entry.getValue().getCount();
		}
		Iterator<Map.Entry<String, AnnotationMetricInfo>> it = metricMap.entrySet().iterator();
		// first calculate the total
		if(total>0){
		while (it.hasNext()) {
		        Map.Entry<String, AnnotationMetricInfo> entry = it.next();
		       if(entry.getKey()!=null){
		    	   Integer value = entry.getValue().getCount();
		    	   double percent = ((double)value/(double)total)*100.0;
		    	   //log.debug("add slice: "+entry.getKey() + " value: "+percent);
		    	   slices.add(Slice.newSlice((int)percent, entry.getKey()));
		       }
		}
		}
		else {
		   slices.add(Slice.newSlice(1, "N/A"));
		}
		PieChart chart = GCharts.newPieChart(slices);
	   
		chart.setTransparency(60);
		chart.setTitle(title);
	    chart.setSize(Integer.parseInt(csvDimensions[0]), Integer.parseInt(csvDimensions[1]));
	    chart.setThreeD(false);
		return chart.toURLString();
	}


}