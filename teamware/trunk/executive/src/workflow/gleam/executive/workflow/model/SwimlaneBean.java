/*
 *  SwimlaneBean.java
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
package gleam.executive.workflow.model;

import java.util.List;

import org.jbpm.taskmgmt.def.Swimlane;
import org.jbpm.taskmgmt.def.Task;

//TODO Javadoc
public class SwimlaneBean {
  private long id = 0;

  private String name = null;

  private List possiblePerformers = null;

  private String actor = null;

  private List inPooledActors =null;

  private String pooledActors = null;

  public String getActor() {
    return actor;
  }

  public void setActor(String actor) {
    this.actor = actor;
  }

  public String getPooledActors() {
    return pooledActors;
  }

  public void setPooledActors(String pooledActors) {
    this.pooledActors = pooledActors;
  }

  public SwimlaneBean(Swimlane swimlane) {
    this.name = swimlane.getName();
    this.id = swimlane.getId();
    this.actor = swimlane.getActorIdExpression();
    this.pooledActors = swimlane.getPooledActorsExpression();
  }

  

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }


  public List getPossiblePerformers() {
    return possiblePerformers;
  }

  public void setPossiblePerformers(List possiblePerformers) {
    this.possiblePerformers = possiblePerformers;
  }


  /**
   * @return Returns the inPooledActors.
   */
  public List getInPooledActors() {
    return inPooledActors;
  }

  /**
   * @param inPooledActors The inPooledActors to set.
   */
  public void setInPooledActors(List inPooledActors) {
    this.inPooledActors = inPooledActors;
  }

}
