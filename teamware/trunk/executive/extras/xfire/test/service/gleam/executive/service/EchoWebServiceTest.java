/*
 *  EchoWebServiceTest.java
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.spring.AbstractXFireSpringTest;
import org.jdom.Document;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class EchoWebServiceTest extends AbstractXFireSpringTest {

    protected final Log log = LogFactory.getLog(getClass());

    public void setUp() throws Exception {
        super.setUp();
    }
    
    public void testGetWsdl() throws Exception {
        Document doc = getWSDLDocument("Echo");
        //printNode(doc);
        
        assertValid("//xsd:element[@name=\"echo\"]", doc);
        assertValid("//xsd:element[@name=\"echoResponse\"]", doc);
    }
    
    public void testCallEcho() throws Exception {
        Document response =
            invokeService("Echo", "/gleam/executive/service/echo.xml");
        //printNode(response);
        
        addNamespace("service","http://test.xfire.codehaus.org");
        assertValid("//service:echoResponse/service:out[text()=\"Hello world!\"]",response);


    }

    protected ApplicationContext createContext() {
        return new ClassPathXmlApplicationContext(new String[]{
                "gleam/executive/service/applicationContext-test.xml",
                "gleam/executive/service/applicationContext-webservice.xml"});
    }
    

}
