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
