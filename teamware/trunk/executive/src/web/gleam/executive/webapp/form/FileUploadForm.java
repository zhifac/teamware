/*
 *  FileUploadForm.java
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
 * Milan Agatonovic and Haotian Sun
 *
 *  $Id$
 */
package gleam.executive.webapp.form;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMapping;

/**
 * 
 * @struts.form name="fileUploadForm"
 *
 */
public class FileUploadForm extends ZipArchiveUploadForm implements java.io.Serializable {

  private static final long serialVersionUID = 448164697673537412L;
  public static final String ERROR_PROPERTY_MAX_LENGTH_EXCEEDED = "MaxLengthExceeded";

  /**
   * @see org.apache.struts.action.ActionForm#reset(org.apache.struts.action.ActionMapping,
   *      javax.servlet.http.HttpServletRequest)
   */
  public void reset(ActionMapping mapping, HttpServletRequest request) {
    // reset any boolean data types to false
  }

  /**
   * <code>ObjectFactory</code> - used if indexed properties exist on
   * this form.
   *
   * @see org.apache.commons.collections.ListUtils
   */
  class ObjectFactory implements org.apache.commons.collections.Factory {
    private String name;

    /**
     * Create a new instance of the specified object
     */
    public Object create() {
      Class c = null;
      try {
        c = Class.forName("gleam.executive.webapp.form." + name + "Form");
        return c.newInstance();
      }
      catch(Exception e) {
        System.err.println("Error instantiating class: " + c.getName());
        throw new RuntimeException(e);
      }
    }

    public ObjectFactory(String name) {
      this.name = name;
    }
  }

}
