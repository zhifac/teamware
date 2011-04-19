/*
 *  MailEngine.java
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
package gleam.executive.service;

import java.util.Map;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.VelocityException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.ui.velocity.VelocityEngineUtils;

/**
 * Class for sending e-mail messages based on Velocity templates or with
 * attachments.
 * 
 * <p>
 * <a href="MailEngine.java.html"><i>View Source</i></a>
 * </p>
 * 
 * @author Matt Raible
 */
public class MailEngine {
  protected static final Log log = LogFactory.getLog(MailEngine.class);

  private MailSender mailSender;

  private VelocityEngine velocityEngine;

  public void setMailSender(MailSender mailSender) {
    this.mailSender = mailSender;
  }

  public void setVelocityEngine(VelocityEngine velocityEngine) {
    this.velocityEngine = velocityEngine;
  }

  /**
   * Send a simple message based on a Velocity template.
   * 
   * @param msg
   * @param templateName
   * @param model
   */
  public void sendMessage(SimpleMailMessage msg, String templateName, Map model) {
    String result = null;
    try {
      //the template must be in the classpath
      result = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine,
              templateName, model);
    }
    catch(VelocityException e) {
      e.printStackTrace();
    }
    msg.setText(result);
    send(msg);
  }

  /**
   * Send a simple message with pre-populated values.
   * 
   * @param msg
   */
  public void send(SimpleMailMessage msg) {
    try {
      mailSender.send(msg);
    }
    catch(MailException ex) {
      // log it and go on
      log.error(ex.getMessage());
    }
  }

  /**
   * Convenience method for sending messages with attachments.
   * 
   * @param emailAddresses
   * @param resource
   * @param bodyText
   * @param subject
   * @param attachmentName
   * @throws MessagingException
   * @author Ben Gill
   */
  public void sendMessage(String[] emailAddresses, ClassPathResource resource,
          String bodyText, String subject, String attachmentName)
          throws MessagingException {
    MimeMessage message = ((JavaMailSenderImpl)mailSender).createMimeMessage();
    // use the true flag to indicate you need a multipart message
    MimeMessageHelper helper = new MimeMessageHelper(message, true);
    helper.setTo(emailAddresses);
    helper.setText(bodyText);
    helper.setSubject(subject);
    helper.addAttachment(attachmentName, resource);
    ((JavaMailSenderImpl)mailSender).send(message);
  }
}
