/*
 *  FMeasureImpl.java
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
package gleam.docservice.proxy.impl.iaa;

import gleam.docservice.proxy.iaa.FMeasure;

/**
 * Class to expose an F-measure value returned from the doc service as
 * the doc service proxy FMeasure interface.
 */
public class FMeasureImpl implements FMeasure {

  private gleam.docservice.iaa.FMeasure clientFMeasure;

  public FMeasureImpl(gleam.docservice.iaa.FMeasure clientFMeasure) {
    this.clientFMeasure = clientFMeasure;
  }

  public float correct() {
    return clientFMeasure.getCorrect();
  }

  public float f1() {
    return clientFMeasure.getF1();
  }

  public float f1Lenient() {
    return clientFMeasure.getF1Lenient();
  }

  public float missing() {
    return clientFMeasure.getMissing();
  }

  public float partiallyCorrect() {
    return clientFMeasure.getPartiallyCorrect();
  }

  public float precision() {
    return clientFMeasure.getPrecision();
  }

  public float precisionLenient() {
    return clientFMeasure.getPrecisionLenient();
  }

  public float recall() {
    return clientFMeasure.getRecall();
  }

  public float recallLenient() {
    return clientFMeasure.getRecallLenient();
  }

  public float spurious() {
    return clientFMeasure.getSpurious();
  }

}
