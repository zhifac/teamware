/*
 *  ProcessInstanceListDecorator.java
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
package gleam.executive.webapp.displaytag;

import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import gleam.executive.util.GATEUtil;
import gleam.executive.webapp.wrapp.ProcessInstanceWrapper;
import gleam.executive.workflow.util.JPDLConstants;
import gleam.executive.workflow.util.WorkflowUtil;

import org.displaytag.decorator.TotalTableDecorator;
/**
 *A custom list wrapper that override the TableDecorator
 */
public class ProcessInstanceListDecorator extends TotalTableDecorator {

  String checkBox;
  String corpusName;
  String corpusNameWithoutLinks;



/**
   * Creates a new Wrapper decorator who's job is to reformat some of the
   * data located in our forms.
   */

  public ProcessInstanceListDecorator() {
   super();
  }

  
  public String getCheckBox(){
    ProcessInstanceWrapper processInstanceWrapper = (ProcessInstanceWrapper) this.getCurrentRowObject();
    String pId = new Long(processInstanceWrapper.getId()).toString();
    if(processInstanceWrapper.getParentId()==0){
    return "<input type=\"checkbox\" name=\"rowId\" value=\""
     + pId
     + "\" />";
    }
    else {
    	return "&nbsp;";
    }
  }
  
  public String addRowClass(){
	  String className = "";
	  ProcessInstanceWrapper processInstanceWrapper = (ProcessInstanceWrapper) this.getCurrentRowObject();
	  if(processInstanceWrapper.getParentId()!=0){
		  className = "hide";
	  }
      return className;
  }
  
  public String addRowId(){
	  String rowId = "";
	  ProcessInstanceWrapper processInstanceWrapper = (ProcessInstanceWrapper) this.getCurrentRowObject();
	  if(processInstanceWrapper.getParentId()!=0){
		  rowId = "sub";
	  }
	  else rowId = "main";
      return rowId;
  }
  
  public String getCorpusName(){
	  ProcessInstanceWrapper processInstanceWrapper = (ProcessInstanceWrapper) this.getCurrentRowObject();
	  String result = "";
	  // if there is no corpus assigned to process (setup)
	  String corpusId = processInstanceWrapper.getCorpusId();
	  if(corpusId!=null && !"".equals(corpusId)){
		  String corpusName = GATEUtil.extractNameOfGATEEntity(corpusId);
		  Collection<String> list = new ArrayList<String>();
		  list.add(corpusName);
		  result = WorkflowUtil.collectionToFormattedCSVString(((HttpServletRequest)getPageContext().getRequest()).getContextPath(), list, JPDLConstants.CORPUS_LINK_FORMAT, corpusId);
	  }	
	  return result;
	  
  }
  
  public String getCorpusNameWithoutLinks(){
	  ProcessInstanceWrapper processInstanceWrapper = (ProcessInstanceWrapper) this.getCurrentRowObject();
	  String corpusName = "";
	  String corpusId = processInstanceWrapper.getCorpusId();
	  if(corpusId!=null && !"".equals(corpusId)){
	    corpusName = GATEUtil.extractNameOfGATEEntity(corpusId);
	  }
	  return corpusName;
	  
  }
  
 
  
}

