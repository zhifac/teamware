package gleam.executive.workflow.action.harvard;

import gleam.executive.model.AnnotationDifferLaunchBean;
import gleam.executive.model.AnnotatorGUILaunchBean;
import gleam.executive.workflow.util.WorkflowUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.graph.exe.ExecutionContext;
import gleam.executive.workflow.sm.JbpmHandlerProxy;

public class PrepareReviewActionHandler extends JbpmHandlerProxy {

	private static final long serialVersionUID = 1L;

	protected final Log log = LogFactory.getLog(getClass());

	/*
	 * <inVarDocumentId>documentId</inVarDocumentId> <inVarCorpusId>corpusId</inVarCorpusId>
	 * <inVarAnnotationSchemaCSVURLs>annotationSchemaCSVURLs</inVarAnnotationSchemaCSVURLs>
	 * <inVarBaseAnnotationSchemaURL>baseAnnotationSchemaURL</inVarBaseAnnotationSchemaURL>
	 * <inVarTemplateAnnotatorGUIURL>templateAnnotatorGUIURL</inVarTemplateAnnotatorGUIURL>
	 * <inVarTemplateAnnotationDifferURL>templateAnnotationDifferURL</inVarTemplateAnnotationDifferURL>
	 * <inVarDocServiceURL>docServiceURL</inVarDocServiceURL>
	 * <inVarPluginCSVList>pluginCSVList</inVarPluginCSVList>
	 * <outVarAnnotatorGUIURL>annotatorGUIURL</outVarAnnotatorGUIURL>
	 * <outVarAnnotationDifferURL>annotationDifferURL</outVarAnnotationDifferURL>
	 * <outVarAnnotatedBy>annotatedBy</outVarAnnotatedBy>
	 * <outVarCanceledBy>canceledBy</outVarCanceledBy>
	 */
	String inVarCorpusId;

	String inVarDocumentId;

	String inVarAnnotationSchemaCSVURLs;

	String inVarBaseAnnotationSchemaURL;

	String inVarTemplateAnnotatorGUIURL;

	String inVarTemplateAnnotationDifferURL;

	String inVarDocServiceURL;

	String inVarPluginCSVList;

	String outVarAnnotatorGUIURL;

	String outVarAnnotationDifferURL;

	String outVarAnnotatedBy;

	String outVarCanceledBy;

	String outVarDocumentName;

	/**
	 * A message process variable is assigned the value of the message member.
	 * The process variable is created if it doesn't exist yet.
	 */
	public void execute(ExecutionContext context) throws Exception {

		log.debug("PrepareReviewActionHandler START");

		// obtain targetProperties
		String corpusId = (String) context.getVariable(getInVarCorpusId());
		log.debug("&&&&&&&&&&&&& corpusId " + corpusId);

		String documentId = (String) context.getVariable(getInVarDocumentId());
		log.debug("&&&&&&&&&&&&& documentId " + documentId);



		String annotationSchemaCSVURLs = (String) context
				.getVariable(getInVarAnnotationSchemaCSVURLs());
		log.debug("&&&&&&&&&&&&& annotationSchemaCSVURLs "
				+ annotationSchemaCSVURLs);
		String baseAnnotationSchemaURL = (String) context
				.getVariable(getInVarBaseAnnotationSchemaURL());
		log.debug("&&&&&&&&&&&&& baseAnnotationSchemaURL "
				+ baseAnnotationSchemaURL);

		String templateAnnotatorGUIURL = (String) context
				.getVariable(getInVarTemplateAnnotatorGUIURL());
		log.debug("&&&&&&&&&&&&& templateAnnotatorGUIURL "
				+ templateAnnotatorGUIURL);

		String annotationDifferTemplateURL = (String) context
				.getVariable(getInVarTemplateAnnotationDifferURL());
		log.debug("&&&&&&&&&&&&& annotationDifferTemplateURL "
				+ annotationDifferTemplateURL);

		String docServiceURL = (String) context
				.getVariable(getInVarDocServiceURL());
		log.debug("&&&&&&&&&&&&& docServiceURL " + docServiceURL);

		String pluginCSVList = (String) context.getVariable(getInVarPluginCSVList());
		log.debug("&&&&&&&&&&&&& pluginCSVList " + pluginCSVList);


		if (documentId != null) {


			String documentName = WorkflowUtil.findDocumentNameById(documentId);
			log.debug("&&&&&&&&&&&&& documentName " + documentName);
			context.getContextInstance().createVariable(
					getOutVarDocumentName(), documentName, context.getToken());

			if (annotationSchemaCSVURLs != null) {
				AnnotatorGUILaunchBean annotatorGUILaunchBean = new AnnotatorGUILaunchBean();

				annotatorGUILaunchBean.setDocServiceURL(docServiceURL);
				annotatorGUILaunchBean.setDocumentId(documentId.replaceAll("&", "%26"));
				annotatorGUILaunchBean.setAnnotationSchemaCSVURLs(WorkflowUtil
						.createAnnotationSchemaCSV(annotationSchemaCSVURLs,
								baseAnnotationSchemaURL));

				if (pluginCSVList !=null) {
					   annotatorGUILaunchBean.setPluginCSVList(pluginCSVList);
				}

				String annotatorGUIURL = templateAnnotatorGUIURL + annotatorGUILaunchBean.toString();

				log.debug("set variable annotatorGUIURL " + annotatorGUIURL);

				context.getContextInstance().createVariable(
						getOutVarAnnotatorGUIURL(), annotatorGUIURL,
						context.getToken());
			}

			AnnotationDifferLaunchBean annotationDifferLaunchBean = new AnnotationDifferLaunchBean();

			annotationDifferLaunchBean.setCorpusId(corpusId);
			annotationDifferLaunchBean.setDocumentId(documentId.replaceAll("&", "%26"));

			String annotationDifferURL = annotationDifferTemplateURL + annotationDifferLaunchBean.toString();

			log
					.debug("create variable annotationDifferURL "
							+ annotationDifferURL);

			context.getContextInstance().createVariable(
					getOutVarAnnotationDifferURL(), annotationDifferURL, context.getToken());


		}

	}

