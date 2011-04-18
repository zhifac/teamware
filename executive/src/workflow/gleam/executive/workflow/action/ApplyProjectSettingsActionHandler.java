package gleam.executive.workflow.action;

import java.util.Date;
import java.util.Map;
import gleam.executive.model.Project;
import gleam.executive.model.WebAppBean;
import gleam.executive.service.ProjectManager;
import gleam.executive.service.UserManager;
import gleam.executive.util.XstreamUtil;
import gleam.executive.workflow.sm.JbpmDataflowHandlerProxy;
import gleam.executive.workflow.util.JPDLConstants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.jbpm.graph.exe.ExecutionContext;



public class ApplyProjectSettingsActionHandler extends JbpmDataflowHandlerProxy {

	private static final long serialVersionUID = 1L;
	protected final Log log = LogFactory.getLog(getClass());
	
	Element targetProperties;
	private ProjectManager projectManager;
	private UserManager userManager;
	private WebAppBean webAppBean;

	 /*
	  * <property name="projectName" type="in" scope="global" empty="true"></property>
	  */	
	/*
     * Stores variableMap in project table, if project name variable is specified and doSetup variable exists
	*/
	
	public void execute(ExecutionContext context) throws Exception {
		log.debug("@@@@@@@@@@@@@@@@@@@@@@@@");
		log.debug("ApplyProjectSettingsActionHandler START");

		Map<String, String> localVariableMap = fetchAndValidateVariables(context,
                this.getClass().getName(),
                targetProperties);
			
		String projectName = (String)localVariableMap.get(JPDLConstants.PROJECT_NAME);
		Map globalVariableMap = context.getContextInstance().getVariables();
		
        log.debug(globalVariableMap);
		byte[] data = XstreamUtil.fromMapToByteArray(globalVariableMap);
		Project project = projectManager.getProjectByName(projectName);
		 // now change the business keys of all process instances that belong to this project
		/*
		JbpmGraphSession graphSession =	(JbpmGraphSession)(context.getJbpmContext().getGraphSession());
		
		List<ProcessInstance> processInstances = graphSession.findProcessInstancesExcludingSubProcessInstancesByKey(project.getName());
		Iterator<ProcessInstance> it = processInstances.iterator();	
		while (it.hasNext()){
			ProcessInstance pi = it.next();
			pi.setKey(projectName);
			log.debug("renamed process instance: "+pi.getId());
		}
		*/
		
		project.setData(data);
		project.setLastUpdate(new Date());
		project.setEnabled(true);
		projectManager.saveProject(project);
		log.debug("project saved");
		
	}

	public Element getTargetProperties() {
		return targetProperties;
	}

	public void setTargetProperties(Element targetProperties) {
		this.targetProperties = targetProperties;
	}
	
	public ProjectManager getProjectManager() {
		return projectManager;
	}

	public void setProjectManager(ProjectManager projectManager) {
		this.projectManager = projectManager;
	}
	

	public UserManager getUserManager() {
		return userManager;
	}

	public void setUserManager(UserManager userManager) {
		this.userManager = userManager;
	}
	
	public WebAppBean getWebAppBean() {
		return webAppBean;
	}

	public void setWebAppBean(WebAppBean webAppBean) {
		this.webAppBean = webAppBean;
	}



}
