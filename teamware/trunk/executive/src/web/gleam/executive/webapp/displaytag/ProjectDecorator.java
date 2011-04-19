/*
 *  ProjectDecorator.java
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
