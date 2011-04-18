/*
 * This file is part of SAFE, licenced under the GNU Lesser General Public
 * Licence.
 *
 * (c) 2007 The University of Sheffield
 *
 * $Id$
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
