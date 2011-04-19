/*
 *  ServiceActionTest.java
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
package gleam.executive.webapp.action;

import java.util.MissingResourceException;
import java.util.ResourceBundle;
import gleam.executive.Constants;

public class ServiceActionTest extends BaseStrutsTestCase {
  public ServiceActionTest(String name) {
    super(name);
  }

  protected void setUp() throws Exception {
    super.setUp();
    getMockRequest().setUserRole("admin");
    String className = this.getClass().getName();
    try {
      rb = ResourceBundle.getBundle(className);
    }
    catch(MissingResourceException mre) {
      log.warn("No service bundle found for: " + className);
    }
  }

  public void testSearch() throws Exception {
    setRequestPathInfo("/services");
    addRequestParameter("method", "Search");
    actionPerform();
    verifyForward("list");
    assertTrue(getRequest().getAttribute(Constants.SERVICE_LIST) != null);
    verifyNoActionErrors();
  }
  
  /*No way to test the save action here as it is not a normal one, 
   * but it is tested in jsp-test indirectly.
  public void testSave() throws Exception { 
    setRequestPathInfo("/services");
    addRequestParameter("method", "Search");
    setRequestPathInfo("/saveService"); 
    addRequestParameter("method", "Save");
    actionPerform();
  }*/
  
}
