package gleam.executive.workflow.action.common;

import gleam.executive.model.AnnotatorGUILaunchBean;
import gleam.executive.model.WebAppBean;
import gleam.executive.workflow.util.WorkflowUtil;
import java.util.Date;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.taskmgmt.def.Task;
import org.jbpm.taskmgmt.def.TaskMgmtDefinition;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.jbpm.taskmgmt.exe.TaskMgmtInstance;
import gleam.executive.workflow.util.CSVUtil;
import gleam.executive.workflow.util.JPDLConstants;
import gleam.executive.workflow.sm.JbpmHandlerProxy;


public class AnnotationTaskActionHandler extends JbpmHandlerProxy {

	private static final long serialVersionUID = 1L;
    private WebAppBean webAppBean;

	protected final Log log = LogFactory.getLog(getClass());
	/*
	   <inVarTaskName>annotate</inVarTaskName>
	   <inVarDocumentId>documentId</inVarDocumentId>
	   <inVarAnnotationSetName>annotationSetName</inVarAnnotationSetName>
	   <inVarAnnotationSchemaCSVURLs>annotationSchemaCSVURLs</inVarAnnotationSchemaCSVURLs>
       <inVarBaseAnnotationSchemaURL>baseAnnotationSchemaURL</inVarBaseAnnotationSchemaURL>
       <inVarTemplateAnnotatorGUIURL>templateAnnotatorGUIURL</inVarTemplateAnnotatorGUIURL>>
       <inVarAnnotationMode>annotationMode</inVarAnnotationMode>
       <inVarPluginCSVList>pluginCSVList</inVarPluginCSVList>
       <inVarDocServiceURL>docServiceURL</inVarDocServiceURL>
       <inVarGosURL>gosURL</inVarGosURL>
	   <inVarOntologyRepositoryName>ontologyRepositoryName</inVarOntologyRepositoryName>
	   <inVarCanCancel>canCancel</inVarCanCancel>
	   <inOutVarAnnotatorCSVList>annotatorCSVList</inOutVarAnnotatorCSVList>
       <outVarAnnotatorGUIURL>annotatorGUIURL</outVarAnnotatorGUIURL>
       <outVarDocumentName>documentName</outVarDocumentName>
     */

	String inVarTaskName;
	String inVarDocumentId;
	String inVarAnnotationSetName;
	String inVarAnnotationSchemaCSVURLs;
	String inVarBaseAnnotationSchemaURL;
	String inVarTemplateAnnotatorGUIURL;
	String inVarAnnotationMode;
	String inVarDocServiceURL;
	String inVarGosURL;
	String inVarOntologyRepositoryName;
	String inVarPerformer;
  // can cancel no longer used by this handler, but left in to avoid having to
  // change the process definitions before I've cleared it with Milan.
	String inVarCanCancel;
	String inVarPluginCSVList;
	String inOutVarAnnotatorCSVList;
	String outVarAnnotatorGUIURL;
	String outVarDocumentName;


