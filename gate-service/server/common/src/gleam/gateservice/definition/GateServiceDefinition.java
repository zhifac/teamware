/*
 *  GateServiceDefinition.java
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

import static gleam.gateservice.GasConstants.SERVICE_DEF_NAMESPACE_URI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jdom.Element;
import org.jdom.Namespace;

/**
 * This class defines the parameters of a GATE Service. GATE Service
 * parameters are String-valued, and each GaS parameter maps to any
 * number of targets. Each target is either:
 * <ul>
 * <li>a particular feature of the document being processed or</li>
 * <li>a runtime parameter of a named PR in the application pipeline.</li>
 * </ul>
 * 
 * Although GaS parameters are all Strings, they are set using the
 * standard GATE
 * {@link Resource#setParameterValue(String, Object) setParameterValue}
 * method, and so will be converted to the appropriate type for the
 * parameter by GATE. This class also stores the annotation set names
 * required and returned by the service. {@link ServiceDefinitionUtils}
 * provides methods to apply the parameter mappings to a GATE document
 * and application.
 */
public class GateServiceDefinition {

  /**
   * A single parameter definition, which contains a PR name/parameter
   * name pair.
   */
  public class ParameterMapping {
    private String paramName;

    private String prName;

    /**
     * Create a parameter definition that maps to a runtime parameter of
     * a PR in the pipeline.
     * 
     * @param prName the name of the PR. If there are multiple PRs in
     *          the application with the same name the results are
     *          undefined. Don't do it.
     * @param paramName the PR parameter name.
     */
    public ParameterMapping(String prName, String paramName) {
      this.prName = prName;
      this.paramName = paramName;
    }

    public String getParamName() {
      return paramName;
    }

    public String getPrName() {
      return prName;
    }

    @Override
    public boolean equals(Object obj) {
      if(!(obj instanceof ParameterMapping)) {
        return false;
      }
      else {
        ParameterMapping other = (ParameterMapping)obj;
        return (stringsEqual(this.prName, other.prName) && stringsEqual(
                this.paramName, other.paramName));
      }
    }

    private final boolean stringsEqual(String s1, String s2) {
      return (s1 == null) ? (s2 == null) : s1.equals(s2);
    }

    @Override
    public int hashCode() {
      return stringHashCode(prName) ^ stringHashCode(paramName);
    }

    private final int stringHashCode(String s) {
      return (s == null) ? 0 : s.hashCode();
    }
  }

  /**
   * Names of the annotation sets that this service requires as input.
   * In GLEAM mode the caller specifies which annotation set from the
   * doc service should be copied into which annotation set in the
   * temporary document processed by the service object. In GATE mode
   * these annotation sets must be populated in the input document.
   * 
   * The set may contain <code>null</code> as one of its elements,
   * denoting the unnamed default annotation set.
   */
  private Set<String> inputAnnotationSetNames = new HashSet<String>();

  /**
   * Names of the annotation sets that this service uses for output. In
   * GLEAM mode, the caller specifies which annotation set in the doc
   * service each of these should be mapped back into. In GATE mode
   * these annotation sets are returned to the caller.
   * 
   * The set may contain <code>null</code> as one of its elements,
   * denoting the unnamed default annotation set.
   */
  private Set<String> outputAnnotationSetNames = new HashSet<String>();

  /**
   * Map holding the mappings to PR parameters for each GaS parameter.
   * Package visible as it is used by ServiceDefinitionUtils.
   */
  Map<String, List<ParameterMapping>> parameterMappings =
          new HashMap<String, List<ParameterMapping>>();

  /**
   * Reverse mapping of PR names -> PR parameter names -> GaS parameter
   * names. Package visible as it is used by ServiceDefinitionUtils.
   */
  Map<String, Map<String, String>> reverseParameterMappings =
          new HashMap<String, Map<String, String>>();

  /**
   * Map holding the mappings to document features for each GaS
   * parameter. Package visible as it is used by ServiceDefinitionUtils.
   */
  Map<String, List<String>> featureMappings =
          new HashMap<String, List<String>>();

