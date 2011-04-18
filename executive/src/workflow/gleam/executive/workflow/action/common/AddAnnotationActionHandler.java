package gleam.executive.workflow.action.common;

import gleam.executive.service.DocServiceManager;
import gleam.executive.workflow.util.WorkflowUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.graph.exe.ExecutionContext;
import gleam.executive.workflow.util.JPDLConstants;
import gleam.executive.workflow.sm.JbpmHandlerProxy;


public class AddAnnotationActionHandler extends JbpmHandlerProxy {

	private String inVarPerformer;

	private String inVarDocumentId;

	private String inVarAnnotationSetName;

	private DocServiceManager docServiceManager;

	protected final Log log = LogFactory.getLog(getClass());

	public void execute(ExecutionContext context) throws Exception {
		log.debug("@@@@@@@@@@@@@@@@@@@@@@@@");
		log.debug("AddAnnotationActionHandler START");
		// obtain targetProperties

		String performer = (String) context.getContextInstance().getVariable(
				getInVarPerformer(), context.getToken());
		log.debug("&&&&&&&&&&&&& performer " + performer);
		String documentId = (String) context.getContextInstance().getVariable(
				getInVarDocumentId(), context.getToken());
		log.debug("&&&&&&&&&&&&& documentId " + documentId);

		String annotSetName = (String) context.getContextInstance()
				.getVariable(getInVarAnnotationSetName(), context.getToken());
		log.debug("&&&&&&&&&&&&& annotationSetName " + annotSetName);

		String currentDocumentFinishedAnnotationCSVString = (String) context
				.getVariable(JPDLConstants.FINISHED_BY_PREFIX + documentId);
		String updatedDocumentFinishedAnnotationCSVString = WorkflowUtil
				.markDocumentAsAnnotated(performer,
						currentDocumentFinishedAnnotationCSVString);
		log.debug("Set variable "
				+ JPDLConstants.FINISHED_BY_PREFIX
				+ documentId
				+ " to: "
				+ (String) context.getVariable(JPDLConstants.FINISHED_BY_PREFIX
						+ documentId) + " to value "
				+ updatedDocumentFinishedAnnotationCSVString);
		context.setVariable(JPDLConstants.FINISHED_BY_PREFIX + documentId,
				updatedDocumentFinishedAnnotationCSVString);

		log.debug("Adding document feature with key \"safe.asname." + annotSetName
				+ "\" and the user who annotated the document \"" + performer
				+ "\"");
		docServiceManager.setDocumentFeature(documentId, "safe.asname."+annotSetName,
				performer);

		log.debug("AddAnnotationActionHandler END");

		log.debug("@@@@@@@@@@@@@@@@@@@@@@@@");
	}

	public String getInVarPerformer() {
		return inVarPerformer;
	}

	public void setInVarPerformer(String inVarPerformer) {
		this.inVarPerformer = inVarPerformer;
	}

	public String getInVarDocumentId() {
		return inVarDocumentId;
	}

	public void setInVarDocumentId(String inVarDocumentId) {
		this.inVarDocumentId = inVarDocumentId;
	}

	public String getInVarAnnotationSetName() {
		return inVarAnnotationSetName;
	}

	public void setInVarAnnotationSetName(String inVarAnnotationSetName) {
		this.inVarAnnotationSetName = inVarAnnotationSetName;
	}

	public DocServiceManager getDocServiceManager() {
		return docServiceManager;
	}

	public void setDocServiceManager(DocServiceManager docServiceManager) {
		this.docServiceManager = docServiceManager;
	}

}
