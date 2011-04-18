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
