/*
 *  RobustByDefaultExecutiveProxyFactoryImpl.java
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
package gleam.executive.proxy.impl;

/**
 * Simple subclass of ExecutiveProxyFactoryImpl that sets its timeout to
 * zero and enables auto-retry with up to 3 retries.
 */
public class RobustByDefaultExecutiveProxyFactoryImpl
                                                     extends
                                                       ExecutiveProxyFactoryImpl {
  public RobustByDefaultExecutiveProxyFactoryImpl() {
    super();
    this.setTimeout(0);
    this.setAutoRetryCount(3);
  }
}