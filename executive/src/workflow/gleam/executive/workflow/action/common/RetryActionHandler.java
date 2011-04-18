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


