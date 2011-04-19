/*
 *  CreateTokenForEachItemInCSVListActionHandler.java
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
import org.jbpm.graph.def.Node;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.Token;
import org.springframework.util.StringUtils;
import gleam.executive.workflow.sm.JbpmHandlerProxy;

public class CreateTokenForEachItemInCSVListActionHandler extends
		JbpmHandlerProxy {

	private static final long serialVersionUID = 1L;

	protected final Log log = LogFactory.getLog(getClass());

	/*
	 * <inVarItemCSVList>annotatorCSVList</inVarItemCSVList>
	 * <outVarItem>performer</outVarItem>
	 */

	private String outVarItem;

	private String inVarItemCSVList;

	public void execute(ExecutionContext context) throws Exception {
		log.debug("@@@@@@@@@@@@@@@@@@@@@@@@");
		log.debug("CreateTokenForEachItemInCSVListActionHandler START");

		// obtain targetProperties
		String itemCSVList = (String) context
				.getVariable(getInVarItemCSVList());
		log.debug("@@@@@@@ itemCSVList " + itemCSVList);

		String[] itemNames = StringUtils
				.commaDelimitedListToStringArray(itemCSVList);
		int numberOfItems = itemNames.length;
		log.debug("@@@@@@@ numOfItems " + numberOfItems);

		final Token rootToken = context.getToken();
		log.debug("@@@@@@@ root token " + rootToken.getName());
		final Node node = context.getNode();
		log.debug("@@@@@@@ node " + node.getName());
        if(numberOfItems > 1){
		for (int j = 0; j < numberOfItems; j++) {
			String tokenName = itemNames[j] + "/" + System.currentTimeMillis();
			final Token newToken = new Token(rootToken, tokenName);
			log.debug("@@@@@@@ created new token ID " + newToken.getId()
					+ "   NAME: " + newToken.getName());
			newToken.setTerminationImplicit(true);
			context.getJbpmContext().getSession().save(newToken);
			final ExecutionContext newExecutionContext = new ExecutionContext(
					newToken);
			// create new performer local variable
			newExecutionContext.getContextInstance().createVariable(
					getOutVarItem(), itemNames[j], newToken);
			log.debug("@@@@@@@ created variable " + itemNames[j]
					+ " for token " + newToken.getName());
			newExecutionContext.getJbpmContext().getSession().save(newToken);

			node.leave(newExecutionContext);
			log.debug("NODE LEFT");
		}
        }
        else {
        	//do not fork
        	context.setVariable(getOutVarItem(), itemNames[0]);
        }

		log.debug("CreateTokenForEachItemInCSVListActionHandler END");
		log.debug("@@@@@@@@@@@@@@@@@@@@@@@@");
	}

	public String getInVarItemCSVList() {
		return inVarItemCSVList;
	}

	public void setInVarItemCSVList(String inVarItemCSVList) {
		this.inVarItemCSVList = inVarItemCSVList;
	}

	public String getOutVarItem() {
		return outVarItem;
	}

	public void setOutVarItem(String outVarItem) {
		this.outVarItem = outVarItem;
	}

}
