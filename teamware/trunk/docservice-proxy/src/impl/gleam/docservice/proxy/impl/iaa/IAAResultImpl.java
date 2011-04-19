/*
 *  IAAResultImpl.java
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
package gleam.docservice.proxy.impl.iaa;

import gleam.docservice.proxy.IAAAlgorithm;

public class IAAResultImpl implements gleam.docservice.proxy.IAAResult {

  protected gleam.docservice.IAAResult resultFromServer;

  protected IAAAlgorithm algorithm;

  public IAAResultImpl(gleam.docservice.IAAResult resultFromServer,
          IAAAlgorithm algorithm) {
    this.resultFromServer = resultFromServer;
    this.algorithm = algorithm;
  }

  public float getAgreement() {
    return resultFromServer.getAgreement();
  }

  public IAAAlgorithm getAlgorithm() {
    return algorithm;
  }

  public String[] getLabelValues() {
    return resultFromServer.getLabelValues();
  }

}
