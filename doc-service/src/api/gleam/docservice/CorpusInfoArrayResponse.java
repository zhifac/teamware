/*
 *  CorpusInfoArrayResponse.java
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
