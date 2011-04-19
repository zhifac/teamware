/*
 *  CurrencyConverter.java
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

import java.text.DecimalFormat;
import java.text.ParseException;
import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class is converts a Double to a double-digit String (and
 * vise-versa) by BeanUtils when copying properties. Registered for use
 * in BaseAction.
 * 
 * <p>
 * <a href="CurrencyConverter.java.html"><i>View Source</i></a>
 * </p>
 * 
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 */
public class CurrencyConverter implements Converter {
  protected final Log log = LogFactory.getLog(CurrencyConverter.class);

  protected final DecimalFormat formatter = new DecimalFormat("###,###.00");

  /**
   * Convert a String to a Double and a Double to a String
   * 
   * @param type the class type to output
   * @param value the object to convert
   * @return object the converted object (Double or String)
   */
  public final Object convert(final Class type, final Object value) {
    // for a null value, return null
    if(value == null) {
      return null;
    }
    else {
      if(value instanceof String) {
        if(log.isDebugEnabled()) {
          log.debug("value (" + value + ") instance of String");
        }
        try {
          if(StringUtils.isBlank(String.valueOf(value))) {
            return null;
          }
          if(log.isDebugEnabled()) {
            log.debug("converting '" + value + "' to a decimal");
          }
          // formatter.setDecimalSeparatorAlwaysShown(true);
          Number num = formatter.parse(String.valueOf(value));
          return new Double(num.doubleValue());
        }
        catch(ParseException pe) {
          pe.printStackTrace();
        }
      }
      else if(value instanceof Double) {
        if(log.isDebugEnabled()) {
          log.debug("value (" + value + ") instance of Double");
          log.debug("returning double: " + formatter.format(value));
        }
        return formatter.format(value);
      }
    }
    throw new ConversionException("Could not convert " + value + " to "
            + type.getName() + "!");
  }
}
