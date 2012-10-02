/*
 *  ServiceDefinitionUtils.java
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

import static gleam.gateservice.GasConstants.PARAM_DEFAULTS_FEATURE;
import gate.CorpusController;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.ProcessingResource;
import gate.creole.ParameterException;
import gate.util.GateException;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Utility class with methods to apply the parameter mappings in a
 * service definition to a GATE document and controller.
 */
public class ServiceDefinitionUtils {

  private static final Log log = LogFactory
          .getLog(ServiceDefinitionUtils.class);

  /**
   * Private constructor - this class should not be instantiated.
   */
  private ServiceDefinitionUtils() {
  }

  /**
   * Sets the runtime parameters of the PRs in this application based on
   * the parameter mappings in this definition. Runtime parameters that
   * do not have values specified by this definition will take their
   * default values from their respective creole.xml files.
   * 
   * @param gasParameterValues
   * @param application
   * @throws ParameterException
   * @throws GateException
   */
  @SuppressWarnings("unchecked")
  public static void apply(GateServiceDefinition def,
          Map<String, Object> gasParameterValues, CorpusController application)
          throws GateException {
    log.debug("Applying parameters to application");
    for(String paramName : def.getRequiredParameterNames()) {
      if(!gasParameterValues.containsKey(paramName)) {
        throw new MissingParameterException(
                "No value provided for required parameter " + paramName);
      }
    }
    for(Object prObj : application.getPRs()) {
      ProcessingResource pr = (ProcessingResource)prObj;
      String prName = pr.getName();
      // start with default parameters as saved by the worker init()
      FeatureMap runtimeParams = Factory.newFeatureMap();
      runtimeParams.putAll((FeatureMap)pr.getFeatures().get(
              PARAM_DEFAULTS_FEATURE));
      // get any PR param -> GaS param mappings for this PR
      Map<String, String> paramMappings = def.reverseParameterMappings
              .get(prName);
      if(paramMappings != null) {
        // apply these mappings, overriding the defaults
        for(Map.Entry<String, String> paramMapping : paramMappings.entrySet()) {
          if(gasParameterValues.containsKey(paramMapping.getValue())) {
            runtimeParams.put(paramMapping.getKey(), gasParameterValues
                    .get(paramMapping.getValue()));
          }
        }
      }
      log.debug("Using parameter values " + runtimeParams);
      pr.setParameterValues(runtimeParams);
    }
  }

  /**
   * Sets features on a document based on the mappings in this
   * definition.
   * 
   * @param gasParameterValues
   * @param doc
   */
  @SuppressWarnings("unchecked")
  public static void apply(GateServiceDefinition def,
          Map<String, Object> gasParameterValues, Document doc) {
    for(String paramName : def.getRequiredParameterNames()) {
      if(!gasParameterValues.containsKey(paramName)) {
        throw new MissingParameterException(
                "No value provided for required parameter " + paramName);
      }
    }
    if(def.featureMappings != null) {
      FeatureMap documentFeatures = doc.getFeatures();
      for(Map.Entry<String, List<String>> featureMapping : def.featureMappings
              .entrySet()) {
        if(gasParameterValues.containsKey(featureMapping.getKey())) {
          for(String featureName : featureMapping.getValue()) {
            documentFeatures.put(featureName, gasParameterValues
                    .get(featureMapping.getKey()));
          }
        }
      }
    }
  }
}