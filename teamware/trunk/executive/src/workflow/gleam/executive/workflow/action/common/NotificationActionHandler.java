/*
 *  NotificationActionHandler.java
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
package gleam.executive.workflow.action.common;

import gleam.executive.model.User;
import gleam.executive.model.WebAppBean;
import gleam.executive.service.MailEngine;
import gleam.executive.service.UserManager;
import gleam.executive.workflow.jms.EmailProducer;
import gleam.executive.workflow.jms.MessageProducer;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.graph.exe.ExecutionContext;
import org.springframework.mail.MailMessage;
import org.springframework.util.StringUtils;
import gleam.executive.workflow.sm.JbpmHandlerProxy;

public class NotificationActionHandler extends JbpmHandlerProxy {

	private static final long serialVersionUID = 1L;

    /*
     *  <inVarUserCSVList>nextPerformer</inVarUserCSVList>
		   <inVarSubject>New Annotation Task Notification</inVarSubject>
		   <inVarTaskName>annotate</inVarTaskName>
		   <inVarLink>annotatorGUIURL</inVarLink>
		   <inVarTemplate>pendingTask.vm</inVarTemplate>
     */

//	 target variables picked from JPDL
	String inVarUserCSVList;

	String inVarSubject;

	String inVarTaskName;

	String inVarLink;

	String inVarTemplate;

//	 beans and temp variables
	MailEngine mailEngine;
	MailMessage mailMessage;
	UserManager userManager;
	EmailProducer emailProducer;
	WebAppBean webAppBean;

	protected final Log log = LogFactory.getLog(getClass());


	public void execute(ExecutionContext context) throws Exception {

		log.debug("NotificationActionHandler START");
		String userCSVList = (String) context.getVariable(getInVarUserCSVList());
		log.debug("@@@@@@@ userCSVList "+userCSVList);
		String subject = (String) context.getVariable(getInVarSubject());
		log.debug("@@@@@@@ subject "+subject);
		String taskName = (String) context.getVariable(getInVarTaskName());
		log.debug("@@@@@@@ taskName "+taskName);
		String link = (String) context.getVariable(getInVarLink());
		log.debug("@@@@@@@ link "+link);
		String template = (String) context.getVariable(getInVarTemplate());
		log.debug("@@@@@@@ template "+template);

		String[] userNames = StringUtils.commaDelimitedListToStringArray(userCSVList);

		for(int i=0;i<userNames.length;i++){
		    User user = userManager.getUserByUsername(userNames[i]);
            mailMessage.setTo(user.getFullName() + "<" + user.getEmail() + ">");
			mailMessage.setSubject(subject);
			Map<String, String> model = new HashMap<String, String>();

			model.put("userName", user.getFullName());
			model.put("processName", context.getProcessDefinition().getName());

			if(taskName!=null){
			  model.put("taskName", taskName);
			}
			if(link==null || "".equals(link)){
				link = webAppBean.getUrlBase() + "/" + webAppBean.getName();
			}

			model.put("link", link);
			log.debug("link "+link);
			emailProducer.sendMessage(mailMessage, template, model);
		}
		log.debug("NotificationActionHandler END");
	}


	public MailEngine getMailEngine() {
		return mailEngine;
	}

	public void setMailEngine(MailEngine mailEngine) {
		this.mailEngine = mailEngine;
	}

	public MailMessage getMailMessage() {
		return mailMessage;
	}

	public void setMailMessage(MailMessage mailMessage) {
		this.mailMessage = mailMessage;
	}

	public UserManager getUserManager() {
		return userManager;
	}

	public void setUserManager(UserManager userManager) {
		this.userManager = userManager;
	}



	public EmailProducer getEmailProducer() {
		return emailProducer;
	}


	public void setEmailProducer(EmailProducer emailProducer) {
		this.emailProducer = emailProducer;
	}


	public String getInVarLink() {
		return inVarLink;
	}


	public void setInVarLink(String inVarLink) {
		this.inVarLink = inVarLink;
	}


	public String getInVarSubject() {
		return inVarSubject;
	}


	public void setInVarSubject(String inVarSubject) {
		this.inVarSubject = inVarSubject;
	}


	public String getInVarTaskName() {
		return inVarTaskName;
	}


	public void setInVarTaskName(String inVarTaskName) {
		this.inVarTaskName = inVarTaskName;
	}


	public String getInVarTemplate() {
		return inVarTemplate;
	}


	public void setInVarTemplate(String inVarTemplate) {
		this.inVarTemplate = inVarTemplate;
	}


	public String getInVarUserCSVList() {
		return inVarUserCSVList;
	}


	public void setInVarUserCSVList(String inVarUserCSVList) {
		this.inVarUserCSVList = inVarUserCSVList;
	}


	public WebAppBean getWebAppBean() {
		return webAppBean;
	}


	public void setWebAppBean(WebAppBean webAppBean) {
		this.webAppBean = webAppBean;
	}

}

