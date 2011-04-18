/*
 * This file is part of SAFE, licenced under the GNU Lesser General Public
 * Licence.
 *
 * (c) 2007 The University of Sheffield
 *
 * $Id$
 */
package gleam.gateservice.definition.tool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import gleam.gateservice.definition.GateServiceDefinition;
import gleam.gateservice.definition.ServiceDefinitionEvent;
import gleam.gateservice.definition.ServiceDefinitionListener;

import javax.swing.table.AbstractTableModel;

/**
 * Table model for the list of GaS parameters in a service definition.
 * The table will have two columns, one for a boolean value (whether the
 * parameter is required or optional) and the other for the parameter
 * name.
 */
public class GasParametersTableModel extends AbstractTableModel implements
                                                               ServiceDefinitionListener {

  private GateServiceDefinition definition;

  /**
   * The list of all parameter names in the service definition. This
   * list is maintained by this class in response to events fired by the
   * definition.
   */
  private List<String> allParameterNames;

  /**
   * The list of optional parameter names. This list is live, i.e. when
   * an optional parameter is added to the service definition it will be
   * reflected in this set, so we can use membership in this set as the
   * criterion for whether a parameter is optional or required.
   */
  private Set<String> optionalParameterNames;

  public GasParametersTableModel(GateServiceDefinition def) {
    this.definition = def;
    allParameterNames = new ArrayList<String>(def.getParameterNames());
    // keep the list in lexicographic order
    Collections.sort(allParameterNames);
    optionalParameterNames = def.getOptionalParameterNames();
    def.addServiceDefinitionListener(this);
  }

  public int getColumnCount() {
    return 2;
  }

  public int getRowCount() {
    return allParameterNames.size();
  }

  /**
   * Get the value for a given cell of the table.
   */
  public Object getValueAt(int rowIndex, int columnIndex) {
    switch(columnIndex) {
      case 0: // required?
        if(optionalParameterNames.contains(getValueAt(rowIndex, 1))) {
          return Boolean.FALSE;
        }
        else {
          return Boolean.TRUE;
        }

      case 1: // name
        return allParameterNames.get(rowIndex);

      default:
        throw new IndexOutOfBoundsException("Illegal column number "
                + columnIndex);
    }
  }

  /**
   * Get the class for a table column. {@link Boolean} for column 0 and
   * {@link String} for column 1.
   */
  @Override
  public Class<?> getColumnClass(int columnIndex) {
    if(columnIndex == 0) {
      return Boolean.class;
    }
    else {
      return String.class;
    }
  }

  /**
   * Get the column name.
   */
  @Override
  public String getColumnName(int column) {
    if(column == 0) {
      return "Required?";
    }
    else {
      return "Name";
    }
  }

  /**
   * Only the "Required?" column is editable.
   */
  @Override
  public boolean isCellEditable(int rowIndex, int columnIndex) {
    return columnIndex == 0;
  }

  /**
   * Update the service definition when the user toggles the checkbox.
   * The event fired by the definition in response to this change will
   * update the table.
   */
  @Override
  public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
    if(columnIndex == 0) {
      definition.setOptional((String)getValueAt(rowIndex, 1),
              !((Boolean)aValue).booleanValue());
    }
  }

  /**
   * Check whether this definition contains the given parameter, either
   * as optional or required.
   */
  public boolean containsParameter(String name) {
    return allParameterNames.contains(name);
  }

  /**
   * Called when a parameter is added to the service definition, updates
   * the table.
   */
  public void parameterAdded(ServiceDefinitionEvent e) {
    String name = e.getGasParameterName();
    int index = Collections.binarySearch(allParameterNames, name);
    if(index < 0) {
      index = -index - 1;
      allParameterNames.add(index, name);
      fireTableRowsInserted(index, index);
    }
  }

  /**
   * Called when a parameter is removed from the service definition,
   * updates the table.
   */
  public void parameterRemoved(ServiceDefinitionEvent e) {
    String name = e.getGasParameterName();
    int index = Collections.binarySearch(allParameterNames, name);
    if(index >= 0) {
      allParameterNames.remove(index);
      fireTableRowsDeleted(index, index);
    }
  }

  /**
   * Called when a parameter is changed from optional to required or
   * vice-versa. Updates the table to match.
   */
  public void parameterChanged(ServiceDefinitionEvent e) {
    String name = e.getGasParameterName();
    int index = Collections.binarySearch(allParameterNames, name);
    if(index >= 0) {
      fireTableRowsUpdated(index, index);
    }
  }

  // remaining ServiceDefinitionListener methods not used

  public void annotationSetAdded(ServiceDefinitionEvent arg0) {
  }

  public void annotationSetRemoved(ServiceDefinitionEvent arg0) {
  }

  public void featureMappingAdded(ServiceDefinitionEvent arg0) {
  }

  public void featureMappingRemoved(ServiceDefinitionEvent arg0) {
  }

  public void parameterMappingAdded(ServiceDefinitionEvent arg0) {
  }

  public void parameterMappingRemoved(ServiceDefinitionEvent arg0) {
  }
}
