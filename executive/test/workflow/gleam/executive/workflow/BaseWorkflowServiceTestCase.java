/*
 *  BaseWorkflowServiceTestCase.java
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
package gleam.executive.workflow;

import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.test.AbstractTransactionalDataSourceSpringContextTests;

/**
 * Base class for running Dao tests.
 *
 * @author mraible
 */
public abstract class BaseWorkflowServiceTestCase
                                     extends
                                     AbstractTransactionalDataSourceSpringContextTests
 {

	private static final String LOG4J_CONF = "log4j.properties";

  protected final Log log = LogFactory.getLog(getClass());

  protected ResourceBundle rb;

  protected static String processesDir;

  protected String[] getConfigLocations() {
    setAutowireMode(AUTOWIRE_BY_NAME);
    return new String[] {
    		"classpath*:/**/dao/applicationContext-*.xml",
    		"classpath*:/**/workflow/applicationContext-*.xml",
        "classpath*:META-INF/applicationContext-*.xml"};
  }

  public BaseWorkflowServiceTestCase() {

    try {
      URL url =  this.getClass().getResource(LOG4J_CONF);
      //System.out.println("URL "+ url);
      PropertyConfigurator.configure(url);
      rb = ResourceBundle.getBundle("gleam.executive.workflow.WorkflowManagerTest");
      processesDir = rb.getString("processes.dir");
    }
    catch(MissingResourceException mre) {
      log.warn("No resource bundle found for: gleam.executive.workflow.WorkflowManagerTest");
    }


  }

  /**
   * Utility method to populate a javabean-style object with values from
   * a Properties file
   *
   * @param obj
   * @return Object populated object
   * @throws Exception
   */
  protected Object populate(Object obj) throws Exception {
    // loop through all the beans methods and set its properties from
    // its .properties file
    Map<String, String> map = new HashMap<String, String>();
    for(Enumeration keys = rb.getKeys(); keys.hasMoreElements();) {
      String key = (String)keys.nextElement();
      map.put(key, rb.getString(key));
    }
    BeanUtils.copyProperties(obj, map);
    return obj;
  }


}