  /**
   * Set holding all required parameter names.
   */
  private Set<String> requiredParameterNames = new HashSet<String>();

  /**
   * Set holding all optional parameter names.
   */
  private Set<String> optionalParameterNames = new HashSet<String>();

  /**
   * Get the names of all input annotation sets used by this service.
   */
  public Set<String> getInputAnnotationSetNames() {
    return Collections.unmodifiableSet(inputAnnotationSetNames);
  }

  /**
   * Get the names of all output annotation sets for this service.
   */
  public Set<String> getOutputAnnotationSetNames() {
    return Collections.unmodifiableSet(outputAnnotationSetNames);
  }

  /**
   * Add an input annotation set name for this service.
   * <code>setName</code> may be null, representing the unnamed
   * annotation set.
   */
  public void addInputAnnotationSet(String setName) {
    if(inputAnnotationSetNames.add(setName)) {
      fireAnnotationSetAdded(setName, true);
    }
  }

  /**
   * Remove an input annotation set name for this service.
   * <code>setName</code> may be null, representing the unnamed
   * annotation set.
   */
  public void removeInputAnnotationSet(String setName) {
    if(inputAnnotationSetNames.remove(setName)) {
      fireAnnotationSetRemoved(setName, true);
    }
  }

  /**
   * Add an output annotation set name for this service.
   * <code>setName</code> may be null, representing the unnamed
   * annotation set.
   */
  public void addOutputAnnotationSet(String setName) {
    if(outputAnnotationSetNames.add(setName)) {
      fireAnnotationSetAdded(setName, false);
    }
  }

  /**
   * Remove an output annotation set name for this service.
   * <code>setName</code> may be null, representing the unnamed
   * annotation set.
   */
  public void removeOutputAnnotationSet(String setName) {
    if(outputAnnotationSetNames.remove(setName)) {
      fireAnnotationSetRemoved(setName, false);
    }
  }

  /**
   * Get the names of all the parameters for this GaS.
   */
  public Set<String> getParameterNames() {
    Set<String> allParamNames = new HashSet<String>();
    allParamNames.addAll(requiredParameterNames);
    allParamNames.addAll(optionalParameterNames);
    return Collections.unmodifiableSet(allParamNames);
  }

  /**
   * Get the names of all required parameters for this GaS.
   */
  public Set<String> getRequiredParameterNames() {
    return Collections.unmodifiableSet(requiredParameterNames);
  }

  /**
   * Get the names of all optional parameters for this GaS.
   */
  public Set<String> getOptionalParameterNames() {
    return Collections.unmodifiableSet(optionalParameterNames);
  }

  /**
   * Register a GaS parameter with this definition.
   * 
   * @param paramName name of the parameter to create.
   */
  public void addParameter(String paramName) {
    addParameter(paramName, false);
  }

  /**
   * Register a GaS parameter with this definition.
   * 
   * @param paramName name of the parameter to create.
   * @param optional whether this parameter is optional (true) or
   *          required (false).
   */
  public void addParameter(String paramName, boolean optional) {
    if(optional) {
      if(requiredParameterNames.contains(paramName)
              || !optionalParameterNames.add(paramName)) {
        throw new IllegalArgumentException(
                "Definition already contains parameter " + paramName);
      }
    }
    else {
      if(optionalParameterNames.contains(paramName)
              || !requiredParameterNames.add(paramName)) {
        throw new IllegalArgumentException(
                "Definition already contains parameter " + paramName);
      }
    }
    fireParameterAdded(paramName, optional);
  }

  public void removeParameter(String paramName) {
    if(optionalParameterNames.contains(paramName)
            || requiredParameterNames.contains(paramName)) {
      removeParameterMappings(paramName);
      removeFeatureMappings(paramName);
      optionalParameterNames.remove(paramName);
      requiredParameterNames.remove(paramName);
      fireParameterRemoved(paramName);
    }
  }

