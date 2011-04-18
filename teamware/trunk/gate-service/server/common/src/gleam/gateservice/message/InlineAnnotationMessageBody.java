/*
 * This file is part of GLEAM, licenced under the GNU Lesser General Public
 * Licence.
 *
 * (c) 2006 The University of Sheffield
 * 
 * $Id$
 */
package gleam.gateservice.message;

/**
 * Request message body for an inline-annotation call.
 */
public class InlineAnnotationMessageBody extends RequestMessageBody {
  static final long serialVersionUID = -8912852825007379691L;

  /**
   * The document to process.
   */
  protected String documentContent;

  public InlineAnnotationMessageBody(String documentContent) {
    super();
    this.documentContent = documentContent;
  }

  public String getDocumentContent() {
    return documentContent;
  }

  public void setDocumentContent(String documentContent) {
    this.documentContent = documentContent;
  }
  
  public String toString() {
    StringBuilder sb = new StringBuilder(super.toString());
    sb.append(", documentContent = ");
    if(documentContent == null) {
      sb.append("null");
    }
    else {
      sb.append('"');
      sb.append(documentContent);
      sb.append('"');
    }
    
    return sb.toString();
  }
}
