/*
 *  ServiceViewSupport.java
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

import gleam.gateservice.definition.GateServiceDefinition;
import gleam.gateservice.definition.GateServiceDefinition.ParameterMapping;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

/**
 * Class dealing with all setup and event handling for the "Service
 * view" panel.
 */
public class ServiceViewSupport {

  private MainFrame mainFrame;

  private MainForm mainForm;

  private GateServiceDefinition serviceDefinition;

  private ApplicationInfo applicationInfo;

  private GasParametersTableModel gasParametersTableModel;

  /**
   * Set up the "Service view" panel of the GUI.
   */
  public ServiceViewSupport(MainFrame mainFrame, MainForm mainForm,
          GateServiceDefinition serviceDefinition,
          ApplicationInfo applicationInfo) {
    this.mainFrame = mainFrame;
    this.mainForm = mainForm;
    this.serviceDefinition = serviceDefinition;
    this.applicationInfo = applicationInfo;

    setup();
  }

  private void setup() {
    gasParametersTableModel = new GasParametersTableModel(serviceDefinition);
    mainForm.gasParametersTable.setModel(gasParametersTableModel);
    // shrink the "Required" column
    mainForm.gasParametersTable.getColumnModel().getColumn(0)
            .setPreferredWidth(80);
    mainForm.gasParametersTable.getColumnModel().getColumn(0).setMaxWidth(100);

    // The "PR parameters" and "Document features" lists
    ParamMappingsListModel paramMappingsModel = new ParamMappingsListModel(
            serviceDefinition, mainForm, mainForm.paramMappingsList
                    .getSelectionModel());
    mainForm.paramMappingsList.setModel(paramMappingsModel);

    FeatureMappingsListModel featureMappingsModel = new FeatureMappingsListModel(
            serviceDefinition, mainForm, mainForm.featureMappingsList
                    .getSelectionModel());
    mainForm.featureMappingsList.setModel(featureMappingsModel);

    // Render the items in the parameter mappings list nicely
    mainForm.paramMappingsList
            .setCellRenderer(new ParameterMappingCellRenderer());

    ListSelectionModel gasParamsTableSelectionModel = mainForm.gasParametersTable
            .getSelectionModel();

    // disable the remove GaS parameter button when nothing is
    // selected
    gasParamsTableSelectionModel
            .addListSelectionListener(new EmptySelectionDisabler(
                    mainForm.removeGasParameterButton,
                    gasParamsTableSelectionModel));
    // disable the add buttons for param and feature mappings when
    // there's nothing selected in the GaS params table
    gasParamsTableSelectionModel
            .addListSelectionListener(new EmptySelectionDisabler(
                    mainForm.addParamMappingButton,
                    gasParamsTableSelectionModel));
    gasParamsTableSelectionModel
            .addListSelectionListener(new EmptySelectionDisabler(
                    mainForm.addFeatureMappingButton,
                    gasParamsTableSelectionModel));

    // disable the remove buttons for param mapping and feature
    // mapping when there's nothing selected in the corresponding list
    mainForm.paramMappingsList.getSelectionModel().addListSelectionListener(
            new EmptySelectionDisabler(mainForm.removeParamMappingButton,
                    mainForm.paramMappingsList.getSelectionModel()));
    mainForm.featureMappingsList.getSelectionModel().addListSelectionListener(
            new EmptySelectionDisabler(mainForm.removeFeatureMappingButton,
                    mainForm.featureMappingsList.getSelectionModel()));

    // Button actions
    mainForm.addGasParameterButton
            .addActionListener(new AddGasParameterAction());
    mainForm.removeGasParameterButton
            .addActionListener(new RemoveGasParameterAction());

    mainForm.addParamMappingButton
            .addActionListener(new AddParamMappingAction());
    mainForm.removeParamMappingButton
            .addActionListener(new RemoveParamMappingAction());

    mainForm.addFeatureMappingButton
            .addActionListener(new AddFeatureMappingAction());
    mainForm.removeFeatureMappingButton
            .addActionListener(new RemoveFeatureMappingAction());

  }

  /**
   * Action to add a new GaS parameter to the service definition.
   */
  public class AddGasParameterAction extends AbstractAction {

    public AddGasParameterAction() {
      super("Add GaS Parameter");
    }

