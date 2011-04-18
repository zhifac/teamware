package gleam.executive.webapp.displaytag;

import javax.servlet.http.HttpServletRequest;

import gleam.executive.model.Document;
import org.displaytag.decorator.TotalTableDecorator;
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

