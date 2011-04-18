package gleam.executive.workflow.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.jbpm.context.def.VariableAccess;

//TODO Javadoc
public class TaskFormParameterBean {
  protected String label = null;

  protected String name = null;

  protected String description = null;

  protected Object value = null;

  protected boolean isReadable = true;

  protected boolean isWritable = true;

  protected boolean booleanValue = false;

  protected boolean dateValue = false;

  protected boolean isRequired = true;

  public TaskFormParameterBean() {
  }

  public TaskFormParameterBean(VariableAccess variableAccess, Object value) {
    this.label = variableAccess.getMappedName();
    if(value instanceof Date) {
      DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
      this.value = df.format((Date)value);
    }
    else {
      this.value = value;
    }
    this.name = variableAccess.getVariableName();
    this.isReadable = variableAccess.isReadable();
    this.isWritable = variableAccess.isWritable();
    this.isRequired = variableAccess.isRequired();
    this.booleanValue = value != null && value instanceof Boolean;
    this.dateValue = value != null && value instanceof Date;
  }

  public TaskFormParameterBean(TaskFormParameterBean other) {
    this.label = other.label;
    this.name = other.name;
    this.description = other.description;
    this.value = other.value;
    this.isReadable = other.isReadable;
    this.isWritable = other.isWritable;
    this.isRequired = other.isRequired;
  }

  public String toString() {
    return "(" + label + "," + value + ")";
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public boolean isReadable() {
    return isReadable;
  }

  public void setReadable(boolean isReadable) {
    this.isReadable = isReadable;
  }

  public boolean isRequired() {
    return isRequired;
  }

  public void setRequired(boolean isRequired) {
    this.isRequired = isRequired;
  }

  public boolean isWritable() {
    return isWritable;
  }

  public boolean isReadOnly() {
    return !isWritable;
  }

  public void setWritable(boolean isWritable) {
    this.isWritable = isWritable;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public Object getValue() {
    return value;
  }

  public void setValue(Object value) {
    this.value = value;
  }

  public boolean isBooleanValue() {
    return booleanValue;
  }

  public void setBooleanValue(boolean booleanValue) {
    this.booleanValue = booleanValue;
  }

  public boolean isDateValue() {
    return dateValue;
  }

  public void setDateValue(boolean dateValue) {
    this.dateValue = dateValue;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
