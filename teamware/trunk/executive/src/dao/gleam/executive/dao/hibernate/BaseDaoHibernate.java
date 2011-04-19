/*
 *  BaseDaoHibernate.java
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

import java.io.Serializable;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import gleam.executive.dao.Dao;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * This class serves as the Base class for all other Daos - namely to
 * hold common methods that they might all use. Can be used for standard
 * CRUD operations.
 * </p>
 * 
 * <p>
 * <a href="BaseDaoHibernate.java.html"><i>View Source</i></a>
 * </p>
 * 
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 */
public class BaseDaoHibernate extends HibernateDaoSupport implements Dao {
  protected final Log log = LogFactory.getLog(getClass());

  /**
   * @see gleam.executive.dao.Dao#saveObject(java.lang.Object)
   */
  public void saveObject(Object o) {
    getHibernateTemplate().saveOrUpdate(o);
  }

  /**
   * @see gleam.executive.dao.Dao#getObject(java.lang.Class,
   *      java.io.Serializable)
   */
  public Object getObject(Class clazz, Serializable id) {
    Object o = getHibernateTemplate().get(clazz, id);
    if(o == null) {
      throw new ObjectRetrievalFailureException(clazz, id);
    }
    return o;
  }

  /**
   * @see gleam.executive.dao.Dao#getObjects(java.lang.Class)
   */
  public List getObjects(Class clazz) {
    return getHibernateTemplate().loadAll(clazz);
  }

  /**
   * @see gleam.executive.dao.Dao#removeObject(java.lang.Class,
   *      java.io.Serializable)
   */
  public void removeObject(Class clazz, Serializable id) {
    getHibernateTemplate().delete(getObject(clazz, id));
  }
}
