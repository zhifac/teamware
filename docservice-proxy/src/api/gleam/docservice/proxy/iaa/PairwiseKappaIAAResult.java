package gleam.docservice.proxy.iaa;

import gleam.docservice.proxy.IAAResult;

public interface PairwiseKappaIAAResult extends IAAResult {
  public float getOverallKappaCohen();
  public float getOverallKappaPi();
  public float getOverallObservedAgreement();
  
  /**
   * For the pair (as1, as2), determine which is included in this result
   * as the key.
   * 
   * The results of a pairwise kappa calculation include the results
   * for every pair of annotation sets, but only one way around, i.e. if
   * the result includes <i>A</i> against <i>B</i> then it will not
   * include <i>B</i> against <i>A</i>.
   * 
   * @return the one of as1 and as2 which is the key for this pairing,
   *         or <code>null</code> if this pairing is not present in
   *         this result.
   */
  public String getKeyASName(String as1, String as2);

  /**
   * For the pair (as1, as2), determine which is included in this result
   * as the response.
   * 
   * The results of a pairwise kappa calculation include the results
   * for every pair of annotation sets, but only one way around, i.e. if
   * the result includes <i>A</i> against <i>B</i> then it will not
   * include <i>B</i> against <i>A</i>.
   * 
   * @return the one of as1 and as2 which is the key for this pairing,
   *         or <code>null</code> if this pairing is not present in
   *         this result.
   */
  public String getResponseASName(String as1, String as2);
  
  public KappaResult getResult(String as1, String as2);

}
