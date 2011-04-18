/*
 *  ActionConnectToExecutive.java
 *
 *  Copyright (c) 2006-2006, The University of Sheffield.
 *
 *  Andrey Shafirin, 09/Jun/2006
 */

package gate.teamware.richui.annotatorgui.actions;

import gate.teamware.richui.annotatorgui.AnnotatorGUI;
import gate.teamware.richui.annotatorgui.gui.ConnectToExecutiveDialog;
import gate.teamware.richui.annotatorgui.gui.MainFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * The purpose of this class is: <br> - formalize operation of the client
 * application <br> - provide easy access to this operation (it's Singleton)
 * <br> - symplify control for enabling/disabling of GUI elements based on this
 * action <br>
 *
 * @author Andrey Shafirin
 */
public class ActionCheckNewTasks extends AbstractAction {

  /** Internal reference to the instance of this action. */
  private static ActionCheckNewTasks ourInstance;

  /**
   * Returns reference to the instance of this action.
   *
   * @return instance of this action
   */
  public synchronized static ActionCheckNewTasks getInstance() {
    if (ourInstance == null) {
      ourInstance = new ActionCheckNewTasks();
    }
    return ourInstance;
  }

  /**
   * Creates this action and defines action icon.
   */
  private ActionCheckNewTasks() {
    super("Get tasks", AnnotatorGUI.createIcon("newtask-32.gif"));
  }

  public synchronized void actionPerformed(ActionEvent e) {
	  System.out.println("!!!!!!!!!!!!!!Clicked");
	  this.setEnabled(false);
	  if (AnnotatorGUI.checkDocOnClose()) {
      if(ConnectToExecutiveDialog.getExistingInstance() != null) {
    	  //System.out.println("ActionCheckNewTasks detected existing ConnectToExecutiveDialog");
    	  return;
      }
      ConnectToExecutiveDialog cd = ConnectToExecutiveDialog.getNewInstance(MainFrame.getInstance(), true, true);
    }
  }
}
