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
