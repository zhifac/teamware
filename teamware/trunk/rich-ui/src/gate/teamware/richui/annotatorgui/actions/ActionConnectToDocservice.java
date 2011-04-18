/*
 *  ActionConnectToDocservice.java
 *
 *  Copyright (c) 2006-2006, The University of Sheffield.
 *
 *  Andrey Shafirin, 03/Jun/2006
 */

package gate.teamware.richui.annotatorgui.actions;

import gate.teamware.richui.annotatorgui.AnnotatorGUI;
import gate.teamware.richui.annotatorgui.gui.ConnectToDocserviceDialog;
import gate.teamware.richui.annotatorgui.gui.MainFrame;
import gate.teamware.richui.common.RichUIException;

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
public class ActionConnectToDocservice extends AbstractAction {

	/** Internal reference to the instance of this action. */
	private static ActionConnectToDocservice ourInstance;

	/**
	 * Returns reference to the instance of this action.
	 * 
	 * @return instance of this action
	 */
	public synchronized static ActionConnectToDocservice getInstance() {
		if (ourInstance == null) {
			ourInstance = new ActionConnectToDocservice();
		}
		return ourInstance;
	}

	/**
	 * Creates this action and defines action icon.
	 */
	private ActionConnectToDocservice() {
		super("Connect", AnnotatorGUI.createIcon("docservice-32.gif"));
	}

	public synchronized void actionPerformed(ActionEvent e) {
		ConnectToDocserviceDialog cd = new ConnectToDocserviceDialog(MainFrame.getInstance(), true);
		cd.setVisible(true);
	}
}
