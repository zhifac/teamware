/*
 *  ResourceActionTest.java
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

import java.util.HashSet;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;

import gleam.executive.Constants;
import gleam.executive.model.Role;
import gleam.executive.webapp.form.ResourceForm;

public class ResourceActionTest extends BaseStrutsTestCase {
  public ResourceActionTest(String name) {
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
      log.warn("No resource bundle found for: " + className);
    }
  }

  public void testCancel() throws Exception {
    setRequestPathInfo("/editResource");
    addRequestParameter("method", "Cancel");
    actionPerform();
    verifyForward("viewResources");
    verifyNoActionErrors();
  }

  public void testEdit() throws Exception {
    setRequestPathInfo("/editResource");
    addRequestParameter("method", "Edit");
    addRequestParameter("from", "list");
    addRequestParameter("id", "8");
    actionPerform();
    verifyForward("edit");
    assertTrue(getRequest().getAttribute(Constants.RESOURCE_KEY) != null);
    verifyNoActionErrors();
  }

  public void testSearch() throws Exception {
    setRequestPathInfo("/resources");
    addRequestParameter("method", "Search");
    actionPerform();
    verifyForward("list");
    assertTrue(getRequest().getAttribute(Constants.RESOURCE_LIST) != null);
    verifyNoActionErrors();
  }
  
  public void testSave() throws Exception { 
    ResourceForm resourceForm = new ResourceForm();
    resourceForm.setId("14");
    resourceForm.setUrl("/test.html*");
    resourceForm.setDescription("A Test Page");
    Role role1 = new Role("admin");
    Role role2 = new Role("user");
    Set<Role> roles = new HashSet<Role>();
    roles.add(role1);
    roles.add(role2);
    resourceForm.setRoles(roles);
    getRequest().setAttribute(Constants.RESOURCE_KEY, resourceForm);
    setRequestPathInfo("/saveResource"); 
    addRequestParameter("method", "Save");
    actionPerform();
    
  }
  
  public void testRemove() throws Exception {
    setRequestPathInfo("/resources");
    addRequestParameter("method", "Delete");
    addRequestParameter("id","14");
    actionPerform();
    
    verifyForward("viewResources");
    verifyNoActionErrors();
  }
}
