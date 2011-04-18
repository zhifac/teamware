package gleam.executive.workflow.jms;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;

public class MessageProducer {

	protected final Log log = LogFactory.getLog(getClass());

	private JmsTemplate template;

	private Destination destination;

	public JmsTemplate getTemplate() {
		return template;
	}

	public void setTemplate(JmsTemplate template) {
		this.template = template;
	}

	public Destination getDestination() {
		return destination;
	}

	public void setDestination(Destination destination) {
		this.destination = destination;
	}

	public void sendMessage(Object message) {
		log.debug("Simulate callback start");
		template.convertAndSend(destination, message, new MessagePostProcessor() {
			public Message postProcessMessage(Message message)
					throws JMSException {
				message.setStringProperty("type", "callback");
				log.debug("Simulate callback end");
				return message;
			}
		});
    }
}
