/*
 *  MarkAbstractPostProcessor.java
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
package gleam.util.spring;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;

/**
 * BeanFactoryPostProcessor that sets the "abstract" flag on a particular bean
 * definition or definitions.
 */
public class MarkAbstractPostProcessor implements BeanFactoryPostProcessor {

  private final Log logger = LogFactory.getLog(getClass());

  /**
   * The names of the beans to make abstract.
   */
  private String[] beanNames;

  public void setBeanNames(String[] beanNames) {
    this.beanNames = beanNames;
  }

  public void postProcessBeanFactory(ConfigurableListableBeanFactory factory)
           throws BeansException {
    for(String beanName : beanNames) {
      BeanDefinition bd = factory.getBeanDefinition(beanName);
      if(bd instanceof AbstractBeanDefinition) {
        ((AbstractBeanDefinition)bd).setAbstract(true);
      }
      else {
        logger.warn("Could not set bean named \"" + beanName
            + "\" to be abstract");
      }
    }
  }
}
