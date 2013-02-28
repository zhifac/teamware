/*
 *  ZipArchiveUploadAction.java
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
 * Haotian Sun, Milan Agatonovic and Ivaylo Kabakov
 *
 *  $Id$
 */
package gleam.executive.webapp.action.fileupload;

import gleam.executive.Constants;
import gleam.executive.model.Corpus;
import gleam.executive.service.DocServiceManager;
import gleam.executive.service.SafeManagerException;
import gleam.executive.util.FileUtil;
import gleam.executive.util.GATEUtil;
import gleam.executive.util.ZipUtil;
import gleam.executive.webapp.action.BaseAction;
import gleam.executive.webapp.action.fileupload.upload.DSListener;
import gleam.executive.webapp.form.ZipArchiveUploadForm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * <p>
 * An abstract class, implementing action for receiving uploaded zip archives 
 * and writing them to disk. <br /> 
 * Subclasses must implement the <code>retrieveCorpus</code> method 
 * for extracting the {@link gleam.executive.model.Corpus Corpus} data from 
 * the parameters provided.
 * </p>
 * <p>
 * Inspired by:
 * <ul>
 * 	<li>
 * 		<a href="http://www.telio.be/blog/2006/01/06/ajax-upload-progress-monitor-for-commons-fileupload-example/">
 * 		"AJAX Upload progress monitor for Commons-FileUpload Example"</a>
 * 	</li>
 * 	<li>
 *  	<a href="http://mail-archives.apache.org/mod_mbox/jakarta-commons-user/200602.mbox/%3C4403654B.2090705@bluewin.ch%3E">
 *  	"Struts & Fileupload not working"</a>
 * 	</li>
 * 	<li>
 * 		<a href="http://kencochrane.blogspot.com/2006/03/ajax-struts-file-upload-progress-meter.html">
 * 		"AJAX Struts File Upload Progress Meter" </a>
 * 	</li>
 * </ul>
 * </p>
 */
public abstract class ZipArchiveUploadAction extends BaseAction {

	protected abstract Corpus retrieveCorpus(HttpServletRequest request, 
			DocServiceManager mgr, ZipArchiveUploadForm uploadForm) throws SafeManagerException;
	
