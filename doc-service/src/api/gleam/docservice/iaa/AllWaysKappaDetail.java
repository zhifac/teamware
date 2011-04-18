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
