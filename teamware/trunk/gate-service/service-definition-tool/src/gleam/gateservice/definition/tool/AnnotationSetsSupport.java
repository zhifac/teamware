/*
 *  AnnotationSetsSupport.java
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

import java.awt.event.ActionEvent;
import java.util.Collection;

import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JList;
import javax.swing.JOptionPane;

import gleam.gateservice.definition.GateServiceDefinition;
import gleam.gateservice.definition.ServiceDefinitionEvent;

/**
 * Class dealing with all setup and event handling for the "Annotation
 * sets" panel.
 */
public class AnnotationSetsSupport {

  private MainFrame mainFrame;

  private MainForm mainForm;

  private GateServiceDefinition serviceDefinition;

  private ApplicationInfo applicationInfo;

  /**
   * Set up the annotation sets panel in the GUI.
   */
  public AnnotationSetsSupport(MainFrame mainFrame, MainForm mainForm,
          GateServiceDefinition serviceDefinition,
          ApplicationInfo applicationInfo) {
    this.mainFrame = mainFrame;
    this.mainForm = mainForm;
    this.serviceDefinition = serviceDefinition;
    this.applicationInfo = applicationInfo;

    setup();
  }

  private void setup() {
    // Input and output annotation sets list models are backed by the
    // service definition, and update themselves in response to
    // events.
    AnnotationSetsListModel inputSetsModel = new AnnotationSetsListModel(
            serviceDefinition, serviceDefinition.getInputAnnotationSetNames(),
            ServiceDefinitionEvent.Direction.IN);
    mainForm.inputSetsList.setModel(inputSetsModel);
    AnnotationSetsListModel outputSetsModel = new AnnotationSetsListModel(
            serviceDefinition, serviceDefinition.getOutputAnnotationSetNames(),
            ServiceDefinitionEvent.Direction.OUT);
    mainForm.outputSetsList.setModel(outputSetsModel);

    // Custom cell renderer to properly display the default annotation
    // set (null name)
    mainForm.inputSetsList
            .setCellRenderer(new AnnotationSetsListCellRenderer());
    mainForm.outputSetsList
            .setCellRenderer(new AnnotationSetsListCellRenderer());

    // Disable remove buttons when there's nothing selected in the
    // corresponding list.
    mainForm.inputSetsList.getSelectionModel().addListSelectionListener(
            new EmptySelectionDisabler(mainForm.removeInputSetButton,
                    mainForm.inputSetsList.getSelectionModel()));
    mainForm.outputSetsList.getSelectionModel().addListSelectionListener(
            new EmptySelectionDisabler(mainForm.removeOutputSetButton,
                    mainForm.outputSetsList.getSelectionModel()));

    // button actions
    mainForm.addInputSetButton.addActionListener(new AddInputASAction(
            inputSetsModel));
    mainForm.removeInputSetButton.addActionListener(new RemoveInputASAction());

    mainForm.addOutputSetButton.addActionListener(new AddOutputASAction(
            outputSetsModel));
    mainForm.removeOutputSetButton
            .addActionListener(new RemoveOutputASAction());

  }

  /**
   * Class encapsulating common behaviour between input and output
   * annotation set add actions.
   */
  protected abstract class AddASAction extends AbstractAction {

    private AnnotationSetsListModel model;

    public AddASAction(String name, AnnotationSetsListModel model) {
      super(name);
      this.model = model;
    }

