/*
 *  EmailSender.java
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
package gleam.executive.workflow.jms;

import gleam.executive.service.MailEngine;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.mail.SimpleMailMessage;

public class EmailSender implements MessageListener {

	protected final Log log = LogFactory.getLog(getClass());

	private JmsTemplate template;

	private Destination destination;

	private MailEngine mailEngine;

	public MailEngine getMailEngine() {
		return mailEngine;
	}

	public void setMailEngine(MailEngine mailEngine) {
		this.mailEngine = mailEngine;
	}

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

	public void onMessage(Message message) {
		String type = null;
		try {
            log.debug("&&&&&&&&&&&& Consuming message");
			Object msg = ((ObjectMessage) message).getObject();
			type = ((ObjectMessage) message).getStringProperty("type");
			log.debug("JMS Message is of type " + type);
			if ("email".equals(type)) {
				log.debug("Sending email ...");
				SimpleMailMessage smm = (SimpleMailMessage) msg;
				Enumeration em = message.getPropertyNames();
				Map<String, String> model = new HashMap<String, String>();
				while (em.hasMoreElements()) {
					String key = (String) em.nextElement();
					String value = message.getStringProperty(key);
					model.put(key, value);
				}
				mailEngine.sendMessage(smm, message
						.getStringProperty("templateName"), model);
			}

		} catch (JMSException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

	}
}
