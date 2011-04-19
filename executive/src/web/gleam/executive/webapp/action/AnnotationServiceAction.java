/*
 *  AnnotationServiceAction.java
 *
 *  Copyright (c) 2006-2011, The University of Sheffield.
 *
 *  This file is part of GATE Teamware (see http://gate.ac.uk/teamware/), 
 *  and is free software, licenced under the GNU Affero General Public License,
 *  Version 3, November 2007 (also included with this distribution as file 
 *  LICENCE-AGPL3.html).
 *
 *  A commercial licence is also available for organisations whose business
 *  models preclude the adoption of open source and is subject to a licence
 *  fee charged by the University of Sheffield. Please contact the GATE team
 *  (see http://gate.ac.uk/g8/contact) if you require a commercial licence.
 *
 *  $Id$
 */
package gleam.executive.webapp.action;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import gleam.executive.Constants;
import gleam.executive.model.AnnotationServiceType;
import gleam.executive.model.AnnotationService;
import gleam.executive.service.AnnotationServiceManager;
import gleam.executive.util.MapUtil;
import gleam.executive.util.XstreamUtil;
import gleam.executive.webapp.form.AnnotationServiceForm;

/**
 * Implementation of <strong>Action</strong> that interacts with the
 * {@link AnnotationServiceForm} and retrieves values. It interacts with the
 * {@link AnnotationServiceManager} to retrieve/persist values to the database.
 * 
 * @struts.action name="annotationServiceForm" path="/annotationServices"
 *                scope="request" validate="false" parameter="method"
 *                input="mainMenu"
 * @struts.action name="annotationServiceForm" path="/editAnnotationService"
 *                scope="request" validate="false" parameter="method"
 *                input="list"
 * @struts.action name="annotationServiceForm" path="/saveAnnotationService"
 *                scope="request" validate="true" parameter="method" input="edit
 * @struts.action name="annotationServiceForm" path="/popupAddAnnotationService"
 *                scope="request" validate="false" parameter="method"
 *                input="list"
 * 
 * @struts.action-set-property property="cancellable" value="true"
 * @struts.action-forward name="list"
 *                        path="/WEB-INF/pages/annotationServiceList.jsp"
 * @struts.action-forward name="edit"
 *                        path="/WEB-INF/pages/annotationServiceForm.jsp"
 * @struts.action-forward name="success" path="/annotationServiceInfo.html"
 *                        redirect="true"
 * @struts.action-forward name="popupSuccess" path="/popupAnnotationServiceInfo.html"
 *                        redirect="true"                       
 */
public final class AnnotationServiceAction extends BaseAction {

	/*
	 * redirects to create annotationService form
	 */
	public ActionForward add(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String userName = request.getRemoteUser();
		String popup = request.getParameter("popup");
		if (log.isDebugEnabled()) {
			log.debug("Entering 'add AnnotationService' method - user: "
					+ userName);
			log.debug("param 'popup' is: " + popup);
		}

		AnnotationService annotationService = new AnnotationService();

		AnnotationServiceForm annotationServiceForm = (AnnotationServiceForm) convert(annotationService);
		updateFormBean(mapping, request, annotationServiceForm);

		return mapping.findForward("edit");
	}

	/*
	 * redirects to annotationServices list
	 */
	public ActionForward cancel(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		if (log.isDebugEnabled()) {
			log.debug("Entering 'cancel AnnotationService' method");
		}

		return mapping.findForward("viewAnnotationServices");
	}

