/*
 *  ActionCancelTask.java
 *
 *  Copyright (c) 2006-2006, The University of Sheffield.
 *
 *  Milan Agatonovic, 06/July/2006
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
