/*
 *  AddAnnotationSetFormSupport.java
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
