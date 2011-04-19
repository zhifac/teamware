/*
 *  KappaResultImpl.java
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

import java.util.HashMap;
import java.util.Map;

import gleam.docservice.iaa.KappaDetailForAnnotator;
import gleam.docservice.proxy.iaa.KappaResult;

public class KappaResultImpl implements KappaResult {
  private KappaDetailForAnnotator detail;

  private Map<String, Integer> labelIndex = new HashMap<String, Integer>();

  public KappaResultImpl(KappaDetailForAnnotator d, String[] labelValues) {
    detail = d;
    if(labelValues != null) {
      for(int i = 0; i < labelValues.length; i++) {
        labelIndex.put(labelValues[i], Integer.valueOf(i));
      }
      labelIndex.put(null, Integer.valueOf(labelValues.length));
    }
    else {
      labelIndex.put(null, Integer.valueOf(0));
    }
  }

  public float getConfusionMatrixEntry(String keyLabel, String responseLabel) {
    Integer keyIndex = labelIndex.get(keyLabel);
    Integer responseIndex = labelIndex.get(responseLabel);
    
    if(keyIndex != null && responseIndex != null) {
      return detail.getConfusionMatrix()[keyIndex][responseIndex];
    }
    else {
      return 0f;
    }
  }

  public float getKappaCohen() {
    return detail.getKappaCohen();
  }

  public float getKappaPi() {
    return detail.getKappaPi();
  }

  public float getObservedAgreement() {
    return detail.getObservedAgreement();
  }

  public float getSpecificAgreement(String labelValue, boolean positive) {
    Integer index = labelIndex.get(labelValue);
    if(index != null) {
      if(positive) {
        return detail.getSpecificAgreementsPositive()[index];
      }
      else {
        return detail.getSpecificAgreementsNegative()[index];
      }
    }
    else {
      return 0f;
    }
  }

}
