package gleam.docservice.iaa;

public class KappaDetailForAnnotatorPairs {
  private String keyAnnotationSetName;
  
  private KappaDetailForAnnotator[] detailForResponses;

  public String getKeyAnnotationSetName() {
    return keyAnnotationSetName;
  }

  public void setKeyAnnotationSetName(String keyAnnotationSetName) {
    this.keyAnnotationSetName = keyAnnotationSetName;
  }

  public KappaDetailForAnnotator[] getDetailForResponses() {
    return detailForResponses;
  }

  public void setDetailForResponses(KappaDetailForAnnotator[] detailForResponses) {
    this.detailForResponses = detailForResponses;
  }
}
