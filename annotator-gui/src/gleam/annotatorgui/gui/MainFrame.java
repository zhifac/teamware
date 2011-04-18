/*
 *  MainFrame. *
 *  Copyright (c) 2006-2006, The University of Sheffield.
 *
 *  Andrey Shafirin, 09/Jun/2006
 */
package gleam.annotatorgui.gui;

import gate.Document;
import gate.creole.ResourceInstantiationException;
import gate.creole.ontology.Ontology;
import gleam.annotatorgui.AnnotatorGUI;
import gleam.annotatorgui.AnnotatorGUIExeption;
import gleam.annotatorgui.AnnotatorGUIListener;
import gleam.annotatorgui.ExecutiveConnection;
import gleam.annotatorgui.AnnotatorTask;
import gleam.annotatorgui.AnnotatorTaskListener;
import gleam.annotatorgui.Connection;
import gleam.annotatorgui.Constants;
import gleam.annotatorgui.DocserviceConnection;
import gleam.annotatorgui.actions.ActionCancelTask;
import gleam.annotatorgui.actions.ActionCheckNewTasks;
import gleam.annotatorgui.actions.ActionEditSettings;
import gleam.annotatorgui.actions.ActionFinishTask;
import gleam.annotatorgui.actions.ActionSaveDocument;
import gleam.annotatorgui.actions.ActionShowDocserviceConnectDialog;
import gleam.annotatorgui.actions.ActionShowHelp;
import gleam.annotatorgui.actions.ActionShowLog;
import gleam.annotatorgui.gui.DocDocumentEditor;
import gleam.annotatorgui.gui.LogFrame;
import gleam.annotatorgui.gui.TaskDocumentEditor;
import gate.gui.docview.DocumentEditor;
import gate.gui.ontology.OntologyEditor;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Main window of the Annotator GUI. <br>
 * It's a singleton, use {@link #getInstance()} to get it. <br>
 * 
 * @author Andrey Shafirin
 */
public class MainFrame extends JFrame implements Constants {

	private static final long serialVersionUID = -5492123118418661456L;

	/** Internal reference to the instance of main window. */
	private static MainFrame ourInstance;

	private JToolBar toolBar;

	/**
	 * Instance of the document editor
	 */
	private DocumentEditor documentEditor;

	/**
	 * Instance of the ontology editor
	 */
	private OntologyEditor ontologyEditor;

	/**
	 * Container for the document editor and ontology editor
	 */
	private JTabbedPane tabbedPane;

	/**
	 * Status label
	 */
	private JLabel statusLabel;

