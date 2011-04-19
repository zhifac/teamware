/*
 *  AnnotationDiffGUIOptionsDialog.java
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
package gleam.annotationdiffgui.gui;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingUtilities;

import gate.gui.OptionsDialog;

/**
 * A cut-down version of the GATE options dialog that only contains the
 * Appearance tab.
 */
public class AnnotationDiffGUIOptionsDialog extends OptionsDialog {

  protected boolean closedByOK = false;

  public AnnotationDiffGUIOptionsDialog(Frame owner) {
    super(owner);
  }

  @Override
  protected void initGuiComponents() {
    super.initGuiComponents();

    // remove all the tabs except Appearance.
    for(int i = mainTabbedPane.getTabCount() - 1; i >= 0; i--) {
      if(!mainTabbedPane.getTitleAt(i).contains("Appearance")) {
        mainTabbedPane.remove(i);
      }
    }
  }

  @Override
  protected void initListeners() {
    super.initListeners();

    // add a listener so we know whether the user closed the dialog with
    // OK rather than cancel.
    okButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        closedByOK = true;
      }
    });
  }
  
  /**
   * Was this dialog closed by the user pressing OK?
   */
  public boolean wasClosedByOK() {
    return closedByOK;
  }

}
