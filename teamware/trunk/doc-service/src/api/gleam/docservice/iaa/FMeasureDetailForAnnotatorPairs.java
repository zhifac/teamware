/*
 *  FMeasureDetailForAnnotatorPairs.java
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
package gleam.docservice.iaa;

public class FMeasureDetailForAnnotatorPairs {
  private String keyAnnotationSetName;
  
  private FMeasureDetailForAnnotator[] detailForResponses;

  public String getKeyAnnotationSetName() {
    return keyAnnotationSetName;
  }

  public void setKeyAnnotationSetName(String keyAnnotationSetName) {
    this.keyAnnotationSetName = keyAnnotationSetName;
  }

  public FMeasureDetailForAnnotator[] getDetailForResponses() {
    return detailForResponses;
  }

  public void setDetailForResponses(
          FMeasureDetailForAnnotator[] detailForAnnotators) {
    this.detailForResponses = detailForAnnotators;
  }
}
