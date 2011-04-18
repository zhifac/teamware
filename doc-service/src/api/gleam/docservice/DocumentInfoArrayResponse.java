package gleam.docservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * Special response wrapper for DocumentInfo[] responses, to treat an empty
 * response as an empty array rather than null.
 */
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(name = "documentInfoArrayResponse")
public class DocumentInfoArrayResponse {
  private DocumentInfo[] _return;

  public void setReturn(DocumentInfo[] _return) {
    this._return = _return;
  }

  public DocumentInfo[] getReturn() {
    if(_return == null) {
      return new DocumentInfo[0];
    }
    else {
      return _return;
    }
  }
}
