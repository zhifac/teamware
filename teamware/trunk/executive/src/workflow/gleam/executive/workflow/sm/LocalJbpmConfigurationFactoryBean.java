package gleam.executive.workflow.sm;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.zip.ZipInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmContext;
import org.jbpm.configuration.ObjectFactory;
import org.jbpm.configuration.ObjectFactoryImpl;
import org.jbpm.configuration.ObjectFactoryParser;
import org.jbpm.configuration.ObjectInfo;
import org.jbpm.configuration.ValueInfo;
import org.jbpm.graph.def.ProcessDefinition;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.access.BeanFactoryReference;
import org.springframework.core.io.Resource;

import org.springmodules.workflow.jbpm31.JbpmUtils;
import org.springmodules.workflow.jbpm31.definition.ProcessDefinitionFactoryBean;

/**
 * FactoryBean which allows customized creation of JbpmConfiguration
 * objects which are binded to the lifecycle of the bean factory
 * container. A BeanFactory aware ObjectFactory can be used by the
 * resulting object for retrieving beans from the application context,
 * delegating to the default implementation for unresolved names. It is
 * possible to use an already defined Hibernate SessionFactory by
 * injecting an approapriate HibernateTemplate - if defined, the
 * underlying session factory will be used by jBPM Persistence Service.
 *
 * If set to true, createSchema and dropSchema will be executed on
 * factory initialization and destruction, using the contextName
 * property which, by default, is equivalent with
 * JbpmContext.DEFAULT_JBPM_CONTEXT_NAME.
 *
 *
 * @see org.jbpm.configuration.ObjectFactory
 * @author Costin Leau
 *
 */
