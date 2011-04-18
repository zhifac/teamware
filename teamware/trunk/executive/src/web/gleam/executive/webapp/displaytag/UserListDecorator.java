package gleam.executive.webapp.displaytag;


import gleam.executive.model.User;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.displaytag.decorator.TotalTableDecorator;
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
public class UserListDecorator extends TotalTableDecorator {

private static Log log = LogFactory.getLog(UserListDecorator.class);


String deleteCheckBox;
String enableCheckBox;

  /**
   * Creates a new Wrapper decorator who's job is to reformat some of the
   * data located in our forms.
   */

  public UserListDecorator() {
   super();
  }

  
  /**
   *  Returns an xhtml-compliant checkbox used to select multiple rows 
   *  in user list page.
   */
  public String getDeleteCheckBox() {
   User user = (User) this.getCurrentRowObject();
   Long id = user.getId();
   
   return "<input type=\"checkbox\" name=\"rowDeleteId\" value=\""
   + id
   + "\" />";
   
  }
  
  /**
   *  Returns an xhtml-compliant checkbox used to select multiple rows 
   *  in user list page.  Also includes a hidden field with the user ID
   *  so the target action can tell which users were listed in the form
   *  (including those whose enable check box is not ticked).
   */
  public String getEnableCheckBox() {
   User user = (User) this.getCurrentRowObject();
   
   Long id = user.getId();
   boolean enabled = user.isEnabled();
   //log.debug("user: "+ id + "  enabled: "+ enabled);
   StringBuilder b = new StringBuilder(
       "<input type=\"hidden\" name=\"rowUserId\" value=\"" + id + "\" />");
   if(enabled){
	   b.append("<input type=\"checkbox\" name=\"rowEnableId\" value=\""
	    + id + "\" checked />");
   }
   else {
	   b.append("<input type=\"checkbox\" name=\"rowEnableId\" value=\""
	    + id + "\" />");
   }
   return b.toString();
  
  }
  
  


  
  
}

