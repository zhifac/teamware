/*
 * This file is part of SAFE, licenced under the GNU Lesser General Public
 * Licence.
 *
 * (c) 2006 The University of Sheffield
 *
 * $Id$
 */
package gleam.gateservice.client;

import java.net.URI;

/**
 * Factory class to provide access to GateServiceClient objects.
 */
public interface GateServiceClientFactory {
  /**
   * Get a {@link GateServiceClient} for the GaS at the given URI.
   *
   * @param gasLocation the location of the web service endpoint
   * @return a client for this GaS.
   * @throws GateServiceClientException if an error occurs contacting the
   *         service or configuring the client.
   */
  public GateServiceClient getGateServiceClient(URI gasLocation)
          throws GateServiceClientException;
}
