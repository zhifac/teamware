/*
 *  AnnotationDiffGUI.java
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
package gleam.annotationdiffgui;

import gate.AnnotationSet;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.GateConstants;
import gate.LanguageResource;
import gate.creole.ResourceData;
import gate.creole.ResourceInstantiationException;
import gate.creole.ontology.Ontology;
import gate.gui.docview.AnnotationSetsView;
import gate.util.GateException;
import gleam.annotationdiffgui.actions.ActionConnectToAnnDiffGUI;
import gleam.annotationdiffgui.actions.ActionShowAnnDiffConnectDialog;
import gleam.annotationdiffgui.gui.LogFrame;
import gleam.annotationdiffgui.gui.MainFrame;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;

/**
 * <p>
 * Main class to start client application.
 * </p>
 *
 * <ul>
 * Annotation Diff GUI startup parameters:
 *
 * <li>Parameter name: <b>sitecfg</b> Value: URL string. Defines a location of
 * GATE's site configuration file. (mandatory)</li>
 *
 * <li>Parameter name: <b>autoconnect</b> Value: "true" or "false".</li>
 * 
 * <li>Parameter name: <b>debug</b> Value: "true" or "false"</li>
 *
 * <li>Parameter name: <b>doc-id</b> Value: persistent ID of the document in
 * document service.</li>
 *
 * <li>Parameter name: <b>docservice-url</b> Value: URL string. Defines
 * location of document service.</li>
 *
 * </ul>
 *
 * @author Thomas Heitz
 */
public class AnnotationDiffGUI implements Constants {
  /** Debug flag */
  private static boolean DEBUG = true;
  private static Connection connection;
  private static List listeners = new ArrayList();
  private static Properties properties = new Properties();
  private static XMLInputFactory xmlInputFactory =
    XMLInputFactory.newInstance();
  private static XMLOutputFactory xmlOutputFactory =
    XMLOutputFactory.newInstance();
  private static volatile Thread connectionThread;