	/**
	 * A message process variable is assigned the value of the message member.
	 * The process variable is created if it doesn't exist yet.
	 */
	public void execute(ExecutionContext context) throws Exception {

		log.debug("AnnotationTaskActionHandler START");

//		 obtain targetProperties

		String taskName = (String) context.getVariable(getInVarTaskName());
		log.debug("&&&&&&&&&&&&& taskName " + taskName);
		String documentId = (String) context.getVariable(getInVarDocumentId());
		log.debug("&&&&&&&&&&&&& documentId " + documentId);
		if(documentId==null){
			log.debug("&&&&&&&&&&&&& make documentId empty by default");
			documentId = "";
		}
		String annotationSchemaCSVURLs = (String) context.getVariable(getInVarAnnotationSchemaCSVURLs());
		log.debug("&&&&&&&&&&&&& annotationSchemaCSVURLs " + annotationSchemaCSVURLs);

		/*
		 * Do not store baseAnnotationSchemaURL as process variable
		 * String baseAnnotationSchemaURL = (String) context.getVariable(getInVarBaseAnnotationSchemaURL());
		 * log.debug("&&&&&&&&&&&&& baseAnnotationSchemaURL " + baseAnnotationSchemaURL);
        */

		String baseAnnotationSchemaURL = webAppBean.getBaseSchemaURL();
		log.debug("&&&&&&&&&&&&& baseAnnotationSchemaURL " + baseAnnotationSchemaURL);

		String templateAnnotatorGUIURL = (String) context.getVariable(getInVarTemplateAnnotatorGUIURL());
		log.debug("&&&&&&&&&&&&& templateAnnotatorGUIURL " + templateAnnotatorGUIURL);
		String annotationMode = (String) context.getVariable(getInVarAnnotationMode());
		log.debug("&&&&&&&&&&&&& annotationMode " + annotationMode);
		String docServiceURL = (String) context.getVariable(getInVarDocServiceURL());
		log.debug("&&&&&&&&&&&&& docServiceURL " + docServiceURL);

		String canCancel = (String) context.getVariable(getInVarCanCancel());
		log.debug("&&&&&&&&&&&&& canCancel " + canCancel);

		String pluginCSVList = (String) context.getVariable(getInVarPluginCSVList());
		log.debug("&&&&&&&&&&&&& pluginCSVList " + pluginCSVList);


		// optional
		String gosURL = "";
        if(getInVarGosURL()!=null){
			gosURL = (String) context.getVariable(getInVarGosURL());
		    log.debug("&&&&&&&&&&&&& gosURL " + gosURL);
		}

		String ontologyRepositoryName = "";
		if(getInVarOntologyRepositoryName()!=null){
		   ontologyRepositoryName = (String) context.getVariable(getInVarOntologyRepositoryName());
		   log.debug("&&&&&&&&&&&&& ontologyRepositoryName " + ontologyRepositoryName);
		}
		AnnotatorGUILaunchBean annotatorGUILaunchBean = new AnnotatorGUILaunchBean();


			annotatorGUILaunchBean.setDocServiceURL(docServiceURL);
			annotatorGUILaunchBean.setDocumentId(documentId.replaceAll("&", "%26"));

			if(annotationSchemaCSVURLs != null || !"".equals(annotationSchemaCSVURLs)){
				annotatorGUILaunchBean.setAnnotationSchemaCSVURLs(WorkflowUtil.createAnnotationSchemaCSV(annotationSchemaCSVURLs, baseAnnotationSchemaURL));
			}



			TaskMgmtDefinition tmd = (TaskMgmtDefinition) context
					.getDefinition(TaskMgmtDefinition.class);

			Task task = tmd.getTask(taskName);
			log.debug("obtained  task " + task.getName());

			String performer = "";

			if (annotationMode != null
					&& JPDLConstants.RANDOM_MODE.equals(annotationMode)) {

				String possiblePerformers = (String) context
						.getVariable(getInOutVarAnnotatorCSVList());
				if(possiblePerformers!=null){
				log.debug("possiblePerformers  "+possiblePerformers);
				String[] result = CSVUtil.fetchLastToken(possiblePerformers);


				context.setVariable(getInOutVarAnnotatorCSVList(),
								result[1]);
				log.debug("set variable possible performers in context "
						+ result[1]);
				performer = result[0];
				}
				else {
					log.debug("possiblePerformers IS NULL ");
					performer = null;
				}
			} else {
                // performerId is local variable
				performer = (String) context.getContextInstance().getVariable(getInVarPerformer(), context.getToken());

			}

			log.debug("performer  "+performer);
			if(performer!=null){

				/*
				 * First check if annotationSetName is suplied in process definition.
				 * If yes, set tit as a param of AGUI URL,
				 * If not, assume that annootation set name wll be the same as performer.
				 */

				String annotationSetName = (String) context.getContextInstance().getVariable(getInVarAnnotationSetName(), context.getToken());
                if(annotationSetName!=null && !"".equals(annotationSetName)){
                	annotatorGUILaunchBean.setAnnotationSetName(annotationSetName);
                }
                else {
				   annotatorGUILaunchBean.setAnnotationSetName(performer);
                }

			annotatorGUILaunchBean.setOwlimServiceURL(gosURL);
			annotatorGUILaunchBean.setRepositoryName(ontologyRepositoryName);

			if (pluginCSVList !=null) {
				   annotatorGUILaunchBean.setPluginCSVList(pluginCSVList);
			}

			String annotatorGUIURL = templateAnnotatorGUIURL + annotatorGUILaunchBean.toString();

			log.debug("set variable annotatorGUIURL " + annotatorGUIURL);

			context.getContextInstance().createVariable(
					getOutVarAnnotatorGUIURL(), annotatorGUIURL, context.getToken());

			String documentName = WorkflowUtil.findDocumentNameById(documentId);
			log.debug("set variable  " +getOutVarDocumentName() + " to the value " +documentName);

			context.getContextInstance().createVariable(
					getOutVarDocumentName(), documentName, context.getToken());

			TaskMgmtInstance tmi = context.getTaskMgmtInstance();
			TaskInstance taskInstance = tmi.createTaskInstance(task, context);
			taskInstance.setCreate(new Date());
			log.debug("created taskInstance ID " + taskInstance.getId());
			log.debug("created taskInstance name " + taskInstance.getName());
			taskInstance.setActorId(performer);
			log.debug("assigned taskInstance to author "
					+ taskInstance.getActorId());
			taskInstance.setDescription(documentName);
			//taskInstance.setVariable(getOutVarAnnotatorGUIURL(), annotatorGUIURL);
			//log.debug(" annotatorGUIURL " + (String)taskInstance.getVariable(getOutVarAnnotatorGUIURL()));
			}
			else {
				log.debug("THERE ARE NO AVAILABLE PERFORMERS - DO NOT ASSIGN TASK");
			}


	}

