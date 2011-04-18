/*
 * This file is part of SAFE, licenced under the GNU Lesser General Public
 * Licence.
 *
 * (c) 2007 The University of Sheffield
 *
 * $Id$
 */
package gleam.gateservice.definition.tool;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * Renderer for the tree representing an {@link ApplicationInfo} object.
 * The nodes in this tree represent either PRs or their runtime
 * parameters.
 */
public class ApplicationInfoTreeCellRenderer extends DefaultTreeCellRenderer {

  @Override
  public Component getTreeCellRendererComponent(JTree tree, Object value,
          boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
    DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
    Object userObj = node.getUserObject();

    if(userObj instanceof PRInfo) {
      // this is a PR node, use its name as the node name and its icon
      // (from GATE) as the icon.
      PRInfo prInfo = (PRInfo)userObj;
      JLabel defaultRenderer = (JLabel)super.getTreeCellRendererComponent(tree,
              prInfo.getName(), sel, expanded, leaf, row, hasFocus);
      defaultRenderer.setIcon(prInfo.getIcon());
      return defaultRenderer;
    }
    else {
      // this is a parameter name node, use the default renderer
      return super.getTreeCellRendererComponent(tree, value, sel, expanded,
              leaf, row, hasFocus);
    }
  }
}
