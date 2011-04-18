/*
 *  ActionShowDocserviceConnectDialog.java
 *
 *  Copyright (c) 2006-2006, The University of Sheffield.
 *
 *  Andrey Shafirin, 09/Jun/2006
 */

package gleam.annotatorgui.actions;

import gleam.annotatorgui.AnnotatorGUI;
import gleam.annotatorgui.gui.ConnectToDocserviceDialog;
import gleam.annotatorgui.gui.MainFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Shows connect dialog to user. <br>
 * 
 * The purpose of this class is: <br> - formalize operation of the client
 * application <br> - provide easy access to this operation (it's Singleton)
 * <br> - simplify control for enabling/disabling of GUI elements based on this
 * action <br>
 * 
 * @author Andrey Shafirin
 */
public class ActionShowDocserviceConnectDialog extends AbstractAction {

	/** Internal reference to the instance of this action. */
	private static ActionShowDocserviceConnectDialog ourInstance;

	/**
	 * Returns reference to the instance of this action.
	 * 
	 * @return instance of this action
	 */
	public synchronized static ActionShowDocserviceConnectDialog getInstance() {
		if (ourInstance == null) {
			ourInstance = new ActionShowDocserviceConnectDialog();
		}
		return ourInstance;
	}

	/**
	 * Creates this action and defines action icon.
	 */
	private ActionShowDocserviceConnectDialog() {
		super("Connect to Document Service", AnnotatorGUI.createIcon("docservice-32.gif"));
	}

	/**
	 * Invoke connect dialog to allow for user to define details of connection.
	 * 
	 * @param e
	 *          not used
	 */
	public synchronized void actionPerformed(ActionEvent e) {
		if (AnnotatorGUI.checkDocOnClose()) {
			ConnectToDocserviceDialog cd = new ConnectToDocserviceDialog(MainFrame.getInstance(), false);
			cd.setVisible(true);
		}
	}
}
