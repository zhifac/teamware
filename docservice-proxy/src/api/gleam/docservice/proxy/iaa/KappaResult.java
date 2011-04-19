/*
 *  KappaResult.java
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

public interface KappaResult {
  public float getKappaCohen();
  public float getKappaPi();
  public float getObservedAgreement();
  
  /**
   * Returns the specific agreement score on the given
   * label value for this pair of annotators.
   * 
   * @param labelValue the label value to check
   * @param positive <code>true</code>: the positive agreement,
   *          <code>false</code>: the negative agreement.
   */
  public float getSpecificAgreement(String labelValue, boolean positive);
  
  /**
   * Returns the given entry in the confusion matrix for
   * this pair of annotators.  Either or both of the key and
   * response labels may be <code>null</code>, meaning the
   * "no-label" column (respectively row) in the matrix.
   * 
   * @param keyLabel the key label value, or <code>null</code>
   * @param responseLabel the response label value, or
   *         <code>null</code>.
   */
  public float getConfusionMatrixEntry(String keyLabel, String responseLabel);
}
