package gleam.executive.webapp.action;

import gleam.executive.Constants;
import gleam.executive.model.AnnotatorGUILaunchBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Action to launch the annotator GUI in pool mode.  This is separate from
 * the rest of {@link DocumentAction} because we need a separate URL path
 * that is not capable of launching the GUI in direct mode for use by
 * annotators who do not have the manager role.
 * 
 * Copyright (c) 1998-2006, The University of Sheffield.
 * 
 * This file is part of GATE (see http://gate.ac.uk/), and is free software,
 * licenced under the GNU Library General Public License, Version 2, June 1991
 * (in the distribution as file licence.html, and also available at
 * http://gate.ac.uk/gate/licence.html).
 * 
 * @author Ian Roberts
 * 
 * @struts.action path="/annotatorGui" validate="false"
 * 
 * @struts.action-forward name="annotation-gui" path="/WEB-INF/pages/jnlpInvoker.jsp"
 *                        redirect="false"
 */
public class AnnotatorGuiAction extends BaseAction {

  /**
   * Set up an {@link AnnotatorGUILaunchBean} to start the annotator gui
   * in pooled mode, ignoring any request parameters to the contrary.
   */
  public ActionForward execute(ActionMapping mapping, ActionForm form,
          HttpServletRequest request, HttpServletResponse response)
          throws Exception {
    AnnotatorGUILaunchBean annotatorGUILaunchBean = new AnnotatorGUILaunchBean();
    annotatorGUILaunchBean.setMode("pool");
    annotatorGUILaunchBean.setUserId(request.getRemoteUser());
    request.setAttribute(Constants.ANNOTATOR_GUI_LAUNCH_BEAN,
            annotatorGUILaunchBean);

    return mapping.findForward("annotation-gui");
  }
}
