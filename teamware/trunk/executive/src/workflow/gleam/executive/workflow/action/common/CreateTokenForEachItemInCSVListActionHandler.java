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
