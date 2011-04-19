/*
 *  AllWaysKappaDetail.java
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

import gleam.docservice.IAADetail;

public class AllWaysKappaDetail extends IAADetail {
  public AllWaysKappaDetail() {
    super("all-ways-kappa");
  }
  
  private float overallKappaSC;
  
  private float overallKappaDF;
  
  private float overallObservedAgreement;

  public float getOverallKappaSC() {
    return overallKappaSC;
  }

  public void setOverallKappaSC(float overallKappaSC) {
    this.overallKappaSC = overallKappaSC;
  }

  public float getOverallKappaDF() {
    return overallKappaDF;
  }

  public void setOverallKappaDF(float overallKappaDF) {
    this.overallKappaDF = overallKappaDF;
  }

  public float getOverallObservedAgreement() {
    return overallObservedAgreement;
  }

  public void setOverallObservedAgreement(float overallObservedAgreement) {
    this.overallObservedAgreement = overallObservedAgreement;
  }
}
