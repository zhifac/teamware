/*
 *  AnnoSchemaAction.java
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
 * Milan Agatonovic
 *
 *  $Id$
 */
package gleam.executive.webapp.action;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;
import org.springframework.web.context.support.WebApplicationContextUtils;
import gleam.executive.Constants;
import gleam.executive.model.AnnotationSchema;
import gleam.executive.model.WebAppBean;
import gleam.executive.service.AnnotationServiceManager;
import gleam.executive.service.ResourceManager;
import gleam.executive.webapp.action.BaseAction;
import gleam.executive.webapp.form.AnnoSchemaForm;
import gleam.executive.webapp.wrapp.ProcessInstanceWrapper;
import gleam.executive.workflow.manager.WorkflowManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @struts.action name="annoSchemaForm" path="/schemas" scope="request"
 *                validate="false" parameter="method" input="mainMenu"
 * @struts.action name="annoSchemaForm" path="/addSchema" scope="request"
 *                validate="false" parameter="method" input="list"
 * 
 * @struts.action name="annoSchemaForm" path="/saveSchema" scope="request"
 *                validate="false" parameter="method" input="edit"
 * 
 * @struts.action name="annoSchemaForm" path="/popupAddSchema" scope="request"
 *                validate="false" parameter="method" input="list"
 * 
 * @struts.action name="annoSchemaForm" path="/popupSaveSchema" scope="request"
 *                validate="false" parameter="method" input="edit"
 * 
 * @struts.action-forward name="list" path="/WEB-INF/pages/annoSchemaList.jsp"
 * 
 * @struts.action-forward name="edit" path="/WEB-INF/pages/annoSchemaForm.jsp"
 * @struts.action-forward name="success" path="/schemaUploadInfo.html"
 *                        redirect="true"
 * @struts.action-forward name="popupSuccess" path="/popupSchemaUploadInfo.html"
 *                        redirect="true"
 */
public class AnnoSchemaAction extends BaseAction {

	public ActionForward cancel(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward("viewAnnoSchemas");
	}

	/**
	 * ActionForward that is invoked in addSchema.html(annoSchemaForm.jsp),
	 * which is used to prepare for uploading a schema
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward add(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String popup = request.getParameter("popup");
		if (log.isDebugEnabled()) {
			log.debug("Entering 'add schema' method");
			log.debug("param 'popup' is: " + popup);
		}
		return mapping.findForward("edit");
	}

	/**
	 * ActionForward that is invoked from saveSchema.html, which uploads a xml
	 * file to the server side.
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward save(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String popup = request.getParameter("popup");
		log.debug("param 'popup' is: " + popup);
		if (log.isDebugEnabled()) {
			log.debug("Entering 'save' method in AnnoSchemaAction");
		}

		ActionMessages errors = form.validate(mapping, request);
		// form-based validation will check the format of file to upload(xml or
		// not)
		if (!errors.isEmpty()) {
			saveErrors(request, errors);
			return mapping.findForward("edit");
		}
		try {
			ApplicationContext ctx = WebApplicationContextUtils
					.getRequiredWebApplicationContext(servlet
							.getServletContext());

			AnnotationServiceManager mgr = (AnnotationServiceManager) ctx
					.getBean("annotationServiceManager");

			String uploadDirString = mgr.getSchemasDirPath();
			ActionMessages messages = new ActionMessages();
			AnnoSchemaForm uploadForm = (AnnoSchemaForm) form;
			StringBuffer ufMessage = new StringBuffer();
			// the directory to upload to
			File uploadDir = new File(uploadDirString);
			if (!uploadDir.exists()) {
				boolean created = uploadDir.mkdirs();
				if (!created)
					throw new Exception("Folder " + uploadDirString
							+ " cannot be created.");
			}
			String outPath0 = null;
			String outPath1 = null;
			String outPath2 = null;
			String outPath3 = null;
			String outPath4 = null;

			if (uploadForm != null) {
				if (uploadForm.getFile().getFileName().length() > 0
						&& uploadForm.getFile().getFileSize() > 0) {

					outPath0 = uploadDir.getAbsolutePath() + "/"
							+ uploadForm.getFile().getFileName();
				}
				if (uploadForm.getFile1().getFileName().length() > 0
						&& uploadForm.getFile1().getFileSize() > 0) {

					outPath1 = uploadDir.getAbsolutePath() + "/"
							+ uploadForm.getFile1().getFileName();
				}

				if (uploadForm.getFile2().getFileName().length() > 0
						&& uploadForm.getFile2().getFileSize() > 0) {

					outPath2 = uploadDir.getAbsolutePath() + "/"
							+ uploadForm.getFile2().getFileName();
				}

				if (uploadForm.getFile3().getFileName().length() > 0
						&& uploadForm.getFile3().getFileSize() > 0) {

					outPath3 = uploadDir.getAbsolutePath() + "/"
							+ uploadForm.getFile3().getFileName();
				}

				if (uploadForm.getFile4().getFileName().length() > 0
						&& uploadForm.getFile4().getFileSize() > 0) {

					outPath4 = uploadDir.getAbsolutePath() + "/"
							+ uploadForm.getFile4().getFileName();
				}

				/*
				 * for(int i=0;i<=4;i++){ String outPath="outPath"+i;
				 * if(outPath.length()>uploadDir.getAbsolutePath().length()+1){
				 * mgr.publishSchema(uploadForm.getFile().getInputStream(),
				 * outPath); } }
				 */
				if (outPath0 != null) {
					mgr.publishSchema(uploadForm.getFile().getInputStream(),
							outPath0);
					ufMessage.append("<ul><li>Uploaded File:<strong> "
							+ uploadForm.getFile().getFileName()
							+ "</strong> </li>");
				}
				if (outPath1 != null) {
					mgr.publishSchema(uploadForm.getFile1().getInputStream(),
							outPath1);
					ufMessage.append("<li>Uploaded File:<strong> "
							+ uploadForm.getFile1().getFileName()
							+ "</strong> </li>");
				}
				if (outPath2 != null) {
					mgr.publishSchema(uploadForm.getFile2().getInputStream(),
							outPath2);
					ufMessage.append("<li>Uploaded File:<strong> "
							+ uploadForm.getFile2().getFileName()
							+ "</strong> </li>");
				}
				if (outPath3 != null) {
					mgr.publishSchema(uploadForm.getFile3().getInputStream(),
							outPath3);
					ufMessage.append("<li>Uploaded File:<strong> "
							+ uploadForm.getFile3().getFileName()
							+ "</strong> </li>");
				}
				if (outPath4 != null) {
					mgr.publishSchema(uploadForm.getFile4().getInputStream(),
							outPath4);
					ufMessage.append("<li>Uploaded File:<strong> "
							+ uploadForm.getFile4().getFileName()
							+ "</strong> </li>");
				}
				ufMessage.append("</ul>");