public class LocalJbpmConfigurationFactoryBean implements InitializingBean,
                                              DisposableBean, FactoryBean,
                                              BeanFactoryAware, BeanNameAware {
  private static final Log logger = LogFactory
          .getLog(LocalJbpmConfigurationFactoryBean.class);

  private JbpmConfiguration jbpmConfiguration;

  private ObjectFactory objectFactory;

  private Resource configuration;

  private boolean createSchema;

  private boolean dropSchema;

  private boolean hasPersistenceService;

  private String contextName = JbpmContext.DEFAULT_JBPM_CONTEXT_NAME;

  private Resource[] processDefinitionsResources;

  private SessionFactory sessionFactory;

  /**
   * FactoryLocator
   */
  private JbpmFactoryLocator factoryLocator = new JbpmFactoryLocator();

  private BeanFactoryReference reference;

  private String factoryKey = JbpmFactoryLocator.class.getName();

  /**
   * @see org.springframework.beans.factory.BeanFactoryAware#setBeanFactory(org.springframework.beans.factory.BeanFactory)
   */
  public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
	  logger.debug("setBeanFactory "+ beanFactory.toString());
    factoryLocator.setBeanFactory(beanFactory);
    reference = factoryLocator.useBeanFactory(factoryKey);
  }

  /**
   * @see org.springframework.beans.factory.BeanNameAware#setBeanName(java.lang.String)
   */
  public void setBeanName(String name) {
	  logger.debug("setBeanName "+ name);
	factoryLocator.setBeanName(name);
    this.factoryKey = name;
  }

  /**
   * @see org.springframework.beans.factory.DisposableBean#destroy()
   */
  public void destroy() throws Exception {
    // trigger locator cleanup
    reference.release();
    if(dropSchema && hasPersistenceService) {
      logger.info("dropping schema");
      jbpmConfiguration.dropSchema(contextName);
    }
  }

  /**
   * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
   */
  public void afterPropertiesSet() throws Exception {
    if(configuration == null && objectFactory == null)
      throw new IllegalArgumentException(
              "configuration or objectFactory property need to be not null");
    ObjectFactory jbpmObjectFactory;
    // 1. create the configuration from the file
    logger.debug("afterPropertiesSet ");

    if(configuration != null) {
      logger.info("creating JbpmConfiguration from resource "
              + configuration.getDescription());
      InputStream stream = configuration.getInputStream();
      jbpmObjectFactory = ObjectFactoryParser.parseInputStream(stream);
      stream.close();
    }
    else jbpmObjectFactory = objectFactory;

    jbpmConfiguration = createJbpmConfiguration(jbpmObjectFactory);
    // 2. inject the HB session factory if it is the case
    if(sessionFactory != null) {
      logger.info("using given Hibernate session factory");
      JbpmContext context = jbpmConfiguration.createJbpmContext(contextName);
      try {
        context.setSessionFactory(sessionFactory);
      }
      finally {
        context.close();
      }
    }
    // 3. execute persistence operations
    hasPersistenceService = JbpmUtils.hasPersistenceService(jbpmConfiguration,
            contextName);
    if(hasPersistenceService) {
      logger.info("persistence service available...");
      if(createSchema) {
        logger.info("creating schema");
        jbpmConfiguration.createSchema(contextName);
      }
      if(processDefinitionsResources != null) {
        JbpmContext context = jbpmConfiguration.createJbpmContext(contextName);

        try {
  
          if(processDefinitionsResources != null) {
            String toString = Arrays.asList(processDefinitionsResources)
                    .toString();
            logger.info("deploying process definitions (from resources):"
                    + toString);
            for(int i = 0; i < processDefinitionsResources.length; i++) {
            	logger.info("uploading process from: "+ processDefinitionsResources[i]);
        			ZipInputStream zipInputStream = new ZipInputStream(
        					new FileInputStream(processDefinitionsResources[i].getFile()));
        			ProcessDefinition processDefinition = ProcessDefinition
        					.parseParZipInputStream(zipInputStream);
        			String name = processDefinition.getName();
        			String description = processDefinition.getDescription();
        			
        			ProcessDefinition existingProcessDefinition = context.getGraphSession().findLatestProcessDefinition(name);
        			String existingDescription = "defaultDescription";
        			if(existingProcessDefinition!=null){
        			   existingDescription = existingProcessDefinition.getDescription();
        			}
        			logger.info("existing description: "+existingDescription + "; new description: "+ description);
        			if(!existingDescription.equals(description)){
        				logger.info("DEPLOY!");
        				context.deployProcessDefinition(processDefinition);	
        			}
        			else {
        				logger.info("DO NOT DEPLOY!");
        			}
            }
          }
        }
        finally {
          context.close();
        }
      }
    }
    else {
      logger
              .info("persistence unavailable not available - schema create/drop and process definition deployment disabled");
    }

  }

  /**
   * @see org.springframework.beans.factory.FactoryBean#getObject()
   */
  public Object getObject() throws Exception {
    return jbpmConfiguration;
  }

  /**
   * @see org.springframework.beans.factory.FactoryBean#getObjectType()
   */
  public Class getObjectType() {
    return JbpmConfiguration.class;
  }

  /**
   * @see org.springframework.beans.factory.FactoryBean#isSingleton()
   */
  public boolean isSingleton() {
    return true;
  }

  /**
   * @return Returns the configuration.
   */
  public Resource getConfiguration() {
    return configuration;
  }

  /**
   * @param configuration The configuration to set.
   */
  public void setConfiguration(Resource configuration) {
    this.configuration = configuration;
  }

  /**
   * @return Returns the objectFactory.
   */
  public ObjectFactory getObjectFactory() {
    return objectFactory;
  }

  /**
   * @param objectFactory The objectFactory to set.
   */
  public void setObjectFactory(ObjectFactory objectFactory) {
    this.objectFactory = objectFactory;
  }

  /**
   * @return Returns the contextName.
   */
  public String getContextName() {
    return contextName;
  }

  /**
   * @param contextName The contextName to set.
   */
  public void setContextName(String contextName) {
    this.contextName = contextName;
  }

  /**
   * @return Returns the createSchema.
   */
  public boolean isCreateSchema() {
    return createSchema;
  }

  /**
   * @param createSchema The createSchema to set.
   */
  public void setCreateSchema(boolean createSchema) {
    this.createSchema = createSchema;
  }

  /**
   * @return Returns the dropSchema.
   */
  public boolean isDropSchema() {
    return dropSchema;
  }

  /**
   * @param dropSchema The dropSchema to set.
   */
  public void setDropSchema(boolean dropSchema) {
    this.dropSchema = dropSchema;
  }


  /**
   * @return Returns the sessionFactory.
   */
  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  /**
   * @param sessionFactory The sessionFactory to set.
   */
  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  /**
   * @return Returns the processDefinitionsResources.
   */
  public Resource[] getProcessDefinitionsResources() {
    return processDefinitionsResources;
  }

  /**
   * Used for loading the process definition from resources when the
   * configuration is created. This method is an alternative to
   * ProcesssDefinitionFactoryBean since when dealing with sub processes
   * (inside the definitions), jBPM requires a JbpmContext to be active
   * on its internal static stack.
   *
   * @param processDefinitionsResources The processDefinitionsResources
   *          to set.
   */
  public void setProcessDefinitionsResources(
          Resource[] processDefinitionsResources) {
    this.processDefinitionsResources = processDefinitionsResources;
  }

  /**
   * @return Returns the factoryLocator.
   */
  protected JbpmFactoryLocator getFactoryLocator() {
    return factoryLocator;
  }

  protected static JbpmConfiguration createJbpmConfiguration(
			ObjectFactory objectFactory) {
		JbpmConfiguration jbpmConfiguration = new JbpmConfiguration(
				objectFactory);
        logger.info("Creating JbpmConfiguration");
		JbpmConfiguration.Configs.setDefaultObjectFactory(objectFactory);
		// now we make the bean jbpm.configuration always available
		if (objectFactory instanceof ObjectFactoryImpl) {
			logger.info("objectFactory is ObjectFactoryImpl");
			ObjectFactoryImpl objectFactoryImpl = (ObjectFactoryImpl) objectFactory;
			ObjectInfo jbpmConfigurationInfo = new ValueInfo(
					"jbpmConfiguration", jbpmConfiguration);
			objectFactoryImpl.addObjectInfo(jbpmConfigurationInfo);
			logger.info("jbpmConfigurationInfo is added to ObjectFactoryImpl");
		}
		else {
			logger.info("objectFactory is not ObjectFactoryImpl");
		}

		return jbpmConfiguration;
	}



}
