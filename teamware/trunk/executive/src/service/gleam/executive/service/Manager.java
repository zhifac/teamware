/*
 *  Manager.java
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
package gleam.executive.service;

import java.io.Serializable;
import java.util.List;
import gleam.executive.dao.Dao;

public interface Manager {
  /**
   * Expose the setDao method for testing purposes
   * 
   * @param dao
   */
  public void setDao(Dao dao);

  /**
   * Generic method used to get a all objects of a particular type.
   * 
   * @param clazz the type of objects
   * @return List of populated objects
   */
  public List getObjects(Class clazz);

  public List getObjects();
  /**
   * Generic method to get an object based on class and identifier.
   * 
   * @param clazz model class to lookup
   * @param id the identifier (primary key) of the class
   * @return a populated object
   * @see org.springframework.orm.ObjectRetrievalFailureException
   */
  public Object getObject(Class clazz, Serializable id);

  /**
   * Generic method to save an object.
   * 
   * @param o the object to save
   */
  public void saveObject(Object o);

  /**
   * Generic method to delete an object based on class and id
   * 
   * @param clazz model class to lookup
   * @param id the identifier of the class
   */
  public void removeObject(Class clazz, Serializable id);
}