  public void setOptional(String paramName, boolean optional) {
    if(!requiredParameterNames.contains(paramName)
            && !optionalParameterNames.contains(paramName)) {
      throw new IllegalArgumentException("Unrecognised parameter " + paramName);
    }
    boolean changed = false;
    if(optional) {
      changed =
              requiredParameterNames.remove(paramName)
                      && optionalParameterNames.add(paramName);
    }
    else {
      changed =
              optionalParameterNames.remove(paramName)
                      && requiredParameterNames.add(paramName);
    }

    if(changed) {
      fireParameterChanged(paramName, optional);
    }
  }

  /**
   * Adds a parameter mapping to one of the maps.
   * 
   * @param gasParam GaS parameter to add the mapping for.
   * @param map map to add the mapping to
   * @param value value to add
   */
  private <T> boolean addMapping(String gasParam, Map<String, List<T>> map,
          T value) {
    if(!requiredParameterNames.contains(gasParam)
            && !optionalParameterNames.contains(gasParam)) {
      throw new IllegalArgumentException("Unrecognised parameter " + gasParam);
    }

    List<T> mappingsForParam = map.get(gasParam);
    if(mappingsForParam == null) {
      mappingsForParam = new ArrayList<T>();
      map.put(gasParam, mappingsForParam);
    }

    return mappingsForParam.add(value);
  }

  /**
   * Adds a parameter mapping to one of the maps.
   * 
   * @param gasParam GaS parameter to add the mapping for.
   * @param map map to add the mapping to
   * @param value value to add
   */
  private <T> boolean removeMapping(String gasParam, Map<String, List<T>> map,
          T value) {
    if(!requiredParameterNames.contains(gasParam)
            && !optionalParameterNames.contains(gasParam)) {
      throw new IllegalArgumentException("Unrecognised parameter " + gasParam);
    }

    List<T> mappingsForParam = map.get(gasParam);
    if(mappingsForParam == null) {
      return false;
    }
    else {
      return mappingsForParam.remove(value);
    }
  }

  /**
   * Adds a document feature mapping.
   * 
   * @param gasParam GaS parameter to add the mapping for.
   * @param featureName feature name to receive the parameter value.
   */
  public void addFeatureMapping(String gasParam, String featureName) {
    if(addMapping(gasParam, featureMappings, featureName)) {
      fireFeatureMappingAdded(gasParam, featureName);
    }
  }

  /**
   * Removes a document feature mapping.
   * 
   * @param gasParam GaS parameter to remove the mapping for.
   * @param featureName feature name to remove from the mapping set.
   */
  public void removeFeatureMapping(String gasParam, String featureName) {
    if(removeMapping(gasParam, featureMappings, featureName)) {
      fireFeatureMappingRemoved(gasParam, featureName);
    }
  }

  /**
   * Remove all the feature mappings for the given GaS parameter.
   * 
   * @param gasParam the GaS parameter name
   */
  public void removeFeatureMappings(String gasParam) {
    if(featureMappings.containsKey(gasParam)) {
      // copy mappings into a new array to avoid concurrent modification
      // exception
      String[] mappings =
              featureMappings.get(gasParam).toArray(
                      new String[featureMappings.get(gasParam).size()]);
      for(String featureName : mappings) {
        removeFeatureMapping(gasParam, featureName);
      }
    }
  }

  /**
   * Adds a PR runtime parameter mapping.
   * 
   * @param gasParam GaS parameter to add the mapping for.
   * @param prName the name of the PR. If there are multiple PRs in the
   *          application with the same name the results are undefined.
   *          Don't do it.
   * @param prParam the PR parameter name.
   */
  public void addParameterMapping(String gasParam, String prName, String prParam) {
    ParameterMapping pm = new ParameterMapping(prName, prParam);
    if(addMapping(gasParam, parameterMappings, pm)) {
      // add the reverse mapping
      Map<String, String> reverseMapForPR =
              reverseParameterMappings.get(prName);
      if(reverseMapForPR == null) {
        reverseMapForPR = new HashMap<String, String>();
        reverseParameterMappings.put(prName, reverseMapForPR);
      }

      reverseMapForPR.put(prParam, gasParam);
      fireParameterMappingAdded(gasParam, pm);
    }
  }

  public void removeParameterMapping(String gasParam, String prName,
          String prParam) {
    ParameterMapping pm = new ParameterMapping(prName, prParam);
    removeParameterMapping(gasParam, pm);
  }

