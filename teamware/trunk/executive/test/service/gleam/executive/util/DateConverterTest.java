/*
 *  DateConverterTest.java
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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import org.springframework.context.i18n.LocaleContextHolder;
import junit.framework.TestCase;

public class DateConverterTest extends TestCase {
  private DateConverter converter = new DateConverter();

  public void testInternationalization() throws Exception {
    List<Locale> locales = new ArrayList<Locale>() {
      private static final long serialVersionUID = 1L;
      {
        add(Locale.US);
        add(Locale.GERMANY);
        add(Locale.FRANCE);
        add(Locale.CHINA);
        add(Locale.ITALY);
      }
    };
    for(Iterator localeIter = locales.iterator(); localeIter.hasNext();) {
      Locale locale = (Locale)localeIter.next();
      LocaleContextHolder.setLocale(locale);
      testConvertStringToDate();
      testConvertDateToString();
      testConvertStringToTimestamp();
      testConvertTimestampToString();
    }
  }

  public void testConvertStringToDate() throws Exception {
    Date today = new Date();
    Calendar todayCalendar = new GregorianCalendar();
    todayCalendar.setTime(today);
    String datePart = DateUtil.convertDateToString(today);
    Date date = (Date)converter.convert(Date.class, datePart);
    Calendar cal = new GregorianCalendar();
    cal.setTime(date);
    assertEquals(todayCalendar.get(Calendar.YEAR), cal.get(Calendar.YEAR));
    assertEquals(todayCalendar.get(Calendar.MONTH), cal.get(Calendar.MONTH));
    assertEquals(todayCalendar.get(Calendar.DAY_OF_MONTH), cal
            .get(Calendar.DAY_OF_MONTH));
  }

  public void testConvertDateToString() throws Exception {
    Calendar cal = new GregorianCalendar(2005, 00, 16);
    String date = (String)converter.convert(String.class, cal.getTime());
    assertEquals(DateUtil.convertDateToString(cal.getTime()), date);
  }

  public void testConvertStringToTimestamp() throws Exception {
    Date today = new Date();
    Calendar todayCalendar = new GregorianCalendar();
    todayCalendar.setTime(today);
    String datePart = DateUtil.convertDateToString(today);
    Timestamp time = (Timestamp)converter.convert(Timestamp.class, datePart
            + " 01:02:03.4");
    Calendar cal = new GregorianCalendar();
    cal.setTimeInMillis(time.getTime());
    assertEquals(todayCalendar.get(Calendar.YEAR), cal.get(Calendar.YEAR));
    assertEquals(todayCalendar.get(Calendar.MONTH), cal.get(Calendar.MONTH));
    assertEquals(todayCalendar.get(Calendar.DAY_OF_MONTH), cal
            .get(Calendar.DAY_OF_MONTH));
  }

  public void testConvertTimestampToString() throws Exception {
    Timestamp timestamp = Timestamp.valueOf("2005-03-10 01:02:03.4");
    String time = (String)converter.convert(String.class, timestamp);
    assertEquals(
            DateUtil.getDateTime(DateUtil.getDateTimePattern(), timestamp),
            time);
  }
}
