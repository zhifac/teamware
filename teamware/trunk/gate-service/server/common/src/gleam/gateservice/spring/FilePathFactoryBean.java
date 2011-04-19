/*
 *  FilePathFactoryBean.java
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
package gleam.gateservice.spring;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.core.io.Resource;

/**
 * Factory bean that takes a Spring resource, and returns the absolute
 * path of the file it represents. This will fail if the given resource
 * does not support the getFile method.
 */
public class FilePathFactoryBean implements FactoryBean {

  private Resource resource;
  
  public void setResource(Resource resource) {
    this.resource = resource;
  }

  public Object getObject() throws Exception {
    return resource.getFile().getAbsolutePath();
  }

  public Class getObjectType() {
    return String.class;
  }

  public boolean isSingleton() {
    return true;
  }
}
