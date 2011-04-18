/*
 *  ActionConnectToAnnicGUI.java
 *
 *  Copyright (c) 2006-2006, The University of Sheffield.
 *
 *  Andrey Shafirin, 03/Jun/2006
 */

package gleam.annic.actions;

import gleam.annic.AnnicGUI;
import gleam.annic.gui.ConnectToANNICDialog;
import gleam.annic.gui.MainFrame;

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
public class ActionConnectToAnnicGUI extends AbstractAction {

	/** Internal reference to the instance of this action. */
	private static ActionConnectToAnnicGUI ourInstance;

	/**
	 * Returns reference to the instance of this action.
	 * 
	 * @return instance of this action
	 */
	public synchronized static ActionConnectToAnnicGUI getInstance() {
		if (ourInstance == null) {
			ourInstance = new ActionConnectToAnnicGUI();
		}
		return ourInstance;
	}

	/**
	 * Creates this action and defines action icon.
	 */
	private ActionConnectToAnnicGUI() {
		super("Connect", AnnicGUI.createIcon("docservice-32.gif"));
	}

	public synchronized void actionPerformed(ActionEvent e) {
		// even if it automatic connection, we still show the dialog to give user
		// a glimpse of what is happenening there
		ConnectToANNICDialog cd = new ConnectToANNICDialog(MainFrame.getInstance(), true);
		cd.setVisible(true);
	}
}
