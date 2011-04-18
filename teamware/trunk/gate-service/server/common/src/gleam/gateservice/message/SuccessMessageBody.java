/*
 * This file is part of GLEAM, licenced under the GNU Lesser General Public
 * Licence.
 *
 * (c) 2006 The University of Sheffield
 * 
 * $Id$
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
