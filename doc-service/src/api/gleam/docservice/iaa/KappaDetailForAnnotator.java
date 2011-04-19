/*
 *  KappaDetailForAnnotator.java
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

public class KappaDetailForAnnotator {
  private String annotationSetName;
  
  private float kappaCohen;
  
  private float kappaPi;
  
  private float observedAgreement;
  
  private float[][] confusionMatrix;
  
  private float[] specificAgreementsPositive;
  
  private float[] specificAgreementsNegative;

  public String getAnnotationSetName() {
    return annotationSetName;
  }

  public void setAnnotationSetName(String annotationSetName) {
    this.annotationSetName = annotationSetName;
  }

  public float getKappaCohen() {
    return kappaCohen;
  }

  public void setKappaCohen(float kappaCohen) {
    this.kappaCohen = kappaCohen;
  }

  public float getKappaPi() {
    return kappaPi;
  }

  public void setKappaPi(float kappaPi) {
    this.kappaPi = kappaPi;
  }

  public float getObservedAgreement() {
    return observedAgreement;
  }

  public void setObservedAgreement(float observedAgreement) {
    this.observedAgreement = observedAgreement;
  }

  public float[][] getConfusionMatrix() {
    return confusionMatrix;
  }

  public void setConfusionMatrix(float[][] confusionMatrix) {
    this.confusionMatrix = confusionMatrix;
  }

  public float[] getSpecificAgreementsPositive() {
    return specificAgreementsPositive;
  }

  public void setSpecificAgreementsPositive(float[] specificAgreementsPositive) {
    this.specificAgreementsPositive = specificAgreementsPositive;
  }

  public float[] getSpecificAgreementsNegative() {
    return specificAgreementsNegative;
  }

  public void setSpecificAgreementsNegative(float[] specificAgreementsNegative) {
    this.specificAgreementsNegative = specificAgreementsNegative;
  }
}
