/*
 *  ApplicationInfoTreeCellRenderer.java
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
