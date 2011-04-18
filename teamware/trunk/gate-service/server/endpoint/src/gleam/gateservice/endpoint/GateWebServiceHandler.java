/*
 * This file is part of GLEAM, licenced under the GNU Lesser General Public
 * Licence.
 *
 * (c) 2006 The University of Sheffield
 * 
 * $Id$
 */
package gleam.gateservice.endpoint;

import gleam.gateservice.definition.GateServiceDefinition;
import gleam.gateservice.message.ErrorMessageBody;
import gleam.gateservice.message.GATEModeMessageBody;
import gleam.gateservice.message.GLEAMModeMessageBody;
import gleam.gateservice.message.InlineAnnotationMessageBody;
import gleam.gateservice.message.InlineAnnotationSuccessMessageBody;
import gleam.gateservice.message.RequestMessageBody;
import gleam.gateservice.message.SuccessMessageBody;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TemporaryQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Actual implementation of the GaS endpoint. Objects of this class
 * maintain a single connection to a JMS broker, but each thread gets
 * its own Session and MessageProducer, so the object is safe for multi
 * threaded use.
 */
public class GateWebServiceHandler implements GateWebService,
                                  InlineAnnotationService {

  private static final Log log = LogFactory.getLog(GateWebServiceHandler.class);

  /**
   * The service definition. Configured by Spring.
   */
  private GateServiceDefinition serviceDefinition;

  /**
   * Connection factory for JMS. Configured by Spring.
   */
  private ConnectionFactory jmsConnectionFactory;

  /**
   * JMS queue used by the service. Configured by Spring.
   */
  private Destination queue;

  /**
   * How long (in milliseconds) to wait for a response in GATE mode
   * before giving up. The default value, 0, means to wait indefinitely.
   */
  private long gateModeResponseTimeout = 0;

  /**
   * Should missing annotation set mappings be treated as the identity
   * mapping (true, default) or simply left unspecified (false). If
   * false, missing mappings will cause asynchronous calls to fail.
   */
  private boolean missingMappingsDefaultToIdentity = true;

  /**
   * JMS connection, created lazily from the connection factory.
   */
  private Connection jmsConnection;

  /**
   * JMS session, created lazily from the connection.
   */
  private ThreadLocal<Session> jmsSession = new ThreadLocal<Session>();

  /**
   * Persistent JMS message producer, created lazily from the session.
   */
  private ThreadLocal<MessageProducer> persistentJmsProducer =
          new ThreadLocal<MessageProducer>();

  /**
   * Non-persistent JMS message producer, created lazily from the
   * session.
   */
  private ThreadLocal<MessageProducer> nonPersistentJmsProducer =
          new ThreadLocal<MessageProducer>();

  public GateServiceDefinition getServiceDefinition() {
    return serviceDefinition;
  }

  public void setServiceDefinition(GateServiceDefinition serviceDefinition) {
    this.serviceDefinition = serviceDefinition;
  }

  public ConnectionFactory getJmsConnectionFactory() {
    return jmsConnectionFactory;
  }

  public void setJmsConnectionFactory(ConnectionFactory jmsConnectionFactory) {
    this.jmsConnectionFactory = jmsConnectionFactory;
  }

  public Destination getQueue() {
    return queue;
  }

  public void setQueue(Destination queue) {
    this.queue = queue;
  }

  public long getGateModeResponseTimeout() {
    return gateModeResponseTimeout;
  }

  public void setGateModeResponseTimeout(long gateModeResponseTimeout) {
    this.gateModeResponseTimeout = gateModeResponseTimeout;
  }

  public boolean isMissingMappingsDefaultToIdentity() {
    return missingMappingsDefaultToIdentity;
  }

  public void setMissingMappingsDefaultToIdentity(
          boolean missingMappingsDefaultToIdentity) {
    this.missingMappingsDefaultToIdentity = missingMappingsDefaultToIdentity;
  }

  /**
   * Returns the names of all the annotation sets used for input by this
   * service.
   */
  public List<String> getInputAnnotationSetNames() {
    if(log.isTraceEnabled()) {
      log.trace("Entering getInputAnnotationSetNames");
    }
    try {
      Set<String> names = serviceDefinition.getInputAnnotationSetNames();
      return Arrays.asList(names.toArray(new String[names.size()]));
    }
    finally {
      log.trace("Leaving getInputAnnotationSetNames");
    }
  }

  /**
   * Returns the names of all optional parameters for this service.
   */
  public List<String> getOptionalParameterNames() {
    if(log.isTraceEnabled()) {
      log.trace("Entering getOptionalParameterNames");
    }
    try {
      Set<String> names = serviceDefinition.getOptionalParameterNames();
      return Arrays.asList(names.toArray(new String[names.size()]));
    }
    finally {
      log.trace("Leaving getOptionalParameterNames");
    }
  }

  /**
   * Returns the names of all annotation sets output by this service.
   * One of these names may be <code>null</code>, indicating the
   * unnamed default annotation set.
   */
  public List<String> getOutputAnnotationSetNames() {
    if(log.isTraceEnabled()) {
      log.trace("Entering getOutputAnnotationSetNames");
    }
    try {
      Set<String> names = serviceDefinition.getOutputAnnotationSetNames();
      return Arrays.asList(names.toArray(new String[names.size()]));
    }
    finally {
      log.trace("Leaving getOutputAnnotationSetNames");
    }
  }

  /**
   * Returns the names of all required parameters for this service.
   */
  public List<String> getRequiredParameterNames() {
    if(log.isTraceEnabled()) {
      log.trace("Entering getRequiredParameterNames");
    }
    try {
      Set<String> names = serviceDefinition.getRequiredParameterNames();
      return Arrays.asList(names.toArray(new String[names.size()]));
    }
    finally {
      log.trace("Leaving getRequiredParameterNames");
    }
  }

  /**
   * GATE mode processing. Packages up the request into a JMS message,
   * sends the message to the worker queue, waits for a response and
   * returns the response to the caller. Any JMS exceptions raised cause
   * a {@link GateWebServiceFault} to be thrown.
   */
  public List<AnnotationSetData> processDocument(byte[] documentXml,
          String encoding, List<ParameterValue> parameterValues)
          throws GateWebServiceFault {
    if(log.isTraceEnabled()) {
      log.trace("Entering processDocument");
    }
    try {
      // create message body
      String documentContent = new String(documentXml, encoding);
      GATEModeMessageBody messageBody =
              new GATEModeMessageBody(documentContent);
      if(parameterValues != null) {
        for(ParameterValue param : parameterValues) {
          messageBody.addParameterValue(param.getName(), param.getValue());
        }
      }

      Object responseMessageBody = doRequestResponseCall(messageBody);

      // finally, we have the response message
      if(responseMessageBody instanceof SuccessMessageBody) {
        // success!
        Map<String, byte[]> annotationSetsToReturn =
                ((SuccessMessageBody)responseMessageBody).getAnnotationSets();
        List<AnnotationSetData> response =
                new ArrayList<AnnotationSetData>(annotationSetsToReturn.size());
        for(Map.Entry<String, byte[]> aSet : annotationSetsToReturn.entrySet()) {
          AnnotationSetData d = new AnnotationSetData();
          d.setName(aSet.getKey());
          d.setXmlData(aSet.getValue());
          response.add(d);
        }

        return response;
      }
      else {
        // unknown failure
        log.error("Unknown error - response message contains unrecognised "
                + "object of type " + responseMessageBody.getClass());
        throw new GateWebServiceFault(
                "Unrecognised response message from worker");
      }
    }
    catch(UnsupportedEncodingException uex) {
      log.error("Unsupported encoding in processDocument", uex);
      throw new GateWebServiceFault("Unsupported encoding name " + encoding);
    }
    catch(IOException iox) {
      log.error("IOException converting document data to String", iox);
      throw new GateWebServiceFault("Error reading documentXml");
    }
    catch(RuntimeException rx) {
      log.error("RuntimeException in processDocument", rx);
      throw rx;
    }
    finally {
      log.trace("Leaving processDocument");
    }
  }

  /**
   * Inline annotation. Packages up the request into a JMS message,
   * sends the message to the worker queue, waits for a response and
   * returns the response to the caller. Any JMS exceptions raised cause
   * a {@link GateWebServiceFault} to be thrown.
   */
  public String annotate(String documentContent,
          List<ParameterValue> parameterValues) throws GateWebServiceFault {
    if(log.isTraceEnabled()) {
      log.trace("Entering annotate");
    }
    try {
      // create message body
      InlineAnnotationMessageBody messageBody =
              new InlineAnnotationMessageBody(documentContent);
      if(parameterValues != null) {
        for(ParameterValue param : parameterValues) {
          messageBody.addParameterValue(param.getName(), param.getValue());
        }
      }

      Object responseMessageBody = doRequestResponseCall(messageBody);

      if(responseMessageBody instanceof InlineAnnotationSuccessMessageBody) {
        return ((InlineAnnotationSuccessMessageBody)responseMessageBody)
                .getAnnotatedDocument();
      }
      else {
        // unknown failure
        log.error("Unknown error - response message contains unrecognised "
                + "object of type " + responseMessageBody.getClass());
        throw new GateWebServiceFault(
                "Unrecognised response message from worker");
      }
    }
    catch(RuntimeException rx) {
      log.error("RuntimeException in annotate", rx);
      throw rx;
    }
    finally {
      log.trace("Leaving processDocument");
    }
  }

  /**
   * Code to do a request/response call to a GaS worker. This is the
   * logic shared by {@link #processDocument} and {@link #annotate}.
   * 
   * @param messageBody the request message body to send
   * @return the response message body
   * @throws GateWebServiceFault if a JMS exception occurs
   */
  private Object doRequestResponseCall(RequestMessageBody messageBody)
          throws GateWebServiceFault {
    Session session = null;
    MessageProducer producer = null;
    try {
      session = getJMSSession();
      // request-response messages should be non-persistent - no point
      // retrying if the server crashes.
      producer = getNonPersistentJMSProducer();
    }
    catch(JMSException jmsx) {
      log.error("Error creating JMS session or producer", jmsx);
      throw new GateWebServiceFault("Internal error", jmsx);
    }

    // create message
    ObjectMessage message = null;
    try {
      message = session.createObjectMessage(messageBody);
    }
    catch(JMSException e) {
      log.error("Exception creating request message", e);
      throw new GateWebServiceFault("Internal error", e);
    }

    // Setup reply queue
    TemporaryQueue replyQueue = null;
    try {
      replyQueue = session.createTemporaryQueue();
      message.setJMSReplyTo(replyQueue);
    }
    catch(JMSException e) {
      log.error("Exception creating reply queue for request", e);
      throw new GateWebServiceFault("Internal error", e);
    }

    try {
      producer.send(message);
    }
    catch(JMSException e) {
      log.error("Exception sending request message", e);
      throw new GateWebServiceFault("Internal error", e);
    }

    Object responseMessageBody = null;
    MessageConsumer replyConsumer = null;
    try {
      replyConsumer = session.createConsumer(replyQueue);
      Message replyMessage = replyConsumer.receive(gateModeResponseTimeout);
      if(replyMessage == null) {
        // timeout or connection close
        log.error("No response message received within timeout "
                + gateModeResponseTimeout);
        throw new GateWebServiceFault("No reply received from worker");
      }
      else if(replyMessage instanceof ObjectMessage) {
        if(message.getJMSMessageID().equals(replyMessage.getJMSCorrelationID())) {
          responseMessageBody = ((ObjectMessage)replyMessage).getObject();
        }
        else {
          log.error("Response message correlation ID does not match "
                  + "request message ID");
          throw new GateWebServiceFault("Unexpected reply message from worker");
        }
      }
      else {
        log.error("Reply message not an ObjectMessage");
        throw new GateWebServiceFault("Wrong type of reply message from worker");
      }
    }
    catch(JMSException e) {
      log.error("Exception getting response message from worker", e);
      throw new GateWebServiceFault("Internal error", e);
    }
    finally {
      try {
        replyConsumer.close();
        replyQueue.delete();
      }
      catch(JMSException e) {
        log.error("Error cleaning up temporary queue", e);
        throw new GateWebServiceFault("Internal error", e);
      }
    }

    // throw an exception if we have an error message
    if(responseMessageBody instanceof ErrorMessageBody) {
      // known failure
      ErrorMessageBody emb = (ErrorMessageBody)responseMessageBody;
      log.error("Error in worker: " + emb.getMessage(), emb.getException());
      throw new GateWebServiceFault(emb.getMessage(), emb.getException());
    }

    return responseMessageBody;
  }

  /**
   * Delegates to {@link #processRemoteDocumentsImpl} with a single
   * task.
   */
  public void processRemoteDocument(URI executiveLocation, String taskId,
          URI docServiceLocation, String docId,
          List<AnnotationSetMapping> annotationSets,
          List<ParameterValue> parameterValues) throws GateWebServiceFault {
    if(log.isTraceEnabled()) {
      log.trace("Entering processRemoteDocument.  executiveLocation = "
              + executiveLocation + ", taskID = " + taskId
              + ", docServiceLocation = " + docServiceLocation + ", docId = "
              + docId);
    }
    try {
      this.processRemoteDocumentsImpl(executiveLocation, taskId,
              docServiceLocation, Collections.singletonList(new AnnotationTask(
                      docId, annotationSets)), parameterValues);
    }
    finally {
      log.trace("Leaving processRemoteDocument");
    }
  }

  /**
   * Delegates to {@link #processRemoteDocumentsImpl} passing a list of
   * tasks.
   */
  public void processRemoteDocuments(URI executiveLocation, String taskId,
          URI docServiceLocation, List<AnnotationTask> tasks,
          List<ParameterValue> parameterValues) throws GateWebServiceFault {
    if(log.isTraceEnabled()) {
      log.trace("Entering processRemoteDocuments.  executiveLocation = "
              + executiveLocation + ", taskID = " + taskId
              + ", docServiceLocation = " + docServiceLocation + ", tasks = "
              + tasks);
    }
    try {
      this.processRemoteDocumentsImpl(executiveLocation, taskId,
              docServiceLocation, tasks, parameterValues);
    }
    finally {
      log.trace("Leaving processRemoteDocuments");
    }
  }

  /**
   * GLEAM mode processing. Packages up the request into a JMS message
   * and sends the message to the worker queue. Any JMS exceptions
   * raised cause a {@link GateWebServiceFault} to be thrown. In this
   * mode we do not wait for a response, as success or failure is
   * communicated direct to the executive by the worker.
   */
  private void processRemoteDocumentsImpl(URI executiveLocation, String taskId,
          URI docServiceLocation, List<AnnotationTask> tasks,
          List<ParameterValue> parameterValues) throws GateWebServiceFault {
    try {
      // create message body
      GLEAMModeMessageBody messageBody =
              new GLEAMModeMessageBody(executiveLocation, taskId,
                      docServiceLocation);

      // add parameters
      if(parameterValues != null) {
        for(ParameterValue param : parameterValues) {
          messageBody.addParameterValue(param.getName(), param.getValue());
        }
      }

      for(AnnotationTask t : tasks) {
        GLEAMModeMessageBody.Task task = messageBody.createTask(t.getDocId());

        // add AS mappings
        if(t.getAnnotationSets() != null) {
          for(AnnotationSetMapping as : t.getAnnotationSets()) {
            task.addAnnotationSetMapping(as.getGateServiceASName(), as
                    .getDocServiceASName());
          }
        }

        if(isMissingMappingsDefaultToIdentity()) {
          // add identity mappings for any annotation set names defined
          // by the service definition but not specified in the call
          for(String as : serviceDefinition.getInputAnnotationSetNames()) {
            if(!task.getAnnotationSetMappings().containsKey(as)) {
              task.addAnnotationSetMapping(as, as);
            }
          }
          for(String as : serviceDefinition.getOutputAnnotationSetNames()) {
            if(!task.getAnnotationSetMappings().containsKey(as)) {
              task.addAnnotationSetMapping(as, as);
            }            
          }
        }
      }

      Session session = null;
      MessageProducer producer = null;
      try {
        session = getJMSSession();
        // gleam-mode messages should be persistent to survive crashes
        producer = getPersistentJMSProducer();
      }
      catch(JMSException jmsx) {
        log.error("Error creating JMS session or producer", jmsx);
        throw new GateWebServiceFault("Internal error", jmsx);
      }

      // create message
      ObjectMessage message = null;
      try {
        message = session.createObjectMessage(messageBody);
      }
      catch(JMSException e) {
        log.error("Exception creating GLEAM mode request message", e);
        throw new GateWebServiceFault("Internal error", e);
      }

      // send message
      try {
        producer.send(message);
      }
      catch(JMSException e) {
        log.error("Exception sending request message", e);
        throw new GateWebServiceFault("Internal error", e);
      }
    }
    catch(RuntimeException rx) {
      log.error("RuntimeException in processRemoteDocument", rx);
      throw rx;
    }
  }

  /**
   * Returns the current JMS session, creating a connection and session
   * if necessary.
   * 
   * @throws JMSException if an error occurs creating the session.
   */
  private Session getJMSSession() throws JMSException {
    if(jmsSession.get() == null) {
      synchronized(jmsConnectionFactory) {
        if(jmsConnection == null) {
          log.debug("Creating JMS connection");
          jmsConnection = jmsConnectionFactory.createConnection();
          jmsConnection.start();
        }
      }
      log.debug("Creating JMS session");
      jmsSession.set(jmsConnection.createSession(false,
              Session.AUTO_ACKNOWLEDGE));
    }

    return jmsSession.get();
  }

  /**
   * Returns the current JMS persistent message producer, creating one
   * if necessary.
   * 
   * @throws JMSException if an error occurs creating the producer.
   */
  private MessageProducer getPersistentJMSProducer() throws JMSException {
    if(persistentJmsProducer.get() == null) {
      Session session = getJMSSession();
      log.debug("Creating JMS message producer");
      MessageProducer p = session.createProducer(getQueue());
      p.setDeliveryMode(DeliveryMode.PERSISTENT);
      persistentJmsProducer.set(p);
    }

    return persistentJmsProducer.get();
  }

  /**
   * Returns the current JMS non-persistent message producer, creating
   * one if necessary.
   * 
   * @throws JMSException if an error occurs creating the producer.
   */
  private MessageProducer getNonPersistentJMSProducer() throws JMSException {
    if(nonPersistentJmsProducer.get() == null) {
      Session session = getJMSSession();
      log.debug("Creating JMS message producer");
      MessageProducer p = session.createProducer(getQueue());
      p.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
      nonPersistentJmsProducer.set(p);
    }

    return nonPersistentJmsProducer.get();
  }

  /**
   * Close all JMS resources. Should be declared as the destroy-method
   * in Spring.
   */
  protected void shutdown() throws Throwable {
    if(log.isTraceEnabled()) {
      log.trace("Entering shutdown");
    }
    try {
      if(jmsConnection != null) {
        log.debug("Closing JMS connection");
        jmsConnection.close();
      }
    }
    finally {
      log.trace("Leaving shutdown");
    }
  }
}
