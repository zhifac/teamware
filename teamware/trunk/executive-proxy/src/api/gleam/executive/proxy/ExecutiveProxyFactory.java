/*
 * This file is part of GLEAM, licenced under the GNU Lesser General Public
 * Licence.
 *
 * (c) 2006 The University of Sheffield
 * 
 * $Id$
 */
package gleam.executive.proxy;

import java.net.URI;

/**
 * Factory class to provide access to ExecutiveProxy objects.
 */
public interface ExecutiveProxyFactory {
  /**
   * Get an {@link ExecutiveProxy} for the executive web service at the
   * given URL.
   * 
   * @param executiveLocation the location of the web service endpoint
   * @return a proxy for this executive.
   * @throws ExecutiveProxyException
   */
  public ExecutiveProxy getExecutiveProxy(URI executiveLocation)
          throws ExecutiveProxyException;
}
