/*
 *  LocaleRequestWrapper.java
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
package gleam.executive.webapp.filter;

import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * HttpRequestWrapper overriding methods getLocale(), getLocales() to
 * include the user's preferred locale.
 */
public class LocaleRequestWrapper extends HttpServletRequestWrapper {
  private final transient Log log = LogFactory
          .getLog(LocaleRequestWrapper.class);

  private final Locale preferredLocale;

  public LocaleRequestWrapper(HttpServletRequest decorated, Locale userLocale) {
    super(decorated);
    preferredLocale = userLocale;
    if(null == preferredLocale) {
      log.error("preferred locale = null, it is an unexpected value!");
    }
  }

  /**
   * @see javax.servlet.ServletRequestWrapper#getLocale()
   */
  public Locale getLocale() {
    if(null != preferredLocale) {
      return preferredLocale;
    }
    else {
      return super.getLocale();
    }
  }

  /**
   * @see javax.servlet.ServletRequestWrapper#getLocales()
   */
  public Enumeration getLocales() {
    if(null != preferredLocale) {
      List l = Collections.list(super.getLocales());
      if(l.contains(preferredLocale)) {
        l.remove(preferredLocale);
      }
      l.add(0, preferredLocale);
      return Collections.enumeration(l);
    }
    else {
      return super.getLocales();
    }
  }

}
