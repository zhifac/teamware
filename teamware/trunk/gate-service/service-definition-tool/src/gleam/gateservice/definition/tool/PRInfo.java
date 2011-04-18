/*
 * This file is part of SAFE, licenced under the GNU Lesser General Public
 * Licence.
 *
 * (c) 2007 The University of Sheffield
 *
 * $Id$
 */
package gleam.gateservice.definition.tool;

import gate.Gate;
import gate.creole.ResourceData;

import javax.swing.Icon;

/**
 * Class holding information about a single PR in a GATE application.
 */
public class PRInfo {
  /**
   * The name of the PR.
   */
  private String name;

  /**
   * The class of the PR.
   */
  private String className;

  /**
   * The icon used for this PR in the GATE GUI. Lazily initialized at
   * first use.
   */
  private Icon icon;

  public String getClassName() {
    return className;
  }

  public void setClassName(String className) {
    this.className = className;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  /**
   * Get a suitable icon for this PR.
   */
  public Icon getIcon() {
    if(icon == null) {
      ResourceData rd = Gate.getCreoleRegister().get(className);
      String iconName = rd.getIcon();
      if(iconName == null) {
        // generic PR icon if this resource doesn't specify one
        iconName = "pr";
      }

      icon = gate.gui.MainFrame.getIcon(iconName);
    }

    return icon;
  }
}
