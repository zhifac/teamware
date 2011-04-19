/*
 *  StartupListenerTest.java
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
package gleam.executive.webapp.listener;

import java.util.Map;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import junit.framework.TestCase;
import gleam.executive.Constants;
import org.springframework.mock.web.MockServletContext;
import org.springframework.util.ClassUtils;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

/**
 * This class tests the StartupListener class to verify that variables are
 * placed into the application context.
 * 
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 */
public class StartupListenerTest extends TestCase {
  private MockServletContext sc = null;
  private ServletContextListener listener = null;

  protected void setUp() throws Exception {
    super.setUp();
    listener = new StartupListener();
    sc = new MockServletContext("");
    sc.addInitParameter("daoType", "hibernate");
    sc.addInitParameter(Constants.CSS_THEME, "simplicity");
    // initialize Spring
    String pkg = ClassUtils.classPackageAsResourcePath(Constants.class);
    System.out.println("StartupListenerTest pkg: " + pkg);
    sc.addInitParameter(ContextLoader.CONFIG_LOCATION_PARAM, "classpath*:/"
            + pkg + "/dao/testApplicationContext-*.xml,"
            + pkg + "/service/applicationContext-*.xml,"
            + pkg + "/webapp/testApplicationContext-*.xml");
  }

  protected void tearDown() throws Exception {
    super.tearDown();
    listener = null;
    sc = null;
  }

  public void testContextInitialized() {
    ServletContextEvent event = new ServletContextEvent(sc);
    listener.contextInitialized(event);
    assertTrue(sc.getAttribute(Constants.CONFIG) != null);
    Map config = (Map)sc.getAttribute(Constants.CONFIG);
    assertEquals(config.get(Constants.CSS_THEME), "simplicity");
    assertTrue(sc
            .getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE) != null);
    assertTrue(sc.getAttribute(Constants.AVAILABLE_ROLES) != null);
  }
}
