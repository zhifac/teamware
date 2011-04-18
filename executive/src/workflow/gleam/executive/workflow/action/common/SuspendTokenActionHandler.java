/**
 *
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
