package gleam.executive.webapp.taglib;

import java.io.IOException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author <a href="M.Agatonovic@dcs.shef.ac.uk">Milan Agatonovic</a>
 * 
 * @jsp.tag name="customCheckbox" bodycontent="JSP"
 * 
 */

public class CustomCheckboxTag extends BodyTagSupport {

	private static Log log = LogFactory.getLog(CustomCheckboxTag.class);
	private static final long serialVersionUID = 2004095567803546495L;

	private String name;

	private String value;

	private String clazz;

	private boolean readOnly;


	
	/**
	 * @param clazz
	 *            The class to set.
	 * 
	 * @jsp.attribute required="false" rtexprvalue="true"
	 */
	public void setClazz(String clazz) {
		this.clazz = clazz;
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
	 * @param value
	 *            The value option.
	 * @jsp.attribute required="false" rtexprvalue="true"
	 */
	public void setValue(String value) {
		this.value = value;
	}

	
	/**
	 * @param readOnly
	 *            The readOnly option.
	 * @jsp.attribute required="false" rtexprvalue="true"
	 */
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
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

		String isDisabled = "";
		String isChecked = "";

		
		StringBuffer resultBuffer = new StringBuffer();
		
		
		if(this.readOnly){
			isDisabled = "disabled";
		}
		
		if("on".equals(value)){
			isChecked = "checked";
		}
		resultBuffer.append("<input type=\"checkbox\" name=\"" + name + "\" id=\"" + name
				+ "\" " + isChecked + " " + isDisabled + "/>\n");

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
