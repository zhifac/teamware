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
 * @struts.form name="fileUploadForm"
 *
 * @author <a href="M.Agatonovic@dcs.shef.ac.uk">Milan Agatonovic</a>
 * @author <a href="mailto:H.Sun@dcs.shef.ac.uk>Haotian Sun</a>
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
