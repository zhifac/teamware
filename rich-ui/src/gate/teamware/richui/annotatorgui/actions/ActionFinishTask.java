/*
 *  ActionFinishTask.java
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

package gate.teamware.richui.annotatorgui.actions;

import gate.teamware.richui.annotatorgui.*;
import gate.teamware.richui.annotatorgui.gui.MainFrame;
import gate.teamware.richui.annotatorgui.gui.MessageDialog;
import gate.teamware.richui.common.RichUIException;

import javax.swing.*;

import java.awt.event.ActionEvent;
import java.util.Date;

/**
 * The purpose of this class is: <br> - formalize operation of the client
 * application <br> - provide easy access to this operation (it's Singleton)
 * <br> - symplify control for enabling/disabling of GUI elements based on this
 * action <br>
 * 
 * @author Andrey Shafirin
 */
public class ActionFinishTask extends AbstractAction implements Constants {

	/** Internal reference to the instance of this action. */
	private static ActionFinishTask ourInstance;

	/**
	 * Returns reference to the instance of this action.
	 * 
	 * @return instance of this action
	 */
	public synchronized static ActionFinishTask getInstance() {
		if (ourInstance == null) {
			ourInstance = new ActionFinishTask();
		}
		return ourInstance;
	}

	/**
	 * Creates this action and defines action icon.
	 */
	private ActionFinishTask() {
		super("Finish Task", AnnotatorGUI.createIcon("finishtask-32.gif"));
	}

	public synchronized void actionPerformed(ActionEvent e) {
		try {
			if (AnnotatorGUI.getConnection() == null)
				return;
			if (AnnotatorGUI.getConnection() instanceof ExecutiveConnection) {
				((ExecutiveConnection) AnnotatorGUI.getConnection())
						.finishTask();
				((ExecutiveConnection) AnnotatorGUI.getConnection())
						.setAnnotatorTask(null);
				System.out.println("["
						+ new Date(System.currentTimeMillis()).toString()
						+ "] Task finished. "
						+ ((ExecutiveConnection) AnnotatorGUI.getConnection())
								.getTaskStatus());
				MessageDialog m = new MessageDialog(MainFrame.getInstance(),
						"Task finished", "Task finished.", 3000);
				m.setVisible(true);
			} else {
				JOptionPane.showMessageDialog(MainFrame.getInstance(),
						"Internal error. This action can be only "
								+ "performed with current connection of type: "
								+ ExecutiveConnection.class.getName(),
						"Error!", JOptionPane.ERROR_MESSAGE);
			}
		} catch (RichUIException ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(MainFrame.getInstance(), ex
					.getMessage()
					+ ((ex.getCause() == null) ? "" : "\n\n"
							+ ex.getCause().getMessage()), "Error!",
					JOptionPane.ERROR_MESSAGE);
		}
	}
}
