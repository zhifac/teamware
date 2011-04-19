/*
 *  AnnotatorGuiAction.java
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
 * Ian Roberts
 *
 *  $Id$
 */
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
