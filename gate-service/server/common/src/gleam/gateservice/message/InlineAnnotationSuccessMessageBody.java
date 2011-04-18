package gleam.gateservice.message;

import java.io.Serializable;

/**
 * Message returned via the JMSReplyTo channel when an inline annotation
 * call is successful.
 */
public class InlineAnnotationSuccessMessageBody implements Serializable {
  static final long serialVersionUID = 8230580050439858124L;

  /**
   * The document content, augmented with inline XML markup.
   */
  protected String annotatedDocument;

  public String getAnnotatedDocument() {
    return annotatedDocument;
  }

  public void setAnnotatedDocument(String annotatedDocument) {
    this.annotatedDocument = annotatedDocument;
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(this.getClass().getName());
    sb.append(": annotatedDocument = ");
    sb.append(annotatedDocument);

    return sb.toString();
  }

}
