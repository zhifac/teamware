package gleam.executive.webapp.displaytag;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import gleam.executive.model.Corpus;
import gleam.executive.webapp.wrapp.ProcessInstanceWrapper;
import gleam.executive.workflow.util.JPDLConstants;
import gleam.executive.workflow.util.WorkflowUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.displaytag.decorator.TotalTableDecorator;
import org.springframework.util.StringUtils;
/**
 *A custom list wrapper that override the TableDecorator
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
public class CorpusListDecorator extends TotalTableDecorator {
	
  private static Log log = LogFactory.getLog(CorpusListDecorator.class);
	
  String checkBox;
  String projects;
  String projectsWithoutLinks;
  String startProjectLink;

  

/**
   * Creates a new Wrapper decorator who's job is to reformat some of the
   * data located in our forms.
   */

  public CorpusListDecorator() {
   super();
  }

  /**
   *  Returns an xhtml-compliant checkbox used to select multiple rows 
   *  in document list page.
   */
  public String getCheckBox() {
   Corpus form = (Corpus) this.getCurrentRowObject();
   String corpusId = form.getCorpusID();
   HttpServletRequest request = (HttpServletRequest)getPageContext().getRequest();
   Map<String, List<ProcessInstanceWrapper>> corpusProcessInstanceWrapperMap = (Map<String, List<ProcessInstanceWrapper>>)request.getAttribute("corpusProcessMap");
   List<ProcessInstanceWrapper> list = corpusProcessInstanceWrapperMap.get(corpusId);
   
   boolean canDelete = true;
   if(list!=null){
	Iterator<ProcessInstanceWrapper> it = list.iterator();
		while(it.hasNext() && canDelete){
			ProcessInstanceWrapper piw = it.next();
			if(piw.isRunning() || piw.isSuspended()){
				canDelete=false;
				break;
			}
		}
   }	
   if(canDelete){
	   return "<input type=\"checkbox\" name=\"rowId\" value=\""
	    + corpusId
	    + "\" />";
   }
   
   else {
	   return "<input type=\"checkbox\" name=\"rowId\" value=\""
	    + corpusId
	    + "\" disabled />";
   }
  }

  
  public String getStartProjectLink() {
	  HttpServletRequest request = (HttpServletRequest)getPageContext().getRequest();
		Map<String, List<ProcessInstanceWrapper>> corpusProcessInstanceWrapperMap = (Map<String, List<ProcessInstanceWrapper>>)request.getAttribute("corpusProcessMap");
		Corpus form = (Corpus) this.getCurrentRowObject();
		String corpusId = form.getCorpusID();
		
		List<ProcessInstanceWrapper> list = corpusProcessInstanceWrapperMap.get(corpusId);
		
	  
		return formatStartProjectLink(list, corpusId);
	}
  
  private String formatStartProjectLink(List<ProcessInstanceWrapper> list, String corpusId){
        String result = "";
        String prefix = ((HttpServletRequest)getPageContext().getRequest()).getContextPath();
		String template = JPDLConstants.LOAD_PROJECT_LINK_FORMAT;
		boolean canStart = true;
        if(list!=null){
		Iterator<ProcessInstanceWrapper> it = list.iterator();
			while(it.hasNext() && canStart){
				ProcessInstanceWrapper piw = it.next();
				if(piw.isRunning() || piw.isSuspended()){
					canStart=false;
					break;
				}
			}
        }	
        if(canStart){
			String[] params = {corpusId};
			result = "<a onmouseover=\"ajax_showTooltip('ajaxtooltip/info/createProcessInstanceBtn.jsp',this);return false\" onmouseout=\"ajax_hideTooltip()\" href=\"" 
				   + prefix + WorkflowUtil.messageFormatter(template, params) + "\"><img class=\"icon\" src=\"" + prefix + "/images/start.gif\"/></a>";
		}
		return result;
	  }
 
  public String getProjects() {
	String result = "";
	HttpServletRequest request = (HttpServletRequest)getPageContext().getRequest();
	Map<String, List<ProcessInstanceWrapper>> corpusProcessInstanceWrapperMap = (Map<String, List<ProcessInstanceWrapper>>)request.getAttribute("corpusProcessMap");
	Corpus form = (Corpus) this.getCurrentRowObject();
	String corpusId = form.getCorpusID();
	
	List<ProcessInstanceWrapper> list = corpusProcessInstanceWrapperMap.get(corpusId);
	if(list!=null){
		result = formatProcessInstanceWrapperList(list, true);
    }
	return result;
  }  
  
  public String getProjectsWithoutLinks() {
		String result = "";
		HttpServletRequest request = (HttpServletRequest)getPageContext().getRequest();
		Map<String, List<ProcessInstanceWrapper>> corpusProcessInstanceWrapperMap = (Map<String, List<ProcessInstanceWrapper>>)request.getAttribute("corpusProcessMap");
		Corpus form = (Corpus) this.getCurrentRowObject();
		String corpusId = form.getCorpusID();
		
		List<ProcessInstanceWrapper> list = corpusProcessInstanceWrapperMap.get(corpusId);
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

