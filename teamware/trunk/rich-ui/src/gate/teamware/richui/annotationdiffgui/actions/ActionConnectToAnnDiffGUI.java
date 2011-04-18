/*
 *  ActionConnectToAnnDiffGUI.java
 *
 *  Copyright (c) 2006-2006, The University of Sheffield.
 *
 *  Andrey Shafirin, 03/Jun/2006
 */

package gate.teamware.richui.annotationdiffgui.actions;

import gate.teamware.richui.annotationdiffgui.AnnotationDiffGUI;
import gate.teamware.richui.annotationdiffgui.gui.ConnectToAnnDiffDialog;
import gate.teamware.richui.annotationdiffgui.gui.MainFrame;

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
public class ActionConnectToAnnDiffGUI extends AbstractAction {

	/** Internal reference to the instance of this action. */
	private static ActionConnectToAnnDiffGUI ourInstance;

	/**
	 * Returns reference to the instance of this action.
	 * 
	 * @return instance of this action
	 */
	public synchronized static ActionConnectToAnnDiffGUI getInstance() {
		if (ourInstance == null) {
			ourInstance = new ActionConnectToAnnDiffGUI();
		}
		return ourInstance;
	}

	/**
	 * Creates this action and defines action icon.
	 */
	private ActionConnectToAnnDiffGUI() {
		super("Connect", AnnotationDiffGUI.createIcon("docservice-32.gif"));
	}

	public synchronized void actionPerformed(ActionEvent e) {
		ConnectToAnnDiffDialog cd = new ConnectToAnnDiffDialog(MainFrame.getInstance(), true);
		cd.setVisible(true);
	}
}
