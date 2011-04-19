/*
 *  FeatureMappingsListModel.java
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
