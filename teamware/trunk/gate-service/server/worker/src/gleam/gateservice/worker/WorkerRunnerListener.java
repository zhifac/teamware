/*
 *  WorkerRunnerListener.java
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
package gleam.gateservice.worker;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @deprecated WorkerConfig beans now run their own workers at Spring context
 * initialization time, so a separate listener is no longer required.  The
 * class has been left for backwards compatibility but is now a no-op.
 */
public class WorkerRunnerListener implements ServletContextListener {

  private static final Log log = LogFactory.getLog(WorkerRunnerListener.class);

  /**
   * Does nothing.
   */
  public void contextDestroyed(ServletContextEvent evt) {
    log.warn("WorkerRunnerListener is no longer required!");
  }

  /**
   * Does nothing.
   */
  public void contextInitialized(ServletContextEvent evt) {
    log.warn("WorkerRunnerListener is no longer required!");
  }

}
