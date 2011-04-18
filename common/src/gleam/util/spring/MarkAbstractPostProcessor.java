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
