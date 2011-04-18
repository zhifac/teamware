/*
 * This file is part of GLEAM, licenced under the GNU Lesser General Public
 * Licence.
 *
 * (c) 2006 The University of Sheffield
 * 
 * $Id$
 */
package gleam.docservice.proxy;

import java.net.URI;

/**
 * Factory class to provide access to DocServiceProxy objects.
 */
public abstract interface DocServiceProxyFactory {
  /**
   * Get a proxy for the given doc service.
   * 
   * @param docServiceLocation the URL of the doc service endpoint.
   * @return TODO
   */
  public DocServiceProxy getDocServiceProxy(URI docServiceLocation)
          throws DSProxyException;
}
