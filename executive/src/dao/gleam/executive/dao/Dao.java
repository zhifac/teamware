/*
 *  Dao.java
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
package gleam.executive.dao;

import java.io.Serializable;
import java.util.List;

/**
 * Data Access Object (Dao) interface. This is an interface used to tag
 * our Dao classes and to provide common methods to all Daos.
 * 
 * <p>
 * <a href="Dao.java.html"><i>View Source</i></a>
 * </p>
 * 
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 */
public interface Dao {
  /**
   * Generic method used to get all objects of a particular type. This
   * is the same as lookup up all rows in a table.
   * 
   * @param clazz the type of objects (a.k.a. while table) to get data
   *          from
   * @return List of populated objects
   */
  public List getObjects(Class clazz);

  /**
   * Generic method to get an object based on class and identifier. An
   * ObjectRetrievalFailureException Runtime Exception is thrown if
   * nothing is found.
   * 
   * @param clazz model class to lookup
   * @param id the identifier (primary key) of the class
   * @return a populated object
   * @see org.springframework.orm.ObjectRetrievalFailureException
   */
  public Object getObject(Class clazz, Serializable id);

  /**
   * Generic method to save an object - handles both update and insert.
   * 
   * @param o the object to save
   */
  public void saveObject(Object o);

  /**
   * Generic method to delete an object based on class and id
   * 
   * @param clazz model class to lookup
   * @param id the identifier (primary key) of the class
   */
  public void removeObject(Class clazz, Serializable id);
}
