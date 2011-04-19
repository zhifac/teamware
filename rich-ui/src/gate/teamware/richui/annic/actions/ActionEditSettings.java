/*
 *  ActionEditSettings.java
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

package gate.teamware.richui.annic.actions;

import gate.Gate;
import gate.teamware.richui.annic.AnnicGUI;
import gate.teamware.richui.annic.Constants;
import gate.teamware.richui.annic.gui.AnnicGUIOptionsDialog;
import gate.teamware.richui.annic.gui.MainFrame;
import gate.util.GateException;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * The purpose of this class is: <br> - formalize operation of the
 * client application <br> - provide easy access to this operation (it's
 * Singleton) <br> - symplify control for enabling/disabling of GUI
 * elements based on this action <br>
 * 
 * @author Ian Roberts
 */
public class ActionEditSettings extends AbstractAction implements Constants {

  /** Internal reference to the instance of this action. */
  private static ActionEditSettings ourInstance;

  /**
   * Returns reference to the instance of this action.
   * 
   * @return instance of this action
   */
  public synchronized static ActionEditSettings getInstance() {
    if(ourInstance == null) {
      ourInstance = new ActionEditSettings();
    }
    return ourInstance;
  }

  /**
   * Creates this action and defines action icon.
   */
  private ActionEditSettings() {
    super("Edit Settings", AnnicGUI.createIcon("settings.png"));
    this.setEnabled(true);
  }

  public synchronized void actionPerformed(ActionEvent e) {
    AnnicGUIOptionsDialog dialog =
            new AnnicGUIOptionsDialog(MainFrame.getInstance());
    dialog.showDialog();
    if(dialog.wasClosedByOK()) {
      try {
        Gate.writeUserConfig();
      }
      catch(GateException ge) {
        JOptionPane.showMessageDialog(MainFrame.getInstance(),
                "Could not save settings, "
                        + "they will not persist for your next session.",
                "Could not save", JOptionPane.WARNING_MESSAGE);
      }
      // make sure the GUI is updated
      SwingUtilities.updateComponentTreeUI(MainFrame.getInstance());
    }
  }
}
