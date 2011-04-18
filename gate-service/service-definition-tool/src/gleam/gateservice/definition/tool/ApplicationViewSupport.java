package gleam.gateservice.definition.tool;

import gleam.gateservice.definition.GateServiceDefinition;
import gleam.gateservice.definition.ServiceDefinitionEvent;
import gleam.gateservice.definition.ServiceDefinitionListener;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

/**
 * Class dealing with all setup and event handling for the "Application
 * view" panel.
 */
public class ApplicationViewSupport implements ServiceDefinitionListener,
                                   TreeSelectionListener {
  private MainFrame mainFrame;

  private MainForm mainForm;

  private GateServiceDefinition serviceDefinition;

  private ApplicationInfo applicationInfo;

  /**
   * A map from PR names (received in ServiceDefinitionEvents) to the
   * nodes that represent them in the tree.
   */
  private Map<String, DefaultMutableTreeNode> nodesByPRName;

  /**
   * A map from PR nodes in the tree to their reverse parameter
   * mappings.
   */
  private Map<DefaultMutableTreeNode, Map<String, String>> reverseParameterMappings;

  /**
   * Model for the tree view.
   */
  private DefaultTreeModel applicationTreeModel;

  /**
   * Set up the "Application view" panel of the GUI.
   */
  public ApplicationViewSupport(MainFrame mainFrame, MainForm mainForm,
          GateServiceDefinition serviceDefinition,
          ApplicationInfo applicationInfo) {
    this.mainFrame = mainFrame;
    this.mainForm = mainForm;
    this.serviceDefinition = serviceDefinition;
    this.applicationInfo = applicationInfo;

    setup();
  }

  private void setup() {
    // we listen directly for update events
    serviceDefinition.addServiceDefinitionListener(this);

    // get the tree from the application info object
    DefaultMutableTreeNode applicationTreeRoot = applicationInfo
            .getParamsTreeRoot();

    // build the maps used by the event handlers
    reverseParameterMappings = new HashMap<DefaultMutableTreeNode, Map<String, String>>();
    nodesByPRName = new HashMap<String, DefaultMutableTreeNode>();
    Enumeration<DefaultMutableTreeNode> prNodes = applicationTreeRoot
            .children();
    while(prNodes.hasMoreElements()) {
      DefaultMutableTreeNode n = prNodes.nextElement();
      PRInfo prInfo = (PRInfo)n.getUserObject();
      nodesByPRName.put(prInfo.getName(), n);
      reverseParameterMappings.put(n, serviceDefinition
              .getReverseParameterMappingsForPR(prInfo.getName()));
      // NB getReverseParameterMappings returns a live map, so we can
      // just keep querying a single copy, don't have to get it each
      // time.
    }

    applicationTreeModel = new DefaultTreeModel(applicationTreeRoot);

    mainForm.applicationTree.setModel(applicationTreeModel);
    mainForm.applicationTree.setShowsRootHandles(true);
    mainForm.applicationTree.setRootVisible(false);

    // special cell renderer that shows the GaS parameter that each PR
    // parameter is mapped to
    mainForm.applicationTree.setCellRenderer(new ApplicationTreeRenderer());

    // listen for selection changes so we can update the buttons
    mainForm.applicationTree.getSelectionModel().addTreeSelectionListener(this);

    // button actions
    mainForm.attachParamButton.addActionListener(new AttachParamAction());
    mainForm.detachParamButton.addActionListener(new DetachParamAction());
  }

  /**
   * Called by the service definition when a parameter mapping has been
   * added.
   */
  public void parameterMappingAdded(ServiceDefinitionEvent e) {
    updateTreeForEvent(e);
  }

  /**
   * Called by the service definition when a parameter mapping has been
   * removed.
   */
  public void parameterMappingRemoved(ServiceDefinitionEvent e) {
    updateTreeForEvent(e);
  }

  /**
   * Tell the tree to re-render the relevant node when a parameter
   * mapping has been added or removed from the definition.
   */
  private void updateTreeForEvent(ServiceDefinitionEvent e) {
    DefaultMutableTreeNode prNode = nodesByPRName.get(e.getPrName());
    DefaultMutableTreeNode paramNode = null;
    Enumeration<DefaultMutableTreeNode> paramsEnum = prNode.children();
    while(paramsEnum.hasMoreElements()) {
      DefaultMutableTreeNode nextNode = paramsEnum.nextElement();
      if(e.getPrParameterName().equals(nextNode.getUserObject())) {
        // found the right node for this parameter
        paramNode = nextNode;
        break;
      }
    }

    if(paramNode != null) {
      applicationTreeModel.nodeChanged(paramNode);
      // force update of the button states if necessary
      valueChanged(null);
    }
  }

  /**
   * Enables and disables the attach/detach buttons depending what is
   * selected in the tree.
   */
  public void valueChanged(TreeSelectionEvent e) {
    TreePath selectionPath = mainForm.applicationTree.getSelectionPath();
    boolean enableAttach;
    boolean enableDetach;
    if(selectionPath == null || selectionPath.getPathCount() != 3) {
      // disable both if we don't have a leaf node selected
      enableAttach = false;
      enableDetach = false;
    }
    else {
      DefaultMutableTreeNode prNode = (DefaultMutableTreeNode)selectionPath
              .getPathComponent(1);
      DefaultMutableTreeNode paramNode = (DefaultMutableTreeNode)selectionPath
              .getPathComponent(2);

      Map<String, String> reverseMapForPR = reverseParameterMappings
              .get(prNode);
      if(reverseMapForPR.containsKey(paramNode.getUserObject())) {
        // currently attached
        enableAttach = false;
        enableDetach = true;
      }
      else {
        // currently detached
        enableAttach = true;
        enableDetach = false;
      }
    }

    mainForm.attachParamButton.setEnabled(enableAttach);
    mainForm.detachParamButton.setEnabled(enableDetach);
  }

  /**
   * Tree cell renderer that adds the name of the GaS parameter mapped
   * to each PR parameter.
   */
  public class ApplicationTreeRenderer extends DefaultTreeCellRenderer {

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value,
            boolean sel, boolean expanded, boolean leaf, int row,
            boolean hasFocus) {
      DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
      if(node.getParent() == null) {
        // root node - not visible but we still need to handle it
        return super.getTreeCellRendererComponent(tree, value, sel, expanded,
                leaf, row, hasFocus);
      }
      Object userObj = node.getUserObject();

      if(userObj instanceof PRInfo) {
        // this is a PR node, use its name as the node name and its icon
        // (from GATE) as the icon.
        PRInfo prInfo = (PRInfo)userObj;
        JLabel defaultRenderer = (JLabel)super.getTreeCellRendererComponent(
                tree, prInfo.getName(), sel, expanded, leaf, row, hasFocus);
        defaultRenderer.setIcon(prInfo.getIcon());
        return defaultRenderer;
      }
      else {
        // a parameter node
        // does it have any parameter mappings?
        String gasParam = reverseParameterMappings.get(
                (DefaultMutableTreeNode)node.getParent()).get(userObj);
        if(gasParam == null) {
          // no, so just render as "prParamName"
          return super.getTreeCellRendererComponent(tree, value, sel, expanded,
                  leaf, row, hasFocus);
        }
        else {
          // yes, so render as "prParamName = gasParamName" with the GaS
          // param name in red (we like HTML labels)
          return super.getTreeCellRendererComponent(tree, "<HTML>" + value
                  + " = <FONT color=red>" + gasParam + "</FONT></HTML>", sel,
                  expanded, leaf, row, hasFocus);
        }
      }
    }

  }

  /**
   * Action to attach the currently selected PR parameter to a GaS
   * parameter in the definition.
   */
  public class AttachParamAction extends AbstractAction {
    public AttachParamAction() {
      super("Attach to parameter");
    }

    public void actionPerformed(ActionEvent e) {
      TreePath selectionPath = mainForm.applicationTree.getSelectionPath();
      if(selectionPath == null || selectionPath.getPathCount() != 3) {
        // no parameter selected in the tree
        return;
      }

      String paramToAttach = JOptionPane.showInputDialog(mainFrame,
              "Please specify the GaS parameter to use", "Attach to parameter",
              JOptionPane.QUESTION_MESSAGE);
      if(paramToAttach == null) {
        // user cancelled
        return;
      }

      boolean parameterAdded = false;
      try {
        serviceDefinition.addParameter(paramToAttach);
        parameterAdded = true;
      }
      catch(IllegalArgumentException iae) {
        // do nothing - this exception just means that the definition
        // already contained the given parameter name
      }

      if(parameterAdded) {
        JOptionPane.showMessageDialog(mainFrame, "Parameter \"" + paramToAttach
                + "\" added to service definition", "Parameter added",
                JOptionPane.INFORMATION_MESSAGE);
      }

      // extract the PR name and PR parameter from the selected tree
      // path
      DefaultMutableTreeNode prNode = (DefaultMutableTreeNode)selectionPath
              .getPathComponent(1);
      DefaultMutableTreeNode paramNode = (DefaultMutableTreeNode)selectionPath
              .getPathComponent(2);

      PRInfo prInfo = (PRInfo)prNode.getUserObject();
      String paramName = (String)paramNode.getUserObject();

      // add the mapping - this will fire events to update the GUI
      serviceDefinition.addParameterMapping(paramToAttach, prInfo.getName(),
              paramName);
    }
  }

  /**
   * Action to detach the currently selected PR parameter from its GaS
   * parameter.
   */
  public class DetachParamAction extends AbstractAction {
    public DetachParamAction() {
      super("Detach");
    }

    public void actionPerformed(ActionEvent e) {
      TreePath selectionPath = mainForm.applicationTree.getSelectionPath();
      if(selectionPath == null || selectionPath.getPathCount() != 3) {
        // no parameter selected in the tree
        return;
      }

      // extract the PR name and PR parameter from the selected tree
      // path
      DefaultMutableTreeNode prNode = (DefaultMutableTreeNode)selectionPath
              .getPathComponent(1);
      DefaultMutableTreeNode paramNode = (DefaultMutableTreeNode)selectionPath
              .getPathComponent(2);

      PRInfo prInfo = (PRInfo)prNode.getUserObject();
      String prParam = (String)paramNode.getUserObject();
      // find the GaS param that it is mapped from
      Map<String, String> reverseMapForPR = reverseParameterMappings
              .get(prNode);
      String gasParam = reverseMapForPR.get(prParam);

      // remove the mapping - this will fire events to update the GUI
      serviceDefinition.removeParameterMapping(gasParam, prInfo.getName(),
              prParam);

      if(serviceDefinition.getFeatureMappings(gasParam).isEmpty()
              && serviceDefinition.getParameterMappings(gasParam).isEmpty()) {
        // offer to remove the GaS parameter entirely if it is no longer
        // used
        int result = JOptionPane.showConfirmDialog(mainFrame, new String[] {
            "GaS parameter \"" + gasParam + "\" is no longer mapped to",
            "anything.  Remove it from the definition?"}, "Parameter unused",
                JOptionPane.YES_NO_OPTION);
        if(result == JOptionPane.YES_OPTION) {
          serviceDefinition.removeParameter(gasParam);
        }
      }
    }
  }

  // remaining ServiceDefinitionListener methods not used

  public void annotationSetAdded(ServiceDefinitionEvent arg0) {
  }

  public void annotationSetRemoved(ServiceDefinitionEvent arg0) {
  }

  public void featureMappingAdded(ServiceDefinitionEvent arg0) {
  }

  public void featureMappingRemoved(ServiceDefinitionEvent arg0) {
  }

  public void parameterAdded(ServiceDefinitionEvent arg0) {
  }

  public void parameterChanged(ServiceDefinitionEvent arg0) {
  }

  public void parameterRemoved(ServiceDefinitionEvent arg0) {
  }
}
