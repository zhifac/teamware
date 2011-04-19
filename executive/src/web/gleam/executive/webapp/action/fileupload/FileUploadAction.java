/*
 *  FileUploadAction.java
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
 * Haotian Sun and Ivaylo Kabakov
 *
 *  $Id$
 */
package gleam.executive.webapp.action.fileupload;

import gleam.executive.model.Corpus;
import gleam.executive.model.Document;
import gleam.executive.service.DocServiceManager;
import gleam.executive.service.SafeManagerException;
import gleam.executive.webapp.form.ZipArchiveUploadForm;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * <p>
 * 	A concrete class, extending {@link ZipArchiveUploadAction ZipArchiveUploadAction},
 * 	for handling of an uploaded archive with documents and adding them to an 
 * 	existing corpus. 
 * </p>
 * 
 * @struts.action path="/editUploadFile" scope="request" name="fileUploadForm"
 *                validate="false" parameter="method"
 * @struts.action path="/popupEditUploadFile" scope="request" name="fileUploadForm"
 *                validate="false" parameter="method"               
 * @struts.action path="/saveUploadFile" scope="request" name="fileUploadForm"
 *                validate="false" parameter="method" input="edit"
 * @struts.action-forward name="edit" path="/WEB-INF/pages/fileUploadForm.jsp"
 * @struts.action-forward name="success" path="/corpusUploadInfo.html"
 *                        redirect="true"
 * @struts.action-forward name="popupSuccess" path="/popupCorpusUploadInfo.html"
 *                        redirect="true"                      
 * @struts.action-forward name="failure" path="/corpusUploadInfo.html"
 * @struts.action-forward name="popupFailure" path="/popupCorpusUploadInfo.html"
 
 */
public class FileUploadAction extends ZipArchiveUploadAction {

	@Override
	protected Corpus retrieveCorpus(HttpServletRequest request, DocServiceManager mgr, ZipArchiveUploadForm uploadForm) throws SafeManagerException {

		Corpus corpus = new Corpus();
		String corpusID = (String) request.getParameter("corpusID");
		if (corpusID == null || corpusID.equals("")) {
			corpusID = (String) request.getAttribute("corpusID");
		}
		String corpusName = mgr.getCorpusName(corpusID);
		
		corpus.setCorpusID(corpusID);
		corpus.setCorpusName(corpusName);
		
		return corpus;
	}
	
	/**
	 * ActionForward that is invoked in editUploadFile.html
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	public ActionForward edit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {

		if (log.isDebugEnabled()) {
			log.debug("Entering 'edit in FileUploadAction' method");
		}
		String corpusID = null;
		try {
			corpusID = (String) request.getParameter("corpusID");
			String delete = (String) request.getParameter("delete");
			boolean shouldDocumentsBeDeleted = false;
			if (delete != null && delete.equals(true)) {
				shouldDocumentsBeDeleted = true;
			}
			if (corpusID.equals("") || corpusID == null) {
				corpusID = (String) request.getAttribute("corpusID");
				log.debug("***edit() in FileUploadAction: get corpusID from getAttribute " + corpusID);
			}
			request.setAttribute("corpusID", corpusID);
			if (shouldDocumentsBeDeleted) {
				log.debug("delete old documents first");
				DocServiceManager mgr = (DocServiceManager) getBean("docServiceManager");
				List documents = mgr.listDocuments(corpusID);
				Iterator it = documents.iterator();
				while (it.hasNext()) {
					Document doc = (Document) it.next();
					String docId = doc.getDocumentID();
					mgr.deleteDocument(docId);
					log.debug("deleted: " + docId);
				}
			}
			ActionForward af = new ActionForward(mapping.findForward("edit"));
			af.setPath(af.getPath() + "?corpusID=" + corpusID);
			return af;
		} catch (Exception e) {
			e.printStackTrace();
			return mapping.findForward("edit");
		}
	}

}
