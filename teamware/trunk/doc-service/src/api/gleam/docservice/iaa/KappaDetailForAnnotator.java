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