    public void actionPerformed(ActionEvent e) {
      String paramName = JOptionPane.showInputDialog(mainFrame,
              "Please specify a name for the new parameter",
              "Add GaS Parameter", JOptionPane.QUESTION_MESSAGE);

      while(paramName == null || "".equals(paramName)
              || gasParametersTableModel.containsParameter(paramName)) {
        if(paramName == null) {
          // user cancelled
          return;
        }
        else if("".equals(paramName)) {
          // empty string input
          paramName = JOptionPane.showInputDialog(mainFrame,
                  "Please specify a name for the new parameter",
                  "Add GaS Parameter", JOptionPane.WARNING_MESSAGE);
        }
        else {
          // name already taken
          paramName = (String)JOptionPane.showInputDialog(mainFrame,
                  new String[] {"A parameter with this name already exists.",
                      "Please choose another name."}, "Add GaS Parameter",
                  JOptionPane.WARNING_MESSAGE, null, null, paramName);
        }
      }

      // add the parameter - this fires events that will update the
      // table
      serviceDefinition.addParameter(paramName);
    }
  }

  /**
   * Action to remove the currently selected GaS parameter from the
   * service definition.
   */
  public class RemoveGasParameterAction extends AbstractAction {

    public RemoveGasParameterAction() {
      super("Remove GaS Parameter");
    }

    public void actionPerformed(ActionEvent e) {
      if(mainForm.gasParametersTable.getSelectedRow() == -1) {
        // nothing selected, so ignore
        return;
      }
      else {
        String selectedParam = (String)mainForm.gasParametersTable.getValueAt(
                mainForm.gasParametersTable.getSelectedRow(), 1);
        serviceDefinition.removeParameter(selectedParam);
      }
    }
  }

  /**
   * Action to add a new feature mapping for the current GaS parameter
   * to the service definition.
   */
  public class AddParamMappingAction extends AbstractAction {

    public AddParamMappingAction() {
      super("Add parameter mapping");
    }

    public void actionPerformed(ActionEvent e) {
      if(mainForm.gasParametersTable.getSelectedRow() == -1) {
        // nothing selected, so ignore
        return;
      }
      else {
        // extract the selected GaS parameter and current set of
        // mappings
        String selectedParam = (String)mainForm.gasParametersTable.getValueAt(
                mainForm.gasParametersTable.getSelectedRow(), 1);
        List<ParameterMapping> paramMappings = serviceDefinition
                .getParameterMappings(selectedParam);

        // set up tree of available PRs and parameters
        AddParamMappingForm form = new AddParamMappingForm();
        DefaultMutableTreeNode root = applicationInfo.getParamsTreeRoot();
        form.tree.setModel(new DefaultTreeModel(root));
        form.tree.setCellRenderer(new ApplicationInfoTreeCellRenderer());
        form.tree.setRootVisible(false);
        form.tree.setShowsRootHandles(true);

        int result = JOptionPane.showOptionDialog(mainFrame, new Object[] {
            "Please select the PR runtime parameter that",
            "this GaS parameter should map to", form}, "Add Parameter Mapping",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, null, null);

        if(result != JOptionPane.OK_OPTION) {
          // user cancelled
          return;
        }

        TreePath selectedPath = null;
        String prName = null;
        String prParam = null;

        boolean validSelection = false;
        while(!validSelection) {
          selectedPath = form.tree.getSelectionPath();
          if(selectedPath != null && selectedPath.getPathCount() == 3) {
            // extract PR name and param from selected node
            DefaultMutableTreeNode prNameNode = (DefaultMutableTreeNode)selectedPath
                    .getPathComponent(1);
            prName = ((PRInfo)prNameNode.getUserObject()).getName();
            prParam = ((DefaultMutableTreeNode)selectedPath.getPathComponent(2))
                    .toString();

            validSelection = (serviceDefinition
                    .getReverseParameterMappingsForPR(prName).get(prParam) == null);
          }
          if(!validSelection) {
            if(selectedPath == null) {
              // nothing selected
              result = JOptionPane.showOptionDialog(mainFrame, new Object[] {
                  "Please select the PR runtime parameter that",
                  "this GaS parameter should map to", form},
                      "Add Parameter Mapping", JOptionPane.OK_CANCEL_OPTION,
                      JOptionPane.WARNING_MESSAGE, null, null, null);
            }
            else if(selectedPath.getPathCount() != 3) {
              // selected a PR rather than a parameter
              result = JOptionPane.showOptionDialog(mainFrame, new Object[] {
                  "Please select a runtime parameter", form},
                      "Add Parameter Mapping", JOptionPane.OK_CANCEL_OPTION,
                      JOptionPane.WARNING_MESSAGE, null, null, null);
            }
            else {
              // already a mapping for the selected parameter
              result = JOptionPane.showOptionDialog(mainFrame, new Object[] {
                  "This PR parameter is already mapped to a",
                  "GaS parameter.  Please select another", form},
                      "Add Parameter Mapping", JOptionPane.OK_CANCEL_OPTION,
                      JOptionPane.WARNING_MESSAGE, null, null, null);
            }

            if(result != JOptionPane.OK_OPTION) {
              // user cancelled
              return;
            }
          }
        }

        // add the mapping - this fires events that will update the
        // table
        serviceDefinition.addParameterMapping(selectedParam, prName, prParam);
      }
    }
  }

