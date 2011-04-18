package gleam.docservice.iaa;

import gleam.docservice.IAADetail;

/**
 * Detail section for an all-ways F measure calculation.
 */
public class AllWaysFMeasureDetail extends IAADetail {

  public AllWaysFMeasureDetail() {
    super("all-ways-f-measure");
  }

  private FMeasure overallFMeasure;

  private FMeasureDetailForAnnotator[] detailForAnnotators;

  public FMeasure getOverallFMeasure() {
    return overallFMeasure;
  }

  public void setOverallFMeasure(FMeasure overallFMeasure) {
    this.overallFMeasure = overallFMeasure;
  }

  public FMeasureDetailForAnnotator[] getDetailForAnnotators() {
    return detailForAnnotators;
  }

  public void setDetailForAnnotators(
          FMeasureDetailForAnnotator[] detailForAnnotators) {
    this.detailForAnnotators = detailForAnnotators;
  }
}
