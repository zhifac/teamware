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
public class ActionConnectToExecutive extends AbstractAction {

	/** Internal reference to the instance of this action. */
	private static ActionConnectToExecutive ourInstance;

	/**
	 * Returns reference to the instance of this action.
	 *
	 * @return instance of this action
	 */
	public synchronized static ActionConnectToExecutive getInstance() {
		if (ourInstance == null) {
			System.out.println("!!!!!!!!!!!!!!ActionConnectToExecutive");

			ourInstance = new ActionConnectToExecutive();
		}
		return ourInstance;
	}

	/**
	 * Creates this action and defines action icon.
	 */
	private ActionConnectToExecutive() {
		super("Connect", AnnotatorGUI.createIcon("connect-32.gif"));
	}

	public synchronized void actionPerformed(ActionEvent e) {
		if (AnnotatorGUI.checkDocOnClose()) {

      if(ConnectToExecutiveDialog.getExistingInstance() != null) return;
			ConnectToExecutiveDialog cd = ConnectToExecutiveDialog.getNewInstance(MainFrame.getInstance(), true, false);
			cd.setVisible(false);
		}


		}


}
