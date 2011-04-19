/*
 *  BulkUploadUserForm.java
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
 * Milan Agatonovic
 *
 *  $Id$
 */
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