  private void removeParameterMapping(String gasParam, ParameterMapping pm) {
    if(removeMapping(gasParam, parameterMappings, pm)) {
      // remove the reverse mapping
      Map<String, String> reverseMapForPR =
              reverseParameterMappings.get(pm.getPrName());
      if(reverseMapForPR != null) {
        reverseMapForPR.remove(pm.getParamName());
      }
      fireParameterMappingRemoved(gasParam, pm);
    }
  }

  /**
   * Remove all the feature mappings for the given GaS parameter.
   * 
   * @param gasParam the GaS parameter name
   */
  public void removeParameterMappings(String gasParam) {
    if(parameterMappings.containsKey(gasParam)) {
      // copy mappings into a new array to avoid concurrent modification
      // exception
      ParameterMapping[] mappings =
              parameterMappings.get(gasParam).toArray(
                      new ParameterMapping[parameterMappings.get(gasParam)
                              .size()]);
      for(ParameterMapping pm : mappings) {
        removeParameterMapping(gasParam, pm);
      }
    }
  }

  /**
   * Returns the list of parameter mappings for the given GaS parameter.
   */
  public List<ParameterMapping> getParameterMappings(String gasParam) {
    return getMappings(gasParam, parameterMappings);
  }

  /**
   * Returns the reverse map for the given PR name. That is, if this
   * definition contains a parameter mapping for the GaS parameter named
   * <code>gasParam</code> to the parameter <code>p</code> of the PR
   * <code>prName</code>, then the returned map will map the key
   * <code>p</code> to the value <code>gasParam</code>. This map is
   * unmodifiable but live, i.e. updates to this definition will be
   * reflected in the returned map.
   * 
   * @param prName the PR whose reverse mappings are required.
   */
  public Map<String, String> getReverseParameterMappingsForPR(String prName) {
    Map<String, String> reverseMapForPR = reverseParameterMappings.get(prName);
    if(reverseMapForPR == null) {
      reverseMapForPR = new HashMap<String, String>();
      reverseParameterMappings.put(prName, reverseMapForPR);
    }
    return Collections.unmodifiableMap(reverseMapForPR);
  }

  /**
   * Returns the list of document feature mappings for the given GaS
   * parameter.
   */
  public List<String> getFeatureMappings(String gasParam) {
    return getMappings(gasParam, featureMappings);
  }

  private <T> List<T> getMappings(String gasParam, Map<String, List<T>> map) {
    List<T> mappings = map.get(gasParam);
    if(mappings == null) {
      mappings = new ArrayList<T>();
      map.put(gasParam, mappings);
    }
    return Collections.unmodifiableList(mappings);
  }

  // ///// Event listener code ///// //

  private List<ServiceDefinitionListener> listeners;

  public synchronized void addServiceDefinitionListener(
          ServiceDefinitionListener l) {
    List<ServiceDefinitionListener> newListeners =
            new ArrayList<ServiceDefinitionListener>();
    if(listeners != null) {
      newListeners.addAll(listeners);
    }
    newListeners.add(l);
    listeners = newListeners;
  }

  public synchronized void removeServiceDefinitionListener(
          ServiceDefinitionListener l) {
    List<ServiceDefinitionListener> currentListeners = listeners;
    if(currentListeners != null) {
      List<ServiceDefinitionListener> newListeners =
              new ArrayList<ServiceDefinitionListener>();
      for(ServiceDefinitionListener listener : currentListeners) {
        if(listener != l) {
          newListeners.add(listener);
        }
      }
      listeners = newListeners;
    }
  }

  protected void fireAnnotationSetAdded(String asName, boolean isInput) {
    if(listeners != null && !listeners.isEmpty()) {
      ServiceDefinitionEvent e =
              new ServiceDefinitionEvent(this,
                      ServiceDefinitionEvent.Type.ANNOTATION_SET_ADDED);
      e.setAnnotationSetName(asName);
      e.setDirection(isInput
              ? ServiceDefinitionEvent.Direction.IN
              : ServiceDefinitionEvent.Direction.OUT);
      for(ServiceDefinitionListener l : listeners) {
        l.annotationSetAdded(e);
      }
    }
  }

