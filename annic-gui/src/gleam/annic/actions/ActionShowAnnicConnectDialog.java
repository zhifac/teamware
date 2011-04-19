/*
 *  ActionShowAnnicConnectDialog.java
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

package gleam.annic.actions;

import gleam.annic.AnnicGUI;
import gleam.annic.gui.ConnectToANNICDialog;
import gleam.annic.gui.MainFrame;

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
