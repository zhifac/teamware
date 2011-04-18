/*
 * This file is part of SAFE, licenced under the GNU Lesser General Public
 * Licence.
 *
 * (c) 2006 The University of Sheffield
 * 
 * $Id$
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
