/*
 *  LocaleFilter.java
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

import gleam.executive.Constants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.jstl.core.Config;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;

/**
 * Filter to wrap request with a request including user preferred
 * locale.
 */
public class LocaleFilter extends OncePerRequestFilter {

	protected final Log log = LogFactory.getLog(getClass());
	
  public void doFilterInternal(HttpServletRequest request,
          HttpServletResponse response, FilterChain chain) throws IOException,
          ServletException {

    String locale = request.getParameter("locale");
   
    Locale preferredLocale = null;

    if(locale != null) {
     log.debug("The value of locale is "+locale);
      preferredLocale = new Locale(locale);
    }

    HttpSession session = request.getSession(false);

    if(session != null) {
      if(preferredLocale == null) {
        preferredLocale = (Locale)session
                .getAttribute(Constants.PREFERRED_LOCALE_KEY);
        // look also in application context
        if(preferredLocale==null){
        	 Map<String, String> config = (Map<String, String>)getServletContext()
             .getAttribute(Constants.CONFIG);
        	 String lang = (String)config.get(Constants.PREFERRED_LOCALE_KEY);
        	 if(lang!=null){
        	    preferredLocale = new Locale(lang);
        	    log.debug("got from application context: PreferredLocale  is "+preferredLocale.getDisplayName()+"  "+preferredLocale.getDisplayLanguage());
        	 }
        	
        }
        else{
        	log.debug("got from session: PreferredLocale  is "+preferredLocale.getDisplayName()+"  "+preferredLocale.getDisplayLanguage());
        }
      }
      else {
    	log.debug("PreferredLocale  is "+preferredLocale.getDisplayName()+"  "+preferredLocale.getDisplayLanguage());
        session.setAttribute(Constants.PREFERRED_LOCALE_KEY, preferredLocale);
        Config.set(session, Config.FMT_LOCALE, preferredLocale);
      }

      if(preferredLocale != null && !(request instanceof LocaleRequestWrapper)) {
        request = new LocaleRequestWrapper(request, preferredLocale);
        LocaleContextHolder.setLocale(preferredLocale);
      }
    }

    String theme = request.getParameter("theme");
    if(theme!=null){
      System.out.println("The value of theme is "+theme);
    }
    
    if(theme != null && request.isUserInRole(Constants.ADMIN_ROLE)) {
      Map<String, String> config = (Map<String, String>)getServletContext()
              .getAttribute(Constants.CONFIG);
      config.put(Constants.CSS_THEME, theme);
      System.out.println("The theme has been added to the config");
    }
   
    chain.doFilter(request, response);

    // Reset thread-bound LocaleContext.
    LocaleContextHolder.setLocaleContext(null);
  }
}
