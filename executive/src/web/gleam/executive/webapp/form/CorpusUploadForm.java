package gleam.executive.webapp.form;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMapping;

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
 * @struts.form name="corpusUploadForm"
 *
 * @author <a href="M.Agatonovic@dcs.shef.ac.uk">Milan Agatonovic</a>
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
