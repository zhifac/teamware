/*
 *  ProcessDefinitionUploadAction.java
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
package gleam.executive.webapp.action;

import java.io.InputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.upload.FormFile;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import gleam.executive.webapp.form.ProcessDefinitionUploadForm;
import gleam.executive.workflow.manager.WorkflowManager;

//TODO Javadoc
/**
 * @author <a href="mailto:agaton@dcs.shef.ac.uk">Milan Agatonovic</a>
 * @struts.action name="processDefinitionUploadForm"
 *                path="/processDefinitionUpload" scope="request"
 *                validate="true" input="failure"
 * @struts.action-set-property property="cancellable" value="true"
 * @struts.action-forward name="failure"
 *                        path="/WEB-INF/pages/processDefinitionUploadForm.jsp"
 * @struts.action-forward name="success"
 *                        path="/processDefinitionUploadDisplay.html" redirect="true"
 */
public class ProcessDefinitionUploadAction extends Action {
  public ActionForward execute(ActionMapping mapping, ActionForm form,
          HttpServletRequest request, HttpServletResponse response)
          throws Exception {
    // Did the user click the cancel button?
    if(isCancelled(request)) {
      request.removeAttribute(mapping.getAttribute());
      return (mapping.findForward("mainMenu"));
    }

    // this line is here for when the input page is upload-utf8.jsp,
    // it sets the correct character encoding for the response
    String encoding = request.getCharacterEncoding();

    if((encoding != null) && (encoding.equalsIgnoreCase("utf-8"))) {
      response.setContentType("text/html; charset=utf-8");
    }

    ProcessDefinitionUploadForm theForm = (ProcessDefinitionUploadForm)form;

    // retrieve the name
    String name = theForm.getName();

    // retrieve the file representation
    FormFile file = theForm.getFile();

    if(file == null) {
      return mapping.findForward("failure");
    }

    // retrieve the file name
    String fileName = file.getFileName();

    // retrieve the content type
    String contentType = file.getContentType();

    // retrieve the file size
    String size = (file.getFileSize() + " bytes");

    String data = null;
    /*
     * String filePath = null;
     *  // the directory to upload to String uploadDir =
     * servlet.getServletContext().getRealPath("/resources") + "/" +
     * request.getRemoteUser() + "/";
     *
     * //write the file to the file specified File dirPath = new
     * File(uploadDir);
     *
     * if (!dirPath.exists()) { dirPath.mkdirs(); }
     *
     */
    // retrieve the file data
    InputStream stream = file.getInputStream();
    /*
     *
     * //write the file to the file specified OutputStream bos = new
     * FileOutputStream(uploadDir + fileName); int bytesRead = 0; byte[]
     * buffer = new byte[8192];
     *
     * while ((bytesRead = stream.read(buffer, 0, 8192)) != -1) {
     * bos.write(buffer, 0, bytesRead); }
     *
     * bos.close();
     *
     * filePath = dirPath.getAbsolutePath() + Constants.FILE_SEP +
     * file.getFileName();
     */
    ActionMessages messages = new ActionMessages();

    ApplicationContext ctx = WebApplicationContextUtils
            .getRequiredWebApplicationContext(servlet.getServletContext());

    WorkflowManager mgr = (WorkflowManager)ctx.getBean("workflowManager");
    mgr.deployProcessFromArchive(stream);

    // log.debug(" process added ");
    messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
            "workflow.ProcessDefinition.uploaded"));

    saveMessages(request.getSession(), messages);
    // close the stream
    stream.close();

    // place the data into the request for retrieval on next page

    request.getSession().setAttribute("friendlyName", name);
    request.getSession().setAttribute("fileName", fileName);
    request.getSession().setAttribute("contentType", contentType);
    request.getSession().setAttribute("size", size);
    request.getSession().setAttribute("data", data);

    /*
     * request.setAttribute("filePath", filePath);
     *
     * String url = request.getContextPath() + "/resources" + "/" +
     * request.getRemoteUser() + "/" + file.getFileName();
     * request.setAttribute("url", url);
     *
     * //destroy the temporary file created file.destroy();
     */
    // return a forward to display.jsp
    return mapping.findForward("success");
  }
}
