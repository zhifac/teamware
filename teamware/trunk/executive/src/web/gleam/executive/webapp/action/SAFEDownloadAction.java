/*
 *  SAFEDownloadAction.java
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

import gleam.executive.model.Project;
import gleam.executive.service.ProjectManager;
import gleam.executive.workflow.manager.WorkflowManager;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DownloadAction;
import org.jbpm.graph.def.ProcessDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
/**
 * This class is used to download a pdf help file
 *
 * <p>
 * <a href="SAFEDownloadAction.java.html"><i>View Source</i></a>
 * </p>
 * @author <a href="mailto:agaton@dcs.shef.ac.uk">Milan Agatonovic</a>
 *
 * @struts.action path="/download" validate="false"
 */
public class SAFEDownloadAction extends DownloadAction{

	protected final Log log = LogFactory.getLog(getClass());
  @Override
  protected StreamInfo getStreamInfo(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
	String id=request.getParameter("id");//Add validation for id
	String type = request.getParameter("type");
	
	String contentType ="";
	if(id.contains(".pdf")){
		contentType = "application/pdf";//PDF file
	}else if(id.contains(".zip")){
		contentType = "application/zip";//ZIP file
	}else if(id.contains(".jpeg")||id.contains(".jpe")||id.contains(".jpg")){//JPG Image file
		contentType="image/jpeg";
	}else if(id.contains(".bmp")){//BMP image file
		contentType="image/bmp";
	}else if(id.contains(".gif")){//GIF movie file
		contentType="image/gif";
	}else if(id.contains(".mpeg")||id.contains(".mpg")||id.contains(".mpe")){//video file
		contentType="aduio/mpeg";
	}else if(id.contains(".mov")||id.contains(".qt")){//Quicktime video file
		contentType="image/mov";
	}else if(id.contains(".doc")||id.contains(".dot")){//MS Word file
		contentType="application/msword";
	}else if(id.contains(".ppt")||id.contains(".pps")){//MS Powerpoint file
		contentType="application/vnd.ms-powerpoint";
	}else if(id.contains(".txt")||id.contains(".bas")||id.contains(".c")){
		contentType="text/plain";
	}else if(id.contains(".html")||id.contains(".htm")){
		contentType="text/html";
	}else if(id.contains(".xml")||id.contains(".xhtml")){
		contentType="text/xml";
	}


	//Download a "pdf" file - gets the file name from the
    log.debug("The content Type is "+contentType);
    
    ServletContext application = servlet.getServletContext();
    
    StreamInfo streamInfo = null;
    // for help files, ensure the id does not contain any ".." so the request can't
    // escape from the docs directory
    if ("help".equals(type) && id.indexOf("..") < 0){
      String path = "/docs/"+id;
      response.setHeader("Content-disposition","attachment; fileName="+id);
      streamInfo = new ResourceStreamInfo(contentType,application, path);
    }
    else if("process".equals(type)){
    	 ApplicationContext ctx = WebApplicationContextUtils
         .getRequiredWebApplicationContext(servlet.getServletContext());
    	WorkflowManager workflowManager = (WorkflowManager) ctx.getBean("workflowManager");
     	ProcessDefinition processDefinition = workflowManager.findProcessDefinition(new Long(id));
    	response.setHeader("Content-disposition","attachment; fileName=" + "processdefinition.xml");
    	streamInfo = new ByteArrayStreamInfo(contentType, processDefinition.getFileDefinition().getBytes("processdefinition.xml"));
    }
    else if("project".equals(type)){
    	 ApplicationContext ctx = WebApplicationContextUtils
         .getRequiredWebApplicationContext(servlet.getServletContext());
    	ProjectManager projectManager = (ProjectManager) ctx.getBean("projectManager");
    	Project project = projectManager.getProject(new Long(id));
    	response.setHeader("Content-disposition","attachment; fileName=" + "project.xml");
    	streamInfo = new ByteArrayStreamInfo(contentType, project.getData());
    } 
    //return new FileStreamInfo(contentType, file);
    return streamInfo;
  }
  
  protected class ByteArrayStreamInfo implements StreamInfo {
      
      protected String contentType;
      protected byte[] bytes;
      
      public ByteArrayStreamInfo(String contentType, byte[] bytes) {
          this.contentType = contentType;
          this.bytes = bytes;
      }
      
      public String getContentType() {
          return contentType;
      }

      public InputStream getInputStream() throws IOException {
          return new ByteArrayInputStream(bytes);
      }
  }

}
