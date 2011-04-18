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