  protected void fireAnnotationSetRemoved(String asName, boolean isInput) {
    if(listeners != null && !listeners.isEmpty()) {
      ServiceDefinitionEvent e =
              new ServiceDefinitionEvent(this,
                      ServiceDefinitionEvent.Type.PARAMETER_REMOVED);
      e.setAnnotationSetName(asName);
      e.setDirection(isInput
              ? ServiceDefinitionEvent.Direction.IN
              : ServiceDefinitionEvent.Direction.OUT);
      for(ServiceDefinitionListener l : listeners) {
        l.annotationSetRemoved(e);
      }
    }
  }

  protected void fireParameterAdded(String paramName, boolean optional) {
    if(listeners != null && !listeners.isEmpty()) {
      ServiceDefinitionEvent e =
              new ServiceDefinitionEvent(this,
                      ServiceDefinitionEvent.Type.PARAMETER_ADDED);
      e.setGasParameterName(paramName);
      e.setOptional(optional);
      for(ServiceDefinitionListener l : listeners) {
        l.parameterAdded(e);
      }
    }
  }

  protected void fireParameterRemoved(String paramName) {
    if(listeners != null && !listeners.isEmpty()) {
      ServiceDefinitionEvent e =
              new ServiceDefinitionEvent(this,
                      ServiceDefinitionEvent.Type.PARAMETER_REMOVED);
      e.setGasParameterName(paramName);
      for(ServiceDefinitionListener l : listeners) {
        l.parameterRemoved(e);
      }
    }
  }

  protected void fireParameterChanged(String paramName, boolean optional) {
    if(listeners != null && !listeners.isEmpty()) {
      ServiceDefinitionEvent e =
              new ServiceDefinitionEvent(this,
                      ServiceDefinitionEvent.Type.PARAMETER_CHANGED);
      e.setGasParameterName(paramName);
      e.setOptional(optional);
      for(ServiceDefinitionListener l : listeners) {
        l.parameterChanged(e);
      }
    }
  }

  protected void fireFeatureMappingAdded(String gasParam, String feature) {
    if(listeners != null && !listeners.isEmpty()) {
      ServiceDefinitionEvent e =
              new ServiceDefinitionEvent(this,
                      ServiceDefinitionEvent.Type.FEATURE_MAPPING_ADDED);
      e.setGasParameterName(gasParam);
      e.setFeatureName(feature);
      for(ServiceDefinitionListener l : listeners) {
        l.featureMappingAdded(e);
      }
    }
  }

  protected void fireFeatureMappingRemoved(String gasParam, String feature) {
    if(listeners != null && !listeners.isEmpty()) {
      ServiceDefinitionEvent e =
              new ServiceDefinitionEvent(this,
                      ServiceDefinitionEvent.Type.FEATURE_MAPPING_REMOVED);
      e.setGasParameterName(gasParam);
      e.setFeatureName(feature);
      for(ServiceDefinitionListener l : listeners) {
        l.featureMappingRemoved(e);
      }
    }
  }

  protected void fireParameterMappingAdded(String gasParam, ParameterMapping pm) {
    if(listeners != null && !listeners.isEmpty()) {
      ServiceDefinitionEvent e =
              new ServiceDefinitionEvent(this,
                      ServiceDefinitionEvent.Type.PARAMETER_MAPPING_ADDED);
      e.setGasParameterName(gasParam);
      e.setPrName(pm.getPrName());
      e.setPrParameterName(pm.getParamName());
      for(ServiceDefinitionListener l : listeners) {
        l.parameterMappingAdded(e);
      }
    }
  }

  protected void fireParameterMappingRemoved(String gasParam,
          ParameterMapping pm) {
    if(listeners != null && !listeners.isEmpty()) {
      ServiceDefinitionEvent e =
              new ServiceDefinitionEvent(this,
                      ServiceDefinitionEvent.Type.FEATURE_MAPPING_REMOVED);
      e.setGasParameterName(gasParam);
      e.setPrName(pm.getPrName());
      e.setPrParameterName(pm.getParamName());
      for(ServiceDefinitionListener l : listeners) {
        l.parameterMappingRemoved(e);
      }
    }
  }