	public String getInVarAnnotationSchemaCSVURLs() {
		return inVarAnnotationSchemaCSVURLs;
	}

	public void setInVarAnnotationSchemaCSVURLs(
			String inVarAnnotationSchemaCSVURLs) {
		this.inVarAnnotationSchemaCSVURLs = inVarAnnotationSchemaCSVURLs;
	}

	public String getInVarBaseAnnotationSchemaURL() {
		return inVarBaseAnnotationSchemaURL;
	}

	public void setInVarBaseAnnotationSchemaURL(
			String inVarBaseAnnotationSchemaURL) {
		this.inVarBaseAnnotationSchemaURL = inVarBaseAnnotationSchemaURL;
	}

	public String getInVarDocServiceURL() {
		return inVarDocServiceURL;
	}

	public void setInVarDocServiceURL(String inVarDocServiceURL) {
		this.inVarDocServiceURL = inVarDocServiceURL;
	}

	public String getInVarDocumentId() {
		return inVarDocumentId;
	}

	public void setInVarDocumentId(String inVarDocumentId) {
		this.inVarDocumentId = inVarDocumentId;
	}

	public String getInVarTemplateAnnotationDifferURL() {
		return inVarTemplateAnnotationDifferURL;
	}

	public void setInVarTemplateAnnotationDifferURL(
			String inVarTemplateAnnotationDifferURL) {
		this.inVarTemplateAnnotationDifferURL = inVarTemplateAnnotationDifferURL;
	}

	public String getInVarTemplateAnnotatorGUIURL() {
		return inVarTemplateAnnotatorGUIURL;
	}

	public void setInVarTemplateAnnotatorGUIURL(
			String inVarTemplateAnnotatorGUIURL) {
		this.inVarTemplateAnnotatorGUIURL = inVarTemplateAnnotatorGUIURL;
	}

	public String getOutVarAnnotatedBy() {
		return outVarAnnotatedBy;
	}

	public void setOutVarAnnotatedBy(String outVarAnnotatedBy) {
		this.outVarAnnotatedBy = outVarAnnotatedBy;
	}

	public String getOutVarAnnotationDifferURL() {
		return outVarAnnotationDifferURL;
	}

	public void setOutVarAnnotationDifferURL(String outVarAnnotationDifferURL) {
		this.outVarAnnotationDifferURL = outVarAnnotationDifferURL;
	}

	public String getOutVarAnnotatorGUIURL() {
		return outVarAnnotatorGUIURL;
	}

	public void setOutVarAnnotatorGUIURL(String outVarAnnotatorGUIURL) {
		this.outVarAnnotatorGUIURL = outVarAnnotatorGUIURL;
	}

	public String getOutVarCanceledBy() {
		return outVarCanceledBy;
	}

	public void setOutVarCanceledBy(String outVarCanceledBy) {
		this.outVarCanceledBy = outVarCanceledBy;
	}

	public String getInVarCorpusId() {
		return inVarCorpusId;
	}

	public void setInVarCorpusId(String inVarCorpusId) {
		this.inVarCorpusId = inVarCorpusId;
	}

	public String getOutVarDocumentName() {
		return outVarDocumentName;
	}

	public void setOutVarDocumentName(String outVarDocumentName) {
		this.outVarDocumentName = outVarDocumentName;
	}

	public String getInVarPluginCSVList() {
		return inVarPluginCSVList;
	}

	public void setInVarPluginCSVList(String inVarPluginCSVList) {
		this.inVarPluginCSVList = inVarPluginCSVList;
	}

}
