package gleam.executive.webapp.action;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import gleam.executive.webapp.action.BaseAction;
import gleam.executive.Constants;
import gleam.executive.workflow.manager.WorkflowManager;
import gleam.executive.workflow.model.AnnotationMetricMatrix;
import gleam.executive.workflow.model.AnnotationStatusInfo;
import gleam.executive.workflow.util.WorkflowException;

/**
 * 
 * @author <a href="mailto:agaton@dcs.shef.ac.uk">Milan Agatonovic</a>
 * 
 * @struts.action path="/annotationStatus" scope="request" parameter="method"
 *                input="mainMenu"
 * @struts.action-forward name="details"
 *                        path="/WEB-INF/pages/annotationStatusDetails.jsp"
 * @struts.action-forward name="overview"
 *                        path="/WEB-INF/pages/annotationStatusOverview.jsp"
 * @struts.action-forward name="annotator"
 *                        path="/WEB-INF/pages/personalAnnotatorRecord.jsp"
 * @struts.action-forward name="globalAnnotator"
 *                        path="/WEB-INF/pages/globalAnnotatorRecord.jsp"
 */
public final class AnnotationMetricAction extends BaseAction {

	public ActionForward showDetails(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		String processInstanceId = request.getParameter("id");
		ActionMessages errors = new ActionMessages();
		WorkflowManager mgr = (WorkflowManager) getBean("workflowManager");
		try {
			List<AnnotationStatusInfo> list = mgr
					.getDocumentStatusList(new Long(processInstanceId));
			request.setAttribute(Constants.ANNOTATION_STATUS_LIST, list);
		} catch (WorkflowException e) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
					"errors.process.missing", processInstanceId));
		}
		return mapping.findForward("details");
	}

	public ActionForward showOverview(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		String processInstanceId = request.getParameter("id");
		ActionMessages errors = new ActionMessages();
		WorkflowManager mgr = (WorkflowManager) getBean("workflowManager");
		try {
			AnnotationMetricMatrix annotationMetricMatrix = mgr
					.getDocumentMatrix(new Long(processInstanceId));
			request.setAttribute(Constants.ANNOTATION_METRIC_MATRIX,
					annotationMetricMatrix);
		} catch (WorkflowException e) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
					"errors.process.missing", processInstanceId));
		}
		return mapping.findForward("overview");
	}

	public ActionForward showAnnotator(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		String processInstanceId = request.getParameter("id");
		ActionMessages errors = new ActionMessages();
		String username = request.getParameter("username");
		//log.debug("show annotator: "+ username);
		WorkflowManager mgr = (WorkflowManager) getBean("workflowManager");
		try {
			AnnotationMetricMatrix annotationMetricMatrix = mgr
					.getAnnotatorMatrix(new Long(processInstanceId),
							username);
			//log.debug(annotationMetricMatrix);
			request.setAttribute(Constants.ANNOTATION_METRIC_MATRIX,
					annotationMetricMatrix);
			
		} catch (WorkflowException e) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
					"errors.process.missing", processInstanceId));
		}
		return mapping.findForward("annotator");
	}
	
	public ActionForward showGlobalAnnotator(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		String processInstanceId = request.getParameter("id");
		ActionMessages errors = new ActionMessages();
		String role = request.getParameter("role");
		WorkflowManager mgr = (WorkflowManager) getBean("workflowManager");
		try {
			AnnotationMetricMatrix annotationMetricMatrix = mgr
					.getGlobalAnnotatorMatrix(new Long(processInstanceId),
							role);
			//log.debug("fetched annotationMetricMatrix: "+annotationMetricMatrix);
			request.setAttribute(Constants.ANNOTATION_METRIC_MATRIX,
					annotationMetricMatrix);
		} catch (WorkflowException e) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
					"errors.process.missing", processInstanceId));
		}
		return mapping.findForward("globalAnnotator");
	}


}
