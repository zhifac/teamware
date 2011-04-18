package gleam.docservice.iaa;

import gleam.docservice.IAADetail;

/**
 * Detail section for a pairwise F-measure IAA calculation.
 */
public class PairwiseFMeasureDetail extends IAADetail {

  public PairwiseFMeasureDetail() {
    super("paiwise-f-measure");
  }

  private FMeasure overallFMeasure;

  private FMeasureDetailForAnnotatorPairs[] detailForPairs;

  public FMeasure getOverallFMeasure() {
    return overallFMeasure;
  }

  public void setOverallFMeasure(FMeasure overallFMeasure) {
    this.overallFMeasure = overallFMeasure;
  }

  public FMeasureDetailForAnnotatorPairs[] getDetailForPairs() {
    return detailForPairs;
  }

  public void setDetailForPairs(FMeasureDetailForAnnotatorPairs[] detailForPairs) {
    this.detailForPairs = detailForPairs;
  }
}
