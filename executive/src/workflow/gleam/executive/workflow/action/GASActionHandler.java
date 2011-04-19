/*
 *  GASActionHandler.java
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
package gleam.executive.workflow.action;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import gleam.executive.model.AnnotationService;
import gleam.executive.model.WebAppBean;
import gleam.executive.service.AnnotationServiceManager;
import gleam.executive.service.GateServiceManager;
import gleam.executive.util.GATEUtil;
import gleam.executive.util.XstreamUtil;
import gleam.executive.workflow.core.JbpmTaskInstance;
import gleam.executive.workflow.jms.MessageProducer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.jbpm.graph.exe.ExecutionContext;
import org.springframework.util.StringUtils;

import gleam.executive.workflow.util.JPDLConstants;
import gleam.executive.workflow.sm.JbpmDataflowHandlerProxy;


public class GASActionHandler extends JbpmDataflowHandlerProxy {

	private static final long serialVersionUID = 1L;

	private GateServiceManager gateServiceManager;
	
	private AnnotationServiceManager annotationServiceManager;
	
	

	private WebAppBean webAppBean;

	private MessageProducer messageProducer;

	protected final Log log = LogFactory.getLog(getClass());

	private Element targetProperties;

	/*
	  *	<property name="mode" type="in" scope="global" empty="true"></property>
	  *	<property name="serviceId" type="in" scope="global" empty="false"></property>
	  *	<property name="callbackTaskId" type="in" scope="local" empty="true"></property>
      */


	/**
	 * Extra annotation set mappings. This is a comma-delimited list of
	 * <code>key=value</code> pairs. Any of the keys and/or values may be
	 * empty, indicating the default annotation set. Thus a value of
	 *
	 * data=fixed data,=ANNIE,dummy=
	 *
	 * denotes three mappings, and the GaS will receive
	 * <ul>
	 * <li>the contents of the <code>fixed</code> set from the doc service in
	 * its <code>data</code> annotation set,</li>
	 * <li>the contents of the <code>ANNIE</code> set from the doc service in
	 * its default set, and</li>
	 * <li>the contents of the default annotation set from the doc service in
	 * its <code>dummy</code> set.</li>
	 * </ul>
	 *
	 * To map the default set from the DS to the default set in the GaS, use a
	 * mapping consisting of just an equals sign with nothing either side.
	 *
	 * Whitespace either side of the commas and equals signs is ignored, but
	 * whitespace <i>within</i> annotation set names is preserved.
	 */


	public void execute(ExecutionContext context) throws Exception {
		    log.debug("@@@@@ GASActionHandler START");
			Map<String, String> variableMap = fetchAndValidateVariables(context,
	                this.getClass().getName(),
	                targetProperties);
			
			String endpointURL = null;
			String asKey = null;
			String asValue = null; 
			String asExtraMappings = null;
			String parameterKey = null;
			String parameterValue = null;
			String callbackTaskId = null;
			// obtain targetProperties
		    String mode = (String) variableMap.get(JPDLConstants.MODE);
		    log.debug("@@@@@@@ mode " + mode);
			// first try to obtain serviceId 
		    String gasId = (String) variableMap.get(JPDLConstants.SERVICE_ID);
		    log.debug("@@@@@@@ gasId " + gasId);
		    
			callbackTaskId = (String) variableMap.get(JPDLConstants.CALLBACK_TASK_ID);			
			log.debug("@@@@@@@ callbackTaskId " + callbackTaskId);

			if(!"0".equals(gasId)){
			AnnotationService annotationService = annotationServiceManager.getAnnotationService(new Long(gasId));
		    endpointURL = annotationService.getUrl();
		    // normalise URL:
		    if(!endpointURL.startsWith("http")){
		    	log.debug("normalise URL: "+endpointURL);
		    	StringBuilder sb = new StringBuilder()
			      .append(webAppBean.getPrivateUrlBase())
			      .append("/")
			      .append(webAppBean.getInstanceName());
		    	endpointURL = sb.toString() + endpointURL;
		    }
		    log.debug("endpointURL: "+endpointURL);
		    String parameterData = annotationService.getParameters();
	
			Map<String, String> parameterMap = XstreamUtil.fromStringToMap(parameterData);
			log.debug("parameterMap: "+parameterMap);
			if(parameterMap.get(JPDLConstants.AS_KEY)!=null){
			 asKey = (String) parameterMap.get(JPDLConstants.AS_KEY);
			}
			if(parameterMap.get(JPDLConstants.AS_VALUE)!=null){
			 asValue = (String) parameterMap.get(JPDLConstants.AS_VALUE);
			}
			if(parameterMap.get(JPDLConstants.AS_EXTRA_MAPPINGS)!=null){
				asExtraMappings = (String) parameterMap.get(JPDLConstants.AS_EXTRA_MAPPINGS);	
			}
			if(parameterMap.get(JPDLConstants.PARAMETER_KEY)!=null){
				parameterKey = (String) parameterMap.get(JPDLConstants.PARAMETER_KEY);		
			}
			if(parameterMap.get(JPDLConstants.PARAMETER_VALUE)!=null){
				parameterValue = (String) parameterMap.get(JPDLConstants.PARAMETER_VALUE);			
			}
			
			URI gsURI = new URI(endpointURL);
			log.debug("@@@@@@@ endpointURL " + endpointURL);
			log.debug("@@@@@@@ aSKey " + asKey);
			log.debug("@@@@@@@ aSValue " + asValue);
			log.debug("@@@@@@@ asExtraMappings " + asExtraMappings);
			log.debug("@@@@@@@ parameterKey " + parameterKey);
			log.debug("@@@@@@@ parameterValue " + parameterValue);
			
			long processInstanceId = context.getProcessInstance().getId();
			log.debug("current ProcessInstance ID " + processInstanceId);

			String processDefinitionName = context.getProcessInstance().getProcessDefinition().getName();
			log.debug("current ProcessDefinition Name " + processDefinitionName);

			String nodeName = context.getNode().getName();
			log.debug("current Node Name " + nodeName);

			long tokenId = context.getToken().getId();
			log.debug("current tokenId " + tokenId);
			String tokenName = context.getToken().getName();
			log.debug("current tokenName " + tokenName);

			JbpmTaskInstance taskInstance = (JbpmTaskInstance)context.getTaskInstance();
			
			log.debug("current taskInstance " + taskInstance.getId());
			String documentId = taskInstance.getDocumentId();
	        log.debug("@@@@@@@ documentId " + documentId);
	        
	       

	        if(asValue==null || "".equals(asValue)){
	            // get it right from current taskInstance
	        	// if asValue is not null, it is about regular standalone GAS on task-create event
	        	asValue = taskInstance.getAnnotationSetName();
	        }

			if(!JPDLConstants.TEST_MODE.equals(mode)){
			   Map<String, String> asMappings = new HashMap<String, String>();
			   // main AS mapping, typically involving the annotator name
			   if (asKey != null && asValue != null) {
				asMappings.put(asKey, asValue);
			   }

			   // process extra AS mappings
			   if (asExtraMappings != null) {
				  String[] mappings = StringUtils
						.commaDelimitedListToStringArray(asExtraMappings);
				  if (mappings != null) {
					 for (String m : mappings) {
						int indexOfEquals = m.indexOf('=');
						if(indexOfEquals!=-1){
						  String k = m.substring(0, indexOfEquals).trim();
						  String v = m.substring(indexOfEquals + 1).trim();
						  asMappings.put(k, v);
						}
					}
				  }
			   }

			   Map<String, String> parameterMappings = null;
			   if (parameterKey != null && parameterValue != null) {
				 parameterMappings = new HashMap<String, String>();
				 parameterMappings.put(parameterKey, parameterValue);
			   }
			   //GATEUtil.startPoint(callbackTaskId);
			   
			   
			   gateServiceManager.processRemoteDocument(gsURI, callbackTaskId,
					documentId, asMappings, parameterMappings,
					annotationService.isCanUsePrivateUrls());
			   
			   
			}
			else {
				log.debug("TEST MODE");
				messageProducer.sendMessage(callbackTaskId);

			}
			}
			else {
				log.debug("DUMMY GAS -> service id =0");
				messageProducer.sendMessage(callbackTaskId);

			}
			
			
		    log.debug("@@@@ GASActionHandler END");
	}

	public GateServiceManager getGateServiceManager() {
		return gateServiceManager;
	}

	public void setGateServiceManager(GateServiceManager gateServiceManager) {
		this.gateServiceManager = gateServiceManager;
	}


	public MessageProducer getMessageProducer() {
		return messageProducer;
	}

	public void setMessageProducer(MessageProducer messageProducer) {
		this.messageProducer = messageProducer;
	}

	public Element getTargetProperties() {
		return targetProperties;
	}

	public void setTargetProperties(Element targetProperties) {
		this.targetProperties = targetProperties;
	}

	public AnnotationServiceManager getAnnotationServiceManager() {
		return annotationServiceManager;
	}

	public void setAnnotationServiceManager(
			AnnotationServiceManager annotationServiceManager) {
		this.annotationServiceManager = annotationServiceManager;
	}
	
	public WebAppBean getWebAppBean() {
		return webAppBean;
	}

	public void setWebAppBean(WebAppBean webAppBean) {
		this.webAppBean = webAppBean;
	}


}
