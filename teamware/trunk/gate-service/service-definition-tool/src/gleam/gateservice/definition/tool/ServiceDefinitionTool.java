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
import gate.util.GateException;

/**
 * Main entry point for the service definition tool.
 */
public class ServiceDefinitionTool {

  /**
   * Initialize GATE and display the first window for this application.
   */
  public static void main(String[] args) throws GateException {
    Gate.init();
    new MainFrame().setVisible(true);
  }
}
