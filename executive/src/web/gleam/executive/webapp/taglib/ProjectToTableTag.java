package gleam.executive.webapp.taglib;

import gleam.executive.model.AnnotationService;
import gleam.executive.model.Project;
import gleam.executive.service.AnnotationServiceManager;
import gleam.executive.service.ProjectManager;
import gleam.executive.util.XstreamUtil;
import gleam.executive.workflow.util.JPDLConstants;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * 
 * @author <a href="M.Agatonovic@dcs.shef.ac.uk">Milan Agatonovic</a>
 * 
 * @jsp.tag name="projectToTable" bodycontent="JSP"
 * 
 */

public class ProjectToTableTag extends BodyTagSupport {

	private static Log log = LogFactory.getLog(ProjectToTableTag.class);

	private static String[] skipKeys = { 
			"annotatorHasToBeUniqueForDocument", 
			"projectName", 
			"serviceId",
			"preProcessingServiceId",
			"postProcessingServiceId",
			"postManualServiceId",
			"projectDescription", 
			"doSetup", 
			"projectId", 
			"initiator" 
	};

	private String projectId;

	/**
	 * @param projectId
	 *            The id to set.
	 * 
	 * @jsp.attribute required="true" rtexprvalue="true"
	 */
	public void setProjectId(String projectId) {
		this.projectId = projectId;
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
		try {

			ApplicationContext ctx = WebApplicationContextUtils
					.getRequiredWebApplicationContext(pageContext
							.getServletContext());

			ProjectManager projectManager = (ProjectManager) ctx
					.getBean("projectManager");

			long projId = Long.parseLong(projectId);
			log.debug("projId: " + projId);
			Project project = projectManager.getProject(projId);
			log.debug("found project: " + project.getName());
			Map<String, Object> variableMap = XstreamUtil
					.fromByteArrayToMap(project.getData());
			StringBuilder sb = new StringBuilder();
			sb.append("<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			Iterator<Map.Entry<String, Object>> it = variableMap.entrySet()
					.iterator();
			while (it.hasNext()) {
				Map.Entry<String, Object> entry = it.next();
				String key = entry.getKey();
				String value = "";
				if (!isSkipKey(key)) {
					sb.append("<tr><td>");
					if (key.equals(JPDLConstants.PREPROCESSING_PIPELINE_CSV_LIST) ||
						key.equals(JPDLConstants.POSTPROCESSING_PIPELINE_CSV_LIST) ||
						key.equals(JPDLConstants.POSTMANUAL_PIPELINE_CSV_LIST)){
						key = key.substring(0, key.indexOf(JPDLConstants.PIPELINE_CSV_LIST));
						sb.append(formatKey(key));
						value = getGASNamesFromIds(entry.getValue()
								.toString());
						sb.append(value);
					} 
					else if(key.equals(JPDLConstants.PRE_MANUAL_SERVICE_ID)){
						sb.append(formatKey(key));
						value = getGASNamesFromIds(entry.getValue()
								.toString());
						sb.append(value);
					}
					else {
						sb.append(formatKey(key));
						value = formatValue(entry.getValue());
						sb.append(value);
					}

					sb.append("</td></tr>");
				}

			}
			sb.append("</table>");

			pageContext.getOut().write(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw new JspException(e);
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

	private boolean isSkipKey(String key) {
		boolean flag = false;
		for (int i = 0; i < skipKeys.length; i++) {
			if (skipKeys[i].equals(key)) {
				flag = true;
				break;
			}
		}
		return flag;
	}

	private String getGASNamesFromIds(String csvIds) throws Exception {
		ApplicationContext ctx = WebApplicationContextUtils
				.getRequiredWebApplicationContext(pageContext
						.getServletContext());

		log.debug("csvIds: " + csvIds);
		String[] ids = StringUtils.commaDelimitedListToStringArray(csvIds);
		AnnotationServiceManager annotationServiceManager = (AnnotationServiceManager) ctx
				.getBean("annotationServiceManager");

		List<String> annotationServices = new ArrayList<String>();

		for (int i = 0; i < ids.length; i++) {
                    String name = "None"; 
                    Long id = new Long(ids[i]);
                    if(id > 0){		
	                AnnotationService annotationService = annotationServiceManager
					.getAnnotationService(id);
			name = annotationService.getName();
                    } 
		    annotationServices.add(name);

		}

		return StringUtils.collectionToDelimitedString(annotationServices, ", ");
	}

	private String formatValue(Object value) throws Exception {
		StringBuffer sb = new StringBuffer("");
		if(value!=null){
		String[] tokens = StringUtils.commaDelimitedListToStringArray(value.toString());
        
		for (int i = 0; i < tokens.length; i++) {
			sb.append(" ");
			sb.append(tokens[i]);
        }
		}
		return sb.toString();
	}
	
	private String formatKey(String key) {
		StringBuffer sb = new StringBuffer("");
		if(key!=null){
			sb.append("<strong>");
			sb.append(key);
			sb.append("</strong>");
			sb.append(" ");
        }
		return sb.toString();
	}
	


}
