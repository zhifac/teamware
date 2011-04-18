package gleam.docservice.proxy;

import gleam.docservice.proxy.iaa.AllWaysFMeasureIAAResult;
import gleam.docservice.proxy.iaa.AllWaysKappaIAAResult;
import gleam.docservice.proxy.iaa.PairwiseFMeasureIAAResult;
import gleam.docservice.proxy.iaa.PairwiseKappaIAAResult;

/**
 * Enumerated type defining the list of available IAA algorithms.
 */
public enum IAAAlgorithm {
  ALL_WAYS_F_MEASURE("all-ways-f-measure", AllWaysFMeasureIAAResult.class),
  ALL_WAYS_KAPPA("all-ways-kappa", AllWaysKappaIAAResult.class),
  PAIRWISE_F_MEASURE("pairwise-f-measure", PairwiseFMeasureIAAResult.class),
  PAIRWISE_KAPPA("pairwise-kappa", PairwiseKappaIAAResult.class);

  private String algorithmName;
  
  private Class<? extends IAAResult> resultType;

  private IAAAlgorithm(String algorithmName, Class<? extends IAAResult> resultType) {
    this.algorithmName = algorithmName;
    this.resultType = resultType;
  }

  public String algorithmName() {
    return algorithmName;
  }
  
  public Class<? extends IAAResult> resultType() {
    return resultType;
  }
}
