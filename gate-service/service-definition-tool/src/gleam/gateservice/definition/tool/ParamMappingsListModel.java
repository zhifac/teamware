/*
 * This file is part of SAFE, licenced under the GNU Lesser General Public
 * Licence.
 *
 * (c) 2007 The University of Sheffield
 *
 * $Id$
 */
package gleam.gateservice.definition.tool;

import java.util.List;

import javax.swing.ListSelectionModel;

import gleam.gateservice.definition.GateServiceDefinition;
import gleam.gateservice.definition.ServiceDefinitionEvent;
import gleam.gateservice.definition.GateServiceDefinition.ParameterMapping;

/**
 * List model for the list of parameter mappings.
 */
public class ParamMappingsListModel extends MappingsListModel<ParameterMapping> {

  public ParamMappingsListModel(GateServiceDefinition def, MainForm form,
          ListSelectionModel selectionModel) {
    super(def, form, selectionModel);
  }

  protected List<ParameterMapping> getMappings(String gasParam) {
    return definition.getParameterMappings(gasParam);
  }

  /**
   * Update the list when a parameter mapping is added to the
   * definition.
   */
  @Override
  public void parameterMappingAdded(ServiceDefinitionEvent e) {
    fireContentsChanged(this, 0, Integer.MAX_VALUE);
  }

  /**
   * Update the list when a parameter mapping is removed from the
   * definition.
   */
  @Override
  public void parameterMappingRemoved(ServiceDefinitionEvent e) {
    fireContentsChanged(this, 0, Integer.MAX_VALUE);
    selectionModel.clearSelection();
  }

}
