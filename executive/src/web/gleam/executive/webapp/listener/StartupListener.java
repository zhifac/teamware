/*
 *  StartupListener.java
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
package gleam.executive.webapp.listener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.acegisecurity.providers.AuthenticationProvider;
import org.acegisecurity.providers.ProviderManager;
import org.acegisecurity.providers.encoding.Md5PasswordEncoder;
import org.acegisecurity.providers.rememberme.RememberMeAuthenticationProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.RuntimeConstants;

import gleam.executive.Constants;
import gleam.executive.model.WebAppBean;
import gleam.executive.service.LookupManager;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * <p>
 * StartupListener class used to initialize and database settings and
 * populate any application-wide drop-downs.
 *
 * <p>
 * Keep in mind that this listener is executed outside of
 * OpenSessionInViewFilter, so if you're using Hibernate you'll have to
 * explicitly initialize all loaded data at the Dao or service level to
 * avoid LazyInitializationException. Hibernate.initialize() works well
 * for doing this.
 *
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 */
public class StartupListener extends ContextLoaderListener implements
                                                          ServletContextListener {

  private static final Log log = LogFactory.getLog(StartupListener.class);

  public void contextInitialized(ServletContextEvent event) {
    if(log.isDebugEnabled()) {
      log.debug("initializing context...");
    }

    // call Spring's context ContextLoaderListener to initialize
    // all the context files specified in web.xml
    super.contextInitialized(event);

    ServletContext context = event.getServletContext();

    // Orion starts Servlets before Listeners, so check if the config
    // object already exists
    Map<String, Object> config = (HashMap)context
            .getAttribute(Constants.CONFIG);

    if(config == null) {
      log.debug("appConfig IS NULL. Create new one");
      config = new HashMap<String, Object>();
    }

    if(context.getInitParameter(Constants.CSS_THEME) != null) {
      config.put(Constants.CSS_THEME, context
              .getInitParameter(Constants.CSS_THEME));
    }
    
    if(context.getInitParameter(Constants.PREFERRED_LOCALE_KEY) != null) {
        config.put(Constants.PREFERRED_LOCALE_KEY, context
                .getInitParameter(Constants.PREFERRED_LOCALE_KEY));
      }
    else {
    	log.debug("PREFERRED_LOCALE_KEY IS NULL");
    }
    

    ApplicationContext ctx = WebApplicationContextUtils
            .getRequiredWebApplicationContext(context);

    boolean encryptPassword = false;
    try {
      ProviderManager provider = (ProviderManager)ctx
              .getBean("authenticationManager");
      for(Iterator it = provider.getProviders().iterator(); it.hasNext();) {
        AuthenticationProvider p = (AuthenticationProvider)it.next();
        if(p instanceof RememberMeAuthenticationProvider) {
          config.put("rememberMeEnabled", Boolean.TRUE);
        }
      }

      if(ctx.containsBean("passwordEncoder")) {
        encryptPassword = true;
        config.put(Constants.ENCRYPT_PASSWORD, Boolean.TRUE);
        String algorithm = "SHA";
        if(ctx.getBean("passwordEncoder") instanceof Md5PasswordEncoder) {
          algorithm = "MD5";
        }
        config.put(Constants.ENC_ALGORITHM, algorithm);
      }


    }
    catch(NoSuchBeanDefinitionException n) {
      // ignore, should only happen when testing
    }

    context.setAttribute(Constants.CONFIG, config);

    // output the retrieved values for the Init and Context Parameters
    if(log.isDebugEnabled()) {
      log.debug("Remember Me Enabled? " + config.get("rememberMeEnabled"));
      log.debug("Encrypt Passwords? " + encryptPassword);
      if(encryptPassword) {
        log.debug("Encryption Algorithm: "
                + config.get(Constants.ENC_ALGORITHM));
      }
      log.debug("Populating drop-downs...");
    }

    setupContext(context);
  }

  public static void setupContext(ServletContext context) {
    ApplicationContext ctx = WebApplicationContextUtils
            .getRequiredWebApplicationContext(context);

    LookupManager lookupManager = (LookupManager)ctx.getBean("lookupManager");

    // get list of possible roles
    context.setAttribute(Constants.AVAILABLE_ROLES, lookupManager.getRoles());
    
    context.setAttribute(Constants.ALL_ROLES, lookupManager.getAllRoles());
//  get list of possible services
    context.setAttribute(Constants.AVAILABLE_SERVICES, lookupManager.getAllServices());
    if(log.isDebugEnabled()) {
      log.debug("Drop-down initialization complete [OK]");
    }

    WebAppBean webapp = (WebAppBean)ctx.getBean("webAppBean");
    context.setAttribute("webappname",webapp.getName());
    context.setAttribute("urlbase",webapp.getUrlBase());
    context.setAttribute("privateurlbase", webapp.getPrivateUrlBase());
    context.setAttribute("instancename",webapp.getInstanceName());
    context.setAttribute("webapptitle",webapp.getTitle());
    context.setAttribute("instancedir",webapp.getInstanceDir());
    log.debug("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
    log.debug("Setting webapp name "+webapp.getName()+" to application scope ");
    log.debug("Setting urlbase "+webapp.getUrlBase()+" to application scope ");
    log.debug("Setting privateurlbase "+webapp.getPrivateUrlBase()+" to application scope ");
    log.debug("Setting instancename "+webapp.getInstanceName()+" to application scope ");
    log.debug("Setting webapptitle "+webapp.getTitle()+" to application scope ");
    log.debug("Setting instancedir "+webapp.getInstanceDir()+" to application scope ");
    log.debug("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
 
  }
}
