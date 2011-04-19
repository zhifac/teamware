/*
 *  DocumentListDecorator.java
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

import javax.servlet.http.HttpServletRequest;

import gleam.executive.model.Document;
import org.displaytag.decorator.TotalTableDecorator;
/**
 *A custom list wrapper that override the TableDecorator
 */
public class DocumentListDecorator extends TotalTableDecorator {
  String checkBox;

  /**
   * Creates a new Wrapper decorator who's job is to reformat some of the
   * data located in our forms.
   */

  public DocumentListDecorator() {
   super();
  }

  /**
   *  Returns an xhtml-compliant checkbox used to select multiple rows 
   *  in document list page.
   */
  public String getCheckBox() {
   Document form = (Document) this.getCurrentRowObject();
   String docId = form.getDocumentID();
   HttpServletRequest request = (HttpServletRequest)getPageContext().getRequest();
   Boolean canDelete = (Boolean)request.getAttribute("canDelete");
   if(canDelete){
	   return "<input type=\"checkbox\" name=\"rowId\" value=\""
	    + docId
	    + "\" />";
   }
   
   else {
	   return "<input type=\"checkbox\" name=\"rowId\" value=\""
	    + docId
	    + "\" disabled />";
   }
   
  
  }

  
  
}