    public void actionPerformed(ActionEvent e) {
      // create the GUI
      AddAnnotationSetForm form = new AddAnnotationSetForm();
      AddAnnotationSetFormSupport.setup(form);

      // The "named set" field is freely editable, but we give
      // suggestions based on the values that have been used for
      // parameters called "inputASName" ("outputASName" as appropriate)
      // in the GAPP, as these are the most likely ones to use.
      form.asNameCombo.setModel(new DefaultComboBoxModel(getAvailableSetNames()
              .toArray()));

      int result = JOptionPane.showOptionDialog(mainFrame, new Object[] {
          "Please choose an annotation set to add", form},
              "Add Annotation Set", JOptionPane.OK_CANCEL_OPTION,
              JOptionPane.QUESTION_MESSAGE, null, null, null);

      if(result != JOptionPane.OK_OPTION) {
        // user cancelled
        return;
      }

      // default set is null name
      String asName = null;
      if(form.namedSetButton.isSelected()) {
        // if user chose "named set", use that instead
        asName = (String)form.asNameCombo.getSelectedItem();
      }

      while("".equals(asName) || model.containsAnnotationSet(asName)) {
        if("".equals(asName)) {
          // empty annotation set name - if you meant the default set
          // then use the radio button!
          result = JOptionPane.showOptionDialog(mainFrame, new Object[] {
              "Invalid annotation set name! (To choose the default",
              "set, use the \"Default annotation set\" button)",
              "Please choose an annotation set to add", form},
                  "Add Annotation Set", JOptionPane.OK_CANCEL_OPTION,
                  JOptionPane.WARNING_MESSAGE, null, null, null);
        }
        else {
          // name already taken
          result = JOptionPane.showOptionDialog(mainFrame, new Object[] {
              "This annotation set name has already been selected.",
              "Please choose an annotation set to add", form},
                  "Add Annotation Set", JOptionPane.OK_CANCEL_OPTION,
                  JOptionPane.WARNING_MESSAGE, null, null, null);
        }

        if(result != JOptionPane.OK_OPTION) {
          // user cancelled
          return;
        }

        // extract set name
        asName = null;
        if(form.namedSetButton.isSelected()) {
          asName = (String)form.asNameCombo.getSelectedItem();
        }
      }

      // we have a valid name, let subclass handle the actual addition
      doAdd(asName);
    }

    protected abstract Collection<String> getAvailableSetNames();

    protected abstract void doAdd(String asName);
  }

  /**
   * Action to add an input annotation set name to the definition.
   */
  public class AddInputASAction extends AddASAction {
    public AddInputASAction(AnnotationSetsListModel model) {
      super("Add input annotation set", model);
    }

    protected Collection<String> getAvailableSetNames() {
      return applicationInfo.getInputAnnotationSetNames();
    }

    protected void doAdd(String asName) {
      serviceDefinition.addInputAnnotationSet(asName);
    }
  }

  /**
   * Action to add an output annotation set name to the definition.
   */
  public class AddOutputASAction extends AddASAction {
    public AddOutputASAction(AnnotationSetsListModel model) {
      super("Add output annotation set", model);
    }

    protected Collection<String> getAvailableSetNames() {
      return applicationInfo.getOutputAnnotationSetNames();
    }

    protected void doAdd(String asName) {
      serviceDefinition.addOutputAnnotationSet(asName);
    }
  }

  /**
   * Class encapsulating common behaviour between input and output
   * annotation set remove actions.
   */
  protected abstract class RemoveASAction extends AbstractAction {
    protected JList list;

    public RemoveASAction(JList list) {
      this.list = list;
    }

    public void actionPerformed(ActionEvent e) {
      if(list.isSelectionEmpty()) {
        return;
      }
      String asName = (String)list.getSelectedValue();
      doRemove(asName);
    }

    protected abstract void doRemove(String asName);
  }

  /**
   * Action to remove an input annotation set name from the definition.
   */
  public class RemoveInputASAction extends RemoveASAction {
    public RemoveInputASAction() {
      super(mainForm.inputSetsList);
    }

    protected void doRemove(String asName) {
      serviceDefinition.removeInputAnnotationSet(asName);
    }
  }

  /**
   * Action to remove an input annotation set name from the definition.
   */
  public class RemoveOutputASAction extends RemoveASAction {
    public RemoveOutputASAction() {
      super(mainForm.outputSetsList);
    }

    protected void doRemove(String asName) {
      serviceDefinition.removeOutputAnnotationSet(asName);
    }
  }
}