  /**
   * Action to remove the currently selected parameter mapping from the
   * service definition.
   */
  public class RemoveParamMappingAction extends AbstractAction {

    public RemoveParamMappingAction() {
      super("Remove parameter mapping");
    }

    public void actionPerformed(ActionEvent e) {
      if(mainForm.gasParametersTable.getSelectedRow() == -1
              || mainForm.paramMappingsList.getSelectedIndex() == -1) {
        // nothing selected, so ignore
        return;
      }
      else {
        String selectedParam = (String)mainForm.gasParametersTable.getValueAt(
                mainForm.gasParametersTable.getSelectedRow(), 1);
        ParameterMapping selectedMapping = (ParameterMapping)mainForm.paramMappingsList
                .getSelectedValue();
        serviceDefinition.removeParameterMapping(selectedParam, selectedMapping
                .getPrName(), selectedMapping.getParamName());
      }
    }
  }

  /**
   * Action to add a new feature mapping for the current GaS parameter
   * to the service definition.
   */
  public class AddFeatureMappingAction extends AbstractAction {

    public AddFeatureMappingAction() {
      super("Add feature mapping");
    }

    public void actionPerformed(ActionEvent e) {
      if(mainForm.gasParametersTable.getSelectedRow() == -1) {
        // nothing selected, so ignore
        return;
      }
      else {
        // extract the selected GaS parameter and current set of
        // mappings
        String selectedParam = (String)mainForm.gasParametersTable.getValueAt(
                mainForm.gasParametersTable.getSelectedRow(), 1);
        List<String> featureMappings = serviceDefinition
                .getFeatureMappings(selectedParam);

        String featureName = JOptionPane.showInputDialog(mainFrame,
                "Please specify the feature name that this parameter "
                        + "should map to", "Add Feature Mapping",
                JOptionPane.QUESTION_MESSAGE);

        while(featureName == null || "".equals(featureName)
                || featureMappings.contains(featureName)) {
          if(featureName == null) {
            // user cancelled
            return;
          }
          else if("".equals(featureName)) {
            // empty string input - not allowed
            featureName = JOptionPane.showInputDialog(mainFrame,
                    "Please specify the feature name that this parameter "
                            + "should map to", "Add Feature Mapping",
                    JOptionPane.WARNING_MESSAGE);
          }
          else {
            // name already taken
            featureName = (String)JOptionPane.showInputDialog(mainFrame,
                    new String[] {"A mapping to this feature already exists.",
                        "Please choose another feature name."},
                    "Add Feature Mapping", JOptionPane.WARNING_MESSAGE, null,
                    null, featureName);
          }
        }

        // add the mapping - this fires events that will update the
        // table
        serviceDefinition.addFeatureMapping(selectedParam, featureName);
      }
    }
  }

  /**
   * Action to remove the currently selected feature mapping from the
   * service definition.
   */
  public class RemoveFeatureMappingAction extends AbstractAction {

    public RemoveFeatureMappingAction() {
      super("Remove feature mapping");
    }

    public void actionPerformed(ActionEvent e) {
      if(mainForm.gasParametersTable.getSelectedRow() == -1
              || mainForm.featureMappingsList.getSelectedIndex() == -1) {
        // nothing selected, so ignore
        return;
      }
      else {
        String selectedParam = (String)mainForm.gasParametersTable.getValueAt(
                mainForm.gasParametersTable.getSelectedRow(), 1);
        String selectedItem = (String)mainForm.featureMappingsList
                .getSelectedValue();
        serviceDefinition.removeFeatureMapping(selectedParam, selectedItem);
      }
    }
  }
}
