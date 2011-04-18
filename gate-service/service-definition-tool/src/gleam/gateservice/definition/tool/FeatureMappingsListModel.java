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

/**
 * List model for the list of feature mappings.
 */
public class FeatureMappingsListModel extends MappingsListModel<String> {

  public FeatureMappingsListModel(GateServiceDefinition def, MainForm form,
          ListSelectionModel selectionModel) {
    super(def, form, selectionModel);
  }

  protected List<String> getMappings(String gasParam) {
    return definition.getFeatureMappings(gasParam);
  }

  /**
   * Update the list when a feature mapping is added to the definition.
   */
  @Override
  public void featureMappingAdded(ServiceDefinitionEvent e) {
    fireContentsChanged(this, 0, Integer.MAX_VALUE);
  }

  /**
   * Update the list when a feature mapping is removed from the
   * definition.
   */
  @Override
  public void featureMappingRemoved(ServiceDefinitionEvent e) {
    fireContentsChanged(this, 0, Integer.MAX_VALUE);
    selectionModel.clearSelection();
  }

}