  /**
   * Construct an XML representation of this service definition.
   * 
   * <pre>
   *            &lt;service
   *                xmlns=&quot;http://gate.ac.uk/gleam/gate-service/definition&quot;&gt;
   *              &lt;annotationSets&gt;
   *                &lt;annotationSet name=&quot;setName&quot;
   *                   in=&quot;true|false&quot; out=&quot;true|false&quot; /&gt;
   *              &lt;/annotationSets&gt;
   *              &lt;parameters&gt;
   *                &lt;param name=&quot;annType&quot;&gt;
   *                  &lt;runtimeParameter prName=&quot;MyAnnotator&quot;
   *                            prParam=&quot;annotationType&quot; /&gt;
   *                  &lt;documentFeature name=&quot;gasAnnotationType&quot; /&gt;
   *                &lt;/param&gt;
   *              &lt;/parameters&gt;
   *            &lt;/service&gt;
   * </pre>
   */
  public Element toXml() {
    Element serviceElement = new Element("service", SERVICE_DEF_NAMESPACE_URI);
    serviceElement.addContent(annotationSetNamesToXml());
    serviceElement.addContent(parametersToXml());
    return serviceElement;
  }

  private Element annotationSetNamesToXml() {
    Element annotationSetsElement =
            new Element("annotationSets", SERVICE_DEF_NAMESPACE_URI);
    Set<String> allNames = new HashSet<String>(inputAnnotationSetNames);
    allNames.addAll(outputAnnotationSetNames);
    for(String name : allNames) {
      Element annotationSetElement =
              new Element("annotationSet", SERVICE_DEF_NAMESPACE_URI);
      // map null name to empty name attribute
      if(name == null) {
        annotationSetElement.setAttribute("name", "");
      }
      else {
        annotationSetElement.setAttribute("name", name);
      }
      if(inputAnnotationSetNames.contains(name)) {
        annotationSetElement.setAttribute("in", "true");
      }
      if(outputAnnotationSetNames.contains(name)) {
        annotationSetElement.setAttribute("out", "true");
      }
      annotationSetsElement.addContent(annotationSetElement);
    }

    return annotationSetsElement;
  }

  /**
   * Generates an XML element that defines the parameter mappings for
   * this GaS.
   * 
   * @return
   */
  private Element parametersToXml() {

    Element parametersElement =
            new Element("parameters", SERVICE_DEF_NAMESPACE_URI);
    for(String paramName : requiredParameterNames) {
      Element paramElement = new Element("param", SERVICE_DEF_NAMESPACE_URI);
      parametersElement.addContent(paramElement);
      populateParamElement(paramElement, paramName);
    }
    for(String paramName : optionalParameterNames) {
      Element paramElement = new Element("param", SERVICE_DEF_NAMESPACE_URI);
      paramElement.setAttribute("optional", "true");
      parametersElement.addContent(paramElement);
      populateParamElement(paramElement, paramName);
    }

    return parametersElement;
  }

  private void populateParamElement(Element paramElement, String paramName) {
    paramElement.setAttribute("name", paramName);
    if(parameterMappings.containsKey(paramName)) {
      for(ParameterMapping paramMapping : parameterMappings.get(paramName)) {
        Element pmElement =
                new Element("runtimeParameter", SERVICE_DEF_NAMESPACE_URI);
        paramElement.addContent(pmElement);
        pmElement.setAttribute("prName", paramMapping.getPrName());
        pmElement.setAttribute("prParam", paramMapping.getParamName());
      }
    }

    if(featureMappings.containsKey(paramName)) {
      for(String featureName : featureMappings.get(paramName)) {
        Element fmElement =
                new Element("documentFeature", SERVICE_DEF_NAMESPACE_URI);
        paramElement.addContent(fmElement);
        fmElement.setAttribute("name", featureName);
      }
    }

  }

