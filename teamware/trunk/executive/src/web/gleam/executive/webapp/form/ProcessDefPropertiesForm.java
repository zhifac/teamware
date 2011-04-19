/*
 *  ProcessDefPropertiesForm.java
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

import gleam.executive.workflow.model.SwimlaneBean;

import java.util.List;

/**
 * @author <a href="M.Agatonovic@dcs.shef.ac.uk">Milan Agatonovic</a>
 * 
 * @struts.form name="processDefPropertiesForm"
 */
public class ProcessDefPropertiesForm extends BaseForm {
	private static final long serialVersionUID = 1967999873896482349L;

	private List<SwimlaneBean> swimlanes;

	public List<SwimlaneBean> getSwimlanes() {
		return swimlanes;
	}

	public void setSwimlanes(List<SwimlaneBean> swimlanes) {
		this.swimlanes = swimlanes;
	}

}
