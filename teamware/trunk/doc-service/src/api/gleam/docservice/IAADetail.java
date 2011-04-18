package gleam.docservice;

/**
 * Class representing the algorithm-specific details of an IAA result.
 * The class of the detail object returned by a given invocation of
 * {@link SerialDocService#calculateIAA} will depend on the algorithm
 * chosen.
 */
public class IAADetail {
  /**
   * An identifier for the IAA algorithm used to generate this result.
   */
  private String algorithm;

  public String getAlgorithm() {
    return algorithm;
  }

  public void setAlgorithm(String algorithm) {
    this.algorithm = algorithm;
  }

  public IAADetail(String algorithm) {
    this.algorithm = algorithm;
  }
  
  /**
   * No argument constructor to satisfy stub generator.
   */
  public IAADetail() {
  }
}