	/**
	 * ActionForward that is invoked from saveUploadFile.html, which uploads a
	 * zip file to the server side and calls docServiceManager to populate the
	 * corpus.
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
		
		if (log.isDebugEnabled()) {
			log.debug("Entering 'save' method in CorpusUploadAction");
		}
		
		ActionForward af = null;
		String corpusID = "";
		String corpusName = request.getParameter("corpusName");
		ZipArchiveUploadForm uploadForm = (ZipArchiveUploadForm) form;
		// form-based validation will check the encoding and the the format of
		// file to upload(zip or not)
		ActionMessages errors = form.validate(mapping, request);

		try {
			ApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(servlet.getServletContext());
			DocServiceManager docService = (DocServiceManager) ctx.getBean("docServiceManager");

			if(errors.isEmpty()) {
				Corpus corpus = retrieveCorpus(request, docService, uploadForm);
				corpusName = corpus.getCorpusName();
				corpusID = corpus.getCorpusID();
				
				String fileName = uploadForm.getFile().getFileName();
				log.debug("Uploaded file name:  " + fileName);
	
				if (uploadForm != null) {
					
					if (fileName.length() > 0
							&& ZipUtil.isZipFile(fileName)
							&& uploadForm.getFile().getFileSize() > 0) {
						
						processArchive(request, corpusID, uploadForm, errors, docService, fileName);
					}
				}
			}
			af = setupForwardAction(mapping, request, corpusID, corpusName, errors, docService);
			
		} catch (ZipException e) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, 
					new ActionMessage("errors.notSupporedZipMethod", e.getMessage()));
			log.debug("forwarding to 'edit'");
			af = mapping.findForward("edit");
		} catch (Exception e) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, 
					new ActionMessage("errors.upload.corpus", e.getMessage()));
			log.debug("forwarding to 'edit'");
			af = mapping.findForward("edit");
		}
		saveErrors(request, errors);
		
		return af;
	}

	protected ActionForward setupForwardAction(ActionMapping mapping, HttpServletRequest request, 
			String corpusID, String corpusName, ActionMessages errors, DocServiceManager mgr) 
		throws SafeManagerException {
		
		ActionForward af;
		ActionMessages messages = new ActionMessages();
		String uploader = request.getRemoteUser();
		mgr.setCorpusFeature(corpusID, Constants.UPLOADER, uploader);
		// corpus size after population
		int corpusNewSize = mgr.listDocuments(corpusID).size();
		boolean isPopup = isPopup(request);
		
		StringBuffer pathSuffix = new StringBuffer();
		pathSuffix.append("?method=search&corpusID=").append(corpusID);
		pathSuffix.append("&corpusName=").append(corpusName);
		pathSuffix.append("&corpusSize=").append(corpusNewSize);
		pathSuffix.append("&uploader=").append(uploader);
		
		if(errors.isEmpty()) {
			if (isPopup) {
				pathSuffix.append("&popup=").append(isPopup);
				af = new ActionForward(mapping.findForward("popupSuccess"));
			} else {
				// not invoked from WF, so redirect as usual
				af = new ActionForward(mapping.findForward("success"));
			}
			messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("corpus.populated"));
			saveMessages(request.getSession(), messages);
		} else {
			if (isPopup) {
				pathSuffix.append("&popup=").append(isPopup);
				af = new ActionForward(mapping.findForward("popupFailure"));
			} else {
				af = new ActionForward(mapping.findForward("failure"));
			}
		}
		af.setPath(af.getPath() + pathSuffix);
		
		return af;
	}

	protected void processArchive(HttpServletRequest request, String corpusID, 
			ZipArchiveUploadForm uploadForm, ActionMessages errors, 
			DocServiceManager docManager, String fileName) 
		throws IOException, FileNotFoundException, ZipException, 
		MalformedURLException, Exception {
		
		ZipFile zipFile = null;
		StringBuffer ufMessage = new StringBuffer();
		
		File uploadDir = determineUploadDirectory(request);
		log.debug("Upload path:  " + uploadDir.toString());
		String outPath = uploadDir.getAbsolutePath() + "/" + fileName;
		
		String encoding = uploadForm.getEncoding();
		log.debug("Encoding: " + encoding);
		
		FileUtil.redirectInputStream(uploadForm.getFile().getInputStream(), outPath);
		ufMessage.append("Uploading file <strong>");
		ufMessage.append(fileName);
		ufMessage.append("</strong> complete. <br />");
		request.setAttribute("uploadLog", ufMessage.toString());
		log.debug("uploadLog = " + ufMessage.toString());
		
		try {
			File file = new File(uploadDir, fileName);
			zipFile = new ZipFile(file);
			request.setAttribute("totalDocs", zipFile.size());
			
			DSListener dsListener = new DSListener(request, 15);
			dsListener.start();
			Enumeration entries = zipFile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry zipEntry = (ZipEntry) entries.nextElement();
				String path = zipEntry.getName();
				if (zipEntry.isDirectory() || ZipUtil.isMACSpecificZipEntry(zipEntry)) {
					continue;
				}
				String markupAware = isMarkupAware(request);
				processZipEntry(corpusID, errors, docManager, encoding,
						markupAware, file, dsListener, zipEntry, path);
			} // end while
			dsListener.done();
		} finally {
			log.debug("Try to close ZIP");
			if(zipFile != null) {
			  zipFile.close();
			  log.debug("ZIP CLOSED ");
			}
		}
	}

	protected void processZipEntry(String corpusID, ActionMessages errors,
			DocServiceManager docManager, String encoding, String markupAware,
			File file, DSListener dsListener, ZipEntry zipEntry, String path)
			throws MalformedURLException, Exception {
		
		if (ZipUtil.isValidZipEntry(zipEntry)) {
			URL entryURL = getEntryURL(file, path);
			log.debug("Entry URL: " + entryURL);
			log.debug("Entry size: " + zipEntry.getSize());
			if (zipEntry.getSize() < GATEUtil.MAX_ALLOWED_SIZE) {
				String docName = path.substring(path.lastIndexOf("/") + 1);
				dsListener.addDoc();
				try {
		      byte[] docXml = GATEUtil.getDocumentXml(entryURL, encoding, markupAware);
					docManager.createDocIntoCorpus(docName, corpusID, docXml, encoding);
					log.debug("Document: " + docName + "added to corpus");
				} catch(Exception e) {
					log.debug("Document: " + docName + "NOT added to corpus!", e);
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("corpus.invalid", path));
					return;
				}
			} else {
				log.debug("MAX_FILE_SIZE exceeded");
				errors.add(ActionMessages.GLOBAL_MESSAGE,
						new ActionMessage("maxFileSizeExceeded", zipEntry.getName()));
			}
		} else {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("corpus.invalid", path));
		}
	}

	protected boolean isPopup(HttpServletRequest request) {
		
		String popup = request.getParameter("popup");
		if(popup != null && !"".equals(popup))
			return true;
		else 
			return false;
	}

	protected String isMarkupAware(HttpServletRequest request) {
		
		String markupAware = (String) request.getParameter("markupAware");
		if (markupAware == null) {
			markupAware = "false";
		}
		log.debug("Is markup aware: " + markupAware);
		
		return markupAware;
	}

	protected URL getEntryURL(File file, String path) throws MalformedURLException {
		
		URL entryURL = null;
		
		if (path.charAt(0) != '/') {
			entryURL = new URL("jar:" + file.toURI().toURL() + "!/" + path);
		} else {
			entryURL = new URL("jar:" + file.toURI().toURL() + "!" + path);
		}
		
		return entryURL;
	}

	// the directory to upload to
	protected File determineUploadDirectory(HttpServletRequest request) throws Exception {
		
//			String uploadDirString = (String)servlet.getServletContext().getAttribute("instancedir") + "/upload/" + uploader + "/" ;
		String uploadDirString = servlet.getServletContext().getRealPath("/resources") + "/" + request.getRemoteUser() + "/";
		File uploadDir = new File(uploadDirString);
		
		if (!uploadDir.exists()) {
			boolean created = uploadDir.mkdirs();
			if (!created)
				throw new Exception("Folder " + uploadDir.toString() + " cannot be created.");
		}
		
		return uploadDir;
	}

	public ActionForward unspecified(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		if (log.isDebugEnabled()) {
			log.debug("Entering 'unspecified' method");
		}

		if (log.isDebugEnabled()) {
			log.debug("Leaving 'unspecified' method");
		}
		
		return new ActionForward("/", true);
	}

}
