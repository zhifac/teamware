/*
 *  ConnectToANNICDialog.java
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
package gate.teamware.richui.annic.gui;

import gate.teamware.richui.annic.*;
import gate.teamware.richui.common.RichUIException;

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
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

/**
 * This class provides a GUI implementation for the ConnectToANNIC dialog.
 * 
 * @author niraj
 * 
 */
public class ConnectToANNICDialog extends ConnectDialog {

	/**
	 * This is where user provides a doc-service URL
	 */
	private JTextField docserviceUrl;

	/**
	 * This is hwere user provides a corpusID in which user wants to search for
	 * patterns.
	 */
	private JTextField corpusId;

	private JButton buttonDetails;
  private JLabel docserviceLabel;
  private JLabel corpusIdLabel;
  private ConnectToANNICDialog dialogInstance = this;

	/**
	 * Connection title for the dialog
	 */
	private static final String CONNECTION_TITLE = "ANNIC Service";

  /**
   * When autoconnection mode is true the dialog is folded.
   */
  private boolean autoconnect;

	/**
	 * Constructor
	 * 
	 * @param owner
	 *            typically the annic gui mainFrame
	 * @param autoconnect
	 *            indicates whether it should wait for user to provide/alter
	 *            parameters in the dialog or directly connect to the
	 *            doc-service with the default parameters
	 */
	public ConnectToANNICDialog(Frame owner, boolean autoconnect) {
		super(owner, CONNECTION_TITLE, true);
		this.autoconnect = autoconnect;
    // initialize the dialog components
		initGUI();
		
		// events for the buttons (connect, cancel)
		hookupEvents();
		
		// if autoconnect set to true, connect to the doc-service
		if (autoconnect) {
			docserviceUrl.setText(AnnicGUI.getProperties().getProperty(
					DOCSERVICE_URL_PARAMETER_NAME));
			corpusId.setText(AnnicGUI.getProperties().getProperty(
					CORPUS_ID_PARAMETER_NAME));
			connect();
		}
	}

	/**
	 * Constructs all needed GUI elements of this dialog.
	 */
	protected void initGUI() {
		JDialog.setDefaultLookAndFeelDecorated(true);
		setResizable(true);
		docserviceUrl = new JTextField();
		corpusId = new JTextField();
		if (AnnicGUI.getConnection() != null
				&& AnnicGUI.getConnection() instanceof DocserviceConnection) {
			docserviceUrl.setText(((DocserviceConnection) AnnicGUI
					.getConnection()).getDocserviceUrlString());
			corpusId.setText(((DocserviceConnection) AnnicGUI.getConnection())
					.getCorpusId());
		} else {
			docserviceUrl.setText(AnnicGUI.getProperties().getProperty(
					DOCSERVICE_URL_PARAMETER_NAME));
			corpusId.setText(AnnicGUI.getProperties().getProperty(
					CORPUS_ID_PARAMETER_NAME));
		}

    docserviceLabel = new JLabel("Document Service URL");
		docserviceUrl.setToolTipText("Document Service URL to connect to");
    corpusIdLabel = new JLabel("Corpus ID");
    corpusId.setToolTipText("Corpus in which to search");
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
        corpusIdLabel.setVisible(true);
        corpusId.setVisible(true);
        buttonConnect.setVisible(true);
        dialogInstance.pack();
      }
    });

		statusMessage = new JLabel(AnnicGUI.getConnectionStatus());
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
		this.getContentPane().add(corpusIdLabel, c);
		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 2;
		this.getContentPane().add(corpusId, c);
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
    c.gridx = 0;
    c.gridy = 5;
    this.getContentPane().add(buttonDetails, c);

    if (autoconnect) {
      docserviceLabel.setVisible(false);
      docserviceUrl.setVisible(false);
      corpusIdLabel.setVisible(false);
      corpusId.setVisible(false);
      buttonConnect.setVisible(false);
    } else {
      buttonDetails.setVisible(false);
    }
    this.pack();
    // we need to show the connect dialog
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
		// what happens when a user clicks on the connect button
		buttonConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				connect();
			}
		});
		
		// cancel event should be invokved 
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
		corpusId.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
				.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
						"enterPressed");
		corpusId.getActionMap().put("enterPressed", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				connect();
			}
		});
		corpusId.addKeyListener(new KeyAdapter() {
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

		// when we start connecting, we disable the connect button
		buttonConnect.setEnabled(false);
		
		// connect asynchronously
		connectAsync(this.docserviceUrl.getText().trim(), this.corpusId
				.getText().trim());
	}

	/**
	 * Starts connection process asynchronously.
	 */
	protected void connectAsync(final String docserviceUrlString,
			final String corpusId) {

		// Called by event thread, but can be safely called by any thread.
		Runnable connectRun = new Runnable() {
			public void run() {
				DocserviceConnection connection = null;
				try {
					// if there is already a connection established, we first close it
					try {
						statusMessage.setText("Closing current connection...");
						AnnicGUI.closeConnection();
					} catch (RichUIException e) {
						handleError(new RichUIException(
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
					
					// lets start connection with docservice
					statusMessage.setText("Connecting to "
							+ docserviceUrlString + " ...");
					
					// we provide the docservice URL as well as the corpus ID
					// this ID is stored in a docService connection, which we 
					// retrieve from it whenever we are interested to know
					// the corpus in which users's queries are issued
					connection = new DocserviceConnection(docserviceUrlString,
							corpusId);
					
					// what happens if the thread is interrupted
					// by user calling the cancel button
					if (Thread.currentThread().isInterrupted()
							|| Thread.currentThread() != connectionThread) {
						buttonConnect.setEnabled(true);
						return;
					}

					if (Thread.currentThread().isInterrupted()
							|| Thread.currentThread() != connectionThread) {

						// if the trehad was interrupted or the connect was called when already another connection is established 
						if (connection != null)
							connection.cleanup();
						buttonConnect.setEnabled(true);
						return;
					}
					
					// finally establish the connection
					statusMessage.setText("Applying connection...");
					setConnectionSafely(connection);
					dispose();
					return;
				} catch (RichUIException e) {
					handleError(e);
					buttonConnect.setEnabled(true);
				} catch (Throwable e) {
					
					// if some error occured
					// we undo the bits we tried so far to connect to docservice
					if (connection != null) {
						try {
							connection.cleanup();
						} catch (RichUIException e1) {
						}
					}
					handleError(new RichUIException(
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
