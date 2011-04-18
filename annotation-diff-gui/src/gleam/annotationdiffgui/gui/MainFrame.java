/*
 *  MainFrame. *
 *  Copyright (c) 2006-2006, The University of Sheffield.
 *
 *  Andrey Shafirin, 09/Jun/2006
 */
package gleam.annotationdiffgui.gui;

import gate.Document;
import gate.Factory;
import gate.creole.ResourceInstantiationException;
import gate.creole.ontology.Ontology;
import gate.gui.LuceneDataStoreSearchGUI;
import gleam.annotationdiffgui.AnnotationDiffGUI;
import gleam.annotationdiffgui.AnnotationDiffGUIException;
import gleam.annotationdiffgui.AnnotationDiffGUIListener;
import gleam.annotationdiffgui.Connection;
import gleam.annotationdiffgui.Constants;
import gleam.annotationdiffgui.DocserviceConnection;
import gleam.annotationdiffgui.actions.ActionEditSettings;
import gleam.annotationdiffgui.actions.ActionShowAnnDiffConnectDialog;
import gleam.annotationdiffgui.actions.ActionShowHelp;
import gleam.annotationdiffgui.actions.ActionShowLog;
import gleam.annotationdiffgui.gui.LogFrame;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Main window of the Annotator GUI. <br>
 * It's a singleton, use {@link #getInstance()} to get it. <br>
 *
 * @author Andrey Shafirin
 */
public class MainFrame extends JFrame implements Constants {
	/** Internal reference to the instance of main window. */
	private static MainFrame ourInstance;

	private JToolBar toolBar;

	/**
	 * Instance of the Annotation Diff GUI
	 */
	private gate.gui.AnnotationDiffGUI annotationDiffFrame;
	
	/**
	 * Container for the document editor and ontology editor
	 */
	private JTabbedPane tabbedPane;

	/**
	 * Status label
	 */
	private JLabel statusLabel;

	/**
	 * Returns reference to the instance of this window.
	 *
	 * @return instance of this window
	 */
	public synchronized static MainFrame getInstance() {
		if (ourInstance == null) {
			ourInstance = new MainFrame();
		}
		return ourInstance;
	}

	/** Constructs this window. */
	private MainFrame() {
		super(APP_TITLE);
		LogFrame.getInstance();
		JFrame.setDefaultLookAndFeelDecorated(true);
		ImageIcon icon = AnnotationDiffGUI.createIcon("gleam-32.gif");
		if (icon != null)
			this.setIconImage(icon.getImage());
		setTitleStatus(AnnotationDiffGUI.getConnectionStatus());
//		this.setSize(800, 600);
//		this.setPreferredSize(new Dimension(800, 600));
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation((d.width - this.getSize().width) / 2, (d.height - this
				.getSize().height) / 2);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent arg0) {
				try {
					if (AnnotationDiffGUI.closeConnection())
						dispose();
				} catch (AnnotationDiffGUIException ex) {
					ex.printStackTrace();
					JOptionPane.showMessageDialog(MainFrame.getInstance(), ex
							.getMessage()
							+ ((ex.getCause() == null) ? "" : "\n\n"
									+ ex.getCause().getMessage()), "Error!",
							JOptionPane.ERROR_MESSAGE);
					dispose();
				}
			}

