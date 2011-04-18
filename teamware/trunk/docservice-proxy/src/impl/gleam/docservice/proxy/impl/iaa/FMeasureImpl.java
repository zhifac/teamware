package gleam.docservice.proxy.impl.iaa;

import gleam.docservice.proxy.iaa.FMeasure;

/**
 * Class to expose an F-measure value returned from the doc service as
 * the doc service proxy FMeasure interface.
 */
public class FMeasureImpl implements FMeasure {

  private gleam.docservice.iaa.FMeasure clientFMeasure;

  public FMeasureImpl(gleam.docservice.iaa.FMeasure clientFMeasure) {
    this.clientFMeasure = clientFMeasure;
  }

  public float correct() {
    return clientFMeasure.getCorrect();
  }

  public float f1() {
    return clientFMeasure.getF1();
  }

  public float f1Lenient() {
    return clientFMeasure.getF1Lenient();
  }

  public float missing() {
    return clientFMeasure.getMissing();
  }

  public float partiallyCorrect() {
    return clientFMeasure.getPartiallyCorrect();
  }

  public float precision() {
    return clientFMeasure.getPrecision();
  }

  public float precisionLenient() {
    return clientFMeasure.getPrecisionLenient();
  }

  public float recall() {
    return clientFMeasure.getRecall();
  }

  public float recallLenient() {
    return clientFMeasure.getRecallLenient();
  }

  public float spurious() {
    return clientFMeasure.getSpurious();
  }

}
