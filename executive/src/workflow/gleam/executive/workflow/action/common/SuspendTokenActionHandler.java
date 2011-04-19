/*
 *  SuspendTokenActionHandler.java
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

import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.Token;
import gleam.executive.workflow.sm.JbpmHandlerProxy;

/**
 * @author agaton
 *
 */
public class SuspendTokenActionHandler extends JbpmHandlerProxy {

	protected final Log log = LogFactory.getLog(getClass());

	public void execute(ExecutionContext context) throws Exception {
		log.debug("@@@@@@@@@@@@@@@@@@@@@@@@");
		log.debug("SuspendTokenActionHandler START");

		// list living tokens
		Token token = context.getToken();
		log.debug("I AM IN TOKEN: " + token.getName());
		if (!token.isSuspended()) {
			token.suspend();
			log.debug("token suspended");
			// if there are children resume them
			Map<String, Token> children = token.getActiveChildren();
			// resume them
			if (children != null) {
				Iterator<String> iter = children.keySet().iterator();
				while (iter.hasNext()) {
					String tokenName = iter.next();
					Token child = children.get(tokenName);
					child.resume();
					log.debug("RESUMED child " + child.getName());
				}
			}
		} else {
			log
					.warn("token has been already suspended. Cannot suspend it now!");
		}

		log.debug("SuspendTokenActionHandler END");
		log.debug("@@@@@@@@@@@@@@@@@@@@@@@@");
	}

}
