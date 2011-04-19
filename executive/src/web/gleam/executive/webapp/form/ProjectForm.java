/*
 *  ProjectForm.java
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

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.upload.FormFile;
import org.apache.struts.upload.MultipartRequestHandler;

import gleam.executive.webapp.form.BaseForm;

/**
 * @struts.form name="projectForm" 
 */
public class ProjectForm extends BaseForm implements java.io.Serializable {

	public static final String ERROR_PROPERTY_MAX_LENGTH_EXCEEDED = "MaxLengthExceeded";

    protected String description;

    protected String id;

    protected String name;

    protected FormFile file;

    protected String lastUpdate;

    protected String userId;
    
    protected Integer version;
    
    protected Boolean enabled;

    /** Default empty constructor. */
    public ProjectForm() {}

    public String getDescription()
    {
        return this.description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    public String getId()
    {
        return this.id;
    }


    public void setId( String id )
    {
        this.id = id;
    }

    public String getName()
    {
        return this.name;
    }
   /**
    * @struts.validator type="required"
    * @struts.validator type="mask" msgkey="projectForm.invalidName"
    * @struts.validator-var name="mask" value="^[a-zA-Z0-9_\s\.-]+$"
    */

    public void setName( String name )
    {
        this.name = name;
    }

 

    public String getLastUpdate()
    {
        return this.lastUpdate;
    }
   /**
    */

    public void setLastUpdate( String lastUpdate )
    {
        this.lastUpdate = lastUpdate;
    }

    public String getUserId()
    {
        return this.userId;
    }
   /**
    */

    public void setUserId( String userId )
    {
        this.userId = userId;
    }

       
    /**
     * @see org.apache.struts.action.ActionForm#reset(org.apache.struts.action.ActionMapping,
     *                                                javax.servlet.http.HttpServletRequest)
     */
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        // reset any boolean data types to false

    }
    
    /**
    * @struts.validator type="required"
    */
	public FormFile getFile() {
		return file;
	}

	public void setFile(FormFile file) {
		this.file = file;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
	    ActionErrors errors = null;
	    // has the maximum length been exceeded?
	    Boolean maxLengthExceeded = (Boolean)request
	            .getAttribute(MultipartRequestHandler.ATTRIBUTE_MAX_LENGTH_EXCEEDED);
	    if((maxLengthExceeded != null) && (maxLengthExceeded.booleanValue())) {
	      errors = new ActionErrors();
	      errors.add(ERROR_PROPERTY_MAX_LENGTH_EXCEEDED, new ActionMessage(
	              "maxLengthExceeded"));
	    }
	    return errors;
	  }

}
