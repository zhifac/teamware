/*
 *  ParamMappingsListModel.java
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
