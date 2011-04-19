/*
 *  ExcelUtilTest.java
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
package gleam.executive.util;

import java.util.Iterator;
import java.util.List;
import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;

import gleam.executive.model.User;

public class ExcelUtilTest extends TestCase {
	  // ~ Instance fields
	  // ========================================================
	  private final Log log = LogFactory.getLog(ExcelUtilTest.class);
	  private static final String testXLSFilePath = "test/service/gleam/executive/service/resource/test.xls";
	  // ~ Constructors
	  // ===========================================================
	  public ExcelUtilTest(String name) {
	    super(name);
	  }
	  
	  public void testPopulateUser(){ 
		  try{
		  List<User> users = ExcelUtil.populateUser(testXLSFilePath);
		  Iterator<User> it = users.iterator();
		  while(it.hasNext()){
			  User user = it.next();
			  log.debug("---------------------------------------------");
			  log.debug("username: "+user.getUsername());
			  assertEquals("agaton", user.getUsername());
			  log.debug("password: "+user.getPassword());
			  assertEquals("tomcat", user.getPassword());
			  log.debug("firstName: "+user.getFirstName());
			  assertEquals("Milan", user.getFirstName());
			  log.debug("lastName: "+user.getLastName());
			  assertEquals("Agatonovic", user.getLastName());
			  log.debug("email: "+user.getEmail());
			  assertEquals("agaton@dcs.shef.ac.uk", user.getEmail());
			  String rolesCSVString = StringUtils.collectionToCommaDelimitedString(user.getRoleNames());
			  log.debug("rolesCSVString: "+rolesCSVString);
			  assertEquals("annotator", rolesCSVString);
		  }
		  } catch(Exception e){
			  e.printStackTrace();
		  }
		
	  }
}
