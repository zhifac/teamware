/*
 *  DateConverter.java
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
import java.sql.Timestamp;
import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.lang.StringUtils;

/**
 * This class is converts a java.util.Date to a String and a String to a
 * java.util.Date.
 * 
 * <p>
 * <a href="DateConverter.java.html"><i>View Source</i></a>
 * </p>
 * 
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 */
public class DateConverter implements Converter {
  public Object convert(Class type, Object value) {
    if(value == null) {
      return null;
    }
    else if(type == Timestamp.class) {
      return convertToDate(type, value, DateUtil.getDateTimePattern());
    }
    else if(type == Date.class) {
      return convertToDate(type, value, DateUtil.getDatePattern());
    }
    else if(type == String.class) {
      return convertToString(type, value);
    }
    throw new ConversionException("Could not convert "
            + value.getClass().getName() + " to " + type.getName());
  }

  protected Object convertToDate(Class type, Object value, String pattern) {
    DateFormat df = new SimpleDateFormat(pattern);
    if(value instanceof String) {
      try {
        if(StringUtils.isEmpty(value.toString())) {
          return null;
        }
        Date date = df.parse((String)value);
        if(type.equals(Timestamp.class)) {
          return new Timestamp(date.getTime());
        }
        return date;
      }
      catch(Exception pe) {
        pe.printStackTrace();
        throw new ConversionException("Error converting String to Date");
      }
    }
    throw new ConversionException("Could not convert "
            + value.getClass().getName() + " to " + type.getName());
  }

  protected Object convertToString(Class type, Object value) {
    if(value instanceof Date) {
      DateFormat df = new SimpleDateFormat(DateUtil.getDatePattern());
      if(value instanceof Timestamp) {
        df = new SimpleDateFormat(DateUtil.getDateTimePattern());
      }
      try {
        return df.format(value);
      }
      catch(Exception e) {
        e.printStackTrace();
        throw new ConversionException("Error converting Date to String");
      }
    }
    else {
      return value.toString();
    }
  }
}
