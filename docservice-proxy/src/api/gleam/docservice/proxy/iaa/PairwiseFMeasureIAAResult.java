/*
 *  PairwiseFMeasureIAAResult.java
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
package gleam.docservice.proxy.iaa;

import gleam.docservice.proxy.IAAResult;

/**
 * Interface representing the results of a pairwise F-measure IAA
 * calculation.
 */
public interface PairwiseFMeasureIAAResult extends IAAResult {
  /**
   * The overall F-measure for this calculation.
   */
  public FMeasure getOverallFMeasure();

  /**
   * For the pair (as1, as2), determine which is included in this result
   * as the key.
   * 
   * The results of a pairwise F-measure calculation include the results
   * for every pair of annotation sets, but only one way around, i.e. if
   * the result includes <i>A</i> against <i>B</i> then it will not
   * include <i>B</i> against <i>A</i>. The one can be obtained from
   * the other by exchanging missing for spurious and precision for
   * recall.
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
   * The results of a pairwise F-measure calculation include the results
   * for every pair of annotation sets, but only one way around, i.e. if
   * the result includes <i>A</i> against <i>B</i> then it will not
   * include <i>B</i> against <i>A</i>. The one can be obtained from
   * the other by exchanging missing for spurious and precision for
   * recall.
   * 
   * @return the one of as1 and as2 which is the key for this pairing,
   *         or <code>null</code> if this pairing is not present in
   *         this result.
   */
  public String getResponseASName(String as1, String as2);

  /**
   * The overall F-measure for the given pair of annotators. Use
   * {@link #getKeyASName} and {@link #getResponseASName} to determine
   * which is the key and which is the response. If labels are in use,
   * this will be the average over all label values of the relevant
   * {@link #getFMeasureForLabel} values.
   */
  public FMeasure getFMeasureForPair(String as1, String as2);

  /**
   * The F-measure for the given pair of annotators for a particular
   * label value. Use {@link #getKeyASName} and
   * {@link #getResponseASName} to determine which is the key and which
   * is the response. Returns <code>null</code> if labels are not in
   * use, or if the given value is not valid.
   */
  public FMeasure getFMeasureForLabel(String as1, String as2, String labelValue);
}
