package gleam.annotatorgui.gui;

import gleam.annotatorgui.AnnotatorGUI;
import gleam.annotatorgui.AnnotatorGUIExeption;
import gleam.annotatorgui.DocserviceConnection;

import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

public class ConnectToDocserviceDialog extends ConnectDialog {

	private JTextField docserviceUrl;

	/**
	 * JTextField for owlimServiceURL
	 */
	private JTextField owlimServiceURL;

	private JTextField owlimRepositoryName;

	private JTextField ontologyUrl;

	private JTextField documentId;

	private JComboBox annotationSetName;

	private static final String CONNECTION_TITLE = "Document Service";

	/**
	 * When autoconnection mode is true the dialog is folded.
	 */
	private boolean autoconnect;

	private JButton buttonDetails;
	private JLabel docserviceLabel;
	private JLabel documentIdLabel;
	private JLabel owlimServiceLabel;
	private JLabel owlimRepositoryNameLabel;
	private JLabel ontologyUrlLabel;
	private JLabel annotationSetNameLabel;

	private ConnectToDocserviceDialog dialogInstance = this;

	public ConnectToDocserviceDialog(Frame owner, boolean autoconnect) {
		super(owner, CONNECTION_TITLE, true);
		this.autoconnect = autoconnect;
		initGUI();
		hookupEvents();
		if (autoconnect) {
			docserviceUrl.setText(AnnotatorGUI.getProperties().getProperty(
					DOCSERVICE_URL_PARAMETER_NAME));
			documentId.setText(AnnotatorGUI.getProperties().getProperty(
					DOC_ID_PARAMETER_NAME));
			connect();
		}
	}

