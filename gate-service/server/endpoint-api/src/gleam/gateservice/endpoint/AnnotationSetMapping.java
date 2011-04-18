/*
 * This file is part of GLEAM, licenced under the GNU Lesser General Public
 * Licence.
 *
 * (c) 2006 The University of Sheffield
 * 
 * $Id$
 */
package gleam.gateservice.endpoint;

public class AnnotationSetMapping {
  private String docServiceASName;
  
  private String gateServiceASName;

  public String getDocServiceASName() {
    return docServiceASName;
  }

  public void setDocServiceASName(String docServiceASName) {
    this.docServiceASName = docServiceASName;
  }

  public String getGateServiceASName() {
    return gateServiceASName;
  }

  public void setGateServiceASName(String gateServiceASName) {
    this.gateServiceASName = gateServiceASName;
  }
}
