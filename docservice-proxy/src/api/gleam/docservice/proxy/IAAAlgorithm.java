/*
 *  IAAAlgorithm.java
 *
 *  Copyright (c) 2006-2011, The University of Sheffield.
 *
 *  This file is part of GATE Teamware (see http://gate.ac.uk/teamware/), 
 *  and is free software, licenced under the GNU Affero General Public License,
 *  Version 3, November 2007 (also included with this distribution as file 
 *  LICENCE-AGPL3.html).
 *
 *  A commercial licence is also available for organisations whose business
 *  models preclude the adoption of open source and is subject to a licence
 *  fee charged by the University of Sheffield. Please contact the GATE team
 *  (see http://gate.ac.uk/g8/contact) if you require a commercial licence.
 *
 *  $Id$
 */
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
