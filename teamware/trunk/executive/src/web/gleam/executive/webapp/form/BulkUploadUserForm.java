package gleam.executive.webapp.form;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.upload.FormFile;

/**
 * <p>
 * 	This class is modelled after the UploadForm from the struts-upload example
 * 	application. For more information on implementation details, please see that
 * 	application.
 * </p>
 * <p>
 * 	Copyright &copy; 1998-2010, The University of Sheffield.
 * </p>
 * <p>
 * 	This file is part of <a href="http://gate.ac.uk/">GATE</a> and is free software,
 * 	licenced under the GNU Library General Public License, Version 2, June 1991
 * 	(in the distribution as file <code>licence.html</code>, and also available at 
 * 	<a href="http://gate.ac.uk/gate/licence.html">http://gate.ac.uk/gate/licence.html</a>).
 * </p>
 * 
 * @author <a href="mailto:agaton@dcs.shef.ac.uk">Milan Agatonovic</a>
 * 
 * @struts.form name="bulkUploadUserForm"
 */
public class BulkUploadUserForm extends BaseUploadForm {

	private static final long serialVersionUID = 3257850969634190134L;
	public static final String ERROR_PROPERTY_MAX_LENGTH_EXCEEDED = "MaxLengthExceeded";

	/** The file that the user has uploaded */
	protected FormFile file;

	/** Default empty constructor. */
	public BulkUploadUserForm() {
	}

	/**
	 * Retrieve a representation of the file the user has uploaded
	 * 
	 * @return FormFile the uploaded file
	 */
	public FormFile getFile() {

		return file;
	}

	/**
	 * Set a representation of the file the user has uploaded
	 * 
	 * @param file
	 *          the file to upload
	 */
	public void setFile(FormFile file) {

		this.file = file;
	}

	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {

		ActionErrors errors = super.validate(mapping, request);

		if (!isExcelFile(file.getFileName())) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.bulkUpload.invalidFormat"));
		}

		return errors;
	}

	public boolean isExcelFile(String filename) {

		boolean result;

		String lowercaseName = filename.toLowerCase();
		if (lowercaseName.endsWith(".xls")) {
			result = true;
		}

		else {
			result = false;
		}

		return result;
	}

}
