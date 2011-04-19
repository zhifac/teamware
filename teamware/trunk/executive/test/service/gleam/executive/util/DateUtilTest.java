/*
 *  DateUtilTest.java
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

import java.util.Date;
import java.util.Locale;
import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.i18n.LocaleContextHolder;

public class DateUtilTest extends TestCase {
  // ~ Instance fields
  // ========================================================
  private final Log log = LogFactory.getLog(DateUtilTest.class);

  // ~ Constructors
  // ===========================================================
  public DateUtilTest(String name) {
    super(name);
  }

  public void testGetInternationalDatePattern() {
    LocaleContextHolder.setLocale(new Locale("en"));
    assertEquals("dd/MM/yyyy", DateUtil.getDatePattern());
    // non-existant bundle should default to default locale
    LocaleContextHolder.setLocale(new Locale("fi"));
    String fiPattern = DateUtil.getDatePattern();
    LocaleContextHolder.setLocale(Locale.getDefault());
    String defaultPattern = DateUtil.getDatePattern();
    assertEquals(defaultPattern, fiPattern);
  }

  public void testGetDate() throws Exception {
    if(log.isDebugEnabled()) {
      log.debug("db date to convert: " + new Date());
    }
    String date = DateUtil.getDate(new Date());
    if(log.isDebugEnabled()) {
      log.debug("converted ui date: " + date);
    }
    assertTrue(date != null);
  }

  public void testGetDateTime() {
    if(log.isDebugEnabled()) {
      log.debug("entered 'testGetDateTime' method");
    }
    String now = DateUtil.getTimeNow(new Date());
    assertTrue(now != null);
    log.debug(now);
  }
  
  public void testGetElapsedTimeHoursMinutesSecondsString(){
	  if(log.isDebugEnabled()) {
	      log.debug("entered 'testGetElapsedTimeHoursMinutesSecondsString' method");
	    }
	 long millis = 5000;
     log.debug(DateUtil.getElapsedTimeHoursMinutesSecondsString(millis));
  }

  public static void main(String[] args) {
    junit.textui.TestRunner.run(DateUtilTest.class);
  }
}
