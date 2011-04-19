/*
 *  InlineAnnotationMessageBody.java
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
