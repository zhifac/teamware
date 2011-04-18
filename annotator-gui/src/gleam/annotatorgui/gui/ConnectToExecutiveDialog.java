/*
 *  ConnectToExecutiveDialog.java
 *
 *  Copyright (c) 2006-2006, The University of Sheffield.
 *
 *  Andrey Shafirin, 09/Jun/2006
 */
package gleam.annotatorgui.gui;

import gleam.annotatorgui.AnnotatorGUI;
import gleam.annotatorgui.AnnotatorGUIExeption;
import gleam.annotatorgui.ExecutiveConnection;
import gleam.annotatorgui.AnnotatorTask;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.URI;

/**
 * Element of GUI to allow for user to select Executive Callback service to
 * connect to and perform connection. <br>
 *
 * @author Andrey Shafirin
 */
public class ConnectToExecutiveDialog extends ConnectDialog {

	private JTextField executiveServiceUrl;

	private JTextField userId;

	private JPasswordField userPassword;

	private boolean silent = false;

	private static final String CONNECTION_TITLE = "Executive Service";

	public static ConnectToExecutiveDialog thisInstance;

	public static ConnectToExecutiveDialog getExistingInstance() {
		return thisInstance;
	}

	public static ConnectToExecutiveDialog getNewInstance(Frame owner,
			boolean autoconnect, boolean silent) {
		if (thisInstance != null)
			throw new RuntimeException(
					"Internal error. Only one Instance of this class allowed at any time.");
		return new ConnectToExecutiveDialog(owner, autoconnect, silent);
	}

	public void dispose() {
		super.dispose();
		thisInstance = null;
	}

	/**
	 * Constructs this dialog.
	 *
	 * @param owner
	 *            the <code>Frame</code> from which the dialog is displayed
	 * @param autoconnect
	 *            set <code>true</code> to perform connection automatically
	 *            after dialog created, <code>false</code> otherwise.
	 * @param silent
	 *            if <code>true</code> - user messages are not displayed
	 *            (error messages will be displayed anyway). Can be useful to
	 *            check if new tasks are available. <code>false</code>
	 *            otherwise.
	 */
	private ConnectToExecutiveDialog(Frame owner, boolean autoconnect,
			boolean silent) {
		super(owner, CONNECTION_TITLE, true);
		this.silent = silent;
		initGUI();
		hookupEvents();
		if (AnnotatorGUI.getConnection() != null
				&& AnnotatorGUI.getConnection() instanceof ExecutiveConnection) {
			// get new task
			System.out
					.println("ConnectToExecutiveDialog - use existing connection - accept new task");
			acceptNewTaskAsync();
		} else {
			System.out
					.println("ConnectToExecutiveDialog Not connected - connect again");
			if (autoconnect) {
				connect();
			}
		}
	}

	/**
	 * Constructs all needed GUI elements of this dialog.
	 */
	protected void initGUI() {
		executiveServiceUrl = new JTextField();
		userId = new JTextField();
		userPassword = new JPasswordField("");
		if (AnnotatorGUI.getConnection() != null
				&& AnnotatorGUI.getConnection() instanceof ExecutiveConnection) {
			executiveServiceUrl.setText(((ExecutiveConnection) AnnotatorGUI
					.getConnection()).getExecutiveUrlString());
			userId.setText(((ExecutiveConnection) AnnotatorGUI.getConnection())
					.getUserId());
			userPassword.setText(((ExecutiveConnection) AnnotatorGUI
					.getConnection()).getUserPassword());
		} else {
			executiveServiceUrl.setText(AnnotatorGUI.getProperties()
					.getProperty(EXECUTIVE_SERVICE_URL_PARAMETER_NAME,
							EXECUTIVE_SERVICE_DEFAULT_URL));
			userId.setText(AnnotatorGUI.getProperties().getProperty(
					USER_ID_PARAMETER_NAME, ""));
			userPassword.setText(AnnotatorGUI.getProperties().getProperty(
					USER_PASSWORD_PARAMETER_NAME, ""));
		}
		executiveServiceUrl
				.setToolTipText("Executive Callback Service URL to connect to");
		userId.setToolTipText("User ID");
		userPassword.setToolTipText("User password");
		buttonConnect = new JButton("Connect");
		buttonConnect.setToolTipText("Connect to Service");
		buttonCancel = new JButton("Cancel");
		buttonCancel.setToolTipText("Cancel");
		statusMessage = new JLabel(AnnotatorGUI.getConnectionStatus());
		this.getContentPane().setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(5, 5, 5, 5);
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0;
		c.weighty = 0;
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 2;
		this.getContentPane().add(new JLabel("Executive service URL"), c);
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 2;
		this.getContentPane().add(executiveServiceUrl, c);
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 1;
		this.getContentPane().add(new JLabel("User ID"), c);
		c.gridx = 1;
		c.gridy = 2;
		c.gridwidth = 1;
		this.getContentPane().add(new JLabel("User Password"), c);
		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 1;
		this.getContentPane().add(userId, c);
		c.gridx = 1;
		c.gridy = 3;
		c.gridwidth = 1;
		this.getContentPane().add(userPassword, c);
		c.gridx = 0;
		c.gridy = 4;
		c.gridwidth = 2;
		this.getContentPane().add(statusMessage, c);
		c.weightx = 1;
		c.weighty = 1;
		c.gridx = 0;
		c.gridy = 5;
		c.gridwidth = 1;
		this.getContentPane().add(buttonConnect, c);
		c.gridx = 1;
		c.gridy = 5;
		this.getContentPane().add(buttonCancel, c);
		this.setSize(400, 200);
		this.setLocation(Math.min(getToolkit().getScreenSize().width
				- getWidth(), getOwner().getX() + getOwner().getWidth() / 2
				- getWidth() / 2), Math.min(getToolkit().getScreenSize().height
				- getHeight(), getOwner().getY() + getOwner().getHeight() / 2
				- getHeight() / 2));
		this.setResizable(false);
	}

