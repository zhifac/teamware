/*
 *  StringUtilTest.java
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

import junit.framework.TestCase;

public class StringUtilTest extends TestCase {
  public StringUtilTest(String name) {
    super(name);
  }

  protected void setUp() throws Exception {
    super.setUp();
  }

  protected void tearDown() throws Exception {
    super.tearDown();
  }

  public void testEncodePassword() throws Exception {
    String password = "tomcat";
    String encrypted = "536c0b339345616c1b33caf454454d8b8a190d6c";
    assertEquals(StringUtil.encodePassword(password, "SHA"), encrypted);
  }

  public static void main(String[] args) {
    junit.textui.TestRunner.run(StringUtilTest.class);
  }
}
