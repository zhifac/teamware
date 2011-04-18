/*
 * This file is part of GLEAM, licenced under the GNU Lesser General Public
 * Licence.
 *
 * (c) 2006 The University of Sheffield
 * 
 * $Id$
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
