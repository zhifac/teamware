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

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

/**
 * List cell renderer that renders a <code>null</code> value as the
 * string "&lt;Default set&gt;". This is used to handle annotation set
 * names.
 */
public class AnnotationSetsListCellRenderer extends DefaultListCellRenderer {

  @Override
  public Component getListCellRendererComponent(JList list, Object value,
          int index, boolean isSelected, boolean cellHasFocus) {
    if(value == null) {
      value = "<Default set>";
    }
    return super.getListCellRendererComponent(list, value, index, isSelected,
            cellHasFocus);
  }

}
