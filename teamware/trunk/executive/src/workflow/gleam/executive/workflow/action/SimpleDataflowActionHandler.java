/*
 *  SimpleDataflowActionHandler.java
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
package gleam.executive.workflow.action;

import java.util.Map;

import gleam.executive.workflow.sm.JbpmDataflowHandlerProxy;
import gleam.executive.workflow.util.JPDLConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.jbpm.graph.exe.ExecutionContext;

public class SimpleDataflowActionHandler extends JbpmDataflowHandlerProxy {

	private static final long serialVersionUID = 1L;

	Element targetProperties;
	protected final Log log = LogFactory.getLog(getClass());

	/*
	 *  <inOutVarItemCSVList>retryCSVList</inOutVarItemCSVList>
	 *  <outVarItemCSVList>documentCSVList</outVarItemCSVList>
	 */
/*
	private String mode;

	private String message;


	public String getMessage() {
		return message;
	}


	public void setMessage(String message) {
		this.message = message;
	}


	public String getMode() {
		return mode;
	}


	public void setMode(String mode) {
		this.mode = mode;
	}

*/
	public void execute(ExecutionContext context) throws Exception {
		log.debug("@@@@@@@@@@@@@@@@@@@@@@@@");
		log.debug("SimpleDataflowActionHandler START");

		Map<String, String> variableMap = fetchAndValidateVariables(context,
                this.getClass().getName(),
                targetProperties);

		String mode = (String)variableMap.get(JPDLConstants.MODE);
		String message = (String)variableMap.get(JPDLConstants.MESSAGE);
		log.debug("@@@@@@@@@@@@ MESSAGE: "+ message);
		log.debug("SimpleDataflowActionHandler END");
		log.debug("@@@@@@@@@@@@@@@@@@@@@@@@");
	}

	public Element getTargetProperties() {
		return targetProperties;
	}

	public void setTargetProperties(Element targetProperties) {
		this.targetProperties = targetProperties;
	}




}
