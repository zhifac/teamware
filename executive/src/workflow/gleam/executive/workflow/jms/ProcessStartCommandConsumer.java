/*
 *  ProcessStartCommandConsumer.java
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

import java.util.regex.Matcher;

import gleam.executive.service.callback.ExecutiveCallbackService;
import gleam.executive.service.callback.ExecutiveServiceCallbackException;
import gleam.executive.workflow.command.ProcessStartCommand;
import gleam.executive.workflow.manager.WorkflowManager;
import gleam.executive.workflow.util.JPDLConstants;
import gleam.executive.workflow.util.WorkflowException;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.graph.exe.ProcessInstance;
import org.springframework.jms.core.JmsTemplate;

public class ProcessStartCommandConsumer implements MessageListener {

	protected final Log log = LogFactory.getLog(getClass());
	private WorkflowManager workflowManager;

	private JmsTemplate template;

	private Destination destination;

	public WorkflowManager getWorkflowManager() {
		return workflowManager;
	}

	public void setWorkflowManager(WorkflowManager workflowManager) {
		this.workflowManager = workflowManager;
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

		String command = null;
		try {

			Object msg = ((ObjectMessage) message).getObject();
			command = ((ObjectMessage) message).getStringProperty("command");
			log.debug("JMS Message is a command " + command);
			if ("startProcess".equals(command)) {
				log.debug("starting timer ...");
				Thread.sleep(1000);
				log.debug("starting process ...");
	
				
				ProcessStartCommand processStartCommand = (ProcessStartCommand)msg;
				log.debug("obtained processStartCommand with key: " + processStartCommand.getKey());
				ProcessInstance pi = workflowManager.createStartProcessInstance(processStartCommand.getProcessDefinitionId(),
				processStartCommand.getVariableMap(),
				processStartCommand.getKey());
			    log.warn("created process: "+pi.getId());
				
				log.debug("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

			}

		} catch (JMSException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		catch (WorkflowException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		catch (Exception e) {
			log.error(e);
		}

	}

}