	public String getInOutVarAnnotatorCSVList() {
		return inOutVarAnnotatorCSVList;
	}

	public void setInOutVarAnnotatorCSVList(String inOutVarAnnotatorCSVList) {
		this.inOutVarAnnotatorCSVList = inOutVarAnnotatorCSVList;
	}

	public String getInVarAnnotationMode() {
		return inVarAnnotationMode;
	}

	public void setInVarAnnotationMode(String inVarAnnotationMode) {
		this.inVarAnnotationMode = inVarAnnotationMode;
	}

	public String getInVarDocumentId() {
		return inVarDocumentId;
	}

	public void setInVarDocumentId(String inVarDocumentId) {
		this.inVarDocumentId = inVarDocumentId;
	}

	public String getInVarTaskName() {
		return inVarTaskName;
	}

	public void setInVarTaskName(String inVarTaskName) {
		this.inVarTaskName = inVarTaskName;
	}

	public String getInVarTemplateAnnotatorGUIURL() {
		return inVarTemplateAnnotatorGUIURL;
	}

	public void setInVarTemplateAnnotatorGUIURL(String inVarTemplateAnnotatorGUIURL) {
		this.inVarTemplateAnnotatorGUIURL = inVarTemplateAnnotatorGUIURL;
	}

	public String getOutVarAnnotatorGUIURL() {
		return outVarAnnotatorGUIURL;
	}

	public void setOutVarAnnotatorGUIURL(String outVarAnnotatorGUIURL) {
		this.outVarAnnotatorGUIURL = outVarAnnotatorGUIURL;
	}

	public String getInVarAnnotationSchemaCSVURLs() {
		return inVarAnnotationSchemaCSVURLs;
	}

	public void setInVarAnnotationSchemaCSVURLs(String inVarAnnotationSchemaCSVURLs) {
		this.inVarAnnotationSchemaCSVURLs = inVarAnnotationSchemaCSVURLs;
	}

	public String getInVarDocServiceURL() {
		return inVarDocServiceURL;
	}

	public void setInVarDocServiceURL(String inVarDocServiceURL) {
		this.inVarDocServiceURL = inVarDocServiceURL;
	}

	public String getInVarPerformer() {
		return inVarPerformer;
	}

	public void setInVarPerformer(String inVarPerformer) {
		this.inVarPerformer = inVarPerformer;
	}

	public String getInVarBaseAnnotationSchemaURL() {
		return inVarBaseAnnotationSchemaURL;
	}

	public void setInVarBaseAnnotationSchemaURL(String inVarBaseAnnotationSchemaURL) {
		this.inVarBaseAnnotationSchemaURL = inVarBaseAnnotationSchemaURL;
	}

	public String getOutVarDocumentName() {
		return outVarDocumentName;
	}

	public void setOutVarDocumentName(String outVarDocumentName) {
		this.outVarDocumentName = outVarDocumentName;
	}

	public String getInVarGosURL() {
		return inVarGosURL;
	}

	public void setInVarGosURL(String inVarGosURL) {
		this.inVarGosURL = inVarGosURL;
	}

	public String getInVarOntologyRepositoryName() {
		return inVarOntologyRepositoryName;
	}

	public void setInVarOntologyRepositoryName(String inVarOntologyRepositoryName) {
		this.inVarOntologyRepositoryName = inVarOntologyRepositoryName;
	}

	public String getInVarCanCancel() {
		return inVarCanCancel;
	}

	public void setInVarCanCancel(String inVarCanCancel) {
		this.inVarCanCancel = inVarCanCancel;
	}

	public String getInVarPluginCSVList() {
		return inVarPluginCSVList;
	}

	public void setInVarPluginCSVList(String inVarPluginCSVList) {
		this.inVarPluginCSVList = inVarPluginCSVList;
	}

	public String getInVarAnnotationSetName() {
		return inVarAnnotationSetName;
	}

	public void setInVarAnnotationSetName(String inVarAnnotationSetName) {
		this.inVarAnnotationSetName = inVarAnnotationSetName;
	}

	public WebAppBean getWebAppBean() {
		return webAppBean;
	}

	public void setWebAppBean(WebAppBean webAppBean) {
		this.webAppBean = webAppBean;
	}


}
