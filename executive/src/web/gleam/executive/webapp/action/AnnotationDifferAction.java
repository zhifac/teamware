package gleam.executive.webapp.action;

import java.util.List;

import gleam.executive.Constants;
import gleam.executive.model.AnnotationDifferResult;
import gleam.executive.service.DocServiceManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Implementation of <strong>Action</strong> that interacts with the
 * {@link AnnotationDifferResultForm} and retrieves values. It interacts with the {@link
 * DocServiceManager} to retrieve/persist values to the datastore.
 *
 * Copyright (c) 1998-2006, The University of Sheffield.
 *
 * This file is part of GATE (see http://gate.ac.uk/), and is free
 * software, licenced under the GNU Library General Public License,
 * Version 2, June 1991 (in the distribution as file licence.html, and
 * also available at http://gate.ac.uk/gate/licence.html).
 *
 * @author <a href="mailto:agaton@dcs.shef.ac.uk">Milan Agatonovic</a>
 *
 * @struts.action name="annotationDifferResultForm" path="/annDiff"
 *                scope="request" validate="false" parameter="method"
 * @struts.action-set-property property="cancellable" value="true"
 * @struts.action-forward name="failure"
 *                        path="/WEB-INF/pages/annDiff.jsp"
 * @struts.action-forward name="success"
 *                        path="/WEB-INF/pages/annDiff.jsp"
 * @struts.action-forward name="list" path="/WEB-INF/pages/annDiff.jsp"
 *
 */

public class AnnotationDifferAction extends BaseAction {
  protected final Log log = LogFactory.getLog(getClass());

  /**
   * The ActionForward that is invoked in annDiffResult.jsp,
   * which gets the annotation differ result from docServiceManager and set them in the request
   * @param mapping
   * @param form
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public ActionForward executeAnnDiff(ActionMapping mapping, ActionForm form,
          HttpServletRequest request, HttpServletResponse response)
          throws Exception {
    ActionMessages messages = new ActionMessages();
    if(isCancelled(request)) {
      request.removeAttribute(mapping.getAttribute());
      return (mapping.findForward("mainMenu"));
    }

    if(log.isDebugEnabled()) {
      log.debug("**Entering 'AnnotaitonDifferAction execute' method**");
    }
    ApplicationContext ctx = WebApplicationContextUtils
            .getRequiredWebApplicationContext(servlet.getServletContext());
    DocServiceManager mgr = (DocServiceManager)ctx.getBean("docServiceManager");
    String docID = (String)request.getParameter("documentID");
    String keyAnnoSetName = (String)request.getParameter("keyAnnoSetName");
    String resAnnoSetName = (String)request.getParameter("resAnnoSetName");
    String annoType = (String)request.getParameter("annoType");
    log.debug("The value of annoTypes is " + annoType);
    if(annoType == null) {
      messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
              "annoDiffer.invalidInput"));
      saveMessages(request.getSession(), messages);
      return mapping.findForward("failure");
    }
    List results = mgr.getAnnoDifferResult(docID, keyAnnoSetName,
            resAnnoSetName, annoType);
    log.debug("The size of the AnnDiff Result is " + results.size());
    request.setAttribute(Constants.ANNOTATION_DIFFER_LIST, results);
    request.setAttribute(Constants.ANNOTATION_DIFFER_SCORES, mgr
            .getAnnDiffScores());
    return mapping.findForward("success");
  }

  /**
   * The ActionForward that is invoked in annDiff.jsp,
   * which populates the annotation set names and types if they exist.
   * @param mapping
   * @param form
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public ActionForward searchAnnSetNames(ActionMapping mapping,
          ActionForm form, HttpServletRequest request,
          HttpServletResponse response) throws Exception {
    log.debug("Entering 'AnnotationDiff search Annotation Set Names' method");
    ApplicationContext ctx = WebApplicationContextUtils
            .getRequiredWebApplicationContext(servlet.getServletContext());
    DocServiceManager mgr = (DocServiceManager)ctx.getBean("docServiceManager");
    String docID = (String)request.getParameter("documentID");
    List annSetNames = (List)mgr.listAnnSetNames(docID);
    request.setAttribute(Constants.ANNOTATION_SET_NAMES,
            annSetNames);
    String lastSelectedKeyAnnoSetName = request.getParameter("keyAnnoSetName");
    String lastSelectedResAnnoSetName = request.getParameter("resAnnoSetName");
    String firstAnnoSetName = ((AnnotationDifferResult)annSetNames.get(0))
            .getKeyAnnoSetName();
    List<AnnotationDifferResult> annoTypes=null;
    if(lastSelectedKeyAnnoSetName!=null&&lastSelectedResAnnoSetName!=null){
      System.out.println("Calling from the lastSelected case**********");
      System.out.println("The key is "+lastSelectedKeyAnnoSetName+" and the Res is "+lastSelectedResAnnoSetName);
      annoTypes=(List<AnnotationDifferResult>)mgr.listSharedAnnoTypes(docID,lastSelectedKeyAnnoSetName,lastSelectedResAnnoSetName);
    }else{
      System.out.println("Calling from the first case**********");
      annoTypes = mgr.listAnnotationTypesForSingleAnnotationSet(docID,
            firstAnnoSetName);
    }
    System.out.println("The size of AnnoTypes is "+annoTypes.size());
    request.setAttribute(Constants.ANNOTATION_Types,
            annoTypes);
    String show =request.getParameter("show");
    if(show!=null&&show.equals("false")){
      return mapping.findForward("list");
    }

    if(show!=null&&show.equals("true")){
      this.executeAnnDiff(mapping, form, request, response);
      return mapping.findForward("success");
    }
    return mapping.findForward("success");
  }

}
