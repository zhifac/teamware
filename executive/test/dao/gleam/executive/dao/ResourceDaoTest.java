/*
 *  ResourceDaoTest.java
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

import java.util.List;

import gleam.executive.Constants;
import gleam.executive.model.Resource;
import gleam.executive.model.Role;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;

public class ResourceDaoTest extends BaseDaoTestCase {
  private ResourceDao dao = null;

  private RoleDao rdao = null;

  public void setResourceDao(ResourceDao dao) {
    this.dao = dao;
  }

  public void setRoleDao(RoleDao rdao) {
    this.rdao = rdao;
  }

  public void testGetResourceInvalid() throws Exception {
    try {
      dao.getResource(new Long(1000));
      fail("'badResourcename' found in database, failing test...");
    }
    catch(DataAccessException d) {
      assertTrue(d != null);
    }
  }

  public void testGetResource() throws Exception {
    Resource resource = dao.getResource(new Long(6));
    assertNotNull(resource);
    assertEquals(2, resource.getRoles().size());
  }

  public void testGetResources() throws Exception{
    List<Resource> resources =(List<Resource>) dao.getResources();
    for(int i=0;i<resources.size();i++){
      Resource re = resources.get(i);
      System.out.println(re.getUrl());
    }
  }
  
  public void testUpdateResource() throws Exception {
    Resource resource = dao.getResource(new Long(1));
    dao.saveResource(resource);
    // we don't actually care for the purposes of this test exactly which
    // resource we're dealing with, only that we can't add two resources
    // with the same URL
    //assertEquals(resource.getUrl(), "/users.html*");
    // verify that violation occurs when adding new Resource with same
    // Resourcename
    resource.setId(null);
    endTransaction();
    try {
      dao.saveResource(resource);
      fail("saveResource didn't throw DataIntegrityViolationException");
    }
    catch(DataIntegrityViolationException e) {
      assertNotNull(e);
      log.debug("expected exception: " + e.getMessage());
    }
  }

  public void testAddAndRemoveResource() throws Exception {
    System.out.println("----------------Testing add and remove Resource----------------");
    Resource resource = new Resource();
    resource.setId(new Long(6));
    resource.setUrl("./index.jsp*");
    resource.setDescription("Home page");
  
    Role role = rdao.getRoleByName(Constants.ADMIN_ROLE);
    assertNotNull(role.getId());
    resource.addRole(role);
    dao.saveResource(resource);
    assertNotNull(resource.getId());
    assertEquals("./index.jsp*", resource.getUrl());
   
    Resource re = dao.getResource(resource.getId());
    dao.removeResource(resource.getId());
    try {
      resource = dao.getResource(resource.getId());
      fail("getResource didn't throw DataAccessException");
    }
    catch(DataAccessException d) {
      assertNotNull(d);
    }
  }
  
  public void testGetResourcesWithRole() throws Exception{
    System.out.println("----------------Testing get Resource with role name----------------");
    List resources=dao.getResourcesWithRole("manager");
    for(int i=0;i<resources.size();i++){
      Resource resource =(Resource)resources.get(i);
      System.out.println("The "+i+" Resource ID is "+resource.getId().toString());
      System.out.println("The "+i+" Resource URL is "+resource.getUrl());
    }
  }
  
}