  /**
   * Populate this service definition from the given XML element.
   * 
   * @param serviceElement the XML - this must be a <code>service</code>
   *          element in the GasConstants.SERVICE_DEF_NAMESPACE_URI
   *          namespace.
   * @throws IllegalArgumentException if the XML is not a valid for a
   *           parameters definition.
   */
  public void fromXml(Element serviceElement) {
    if(!"service".equals(serviceElement.getName())
            || !SERVICE_DEF_NAMESPACE_URI.equals(serviceElement
                    .getNamespaceURI())) {
      throw new IllegalArgumentException(
              "GateServiceDefinition can only be built from a {"
                      + SERVICE_DEF_NAMESPACE_URI + "}:service element");
    }

    Element annotationSetsElt =
            serviceElement.getChild("annotationSets", Namespace
                    .getNamespace(SERVICE_DEF_NAMESPACE_URI));
    if(annotationSetsElt != null) {
      List annotationSetElements =
              annotationSetsElt.getChildren("annotationSet", Namespace
                      .getNamespace(SERVICE_DEF_NAMESPACE_URI));
      for(Object annotationSetEltObj : annotationSetElements) {
        Element annotationSetElement = (Element)annotationSetEltObj;
        String asName = annotationSetElement.getAttributeValue("name");
        if(asName == null) {
          throw new IllegalArgumentException(
                  "annotationSet element must have a \"name\" attribute");
        }
        if("".equals(asName)) {
          // empty name attribute means default annotation set
          asName = null;
        }

        boolean isInOrOut = false;
        if("true".equals(annotationSetElement.getAttributeValue("in"))) {
          this.addInputAnnotationSet(asName);
          isInOrOut = true;
        }
        if("true".equals(annotationSetElement.getAttributeValue("out"))) {
          this.addOutputAnnotationSet(asName);
          isInOrOut = true;
        }

        if(!isInOrOut) {
          throw new IllegalArgumentException("annotationSet element for "
                  + ((asName == null) ? "default set" : ("set named \""
                          + asName + "\""))
                  + " must have at least one of in=true and out=true");
        }
      }
    }

    Element parametersElt =
            serviceElement.getChild("parameters", Namespace
                    .getNamespace(SERVICE_DEF_NAMESPACE_URI));
    if(parametersElt != null) {
      // add parameter mappings
      List paramElements =
              parametersElt.getChildren("param", Namespace
                      .getNamespace(SERVICE_DEF_NAMESPACE_URI));
      for(Object paramEltObj : paramElements) {
        Element paramElement = (Element)paramEltObj;
        String paramName = paramElement.getAttributeValue("name");
        if(paramName == null) {
          throw new IllegalArgumentException(
                  "param element must have a \"name\" attribute");
        }

        // is this parameter optional?
        String optionalAttributeValue =
                paramElement.getAttributeValue("optional");
        if("true".equals(optionalAttributeValue)) {
          this.addParameter(paramName, true);
        }
        else {
          this.addParameter(paramName, false);
        }

        // runtimeParameter elements
        List runtimeParamElements =
                paramElement.getChildren("runtimeParameter", Namespace
                        .getNamespace(SERVICE_DEF_NAMESPACE_URI));
        for(Object runtimeParamEltObj : runtimeParamElements) {
          Element runtimeParamElement = (Element)runtimeParamEltObj;
          String prName = runtimeParamElement.getAttributeValue("prName");
          String prParam = runtimeParamElement.getAttributeValue("prParam");
          if(prName == null || prParam == null) {
            throw new IllegalArgumentException(
                    "runtimeParameter element must have attributes \"prName\" "
                            + "and \"prParam\"");
          }
          this.addParameterMapping(paramName, prName, prParam);
        }

        List featureElements =
                paramElement.getChildren("documentFeature", Namespace
                        .getNamespace(SERVICE_DEF_NAMESPACE_URI));
        for(Object featureEltObj : featureElements) {
          Element featureElement = (Element)featureEltObj;
          String featureName = featureElement.getAttributeValue("name");
          if(featureName == null) {
            throw new IllegalArgumentException(
                    "feature element must have a \"name\" attribute");
          }
          this.addFeatureMapping(paramName, featureName);
        }
      }
    }
  }
}
