package gleam.executive.webapp.form;

import java.nio.charset.Charset;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

/**
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
 * @author <a href="mailto:ivaylo.kabakov@ontotext.com">Ivaylo Kabakov</a>
 */
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
