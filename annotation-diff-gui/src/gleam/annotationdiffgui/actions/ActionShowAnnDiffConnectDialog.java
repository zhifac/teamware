/*
 *  ActionShowAnnDiffConnectDialog.java
 *
 *  Copyright (c) 2006-2006, The University of Sheffield.
 *
 *  Andrey Shafirin, 09/Jun/2006
 */

package gleam.annotationdiffgui.actions;

import gleam.annotationdiffgui.AnnotationDiffGUI;
import gleam.annotationdiffgui.gui.ConnectToAnnDiffDialog;
import gleam.annotationdiffgui.gui.MainFrame;

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
public class ActionShowAnnDiffConnectDialog extends AbstractAction {

	/** Internal reference to the instance of this action. */
	private static ActionShowAnnDiffConnectDialog ourInstance;

	/**
	 * Returns reference to the instance of this action.
	 * 
	 * @return instance of this action
	 */
	public synchronized static ActionShowAnnDiffConnectDialog getInstance() {
		if (ourInstance == null) {
			ourInstance = new ActionShowAnnDiffConnectDialog();
		}
		return ourInstance;
	}

	/**
	 * Creates this action and defines action icon.
	 */
	private ActionShowAnnDiffConnectDialog() {
		super("Connect to Document Service",
				AnnotationDiffGUI.createIcon("docservice-32.gif"));
	}

	/**
	 * Invoke connect dialog to allow for user to define details of connection.
	 * 
	 * @param e
	 *          not used
	 */
	public synchronized void actionPerformed(ActionEvent e) {
		ConnectToAnnDiffDialog cd = new ConnectToAnnDiffDialog(MainFrame.getInstance(), false);
		cd.setVisible(true);
	}
}
