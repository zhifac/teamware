/*
 * This file is part of GLEAM, licenced under the GNU Lesser General Public
 * Licence.
 *
 * (c) 2006 The University of Sheffield
 * 
 * $Id$
 */
package gleam.gateservice;

/**
 * Various constants for the GATE Service.
 */
public interface GasConstants {
  // default name of the JMS queue used for a gate-service
  public static final String DEFAULT_QUEUE_NAME = "GLEAM.GATEService";

  // namespace URIs
  public static final String SERVICE_DEF_NAMESPACE_URI = "http://gate.ac.uk/gleam/gate-service/definition";

  // Feature name used to store runtime parameter defaults on a PR
  public static final String PARAM_DEFAULTS_FEATURE = "gleam.gateservice.runtimeDefaults";
}
