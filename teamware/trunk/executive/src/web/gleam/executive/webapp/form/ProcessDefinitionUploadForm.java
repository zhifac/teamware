/*
 *  ProcessDefinitionUploadForm.java
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
package gleam.executive.webapp.form;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.upload.FormFile;
import org.apache.struts.upload.MultipartRequestHandler;

/**
 *  @author <a href="M.Agatonovic@dcs.shef.ac.uk">Milan Agatonovic</a>
 *
 * @struts.form name="processDefinitionUploadForm"
 */
public class ProcessDefinitionUploadForm extends BaseForm implements java.io.Serializable{
  private static final long serialVersionUID = 3257850969634190134L;

  public static final String ERROR_PROPERTY_MAX_LENGTH_EXCEEDED = "MaxLengthExceeded";

  /** The value of the text the user has sent as form data */
  protected String name;

  /** The file that the user has uploaded */
  protected FormFile file;

  /**
   * Retrieve the name the user has given the uploaded file
   *
   * @return the file's name
   */
  public String getName() {
    return name;
  }

  /**
   * Set the name of the uploaded file (by the user)
   *
   * @param name
   */
  public void setName(String name) {
    this.name = name;
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
   * @param file the file to upload
   */
  public void setFile(FormFile file) {
    this.file = file;
  }

  /**
   * Check to make sure the client hasn't exceeded the maximum allowed
   * upload size inside of this validate method.
   */
  // Commented out to avoid: Unhandled Exception thrown: class
  // java.lang.NullPointerException
  public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
    ActionErrors errors = null;
    // has the maximum length been exceeded?
    Boolean maxLengthExceeded = (Boolean)request
            .getAttribute(MultipartRequestHandler.ATTRIBUTE_MAX_LENGTH_EXCEEDED);
    if((maxLengthExceeded != null) && (maxLengthExceeded.booleanValue())) {
      errors = new ActionErrors();
      errors.add(ERROR_PROPERTY_MAX_LENGTH_EXCEEDED, new ActionMessage(
              "maxLengthExceeded"));
    }
    return errors;
  }
}