	private MainFrameConnectionListener mainFrameConnectionListener = null;
	
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
		ImageIcon icon = AnnotatorGUI.createIcon("gleam-32.gif");
		if (icon != null)
			this.setIconImage(icon.getImage());
		setTitleStatus(AnnotatorGUI.getConnectionStatus());
		this.setSize(800, 600);
		this.setPreferredSize(new Dimension(800, 600));
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation((d.width - this.getSize().width) / 2, (d.height - this
				.getSize().height) / 2);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent arg0) {
				try {
					if (AnnotatorGUI.closeConnection())
						dispose();
				} catch (AnnotatorGUIExeption ex) {
					ex.printStackTrace();
					JOptionPane.showMessageDialog(MainFrame.getInstance(), ex
							.getMessage()
							+ ((ex.getCause() == null) ? "" : "\n\n"
									+ ex.getCause().getMessage()), "Error!",
							JOptionPane.ERROR_MESSAGE);
					dispose();
				} finally {
					if(mainFrameConnectionListener != null) {
						mainFrameConnectionListener.getTimer().cancel();
					}
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
		mainFrameConnectionListener = new MainFrameConnectionListener();
		AnnotatorGUI.addAnnotatorGUIListener(mainFrameConnectionListener);

		// empty tabbed pane
		tabbedPane = new JTabbedPane();
		tabbedPane.setSize(800, 600);
		tabbedPane.setPreferredSize(new Dimension(800, 600));
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

	/**
	 * updates the text in title bar
	 */
	public void updateTitleStatus() {
		this.setTitle(APP_TITLE + " [" + AnnotatorGUI.getConnectionStatus()
				+ "]");
	}

	/**
	 * updates the text on status bar
	 */
	public void updateBottomStatus() {
		if (this.statusLabel == null)
			this.statusLabel = new JLabel();
		this.statusLabel.setText(getStatusLabelText());
		this.statusLabel.setToolTipText(getStatusLabelToolTipText());
	}

	/**
	 * Sets the text on status bar
	 * 
	 * @param status
	 */
	public void setBottomStatus(String status) {
		if (this.statusLabel == null)
			this.statusLabel = new JLabel();
		this.statusLabel.setText(status);
		this.statusLabel.setToolTipText(status);
	}

	/**
	 * updates the window title and the status bar
	 */
	public void updateAllStatuses() {
		this.setTitle(APP_TITLE + " [" + AnnotatorGUI.getConnectionStatus()
				+ "]");
		updateBottomStatus();
	}

	/**
	 * Enables and disables the toolbar buttons based on the connection type and
	 * available tasks if in the pool mode
	 */
	public void updateAllActions() {
		// lets show the connection dialog to our users
		ActionShowDocserviceConnectDialog.getInstance().setEnabled(true);
		ActionShowHelp.getInstance().setEnabled(true);
		ActionShowLog.getInstance().setEnabled(true);
		ActionEditSettings.getInstance().setEnabled(true);

		// by default turn off these buttons
		ActionSaveDocument.getInstance().setEnabled(false);
		ActionCheckNewTasks.getInstance().setEnabled(false);
		ActionCancelTask.getInstance().setEnabled(false);
		ActionFinishTask.getInstance().setEnabled(false);

		if (AnnotatorGUI.getConnection() != null) {
			Document toLoad = AnnotatorGUI.getConnection().getDocument();

			// enable the save button only if there's a document
			ActionSaveDocument.getInstance().setEnabled(toLoad != null);

			if (AnnotatorGUI.getConnection() instanceof ExecutiveConnection) {
				ExecutiveConnection c = (ExecutiveConnection) AnnotatorGUI
						.getConnection();

        AnnotatorTask task = c.getCurrentAnnotatorTask();
				// user should be allowed to obtain new task if there isn't
				// already one available
				ActionCheckNewTasks.getInstance().setEnabled(
						task == null);

				// finish button should only appear if there's a document
				// loaded
				ActionFinishTask.getInstance().setEnabled(
						task != null);

        // cancel button should only appear if there is a current
        // task and the task allows cancellation
        ActionCancelTask.getInstance().setEnabled(
            task != null && task.getTask() != null &&
            task.getTask().isCancelAllowed());
			}
		}
	}

	/**
	 * Returns tool bar for basic user operations. New one will be created if
	 * needed.
	 */
	public JToolBar getToolBar() {
		if (null != toolBar)
			return toolBar;

		// new task button
		JButton buttonCheckNewTask = new JButton(ActionCheckNewTasks
				.getInstance());
		buttonCheckNewTask.setText("");
		buttonCheckNewTask.setToolTipText("Get New Task");

		// connect to DS button
		JButton buttonConnectToDS = new JButton(
				ActionShowDocserviceConnectDialog.getInstance());
		buttonConnectToDS.setText("");
		buttonConnectToDS.setToolTipText("Connect to Document Service");

		// finish task
		JButton buttonFinish = new JButton(ActionFinishTask.getInstance());
		buttonFinish.setText("");
		buttonFinish.setToolTipText("Finish Task");

		// save the document
		JButton buttonSave = new JButton(ActionSaveDocument.getInstance());
		buttonSave.setText("");
		buttonSave.setToolTipText("Save Document");

		// reject the task
		JButton buttonCancel = new JButton(ActionCancelTask.getInstance());
		buttonCancel.setText("");
		buttonCancel.setToolTipText("Reject Task");

		// help button
		JButton buttonHelp = new JButton(ActionShowHelp.getInstance());
		buttonHelp.setText("");
		buttonHelp.setToolTipText("Help");

		// show log button
		JButton buttonShowLog = new JButton(ActionShowLog.getInstance());
		buttonShowLog.setText("");
		buttonShowLog.setToolTipText("Show Application Log");

		// change preferences
		JButton buttonEditSettings = new JButton(ActionEditSettings
				.getInstance());
		buttonEditSettings.setText("");
		buttonEditSettings.setToolTipText("Edit settings");

		// enable-disable buttons
		updateAllActions();

		// populate toolbar
		toolBar = new JToolBar(JToolBar.HORIZONTAL);
		toolBar.setFloatable(false);

		if (AnnotatorGUI.getProperties().getProperty(MODE_PARAMETER_NAME)
				.equals(DIRECT_MODE)) {
			toolBar.add(buttonConnectToDS);
			toolBar.add(buttonSave);

		} else if (AnnotatorGUI.getProperties()
				.getProperty(MODE_PARAMETER_NAME).equals(POOL_MODE)) {

			toolBar.add(buttonCheckNewTask);
			toolBar.add(buttonFinish);
			toolBar.add(buttonSave);
			toolBar.add(buttonCancel);

		} else {
			System.out.println("Internal error. Undefined mode.");
		}

		toolBar.addSeparator();

		// application log should be enabled based on users's input
		String enableAL = AnnotatorGUI.getProperties().getProperty(
				ENABLE_APPLICATION_LOG_PARAMETER_NAME);
		if (enableAL == null || Boolean.parseBoolean(enableAL))
			toolBar.add(buttonShowLog);

		toolBar.add(buttonHelp);
		toolBar.add(buttonEditSettings);

		return toolBar;
	}

	/**
	 * Label object used for displaying status
	 * 
	 * @return
	 */
	private JLabel getStatusLabel() {
		if (this.statusLabel != null)
			return this.statusLabel;
		this.statusLabel = new JLabel();
		this.statusLabel.setText(getStatusLabelText());
		this.statusLabel.setToolTipText(getStatusLabelToolTipText());
		return statusLabel;
	}

	/**
	 * Returns the text for status bar
	 * 
	 * @return
	 */
	private String getStatusLabelText() {
		Connection c = AnnotatorGUI.getConnection();
		if (c == null) {
			return "Not connected.";
		} else if (c instanceof ExecutiveConnection) {
			return ((ExecutiveConnection) c).getTaskStatus();
		} else if (c instanceof DocserviceConnection) {
			return "Document: "
					+ ((((DocserviceConnection) c).getDocument().isModified()) ? "* "
							: "") + ((DocserviceConnection) c).getDocId();
		} else {
			return "Internal error: unknown type of connection.";
		}
	}

	/**
	 * Returns the tooltip text for status bar
	 * 
	 * @return
	 */
	private String getStatusLabelToolTipText() {
		Connection c = AnnotatorGUI.getConnection();
		if (c == null) {
			return "Not connected.";
		} else if (c instanceof ExecutiveConnection) {
			return ((ExecutiveConnection) c).getTaskDetails();
		} else if (c instanceof DocserviceConnection) {
			return "Document "
					+ ((((DocserviceConnection) c).getDocument().isModified()) ? "* "
							: "") + ((DocserviceConnection) c).getDocId();
		} else {
			return "";
		}
	}

	/**
	 * MainFrame Connection Listener
	 * 
	 * @author niraj
	 */
	class MainFrameConnectionListener implements AnnotatorGUIListener {

		private Timer timeoutTimer = new Timer(this.getClass().getName()
				+ " timeout timer");
		private SaveDocumentTask docSaveTask = new SaveDocumentTask();
		private boolean taskScheduled = false;
		public Timer getTimer() {
			return this.timeoutTimer;
		}
		
		public void connectionChanged(Connection newConnection) {
			// cancel the existing timer task if it is not null
			if(docSaveTask != null && taskScheduled) {
				docSaveTask.cancel();
			}
			
			MainFrame.getInstance().updateAllActions();
			MainFrame.getInstance().updateAllStatuses();
			if (newConnection instanceof ExecutiveConnection) {
				((ExecutiveConnection) newConnection)
						.addConnectionListener(new AnnotatorTaskListener() {
							public void taskChanged(
									AnnotatorTask newAnnotatorTask) {
								MainFrame.getInstance().updateAllStatuses();
								MainFrame.getInstance().updateAllActions();
								if (newAnnotatorTask == null) {
									if (documentEditor != null)
										tabbedPane.removeAll();
								} else {
									try {
										// lets create a new instance of the
										// task document editor
										if (documentEditor != null) {
											tabbedPane.removeAll();
										}

										initializeDocumentEditor(null,
												newAnnotatorTask);
										initializeOntologyEditor(null,
												newAnnotatorTask);
										tabbedPane
												.setSelectedComponent(documentEditor);

									} catch (ResourceInstantiationException e) {
										e.printStackTrace();
									}
								}
							}
						});
				if (((ExecutiveConnection) newConnection)
						.getCurrentAnnotatorTask() == null) {
					if (documentEditor != null) {
						tabbedPane.removeAll();
					}
				} else {
					try {
						if (((ExecutiveConnection) newConnection)
								.getCurrentAnnotatorTask() == null)
							return;

						if (documentEditor != null) {
							tabbedPane.removeAll();
						}

						AnnotatorTask task = ((ExecutiveConnection) newConnection)
								.getCurrentAnnotatorTask();

						initializeDocumentEditor(newConnection, task);
						initializeOntologyEditor(newConnection, task);
						tabbedPane.setSelectedComponent(documentEditor);

					} catch (ResourceInstantiationException e) {
						e.printStackTrace();
					}
				}
			} else if (newConnection instanceof DocserviceConnection) {
				try {
					if (documentEditor != null) {
						tabbedPane.removeAll();
					}

					initializeDocumentEditor(newConnection, null);
					initializeOntologyEditor(newConnection, null);
					tabbedPane.setSelectedComponent(documentEditor);

				} catch (ResourceInstantiationException e) {
					e.printStackTrace();
				}
			}
		}

		/**
		 * initializes the document editor bases on the connection type
		 * 
		 * @param newConnection
		 * @throws ResourceInstantiationException
		 */
		private void initializeDocumentEditor(Connection newConnection,
				AnnotatorTask task) throws ResourceInstantiationException {

			if (newConnection != null
					&& newConnection instanceof DocserviceConnection) {
				documentEditor = new DocDocumentEditor();
			} else if (task != null) {
				documentEditor = new TaskDocumentEditor();
				((TaskDocumentEditor) documentEditor).setAnnotatorTask(task);
			}

			if (documentEditor != null) {
				if (newConnection != null)
					documentEditor.setTarget(newConnection.getDocument());

				if (task != null) {
					documentEditor.setTarget(task.getDocument());
					if(!taskScheduled) {
						// task scheduled at every 5 mins
						timeoutTimer.scheduleAtFixedRate(docSaveTask,
								300000, 300000);
						taskScheduled = true;
					}
				}

				documentEditor.setSize(800, 600);
				documentEditor.setPreferredSize(new Dimension(800, 600));
				documentEditor.init();
				tabbedPane.add("Document Editor", documentEditor);
				documentEditor.setVisible(false);
				documentEditor.update(documentEditor.getGraphics());
				documentEditor.setVisible(true);
				documentEditor.update(documentEditor.getGraphics());
			}

		}

		/**
		 * Initializes the ontology editor and adds it to the tabbed pane if and
		 * only if user has asked to enable the ontology editor
		 * 
		 * @param ontology
		 * @throws ResourceInstantiationException
		 */
		private void initializeOntologyEditor(Connection newConnection,
				AnnotatorTask task) throws ResourceInstantiationException {

			String enableOE = (String) AnnotatorGUI.getProperties().get(
					Constants.ENABLE_ONTOLOGY_EDITOR_PARAMETER_NAME);
			boolean eoe = false;
			if (enableOE != null) {
				try {
					eoe = Boolean.parseBoolean(enableOE);
				} catch (Exception e) {
					eoe = false;
				}
			}

			Ontology ontology = null;
			if (newConnection != null
					&& newConnection instanceof DocserviceConnection) {
				ontology = ((DocserviceConnection) newConnection).getOntology();
			} else if (task != null) {
				ontology = task.getOntology();
			}

			if (ontology != null && eoe) {
				ontologyEditor = new OntologyEditor();
				ontologyEditor.init();
				ontologyEditor.setTarget(ontology);
				ontologyEditor.setSize(800, 600);
				ontologyEditor.setPreferredSize(new Dimension(800, 600));
				tabbedPane.add("Ontology Editor", ontologyEditor);
				ontologyEditor.setVisible(false);
				ontologyEditor.update(ontologyEditor.getGraphics());
				ontologyEditor.setVisible(true);
				ontologyEditor.update(ontologyEditor.getGraphics());
				tabbedPane.updateUI();
			}
		}

		class SaveDocumentTask extends TimerTask {
			public void run() {
				String oldStatus = getStatusLabelText();
				setBottomStatus("Autosaving document...");
				ActionSaveDocument.getInstance().actionPerformed(null);
				if(oldStatus != null) setBottomStatus(oldStatus);
				else setBottomStatus("Document saved!");
			}
		}

	}

}
