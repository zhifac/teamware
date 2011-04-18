/*
 *  ActionShowExecutiveConnectDialog.java
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
 * Shows connect dialog to user. <br>
 * 
 * The purpose of this class is: <br> - formalize operation of the client
 * application <br> - provide easy access to this operation (it's Singleton)
 * <br> - symplify control for enabling/disabling of GUI elements based on this
 * action <br>
 * 
 * @author Andrey Shafirin
 */
public class ActionShowExecutiveConnectDialog extends AbstractAction {

	/** Internal reference to the instance of this action. */
	private static ActionShowExecutiveConnectDialog ourInstance;

	/**
	 * Returns reference to the instance of this action.
	 * 
	 * @return instance of this action
	 */
	public synchronized static ActionShowExecutiveConnectDialog getInstance() {
		if (ourInstance == null) {
			ourInstance = new ActionShowExecutiveConnectDialog();
		}
		return ourInstance;
	}

	/**
	 * Creates this action and defines action icon.
	 */
	private ActionShowExecutiveConnectDialog() {
		super("Connect to Annotator Pool", AnnotatorGUI.createIcon("connect-32.gif"));
	}

	/**
	 * Invoke connect dialog to allow for user to define details of connection.
	 * 
	 * @param e
	 *          not used
	 */
	public synchronized void actionPerformed(ActionEvent e) {
		if (AnnotatorGUI.checkDocOnClose()) {
      if(ConnectToExecutiveDialog.getExistingInstance() != null) return;
			ConnectToExecutiveDialog cd = ConnectToExecutiveDialog.getNewInstance(MainFrame.getInstance(), false, false);
			cd.setVisible(true);
		}
	}
}
