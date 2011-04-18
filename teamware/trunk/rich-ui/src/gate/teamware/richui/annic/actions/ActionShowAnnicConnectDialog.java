/*
 *  ActionShowAnnicConnectDialog.java
 *
 *  Copyright (c) 2006-2006, The University of Sheffield.
 *
 *  Andrey Shafirin, 09/Jun/2006
 */

package gate.teamware.richui.annic.actions;

import gate.teamware.richui.annic.AnnicGUI;
import gate.teamware.richui.annic.gui.ConnectToANNICDialog;
import gate.teamware.richui.annic.gui.MainFrame;

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
public class ActionShowAnnicConnectDialog extends AbstractAction {

	/** Internal reference to the instance of this action. */
	private static ActionShowAnnicConnectDialog ourInstance;

	/**
	 * Returns reference to the instance of this action.
	 * 
	 * @return instance of this action
	 */
	public synchronized static ActionShowAnnicConnectDialog getInstance() {
		if (ourInstance == null) {
			ourInstance = new ActionShowAnnicConnectDialog();
		}
		return ourInstance;
	}

	/**
	 * Creates this action and defines action icon.
	 */
	private ActionShowAnnicConnectDialog() {
		super("Connect to Document Service", AnnicGUI.createIcon("docservice-32.gif"));
	}

	/**
	 * Invoke connect dialog to allow for user to define details of connection.
	 * 
	 * @param e
	 *          not used
	 */
	public synchronized void actionPerformed(ActionEvent e) {
		ConnectToANNICDialog cd = new ConnectToANNICDialog(MainFrame.getInstance(), false);
		cd.setVisible(true);
	}
}
