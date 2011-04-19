/*
 *  CorpusUploadAction.java
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
 * Ivaylo Kabakov
 *
 *  $Id$
 */
package gleam.executive.webapp.action;

import gleam.executive.model.Corpus;
import gleam.executive.service.DocServiceManager;
import gleam.executive.service.SafeManagerException;
import gleam.executive.webapp.action.fileupload.ZipArchiveUploadAction;
import gleam.executive.webapp.form.CorpusUploadForm;
import gleam.executive.webapp.form.ZipArchiveUploadForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * <p>
 * 	A concrete class, extending {@link ZipArchiveUploadAction ZipArchiveUploadAction},
 * 	for creating a new corpus from an uploaded documents archive. 
 * </p>
 * 
 * @struts.action path="/editUploadCorpus" scope="request"
 *                name="corpusUploadForm" validate="false" parameter="method"
 * @struts.action path="/saveUploadCorpus" scope="request"
 *                name="corpusUploadForm" validate="true" parameter="method"
 *                input="edit"
 * @struts.action path="/popupEditUploadCorpus" scope="request"
 *                				name="corpusUploadForm" validate="false" parameter="method"
 * @struts.action-forward name="edit" path="/WEB-INF/pages/corpusUploadForm.jsp"
 * @struts.action-forward name="success" path="/corpusUploadInfo.html"
 *                        redirect="true"
 * @struts.action-forward name="popupSuccess" path="/popupCorpusUploadInfo.html"
 *                        redirect="true"
 * @struts.action-forward name="failure" path="/corpusUploadInfo.html"
 * @struts.action-forward name="popupFailure" path="/popupCorpusUploadInfo.html"
 */
public class CorpusUploadAction extends ZipArchiveUploadAction {

	@Override
	protected Corpus retrieveCorpus(HttpServletRequest request, DocServiceManager mgr, ZipArchiveUploadForm uploadForm)
			throws SafeManagerException {

		Corpus corpus = new Corpus();
		String corpusName = ((CorpusUploadForm) uploadForm).getCorpusName();
		String corpusID = mgr.createCorpus(corpusName);
		
		corpus.setCorpusID(corpusID);
		corpus.setCorpusName(corpusName);
		
		return corpus;
	}

	public ActionForward cancel(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward("viewCorpora");
	}

	/**
	 * ActionForward that is invoked in addCorpus.html(corpusForm.jsp), which is
	 * used to prepare for the creation of a corpus
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
		if (log.isDebugEnabled()) {
			log.debug("Entering 'edit corpus' method");
		}

		return mapping.findForward("edit");
	}

}
