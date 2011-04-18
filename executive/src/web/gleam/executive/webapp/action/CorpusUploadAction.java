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
 * <p>
 * 	Copyright &copy; 1998-2010, The University of Sheffield. <br />
 * </p>
 * <p>
 * 	This file is part of <a href="http://gate.ac.uk/">GATE</a> and is free software,
 * 	licenced under the GNU Library General Public License, Version 2, June 1991
 * 	(in the distribution as file <code>licence.html</code>, and also available at 
 * 	<a href="http://gate.ac.uk/gate/licence.html">http://gate.ac.uk/gate/licence.html</a>).
 * </p>
 * 
 * @author <a href="mailto:ivaylo.kabakov@ontotext.com">Ivaylo Kabakov</a>
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
