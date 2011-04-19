/*
 *  CustomLinkTag.java
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
package gleam.executive.webapp.taglib;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;
import org.springframework.web.context.support.WebApplicationContextUtils;

import gleam.executive.model.AnnotationSchema;
import gleam.executive.model.AnnotationService;
import gleam.executive.model.Corpus;
import gleam.executive.model.LabelValue;
import gleam.executive.model.User;
import gleam.executive.model.WebAppBean;
import gleam.executive.service.AnnotationServiceManager;
import gleam.executive.service.DocServiceManager;
import gleam.executive.service.GateServiceManager;
import gleam.executive.service.SafeManagerException;
import gleam.executive.service.UserManager;
import gleam.executive.workflow.manager.WorkflowManager;
import gleam.gateservice.client.GateServiceClientException;

/**
 * 
 * @author <a href="M.Agatonovic@dcs.shef.ac.uk">Milan Agatonovic</a>
 * 
 * @jsp.tag name="customLink" bodycontent="JSP"
 * 
 */

public class CustomLinkTag extends BodyTagSupport {

	private static Log log = LogFactory.getLog(CustomLinkTag.class);

	private String name;
	
	private String path;

	private String params;

	private String task;
	
	/**
	 * @param name
	 *            The path to set.
	 *
	 * @jsp.attribute required="true" rtexprvalue="true"
	 */
	public void setPath(String path) {
		this.path = path;
	}
	
	/**
	 * Property used to pass taskInstanceId to the tag
	 * 
	 * @param method
	 * 
	 * @jsp.attribute required="true" rtexprvalue="true"
	 */
	public void setTask(String task) {
		this.task = task;
	}

	
	/**
	 * @param params
	 *            Comma separated param. values
	 * 
	 * @jsp.attribute required="false" rtexprvalue="true"
	 */
	public void setParams(String params) {
		this.params = params;
	}

	/**
	 * @param name
	 *            The name to set.
	 * 
	 * @jsp.attribute required="true" rtexprvalue="true"
	 */
	public void setName(String name) {
		this.name = name;
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

		// Locale userLocale = pageContext.getRequest().getLocale();
		String[] parameters = null;
		String[] parameterNames = null;
		String[] parameterValues = null;
		String parameterString = "";

		if (this.params != null && !this.params.equals("")) {
			parameters = params.split(";");
			parameterNames = new String[parameters.length];
			parameterValues = new String[parameters.length];

		}
		

		ApplicationContext ctx = WebApplicationContextUtils
				.getRequiredWebApplicationContext(pageContext
						.getServletContext());
		WorkflowManager workflowManager = (WorkflowManager) ctx
				.getBean("workflowManager");


		try {
            StringBuilder parameterStringBuffer = new StringBuilder("");
			// Fetch the method
			if (parameters != null && parameters.length != 0) {
				for (int i = 0; i < parameters.length; i++) {
					// split parameter to name and value;
					// parameter comes in format name:value
					String [] nameValueArray = parameters[i].split(":");
					parameterNames[i] = nameValueArray[0];
					parameterValues[i] = nameValueArray[1];
					log.debug("parameterNames[" +i + "]="+ parameterNames[i] +"; parameterValues[" + i + "]="+parameterValues[i]);
					if (workflowManager.findVariable(new Long(task),
							parameterValues[i]) != null) {
						parameterValues[i] = workflowManager.findVariable(
								new Long(task), parameterValues[i]).toString();
						log.debug("found variable: NAME: " + parameterValues[i]
								+ ", VALUE: " + parameterValues[i]);
					
					} else {
						log.debug("NOT found variable: NAME: " + parameterValues[i]
								+ " Treat is as string constant");
					}      
					
					parameterStringBuffer.append(parameterNames[i]);
					parameterStringBuffer.append("=");
					parameterStringBuffer.append(parameterValues[i]);
					parameterStringBuffer.append("&");
				}
			    parameterString = parameterStringBuffer.toString();
				if(parameterString.endsWith("&")){
					parameterString = parameterString.substring(0, parameterString.length()-1);
				}
					log.debug("constructed parameterString " + parameterString);
			}
				
		} catch (Exception e) {
			e.printStackTrace();
			throw new JspException(e);
		}

		
		 WebAppBean webAppBean = (WebAppBean)ctx.getBean("webAppBean");
		    String url = webAppBean.getUrlBase() + "/" + webAppBean.getName()  + this.path + "?" + parameterString;
		    log.debug("constructed url: "+ url);
			StringBuffer resultBuffer = new StringBuffer();
			String link = "<a title=\""+ this.name + "\" href=\""+url + "\">" + this.name + "</a>";
			log.debug("constructed link: "+ link);
			resultBuffer.append(link);		
			
			try {
				pageContext.getOut().write(resultBuffer.toString());
			} catch (IOException io) {
				throw new JspException(io);
			}
			

			return super.doStartTag();
	}

	/**
	 * Release aquired resources to enable tag reusage.
	 * 
	 * @see javax.servlet.jsp.tagext.Tag#release()
	 */
	public void release() {
		super.release();
	}



}
