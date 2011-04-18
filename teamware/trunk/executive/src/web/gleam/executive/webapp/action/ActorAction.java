package gleam.executive.webapp.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.jbpm.graph.exe.ProcessInstance;
import org.springframework.util.StringUtils;
import gleam.executive.Constants;
import gleam.executive.service.UserManager;
import gleam.executive.webapp.form.ActorForm;
import gleam.executive.workflow.manager.WorkflowManager;
import gleam.executive.workflow.util.JPDLConstants;


/**
 * @author <a href="mailto:agaton@dcs.shef.ac.uk">Milan Agatonovic</a>
 *
 * @struts.action name="actorForm" path="/saveActors" scope="request"
 *                validate="true" parameter="method" input="edit"
 * @struts.action name="actorForm" path="/editActors" scope="request"
 *                validate="false" parameter="method" input="edit" 
 * @struts.action-set-property property="cancellable" value="true"             
 * @struts.action-forward name="edit"
 *                        path="/WEB-INF/pages/actorForm.jsp" 
 */
public class ActorAction extends BaseAction {


/*
 * redirects to actor form
 */
public ActionForward edit(ActionMapping mapping, ActionForm form,
		HttpServletRequest request, HttpServletResponse response)
		throws Exception {
	String username = request.getRemoteUser();
	ActorForm actorForm = (ActorForm) form;
	if (log.isDebugEnabled()) {
		log.debug("Entering 'editActors Process Instance' method - user: " + username);
	}
	
	String processInstanceId = request.getParameter("processInstanceId");
	log.debug("processInstanceId: "+processInstanceId);
	// now find the users
	WorkflowManager mgr = (WorkflowManager)getBean("workflowManager");	
    UserManager userManager = (UserManager) getBean("userManager");

	ProcessInstance processInstance = mgr.getProcessInstance(Long
	          .parseLong(processInstanceId));
	
	Map variableMap = processInstance.getContextInstance().getVariables();
	String annotatorsCSVList = (String)variableMap.get(JPDLConstants.ANNOTATOR_CSV_LIST);
	log.debug("annotatorsCSVList: "+annotatorsCSVList);
	//String curatorCSVList = (String)variableMap.get(JPDLConstants.CURATOR_CSV_LIST);
	//log.debug("curatorCSVList: "+curatorCSVList);
	String manager = (String)variableMap.get(JPDLConstants.INITIATOR);
	log.debug("manager: "+manager);
	if(annotatorsCSVList!=null){
	  String[] annotators = StringUtils.commaDelimitedListToStringArray(annotatorsCSVList);
	  actorForm.setAnnotators(annotators);
	  List annotatorList = userManager.getUsersWithRole(Constants.ANNOTATOR_ROLE);
	  log.debug("annotators size "+annotatorList.size());
	  request.setAttribute(Constants.ANNOTATOR_LIST, annotatorList);
	}
	/*
	if(curatorCSVList!=null){
      String[] curators = StringUtils.commaDelimitedListToStringArray(curatorCSVList);
      actorForm.setCurators(curators);
      List curatorList = userManager.getUsersWithRole(Constants.CURATOR_ROLE);
  	  log.debug("curators size "+curatorList.size());
  	  request.setAttribute(Constants.CURATOR_LIST, curatorList);
	}
	*/
	
	actorForm.setManager(manager);
	List managerList = userManager.getUsersWithRole(Constants.MANAGER_ROLE);
	log.debug("managers size "+managerList.size());
	request.setAttribute(Constants.MANAGER_LIST, managerList);
	
	// check if process has been completed
	log.debug("check if processInstance has ended: "+processInstance.getId());
	if(!processInstance.hasEnded()){
		log.debug("processInstance has ended - allow change actors");
		request.setAttribute("changeAllowed", "true");
	}
	return mapping.findForward("edit");
}

/*
 * creates and starts process instance from project
 */
public ActionForward save(ActionMapping mapping, ActionForm form,
		HttpServletRequest request, HttpServletResponse response)
		throws Exception {
	
	if (log.isDebugEnabled()) {
		log.debug("Entering 'save Actors' method");
	}
	ActionMessages errors = new ActionMessages();
	ActorForm actorForm = (ActorForm) form;
	String processInstanceId =  actorForm.getProcessInstanceId();
	WorkflowManager mgr = (WorkflowManager)getBean("workflowManager");
	UserManager userManager = (UserManager) getBean("userManager");
	ProcessInstance processInstance = mgr.getProcessInstance(Long
	          .parseLong(processInstanceId));
	
	Map variableMap = processInstance.getContextInstance().getVariables();
	Map<String, String> map = new HashMap<String, String>();
	String oldAnnotatorsCSVList = (String)variableMap.get(JPDLConstants.ANNOTATOR_CSV_LIST);
	log.debug("oldAnnotatorsCSVList: "+oldAnnotatorsCSVList);
	
	if(oldAnnotatorsCSVList!=null){
	  String[] oldAnnotators = StringUtils.commaDelimitedListToStringArray(oldAnnotatorsCSVList);
	  String annotatorCSVList = "";
	  String[] annotators = actorForm.getAnnotators();
	  if(annotators!=null){
		annotatorCSVList = StringUtils.arrayToCommaDelimitedString(annotators);
		//map.put(JPDLConstants.ANNOTATOR_CSV_LIST, annotatorCSVList);
	  }
	  //log.debug("annotatorCSVList: " + annotatorCSVList);
	  if(!"".equals(annotatorCSVList)){
		 log.debug("update annotator pool");
		 mgr.updatePooledActors(JPDLConstants.ANNOTATOR_CSV_LIST, annotators, processInstance);
	  }
	  else {
		  errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
			      "errors.required", "annotators"));
		  request.setAttribute(Constants.ANNOTATOR_LIST, userManager.getUsersWithRole(Constants.ANNOTATOR_ROLE));
		  actorForm.setAnnotators(oldAnnotators);
	  }
	}
	/*
	String oldCuratorCSVList = (String)variableMap.get(JPDLConstants.CURATOR_CSV_LIST);
	log.debug("oldCuratorCSVList: "+oldCuratorCSVList);
	
	
	if(oldCuratorCSVList!=null){
		  String[] oldCurators = StringUtils.commaDelimitedListToStringArray(oldAnnotatorsCSVList);
		  String curatorCSVList = "";
		  String[] curators = actorForm.getCurators();
		  if(curators!=null){
			  curatorCSVList = StringUtils.arrayToCommaDelimitedString(curators);
			  //map.put(JPDLConstants.CURATOR_CSV_LIST, curatorCSVList);
		  }
		  //log.debug("curatorCSVList: " + curatorCSVList);
		  if(!"".equals(curatorCSVList)){
			 log.debug("update curator pool");
			 mgr.updatePooledActors(JPDLConstants.CURATOR_CSV_LIST, curators, processInstance);
		  }
		  else {
			  errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
				      "errors.required", "curators"));
			  request.setAttribute(Constants.CURATOR_LIST, userManager.getUsersWithRole(Constants.CURATOR_ROLE));
			  actorForm.setCurators(oldCurators);
		  }
		}
    */
	if(errors.size()>0) {
        saveErrors(request, errors);
        request.setAttribute(Constants.MANAGER_LIST, userManager.getUsersWithRole(Constants.MANAGER_ROLE));
        return mapping.findForward("edit");
    }
	
	String manager = actorForm.getManager();
	Map<String, String> updateMap = new HashMap<String, String>();
	updateMap.put(JPDLConstants.INITIATOR, manager);
	
	
	String processInstanceName = (String)variableMap.get(JPDLConstants.PROCESS_INSTANCE_NAME);
	log.debug("first update variables");
	mgr.updateVariables(processInstance, updateMap);
	
	ActionForward actionForward = mapping.findForward(Constants.FORWARD_PROCESSES_IN_PROJECT);
	ActionForward newActionForward = new ActionForward(actionForward);
	ActionMessages messages = new ActionMessages();

    messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
					"processInstance.actors.saved", processInstanceName));
	
	saveMessages(request.getSession(), messages);
	
	String path = actionForward.getPath() + "&projectName=" + processInstance.getKey() + "&from="+Constants.FORWARD_PROCESSES_IN_PROJECT;
	
	newActionForward.setPath(path);
	newActionForward.setRedirect(true);
	if (log.isDebugEnabled())
		log.debug("Exit 'save process instance' method");
	return newActionForward;
}



/*
 * redirects to projects list
 */
public ActionForward cancel(ActionMapping mapping, ActionForm form,
		HttpServletRequest request, HttpServletResponse response)
		throws Exception {
	if (log.isDebugEnabled()) {
		log.debug("Entering 'cancel Actors' method");
	}
	ActorForm actorForm = (ActorForm) form;
	String processInstanceId =  actorForm.getProcessInstanceId();
	
	WorkflowManager mgr = (WorkflowManager)getBean("workflowManager");
	ProcessInstance processInstance = mgr.getProcessInstance(Long
	          .parseLong(processInstanceId));
	
	ActionForward actionForward = mapping.findForward(Constants.FORWARD_PROCESSES_IN_PROJECT);
	String path = actionForward.getPath() + "&projectName=" + processInstance.getKey() + "&from="+Constants.FORWARD_PROCESSES_IN_PROJECT;
	ActionForward newActionForward = new ActionForward(actionForward);
	newActionForward.setPath(path);
	newActionForward.setRedirect(true);
	if (log.isDebugEnabled())
		log.debug("Exit 'cancel Actors' method");
	return newActionForward;
}

}
