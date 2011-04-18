package gleam.util.logging;

import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Really simple message handler that takes log4j LoggingEvents and
 * passes them to the local log4j. When used with Spring's
 * "message-driven POJO" support, this class can receive messages sent
 * by a log4j JMSAppender in a remote JVM and log them according to the
 * local log4j configuration.
 * 
 * <pre>
 * &lt;amq:connectionFactory id=&quot;myConnectionFactory&quot; ... /&gt;
 * &lt;amq:topic id=&quot;logTopic&quot; physicalName=&quot;....&quot; /&gt;
 * 
 * &lt;bean id=&quot;log4jReceiver&quot; class=&quot;org.springframework.jms.listener.SimpleMessageListenerContainer&quot;&gt;
 *   &lt;property name=&quot;connectionFactory&quot; ref=&quot;myConnectionFactory&quot; /&gt;
 *   &lt;property name=&quot;destination&quot; ref=&quot;logTopic&quot; /&gt;
 *   &lt;property name=&quot;messageListener&quot;&gt;
 *     &lt;bean class=&quot;org.springframework.jms.listener.adapter.MessageListenerAdapter&quot;&gt;
 *       &lt;constructor-arg&gt;
 *         &lt;bean class=&quot;gleam.util.logging.Log4jEventLogger&quot; /&gt;
 *       &lt;/constructor-arg&gt;
 *     &lt;/bean&gt;
 *   &lt;/property&gt;
 * &lt;/bean&gt;
 * </pre>
 */
public class Log4jEventLogger {
  public void handleMessage(LoggingEvent event) {
    Logger.getLogger(event.getLoggerName()).callAppenders(event);
  }
}
