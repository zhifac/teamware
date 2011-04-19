/*
 *  Log4jInitListener.java
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
package gleam.util.logging;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.LogManager;
import org.apache.log4j.PropertyConfigurator;

/**
 * Servlet context listener used to initialise log4j from a property
 * file containing placeholders that resolve to paths within the web
 * application context root. At startup, the listener loads log4j
 * configuration from a properties file in a custom location (by
 * default, <code>log4j-config.properties</code> on the classpath, but
 * overridable with a context parameter named
 * "log4jInitListener.config"), but substituting specified variables
 * with "real path" values from the servlet context. The substitute
 * values are read from a second properties file (<code>log4j-paths.properties</code>
 * on the classpath, but overridable with a context parameter named
 * "log4jInitListener.paths"). For example, given a
 * log4j-config.properties containing:
 * 
 * <pre>
 * log4j.appender.F = org.apache.log4j.RollingFileAppender
 * log4j.appender.F.File = ${webinf}/logs/logfile.txt
 * </pre>
 * 
 * and a context parameter:
 * 
 * <pre>
 * &lt;context-param&gt;
 *   &lt;param-name&gt;log4jExtraPaths&lt;/param-name&gt;
 *   &lt;param-value&gt;
 *     webinf=/WEB-INF
 *   &lt;/param-value&gt;
 * &lt;/context-param&gt;
 * </pre>
 * 
 * log4j would be configured with
 * 
 * <pre>
 * log4j.appender.F.File=/real/path/to/webapp/WEB-INF/logs/logfile.txt
 * </pre>
 */
public class Log4jInitListener implements ServletContextListener {

  public static final String DEFAULT_PATHS_LOCATION = "log4j-paths.properties";

  public static final String DEFAULT_CONFIG_LOCATION = "log4j-config.properties";

  /**
   * Shut down log4j.
   */
  public void contextDestroyed(ServletContextEvent e) {
    LogManager.shutdown();
  }

  /**
   * Configure log4j from a custom property file, optionally
   * substituting in webapp-relative paths.
   */
  public void contextInitialized(ServletContextEvent e) {
    ServletContext ctx = e.getServletContext();
    ctx.log("Initialising log4j");

    // first load the paths file
    String log4jPaths = ctx.getInitParameter("log4jInitListener.paths");
    Properties extraPathsProperties = new Properties();
    if(log4jPaths == null) {
      log4jPaths = DEFAULT_PATHS_LOCATION;
    }
    ctx.log("Loading path definitions from " + log4jPaths);
    InputStream pathsPropertiesInputStream = this.getClass()
            .getResourceAsStream("/" + log4jPaths);
    if(pathsPropertiesInputStream != null) {
      try {
        extraPathsProperties.load(pathsPropertiesInputStream);
        pathsPropertiesInputStream.close();
        // convert context-relative paths to absolute ones
        Iterator<Map.Entry<Object, Object>> it = extraPathsProperties
                .entrySet().iterator();
        while(it.hasNext()) {
          Map.Entry<Object, Object> propEntry = it.next();
          String ctxPath = (String)propEntry.getValue();
          String translatedPath = ctx.getRealPath(ctxPath);
          if(translatedPath == null) {
            ctx.log("Entry \"" + propEntry.getKey() + "\": Couldn't translate "
                    + ctxPath + " to a real path - ignoring.");
            it.remove();
          }
          else {
            propEntry.setValue(translatedPath);
          }
        }
      }
      catch(IOException ioe) {
        ctx.log("Couldn't load properties from " + log4jPaths + ", ignoring",
                ioe);
      }
    }

    // next, load the config file, but with the paths properties as its
    // parent
    String log4jConfig = ctx.getInitParameter("log4jInitListener.config");
    Properties log4jProperties = new Properties(extraPathsProperties);
    if(log4jConfig == null) {
      log4jConfig = DEFAULT_CONFIG_LOCATION;
    }
    ctx.log("Loading log4j config from " + log4jConfig);
    InputStream configInputStream = this.getClass().getResourceAsStream(
            "/" + log4jConfig);
    if(configInputStream != null) {
      try {
        log4jProperties.load(configInputStream);
        configInputStream.close();
        
        // finally, configure log4j with the combined properties
        ctx.log("Applying log4j configuration");
        PropertyConfigurator.configure(log4jProperties);
        ctx.log("Log4j configuration complete");
      }
      catch(IOException ioe) {
        ctx.log("Couldn't load properties from " + log4jConfig
                + ", falling back on log4j default init.", ioe);
      }
    }
  }
}
