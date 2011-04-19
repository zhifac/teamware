/*
 *  TaskFormParameterWrapper.java
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
package gleam.executive.webapp.wrapp;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.jbpm.context.def.VariableAccess;

//TODO Javadoc
public class TaskFormParameterWrapper {
  protected String label = null;

  protected String name = null;

  protected String description = null;

  protected Object value = null;

  protected boolean readable = true;

  protected boolean writable = true;

  protected boolean booleanValue = false;

  protected boolean dateValue = false;

  protected boolean required = true;
  
  protected boolean readOnly = false;

  public TaskFormParameterWrapper() {
  }

  public TaskFormParameterWrapper(VariableAccess variableAccess, Object value) {
    this.label = variableAccess.getMappedName();
    if(value instanceof Date) {
      DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
      this.value = df.format((Date)value);
    }
    else {
      this.value = value;
    }
    this.name = variableAccess.getVariableName();
    this.readable = variableAccess.isReadable();
    this.writable = variableAccess.isWritable();
    this.required = variableAccess.isRequired();
    this.readOnly = !variableAccess.isWritable();
    this.booleanValue = value != null && value instanceof Boolean;
    this.dateValue = value != null && value instanceof Date;
  }

  public TaskFormParameterWrapper(TaskFormParameterWrapper other) {
    this.label = other.label;
    this.name = other.name;
    this.description = other.description;
    this.value = other.value;
    this.readable = other.readable;
    this.writable = other.writable;
    this.required = other.required;
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
    return readable;
  }

  public void setReadable(boolean isReadable) {
    this.readable = isReadable;
  }

  public boolean isRequired() {
    return required;
  }

  public void setRequired(boolean isRequired) {
    this.required = isRequired;
  }

  public boolean isWritable() {
    return writable;
  }

  public boolean isReadOnly() {
    return readOnly;
  }

  public void setReadOnly(boolean readOnly) {
	this.readOnly = readOnly;
}

public void setWritable(boolean isWritable) {
    this.writable = isWritable;
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
