package gleam.executive.webapp.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.jbpm.graph.def.ProcessDefinition;
import org.springframework.util.StringUtils;

import gleam.executive.webapp.action.BaseAction;
import gleam.executive.Constants;
import gleam.executive.model.AnnotationDiffGUILaunchBean;
import gleam.executive.model.AnnotationSchema;
import gleam.executive.model.AnnotatorGUILaunchBean;
import gleam.executive.model.Document;
import gleam.executive.model.WebAppBean;
import gleam.executive.service.DocServiceManager;
import gleam.executive.service.AnnotationServiceManager;
import gleam.executive.webapp.form.DocumentForm;
import gleam.executive.workflow.manager.WorkflowManager;
import gleam.executive.workflow.util.WorkflowUtil;

/**
 * Implementation of <strong>Action</strong> that interacts with the
 * {@link DocumentForm} and retrieves values. It interacts with the
 * {@link DocServiceManager} to retrieve/persist values to the datastore.
 * 
 * Copyright (c) 1998-2006, The University of Sheffield.
 * 
 * This file is part of GATE (see http://gate.ac.uk/), and is free software,
 * licenced under the GNU Library General Public License, Version 2, June 1991
 * (in the distribution as file licence.html, and also available at
 * http://gate.ac.uk/gate/licence.html).
 * 
 * @author <a href="mailto:agaton@dcs.shef.ac.uk">Milan Agatonovic</a>
 * 
 * @struts.action name="documentForm" path="/documentsInCorpus" scope="request"
 *                validate="false" parameter="method" input="corpora"
 * @struts.action name="documentForm" path="/viewDocument" scope="request"
 *                validate="false" parameter="method" input="list"
 * @struts.action-set-property property="cancellable" value="true"
 * @struts.action-forward name="list" path="/WEB-INF/pages/documentList.jsp"
 * @struts.action-forward name="annotation-gui" path="/WEB-INF/pages/jnlpInvoker.jsp"
 * @struts.action-forward name="annotation-diff" path="/WEB-INF/pages/annotationDiffJnlpInvoker.jsp"
 * @struts.action-forward name="search" path="/documentsInCorpus.html"
 *                        redirect="true"
 * 
 */
public final class DocumentAction extends BaseAction {

	public ActionForward cancel(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward("search");
	}

