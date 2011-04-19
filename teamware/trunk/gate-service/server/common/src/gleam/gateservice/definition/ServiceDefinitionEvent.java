/*
 *  ServiceDefinitionEvent.java
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

import java.util.EventObject;

public class ServiceDefinitionEvent extends EventObject {

  public static enum Type {
    ANNOTATION_SET_ADDED, ANNOTATION_SET_REMOVED, PARAMETER_ADDED,
    PARAMETER_REMOVED, PARAMETER_CHANGED, FEATURE_MAPPING_ADDED,
    FEATURE_MAPPING_REMOVED, PARAMETER_MAPPING_ADDED, PARAMETER_MAPPING_REMOVED
  }

  public static enum Direction {
    IN, OUT
  }

  private Type type;
  
  private String annotationSetName;
  
  private Direction direction;

  private String gasParameterName;

  private boolean optional;

  private String featureName;

  private String prName;

  private String prParameterName;

  public ServiceDefinitionEvent(GateServiceDefinition source, Type type) {
    super(source);
    this.type = type;
  }

  public Type getType() {
    return type;
  }

  public String getAnnotationSetName() {
    return annotationSetName;
  }

  protected void setAnnotationSetName(String annotationSetName) {
    this.annotationSetName = annotationSetName;
  }

  public Direction getDirection() {
    return direction;
  }

  protected void setDirection(Direction direction) {
    this.direction = direction;
  }

  public String getGasParameterName() {
    return gasParameterName;
  }

  protected void setGasParameterName(String gasParameterName) {
    this.gasParameterName = gasParameterName;
  }

  public boolean isOptional() {
    return optional;
  }

  protected void setOptional(boolean optional) {
    this.optional = optional;
  }

  public String getFeatureName() {
    return featureName;
  }

  protected void setFeatureName(String featureName) {
    this.featureName = featureName;
  }

  public String getPrName() {
    return prName;
  }

  protected void setPrName(String prName) {
    this.prName = prName;
  }

  public String getPrParameterName() {
    return prParameterName;
  }

  protected void setPrParameterName(String prParameterName) {
    this.prParameterName = prParameterName;
  }

}
