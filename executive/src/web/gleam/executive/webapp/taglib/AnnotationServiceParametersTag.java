/*
 *  AnnotationServiceParametersTag.java
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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;
import org.springframework.web.context.support.WebApplicationContextUtils;

import gleam.executive.model.AnnotationServiceType;
import gleam.executive.service.AnnotationServiceManager;


/**
 *
 *  @author <a href="M.Agatonovic@dcs.shef.ac.uk">Milan Agatonovic</a>
 *
 * @jsp.tag name="annotationServiceParameters" bodycontent="JSP"
 *
 */

public class AnnotationServiceParametersTag extends BodyTagSupport {

	private static Log log = LogFactory.getLog(AnnotationServiceParametersTag.class);

	private String annotationServiceTypeName;
	

	public String getAnnotationServiceTypeName() {
		return annotationServiceTypeName;
	}

	/**
	 * @param name
	 *            The serviceType name to set.
	 *
	 * @jsp.attribute required="true" rtexprvalue="true"
	 */
	public void setAnnotationServiceTypeName(String annotationServiceTypeName) {
		this.annotationServiceTypeName = annotationServiceTypeName;
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
	      .getRequiredWebApplicationContext(pageContext.getServletContext());

		AnnotationServiceManager annotationServiceManager = (AnnotationServiceManager)ctx.getBean("annotationServiceManager");
	    AnnotationServiceType annotationServiceType = annotationServiceManager.getAnnotationServiceTypeByName(annotationServiceTypeName);
	    log.debug("analysing annotationServiceType: "+annotationServiceType.getName());
	    String parameterCSVList = annotationServiceType.getData();
	    log.debug("analysing parameterCSVList: "+parameterCSVList);
	    
	    String[] parameters = StringUtils.commaDelimitedListToStringArray(parameterCSVList);
	    // now iterate through array and generate 1 text area for each
		StringBuffer resultBuffer = new StringBuffer();
	
		for (int i = 0; i < parameters.length; i++) {
			resultBuffer.append("<li>");
			resultBuffer.append("<label for");
			resultBuffer.append("\"");
			resultBuffer.append(parameters[i]);
			resultBuffer.append("\" class=\"desc\">"); 
			resultBuffer.append(parameters[i]);
			resultBuffer.append("</label>");
			resultBuffer.append("<textarea name=");
			resultBuffer.append("\"");
			resultBuffer.append(parameters[i]);
			resultBuffer.append("\"");           
			resultBuffer.append("rows=\"3\" cols=\"40\">");
			String value = (String)pageContext.getRequest().getAttribute(parameters[i]);
			if(value!=null){
			  resultBuffer.append(value);
			}
			resultBuffer.append("</textarea>");
			resultBuffer.append("</li>");
		}
    

			pageContext.getOut().write(resultBuffer.toString());
		} catch (Exception e) {
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





	


}

