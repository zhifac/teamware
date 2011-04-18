/*
 * This file is part of GLEAM, licenced under the GNU Lesser General Public
 * Licence.
 *
 * (c) 2006 The University of Sheffield
 * 
 * $Id$
 */
package gleam.gateservice.worker;

import gate.creole.ResourceInstantiationException;

import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * This class provides the entry point to run a standalone set of GaS
 * workers via Apache commons-daemon. At startup, it loads a Spring
 * application context containing one or more WorkerConfig beans, and at
 * shutdown it closes the application context, which will cause the
 * WorkerConfig beans to shut down their workers.
 * 
 * @author ian
 */
public class WorkerRunner {

  private static final Log log = LogFactory.getLog(WorkerRunner.class);

  /**
   * The command-line arguments passed to this runner.
   */
  private String[] commandLineArgs = null;

  /**
   * The spring application context that actually runs the workers.
   */
  private ConfigurableApplicationContext appCtx;

  /**
   * Saves the command line arguments for later use.
   * 
   * @param args paths to Spring bean definition files. This array is
   *          passed directly to the FileSystemXmlApplicationContext
   *          constructor, so constructs like "classpath:" will work.
   */
  public void init(String[] args) {
    commandLineArgs = args;
    log.info("Command line arguments: " + Arrays.asList(args));
  }

  /**
   * Loads a Spring application context from the files specified to
   * {@link #init}, and runs the workers defined by any beans of type
   * {@link WorkerConfig} in the context.
   */
  public void start() throws ResourceInstantiationException {
    // debugging option - show the GUI if requested by a system property
    if(Boolean.getBoolean("gleam.gateservice.worker.runner.showGui")) {
      log.info("Starting GATE GUI for debugging");
      javax.swing.JFrame mainFrame = new gate.gui.MainFrame();
      mainFrame.setTitle("GATE for GaS Workers");
      mainFrame.setVisible(true);
    }
    else {
      // if we're not showing the GUI then run headless
      System.setProperty("java.awt.headless", "true");
    }

    log.info("Loading application context");
    appCtx = new FileSystemXmlApplicationContext(commandLineArgs);

    // idiot check that this context really is running some GaS workers
    // String[] workerConfigBeans = appCtx.getBeanNamesForType(WorkerConfig.class);
    // if(workerConfigBeans == null || workerConfigBeans.length == 0) {
    //   throw new IllegalStateException("No WorkerConfig beans defined in "
    //           + "application context!");
    // }
  }

  /**
   * Calls {@link shutdown}.
   */
  public void stop() {
    shutdown();
  }

  /**
   * Does nothing, but required to work with commons-daemon.
   */
  public void destroy() {
    // nothing more to do
  }

  /**
   * Run a number of GasWorkers as defined in the given Spring bean
   * definition files. We extract all beans of type {@link WorkerConfig}
   * from the bean definitions and run all the workers they define.
   * 
   * @deprecated The preferred way to run standalone workers is via the
   *             <code>jsvc</code> tool of Apache commons-daemon,
   *             which provides much better lifecycle support (clean
   *             shutdown, etc.).
   * 
   * @param args paths to XML bean definition files.
   * @throws ResourceInstantiationException
   */
  public static void main(String[] args) throws ResourceInstantiationException {
    final WorkerRunner runner = new WorkerRunner();

    runner.init(args);
    runner.start();

    // add a shutdown hook to make sure workers are shut down properly
    Runnable callRunnerShutdown = new Runnable() {
      public void run() {
        log.info("Entering runner shutdown hook");
        runner.shutdown();
      }
    };
    Runtime.getRuntime().addShutdownHook(
            new Thread(callRunnerShutdown, "RunnerShutdownHook"));

    log.info("Startup complete");
    synchronized(runner) {
      try {
        runner.wait();
      }
      catch(InterruptedException e) {
        // do nothing
      }
    }

    System.exit(0);
  }

  /**
   * Close down the application context.
   */
  public void shutdown() {
    log.debug("Closing application context");
    appCtx.close();
    log.debug("Application context closed");
  }

}