	/**
	 * ActionForward that is invoked from
	 * documentsInCorpus.html(documentList.jsp), which calls docSercieManager to
	 * remove a corpus from its corpus
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward removeFromCorpus(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		if (log.isDebugEnabled()) {
			log.debug("Entering 'remove' method");
		}

		ActionMessages messages = new ActionMessages();

		// Exceptions are caught by ActionExceptionHandler
		DocServiceManager mgr = (DocServiceManager) getBean("docServiceManager");
		String corpusID = request.getParameter("corpusID");
		if (corpusID.equals("")) {
			corpusID = (String) request.getAttribute("corpusID");
			log
					.debug("Retrieving the corpusID from request scope in removeFromCorpus()"
							+ corpusID);
		}
		log.debug("@@@ DEL corpusId " + corpusID);
		String documentId = request.getParameter("documentID");
		log.debug("@@@ DEL documentId " + documentId);
		mgr.removeDocumentFromCorpus(corpusID, documentId);

		messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
				"document.removed"));

		List list = mgr.listDocuments(corpusID);
		request.setAttribute(Constants.DOCUMENT_LIST, list);
		// save messages in session, so they'll survive the redirect
		saveMessages(request.getSession(), messages);

		return mapping.findForward("list");
	}

	/**
	 * ActionForward that is invoked from
	 * documentsInCorpus.html(documentList.jsp), which calls docSercieManager to
	 * delete a document from the datastore
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward delete(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		if (log.isDebugEnabled()) {
			log.debug("Entering 'delete' method");
		}

		ActionMessages messages = new ActionMessages();

		// Exceptions are caught by ActionExceptionHandler
		DocServiceManager mgr = (DocServiceManager) getBean("docServiceManager");
		String corpusID = request.getParameter("corpusID");
		if (corpusID.equals("") || corpusID == null) {
			corpusID = (String) request.getAttribute("corpusID");
			log.debug("Retrieving the corpusID from session scope " + corpusID);
		}
		log.debug("@@@ DEL corpusId " + corpusID);
		String documentId = request.getParameter("documentID");
		log.debug("@@@ DEL documentId " + documentId);

		mgr.deleteDocument(documentId);

		messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
				"document.deleted"));

		List list = mgr.listDocuments(corpusID);
		request.setAttribute(Constants.DOCUMENT_LIST, list);
		// save messages in session, so they'll survive the redirect
		saveMessages(request.getSession(), messages);
		return mapping.findForward("list");
	}

	public ActionForward deleteMultipleDocs(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		if (log.isDebugEnabled()) {
			log.debug("Entering 'delete multiple docs' method");
		}
		ActionMessages messages = new ActionMessages();

		// Exceptions are caught by ActionExceptionHandler
		DocServiceManager mgr = (DocServiceManager) getBean("docServiceManager");
		String corpusID = request.getParameter("corpusID");
		String toDelete = request.getParameter("toDelete");
		String toRemove = request.getParameter("toRemove");

		if (corpusID.equals("") || corpusID == null) {
			corpusID = (String) request.getAttribute("corpusID");
		}
		log.debug("@@@ DEL Multiple Docs corpusID " + corpusID);

		String[] docIds = request.getParameterValues("rowId");
		if (docIds == null) {
			ActionMessages errors = new ActionMessages();
			errors.add("errors.detail", new ActionMessage(
					"document.noDocumentSelected"));
			saveErrors(request, errors);
			request.setAttribute("corpusID", corpusID);
			return dispatchMethod(mapping, form, request, response, "search");
		}
		if (toDelete != null) {
			for (String docId : docIds) {
				mgr.deleteDocument(docId);
			}
			messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
					"document.deleted"));
		}
		if (toRemove != null) {
			for (String docId : docIds) {
				mgr.removeDocumentFromCorpus(corpusID, docId);
			}
			messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
					"document.removed"));
		}
		List list = mgr.listDocuments(corpusID);
		request.setAttribute(Constants.DOCUMENT_LIST, list);
        
		// pass corpusID as a request parameter
		
		
		// save messages in session, so they'll survive the redirect
		saveMessages(request.getSession(), messages);
	
		request.setAttribute("corpusID", corpusID);
		return mapping.findForward("list");

	}

	/**
	 * ActionForward that is invoked from
	 * documentsInCorpus.html(documentList.jsp), which calls docServiceManager
	 * to list all the documents in a corpus
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
			log.debug("Entering 'documents search' method");
		}

		DocServiceManager mgr = (DocServiceManager) getBean("docServiceManager");
		WorkflowManager workflowManager = (WorkflowManager) getBean("workflowManager");
		
		String corpusID = request.getParameter("corpusID");
		if (corpusID == null || corpusID.equals("")) {
			corpusID = (String) request.getAttribute("corpusID");
			log
					.debug("Retrieving the corpusID from request scope, corpusID is "
							+ corpusID);
		}

		String corpusName = request.getParameter("corpusName");
		if (corpusName == null || corpusName.equals("")) {
			corpusName = (String) request.getAttribute("corpusName");
			log
					.debug("Retrieving the corpusID from request scope, corpusName is "
							+ corpusName);
		}
		request.setAttribute("corpusID", corpusID);
		request.setAttribute("corpusName", corpusName);
		log.debug("search @@@@ corpusID " + corpusID);
		log.debug("search @@@@ corpusName " + corpusName);
		List list = mgr.listDocuments(corpusID);

		request.setAttribute(Constants.DOCUMENT_LIST, list);
		
		boolean canDelete = !workflowManager.isAnyProcessInstanceRunningAgainstTheCorpus(Constants.MAIN_PROCESS_DEFINITION_NAME, corpusID);
		log.debug("search @@@@ canDelete " + canDelete);
		
		request.setAttribute("canDelete", canDelete);
		return mapping.findForward("list");
	}

	public ActionForward unspecified(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return search(mapping, form, request, response);
	}
/*
	private List formatDocumentList(List list, String docServiceURL,
			String annGUIURL, String aDiffGUIURL) throws Exception {

		AnnotationServiceManager annotationServiceManager = (AnnotationServiceManager) getBean("annotationServiceManager");
		WebAppBean webAppBean = (WebAppBean) getBean("webAppBean");

		Iterator it = list.iterator();

		// TODO james acrobatic method: REFACTOR IT!
		while (it.hasNext()) {
			Document doc = (Document) it.next();
			AnnotatorGUILaunchBean annotatorGUILaunchBean = new AnnotatorGUILaunchBean();
			annotatorGUILaunchBean.setDocServiceURL(docServiceURL);
			annotatorGUILaunchBean.setDocumentId(doc.getDocumentID()
					.replaceAll("&", "%26"));
			List<AnnotationSchema> annotationSchemas = annotationServiceManager
					.listSchemas();

			String baseAnnotationSchemaURL =
					webAppBean.getUrlBase() + "/"
					+ webAppBean.getInstanceName() + "/schemas/";

			List<String> annotationSchemaURLList = new ArrayList<String>();
			Iterator<AnnotationSchema> ita = annotationSchemas.iterator();
			while (ita.hasNext()) {
				AnnotationSchema as = ita.next();
				annotationSchemaURLList.add(baseAnnotationSchemaURL
						+ as.getName());
			}

			String annotationSchemaCSVURLs = StringUtils
					.collectionToCommaDelimitedString(annotationSchemaURLList);
			annotatorGUILaunchBean
					.setAnnotationSchemaCSVURLs(annotationSchemaCSVURLs);

			String annoURL = annGUIURL + annotatorGUILaunchBean.toString();
			doc.setAnnotatorGUIURL(annoURL);

			AnnotationDiffGUILaunchBean adiffGUILaunchBean = new AnnotationDiffGUILaunchBean();
			adiffGUILaunchBean.setDocServiceURL(docServiceURL);
			adiffGUILaunchBean.setDocumentId(doc.getDocumentID().replaceAll(
					"&", "%26"));
			String adiffURL = aDiffGUIURL + adiffGUILaunchBean.toString();
			doc.setAnnotationDiffGUIURL(adiffURL);

		}
		return list;
	}
*/
	public ActionForward view(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		String documentId = request.getParameter("documentId");
		if (log.isDebugEnabled()) {
			log.debug("Entering 'view' method. documentId: " + documentId);
		}

		if(documentId!=null){
			log.debug("DIRECT MODE");
		DocServiceManager mgr = (DocServiceManager) getBean("docServiceManager");
		AnnotationServiceManager annotationServiceManager = (AnnotationServiceManager) getBean("annotationServiceManager");
		WebAppBean webAppBean = (WebAppBean) getBean("webAppBean");

		AnnotatorGUILaunchBean annotatorGUILaunchBean = new AnnotatorGUILaunchBean();
		annotatorGUILaunchBean.setDocServiceURL(mgr.getDocServiceURL());
		annotatorGUILaunchBean.setDocumentId(documentId.replaceAll("&", "%26"));
		List<AnnotationSchema> annotationSchemas = annotationServiceManager
				.listSchemas();

		String baseAnnotationSchemaURL = webAppBean.getUrlBase()
				+ "/" + webAppBean.getInstanceName() + "/schemas/";

		List<String> annotationSchemaURLList = new ArrayList<String>();
		Iterator<AnnotationSchema> ita = annotationSchemas.iterator();
		while (ita.hasNext()) {
			AnnotationSchema as = ita.next();
			annotationSchemaURLList.add(baseAnnotationSchemaURL + as.getName());
		}

		String annotationSchemaCSVURLs = StringUtils
				.collectionToCommaDelimitedString(annotationSchemaURLList);
		annotatorGUILaunchBean
				.setAnnotationSchemaCSVURLs(annotationSchemaCSVURLs);

		request.setAttribute(Constants.ANNOTATOR_GUI_LAUNCH_BEAN,
				annotatorGUILaunchBean);
		}
		else {
			log.debug("POOL MODE");
		}
		return mapping.findForward("annotation-gui");
	}

	public ActionForward diff(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		String documentId = request.getParameter("documentId");
		if (log.isDebugEnabled()) {
			log.debug("Entering 'diff' method. documentId: " + documentId);
		}

		DocServiceManager mgr = (DocServiceManager) getBean("docServiceManager");
	
		AnnotationDiffGUILaunchBean annotationDiffLaunchBean = new AnnotationDiffGUILaunchBean();
		annotationDiffLaunchBean.setDocServiceURL(mgr.getDocServiceURL());
		annotationDiffLaunchBean.setDocumentId(documentId.replaceAll(
				"&", "%26"));
		annotationDiffLaunchBean.setAutoconnect("true");
		request.setAttribute(Constants.ANNOTATION_DIFF_GUI_LAUNCH_BEAN, annotationDiffLaunchBean);
		return mapping.findForward("annotation-diff");
	}

}