  /**
   * Starts the client application.
   */
  public static void main(String[] args) {
    // temporary dirty hack and example of bad design
    LogFrame.getInstance();
    // set up properties according to parameters passed
    for(int i = 0; i < args.length; i++) {
      String arg = args[i];
      // ===
      if(arg.trim().startsWith(DEBUG_PARAMETER_NAME + "=")) {
        properties.put(DEBUG_PARAMETER_NAME, arg.trim().substring(
          DEBUG_PARAMETER_NAME.length() + 1).trim());
        if(properties.getProperty(DEBUG_PARAMETER_NAME).toLowerCase().equals(
          DEBUG_TRUE)) {
          DEBUG = true;
        }
      } else if (arg.trim().startsWith(AUTOCONNECT_PARAMETER_NAME + "=")) {
		properties.put(AUTOCONNECT_PARAMETER_NAME, arg.trim()
				.substring(AUTOCONNECT_PARAMETER_NAME.length() + 1)
				.trim());
      } else if(arg.trim().startsWith(SITE_CONFIG_URL_PARAMETER_NAME + "=")) {
        properties.put(SITE_CONFIG_URL_PARAMETER_NAME, arg.trim().substring(
          SITE_CONFIG_URL_PARAMETER_NAME.length() + 1).trim());
      // ===
      } else if(arg.trim().startsWith(DOCSERVICE_URL_PARAMETER_NAME + "=")) {
        properties.put(DOCSERVICE_URL_PARAMETER_NAME, arg.trim().substring(
          DOCSERVICE_URL_PARAMETER_NAME.length() + 1).trim());
      } else if(arg.trim().startsWith(DOC_ID_PARAMETER_NAME + "=")) {
        properties.put(DOC_ID_PARAMETER_NAME, arg.trim().substring(
          DOC_ID_PARAMETER_NAME.length() + 1).trim());
      }
      // ===
      else {
        System.out.println("WARNING! Unknown or undefined parameter: '"
          + arg.trim() + "'");
      }
    }

    System.out.println("Annotation Diff GUI startup parameters:");
    System.out.println("------------------------------");
    for(Object propName : properties.keySet()) {
      System.out.println(propName.toString() + "="
        + properties.getProperty((String)propName));
    }
    System.out.println("------------------------------");

    if(properties.getProperty(SITE_CONFIG_URL_PARAMETER_NAME) == null
      || properties.getProperty(SITE_CONFIG_URL_PARAMETER_NAME).length() == 0) {
      String err =
        "Mandatory parameter '" + SITE_CONFIG_URL_PARAMETER_NAME
          + "' is missing.\n\nApplication will exit.";
      System.out.println(err);
      JOptionPane.showMessageDialog(new JFrame(), err, "Error!",
        JOptionPane.ERROR_MESSAGE);
      System.exit(-1);
    }

    try {
      String context= System.getProperty(CONTEXT);
      if(context==null || "".equals(context)){
    	  context=DEFAULT_CONTEXT;
      }

      String s = System.getProperty(GateConstants.GATE_HOME_PROPERTY_NAME);
      if(s == null || s.length() == 0) {
        File f = File.createTempFile("foo", "");
        String gateHome = f.getParent().toString() + context;
        f.delete();
        System.setProperty(GateConstants.GATE_HOME_PROPERTY_NAME, gateHome);
        f = new File(System.getProperty(GateConstants.GATE_HOME_PROPERTY_NAME));
        if(!f.exists()) {
          f.mkdirs();
        }
      }

      s = System.getProperty(GateConstants.PLUGINS_HOME_PROPERTY_NAME);
      if(s == null || s.length() == 0) {
        System.setProperty(GateConstants.PLUGINS_HOME_PROPERTY_NAME, System
          .getProperty(GateConstants.GATE_HOME_PROPERTY_NAME)
          + "/plugins");
        File f =
          new File(System.getProperty(GateConstants.PLUGINS_HOME_PROPERTY_NAME));
        if(!f.exists()) {
          f.mkdirs();
        }
      }

      s = System.getProperty(GateConstants.GATE_SITE_CONFIG_PROPERTY_NAME);
      if(s == null || s.length() == 0) {
        System.setProperty(GateConstants.GATE_SITE_CONFIG_PROPERTY_NAME, System
          .getProperty(GateConstants.GATE_HOME_PROPERTY_NAME)
          + "/gate.xml");
      }
      if(properties.getProperty(SITE_CONFIG_URL_PARAMETER_NAME) != null
        && properties.getProperty(SITE_CONFIG_URL_PARAMETER_NAME).length() > 0) {
        File f =
          new File(System
            .getProperty(GateConstants.GATE_SITE_CONFIG_PROPERTY_NAME));
        if(f.exists()) {
          f.delete();
        }
        f.getParentFile().mkdirs();
        f.createNewFile();
        URL url =
          new URL(properties.getProperty(SITE_CONFIG_URL_PARAMETER_NAME));
        InputStream is = url.openStream();
        FileOutputStream fos = new FileOutputStream(f);
        int i = is.read();
        while(i != -1) {
          fos.write(i);
          i = is.read();
        }
        fos.close();
        is.close();
      }

      // read a teamware-specific user config rather than the same one GATE Developer uses
      File userConfig = new File(System.getProperty("user.home"), Gate.runningOnUnix() ? ".gate-teamware.xml" : "gate-teamware.xml");
      Gate.setUserConfigFile(userConfig);
      // Initialize the framework Gate
      try {
        Gate.init();
        // apply font and L&F preferences from user config
        gate.Main.applyUserPreferences();
      } catch(Exception e) {
        e.printStackTrace();
      }

	} catch (Throwable e) {
		e.printStackTrace();
	}

	  MainFrame.getInstance().setVisible(true);
    MainFrame.getInstance().pack();

      // direct mode
      if(properties.getProperty(AUTOCONNECT_PARAMETER_NAME, "").toLowerCase()
        .equals(AUTOCONNECT_TRUE)) {
        if(properties.getProperty(DOC_ID_PARAMETER_NAME) == null
          || properties.getProperty(DOC_ID_PARAMETER_NAME).length() == 0) {

        	String err = "Can't autoconnect. A parameter '" + DOC_ID_PARAMETER_NAME
              		   + "' is missing.";
          System.out.println(err);
          JOptionPane.showMessageDialog(new JFrame(), err, "Error!",
            JOptionPane.ERROR_MESSAGE);
          ActionShowAnnDiffConnectDialog.getInstance().actionPerformed(null);

        } else {
          ActionConnectToAnnDiffGUI.getInstance().actionPerformed(null);
        }
      } else {
        ActionShowAnnDiffConnectDialog.getInstance().actionPerformed(null);
      }
  }

