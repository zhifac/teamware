/*
 *  JbpmDbPersistenceService.java
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
package gleam.executive.workflow.core;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.jbpm.persistence.db.DbPersistenceService;


public class JbpmDbPersistenceService extends DbPersistenceService {

  private static final long serialVersionUID = 1L;

  protected JbpmDbPersistenceServiceFactory persistenceServiceFactory = null;


  protected JbpmGraphSession graphSession = null;
  protected JbpmTaskMgmtSession taskMgmtSession = null;


  public JbpmDbPersistenceService(JbpmDbPersistenceServiceFactory persistenceServiceFactory) {
    super(persistenceServiceFactory);
  }

  // getters and setters //////////////////////////////////////////////////////

  public JbpmGraphSession getGraphSession() {
    if (graphSession==null) {
      Session session = getSession();
      if (session!=null) {
        graphSession = new JbpmGraphSession(session);
      }
    }
    return graphSession;
  }

  public JbpmTaskMgmtSession getTaskMgmtSession() {
    if (taskMgmtSession==null) {
      Session session = getSession();
      if (session!=null) {
        taskMgmtSession = new JbpmTaskMgmtSession(session);
      }
    }
    return taskMgmtSession;
  }

  private static Log log = LogFactory.getLog(JbpmDbPersistenceService.class);
}
