/*
 *  CustomPopupTag.java
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

import gleam.executive.model.WebAppBean;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 *
 *  @author <a href="M.Agatonovic@dcs.shef.ac.uk">Milan Agatonovic</a>
 *
 * @jsp.tag name="customPopup" bodycontent="JSP"
 *
 */

public class CustomPopupTag extends BodyTagSupport {

	private static Log log = LogFactory.getLog(CustomPopupTag.class);

	private String name;
	
	private String path;
	

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

		
		 ApplicationContext ctx = WebApplicationContextUtils
	      .getRequiredWebApplicationContext(pageContext.getServletContext());
	    
	    WebAppBean webAppBean = (WebAppBean)ctx.getBean("webAppBean");
	    String url = webAppBean.getUrlBase() + "/" + webAppBean.getName()  + this.path + "&popup=true";
	    log.debug("constructed url: "+ url);
		StringBuffer resultBuffer = new StringBuffer();
		String link = "<a title=\"[Open popup window]\" onclick=\"popupBox('" 
				+ url + "')\"  href=\"#\">" + this.name + "</a>";

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
	 * Release acquired resources to enable tag reusage.
	 *
	 * @see javax.servlet.jsp.tagext.Tag#release()
	 */
	public void release() {
		super.release();
	}





	


}

