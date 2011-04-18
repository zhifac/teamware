package gleam.executive.webapp.action;

import gleam.executive.Constants;
import gleam.executive.model.User;
import gleam.executive.security.SaltWithIterations;
import gleam.executive.service.RoleManager;
import gleam.executive.service.UserExistsException;
import gleam.executive.service.UserManager;
import gleam.executive.util.ExcelUtil;
import gleam.executive.webapp.form.BulkUploadUserForm;
import gleam.executive.webapp.util.RequestUtil;
import gleam.executive.workflow.jms.EmailProducer;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acegisecurity.providers.encoding.PasswordEncoder;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.upload.FormFile;
import org.apache.struts.util.MessageResources;
import org.springframework.mail.SimpleMailMessage;

/**
 * 
 * @author <a href="agaton@dcs.shef.ac.uk">Milan Agatonovic</a>
 * 
 * @struts.action name="bulkUploadUserForm" path="/bulkSave" scope="request"
 *                validate="true" input="failure"
 * @struts.action-set-property property="cancellable" value="true"
 * @struts.action-forward name="failure"
 *                        path="/WEB-INF/pages/bulkUploadUserForm.jsp"
 * @struts.action-forward name="success"
 *                        path="/WEB-INF/pages/bulkUploadUserInfo.jsp"
 */

public class BulkUploadUserAction extends BaseAction {
	protected final Log log = LogFactory.getLog(getClass());

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		log.debug("entered BULK UPLOAD action");
		BulkUploadUserForm theForm = (BulkUploadUserForm) form;
	    boolean sendEmail = false;
		if(request.getParameter("sendEmail")!=null) {
			sendEmail = true;
		}
		
		boolean receiveCopy = false;
		if(request.getParameter("receiveCopy")!=null) {
			receiveCopy = true;
		}

		// retrieve the file representation
		FormFile file = theForm.getFile();

		if (file == null) {
			return mapping.findForward("failure");
		}

		// retrieve the file data
		InputStream is = file.getInputStream();

		ActionMessages errors = new ActionMessages();
		List<String> uploadedUsers = new ArrayList<String>();
		List<User> users = ExcelUtil.populateUser(is);
		if(users.size() > 0) {
			
			UserManager userManager = (UserManager) getBean("userManager");
			RoleManager roleManager = (RoleManager) getBean("roleManager");
	
			Iterator<User> it = users.iterator();
			while (it.hasNext()) {
				User user = it.next();
				Set<String> roleNames = user.getRoleNames();
				Iterator<String> itr = roleNames.iterator();
				while (itr.hasNext()) {
					String roleName = itr.next();
					if(roleManager.getRole(roleName.trim())!=null){
					  user.addRole(roleManager.getRole(roleName.trim()));
					}
				}
				try {
					log.debug("saving user: " + user.getFullName());
	        user.setConfirmPassword(user.getPassword());
	
					if (StringUtils.equals(request.getParameter("encryptPass"),
							"true")) {
					  userManager.setUserPassword(user, user.getPassword());
					}
					userManager.saveUser(user);
					uploadedUsers.add(user.getFullName());
					if(sendEmail){
						if(receiveCopy){
							String adminUsername = request.getRemoteUser();
							User admin = userManager.getUserByUsername(adminUsername);
					        sendEmail(request, user, admin);
						}
						else {
							sendEmail(request, user, null);
						}
					}
					else {
						log.debug("do not send email");
					}
				} catch (UserExistsException e) {
					log.warn(e.getMessage());
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
							"errors.existing.user", user.getUsername(), user
									.getEmail()));
					
				}
			}
			
			// return a forward to uploadDisplay.jsp
			request.setAttribute("uploadedUsers", uploadedUsers);
			
			ActionMessages messages = new ActionMessages();
			messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
	         "user.bulkUpload.success"));
			saveMessages(request.getSession(), messages);
			
		}	else {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
			"error.bulkUpload.failure"));
		}
		
		if (errors.size() > 0) {
			saveErrors(request, errors);
			return mapping.findForward("failure");
		}

		return mapping.findForward("success");
	}

	public ActionForward cancel(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		log.debug("CANCEL detected");
		return mapping.findForward("viewUsers");
	}

	private void sendEmail(HttpServletRequest request, User user, User admin)
			throws Exception {
		// Send user an e-mail
		if (log.isDebugEnabled()) {
			log.debug("Sending user '" + user.getUsername()
					+ "' an account information e-mail");
		}
		EmailProducer mProducer = (EmailProducer)getBean("emailProducer");
		MessageResources resources = getResources(request);
		SimpleMailMessage message = (SimpleMailMessage) getBean("mailMessage");

		message.setTo(user.getFullName() + "<" + user.getEmail() + ">");
		
		if(admin!=null){
			if (log.isDebugEnabled()) {
				log.debug("Sending admin '" + admin.getUsername()
						+ "' a copy of an e-mail");
			}	
		  message.setBcc(admin.getFullName() + "<" + admin.getEmail() + ">");
		}

		message.setSubject(resources.getMessage("newuser.email.subject"));
		Map<String, String> model = new HashMap<String, String>();
		model.put("title", resources.getMessage("newuser.email.title"));
		model.put("content", resources.getMessage("newuser.email.content"));
		model.put("userName", user.getUsername());
		model.put("confirmPassword", user.getConfirmPassword());
		model.put("applicationURL", 
				request.getSession().getServletContext().getAttribute("urlbase").toString());

		mProducer.sendMessage(message, "accountAdded.vm", model);

	}
}