	/**
	 * Adds event handling for this dialog and its elements.
	 */
	protected void hookupEvents() {
		buttonConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				connect();
			}
		});
		buttonCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancel();
			}
		});
		// allow close dialog by 'Esc'
		getRootPane()
				.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
				.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
						"escPressed");
		getRootPane().getActionMap().put("escPressed", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				cancel();
			}
		});
		addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					cancel();
				}
			}
		});
		// allow connect by 'Enter'
		userPassword.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
				.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
						"enterPressed");
		userPassword.getActionMap().put("enterPressed", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				connect();
			}
		});
		userPassword.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					connect();
				}
			}
		});
	}

	/**
	 * Performs connection to the server.
	 */
	protected void connect() {
		// ensureEventThread();
		buttonConnect.setEnabled(false);
		/*
		 * connectSync(this.executiveServiceUrl.getText().trim(), this.userId
		 * .getText(), this.userPassword.getText());
		 */
		String executiveString = this.executiveServiceUrl.getText().trim();
		statusMessage.setText("Connecting to " + executiveString + " ...");
		try {
			MainFrame.getInstance().setBottomStatus(
					"Connecting to " + executiveString + " ...");
			ExecutiveConnection connection = new ExecutiveConnection(
					executiveString, this.userId.getText(), this.userPassword
							.getText());
			// set this connection in AnnotatorGUI (on global level)
			setConnectionSafely(connection, null);

			if (Thread.currentThread().isInterrupted()
					|| Thread.currentThread() != connectionThread) {
				buttonConnect.setEnabled(true);
				return;
			}
			dispose();
			return;
		} catch (AnnotatorGUIExeption e) {
			handleError(e);
			buttonConnect.setEnabled(true);
		}
	}

	/**
	 * Starts connection process asynchronously.
	 */
	private void acceptNewTaskAsync() {
		// Called by event thread, but can be safely called by any thread.

		Runnable connectRun = new Runnable() {
			public void run() {
				AnnotatorTask task = null;
				ExecutiveConnection connection = ((ExecutiveConnection) AnnotatorGUI
						.getConnection());
				if (connection != null) {
					try {
						statusMessage.setText("Requesting Annotation Task...");
						MainFrame.getInstance().setBottomStatus(
								"Requesting Annotation Task ...");

						// first see if you have task ready.
						task = connection.getNextAnnotatorTask();

						boolean isNewTaskAvailable = false;
						if(task!=null) {
							System.out.println("There is a pending task "
									+ task.getTaskId());
							isNewTaskAvailable = true;
						}
						else {
							isNewTaskAvailable = connection.checkForNextTask();
						}
						System.out.println("isNewTaskAvailable "
								+ isNewTaskAvailable);
						if (isNewTaskAvailable) {

							statusMessage
									.setText("Searching for your next Annotation Task...");
							MainFrame
									.getInstance()
									.setBottomStatus(
											"Searching for your next Annotation Task...");
							MessageDialog m = new MessageDialog(MainFrame
									.getInstance(),
									ANNOTATOR_GUI_DIALOG_TITLE,
									"Searching for Annotation Task",
									15000);
							m.setVisible(true);
							//Thread.sleep(15000);
							int timeInc = 0;
                            while(timeInc <10 && task == null){
							 System.out.println("Call getNextAnnotatorTask for the "+timeInc + " time.");
                             task = connection.getNextAnnotatorTask();

							if (Thread.currentThread().isInterrupted()
									|| Thread.currentThread() != connectionThread) {
								task.cleanup();
								buttonConnect.setEnabled(true);
								return;
							}
							  //Thread.sleep(20000);
							  timeInc ++;
                            }
							if (task != null) {
								statusMessage
										.setText("Loading Annotation Task...");
								MainFrame.getInstance().setBottomStatus(
										"Loading Annotation Task...");
								MessageDialog m1 = new MessageDialog(MainFrame
										.getInstance(),
										ANNOTATOR_GUI_DIALOG_TITLE,
										"Loading Annotation Task", 3000);
								m1.setVisible(true);
								URI owlimServiceURL = task.getTask()
										.getOwlimServiceURL();
								String repositoryName = task.getTask()
										.getOwlimRepositoryName();

								if (owlimServiceURL != null
										&& repositoryName != null
										&& repositoryName.trim().length() > 0) {
									statusMessage
											.setText("Loading an ontology for Annotator Task...");
									MainFrame
											.getInstance()
											.setBottomStatus(
													"Loading an ontology for Annotator Task...");
									try {
										task.loadOntology();
									} catch (Exception e) {
										handleError(new AnnotatorGUIExeption(
												"Failed to load ontology. Unexpected error:\n\n"
														+ e.getClass()
																.getName()
														+ " occured. Message:\n"
														+ e.getMessage(), e));
									}
								}

								if (Thread.currentThread().isInterrupted()
										|| Thread.currentThread() != connectionThread) {
									if (task != null)
										task.cleanup();
									buttonConnect.setEnabled(true);
									return;
								}

							} else {
								MainFrame.getInstance().setBottomStatus(
										"No tasks available.");
								// if (!silent) {
								MessageDialog m2 = new MessageDialog(MainFrame
										.getInstance(), ANNOTATOR_GUI_DIALOG_TITLE,
										"No tasks available.", 7000);
								m2.setVisible(true);
								// JOptionPane.showMessageDialog(null, "No tasks
								// available.",
								// "Message", JOptionPane.PLAIN_MESSAGE);}
								// }
							}
						} else {
							MainFrame.getInstance().setBottomStatus(
									"No tasks available.");
							if (!silent) {
								MessageDialog m = new MessageDialog(MainFrame
										.getInstance(), ANNOTATOR_GUI_DIALOG_TITLE,
										"No tasks available.", 7000);
								m.setVisible(true);
								// JOptionPane.showMessageDialog(null, "No tasks
								// available.",
								// "Message", JOptionPane.PLAIN_MESSAGE);}
							}
						}
						setConnectionSafely(connection, task);
						dispose();
						return;
					} catch (AnnotatorGUIExeption e) {
						handleError(e);
						buttonConnect.setEnabled(true);
					} catch (Throwable e) {
						if (task != null)
							try {
								task.cleanup();
							} catch (AnnotatorGUIExeption e1) {
							}
						handleError(new AnnotatorGUIExeption(
								"Unexpected Internal error:\n\n"
										+ e.getClass().getName()
										+ " occured. Message:\n"
										+ e.getMessage(), e));
						buttonConnect.setEnabled(true);
					}
				} else {
					MainFrame.getInstance().setBottomStatus("Connection lost.");
					if (!silent) {
						MessageDialog m = new MessageDialog(MainFrame
								.getInstance(), ANNOTATOR_GUI_DIALOG_TITLE,
								"The connection with Workflow System has been lost. Please reconnect.", 5000);
						m.setVisible(true);
						// JOptionPane.showMessageDialog(null, "No tasks
						// available.",
						// "Message", JOptionPane.PLAIN_MESSAGE);}
					}
				}
			}
		};
		connectionThread = new Thread(connectRun, "connectionThread");
		connectionThread.start();
	}

}
