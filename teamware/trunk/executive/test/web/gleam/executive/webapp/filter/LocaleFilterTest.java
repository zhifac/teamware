/*
 *  LocaleFilterTest.java
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

import java.util.Locale;
import javax.servlet.jsp.jstl.core.Config;
import junit.framework.TestCase;
import gleam.executive.Constants;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

public class LocaleFilterTest extends TestCase {
  private LocaleFilter filter = null;

  protected void setUp() throws Exception{
    filter = new LocaleFilter();
    filter.init(new MockFilterConfig());
  }

  public void testSetLocaleInSessionWhenSessionIsNull() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addParameter("locale", "es");
    MockHttpServletResponse response = new MockHttpServletResponse();
    filter.doFilter(request, response, new MockFilterChain());
    // no session, should result in null
    assertNull(request.getSession()
            .getAttribute(Constants.PREFERRED_LOCALE_KEY));
    // thread locale should always have it, regardless of session
    assertNotNull(LocaleContextHolder.getLocale());
  }

  public void testSetLocaleInSessionWhenSessionNotNull() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addParameter("locale", "es");
    MockHttpServletResponse response = new MockHttpServletResponse();
    request.setSession(new MockHttpSession(null));
    filter.doFilter(request, response, new MockFilterChain());
    // session not null, should result in not null
    Locale locale = (Locale)request.getSession().getAttribute(
            Constants.PREFERRED_LOCALE_KEY);
    assertNotNull(locale);
    assertNotNull(LocaleContextHolder.getLocale());
    assertEquals(new Locale("es"), locale);
  }

  public void testSetInvalidLocale() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addParameter("locale", "foo");
    MockHttpServletResponse response = new MockHttpServletResponse();
    request.setSession(new MockHttpSession(null));
    filter.doFilter(request, response, new MockFilterChain());
    // a locale will get set regardless - there's no such thing as an
    // invalid
    // one
    assertNotNull(request.getSession().getAttribute(
            Constants.PREFERRED_LOCALE_KEY));
  }

  public void testJstlLocaleIsSet() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addParameter("locale", "es");
    MockHttpServletResponse response = new MockHttpServletResponse();
    request.setSession(new MockHttpSession(null));
    filter.doFilter(request, response, new MockFilterChain());
    assertNotNull(Config.get(request.getSession(), Config.FMT_LOCALE));
  }
}
