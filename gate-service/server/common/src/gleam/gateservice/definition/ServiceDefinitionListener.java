/*
 *  ServiceDefinitionListener.java
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
package gleam.gateservice.definition;

/**
 * Interface to be implemented by objects that wish to be notified of
 * changes to a GaS service definition.
 */
public interface ServiceDefinitionListener {
  /**
   * Called when an input or output annotation set has been added to the
   * definition. Use {@link ServiceDefinitionEvent#getDirection} to
   * determine whether it is an input or output set.
   */
  public void annotationSetAdded(ServiceDefinitionEvent e);

  /**
   * Called when an input or output annotation set has been removed from
   * the definition. Use {@link ServiceDefinitionEvent#getDirection} to
   * determine whether it is an input or output set.
   */
  public void annotationSetRemoved(ServiceDefinitionEvent e);

  /**
   * Called when a new GaS parameter has been added to the definition.
   */
  public void parameterAdded(ServiceDefinitionEvent e);

  /**
   * Called when a GaS parameter has been completely removed from the
   * definition. Before this call, any feature and parameter mappings
   * for the GaS parameter will already have been removed, firing their
   * corresponding events.
   */
  public void parameterRemoved(ServiceDefinitionEvent e);

  /**
   * Called when a GaS parameter has been changed from optional to
   * required or vice-versa.
   */
  public void parameterChanged(ServiceDefinitionEvent e);

  /**
   * Called when a feature mapping has been added to this definition for
   * a particular GaS parameter.
   */
  public void featureMappingAdded(ServiceDefinitionEvent e);

  /**
   * Called when a feature mapping has been removed from this definition
   * for a particular GaS parameter.
   */
  public void featureMappingRemoved(ServiceDefinitionEvent e);

  /**
   * Called when a parameter mapping has been added to this definition
   * for a particular GaS parameter.
   */
  public void parameterMappingAdded(ServiceDefinitionEvent e);

  /**
   * Called when a parameter mapping has been removed from this
   * definition for a particular GaS parameter.
   */
  public void parameterMappingRemoved(ServiceDefinitionEvent e);
}
