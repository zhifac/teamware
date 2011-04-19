/*
 *  WorkerConfig.java
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
package gleam.gateservice.worker;

import gate.CorpusController;
import gleam.docservice.proxy.DocServiceProxyFactory;
import gleam.executive.proxy.ExecutiveProxyFactory;
import gleam.gateservice.definition.GateServiceDefinition;

import java.util.List;
import java.util.ArrayList;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MessageConsumer;
import javax.jms.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * Object defining a set of identical GaS workers.  It is intended to be
 * created via Spring, and will run the workers it defines at Spring
 * application context startup time and shut them down when the context closes.
 */
public class WorkerConfig implements BeanFactoryAware, DisposableBean, InitializingBean {

  private static final Log log = LogFactory.getLog(WorkerConfig.class);

  private BeanFactory beanFactory;

  private int numWorkers = 1;

  private Destination queue;

  private ConnectionFactory connectionFactory;

  private ExecutiveProxyFactory executiveProxyFactory;

  private DocServiceProxyFactory docServiceProxyFactory;

  private GateServiceDefinition serviceDefinition;
  
  private String gateModeInputMimeType = "text/xml";

  private String gateApplicationBeanName;

  public BeanFactory getBeanFactory() {
    return beanFactory;
  }

  public void setBeanFactory(BeanFactory beanFactory) {
    this.beanFactory = beanFactory;
  }

  public ConnectionFactory getConnectionFactory() {
    return connectionFactory;
  }

  public void setConnectionFactory(ConnectionFactory connectionFactory) {
    this.connectionFactory = connectionFactory;
  }

  public DocServiceProxyFactory getDocServiceProxyFactory() {
    return docServiceProxyFactory;
  }

  public void setDocServiceProxyFactory(
          DocServiceProxyFactory docServiceProxyFactory) {
    this.docServiceProxyFactory = docServiceProxyFactory;
  }

  public ExecutiveProxyFactory getExecutiveProxyFactory() {
    return executiveProxyFactory;
  }

  public void setExecutiveProxyFactory(
          ExecutiveProxyFactory executiveProxyFactory) {
    this.executiveProxyFactory = executiveProxyFactory;
  }

  public String getGateApplicationBeanName() {
    return gateApplicationBeanName;
  }

  public void setGateApplicationBeanName(String gateApplicationBeanName) {
    this.gateApplicationBeanName = gateApplicationBeanName;
  }

  public int getNumWorkers() {
    return numWorkers;
  }

  public void setNumWorkers(int numWorkers) {
    this.numWorkers = numWorkers;
  }

  public Destination getQueue() {
    return queue;
  }

  public void setQueue(Destination queue) {
    this.queue = queue;
  }

  public GateServiceDefinition getServiceDefinition() {
    return serviceDefinition;
  }

  public void setServiceDefinition(GateServiceDefinition serviceDefinition) {
    this.serviceDefinition = serviceDefinition;
  }

  public String getGateModeInputMimeType() {
    return gateModeInputMimeType;
  }

  public void setGateModeInputMimeType(String gateModeInputMimeType) {
    this.gateModeInputMimeType = gateModeInputMimeType;
  }

  /**
   * Returns an instance of the CorpusController registered as
   * {@link #gateApplicationBeanName}. This bean must be declared as a
   * non-singleton, so each call to this method returns an independent
   * copy of the application.
   * 
   * @return
   */
  public CorpusController getController() {
    return (CorpusController)beanFactory.getBean(gateApplicationBeanName,
            CorpusController.class);
  }


  //  Methods to handle the actual running of the workers defined

  /**
   * JMS connection used by the workers started by this config.
   */
  private Connection jmsConnection;

  /**
   * The workers themselves.
   */
  private List<GasWorker> workers = new ArrayList<GasWorker>();

  /**
   * Run the workers defined by this configuration.
   */
  public void afterPropertiesSet() throws Exception {
    log.debug("Creating connection");
    jmsConnection = this.getConnectionFactory().createConnection();
    log.debug("Connection created");

    for(int i = 0; i < this.getNumWorkers(); i++) {
      log.debug("Creating worker " + i);
      log.debug("Loading application...");
      CorpusController controller = this.getController();
      log.debug("Application loaded");

      Session jmsSession =
              jmsConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);

      // create worker and inject dependencies
      GasWorker worker = new GasWorker();
      worker.setController(controller);
      worker.setSession(jmsSession);
      worker.setExecutiveProxyFactory(this.getExecutiveProxyFactory());
      worker.setDocServiceProxyFactory(this.getDocServiceProxyFactory());
      worker.setServiceDefinition(this.getServiceDefinition());
      worker.setGateModeInputMimeType(this.getGateModeInputMimeType());
      worker.init();
      workers.add(worker);

      log.debug("Creating MessageConsumer");
      MessageConsumer consumer = jmsSession.createConsumer(this.getQueue());
      consumer.setMessageListener(worker);
    }

    log.debug("Starting connection");
    jmsConnection.start();
  }

  /**
   * Close the JMS connection and shut down the workers we ran.
   */
  public void destroy() throws Exception {
    log.debug("Closing JMS connection");
    if(jmsConnection != null) {
      jmsConnection.close();
    }
    log.debug("Shutting down workers");
    for(GasWorker worker : workers) {
      worker.shutdown();
    }
  }
}
