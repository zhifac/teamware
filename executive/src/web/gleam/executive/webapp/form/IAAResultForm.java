/*
 *  IAAResultForm.java
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
package gleam.executive.webapp.form;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author <a href="mailto:H.Sun@dcs.shef.ac.uk">Haotian Sun</a>
 * 
 * @struts.form name="iAAResultForm"
 */
public class IAAResultForm extends BaseForm {
  private static final long serialVersionUID = 3257850969634190139L;
  
  protected String totalFmeasure;
  protected String pairFmeasure;
  protected String agreement;
  protected String feature;
  protected String algorithm;
  private String correct;
  private String partiallyCorrect;
  private String spurious;
  private String missing;
  
  private String precision;
  private String recall;
  private String f1;
  private String precisionLenient;
  private String recallLenient;
  private String f1Lenient;
  
  private String kappaCohen;
  private String kappaPi;
  private String observedAgreement;
  private String kappaSC;
  private String kappaDF;
  
  protected Map<String, String> strictvalues;
  protected Map<String, String> lenientValues;
  protected Map<String, String> otherValues;
  protected Map<String, String> kappavalues;
  protected String allwaysStrictvalues;
  protected String allwaysLenientValues;
  protected String allwaysOtherValues;
  protected String allwaysResponse;
  protected Map<String, List<LabelValueDetailForm>> labelDetails;
  protected Map<String, List<ConfusionMatrixRowForm>> confusionMatrices;
  
  protected Map<String, String> labelValues;
  
  protected String keyASName;
  protected transient java.util.Set asNames = new java.util.HashSet();
 
  protected String annoType;
  
  public String getPariFmeasure() {
    return pairFmeasure;
  }
  public void setPariFmeasure(String pairFmeasure) {
    this.pairFmeasure = pairFmeasure;
  }
  public String getTotalFmeasure() {
    return totalFmeasure;
  }
  public void setTotalFmeasure(String totalFmeasure) {
    this.totalFmeasure = totalFmeasure;
  }
  
  public String getAgreement() {
    return agreement;
  }
  public void setAgreement(String agreement) {
    this.agreement = agreement;
  }
  public java.util.Set getAsNames() {
    return asNames;
  }
  public void setAsNames(java.util.Set asNames) {
    this.asNames = asNames;
  }
  
  public String getAnnoType() {
    return annoType;
  }
  public void setAnnoType(String annoType) {
    this.annoType = annoType;
  }
  public void addASNames(String asName){
    this.getAsNames().add(asName);
  }

  public String[] getIAAResultAsNames() {
    gleam.executive.model.LabelValue label;
    String[] iaaResultASNames = new String[asNames.size()];
    int i = 0;
    for (java.util.Iterator iter = asNames.iterator(); iter.hasNext();) {
        label = (gleam.executive.model.LabelValue) iter.next();
        iaaResultASNames[i] = label.getValue();
        i++;
    }
    return iaaResultASNames;
  }

/**
 * Note that this is not used - it's just needed by Struts.  If you look
 * in IAACaculateAction - you'll see that request.getParameterValues("iAAResultASNames")
 * is used instead.
 * 
 * @param roles
 */
  public void setIAAResultAsNames(String[] asNames) {}
  public String getKeyASName() {
    return keyASName;
  }
  public void setKeyASName(String keyASName) {
    this.keyASName = keyASName;
  }
  public Map<String, String> getLenientValues() {
    return lenientValues;
  }
  public void setLenientValues(Map<String, String> lenientValues) {
    this.lenientValues = lenientValues;
  }
  public Map<String, String> getOtherValues() {
    return otherValues;
  }
  public void setOtherValues(Map<String, String> otherValues) {
    this.otherValues = otherValues;
  }
  public Map<String, String> getStrictvalues() {
    return strictvalues;
  }
  public void setStrictvalues(Map<String, String> strictvalues) {
    this.strictvalues = strictvalues;
  }
  public Map<String, String> getLabelValues() {
    return labelValues;
  }
  public void setLabelValues(Map<String, String> labelValues) {
    this.labelValues = labelValues;
  }
  public Map<String, List<LabelValueDetailForm>> getLabelDetails() {
    return labelDetails;
  }
  
