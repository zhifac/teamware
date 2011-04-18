package gleam.executive.webapp.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gleam.docservice.proxy.IAAAlgorithm;
import gleam.docservice.proxy.iaa.AllWaysFMeasureIAAResult;
import gleam.docservice.proxy.iaa.AllWaysKappaIAAResult;
import gleam.docservice.proxy.iaa.FMeasure;
import gleam.docservice.proxy.iaa.KappaResult;
import gleam.docservice.proxy.iaa.PairwiseFMeasureIAAResult;
import gleam.docservice.proxy.iaa.PairwiseKappaIAAResult;
import gleam.executive.Constants;
import gleam.executive.model.LabelValue;
import gleam.executive.service.DocServiceManager;
import gleam.executive.webapp.form.ConfusionMatrixRowForm;
import gleam.executive.webapp.form.IAAResultForm;
import gleam.executive.webapp.form.LabelValueDetailForm;

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
import org.springframework.util.StringUtils;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Implementation of <strong>Action</strong> that interacts with the
 * {@link IAACaculateForm} and retrieves values. It interacts with the {@link
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
 * @struts.action name="iAAResultForm" path="/caculateIAA"
 *                scope="request" validate="false" parameter="method"
 * @struts.action-set-property property="cancellable" value="true"
 * @struts.action-forward name="failure"
 *                        path="/WEB-INF/pages/caculateIAA.jsp"
 * @struts.action-forward name="success"
 *                        path="/WEB-INF/pages/caculateIAA.jsp"
 * @struts.action-forward name="showQueries" path="/WEB-INF/pages/caculateIAA.jsp"
 *
 */

public class IAACaculateAction extends BaseAction {
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
  public ActionForward caculateIAA(ActionMapping mapping, ActionForm form,
          HttpServletRequest request, HttpServletResponse response)
          throws Exception {
    ActionMessages messages = new ActionMessages();
    if(isCancelled(request)) {
      request.removeAttribute(mapping.getAttribute());
      return (mapping.findForward("mainMenu"));
    }

    if(log.isDebugEnabled()) {
      log.debug("**Entering 'IAA Caculate Action caculateIAA' method**");
    }

    ApplicationContext ctx = WebApplicationContextUtils
            .getRequiredWebApplicationContext(servlet.getServletContext());
    DocServiceManager mgr = (DocServiceManager)ctx.getBean("docServiceManager");
    String docID = (String)request.getParameter("documentID");
    String[] docIDs = new String[1];
    docIDs[0]=docID;
    String selectedAS=request.getParameter("SelectedList");
    System.out.println("selectedList is "+selectedAS);
    String[] asNames = StringUtils.commaDelimitedListToStringArray(selectedAS);

    String annoType = (String)request.getParameter("annoType");
    if(annoType == null) {
      messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
              "caculateIAA.noType"));
      saveMessages(request.getSession(), messages);
      return mapping.findForward("failure");
    }
    String featureName = request.getParameter("feature");
    // empty string for featureName is treated as not specifying featureName at all
    if("".equals(featureName)) {
      featureName = null;
    }
    String iaaAlgorithm = request.getParameter("algorithm");
    List<IAAResultForm> list = new ArrayList<IAAResultForm>();

    if(iaaAlgorithm.equals("pairwise-f-measure")){
      PairwiseFMeasureIAAResult pairwiseResult = (PairwiseFMeasureIAAResult)
         mgr.calculateIAA(docIDs, asNames, annoType, featureName, IAAAlgorithm.PAIRWISE_F_MEASURE);

//    Get overall F-Measure and set it in the request scope.
      FMeasure fmForAllways = pairwiseResult.getOverallFMeasure();
      IAAResultForm iaaFMeasureForm = new IAAResultForm();
      iaaFMeasureForm.setPrecision(new Float(fmForAllways.precision()).toString());
      iaaFMeasureForm.setRecall(new Float(fmForAllways.recall()).toString());
      iaaFMeasureForm.setF1(new Float(fmForAllways.f1()).toString());
      iaaFMeasureForm.setPrecisionLenient(new Float(fmForAllways.precisionLenient()).toString());
      iaaFMeasureForm.setRecallLenient(new Float(fmForAllways.recallLenient()).toString());
      iaaFMeasureForm.setF1Lenient(new Float(fmForAllways.f1Lenient()).toString());
      iaaFMeasureForm.setCorrect(new Float(fmForAllways.correct()).toString());
      iaaFMeasureForm.setPartiallyCorrect(new Float(fmForAllways.partiallyCorrect()).toString());
      iaaFMeasureForm.setMissing(new Float(fmForAllways.missing()).toString());
      iaaFMeasureForm.setSpurious(new Float(fmForAllways.spurious()).toString());
      List<IAAResultForm> overallFList = new ArrayList<IAAResultForm>();
      overallFList.add(iaaFMeasureForm);
      request.setAttribute(Constants.IAAResult_Pairwise_F_Measure, overallFList);

      for(int j=0;j<asNames.length;j++){
        Map<String, String> sValues = new HashMap<String, String>();
        Map<String, String> lValues = new HashMap<String, String>();
        Map<String, String> oValues = new HashMap<String, String>();

        Map<String, List<LabelValueDetailForm>> labelDetailsMap = new HashMap<String, List<LabelValueDetailForm>>();

        IAAResultForm iForm = new IAAResultForm();
        iForm.setKeyASName(asNames[j]);
        for(int k=j+1;k<asNames.length;k++){
          FMeasure fmForPair = pairwiseResult.getFMeasureForPair(asNames[j], asNames[k]);
          sValues.put(asNames[k], fmForPair.precision() + " / " + fmForPair.recall()
            + " / " + fmForPair.f1());
          lValues.put(asNames[k], fmForPair.precisionLenient() + " / " + fmForPair.recallLenient()
            + " / " + fmForPair.f1Lenient());
          oValues.put(asNames[k], fmForPair.correct() + " / "+fmForPair.partiallyCorrect() + " / "
            + fmForPair.missing() + " / "+fmForPair.spurious());

          if(featureName != null) {
            List<LabelValueDetailForm> labelDetailsForPair = new ArrayList<LabelValueDetailForm>();
            if(pairwiseResult.getLabelValues()!=null){
            for(String label : pairwiseResult.getLabelValues()) {
              FMeasure fmForLabel = pairwiseResult.getFMeasureForLabel(asNames[j], asNames[k], label);
              LabelValueDetailForm dForLabel = new LabelValueDetailForm();
              dForLabel.setLabelValue(label);
              dForLabel.setStrictValues(fmForLabel.precision() + " / " + fmForLabel.recall()
                + " / " + fmForLabel.f1());
              dForLabel.setLenientValues(fmForLabel.precisionLenient()+" / "
                + fmForLabel.recallLenient()+" / "+fmForLabel.f1Lenient());
              dForLabel.setOtherValues(fmForLabel.correct() + " / " + fmForLabel.partiallyCorrect()+" / "
                +fmForLabel.missing()+" / "+fmForLabel.spurious());
              labelDetailsForPair.add(dForLabel);
            }
            }
            else{
            	log.debug("pairwiseResult.getLabelValues() is null");
            }

            labelDetailsMap.put(asNames[k], labelDetailsForPair);
          }

        }
        iForm.setStrictvalues(sValues);
        iForm.setLenientValues(lValues);
        iForm.setOtherValues(oValues);
        iForm.setLabelDetails(labelDetailsMap);
        list.add(iForm);
      }
      request.setAttribute(Constants.IAAResult_LIST, list);
      request.setAttribute(Constants.IAA_AS_NAMES, asNames);
      request.setAttribute(Constants.IAA_SCORE, pairwiseResult.getAgreement());

    }else if(iaaAlgorithm.equals("all-ways-f-measure")){
      System.out.println("calling all-ways-f-measure from the IAACaculationAction...");
      AllWaysFMeasureIAAResult allwaysResult = (AllWaysFMeasureIAAResult)
        mgr.calculateIAA(docIDs, asNames, annoType, featureName, IAAAlgorithm.ALL_WAYS_F_MEASURE);
      //Get the f-measure vaules for a response.
      List<IAAResultForm> list1 = new ArrayList<IAAResultForm >();
      List<String> resASList = new ArrayList<String>();
      IAAResultForm irForm;
      for(int m=1;m<asNames.length;m++){
        irForm = new IAAResultForm();
        FMeasure fmForAllwaysWithRes = allwaysResult.getFMeasure(asNames[m]);
        irForm.setAllwaysResponse(asNames[m]);
        irForm.setAllwaysStrictvalues(fmForAllwaysWithRes.precision() + " / " + fmForAllwaysWithRes.recall()
          + " / " + fmForAllwaysWithRes.f1());
        irForm.setAllwaysLenientValues(fmForAllwaysWithRes.precisionLenient() + " / "
          + fmForAllwaysWithRes.recallLenient() + " / " + fmForAllwaysWithRes.f1Lenient());
        irForm.setAllwaysOtherValues(fmForAllwaysWithRes.correct() + " / "
          +fmForAllwaysWithRes.partiallyCorrect() + " / "
          + fmForAllwaysWithRes.missing() + " / "+fmForAllwaysWithRes.spurious());
        list1.add(irForm);
        resASList.add(asNames[m]);
        Map<String, List<LabelValueDetailForm>> labelDetailsMap
           = new HashMap<String, List<LabelValueDetailForm>>();
        if(featureName != null) {
          List<LabelValueDetailForm> labelDetailsForResponse = new ArrayList<LabelValueDetailForm>();
          if(allwaysResult.getLabelValues()!=null){
          for(String label : allwaysResult.getLabelValues()) {
            FMeasure fmForLabel = allwaysResult.getFMeasureForLabel(asNames[m], label);
            LabelValueDetailForm dForLabel = new LabelValueDetailForm();
            dForLabel.setLabelValue(label);
            dForLabel.setStrictValues(fmForLabel.precision() + " / " + fmForLabel.recall()
              + " / " + fmForLabel.f1());
            dForLabel.setLenientValues(fmForLabel.precisionLenient()+" / "
              + fmForLabel.recallLenient()+" / "+fmForLabel.f1Lenient());
            dForLabel.setOtherValues(fmForLabel.correct() + " / " + fmForLabel.partiallyCorrect()+" / "
              +fmForLabel.missing()+" / "+fmForLabel.spurious());
            labelDetailsForResponse.add(dForLabel);
          }
          
        }
        else{
        	log.debug("alwaysResult.getLabelValues() is null");
        }

          labelDetailsMap.put(asNames[m], labelDetailsForResponse);
        }
        irForm.setLabelDetails(labelDetailsMap);
        list.add(irForm);
      }
      request.setAttribute(Constants.IAAResult_AllWays_Response_List,resASList);
      request.setAttribute(Constants.IAAResult_AllWays_List, list1);

      //Get overall F-Measure and set it in the request scope.
      FMeasure fmForAllways = allwaysResult.getOverallFMeasure();
      IAAResultForm iaaFMeasureForm = new IAAResultForm();
      iaaFMeasureForm.setPrecision(new Float(fmForAllways.precision()).toString());
      iaaFMeasureForm.setRecall(new Float(fmForAllways.recall()).toString());
      iaaFMeasureForm.setF1(new Float(fmForAllways.f1()).toString());
      iaaFMeasureForm.setPrecisionLenient(new Float(fmForAllways.precisionLenient()).toString());
      iaaFMeasureForm.setRecallLenient(new Float(fmForAllways.recallLenient()).toString());
      iaaFMeasureForm.setF1Lenient(new Float(fmForAllways.f1Lenient()).toString());
      iaaFMeasureForm.setCorrect(new Float(fmForAllways.correct()).toString());
      iaaFMeasureForm.setPartiallyCorrect(new Float(fmForAllways.partiallyCorrect()).toString());
      iaaFMeasureForm.setMissing(new Float(fmForAllways.missing()).toString());
      iaaFMeasureForm.setSpurious(new Float(fmForAllways.spurious()).toString());
      List<IAAResultForm> list2 = new ArrayList<IAAResultForm>();
      list2.add(iaaFMeasureForm);
      request.setAttribute(Constants.IAAResult_AllWays_F_Measure, list2);
      request.setAttribute(Constants.IAAResult_LIST, list);
      request.setAttribute(Constants.IAA_AS_NAMES, asNames);

    }else if(iaaAlgorithm.equals("pairwise-kappa")){
      PairwiseKappaIAAResult pwKappaResult = (PairwiseKappaIAAResult)
        mgr.calculateIAA(docIDs, asNames, annoType, featureName, IAAAlgorithm.PAIRWISE_KAPPA);
      //Get the overall result and set it to the request scope
      List<IAAResultForm> kappaOverallResultList = new ArrayList<IAAResultForm>();
      IAAResultForm irForm = new IAAResultForm();
      irForm.setKappaCohen(new Float(pwKappaResult.getOverallKappaCohen()).toString());
      irForm.setKappaPi(new Float(pwKappaResult.getOverallKappaPi()).toString());
      irForm.setObservedAgreement(new Float(pwKappaResult.getOverallObservedAgreement()).toString());
      kappaOverallResultList.add(irForm);
      request.setAttribute(Constants.IAAResult_Pairwise_Kappa_Overall, kappaOverallResultList);

      String[] labelValues = pwKappaResult.getLabelValues();
      for(int j=0;j<asNames.length;j++){
        Map<String, String> kappaValues = new HashMap<String, String>();
        Map<String, List<ConfusionMatrixRowForm>> confMatrices = new HashMap<String, List<ConfusionMatrixRowForm>>();
        IAAResultForm iForm = new IAAResultForm();
        iForm.setKeyASName(asNames[j]);
        for(int k=j+1;k<asNames.length;k++){
          KappaResult kaResult = pwKappaResult.getResult(asNames[j], asNames[k]);
          kappaValues.put(asNames[k],kaResult.getKappaCohen()+" / "+kaResult.getKappaPi()+
            " / "+kaResult.getObservedAgreement());
          if(featureName != null) {
            List<ConfusionMatrixRowForm> matrix = new ArrayList<ConfusionMatrixRowForm>();
            if(labelValues!=null){
            for(int m=0;m<=labelValues.length;m++) {
              String keyLabel = (m==0)?null : labelValues[m-1];
              ConfusionMatrixRowForm row = new ConfusionMatrixRowForm();
              row.setKeyLabel((keyLabel == null)? "NONE" : keyLabel);
              for(int n=0;n<=labelValues.length;n++){
                String responseLabel = (n==0)? null:labelValues[n-1];
                row.setEntry(responseLabel, kaResult.getConfusionMatrixEntry(keyLabel, responseLabel));
              }
              row.setSpecificAgreementPositive(new Float(kaResult.getSpecificAgreement(keyLabel, true)).toString());
              row.setSpecificAgreementNegative(new Float(kaResult.getSpecificAgreement(keyLabel, false)).toString());
              matrix.add(row);
            }
            confMatrices.put(asNames[k], matrix);
          }
        }
        else{
        	log.debug("pwKappaResult.getLabelValues() is null");
        }
        }
        iForm.setKappavalues(kappaValues);
        iForm.setConfusionMatrices(confMatrices);
        list.add(iForm);
      }
      request.setAttribute(Constants.IAAResult_LIST, list);
      request.setAttribute(Constants.IAA_AS_NAMES, asNames);
      request.setAttribute(Constants.IAAResult_LABEL_VALUES, labelValues);

    }else if(iaaAlgorithm.equals("all-ways-kappa")){
      AllWaysKappaIAAResult allwaysKappaResult = (AllWaysKappaIAAResult)
        mgr.calculateIAA(docIDs, asNames, annoType, featureName, IAAAlgorithm.ALL_WAYS_KAPPA);
      List<IAAResultForm> allwaysKappaResultList = new ArrayList<IAAResultForm>();
      IAAResultForm irForm = new IAAResultForm();
      irForm.setKappaSC(new Float(allwaysKappaResult.getKappaSC()).toString());
      irForm.setKappaDF(new Float(allwaysKappaResult.getKappaDF()).toString());
      irForm.setObservedAgreement(new Float(allwaysKappaResult.getObservedAgreement()).toString());
      allwaysKappaResultList.add(irForm);
      request.setAttribute(Constants.IAAResult_Allways_Kappa_Overall, allwaysKappaResultList);
    }

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
    log.debug("Entering 'IAACaculateAction search Annotation Set Names' method");
    ApplicationContext ctx = WebApplicationContextUtils
            .getRequiredWebApplicationContext(servlet.getServletContext());
    DocServiceManager mgr = (DocServiceManager)ctx.getBean("docServiceManager");

    String docID = (String)request.getParameter("documentID");
    String selectedAS=request.getParameter("SelectedList");
    List<String> annSetNames = (List<String>)mgr.listAnnotationSetNames(docID);

    List<LabelValue> labelList = new ArrayList<LabelValue>();
    for(int i=0;i<annSetNames.size();i++){

      String annSetName=annSetNames.get(i);
      if(annSetName==null){
        annSetName="<Default>";
      }
      System.out.println("*****"+annSetNames.get(i)+"***********");
      labelList.add(new LabelValue(annSetName,annSetName));
    }
    request.setAttribute(Constants.AVAILABLE_AS_NAMES,labelList);

    String firstAnnoSetName =annSetNames.get(0);
    List<IAAResultForm> annoTypes=new ArrayList<IAAResultForm>();

    String[] asNames = StringUtils.commaDelimitedListToStringArray(selectedAS);

    if(asNames!=null&&asNames.length>=2){
      System.out.println("Calling from the last selected case**********");
      List<String> asTypes=mgr.listSharedAnnotationTypes(docID,asNames);
      for(int j=0;asTypes.size()>0&&j<asTypes.size();j++){
        IAAResultForm iaaForm = new IAAResultForm();
        for(int k=0;k<asNames.length;k++){
          iaaForm.addASNames(asNames[k]);
        }
        iaaForm.setAnnoType((String)asTypes.get(j));
        annoTypes.add(iaaForm);
      }
    }else{
      System.out.println("Calling from the first case**********");
      List<String> asTypes=mgr.listSharedAnnotationTypes(docID,firstAnnoSetName,firstAnnoSetName);
      for(int j=0;asTypes.size()>0&&j<asTypes.size();j++){
        IAAResultForm iaaForm = new IAAResultForm();
        iaaForm.addASNames(firstAnnoSetName);
        iaaForm.setAnnoType((String)asTypes.get(j));
        annoTypes.add(iaaForm);
      }
    }
    request.setAttribute(Constants.ANNOTATION_Types,annoTypes);

    List<IAAResultForm> algorithmsList = new ArrayList<IAAResultForm>();
    String[] iaaAlgorithms=new String[4];
    iaaAlgorithms[0]="pairwise-f-measure";
    iaaAlgorithms[1]="all-ways-f-measure";
    iaaAlgorithms[2]="pairwise-kappa";
    iaaAlgorithms[3]="all-ways-kappa";
    for(int m=0;m<iaaAlgorithms.length;m++){
      IAAResultForm iaaForm = new IAAResultForm();
      iaaForm.setAlgorithm(iaaAlgorithms[m]);
      algorithmsList.add(iaaForm);
    }
    request.setAttribute(Constants.IAA_Algorithms_List, algorithmsList);

    String show =request.getParameter("show");
    if(show!=null&&show.equals("false")){
      return mapping.findForward("showQueries");
    }

    if(show!=null&&show.equals("true")){
      this.caculateIAA(mapping, form, request, response);
      return mapping.findForward("success");
    }
    return mapping.findForward("success");
  }

}
