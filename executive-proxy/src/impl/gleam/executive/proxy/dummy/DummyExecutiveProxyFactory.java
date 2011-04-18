/*
 * This file is part of GLEAM, licenced under the GNU Lesser General Public
 * Licence.
 *
 * (c) 2006 The University of Sheffield
 * 
 * $Id$
 */
package gleam.executive.proxy.dummy;

import java.net.URI;

import gleam.executive.proxy.ExecutiveProxy;
import gleam.executive.proxy.ExecutiveProxyFactory;

/**
 * Factory for the dummy ExecutiveProxy which always returns the same
 * dummy proxy instance.
 */
public class DummyExecutiveProxyFactory implements ExecutiveProxyFactory {

  private static final ExecutiveProxy THE_PROXY = new DummyExecutiveProxy();

  /**
   * Returns the single shared DummyExecutiveProxy instance.
   */
  public ExecutiveProxy getExecutiveProxy(URI executiveLocation) {
    return THE_PROXY;
  }

}
