/*
 *  TimestampConverter.java
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.lang.StringUtils;

/**
 * This class is converts a java.util.Date to a String and a String to a
 * java.util.Date for use as a Timestamp. It is used by BeanUtils when
 * copying properties.
 * 
 * <p>
 * <a href="TimestampConverter.java.html"><i>View Source</i></a>
 * </p>
 * 
 * @author <a href="mailto:dan@getrolling.com">Dan Kibler</a>
 */
public class TimestampConverter extends DateConverter {
  public static final String TS_FORMAT = DateUtil.getDatePattern()
          + " HH:mm:ss.S";

  protected Object convertToDate(Class type, Object value) {
    DateFormat df = new SimpleDateFormat(TS_FORMAT);
    if(value instanceof String) {
      try {
        if(StringUtils.isEmpty(value.toString())) {
          return null;
        }
        return df.parse((String)value);
      }
      catch(Exception pe) {
        throw new ConversionException("Error converting String to Timestamp");
      }
    }
    throw new ConversionException("Could not convert "
            + value.getClass().getName() + " to " + type.getName());
  }

  protected Object convertToString(Class type, Object value) {
    DateFormat df = new SimpleDateFormat(TS_FORMAT);
    if(value instanceof Date) {
      try {
        return df.format(value);
      }
      catch(Exception e) {
        throw new ConversionException("Error converting Timestamp to String");
      }
    }
    return value.toString();
  }
}
