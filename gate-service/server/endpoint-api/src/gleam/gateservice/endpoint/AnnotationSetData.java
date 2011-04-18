/*
 * This file is part of GLEAM, licenced under the GNU Lesser General Public
 * Licence.
 *
 * (c) 2006 The University of Sheffield
 * 
 * $Id$
 */
package gleam.gateservice.endpoint;

public class AnnotationSetData {
  private String name;
  
  /**
   * The GATE XML representation of the annotation set contents, encoded in
   * UTF-8.
   */
  private byte[] xmlData;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public byte[] getXmlData() {
    return xmlData;
  }

  public void setXmlData(byte[] xmlData) {
    this.xmlData = xmlData;
  }
}
