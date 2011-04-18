package gleam.executive.workflow.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.persistence.db.DbPersistenceServiceFactory;
import org.jbpm.svc.Service;


public class JbpmDbPersistenceServiceFactory extends DbPersistenceServiceFactory {

  private static final long serialVersionUID = 1L;

  public Service openService() {
    return new JbpmDbPersistenceService(this);
  }

  private static Log log = LogFactory.getLog(JbpmDbPersistenceServiceFactory.class);
}

