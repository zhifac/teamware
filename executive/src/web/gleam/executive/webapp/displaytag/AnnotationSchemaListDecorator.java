package gleam.executive.webapp.displaytag;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import gleam.executive.model.AnnotationSchema;
import gleam.executive.model.Corpus;
import gleam.executive.webapp.wrapp.ProcessInstanceWrapper;
import gleam.executive.workflow.util.JPDLConstants;
import gleam.executive.workflow.util.WorkflowUtil;

import org.displaytag.decorator.TotalTableDecorator;
import org.springframework.util.StringUtils;
/**
 *A custom list wrapper that override the TotalTableDecorator
 * Copyright (c) 1998-2007, The University of Sheffield.
 *
 * This file is part of GATE (see http://gate.ac.uk/), and is free
 * software, licenced under the GNU Library General Public License,
 * Version 2, June 1991 (in the distribution as file licence.html, and
 * also available at http://gate.ac.uk/gate/licence.html).
 *
 * @author <a href="mailto:agaton@dcs.shef.ac.uk">Milan Agatonovic</a>
 *
 */
public class AnnotationSchemaListDecorator extends TotalTableDecorator {

 

String checkBox;
  String projects;
  String projectsWithoutLinks;
  /**
   * Creates a new Wrapper decorator who's job is to reformat some of the
   * data located in our forms.
   */

  public AnnotationSchemaListDecorator() {
   super();
  }

  
  /**
   *  Returns an xhtml-compliant checkbox used to select multiple rows 
   *  in annotation schema list page.
   */
  public String getCheckBox() {
   AnnotationSchema form = (AnnotationSchema) this.getCurrentRowObject();
   String name = form.getName();
   HttpServletRequest request = (HttpServletRequest)getPageContext().getRequest();
   Map<String, List<ProcessInstanceWrapper>> annotationSchemaProcessInstanceWrapperMap = (Map<String, List<ProcessInstanceWrapper>>)request.getAttribute("annotationSchemaProcessMap");
   List<ProcessInstanceWrapper> list = annotationSchemaProcessInstanceWrapperMap.get(name);
   if(list==null){
   return "<input type=\"checkbox\" name=\"rowId\" value=\""
    + name
    + "\" />";
   }
   else {
	   return "<input type=\"checkbox\" name=\"rowId\" value=\""
	    + name
	    + "\" disabled />";
   }
  }
  
  
  public String getProjects() {
	String result = "";
	HttpServletRequest request = (HttpServletRequest)getPageContext().getRequest();
	Map<String, List<ProcessInstanceWrapper>> annotationSchemaProcessInstanceWrapperMap = (Map<String, List<ProcessInstanceWrapper>>)request.getAttribute("annotationSchemaProcessMap");
	AnnotationSchema form = (AnnotationSchema) this.getCurrentRowObject();
	String asName = form.getName();
	
	List<ProcessInstanceWrapper> list = annotationSchemaProcessInstanceWrapperMap.get(asName);
	if(list!=null){
		result = formatProcessInstanceWrapperList(list, true);
    }
	return result;
  }  
  
  public String getProjectsWithoutLinks() {
		String result = "";
		HttpServletRequest request = (HttpServletRequest)getPageContext().getRequest();
		Map<String, List<ProcessInstanceWrapper>> annotationSchemaProcessInstanceWrapperMap = (Map<String, List<ProcessInstanceWrapper>>)request.getAttribute("annotationSchemaProcessMap");
		AnnotationSchema form = (AnnotationSchema) this.getCurrentRowObject();
		String asName = form.getName();
		
		List<ProcessInstanceWrapper> list = annotationSchemaProcessInstanceWrapperMap.get(asName);
		if(list!=null){
			result = formatProcessInstanceWrapperList(list, false);
	    }
		return result;
	  }  
  
  private String formatProcessInstanceWrapperList(List<ProcessInstanceWrapper> list, boolean includeLinks){
	List<String> links = new ArrayList<String>();
	Iterator<ProcessInstanceWrapper> it = list.iterator();
	
	String prefix = ((HttpServletRequest)getPageContext().getRequest()).getContextPath();
	String template = JPDLConstants.MAIN_PROCESS_LINK_FORMAT;
	while(it.hasNext()){
		ProcessInstanceWrapper piw = it.next();
		String link = "";
		if(includeLinks){
		String[] params = {String.valueOf(piw.getId())};
	     link = "<span class=\"" + piw.getStatus() + "\"><a title=\"" +piw.getStatus() + "\" href=\"" + prefix + WorkflowUtil.messageFormatter(template, params) + "\">" + piw.getName() + "</a></span>";
		}
		else {
			link = piw.getName();
		}
		links.add(link);
	}
		
	  return StringUtils.collectionToDelimitedString(links, "<br/>");
  }
  
 



  
  
}

