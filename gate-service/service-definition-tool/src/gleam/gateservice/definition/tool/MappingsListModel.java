/*
 *  MappingsListModel.java
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
package gleam.gateservice.definition.tool;

import gleam.gateservice.definition.GateServiceDefinition;
import gleam.gateservice.definition.ServiceDefinitionEvent;
import gleam.gateservice.definition.ServiceDefinitionListener;

import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Abstract class holding the common behaviour between the parameter
 * mappings and feature mappings lists. This model registers itself as a
 * selection listener on the main GaS parameters table, so it knows when
 * a new parameter has been selected and can update itself accordingly.
 * 
 * @param <T> the type of the elements in this list.
 */
public abstract class MappingsListModel<T> extends AbstractListModel
                                                                    implements
                                                                    ServiceDefinitionListener,
                                                                    ListSelectionListener {

  protected GateServiceDefinition definition;

  /**
   * The list of mappings for the currently selected parameter.
   */
  protected List<T> mappings;

  /**
   * The currently selected GaS parameter name.
   */
  protected String gasParam;

  protected MainForm form;

  /**
   * The selection model for the list this model is backing.
   */
  protected ListSelectionModel selectionModel;

  public MappingsListModel(GateServiceDefinition def, MainForm form,
          ListSelectionModel selectionModel) {
    this.definition = def;
    this.form = form;
    this.selectionModel = selectionModel;
    form.gasParametersTable.getSelectionModel().addListSelectionListener(this);
    def.addServiceDefinitionListener(this);
  }

  /**
   * Set this list to display the mappings for a different GaS
   * parameter.
   * 
   * @param gasParam the parameter name whose mappings we are to
   *          display, or null to display nothing.
   */
  public void setGasParamName(String gasParam) {
    if((this.gasParam == null && gasParam == null)
            || (this.gasParam != null && this.gasParam.equals(gasParam))) {
      // no change to the value, so do nothing
      return;
    }
    this.gasParam = gasParam;
    if(gasParam == null) {
      this.mappings = null;
    }
    else {
      this.mappings = getMappings(gasParam);
    }

    fireContentsChanged(this, 0, Integer.MAX_VALUE);
    // clear the list selection as the list has now completely changed.
    selectionModel.clearSelection();
  }

  /**
   * Abstract method implemented by subclasses to provide the list of
   * mappings for the given GaS parameter.
   */
  protected abstract List<T> getMappings(String gasParam);

  /**
   * List model method - delegates to the underlying list.
   */
  public Object getElementAt(int index) {
    return mappings.get(index);
  }

  /**
   * List model method - delegates to the underlying list.
   */
  public int getSize() {
    if(mappings == null) {
      return 0;
    }
    else {
      return mappings.size();
    }
  }

  /**
   * Called when the selection changes in the GaS parameters table,
   * updates this list to match.
   */
  public void valueChanged(ListSelectionEvent e) {
    int selectedRow = form.gasParametersTable.getSelectedRow();
    if(selectedRow == -1) {
      setGasParamName(null);
    }
    else {
      setGasParamName((String)form.gasParametersTable
              .getValueAt(selectedRow, 1));
    }
  }

  // remaining ServiceDefinitionListener methods are unused here but may
  // be implemented by subclasses

  public void annotationSetAdded(ServiceDefinitionEvent arg0) {
  }

  public void annotationSetRemoved(ServiceDefinitionEvent arg0) {
  }

  public void featureMappingAdded(ServiceDefinitionEvent arg0) {
  }

  public void featureMappingRemoved(ServiceDefinitionEvent arg0) {
  }

  public void parameterMappingRemoved(ServiceDefinitionEvent e) {
  }

  public void parameterMappingAdded(ServiceDefinitionEvent e) {
  }

  public void parameterAdded(ServiceDefinitionEvent arg0) {
  }

  public void parameterChanged(ServiceDefinitionEvent arg0) {
  }

  public void parameterRemoved(ServiceDefinitionEvent arg0) {
  }

}
