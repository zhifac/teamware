/*
 *  GasConstants.java
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
