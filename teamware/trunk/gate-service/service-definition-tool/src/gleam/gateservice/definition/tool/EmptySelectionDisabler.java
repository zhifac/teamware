/*
 *  EmptySelectionDisabler.java
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

import java.awt.Component;

import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Simple listener that updates the enabled state of a component so that
 * it is enabled if and only if something is selected in the associated
 * selection model.
 */
public class EmptySelectionDisabler implements ListSelectionListener {
  private Component component;

  private ListSelectionModel model;

  public EmptySelectionDisabler(Component component, ListSelectionModel model) {
    this.component = component;
    this.model = model;
  }

  public void valueChanged(ListSelectionEvent e) {
    component.setEnabled(!model.isSelectionEmpty());
  }

}
