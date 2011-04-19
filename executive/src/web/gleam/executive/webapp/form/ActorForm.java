/*
 *  ActorForm.java
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
package gleam.executive.webapp.form;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMapping;


/**
 * @struts.form name="actorForm" 
 */
public class ActorForm extends gleam.executive.webapp.form.BaseForm implements java.io.Serializable {

    protected String processInstanceId;
    
    protected String[] annotators;
  
    //protected String[] curators;
    
    protected String manager;

   
       
    public String[] getAnnotators() {
	    return annotators;
	}
    
  
	public void setAnnotators(String[] annotators) {
		this.annotators = annotators;
	}
	
	/*
	public String[] getCurators() {
		return curators;
	}
	
	public void setCurators(String[] curators) {
		this.curators = curators;
	}
	*/
	
	
	public String getManager() {
		return manager;
	}
	
	/**
	 * @struts.validator type="required"
	 */
	public void setManager(String manager) {
		this.manager = manager;
	}
	
	public String getProcessInstanceId() {
		return processInstanceId;
	}

	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}
    
	
	/**
     * @see org.apache.struts.action.ActionForm#reset(org.apache.struts.action.ActionMapping,
     *                                                javax.servlet.http.HttpServletRequest)
     */
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        // reset any boolean data types to false

    }
    
}
