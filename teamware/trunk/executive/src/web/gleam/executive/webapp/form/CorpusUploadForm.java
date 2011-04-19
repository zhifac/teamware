/*
 *  CorpusUploadForm.java
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

import org.apache.struts.action.ActionMapping;

/**
 * 
 * @struts.form name="corpusUploadForm"
 */
public class CorpusUploadForm extends ZipArchiveUploadForm implements java.io.Serializable {

  protected String corpusName;
  protected String corpusID;

  public String getCorpusID() {
  	return corpusID;
  }

	public void setCorpusID(String corpusID) {
		this.corpusID = corpusID;
	}

	public String getCorpusName() {
      return this.corpusName;
  }

  public void setCorpusName(String corpusName) {
      this.corpusName = corpusName;
  }

  /**
   * @see org.apache.struts.action.ActionForm#reset(org.apache.struts.action.ActionMapping,
   *      javax.servlet.http.HttpServletRequest)
   */
  public void reset(ActionMapping mapping, HttpServletRequest request) {
    // reset any boolean data types to false
  }

}
