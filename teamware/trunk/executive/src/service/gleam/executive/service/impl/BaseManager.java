package gleam.executive.service.impl;

import java.io.Serializable;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import gleam.executive.dao.Dao;
import gleam.executive.service.Manager;

/**
 * Base class for Business Services - use this class for utility methods
 * and generic CRUD methods.
 * 
 * <p>
 * <a href="BaseManager.java.html"><i>View Source</i></a>
 * </p>
 * 
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 */
public class BaseManager implements Manager {
  protected final Log log = LogFactory.getLog(getClass());

  protected Dao dao = null;

  /**
   * @see gleam.executive.service.Manager#setDao(gleam.executive.dao.Dao)
   */
  public void setDao(Dao dao) {
    this.dao = dao;
  }

  /**
   * @see gleam.executive.service.Manager#getObject(java.lang.Class,
   *      java.io.Serializable)
   */
  public Object getObject(Class clazz, Serializable id) {
    return dao.getObject(clazz, id);
  }

  /**
   * @see gleam.executive.service.Manager#getObjects(java.lang.Class)
   */
  public List getObjects(Class clazz) {
    return dao.getObjects(clazz);
  }
  
  public List getObjects(){
    Class clazz = null;
    return dao.getObjects(clazz);
  }
  
  /**
   * @see gleam.executive.service.Manager#removeObject(java.lang.Class,
   *      java.io.Serializable)
   */
  public void removeObject(Class clazz, Serializable id) {
    dao.removeObject(clazz, id);
  }

  /**
   * @see gleam.executive.service.Manager#saveObject(java.lang.Object)
   */
  public void saveObject(Object o) {
    dao.saveObject(o);
  }
}
