/*
 *  GASConfigurationActionHandler.java
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

import java.util.HashMap;
import java.util.Map;

import gleam.executive.workflow.sm.JbpmDataflowHandlerProxy;
import gleam.executive.workflow.util.CSVUtil;
import gleam.executive.workflow.util.JPDLConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.Token;

public class GASConfigurationActionHandler extends JbpmDataflowHandlerProxy {

	private static final long serialVersionUID = 1L;

	protected final Log log = LogFactory.getLog(getClass());

	
	private Element targetProperties;

	/*
	 * <property name="serviceId" type="in" scope="global" ref="preProcessingServiceId" empty="false"></property>
	 * <property name="gasPipelineCSVList" type="inout" scope="global" ref="preprocessingPipelineCSVList" empty="false"></property>
	 */
	
	public void execute(ExecutionContext context) throws Exception {
	    log.debug("@@@@@ GASConfigurationActionHandler START");
		Map<String, String> variableMap = fetchAndValidateVariables(context,
                this.getClass().getName(),
                targetProperties);
        
		String serviceId = (String) variableMap.get(JPDLConstants.SERVICE_ID);
		log.debug("@@@@@@@ serviceId " + serviceId);

		Token token = context.getToken();
        Map<String, Object> map = new HashMap<String, Object>();

		// now first check if gasPipelineCSVList already exists, 
		
		String gasPipelineCSVList = (String) variableMap.get(JPDLConstants.GAS_PIPELINE_CSV_LIST);
		if(gasPipelineCSVList==null){
			gasPipelineCSVList = "";
			log.debug("@@@@@@@ gasPipelineCSVList does not exist. create new");
			}
		else {
			log.debug("@@@@@@@ gasPipelineCSVList: "+gasPipelineCSVList);
		}

		
		// add service name to pipeline
		gasPipelineCSVList = CSVUtil.appendTokenToCSVString(serviceId, gasPipelineCSVList);
		log.debug("@@@@@@@ gasPipelineCSVList: "+ gasPipelineCSVList);
		
		map.put(JPDLConstants.GAS_PIPELINE_CSV_LIST, gasPipelineCSVList);
		submitTokenVariables(context,this.getClass().getName(), targetProperties, map, token);
	    
		log.debug("@@@@ GASConfigurationActionHandler END");
	}

	public Element getTargetProperties() {
		return targetProperties;
	}

	public void setTargetProperties(Element targetProperties) {
		this.targetProperties = targetProperties;
	}


}
