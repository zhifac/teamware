package gleam.executive.workflow.action.common;

import gleam.executive.workflow.util.WorkflowUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.graph.exe.ExecutionContext;
import gleam.executive.workflow.util.JPDLConstants;
import gleam.executive.workflow.sm.JbpmHandlerProxy;

public class PrepareDocumentForSuperAnnotationActionHandler extends
		JbpmHandlerProxy {

	/*
	 * <inOutVarDocumentCSVList>documentCSVList</inOutVarDocumentCSVList>
	 * <inVarDocumentId>documentId</inVarDocumentId> <inVarPerformer>performer</inVarPerformer>
	 *
	 */

	// target variables picked from JPDL
	String inVarDocumentId;

	String inOutVarDocumentCSVList;

	String inVarPerformer;

	protected final Log log = LogFactory.getLog(getClass());

	public void execute(ExecutionContext context) throws Exception {
		log.debug("@@@@@@@@@@@@@@@@@@@@@@@@");
		log.debug("PrepareDocumentForSuperAnnotationActionHandler START");
		// obtain targetProperties
		String documentCSVList = (String) context.getContextInstance()
				.getVariable(getInOutVarDocumentCSVList());
		log.debug("&&&&&&&&&&&&& documentCSVList " + documentCSVList);

		String documentId = (String) context.getVariable(getInVarDocumentId());
		log.debug("&&&&&&&&&&&&& documentId " + documentId);

		String superAnnotationCSVString = (String) context
				.getVariable(JPDLConstants.SUPERANNOTATED_BY_PREFIX
						+ documentId);
		log.debug("superAnnotationCSVString " + superAnnotationCSVString);

		String performer = (String) context.getContextInstance().getVariable(
				getInVarPerformer(), context.getToken());
		log.debug("performer " + performer);

		log.debug("Set variable " + JPDLConstants.SUPERANNOTATED_BY_PREFIX
				+ documentId + " to value " + performer);
		context.setVariable(
				JPDLConstants.SUPERANNOTATED_BY_PREFIX + documentId, performer);

		String updatedDocumentCSVList = WorkflowUtil.addDocumentToList(
				documentId, documentCSVList);
		log.debug("Set variable " + getInOutVarDocumentCSVList() + " to value "
				+ updatedDocumentCSVList);

		context.setVariable(getInOutVarDocumentCSVList(),
				updatedDocumentCSVList);

		log.debug("Leave node with  default transition ");
		context.leaveNode();

		log.debug("PrepareDocumentForSuperAnnotationActionHandler END");
		log.debug("@@@@@@@@@@@@@@@@@@@@@@@@");
	}

	public String getInOutVarDocumentCSVList() {
		return inOutVarDocumentCSVList;
	}

	public void setInOutVarDocumentCSVList(String inOutVarDocumentCSVList) {
		this.inOutVarDocumentCSVList = inOutVarDocumentCSVList;
	}

	public String getInVarDocumentId() {
		return inVarDocumentId;
	}

	public void setInVarDocumentId(String inVarDocumentId) {
		this.inVarDocumentId = inVarDocumentId;
	}

	public String getInVarPerformer() {
		return inVarPerformer;
	}

	public void setInVarPerformer(String inVarPerformer) {
		this.inVarPerformer = inVarPerformer;
	}

}
