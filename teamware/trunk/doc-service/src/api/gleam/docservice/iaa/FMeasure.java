/*
 *  FMeasure.java
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

public class FMeasure {
private float precision;
  
  private float recall;
  
  private float f1;
  
  private float precisionLenient;
  
  private float recallLenient;
  
  private float f1Lenient;
  
  private float correct;
  
  private float spurious;
  
  private float missing;
  
  private float partiallyCorrect;

  public float getPrecision() {
    return precision;
  }

  public void setPrecision(float precision) {
    this.precision = precision;
  }

  public float getRecall() {
    return recall;
  }

  public void setRecall(float recall) {
    this.recall = recall;
  }

  public float getF1() {
    return f1;
  }

  public void setF1(float f1) {
    this.f1 = f1;
  }

  public float getPrecisionLenient() {
    return precisionLenient;
  }

  public void setPrecisionLenient(float precisionLenient) {
    this.precisionLenient = precisionLenient;
  }

  public float getRecallLenient() {
    return recallLenient;
  }

  public void setRecallLenient(float recallLenient) {
    this.recallLenient = recallLenient;
  }

  public float getF1Lenient() {
    return f1Lenient;
  }

  public void setF1Lenient(float lenient) {
    f1Lenient = lenient;
  }

  public float getCorrect() {
    return correct;
  }

  public void setCorrect(float correct) {
    this.correct = correct;
  }

  public float getSpurious() {
    return spurious;
  }

  public void setSpurious(float spurious) {
    this.spurious = spurious;
  }

  public float getMissing() {
    return missing;
  }

  public void setMissing(float missing) {
    this.missing = missing;
  }

  public float getPartiallyCorrect() {
    return partiallyCorrect;
  }

  public void setPartiallyCorrect(float partiallyCorrect) {
    this.partiallyCorrect = partiallyCorrect;
  }
  
}
