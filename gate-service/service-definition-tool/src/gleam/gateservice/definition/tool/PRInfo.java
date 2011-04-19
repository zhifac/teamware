/*
 *  PRInfo.java
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
