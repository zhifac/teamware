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
