/*
 *  ParameterMappingCellRenderer.java
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

import gleam.gateservice.definition.GateServiceDefinition.ParameterMapping;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

/**
 * List cell renderer for a list of {@link ParameterMapping} objects.
 * Renders each row as "PRName: prParameter".
 * 
 * @author ian
 * 
 */
public class ParameterMappingCellRenderer extends DefaultListCellRenderer {

  @Override
  public Component getListCellRendererComponent(JList list, Object value,
          int index, boolean isSelected, boolean cellHasFocus) {
    ParameterMapping pm = (ParameterMapping)value;

    return super.getListCellRendererComponent(list, pm.getPrName() + ": "
            + pm.getParamName(), index, isSelected, cellHasFocus);
  }

}
