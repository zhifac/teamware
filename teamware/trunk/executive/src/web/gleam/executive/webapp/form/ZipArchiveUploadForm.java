/*
 *  ZipArchiveUploadForm.java
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
 * Ivaylo Kabakov
 *
 *  $Id$
 */

package gleam.executive.webapp.form;

import java.nio.charset.Charset;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

public class ZipArchiveUploadForm extends BaseUploadForm implements java.io.Serializable {

	private String encoding;
  
  /**
   * @return Returns the encoding.
   */
  public String getEncoding() {
    return encoding;
  }

  /**
   * Note Validation is defined in /metadata/web/validate-global.xml
   *
   * @param encoding The encoding to set.
   */
  public void setEncoding(String encoding) {
    this.encoding = encoding;
  }

  public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {

    ActionErrors errors = super.validate(mapping, request);

    if(!isValidEncoding(this.encoding)){
      errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
      "corpus.encoding.invalid"));
    }

    if(!isZipFile(getFile().getFileName())){
      errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
      "corpus.population.fileInValid"));
    }

    return errors;
  }

  public boolean isZipFile(String filename) {

  	boolean result;
    String lowercaseName = filename.toLowerCase();
    if(lowercaseName.endsWith(".zip")) {
      result = true;
    } else if(lowercaseName.endsWith(".ear")) {
      result = true;
    } else if(lowercaseName.endsWith(".war")) {
      result = true;
    } else if(lowercaseName.endsWith(".rar")) {
      result = true;
    } else if(lowercaseName.endsWith(".jar")) {
      result = true;
    } else {
      result = false;
    }

    return result;
  }

  public boolean isValidEncoding(String encoding) {
		try {
			return Charset.isSupported(encoding);
		} catch(IllegalArgumentException e) {
			return false;
		}
  }

}
