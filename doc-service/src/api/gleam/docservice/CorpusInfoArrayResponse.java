package gleam.docservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * Special response wrapper for CorpusInfo[] responses, to treat an empty
 * response as an empty array rather than null.
 */
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(name = "corpusInfoArrayResponse")
public class CorpusInfoArrayResponse {
  private CorpusInfo[] _return;

  public void setReturn(CorpusInfo[] _return) {
    this._return = _return;
  }

  public CorpusInfo[] getReturn() {
    if(_return == null) {
      return new CorpusInfo[0];
    }
    else {
      return _return;
    }
  }
}
