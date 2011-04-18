package gleam.docservice.iaa;

public class FMeasureDetailForAnnotator {
  private String annotationSetName;
  
  private FMeasure overallFMeasure;
  
  private FMeasureDetailForLabel[] detailForLabels;

  public String getAnnotationSetName() {
    return annotationSetName;
  }

  public void setAnnotationSetName(String annotatorName) {
    this.annotationSetName = annotatorName;
  }

  public FMeasure getOverallFMeasure() {
    return overallFMeasure;
  }

  public void setOverallFMeasure(FMeasure overallFMeasure) {
    this.overallFMeasure = overallFMeasure;
  }

  public FMeasureDetailForLabel[] getDetailForLabels() {
    return detailForLabels;
  }

  public void setDetailForLabels(FMeasureDetailForLabel[] detailForLabels) {
    this.detailForLabels = detailForLabels;
  }
}
