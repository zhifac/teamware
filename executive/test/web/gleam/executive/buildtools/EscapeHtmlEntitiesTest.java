/*
 *  EscapeHtmlEntitiesTest.java
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
package gleam.executive.buildtools;

import java.io.StringReader;
import junit.framework.TestCase;
import org.apache.tools.ant.util.FileUtils;

/**
 * Testcase to verify EscapeHtmlEntities filter.
 * 
 * @author <a href="mailto:mikagoeckel@codehaus.org">Mika Goeckel</a>
 */
public class EscapeHtmlEntitiesTest extends TestCase {
  /**
   * Test Unicode->Entity escaping.
   * 
   * @throws Exception
   */
  public void testEscape() throws Exception {
    StringReader str = new StringReader("\u00E4\u00FC\u00F6\u00DF-\u00D6\u00F3");
    EscapeHtmlEntities boot = new EscapeHtmlEntities();
    EscapeHtmlEntities filter = (EscapeHtmlEntities)boot.chain(str);
    filter.setMode(EscapeHtmlEntities.ESCAPE);
    String result = FileUtils.readFully(filter, 200);
    assertEquals("&auml;&uuml;&ouml;&szlig;-&Ouml;&oacute;", result);
  }

  /**
   * Test Entity->Unicode unescaping.
   * 
   * @throws Exception
   */
  public void testUnescape() throws Exception {
    StringReader str = new StringReader(
            "&auml;&uuml;&ouml;&szlig;-&Ouml;&oacute;&noentity;");
    EscapeHtmlEntities boot = new EscapeHtmlEntities();
    EscapeHtmlEntities filter = (EscapeHtmlEntities)boot.chain(str);
    filter.setMode(EscapeHtmlEntities.UNESCAPE);
    String result = FileUtils.readFully(filter, 200);
    assertEquals("\u00E4\u00FC\u00F6\u00DF-\u00D6\u00F3&noentity;", result);
  }
}
