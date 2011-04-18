package gleam.docservice.proxy;

public interface IAAResult {
  public float getAgreement();
  public IAAAlgorithm getAlgorithm();
  
  /**
   * Get the set of label values.  This may be null if
   * this calculation did not make use of labels (i.e.
   * featureName was null).
   */
  public String[] getLabelValues();
}