				request.getSession().setAttribute("uploadLog",
						ufMessage.toString());
				log.debug("uploadLog = " + ufMessage.toString());
				messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
						"annoSchema.added"));
				saveMessages(request.getSession(), messages);
			}

		} catch (Exception e) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
					"errors.upload.corpus", e.getMessage()));
			saveErrors(request, errors);
			request.setAttribute(Globals.ERROR_KEY, errors);
			ActionForward af = new ActionForward(mapping.findForward("edit"));
			return af;
		}
		if (log.isDebugEnabled()) {
			log.debug("Leaving 'save' method in AnnoSchemaAction");
		}

		ActionForward af = null;
		String pathSuffix = "";
		if (popup != null && !"".equals(popup)) {
			af = new ActionForward(mapping.findForward("popupSuccess"));
			pathSuffix = "?popup=" + popup;
		} else {
			// not invoked from WF, so redirect as usual
			af = new ActionForward(mapping.findForward("success"));

		}
		af.setPath(af.getPath() + pathSuffix);
		return af;
	}

	/**
	 * ActionForward that is invoked in editSchemaFile.html
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	public ActionForward edit(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		if (log.isDebugEnabled()) {
			log.debug("Entering 'edit in AnnoSchemaAction' method");
		}
		return mapping.findForward("edit");
	}

	/**
	 * ActionForward that is invoked from schemas.jsp, which calls
	 * annontationServiceManager to list all the xml files in the FS
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward search(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		if (log.isDebugEnabled()) {
			log.debug("Entering 'schemas search' method");
		}
		AnnotationServiceManager mgr = (AnnotationServiceManager) getBean("annotationServiceManager");
		List<AnnotationSchema> annotationSchemas = mgr.listSchemas();
		request.setAttribute(Constants.SCHEMA_LIST, annotationSchemas);

		WorkflowManager workflowManager = (WorkflowManager) getBean("workflowManager");
		WebAppBean webAppBean = (WebAppBean) getBean("webAppBean");
		/*
		ProcessDefinition processDefinition = workflowManager
				.findLatestProcessDefinition(Constants.MAIN_PROCESS_DEFINITION_NAME);
		List processInstances = workflowManager
				.findAllProcessInstancesByProcessDefinitionIdExcludingSubProcessInstances(processDefinition
						.getId());
		*/
		List processInstances = workflowManager
		.findAllProcessInstancesByProcessDefinitionNameExcludingSubProcessInstances(Constants.MAIN_PROCESS_DEFINITION_NAME);
		Map<String, List<ProcessInstanceWrapper>> annotationSchemaProcessInstanceWrapperMap = new HashMap<String, List<ProcessInstanceWrapper>>();
		Iterator<AnnotationSchema> itr = annotationSchemas.iterator();
		while (itr.hasNext()) {
			String asName = itr.next().getName();
			Iterator it = processInstances.iterator();
			while (it.hasNext()) {
				ProcessInstance processInstance = (ProcessInstance) it.next();
				ProcessInstanceWrapper piw = new ProcessInstanceWrapper(
						processInstance);
				Set<String> annotationSchemaNames = piw
						.getAnnotationSchemaNames();
				//log.debug("annotationSchemaNames size: " + annotationSchemaNames.size());
				if (annotationSchemaNames.contains(asName)) {
					List<ProcessInstanceWrapper> list = annotationSchemaProcessInstanceWrapperMap
							.get(asName);
					if (list == null) {
						list = new ArrayList<ProcessInstanceWrapper>();
					}
					list.add(piw);
					annotationSchemaProcessInstanceWrapperMap.put(asName, list);
				}
			}
		}
		request.setAttribute("annotationSchemaProcessMap",
				annotationSchemaProcessInstanceWrapperMap);
		
		ResourceManager resourceManager = (ResourceManager)getBean("resourceManager");
		    
		List processInstancesUsingRoles = resourceManager.getRolesWithResource(Constants.PROCESS_INSTANCE_PATTERN);
	    String processInstancesUsingRolesString = StringUtils.collectionToCommaDelimitedString(processInstancesUsingRoles);
		log.debug("processInstancesUsingRoles "+processInstancesUsingRoles);
		request.setAttribute("processInstancesUsingRoles",processInstancesUsingRolesString);
			

		return mapping.findForward("list");
	}

	public ActionForward delete(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		if (log.isDebugEnabled()) {
			log.debug("Entering 'delete' method");
		}
		ActionMessages messages = new ActionMessages();
		String toDelete = request.getParameter("toDelete");
		String[] schemaNames = request.getParameterValues("rowId");
		AnnotationServiceManager mgr = (AnnotationServiceManager) getBean("annotationServiceManager");
		String sourceDir = mgr.getSchemasDirPath();
		if (schemaNames == null) {
			ActionMessages errors = new ActionMessages();
			errors.add("errors.detail", new ActionMessage(
					"errors.schemaNotSelected"));
			saveErrors(request, errors);
			return dispatchMethod(mapping, form, request, response, "search");
		}
		if (toDelete != null) {
			for (String schema : schemaNames) {
				mgr.deleteSchema(sourceDir + "/" + schema);
			}
			messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
					"schemas.deleted"));
			saveMessages(request.getSession(), messages);
		}
		return mapping.findForward("viewAnnoSchemas");
	}

	/*
	 * public ActionForward downloadSchema(ActionMapping mapping, ActionForm
	 * form, HttpServletRequest request, HttpServletResponse response) throws
	 * Exception { if(log.isDebugEnabled()) {
	 * log.debug("Entering 'download Schema' method"); }
	 * AnnotationServiceManager mgr =
	 * (AnnotationServiceManager)getBean("annotationServiceManager"); String
	 * schemaName = request.getParameter("fileName"); String filePath =
	 * mgr.getSchemasDirPath()+"/"+schemaName; FileInputStream input = new
	 * FileInputStream(new File(filePath)); response.setContentType("text/xml");
	 * response.setHeader("Content-Disposition", "attachment; filename=\""
	 * +schemaName); ServletOutputStream out = response.getOutputStream(); try{
	 * byte[] buf = new byte[4096]; int numRead = 0; while((numRead =
	 * input.read(buf)) >= 0) { out.write(buf, 0, numRead); } }catch(Exception
	 * e){ e.printStackTrace(); } finally{ input.close(); out.close(); } return
	 * null; }
	 */

	public ActionForward unspecified(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		if (log.isDebugEnabled()) {
			log.debug("Entering 'unspecified' method");
		}

		if (log.isDebugEnabled()) {
			log.debug("Leaving 'unspecified' method");
		}
		return search(mapping, form, request, response);
	}

}
