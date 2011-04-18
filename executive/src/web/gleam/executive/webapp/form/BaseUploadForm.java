package gleam.executive.webapp.form;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.upload.FormFile;
import org.apache.struts.upload.MultipartRequestHandler;

/**
 * <p>
 * 	Serves as a base form class for handling file upload.
 * </p>
 * <p>
 * 	Copyright &copy; 1998-2010, The University of Sheffield. <br />
 * </p>
 * <p>
 * 	This file is part of <a href="http://gate.ac.uk/">GATE</a> and is free software,
 * 	licenced under the GNU Library General Public License, Version 2, June 1991
 * 	(in the distribution as file <code>licence.html</code>, and also available at 
 * 	<a href="http://gate.ac.uk/gate/licence.html">http://gate.ac.uk/gate/licence.html</a>).
 * </p>
 * 
 * @author <a href="mailto:ivaylo.kabakov@ontotext.com">Ivaylo Kabakov</a>
 */
public class BaseUploadForm extends BaseForm implements java.io.Serializable {

	public static final String ERROR_PROPERTY_MAX_LENGTH_EXCEEDED = "MaxLengthExceeded";

	private org.apache.struts.upload.FormFile file;
	
  /** Default empty constructor. */
  public BaseUploadForm() {
  }

  /**
   *
   * @return FormFile the uploaded file
   */
  public FormFile getFile() {
    return file;
  }

  /**
   * Note Validation is defined in /metadata/web/validate-global.xml
   *
   * @param file the file to upload
   */
  public void setFile(FormFile file) {
    this.file = file;
  }

  /**
   * @see org.apache.struts.action.ActionForm#reset(org.apache.struts.action.ActionMapping,
   *      javax.servlet.http.HttpServletRequest)
   */
  public void reset(ActionMapping mapping, HttpServletRequest request) {
    // reset any boolean data types to false
  }

  /**
   * Check to make sure the client hasn't exceeded the maximum allowed upload size
   */
  public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {

    ActionErrors errors = super.validate(mapping, request);
    errors = validateFileSize(request, errors);
    return errors;
  }

	protected ActionErrors validateFileSize(HttpServletRequest request, ActionErrors errors) {

		// has the maximum length been exceeded?
    Boolean maxLengthExceeded = (Boolean)request
            .getAttribute(MultipartRequestHandler.ATTRIBUTE_MAX_LENGTH_EXCEEDED);
    if((maxLengthExceeded != null) && (maxLengthExceeded.booleanValue())) {
      if(errors == null) {
        errors = new ActionErrors();
      }
      errors.add(ERROR_PROPERTY_MAX_LENGTH_EXCEEDED, new ActionMessage(
              "maxLengthExceeded"));
    }
		return errors;
	}

}