	/*
	 * deletes selected annotationService and redirects to annotationService
	 * list
	 */
	public ActionForward delete(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		if (log.isDebugEnabled()) {
			log.debug("Entering 'delete AnnotationService' method");
		}

		ActionMessages messages = new ActionMessages();
		String annotationServiceId = request.getParameter("id");
		if (log.isDebugEnabled()) {
			log.debug("annotationServiceId: " + annotationServiceId);
		}
		AnnotationServiceManager annotationServiceManager = (AnnotationServiceManager) getBean("annotationServiceManager");

		try {

			annotationServiceManager.removeAnnotationService(new Long(
					annotationServiceId));
			messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
					"annotationService.deleted", annotationServiceId));
			saveMessages(request.getSession(), messages);
			return mapping.findForward("viewAnnotationServices");

		} catch (Exception e) {
			log
					.debug("-------The annotationService cannot be deleted because of exception "
							+ e.getMessage());
			messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
					"errors.annotationService.delete", annotationServiceId));
			saveMessages(request.getSession(), messages);
			return mapping.findForward("viewAnnotationServices");
		}
	}

	/*
	 * edits selected annotationService and redirects to annotationService form
	 */
	public ActionForward edit(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String userName = request.getRemoteUser();
		if (log.isDebugEnabled()) {
			log.debug("Entering 'edit AnnotationService' method - user: "
					+ userName);
		}
		AnnotationServiceForm annotationServiceForm = (AnnotationServiceForm) form;
		String annotationServiceId = request.getParameter("id");
		if (log.isDebugEnabled()) {
			log.debug("annotationServiceId: " + annotationServiceId);
		}
		AnnotationServiceManager mgr = (AnnotationServiceManager) getBean("annotationServiceManager");
		AnnotationService annotationService = mgr
				.getAnnotationService(new Long(annotationServiceId));
		log.debug("parameterData: " + annotationService.getParameters());

		BeanUtils.copyProperties(annotationServiceForm,
				convert(annotationService));
		updateFormBean(mapping, request, annotationServiceForm);
		updateServiceParameters(annotationServiceForm, request);

		// return a forward to edit forward
		return mapping.findForward("edit");
	}

	/*
	 * saves selected annotationService and redirects to annotationService form
	 */
	public ActionForward save(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String userName = request.getRemoteUser();
		String popup = request.getParameter("popup");

		if (log.isDebugEnabled()) {
			log.debug("Entering 'save AnnotationService' method - user: "
					+ userName);
			log.debug("param 'popup' is: " + popup);
		}
		ActionMessages errors = form.validate(mapping, request);
		ActionMessages messages = new ActionMessages();
		AnnotationServiceForm annotationServiceForm = (AnnotationServiceForm) form;

		if (log.isDebugEnabled()) {
			log.debug("Saving annotationService: " + annotationServiceForm);
		}
		boolean isNewAnnotationService = ("".equals(annotationServiceForm
				.getId()));
		String oldAnnotationServiceName = request
				.getParameter("oldAnnotationServiceName");
		log.debug("oldAnnotationServiceName is " + oldAnnotationServiceName);
		AnnotationServiceManager mgr = (AnnotationServiceManager) getBean("annotationServiceManager");

		AnnotationService annotationService = (AnnotationService) convert(annotationServiceForm);
		log.debug("name: " + annotationService.getName());
		log.debug("url: " + annotationService.getUrl());
		log.debug("description: " + annotationService.getDescription());

		Map<String, String[]> requestParameterMap = request.getParameterMap();
		Long annotationServiceTypeId = annotationService
				.getAnnotationServiceTypeId();
		log.debug("retrieving annotationServiceTypeId: "
				+ annotationServiceTypeId);
		AnnotationServiceType annotationServiceType = mgr
				.getAnnotationServiceType(annotationServiceTypeId);
		String csvKeys = annotationServiceType.getData();
		log.debug("retrieving csvKeys: " + csvKeys);

		Map<String, String> serviceParameterMap = MapUtil
				.copyMatchingEntriesIntoMap(requestParameterMap, csvKeys);
		log.debug("creating parameter map: " + serviceParameterMap);

		String data = XstreamUtil.fromMapToString(serviceParameterMap);

		annotationService.setParameters(data);
		annotationService.setAnnotationServiceType(annotationServiceType);

		try {
			mgr.saveAnnotationService(annotationService);
			if (isNewAnnotationService) {
				messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
						"annotationService.added"));
				// save messages in session to survive a redirect
				saveMessages(request.getSession(), messages);

			} else {
				messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
						"annotationService.updated"));
				saveMessages(request.getSession(), messages);

			}
		} catch (Exception e) {
			log.warn(e.getMessage());
			log.debug("------------Duplicate annotationServiceName------");
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
					"errors.existing.annotationService", annotationServiceForm
							.getName()));
			saveErrors(request, errors);
			BeanUtils.copyProperties(annotationServiceForm,
					convert(annotationService));
			updateFormBean(mapping, request, annotationServiceForm);
			updateServiceParameters(annotationServiceForm, request);
			return mapping.findForward("edit");
		}

		ActionForward af = null;
		String pathSuffix = "";
		if (popup != null && !"".equals(popup)) {
			af = new ActionForward(mapping.findForward("popupSuccess"));
			pathSuffix = "?popup=" + popup;
		} else {
			// not invoked from WF, so redirect as usual
			af = new ActionForward(mapping
					.findForward("viewAnnotationServices"));

		}
		af.setPath(af.getPath() + pathSuffix);
		return af;

	}

	/*
	 * list annotationServices for user and redirects to annotationService list
	 */
	public ActionForward search(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String userName = request.getRemoteUser();
		if (log.isDebugEnabled()) {
			log.debug("Entering 'search AnnotationService' method - user: "
					+ userName);
		}

		AnnotationServiceManager mgr = (AnnotationServiceManager) getBean("annotationServiceManager");

		List annotationServices = mgr.getAnnotationServices();
		request.setAttribute(Constants.ANNOTATION_SERVICE_LIST,
				annotationServices);

		// return a forward to the annotationService list definition
		return mapping.findForward("list");
	}

	public ActionForward unspecified(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		return search(mapping, form, request, response);
	}

	private void updateServiceParameters(
			AnnotationServiceForm annotationServiceForm,
			HttpServletRequest request) throws Exception {
		String parameterData = annotationServiceForm.getParameters();
		log.debug("parameterData: " + parameterData);
		Map<String, String> parameterMap = XstreamUtil
				.fromStringToMap(parameterData);

		// put them in request
		Iterator<String> it = parameterMap.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			log.debug("key: " + key);
			if(parameterMap.get(key)!=null){
			log.debug("class: "+parameterMap.get(key).getClass().getName());
			}
			String value = (String) parameterMap.get(key);
			log.debug("value: " + value);
			request.setAttribute(key, value);
		}

	}

}