  public Map<String, String> getKappavalues() {
    return kappavalues;
  }
  public void setKappavalues(Map<String, String> kappavalues) {
    this.kappavalues = kappavalues;
  }
  public void setLabelDetails(Map<String, List<LabelValueDetailForm>> labelDetails) {
    this.labelDetails = labelDetails;
  }
  public Map<String, List<ConfusionMatrixRowForm>> getConfusionMatrices() {
    return confusionMatrices;
  }
  public void setConfusionMatrices(
    Map<String, List<ConfusionMatrixRowForm>> confusionMatrices) {
    this.confusionMatrices = confusionMatrices;
  }
  public String getFeature() {
    return feature;
  }
  public void setFeature(String feature) {
    this.feature = feature;
  }
  public String getPairFmeasure() {
    return pairFmeasure;
  }
  public void setPairFmeasure(String pairFmeasure) {
    this.pairFmeasure = pairFmeasure;
  }
  public String getAllwaysLenientValues() {
    return allwaysLenientValues;
  }
  public void setAllwaysLenientValues(String allwaysLenientValues) {
    this.allwaysLenientValues = allwaysLenientValues;
  }
  public String getAllwaysOtherValues() {
    return allwaysOtherValues;
  }
  public void setAllwaysOtherValues(String allwaysOtherValues) {
    this.allwaysOtherValues = allwaysOtherValues;
  }
  public String getAllwaysResponse() {
    return allwaysResponse;
  }
  public void setAllwaysResponse(String allwaysResponse) {
    this.allwaysResponse = allwaysResponse;
  }
  public String getAllwaysStrictvalues() {
    return allwaysStrictvalues;
  }
  public void setAllwaysStrictvalues(String allwaysStrictvalues) {
    this.allwaysStrictvalues = allwaysStrictvalues;
  }
  public String getCorrect() {
    return correct;
  }
  public void setCorrect(String correct) {
    this.correct = correct;
  }
  public String getF1() {
    return f1;
  }
  public void setF1(String f1) {
    this.f1 = f1;
  }
  public String getF1Lenient() {
    return f1Lenient;
  }
  public void setF1Lenient(String lenient) {
    f1Lenient = lenient;
  }
  public String getMissing() {
    return missing;
  }
  public void setMissing(String missing) {
    this.missing = missing;
  }
  public String getPartiallyCorrect() {
    return partiallyCorrect;
  }
  public void setPartiallyCorrect(String partiallyCorrect) {
    this.partiallyCorrect = partiallyCorrect;
  }
  public String getPrecision() {
    return precision;
  }
  public void setPrecision(String precision) {
    this.precision = precision;
  }
  public String getPrecisionLenient() {
    return precisionLenient;
  }
  public void setPrecisionLenient(String precisionLenient) {
    this.precisionLenient = precisionLenient;
  }
  public String getRecall() {
    return recall;
  }
  public void setRecall(String recall) {
    this.recall = recall;
  }
  public String getRecallLenient() {
    return recallLenient;
  }
  public void setRecallLenient(String recallLenient) {
    this.recallLenient = recallLenient;
  }
  public String getSpurious() {
    return spurious;
  }
  public void setSpurious(String spurious) {
    this.spurious = spurious;
  }
  public String getKappaCohen() {
    return kappaCohen;
  }
  public void setKappaCohen(String kappaCohen) {
    this.kappaCohen = kappaCohen;
  }
  public String getKappaPi() {
    return kappaPi;
  }
  public void setKappaPi(String kappaPi) {
    this.kappaPi = kappaPi;
  }
  public String getObservedAgreement() {
    return observedAgreement;
  }
  public void setObservedAgreement(String observedAgreement) {
    this.observedAgreement = observedAgreement;
  }
  public String getKappaDF() {
    return kappaDF;
  }
  public void setKappaDF(String kappaDF) {
    this.kappaDF = kappaDF;
  }
  public String getKappaSC() {
    return kappaSC;
  }
  public void setKappaSC(String kappaSC) {
    this.kappaSC = kappaSC;
  }
  public String getAlgorithm() {
    return algorithm;
  }
  public void setAlgorithm(String algorithm) {
    this.algorithm = algorithm;
  }
 
  
  
}
