/*
 *  BaseDaoTestCase.java
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
package gleam.executive.dao;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.test.AbstractTransactionalDataSourceSpringContextTests;

/**
 * Base class for running Dao tests.
 * 
 * @author mraible
 */
public abstract class BaseDaoTestCase
                                     extends
                                       AbstractTransactionalDataSourceSpringContextTests {
  protected final Log log = LogFactory.getLog(getClass());

  protected ResourceBundle rb;

  protected String[] getConfigLocations() {
    setAutowireMode(AUTOWIRE_BY_NAME);
    return new String[] {"classpath*:/**/dao/testApplicationContext-*.xml"};
  }

  public BaseDaoTestCase() {
    // Since a ResourceBundle is not required for each class, just
    // do a simple check to see if one exists
    String className = this.getClass().getName();
    try {
      rb = ResourceBundle.getBundle(className);
    }
    catch(MissingResourceException mre) {
      // log.warn("No resource bundle found for: " + className);
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
