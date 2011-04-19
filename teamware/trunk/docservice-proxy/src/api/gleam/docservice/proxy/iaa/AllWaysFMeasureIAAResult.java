/*
 *  AllWaysFMeasureIAAResult.java
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
 * Interface representing the results of an all-ways F-measure IAA
 * calculation.
 */
public interface AllWaysFMeasureIAAResult extends IAAResult {
  /**
   * The overall F-measure for this calculation.
   */
  public FMeasure getOverallFMeasure();

  /**
   * The overall F-measure for a particular annotation set against the
   * key set. If labels are being used, this is the average over all
   * label values of {@link #getFMeasureForLabel}.
   */
  public FMeasure getFMeasure(String asName);

  /**
   * The F-measure for a particular annotation set against the key set
   * for a particular label value. Returns <code>null</code> if labels
   * are not being used or if the given value is not valid.
   */
  public FMeasure getFMeasureForLabel(String asName, String label);
}
