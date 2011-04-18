package gleam.executive.webapp.action;

import java.util.List;

import gleam.executive.Constants;
import gleam.executive.model.AnnicSearchResult;
import gleam.executive.service.DocServiceManager;
import gleam.executive.util.GATEUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;


/**
 * Implementation of <strong>Action</strong> that interacts with the
 * {@link AnnicSearchResultForm} and retrieves values. It interacts with the {@link
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
 * @struts.action name="annicSearchResultForm" path="/searchResult"
 *                scope="request" validate="true" input="failure"
 * @struts.action name="annicSearchResultForm" path="/annicSearch"
 *                scope="request" validate="false" input="failure"
 * @struts.action-set-property property="cancellable" value="true"
 * @struts.action-forward name="failure"
 *                        path="/WEB-INF/pages/annicSearch.jsp"
 * @struts.action-forward name="success"
 *                        path="/WEB-INF/pages/annicSearch.jsp"
 *
 */
public class AnnicSearchAction extends Action {
  protected final Log log = LogFactory.getLog(getClass());

  /**
   * The ActionForward that is invoked in the annicSearch.jsp,
   * which carries out annic search from docServiceManager and save the result in the request.
   */
  public ActionForward execute(ActionMapping mapping, ActionForm form,
          HttpServletRequest request, HttpServletResponse response)
          throws Exception {

    if(isCancelled(request)) {
      request.removeAttribute(mapping.getAttribute());
      return (mapping.findForward("viewCorpora"));
    }

    if(log.isDebugEnabled()) {
      log.debug("Entering 'Annic Search' method");
    }
    ActionMessages messages = new ActionMessages();
    ApplicationContext ctx = WebApplicationContextUtils
            .getRequiredWebApplicationContext(servlet.getServletContext());
    DocServiceManager mgr = (DocServiceManager)ctx.getBean("docServiceManager");
    String query = request.getParameter("query");
    if(!GATEUtil.isValidQuery(query)) {
      messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
              "annicSearch.invalidQuery"));
      saveMessages(request.getSession(), messages);
      return mapping.findForward("failure");
    }
    request.setAttribute("query", query);

    String corpusID = request.getParameter("corpusID");
    request.setAttribute("corpusID", corpusID);
    String annotationSetID = request.getParameter("annotationSetID");
    request.setAttribute("annotationSetID", annotationSetID);
    String contextWindow = request.getParameter("contextWindow");
    request.setAttribute("contextWindow", contextWindow);
    Integer context = new Integer(contextWindow);
    if(context instanceof Integer) {
      log.debug("contextWindow entered is valid");
    }
    else {
      messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
              "annicSearch.invalidContextWindow"));
      saveMessages(request.getSession(), messages);
      return mapping.findForward("failure");
    }
    String searcherID = null;
    if(request.getParameter("searcherID") != null && !"".equals(request.getParameter("searcherID"))) {
      searcherID = request.getParameter("searcherID");
      log.debug("searcherID obtained from request as parameter "+searcherID);
      request.setAttribute("searcherID", searcherID);
    }
    else {
      searcherID = mgr.getSearcherID(query, corpusID, annotationSetID, context);
      log.debug("searcherID obtained from docservice manager "+searcherID);
      request.setAttribute("searcherID", searcherID);
    }
    if(searcherID != null) {
      log.debug("searcherID is not null - get next results "+searcherID);
      List<AnnicSearchResult> results = mgr.getAnnicSearchResults(searcherID, 50);
      request.setAttribute(Constants.ANNIC_LIST, results);
      return mapping.findForward("success");
    }
    else {
      log.debug("searcherID IS NULL ");

      messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
              "annicSearch.noResultsFound"));
      saveMessages(request.getSession(), messages);
      return mapping.findForward("success");
    }
  }
}
