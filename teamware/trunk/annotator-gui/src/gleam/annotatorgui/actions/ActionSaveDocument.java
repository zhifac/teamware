/*
 *  ActionSaveDocument.java
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

package gleam.annotatorgui.actions;

import gleam.annotatorgui.AnnotatorGUI;
import gleam.annotatorgui.AnnotatorGUIExeption;
import gleam.annotatorgui.gui.MainFrame;

import javax.swing.*;

import java.awt.event.ActionEvent;
import java.util.Date;

/**
 * The purpose of this class is: <br> - formalize operation of the client
 * application <br> - provide easy access to this operation (it's Singleton)
 * <br> - simplify control for enabling/disabling of GUI elements based on this
 * action <br>
 * 
 * @author Andrey Shafirin
 */
public class ActionSaveDocument extends AbstractAction {
	
	private static final long serialVersionUID = 4912298135000319902L;

	/** Internal reference to the instance of this action. */
	private static ActionSaveDocument ourInstance;

	/**
	 * Returns reference to the instance of this action.
	 * 
	 * @return instance of this action
	 */
	public synchronized static ActionSaveDocument getInstance() {
		if (ourInstance == null) {
			ourInstance = new ActionSaveDocument();
		}
		return ourInstance;
	}

	/**
	 * Creates this action and defines action icon.
	 */
	private ActionSaveDocument() {
		super("Connect", AnnotatorGUI.createIcon("save-32.gif"));
	}

	public synchronized void actionPerformed(ActionEvent e) {
		System.out.println("Saving the document...");
		try {
			if (AnnotatorGUI.getConnection() == null) return;
			AnnotatorGUI.getConnection().saveDocument();
			System.out.println("[" + new Date(System.currentTimeMillis()).toString() + "] Document: "
					+ AnnotatorGUI.getConnection().getDocument().getLRPersistenceId() + " has been saved.");
		} catch (AnnotatorGUIExeption ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(MainFrame.getInstance(), ex.getMessage()
					+ ((ex.getCause() == null) ? "" : "\n\n" + ex.getCause().getMessage()), "Error!", JOptionPane.ERROR_MESSAGE);
		}
	}
}
