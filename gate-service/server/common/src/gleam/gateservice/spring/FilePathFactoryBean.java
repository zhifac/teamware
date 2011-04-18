/*
 * This file is part of SAFE, licenced under the GNU Lesser General Public
 * Licence.
 *
 * (c) 2006 The University of Sheffield
 * 
 * $Id$
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
