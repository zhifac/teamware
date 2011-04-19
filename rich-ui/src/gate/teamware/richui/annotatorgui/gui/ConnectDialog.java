/*
 *  ConnectDialog.java
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
package gate.teamware.richui.annotatorgui.gui;

import java.awt.Frame;

import gate.teamware.richui.annotatorgui.*;
import gate.teamware.richui.common.RichUIException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public abstract class ConnectDialog extends JDialog implements Constants{

	protected JLabel statusMessage;

	protected JButton buttonConnect;

	protected JButton buttonCancel;


	protected static String errMessage = "Failed to connect to server";
	/**
	 * Refers to last started thread that connects to the server.
	 * <code>Null</code> if no connection performed.
	 */
	protected ConnectDialog(Frame owner, String title, boolean autoconnect){

		super(owner, "Connect to " + title, true);
	}

	protected volatile Thread connectionThread;

	protected void handleError(RichUIException e) {
		if (Thread.currentThread() == connectionThread) {
			e.printStackTrace();
			statusMessage.setText(AnnotatorGUI.getConnectionStatus());
			// MainFrame.getInstance().setStatus(AnnotatorGUI.getConnectionStatus());
			MainFrame.getInstance().updateAllStatuses();
			JOptionPane.showMessageDialog(this, e.getMessage(), "Error!",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Controls that we are in event thread. Throws an exception if called not
	 * from event thread.
	 */
	protected void ensureEventThread() {
		// throws an exception if not invoked by the event thread.
		if (SwingUtilities.isEventDispatchThread()) {
			return;
		}
		throw new RuntimeException(
				"only the event thread should invoke this method");
	}

	/**
	 * Safetly sets given connection for application.
	 */
	protected void setConnectionSafely(final Connection connection,
			final AnnotatorTask task) {
		// Called by lookupThread, but can be safely called by any thread.
		Runnable r = new Runnable() {
			public void run() {
				try {
					AnnotatorGUI.setConnection(connection);
					if (task != null)
						connection.setAnnotatorTask(task);
					// MainFrame.getInstance().setStatus(AnnotatorGUI.getConnectionStatus());
					MainFrame.getInstance().updateAllStatuses();
				} catch (Exception x) {
					x.printStackTrace();
				}
			}
		};
		SwingUtilities.invokeLater(r);
	}


	/**
	 * Cancels connection process or closes this dialog if no connection in
	 * progress.
	 */
	protected void cancel() {
		ensureEventThread();
		if (null != connectionThread) {
			// if we are connecting - cancel connect
			connectionThread.interrupt();
			connectionThread = null;
			statusMessage.setText("Connection to "+ getTitle() +" was calcelled");
			MainFrame.getInstance().setTitleStatus(AnnotatorGUI.getConnectionStatus());
			buttonConnect.setEnabled(true);
		} else {
			// if we are NOT connecting - close this dialog
			this.dispose();
		}
	}

	protected abstract void initGUI();

	protected abstract void hookupEvents();

	protected abstract void connect();



}
