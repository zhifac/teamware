/*
 *  MainFrame.java
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
package gleam.gateservice.definition.tool;

import gate.persist.PersistenceException;
import gate.util.ExtensionFileFilter;
import gate.util.persistence.ServiceDefinitionToolGappParser;
import gleam.gateservice.definition.GateServiceDefinition;
import gleam.gateservice.definition.ServiceDefinitionEvent;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * The main frame for the service definition tool. Any number of these
 * frames can be open at the same time.
 */
public class MainFrame extends JFrame {
  public static final String WINDOW_TITLE = "SAFE GATE Service Definition Tool";

  public static File lastSelectionDir;

  /**
   * The main form making up the body of the window. The MainForm class
   * is auto-generated by Abeille Forms Designer.
   */
  private MainForm mainForm = null;

  /**
   * The location where the current service definition is saved. May be
   * null if we created a definition from scratch.
   */
  private File serviceDefinitionFile = null;

  /**
   * Save action (on the File menu).
   */
  private Action saveAction;

  /**
   * Save As action (on the file menu).
   */
  private Action saveAsAction;

  /**
   * The service definition being edited by this window.
   */
  private GateServiceDefinition serviceDefinition;

  /**
   * An object describing the GAPP that this service definition relates
   * to.
   */
  private ApplicationInfo applicationInfo;

  /**
   * Create a new main window, initially with no service definition
   * open.
   */
  public MainFrame() {
    super(WINDOW_TITLE);

    // set up menu bar
    JMenuBar menuBar = new JMenuBar();
    JMenu fileMenu = new JMenu("File");
    fileMenu.add(new NewFileAction());
    fileMenu.add(new OpenFileAction());
    fileMenu.addSeparator();
    saveAction = new SaveAction();
    fileMenu.add(saveAction);
    saveAsAction = new SaveAsAction();
    fileMenu.add(saveAsAction);
    menuBar.add(fileMenu);
    this.setJMenuBar(menuBar);

    // set size
    Dimension dim = new Dimension(640, 480);
    this.setMinimumSize(dim);
    this.setSize(dim);
    this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
  }

  /**
   * Set the file name and enable the "Save" action.
   * 
   * @param file
   */
  public void setFile(File file) {
    this.serviceDefinitionFile = file;
    if(file == null) {
      this.setTitle(WINDOW_TITLE);
      saveAction.setEnabled(false);
    }
    else {
      this.setTitle(file.getName() + " - " + WINDOW_TITLE);
      saveAction.setEnabled(true);
    }
  }

  /**
   * Configure this window for a given service definition. This creates
   * the main form and wires up all the event handlers.
   * 
   * @param def the service definition to edit.
   */
  void configureForDefinition(GateServiceDefinition def) {
    // We must have the corresponding GAPP file for this service
    // definition, so we know what PR parameters to offer, etc.
    JFileChooser gappChooser = new JFileChooser(lastSelectionDir);
    gappChooser.setDialogTitle("Select the application file for this "
            + "service");

    int result = gappChooser.showOpenDialog(this);
    if(result == JFileChooser.APPROVE_OPTION) {
      File gappFile = gappChooser.getSelectedFile();
      lastSelectionDir = gappChooser.getCurrentDirectory();
      try {
        // parse the GAPP file
        applicationInfo = ServiceDefinitionToolGappParser.parseGapp(gappFile
                .toURL());
      }
      catch(MalformedURLException mue) {
        JOptionPane.showMessageDialog(this, "Can't open GAPP file", "Error",
                JOptionPane.ERROR_MESSAGE);
        return;
      }
      catch(PersistenceException pe) {
        JOptionPane.showMessageDialog(this, "Error loading GAPP file", "Error",
                JOptionPane.ERROR_MESSAGE);
        return;
      }

      serviceDefinition = def;

      // create the GUI
      mainForm = new MainForm();

      // The various tables/lists/trees in the GUI have models backed by
      // the GateServiceDefinition, which update themselves based on
      // events fired by the definition.

      // application view
      ApplicationViewSupport avs = new ApplicationViewSupport(this, mainForm,
              serviceDefinition, applicationInfo);

      // service view
      ServiceViewSupport svs = new ServiceViewSupport(this, mainForm,
              serviceDefinition, applicationInfo);

      // annotation sets tab
      AnnotationSetsSupport ass = new AnnotationSetsSupport(this, mainForm,
              serviceDefinition, applicationInfo);

      this.getContentPane().add(mainForm);

      // We now have a definition to save, so enable the "save as"
      // option
      saveAsAction.setEnabled(true);

    } // end if(APPROVE_OPTION) - if user cancelled GAPP file selector,
    // do nothing.
  }

