package gleam.executive.webapp.displaytag;

import gleam.executive.model.Project;
import gleam.executive.workflow.util.JPDLConstants;
import gleam.executive.workflow.util.WorkflowUtil;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.displaytag.decorator.TotalTableDecorator;

public class ProjectDecorator extends TotalTableDecorator {

	private static Log log = LogFactory.getLog(ProjectDecorator.class);

	String link;

	/**
	 * Creates a new Wrapper decorator who's job is to reformat some of the data
	 * located in our forms.
	 */

	public ProjectDecorator() {
		super();
	}

	public String getLink() {
		String result = "";
		HttpServletRequest request = (HttpServletRequest) getPageContext()
				.getRequest();
		Project project = (Project) this.getCurrentRowObject();
		Long projectId = project.getId();
		// check what to display, depending on version and status
		List<Integer> versions = (List<Integer>) request
				.getAttribute("versions");
		// check version
		if (versions.contains(project.getVersion())) {
			if (project.isEnabled()) {
				result = formatStartProcessFromProjectLink(projectId);
			} else if(request.getRemoteUser().equals(project.getUser().getUsername())){
				result = formatResumeProjectConfigurationLink(projectId);
			}
			else {
				result = "";
			}

		} else {
			// log.debug("Disable template, cause there is no matching process
			// definition version");
			result = formatDisableLink();
		}

		return result;
	}

	private String formatDisableLink() {
		String prefix = ((HttpServletRequest) getPageContext().getRequest())
				.getContextPath();
		return "<img class=\"icon\" src=\"" + prefix
				+ "/images/suspend.gif\"/>";
	}

	private String formatStartProcessFromProjectLink(Long projectId) {
		String prefix = ((HttpServletRequest) getPageContext().getRequest())
				.getContextPath();
		String[] params = { String.valueOf(projectId) };
		return "<a onmouseover=\"ajax_showTooltip('ajaxtooltip/info/createProcessInstanceBtn.jsp',this);return false\" onmouseout=\"ajax_hideTooltip()\" href=\""
				+ prefix
				+ WorkflowUtil.messageFormatter(
						JPDLConstants.START_PROCESS_FROM_PROJECT_LINK_FORMAT,
						params)
				+ "\"><img class=\"icon\" src=\""
				+ prefix
				+ "/images/start.gif\"/></a>";
	}

	private String formatResumeProjectConfigurationLink(Long projectId) {
		String prefix = ((HttpServletRequest) getPageContext().getRequest())
				.getContextPath();
		String[] params = { String.valueOf(projectId) };
		return "<a onmouseover=\"ajax_showTooltip('ajaxtooltip/info/resumeProject.jsp',this);return false\" onmouseout=\"ajax_hideTooltip()\" href=\""
				+ prefix
				+ WorkflowUtil.messageFormatter(
						JPDLConstants.RESUME_PROJECT_LINK_FORMAT, params)
				+ "\"><img class=\"icon\" src=\""
				+ prefix
				+ "/images/resume.gif\"/></a>";
	}

}
