/*
 *  ActionFinishTask.java
 *
 *  Copyright (c) 2006-2006, The University of Sheffield.
 *
 *  Andrey Shafirin, 09/Jun/2006
 */

package gleam.annotatorgui.actions;

import gleam.annotatorgui.AnnotatorGUI;
import gleam.annotatorgui.AnnotatorGUIExeption;
import gleam.annotatorgui.ExecutiveConnection;
import gleam.annotatorgui.Constants;
import gleam.annotatorgui.gui.MainFrame;
import gleam.annotatorgui.gui.MessageDialog;

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
public class ActionFinishTask extends AbstractAction implements Constants {

	private static final long serialVersionUID = 7867106446467467963L;
	
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
		} catch (AnnotatorGUIExeption ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(MainFrame.getInstance(), ex
					.getMessage()
					+ ((ex.getCause() == null) ? "" : "\n\n"
							+ ex.getCause().getMessage()), "Error!",
					JOptionPane.ERROR_MESSAGE);
		}
	}
}
