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
