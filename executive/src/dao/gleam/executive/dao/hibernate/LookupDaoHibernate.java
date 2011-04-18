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
