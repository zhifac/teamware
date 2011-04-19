/*
 *  IAADetail.java
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