	/**
	 * Constructs all needed GUI elements of this dialog.
	 */
	protected void initGUI() {
		setResizable(true);
		docserviceLabel = new JLabel("Document Service URL");
		docserviceUrl = new JTextField();
		documentIdLabel = new JLabel("Document ID");
		documentId = new JTextField();
		annotationSetNameLabel = new JLabel("Annotation Set Name");
		annotationSetName = new JComboBox(new String[] { "",
				DEFAULT_AS_NAME_PARAMETER_VALUE });
		annotationSetName.setEditable(true);
		ontologyUrlLabel = new JLabel("Ontology URL");
		ontologyUrl = new JTextField();
		owlimServiceLabel = new JLabel("Owlim Service URL");
		owlimServiceURL = new JTextField();
		owlimRepositoryNameLabel = new JLabel("OWLIM Repository Name");
		owlimRepositoryName = new JTextField();
		if (AnnotatorGUI.getConnection() != null
				&& AnnotatorGUI.getConnection() instanceof DocserviceConnection) {
			docserviceUrl.setText(((DocserviceConnection) AnnotatorGUI
					.getConnection()).getDocserviceUrlString());
			owlimServiceURL.setText(((DocserviceConnection) AnnotatorGUI
					.getConnection()).getOwlimServiceUrlString());
			owlimRepositoryName.setText(((DocserviceConnection) AnnotatorGUI
					.getConnection()).getOwlimRepositoryName());
			ontologyUrl.setText(((DocserviceConnection) AnnotatorGUI
					.getConnection()).getOntologyUrlString());
			documentId.setText(((DocserviceConnection) AnnotatorGUI
					.getConnection()).getDocId());
			String asName = ((DocserviceConnection) AnnotatorGUI
					.getConnection()).getAnnotationSetName();
			if (asName == null) {
				annotationSetName.getModel().setSelectedItem(
						DEFAULT_AS_NAME_PARAMETER_VALUE);
			} else if (asName == "") {
				annotationSetName.getModel().setSelectedItem("");
			} else {
				annotationSetName.addItem(asName);
				annotationSetName.getModel().setSelectedItem(asName);
			}
		} else {
			docserviceUrl.setText(AnnotatorGUI.getProperties().getProperty(
					DOCSERVICE_URL_PARAMETER_NAME, ""));
			documentId.setText(AnnotatorGUI.getProperties().getProperty(
					DOC_ID_PARAMETER_NAME, ""));
			ontologyUrl.setText(AnnotatorGUI.getProperties().getProperty(
					ONTOLOGY_URL_PARAMETER_NAME, ""));
			owlimServiceURL.setText(AnnotatorGUI.getProperties().getProperty(
					OWLIMSERVICE_URL_PARAMETER_NAME, ""));
			owlimRepositoryName.setText(AnnotatorGUI.getProperties()
					.getProperty(REPOSITORY_PARAMETER_NAME, ""));
			String asNameParam = AnnotatorGUI.getProperties().getProperty(
					ANNSET_NAME_PARAMETER_NAME);
			if (asNameParam == null) {
				annotationSetName.getModel().setSelectedItem("");
			} else if (asNameParam == "") {
				annotationSetName.getModel().setSelectedItem(
						DEFAULT_AS_NAME_PARAMETER_VALUE);
			} else {
				annotationSetName.addItem(asNameParam);
				annotationSetName.getModel().setSelectedItem(asNameParam);
			}
		}
		docserviceUrl.setToolTipText("Document Service URL to connect to");
		owlimServiceURL.setToolTipText("OWLIM Service URL to connect to");
		owlimRepositoryName.setToolTipText("Repository to store data into");
		ontologyUrl.setToolTipText("Ontology to load");
		documentId.setToolTipText("Document ID to load");
		buttonConnect = new JButton("Connect");
		buttonConnect.setToolTipText("Connect to Service");
		buttonCancel = new JButton("Cancel");
		buttonCancel.setToolTipText("Cancel");
		buttonDetails = new JButton("Details");
		buttonDetails.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonDetails.setVisible(false);
				docserviceLabel.setVisible(true);
				docserviceUrl.setVisible(true);
				documentIdLabel.setVisible(true);
				documentId.setVisible(true);
				annotationSetNameLabel.setVisible(true);
				annotationSetName.setVisible(true);
				ontologyUrlLabel.setVisible(true);
				ontologyUrl.setVisible(true);
				owlimServiceLabel.setVisible(true);
				owlimServiceURL.setVisible(true);
				owlimRepositoryNameLabel.setVisible(true);
				owlimRepositoryName.setVisible(true);
				buttonConnect.setVisible(true);
				dialogInstance.pack();
			}
		});

		statusMessage = new JLabel(AnnotatorGUI.getConnectionStatus());
		statusMessage.setFont(statusMessage.getFont().deriveFont(Font.PLAIN,
				9.0F));

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
		this.getContentPane().add(docserviceLabel, c);
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 2;
		this.getContentPane().add(docserviceUrl, c);
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 2;
		this.getContentPane().add(documentIdLabel, c);
		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 2;
		this.getContentPane().add(documentId, c);
		c.gridx = 0;
		c.gridy = 4;
		c.gridwidth = 2;
		this.getContentPane().add(annotationSetNameLabel, c);
		c.gridx = 0;
		c.gridy = 5;
		c.gridwidth = 2;
		this.getContentPane().add(annotationSetName, c);
		c.gridx = 0;
		c.gridy = 6;
		c.gridwidth = 2;
		this.getContentPane().add(ontologyUrlLabel, c);
		c.gridx = 0;
		c.gridy = 7;
		c.gridwidth = 2;
		this.getContentPane().add(ontologyUrl, c);
		c.gridx = 0;
		c.gridy = 8;
		c.gridwidth = 2;
		this.getContentPane().add(owlimServiceLabel, c);
		c.gridx = 0;
		c.gridy = 9;
		c.gridwidth = 2;
		this.getContentPane().add(owlimServiceURL, c);
		c.gridx = 0;
		c.gridy = 10;
		c.gridwidth = 2;
		this.getContentPane().add(owlimRepositoryNameLabel, c);
		c.gridx = 0;
		c.gridy = 11;
		c.gridwidth = 2;
		this.getContentPane().add(owlimRepositoryName, c);
		c.gridx = 0;
		c.gridy = 12;
		c.gridwidth = 2;
		this.getContentPane().add(statusMessage, c);
		c.weightx = 1;
		c.weighty = 1;
		c.gridx = 0;
		c.gridy = 13;
		c.gridwidth = 1;
		this.getContentPane().add(buttonConnect, c);
		c.gridx = 1;
		c.gridy = 13;
		this.getContentPane().add(buttonCancel, c);
		c.gridx = 0;
		c.gridy = 13;
		this.getContentPane().add(buttonDetails, c);

		if (autoconnect) {
			docserviceLabel.setVisible(false);
			docserviceUrl.setVisible(false);
			documentIdLabel.setVisible(false);
			documentId.setVisible(false);
			annotationSetNameLabel.setVisible(false);
			annotationSetName.setVisible(false);
			ontologyUrlLabel.setVisible(false);
			ontologyUrl.setVisible(false);
			owlimServiceLabel.setVisible(false);
			owlimServiceURL.setVisible(false);
			owlimRepositoryNameLabel.setVisible(false);
			owlimRepositoryName.setVisible(false);
			buttonConnect.setVisible(false);
		} else {
			buttonDetails.setVisible(false);
		}

		this.pack();// setSize(500, 320);
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
		documentId.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
				.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
						"enterPressed");
		documentId.getActionMap().put("enterPressed", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				connect();
			}
		});
		documentId.addKeyListener(new KeyAdapter() {
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
		if (annotationSetName.getSelectedItem().equals(
				DEFAULT_AS_NAME_PARAMETER_VALUE)) {
			connectAsync(this.docserviceUrl.getText().trim(), this.ontologyUrl
					.getText().trim(), this.owlimServiceURL.getText(),
					this.owlimRepositoryName.getText(),
					this.documentId.getText(), null);
		} else {
			connectAsync(this.docserviceUrl.getText().trim(), this.ontologyUrl
					.getText().trim(), this.owlimServiceURL.getText(),
					this.owlimRepositoryName.getText(),
					this.documentId.getText(),
					(String) annotationSetName.getSelectedItem());
		}
	}

	/**
	 * Starts connection process asynchronously.
	 */
	protected void connectAsync(final String docserviceUrlString,
			final String ontologyUrlString, final String owlimServiceURL,
			final String owlimRepositoryName, final String docId,
			final String annSetName) {

		// Called by event thread, but can be safely called by any thread.
		Runnable connectRun = new Runnable() {
			public void run() {
				DocserviceConnection connection = null;
				try {
					try {
						statusMessage.setText("Closing current connection...");
						AnnotatorGUI.closeConnection();
					} catch (AnnotatorGUIExeption e) {
						handleError(new AnnotatorGUIExeption(
								"An error occured while closing current connection.\n\n"
										+ e.getClass().getName()
										+ " occured. Message:\n"
										+ e.getMessage(), e));
					}
					if (Thread.currentThread().isInterrupted()
							|| Thread.currentThread() != connectionThread) {
						buttonConnect.setEnabled(true);
						return;
					}
					statusMessage.setText("Connecting to "
							+ docserviceUrlString + " ...");
					connection = new DocserviceConnection(docserviceUrlString,
							docId, annSetName);
					if (Thread.currentThread().isInterrupted()
							|| Thread.currentThread() != connectionThread) {
						buttonConnect.setEnabled(true);
						return;
					}

					// load an ontology if either (a) service and repository are
					// specified
					// or (b) ontology ontologyURL is specified.
					boolean goAhead = false;
					if ((owlimServiceURL != null
							&& owlimServiceURL.trim().length() > 0
							&& owlimRepositoryName != null && owlimRepositoryName
							.trim().length() > 0)
							|| (ontologyUrlString != null && ontologyUrlString
									.trim().length() > 0)) {
						goAhead = true;
					}

					if (goAhead) {
						statusMessage.setText("Loading an ontology...");
						try {
							connection.loadOntology(ontologyUrlString,
									owlimServiceURL, owlimRepositoryName);
						} catch (Exception e) {
							handleError(new AnnotatorGUIExeption(
									"Failed to load ontology. Unexpected error:\n\n"
											+ e.getClass().getName()
											+ " occured. Message:\n"
											+ e.getMessage(), e));
						}
					}

					if (Thread.currentThread().isInterrupted()
							|| Thread.currentThread() != connectionThread) {
						buttonConnect.setEnabled(true);
						return;
					}

					statusMessage.setText("Loading document...");
					connection.loadDocument();
					if (Thread.currentThread().isInterrupted()
							|| Thread.currentThread() != connectionThread) {
						if (connection != null)
							connection.cleanup();
						buttonConnect.setEnabled(true);
						return;
					}
					statusMessage.setText("Applying connection...");
					setConnectionSafely(connection, null);
					dispose();
					return;
				} catch (AnnotatorGUIExeption e) {
					// FIXME: provide some neat solution for resource locking!
					// JIRA:TMW-7
					try {
						if (e.getCause().getCause().getCause().getMessage()
								.equals("LOCK")) {
							JOptionPane.showMessageDialog(null,
									"The document is being edited and is locked!", "Cannot edit document",
									JOptionPane.INFORMATION_MESSAGE);
							ConnectToDocserviceDialog.this.hide();
						}
						return;
					} catch (NullPointerException npe) {
						// ignore
					}
					// -----------------------------------
					handleError(e);
					buttonConnect.setEnabled(true);
				} catch (Throwable e) {
					if (connection != null) {
						try {
							connection.cleanup();
						} catch (AnnotatorGUIExeption e1) {
						}
					}
					handleError(new AnnotatorGUIExeption(
							"Unexpected Internal error:\n\n"
									+ e.getClass().getName()
									+ " occured. Message:\n" + e.getMessage(),
							e));
					buttonConnect.setEnabled(true);
				}
			}
		};
		connectionThread = new Thread(connectRun, "connectionThread");
		connectionThread.start();
	}
}
