package gleam.executive.workflow.jms;

import gleam.executive.service.callback.ExecutiveCallbackService;
import gleam.executive.service.callback.ExecutiveServiceCallbackException;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jms.core.JmsTemplate;



public class MessageConsumer implements MessageListener {

  private ExecutiveCallbackService executiveCallbackService;
  protected final Log log = LogFactory.getLog(getClass());

  private String threshold;

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


  public void onMessage(Message message) {

	  String type = null;
		 try {
	   
    	  type = ((TextMessage)message).getStringProperty("type");
    	  log.debug("message is TextMessage ");
    	  if("callback".equals(type)){
          	log.debug("Simulate callback ...");
          	Thread.sleep(4000);

          	String taskId = ((TextMessage)message).getText();
          	log.debug("obtained taskId "+taskId);
          	if(shouldItPass()){
          		executiveCallbackService.taskFinished(taskId);
          	}
          	else {
          		executiveCallbackService.taskFailed(taskId, "Forced error. Don't panic!");
          	}

      }

	    }
        catch (JMSException e) {
      	  e.printStackTrace();
            throw new RuntimeException(e);
        }

        catch (ExecutiveServiceCallbackException e) {
      	  e.printStackTrace();
            throw new RuntimeException(e);
        }

        catch(InterruptedException e){
        	e.printStackTrace();
            throw new RuntimeException(e);
        }

  }

  private boolean shouldItPass(){
	  boolean outcome = false;
	  if(Math.random() > Double.valueOf(getThreshold())){
		  outcome = true;
	  }
	  return outcome;
  }

public ExecutiveCallbackService getExecutiveCallbackService() {
	return executiveCallbackService;
}

public void setExecutiveCallbackService(
		ExecutiveCallbackService executiveCallbackService) {
	this.executiveCallbackService = executiveCallbackService;
}

public String getThreshold() {
	return threshold;
}

public void setThreshold(String threshold) {
	this.threshold = threshold;
}



}


