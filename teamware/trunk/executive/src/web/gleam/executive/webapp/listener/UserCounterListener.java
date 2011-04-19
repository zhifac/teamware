/*
 *  UserCounterListener.java
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

import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;

import org.acegisecurity.context.HttpSessionContextIntegrationFilter;
import org.acegisecurity.context.SecurityContext;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.Authentication;
import org.acegisecurity.AuthenticationTrustResolver;
import org.acegisecurity.AuthenticationTrustResolverImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import gleam.executive.model.User;

/**
 * UserCounterListener class used to count the current number of active
 * users for the applications. Does this by counting how many user
 * objects are stuffed into the session. It Also grabs these users and
 * exposes them in the servlet context.
 * 
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 */
public class UserCounterListener implements ServletContextListener,
                                HttpSessionAttributeListener {
  public static final String COUNT_KEY = "userCounter";

  public static final String USERS_KEY = "userNames";

  public static final String EVENT_KEY = HttpSessionContextIntegrationFilter.ACEGI_SECURITY_CONTEXT_KEY;

  private final transient Log log = LogFactory
          .getLog(UserCounterListener.class);

  private transient ServletContext servletContext;

  private int counter;

  private Set<Object> users;

  public synchronized void contextInitialized(ServletContextEvent sce) {
    servletContext = sce.getServletContext();
    servletContext.setAttribute((COUNT_KEY), Integer.toString(counter));
  }

  public synchronized void contextDestroyed(ServletContextEvent event) {
    servletContext = null;
    users = null;
    counter = 0;
  }

  synchronized void incrementUserCounter() {
    counter = Integer.parseInt((String)servletContext.getAttribute(COUNT_KEY));
    counter++;
    servletContext.setAttribute(COUNT_KEY, Integer.toString(counter));

    if(log.isDebugEnabled()) {
      log.debug("User Count: " + counter);
    }
  }

  synchronized void decrementUserCounter() {
    int counter = Integer.parseInt((String)servletContext
            .getAttribute(COUNT_KEY));
    counter--;

    if(counter < 0) {
      counter = 0;
    }

    servletContext.setAttribute(COUNT_KEY, Integer.toString(counter));

    if(log.isDebugEnabled()) {
      log.debug("User Count: " + counter);
    }
  }

  synchronized void addUsername(Object user) {
    users = (Set<Object>)servletContext.getAttribute(USERS_KEY);

    if(users == null) {
      users = new HashSet<Object>();
    }

    if(log.isDebugEnabled()) {
      if(users.contains(user)) {
        log.debug("User already logged in, adding anyway...");
      }
    }

    users.add(user);
    servletContext.setAttribute(USERS_KEY, users);
    incrementUserCounter();
  }

  synchronized void removeUsername(Object user) {
    users = (Set)servletContext.getAttribute(USERS_KEY);

    if(users != null) {
      users.remove(user);
    }

    servletContext.setAttribute(USERS_KEY, users);
    decrementUserCounter();
  }

  /**
   * This method is designed to catch when user's login and record their
   * name
   * 
   * @see javax.servlet.http.HttpSessionAttributeListener#attributeAdded(javax.servlet.http.HttpSessionBindingEvent)
   */
  public void attributeAdded(HttpSessionBindingEvent event) {
    log.debug("event.name: " + event.getName());
    if(event.getName().equals(EVENT_KEY) && !isAnonymous()) {
      SecurityContext securityContext = (SecurityContext)event.getValue();
      User user = (User)securityContext.getAuthentication().getPrincipal();
      addUsername(user);
    }
  }

  private boolean isAnonymous() {
    AuthenticationTrustResolver resolver = new AuthenticationTrustResolverImpl();
    SecurityContext ctx = SecurityContextHolder.getContext();
    if(ctx != null) {
      Authentication auth = ctx.getAuthentication();
      return resolver.isAnonymous(auth);
    }
    return true;
  }

  /**
   * When user's logout, remove their name from the hashMap
   * 
   * @see javax.servlet.http.HttpSessionAttributeListener#attributeRemoved(javax.servlet.http.HttpSessionBindingEvent)
   */
  public void attributeRemoved(HttpSessionBindingEvent event) {
    if(event.getName().equals(EVENT_KEY) && !isAnonymous()) {
      SecurityContext securityContext = (SecurityContext)event.getValue();
      Authentication auth = securityContext.getAuthentication();
      if(auth != null && (auth.getPrincipal() instanceof User)) {
        User user = (User)auth.getPrincipal();
        removeUsername(user);
      }
    }
  }

  /**
   * Needed for Acegi Security 1.0, as it adds an anonymous user to the
   * session and then replaces it after authentication.
   * http://forum.springframework.org/showthread.php?p=63593
   * 
   * @see javax.servlet.http.HttpSessionAttributeListener#attributeReplaced(javax.servlet.http.HttpSessionBindingEvent)
   */
  public void attributeReplaced(HttpSessionBindingEvent event) {
    if(event.getName().equals(EVENT_KEY) && !isAnonymous()) {
      SecurityContext securityContext = (SecurityContext)event.getValue();
      if(securityContext.getAuthentication() != null) {
        User user = (User)securityContext.getAuthentication().getPrincipal();
        addUsername(user);
      }
    }
  }
}
