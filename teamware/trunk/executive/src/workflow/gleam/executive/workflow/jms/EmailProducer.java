package gleam.executive.workflow.jms;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;

public class EmailProducer {

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

	public void sendMessage(Object message, final String templateName,
			final Map model) {
		template.convertAndSend(destination, message, new MessagePostProcessor() {
			public Message postProcessMessage(Message message)
					throws JMSException {
				message.setStringProperty("type", "email");
				message.setStringProperty("templateName", templateName);
				if (model != null) {
					Set keys = model.keySet();
					Iterator it = keys.iterator();
					while (it.hasNext()) {
						String key = (String) it.next();
						String value = (String) model.get(key);
						message.setStringProperty(key, value);
					}
				}
				return message;
			}
		});

	}
}
