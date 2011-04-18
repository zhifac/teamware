package gleam.executive.workflow.action.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.graph.exe.ExecutionContext;
import gleam.executive.workflow.util.CSVUtil;
import gleam.executive.workflow.sm.JbpmHandlerProxy;

public class UpdateCSVListActionHandler extends JbpmHandlerProxy {

	private static final long serialVersionUID = 1L;

	protected final Log log = LogFactory.getLog(getClass());

	/*
	 * <inVarItem>item</inVarItem>
	 * <inVarOperation>operation</inVarOperation>
	 * <inOutVarItemCSVList>itemCSVList</inOutVarItemCSVList>
	 */

	private String inVarItem;

	private String inOutVarItemCSVList;

	private String inVarOperation;

	public void execute(ExecutionContext context) throws Exception {
		log.debug("@@@@@@@@@@@@@@@@@@@@@@@@");
		log.debug("UpdateCSVListActionHandler START");
		String itemCSVList = (String) context
		.getVariable(getInOutVarItemCSVList());
        log.debug("@@@@@@@ itemCSVList " + itemCSVList);
        String item = (String) context
		.getVariable(getInVarItem());
        log.debug("@@@@@@@ item " + item);
        String operation = (String) context
		.getVariable(getInVarOperation());
        log.debug("@@@@@@@ operation " + operation);

        String updatedCSVList = CSVUtil.execute(item, itemCSVList, operation);
        log.debug("@@@@@@@ updatedCSVList " + updatedCSVList);
        context.setVariable(getInOutVarItemCSVList(), updatedCSVList);
        context.leaveNode();
		log.debug("UpdateCSVListActionHandler END");
		log.debug("@@@@@@@@@@@@@@@@@@@@@@@@");
	}

	public String getInOutVarItemCSVList() {
		return inOutVarItemCSVList;
	}

	public void setInOutVarItemCSVList(String inOutVarItemCSVList) {
		this.inOutVarItemCSVList = inOutVarItemCSVList;
	}

	public String getInVarItem() {
		return inVarItem;
	}

	public void setInVarItem(String inVarItem) {
		this.inVarItem = inVarItem;
	}

	public String getInVarOperation() {
		return inVarOperation;
	}

	public void setInVarOperation(String inVarOperation) {
		this.inVarOperation = inVarOperation;
	}


}