			public void windowClosed(WindowEvent arg0) {
				System.exit(0);
			}
		});
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(getToolBar(), BorderLayout.NORTH);
		getStatusLabel().setBorder(
				BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
		this.getContentPane().add(getStatusLabel(), BorderLayout.SOUTH);
		updateAllActions();
		AnnotationDiffGUI.addAnnotationDiffGUIListener(new MainFrameConnectionListener());

		// empty tabbed pane
    tabbedPane = new JTabbedPane();
//    tabbedPane.setSize(1000, 600);
//    tabbedPane.setPreferredSize(new Dimension(1000, 600));
		this.getContentPane().add(tabbedPane, BorderLayout.CENTER);
	}

	/**
	 * Set application status displayed in main window title. Staus are
	 * displayed in form APP_TITLE [ status ]
	 *
	 * @param status
	 *            status you wont to add to application title.
	 */
	public void setTitleStatus(String status) {
		this.setTitle(APP_TITLE + " [" + status + "]");
	}

	public void updateTitleStatus() {
		this.setTitle(APP_TITLE + " [" + AnnotationDiffGUI.getConnectionStatus()
				+ "]");
	}

	public void updateBottomStatus() {
		if (this.statusLabel == null)
			this.statusLabel = new JLabel();
		this.statusLabel.setText(getStatusLabelText());
		this.statusLabel.setToolTipText(getStatusLabelToolTipText());
	}

	public void setBottomStatus(String status) {
		if (this.statusLabel == null)
			this.statusLabel = new JLabel();
		this.statusLabel.setText(status);
		this.statusLabel.setToolTipText(status);
	}

	public void updateAllStatuses() {
		this.setTitle(APP_TITLE + " [" + AnnotationDiffGUI.getConnectionStatus()
				+ "]");
		updateBottomStatus();
	}

	public void updateAllActions() {
		//ActionShowExecutiveConnectDialog.getInstance().setEnabled(true);
//		ActionShowDocserviceConnectDialog.getInstance().setEnabled(true);
		ActionShowHelp.getInstance().setEnabled(true);
		ActionShowLog.getInstance().setEnabled(true);
		ActionEditSettings.getInstance().setEnabled(true);
		if (AnnotationDiffGUI.getConnection() != null) {
//			ActionSaveDocument.getInstance().setEnabled(
//					AnnotationDiffGUI.getConnection().getDocument() != null);
//			if (AnnotationDiffGUI.getConnection() instanceof ExecutiveConnection) {
//				ExecutiveConnection c = (ExecutiveConnection) AnnotationDiffGUI
//						.getConnection();
//				ActionCheckNewTasks.getInstance().setEnabled(
//						c.getCurrentAnnotatorTask() == null);
//				ActionFinishTask.getInstance().setEnabled(
//						c.getCurrentAnnotatorTask() != null);
//				ActionCancelTask.getInstance().setEnabled(
//						c.getCurrentAnnotatorTask() != null);
//
//			} else {
//				ActionCheckNewTasks.getInstance().setEnabled(false);
//				ActionCancelTask.getInstance().setEnabled(false);
//				ActionFinishTask.getInstance().setEnabled(false);
				if (AnnotationDiffGUI.getConnection() instanceof DocserviceConnection) {
					AnnotationDiffGUI.getConnection();
				}
//			}
//		} else {
//			ActionSaveDocument.getInstance().setEnabled(false);
//			ActionFinishTask.getInstance().setEnabled(false);
		}
	}

	/**
	 * Returns tool bar for basic user operations. New one will be created if
	 * needed.
	 */
	public JToolBar getToolBar() {
		if (null != toolBar)
			return toolBar;
/*
		JButton buttonConnectToAP = new JButton(
				ActionConnectToExecutive.getInstance());
				//ActionShowExecutiveConnectDialog.getInstance());
		buttonConnectToAP.setText("");
		buttonConnectToAP.setToolTipText("Connect to Annotator Pool");
*/
		/*
		JButton buttonGetNewTask = new JButton(
				ActionGetNewTask.getInstance());
				//ActionShowExecutiveConnectDialog.getInstance());
		buttonGetNewTask.setText("");
		buttonGetNewTask.setToolTipText("Get New Task");
        */
//		JButton buttonCheckNewTask = new JButton(
//				ActionCheckNewTasks.getInstance());
//				//ActionShowExecutiveConnectDialog.getInstance());
//		buttonCheckNewTask.setText("");
//		buttonCheckNewTask.setToolTipText("Get New Task");
//
		JButton buttonConnectToDS = new JButton(
				ActionShowAnnDiffConnectDialog.getInstance());
		buttonConnectToDS.setText("");
		buttonConnectToDS.setToolTipText("Connect to Document Service");
//
//		JButton buttonSave = new JButton(ActionSaveDocument.getInstance());
//		buttonSave.setText("");
//		buttonSave.setToolTipText("Save Document");

//		JButton buttonFinish = new JButton(ActionFinishTask.getInstance());
//		buttonFinish.setText("");
//		buttonFinish.setToolTipText("Finish Task");

//		JButton buttonCancel = new JButton(ActionCancelTask.getInstance());
//		buttonCancel.setText("");
//		buttonCancel.setToolTipText("Cancel Task");

		JButton buttonHelp = new JButton(ActionShowHelp.getInstance());
		buttonHelp.setText("");
		buttonHelp.setToolTipText("Help");

		JButton buttonShowLog = new JButton(ActionShowLog.getInstance());
		buttonShowLog.setText("");
		buttonShowLog.setToolTipText("Show Application Log");

		JButton buttonEditSettings = new JButton(ActionEditSettings.getInstance());
		buttonEditSettings.setText("");
		buttonEditSettings.setToolTipText("Edit settings");

		updateAllActions();

		toolBar = new JToolBar(JToolBar.HORIZONTAL);
		toolBar.setFloatable(false);


//		if (AnnotationDiffGUI.getProperties().getProperty(MODE_PARAMETER_NAME)
//				.equals(DIRECT_MODE)) {
//			toolBar.add(buttonConnectToDS);
//			toolBar.add(buttonSave);

//		} else if (AnnotationDiffGUI.getProperties()
//				.getProperty(MODE_PARAMETER_NAME).equals(POOL_MODE)) {
			//toolBar.add(buttonConnectToAP);
//			toolBar.add(buttonCheckNewTask);
//			toolBar.add(buttonFinish);
//			toolBar.add(buttonCancel);
//		} else {
//			System.out.println("Internal error. Undefined mode.");
//		}


		toolBar.addSeparator();
		toolBar.add(buttonShowLog);
		toolBar.add(buttonHelp);
		toolBar.add(buttonEditSettings);
//		toolBar.add(buttonConnectToDS);

		return toolBar;
	}

	private JLabel getStatusLabel() {
		if (this.statusLabel != null)
			return this.statusLabel;
		this.statusLabel = new JLabel();
		this.statusLabel.setText(getStatusLabelText());
		this.statusLabel.setToolTipText(getStatusLabelToolTipText());
		return statusLabel;
	}

	private String getStatusLabelText() {
		Connection c = AnnotationDiffGUI.getConnection();
		if (c == null) {
			return "Not connected.";
//		} else if (c instanceof ExecutiveConnection) {
//			return ((ExecutiveConnection) c).getTaskStatus();
		} else if (c instanceof DocserviceConnection) {
			return "Document: "
					+ ((((DocserviceConnection) c).getDocument().isModified()) ? "* "
							: "") + ((DocserviceConnection) c).getDocId();
		} else {
			return "Internal error: unknown type of connection.";
		}
	}

	private String getStatusLabelToolTipText() {
		Connection c = AnnotationDiffGUI.getConnection();
		if (c == null) {
			return "Not connected.";
//		} else if (c instanceof ExecutiveConnection) {
//			return ((ExecutiveConnection) c).getTaskDetails();
		} else if (c instanceof DocserviceConnection) {
			return "Document "
					+ ((((DocserviceConnection) c).getDocument().isModified()) ? "* "
							: "") + ((DocserviceConnection) c).getDocId();
		} else {
			return "";
		}
	}

	class MainFrameConnectionListener implements AnnotationDiffGUIListener {
		public void connectionChanged(Connection newConnection) {
			MainFrame.getInstance().updateAllActions();
			MainFrame.getInstance().updateAllStatuses();
			if (newConnection instanceof DocserviceConnection) {
				if (annotationDiffFrame != null)
					tabbedPane.removeAll();

				annotationDiffFrame =
					new gate.gui.AnnotationDiffGUI("Annotation Diff Tool");
				annotationDiffFrame.pack();
				try {
//					frame.setIconImage(((ImageIcon)getIcon("annotation-diff")).getImage());
		      	} catch(Exception ex) {
		        // ignore exceptions here - this is only for aesthetic reasons
		      	}
//		      	annotationDiffFrame.setLocationRelativeTo(MainFrame.this);
		      	tabbedPane.add("Annotation Diff GUI", annotationDiffFrame.getContentPane());
		      	MainFrame.getInstance().pack();
//		      	annotationDiffFrame.setVisible(true);
			}
		}
	}
}