  /**
   * Open a service definition file in this window.
   * 
   * @param serviceDefinitionFile the file to open.
   */
  public void openFile(File serviceDefinitionFile) {
    // load the service definition
    GateServiceDefinition def = new GateServiceDefinition();
    Document serviceDefinitionDoc = null;
    try {
      SAXBuilder builder = new SAXBuilder();
      serviceDefinitionDoc = builder.build(serviceDefinitionFile);
      def.fromXml(serviceDefinitionDoc.getRootElement());
    }
    catch(JDOMException jde) {
      JOptionPane.showMessageDialog(this,
              "Error parsing service definition file", "Error",
              JOptionPane.ERROR_MESSAGE);
      jde.printStackTrace();
      return;
    }
    catch(IOException ioe) {
      JOptionPane.showMessageDialog(this,
              "I/O error loading service definition file", "Error",
              JOptionPane.ERROR_MESSAGE);
      ioe.printStackTrace();
      return;
    }
    catch(IllegalArgumentException iae) {
      JOptionPane.showMessageDialog(this,
              "Error processing service definition file", "Error",
              JOptionPane.ERROR_MESSAGE);
      iae.printStackTrace();
      return;
    }

    // set up the window for the newly loaded definition
    this.configureForDefinition(def);

    // and store the file location
    this.setFile(serviceDefinitionFile);
  }

  /**
   * Save the current service definition to the currently set file name.
   */
  public void saveFile() {
    XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
    Element serviceDefinitionElt = serviceDefinition.toXml();
    org.jdom.Document doc = new org.jdom.Document(serviceDefinitionElt);
    try {
      FileOutputStream fos = new FileOutputStream(serviceDefinitionFile);
      BufferedOutputStream out = new BufferedOutputStream(fos);
      outputter.output(doc, out);
    }
    catch(IOException e) {
      JOptionPane.showMessageDialog(this, new Object[] {
          "Error saving service definition to file:", serviceDefinitionFile},
              "Error", JOptionPane.ERROR_MESSAGE);
      e.printStackTrace();
    }
  }

  /**
   * Action to create a new, empty service definition for editing. If
   * the current window is in use, a new window is created for the new
   * definition.
   */
  public class NewFileAction extends AbstractAction {

    public NewFileAction() {
      super("New...");
    }

    public void actionPerformed(ActionEvent e) {
      MainFrame frameToUse = MainFrame.this;
      // create a new window if necessary
      if(frameToUse.mainForm != null) {
        frameToUse = new MainFrame();
      }

      // configure the window.
      frameToUse.configureForDefinition(new GateServiceDefinition());
      frameToUse.setVisible(true);
    }

  }

  /**
   * Action to open an existing definition for editing. Prompts the user
   * to select a file and then hands off to {@link MainFrame#openFile}.
   */
  public class OpenFileAction extends AbstractAction {

    public OpenFileAction() {
      super("Open...");
    }

    public void actionPerformed(ActionEvent e) {
      MainFrame frameToUse = MainFrame.this;
      if(frameToUse.mainForm != null) {
        frameToUse = new MainFrame();
      }

      JFileChooser chooser = new JFileChooser(lastSelectionDir);
      chooser.setDialogTitle("Select service definition file");
      int result = chooser.showOpenDialog(MainFrame.this);
      if(result == JFileChooser.APPROVE_OPTION) {
        File file = chooser.getSelectedFile();
        lastSelectionDir = chooser.getCurrentDirectory();
        frameToUse.openFile(file);
        frameToUse.setVisible(true);
      }
    }
  }

  /**
   * Action to save the current service definition to its original file
   * name.
   */
  public class SaveAction extends AbstractAction {
    public SaveAction() {
      super("Save");
      setEnabled(false);
    }

    public void actionPerformed(ActionEvent e) {
      saveFile();
    }
  }

  /**
   * Action to save the current service definition under a different
   * file name.
   */
  public class SaveAsAction extends AbstractAction {
    public SaveAsAction() {
      super("Save as...");
      setEnabled(false);
    }

    public void actionPerformed(ActionEvent e) {
      JFileChooser chooser = new JFileChooser(lastSelectionDir);
      chooser.setDialogTitle("Save As");
      ExtensionFileFilter filter = new ExtensionFileFilter();
      filter.addExtension("xml");
      chooser.setFileFilter(filter);
      int result = chooser.showSaveDialog(MainFrame.this);
      if(result == JFileChooser.APPROVE_OPTION) {
        File file = chooser.getSelectedFile();
        boolean proceed = true;
        // prompt for confirmation before overwriting
        if(file.exists()) {
          result = JOptionPane.showConfirmDialog(MainFrame.this,
                  "The selected file already exists, overwrite?",
                  "File exists", JOptionPane.OK_CANCEL_OPTION,
                  JOptionPane.WARNING_MESSAGE);
          proceed = (result == JOptionPane.OK_OPTION);
        }
        if(proceed) {
          setFile(file);
          saveFile();
        }
      }
    }
  }
}