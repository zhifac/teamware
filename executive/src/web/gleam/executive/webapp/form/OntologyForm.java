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
