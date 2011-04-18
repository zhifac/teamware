/*
 *  MainFrame. *
 *  Copyright (c) 2006-2006, The University of Sheffield.
 *
 *  Niraj Aswani
 */
package gleam.annic.gui;

import gate.creole.annic.Searcher;
import gleam.annic.AnnicGUI;
import gleam.annic.AnnicGUIExeption;
import gleam.annic.AnnicGUIListener;
import gleam.annic.Connection;
import gleam.annic.Constants;
import gleam.annic.DocserviceConnection;
import gleam.annic.SearcherImpl;
import gleam.annic.actions.ActionEditSettings;
import gleam.annic.actions.ActionShowAnnicConnectDialog;
import gleam.annic.actions.ActionShowHelp;
import gleam.annic.actions.ActionShowLog;
import gleam.annic.gui.LogFrame;
import gate.gui.LuceneDataStoreSearchGUI;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Main window of the Annic GUI. <br>
 * It's a singleton, use {@link #getInstance()} to get it. <br>
 * 
 * @author Niraj Aswani
 */
public class MainFrame extends JFrame implements Constants {
	/** Internal reference to the instance of main window. */
	private static MainFrame ourInstance;

	/**
	 * Toolbar to hold various buttons
	 */
	private JToolBar toolBar;

	/**
	 * Instance of the Lucene DataStore Search GUI
	 */
	private LuceneDataStoreSearchGUI searchGUI;

	/**
	 * Container for the lucene datastore search GUI.
	 */
	private JTabbedPane tabbedPane;
	// the tabbed pane is really not needed, however, we use it if in future we want to add alternate gui
	// or may be something that lists the parameters or show the exported patterns or so on.

	/**
	 * Status label
	 */
	private JLabel statusLabel;

	/**
	 * Annic searcher implementation
	 * 
	 */
	private Searcher annicSearcher;

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
		
		// log frame, where all messages are being displayed
		LogFrame.getInstance();
		JFrame.setDefaultLookAndFeelDecorated(true);
		ImageIcon icon = AnnicGUI.createIcon("gleam-32.gif");
		if (icon != null)
			this.setIconImage(icon.getImage());
		
		// the title message for the annic gui
		setTitleStatus(AnnicGUI.getConnectionStatus());

		// we set the gui size to 800,600 to being with
		// user can certainly change this once the window is appearing on the screen
		this.setSize(800, 600);
		
		// however the preferred size is 800 X 600
		this.setPreferredSize(new Dimension(800, 600));
		
