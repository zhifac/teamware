/*
 *  UserWebServiceTest.java
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
package gleam.executive.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import gleam.executive.dao.UserDao;
import gleam.executive.model.User;
import org.codehaus.xfire.client.XFireProxyFactory;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.spring.AbstractXFireSpringTest;
import org.jdom.Document;
import org.jmock.Mock;
import org.jmock.core.constraint.IsEqual;
import org.jmock.core.matcher.InvokeOnceMatcher;
import org.jmock.core.stub.ReturnStub;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class UserWebServiceTest extends AbstractXFireSpringTest {

    protected final Log log = LogFactory.getLog(getClass());

    public void setUp() throws Exception {
        super.setUp();
    }
    
    public void testGetWsdl() throws Exception {
        Document doc = getWSDLDocument("UserWebService");
        printNode(doc);
        
        assertValid("//xsd:complexType[@name=\"User\"]", doc);
        assertValid("//xsd:complexType[@name=\"Role\"]", doc);
    }
    
    public void testGetUser() throws Exception {
        // Setup testharness 
        User testData = new User("tomcat");
        testData.setEnabled(true);
        Mock userDao = new Mock(UserDao.class);
        
        // because we can't extend MockObjectTestCase we create new instances for once(), eq() and returnValue()
        InvokeOnceMatcher once = new InvokeOnceMatcher();
        IsEqual eq = new IsEqual(new Long(1));
        ReturnStub returnValue = new ReturnStub(testData);
        userDao.expects(once).method("getUser").with(eq).will(returnValue);
        
        UserManager service = (UserManager) getContext().getBean("userManager");
        service.setUserDao((UserDao)userDao.proxy());
        
        // invoke webservice
        Document response =
            invokeService("UserWebService", "/gleam/executive/service/getUser.xml");

        //printNode(response);
        // verify result
        userDao.verify();
        addNamespace("service","http://service.appfuse.org");
        addNamespace("model","http://model.appfuse.org");
        assertValid("//service:getUserResponse/service:out[model:username=\"tomcat\"]",response);
        assertValid("//service:getUserResponse/service:out[model:enabled=\"true\"]",response);
    }
    
    public void testGetUsers() throws Exception {
        Service service = getServiceRegistry().getService("UserWebService");
        UserWebService client = (UserWebService)
            new XFireProxyFactory(getXFire()).create(service, "xfire.local://UserWebService");

        final User testUser = new User("tomcat");
        User user = new User();

        List testData = new ArrayList(){{ add(testUser); }};
        testUser.setEnabled(true);
        Mock userDao = new Mock(UserDao.class);
        
        // because we can't extend MockObjectTestCase we create new instances for once(), eq() and returnValue()
        InvokeOnceMatcher once = new InvokeOnceMatcher();
        ReturnStub returnValue = new ReturnStub(testData);
        IsEqual eq = new IsEqual(user);
        userDao.expects(once).method("getUsers").with(eq).will(returnValue);
        
        UserManager userService = (UserManager) getContext().getBean("userManager");
        userService.setUserDao((UserDao)userDao.proxy());

        List userList = (List)client.getUsers(user);
        assertNotNull(userList);
        userDao.verify();
    }
    
    protected ApplicationContext createContext() {
        return new ClassPathXmlApplicationContext(new String[]{
                "gleam/executive/service/applicationContext-test.xml",
                "gleam/executive/service/applicationContext-webservice.xml"});
    }
    

}
