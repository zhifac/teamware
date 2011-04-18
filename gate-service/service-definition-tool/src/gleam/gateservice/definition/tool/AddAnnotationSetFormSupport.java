/*
 * This file is part of SAFE, licenced under the GNU Lesser General Public
 * Licence.
 *
 * (c) 2007 The University of Sheffield
 *
 * $Id$
 */
package gleam.gateservice.definition.tool;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Class to hold static method to set up the fixed listeners for an
 * AddAnnotationSetForm.
 */
public class AddAnnotationSetFormSupport {

  public static void setup(final AddAnnotationSetForm form) {
    // only enable the annotation set name combo box when the "named
    // set" radio button is selected.
    form.namedSetButton.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        form.asNameCombo.setEnabled(form.namedSetButton.isSelected());
      }
    });
  }

}
