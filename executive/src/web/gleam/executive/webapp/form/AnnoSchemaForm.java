package gleam.executive.webapp.form;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.upload.MultipartRequestHandler;

import gleam.executive.webapp.form.BaseForm;

/**
 * @struts.form name="annoSchemaForm"
 * @author <a href="M.Agatonovic@dcs.shef.ac.uk">Milan Agatonovic</a>
 * @author <a href="mailto:H.Sun@dcs.shef.ac.uk>Haotian Sun</a>
 */
public class AnnoSchemaForm extends BaseForm implements java.io.Serializable {

  private static final long serialVersionUID = 448164697673537414L;

  public static final String ERROR_PROPERTY_MAX_LENGTH_EXCEEDED = "MaxLengthExceeded";

  /** Default empty constructor. */
  public AnnoSchemaForm() {
  }

  // Start block: This block is from the file
  // metadata\web\xdoclet-FileUploadForm.java
  private org.apache.struts.upload.FormFile file;

  private org.apache.struts.upload.FormFile file1;

  private org.apache.struts.upload.FormFile file2;

  private org.apache.struts.upload.FormFile file3;

  private org.apache.struts.upload.FormFile file4;

  /**
   *
   * @return FormFile the uploaded file
   */
  public org.apache.struts.upload.FormFile getFile() {
    return file;
  }

  /**
   * Note Validation is defined in /metadata/web/validate-global.xml
   *
   * @param file1 the file to upload
   */
  public void setFile(org.apache.struts.upload.FormFile file) {
    this.file = file;
  }


  // End block

  public org.apache.struts.upload.FormFile getFile1() {
    return file1;
  }

  public void setFile1(org.apache.struts.upload.FormFile file1) {
    this.file1 = file1;
  }

  public org.apache.struts.upload.FormFile getFile2() {
    return file2;
  }

  public void setFile2(org.apache.struts.upload.FormFile file2) {
    this.file2 = file2;
  }

  public org.apache.struts.upload.FormFile getFile3() {
    return file3;
  }

  public void setFile3(org.apache.struts.upload.FormFile file3) {
    this.file3 = file3;
  }

  public org.apache.struts.upload.FormFile getFile4() {
    return file4;
  }

  public void setFile4(org.apache.struts.upload.FormFile file4) {
    this.file4 = file4;
  }

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

  /**
   * Check to make sure the client hasn't exceeded the maximum allowed
   * upload size inside of this validate method.
   */
  public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {

    ActionErrors errors = super.validate(mapping, request);

    // has the maximum length been exceeded?
    Boolean maxLengthExceeded = (Boolean)request
            .getAttribute(MultipartRequestHandler.ATTRIBUTE_MAX_LENGTH_EXCEEDED);
    if((maxLengthExceeded != null) && (maxLengthExceeded.booleanValue())) {
      if(errors == null) {
        errors = new ActionErrors();
      }
      errors.add(ERROR_PROPERTY_MAX_LENGTH_EXCEEDED, new ActionMessage(
              "maxLengthExceeded"));
    }

    if(!isXMLFile(file.getFileName())||
       (!isXMLFile(file1.getFileName())&&!file1.getFileName().equals(""))||
       (!isXMLFile(file2.getFileName())&&!file2.getFileName().equals(""))||
       (!isXMLFile(file3.getFileName())&&!file3.getFileName().equals(""))||
       (!isXMLFile(file4.getFileName())&&!file4.getFileName().equals(""))
      ){
      errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
      "annoSchema.upload.fileInValid"));
    }

    return errors;
  }

  public boolean isXMLFile(String filename) {
    boolean result=false;

    String lowercaseName = filename.toLowerCase();
    if(lowercaseName.endsWith(".xml")) {
      result = true;
    }
    return result;
  }


}