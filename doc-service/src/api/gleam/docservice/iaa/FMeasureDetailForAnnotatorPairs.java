package gleam.docservice.iaa;

public class FMeasureDetailForAnnotatorPairs {
  private String keyAnnotationSetName;
  
  private FMeasureDetailForAnnotator[] detailForResponses;

  public String getKeyAnnotationSetName() {
    return keyAnnotationSetName;
  }

  public void setKeyAnnotationSetName(String keyAnnotationSetName) {
    this.keyAnnotationSetName = keyAnnotationSetName;
  }

  public FMeasureDetailForAnnotator[] getDetailForResponses() {
    return detailForResponses;
  }

  public void setDetailForResponses(
          FMeasureDetailForAnnotator[] detailForAnnotators) {
    this.detailForResponses = detailForAnnotators;
  }
}
