/*
 *  SuccessMessageBody.java
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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Message returned via the JMSReplyTo channel when a GATE mode call is
 * successful.
 */
public class SuccessMessageBody implements Serializable {
  static final long serialVersionUID = -6964544525888058424L;

  /**
   * Updated annotation sets. This map may have <code>null</code> as
   * one of its keys, indicating the unnamed annotation set.
   */
  protected Map<String, byte[]> annotationSets = new HashMap<String, byte[]>();

  public Map<String, byte[]> getAnnotationSets() {
    return annotationSets;
  }

  public void addAnnotationSet(String asName, byte[] xml) {
    annotationSets.put(asName, xml);
  }
  
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(this.getClass().getName());
    sb.append(": annotationSets = ");
    sb.append(annotationSets);
    
    return sb.toString();
  }
}
