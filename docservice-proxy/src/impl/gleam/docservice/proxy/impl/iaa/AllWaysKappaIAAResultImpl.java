/*
 *  AllWaysKappaIAAResultImpl.java
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gleam.docservice.IAADetail;
import gleam.docservice.IAAResult;
import gleam.docservice.iaa.AllWaysKappaDetail;
import gleam.docservice.proxy.DSProxyException;
import gleam.docservice.proxy.IAAAlgorithm;
import gleam.docservice.proxy.iaa.AllWaysKappaIAAResult;

/**
 * Implementation class for the all-ways kappa IAA results.
 */
public class AllWaysKappaIAAResultImpl extends IAAResultImpl implements
                                                            AllWaysKappaIAAResult {
  private static final Log log = LogFactory
          .getLog(AllWaysKappaIAAResultImpl.class);

  /**
   * The detail object returned from the doc service.
   */
  private AllWaysKappaDetail detailFromServer;

  public AllWaysKappaIAAResultImpl(IAAResult resultFromServer)
          throws DSProxyException {
    super(resultFromServer, IAAAlgorithm.ALL_WAYS_KAPPA);
    IAADetail iaaDetail = resultFromServer.getDetail();
    if(!(iaaDetail instanceof AllWaysKappaDetail)) {
      log.error("Wrong detail type received from doc service.  Expected "
              + AllWaysKappaDetail.class.getName() + " but got "
              + iaaDetail.getClass().getName());
      throw new DSProxyException("Unexpected response type from doc service");
    }
    this.detailFromServer = (AllWaysKappaDetail)iaaDetail;
  }

  public float getKappaDF() {
    return detailFromServer.getOverallKappaDF();
  }

  public float getKappaSC() {
    return detailFromServer.getOverallKappaSC();
  }

  public float getObservedAgreement() {
    return detailFromServer.getOverallObservedAgreement();
  }

}
