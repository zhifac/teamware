/*
 *  OntologyForm.java
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

/**
 * This class is modeled after the UploadForm from the struts-upload
 * example application. For more information on implementation details,
 * please see that application.
 *
 * @author <a href="M.Agatonovic@dcs.shef.ac.uk">Milan Agatonovic</a>
 * @author <a href="mailto:H.Sun@dcs.shef.ac.uk">Haotian Sun</a>
 *
 * @struts.form name="ontologyForm"
 */
public class OntologyForm extends BaseForm {
  private static final long serialVersionUID = 3257850969634190139L;

  /** The value of the text the user has sent as form data */
  protected String data;


  /**
   * Retrieve the name the user has given the uploaded file
   *
   * @return the file's name
   */
  public String getData() {
    return data;
  }

  /**
   * Set the data of an ontology loaded from the database
   *
   * @param data
   */
  public void setData(String data) {
    this.data = data;
  }
}
