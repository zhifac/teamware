/*
 *  RetryActionHandler.java
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
package gleam.executive.workflow.action.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.graph.exe.ExecutionContext;
import gleam.executive.workflow.util.JPDLConstants;
import gleam.executive.workflow.sm.JbpmHandlerProxy;

public class RetryActionHandler extends JbpmHandlerProxy {

	private static final long serialVersionUID = 1L;

	protected final Log log = LogFactory.getLog(getClass());

	/*
	 *  <inOutVarItemCSVList>retryCSVList</inOutVarItemCSVList>
	 *  <outVarItemCSVList>documentCSVList</outVarItemCSVList>
	 */

	private String outVarItemCSVList;

	private String inOutVarItemCSVList;


	public void execute(ExecutionContext context) throws Exception {
		log.debug("@@@@@@@@@@@@@@@@@@@@@@@@");
		log.debug("RetryActionHandler START");
		String retryCSVList = (String) context
		.getVariable(getInOutVarItemCSVList());
        log.debug("@@@@@@@ retryCSVList " + retryCSVList);

        if(retryCSVList != null && !"".equals(retryCSVList)){
           // set document list (docs tha tshould be processed by GAS)
           context.setVariable(getOutVarItemCSVList(), retryCSVList);
           // reset retry list
           context.setVariable(getInOutVarItemCSVList(), "");
           context.leaveNode(JPDLConstants.TRANSITION_YES);
        }
        else {
           context.leaveNode(JPDLConstants.TRANSITION_NO);
        }

		log.debug("RetryActionHandler END");
		log.debug("@@@@@@@@@@@@@@@@@@@@@@@@");
	}

	public String getInOutVarItemCSVList() {
		return inOutVarItemCSVList;
	}

	public void setInOutVarItemCSVList(String inOutVarItemCSVList) {
		this.inOutVarItemCSVList = inOutVarItemCSVList;
	}

	public String getOutVarItemCSVList() {
		return outVarItemCSVList;
	}

	public void setOutVarItemCSVList(String outVarItemCSVList) {
		this.outVarItemCSVList = outVarItemCSVList;
	}




}


