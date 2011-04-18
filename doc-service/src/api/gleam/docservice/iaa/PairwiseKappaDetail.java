package gleam.docservice.iaa;

import gleam.docservice.IAADetail;

public class PairwiseKappaDetail extends IAADetail {
  public PairwiseKappaDetail() {
    super("pairwise-kappa");
  }
  
  private float overallKappaCohen;
  
  private float overallKappaPi;
  
  private float overallObservedAgreement;
  
  private KappaDetailForAnnotatorPairs[] detailForPairs;

  public float getOverallKappaCohen() {
    return overallKappaCohen;
  }

  public void setOverallKappaCohen(float overallKappaCohen) {
    this.overallKappaCohen = overallKappaCohen;
  }

  public float getOverallKappaPi() {
    return overallKappaPi;
  }

  public void setOverallKappaPi(float overallKappaPi) {
    this.overallKappaPi = overallKappaPi;
  }

  public float getOverallObservedAgreement() {
    return overallObservedAgreement;
  }

  public void setOverallObservedAgreement(float overallObservedAgreement) {
    this.overallObservedAgreement = overallObservedAgreement;
  }

  public KappaDetailForAnnotatorPairs[] getDetailForPairs() {
    return detailForPairs;
  }

  public void setDetailForPairs(KappaDetailForAnnotatorPairs[] detailForPairs) {
    this.detailForPairs = detailForPairs;
  }
}