		// finally we show it in the middle of the screen
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation((d.width - this.getSize().width) / 2, (d.height - this
				.getSize().height) / 2);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		// what happens when users clicks on the close button
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent arg0) {
				try {
					// we try to close the connection
					// if successfull, we dispose the window
					// or else, an error message will be shown
					if (AnnicGUI.closeConnection())
						dispose();
				} catch (AnnicGUIExeption ex) {
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
		
		// we choose border layout to place our components
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(getToolBar(), BorderLayout.NORTH);
		getStatusLabel().setBorder(
				BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
		this.getContentPane().add(getStatusLabel(), BorderLayout.SOUTH);
		
		// enabling all buttons in the toolbar
		updateAllActions();

		// the mainFrameConnectionListener is the once that once the
		// connection is establised, obtains necessary information from the
		// lucenedatastore and populates the annic gui
		// it listens to the connectionChanged events
		AnnicGUI.addAnnicGUIListener(new MainFrameConnectionListener());

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
	 * Updates the title with the connection status
	 */
	public void updateTitleStatus() {
		this.setTitle(APP_TITLE + " [" + AnnicGUI.getConnectionStatus() + "]");
	}

	/**
	 * Updates the label being displayed in the bottom of the window
	 */
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
		this.setTitle(APP_TITLE + " [" + AnnicGUI.getConnectionStatus() + "]");
		updateBottomStatus();
	}

	public void updateAllActions() {
		
		//enabling the connect to annic button
		gleam.annic.actions.ActionShowAnnicConnectDialog.getInstance()
				.setEnabled(true);
		
		// similarly other buttons in the toolbar
		ActionShowHelp.getInstance().setEnabled(true);
		ActionShowLog.getInstance().setEnabled(true);
		ActionEditSettings.getInstance().setEnabled(true);
	}

	/**
	 * Returns tool bar for basic user operations. New one will be created if
	 * needed.
	 */
	public JToolBar getToolBar() {
		if (null != toolBar)
			return toolBar;
		
		// button to connect to doc-service
		// temporarily not being shown
		JButton buttonConnectToDS = new JButton(
				ActionShowAnnicConnectDialog.getInstance());
		buttonConnectToDS.setText("");
		buttonConnectToDS.setToolTipText("Connect to ANNIC");

		// button to show help
		JButton buttonHelp = new JButton(ActionShowHelp.getInstance());
		buttonHelp.setText("");
		buttonHelp.setToolTipText("Help");

		// button to show application log
		JButton buttonShowLog = new JButton(ActionShowLog.getInstance());
		buttonShowLog.setText("");
		buttonShowLog.setToolTipText("Show Application Log");

		// button to allow users editing their preferences for the gui
		JButton buttonEditSettings = new JButton(ActionEditSettings
				.getInstance());
		buttonEditSettings.setText("");
		buttonEditSettings.setToolTipText("Edit settings");

		// and we enable all the buttons
		updateAllActions();

		toolBar = new JToolBar(JToolBar.HORIZONTAL);
		toolBar.setFloatable(false);
		// we don't want this
		//toolBar.add(buttonConnectToDS);
		//toolBar.addSeparator();
		toolBar.add(buttonShowLog);
		toolBar.add(buttonHelp);
		toolBar.add(buttonEditSettings);
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
		Connection c = AnnicGUI.getConnection();
		if (c == null) {
			return "Not connected.";
		} else if (c instanceof DocserviceConnection) {
			return "ANNIC Connection ";
		} else {
			return "Internal error: unknown type of connection.";
		}
	}

	private String getStatusLabelToolTipText() {
		Connection c = AnnicGUI.getConnection();
		if (c == null) {
			return "Not connected.";
		} else if (c instanceof DocserviceConnection) {
			return "ANNIC Connection ";
		} else {
			return "";
		}
	}

	/**
	 * Listener that listens to the connectionChanged event.
	 * This method loads the annic GUI interface, puts it in the tabbed pane
	 * where user can issue their queries and see results.
	 * @author niraj
	 *
	 */
	class MainFrameConnectionListener implements AnnicGUIListener {
		public void connectionChanged(Connection newConnection) {
			
			// we enabled all controls (e.g. buttons in tool bar)
			MainFrame.getInstance().updateAllActions();
			
			// change various status messages
			MainFrame.getInstance().updateAllStatuses();
			
			// we proceed only if it is a DocserviceConnection
			if (newConnection instanceof DocserviceConnection) {
				// if searchGUI is not null, we need to start from the begining
				// so we remove all components from the taabbedPane
				if (searchGUI != null)
					tabbedPane.removeAll();

				/// we use this gui from the gate code
				searchGUI = new LuceneDataStoreSearchGUI();
				
				// we know the connection is of type docservice
				DocserviceConnection dc = (DocserviceConnection) newConnection;
				
				// SearcherImpl is an another implementation of the Searcher interface
				// from the GATE.  This is the heart of the GUI.
				// The searcher objects talks to the doc-service and provides various
				// methods which are used by the searchGUI to populated data into it.
				// we provide the nexessary parameters, e.g. a proxy object obtained from 
				// a docservice and the corpus ID in which we want to search patterns
				annicSearcher = new SearcherImpl(dc.getDocServiceProxy(), dc.getCorpusId());
				
				// this is how a typical GATE VR starts
				// you init it
				searchGUI.init();
				
				// then you set a target
				// in this case the searcher object is expected from the LuceneDataStoreGUI
				searchGUI.setTarget(annicSearcher);
				
				// as the window size is 800 x 600, we set the same size for searchGUI
				searchGUI.setSize(800, 600);
				searchGUI.setPreferredSize(new Dimension(800, 600));

				tabbedPane.add("Annic GUI", searchGUI);
				searchGUI.setVisible(false);
				searchGUI.update(searchGUI.getGraphics());
				searchGUI.setVisible(true);
				searchGUI.update(searchGUI.getGraphics());
			}
		}
	}
}
