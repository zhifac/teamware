/*
 *  LookupDaoHibernate.java
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
package gleam.executive.dao.hibernate;

import java.util.List;
import gleam.executive.dao.LookupDao;

/**
 * Hibernate implementation of LookupDao.
 * 
 * <p>
 * <a href="LookupDaoHibernate.java.html"><i>View Source</i></a>
 * </p>
 * 
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 */
public class LookupDaoHibernate extends BaseDaoHibernate implements LookupDao {
  /**
   * @see gleam.executive.dao.LookupDao#getRoles()
   */
  public List getRoles() {
    log.debug("retrieving all role names...");
    return getHibernateTemplate().find("from Role where id > 1");
  }
  
  public List getAllRoles() {
	    log.debug("retrieving all role names...");
	    return getHibernateTemplate().find("from Role");
	  }
  
  public List getResources(){
    log.debug("retrieving all resource ids...");
    return getHibernateTemplate().find("from Resource order by id");
  }
  
  public List getServices(){
    log.debug("retrieving all services names...");
    return getHibernateTemplate().find("from Service order by name");
  }
}