  public static Connection getConnection() {
    return connection;
  }

  public static Properties getProperties() {
    return properties;
  }

  public static XMLInputFactory getXmlInputFactory() {
    return xmlInputFactory;
  }

  public static XMLOutputFactory getXmlOutputFactory() {
    return xmlOutputFactory;
  }

  public static void setConnection(Connection newConnection)
    throws AnnotationDiffGUIException {
    System.out.println("AGUI set connection "+newConnection.toString());
	if(AnnotationDiffGUI.getConnection() != null) {
      closeConnection();
    }
    AnnotationDiffGUI.connection = newConnection;
    System.out.println("Annotation Diff GUI status of connection "+getConnectionStatus());
    MainFrame.getInstance().updateAllStatuses();
    fireConnectionChanged();
  }

  /**
   * Returns text representation for connection status of client application.
   *
   * @return text representation of the application connection status
   */
  public static String getConnectionStatus() {
    if(connection != null) {
      return connection.getConnectionStatus();
    } else {
      return "Not connected";
    }
  }

  /**
   * Returns URL of the resource with a given name.*
   * @param resourceName
   *          name
   * @return URL of the resorce with a given name
   */
  public static URL getResourceURL(String resourceName) {
    return AnnotationDiffGUI.class.getResource("resource/" + resourceName);
  }

  /**
   * Returns path to the application resources.
   *
   * @return path to the application resources
   */
  public static String getResourcePath() {
    return AnnotationDiffGUI.class.getResource("resource").toString();
  }

  public static ImageIcon createIcon(String imageName) {
    URL imageURL = getResourceURL(imageName);
    if(imageURL == null) {
      System.err.println("Resource not found: " + imageName);
      return null;
    } else {
      return new ImageIcon(imageURL);
    }
  }

  /**
   * Adds given listener to the list of listeners if this listener doesn't
   * exists.
   *
   * @param lsnr
   *          listener to add
   */
  public static boolean addAnnotationDiffGUIListener(AnnotationDiffGUIListener lsnr) {
    if(listeners.contains(lsnr)) return false;
    listeners.add(lsnr);
    return true;
  }

  /**
   * Removes given listener to the list of listeners if this listener doesn't
   * exists.
   *
   * @param lsnr
   *          listener to remove
   */
  public static boolean removeAnnotationDiffGUIListener(AnnotationDiffGUIListener lsnr) {
    if(listeners.contains(lsnr)) {
      listeners.remove(lsnr);
      return true;
    }
    return false;
  }

  public static boolean closeConnection() throws AnnotationDiffGUIException {
    if(connection == null) return true;
      connection.cleanup();
      connection = null;
      return true;
  }

  public static boolean isDebug() {
    return DEBUG;
  }

  private static void fireConnectionChanged() {
    if(listeners.size() == 0) return;
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        Object[] lsnrs = listeners.toArray();
        for(int i = 0; i < lsnrs.length; i++) {
          AnnotationDiffGUIListener l = (AnnotationDiffGUIListener)lsnrs[i];
          l.connectionChanged(connection);
        }
      }
    });
  }

}
