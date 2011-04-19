/*
 *  TestDocservice.java
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
package safe.docservice.test;

import gate.Gate;

import java.net.*;
import java.io.*;

import junit.framework.Test;
import junit.framework.TestSuite;


public class TestDocservice {

  /**
   * @param args
   */
  //public static void main(String[] args) {
  //}

  public static Test suite() throws Exception {
    Gate.init();
    TestSuite suite = new TestSuite();

    try {
      suite.addTest(TestDocserviceAPI.suite());
    } catch(Exception e) {
      System.out.println("Can't add tests! exception = " + e);
      throw(e);
    }
    return suite;
  }
  
  public static byte[] getFileContent(URL url) throws IOException {
    System.out.println("Opening url: " + url);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    BufferedInputStream in = new BufferedInputStream(url.openStream());
    byte[] buf = new byte[4096];
    int numRead = 0;
    while ((numRead = in.read(buf)) >= 0) {
      baos.write(buf, 0, numRead);
    }
    in.close();
    return baos.toByteArray();
  }
}
