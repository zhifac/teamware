package gleam.annotationdiffgui.gui;

import java.awt.Frame;

import gleam.annotationdiffgui.AnnotationDiffGUI;
import gleam.annotationdiffgui.AnnotationDiffGUIException;
import gleam.annotationdiffgui.Connection;
import gleam.annotationdiffgui.Constants;

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

	protected void handleError(AnnotationDiffGUIException e) {
		if (Thread.currentThread() == connectionThread) {
			e.printStackTrace();
			statusMessage.setText(AnnotationDiffGUI.getConnectionStatus());
			// MainFrame.getInstance().setStatus(AnnotationDiffGUI.getConnectionStatus());
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
	protected void setConnectionSafely(final Connection connection) {
		// Called by lookupThread, but can be safely called by any thread.
		Runnable r = new Runnable() {
			public void run() {
				try {
					AnnotationDiffGUI.setConnection(connection);
					// MainFrame.getInstance().setStatus(AnnotationDiffGUI.getConnectionStatus());
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
			MainFrame.getInstance().setTitleStatus(AnnotationDiffGUI.getConnectionStatus());
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
