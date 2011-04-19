/*
 *  ProcessStartCommand.java
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
package gleam.executive.workflow.command;

import java.io.Serializable;
import java.util.Map;

public class ProcessStartCommand implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String key;
	
	private Map<String, Object> variableMap;
	
	private Long processDefinitionId;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Map<String, Object> getVariableMap() {
		return variableMap;
	}

	public void setVariableMap(Map<String, Object> variableMap) {
		this.variableMap = variableMap;
	}

	public Long getProcessDefinitionId() {
		return processDefinitionId;
	}

	public void setProcessDefinitionId(Long processDefinitionId) {
		this.processDefinitionId = processDefinitionId;
	}

	public ProcessStartCommand(String key, Map<String, Object> variableMap,
			Long processDefinitionId) {
		this.key = key;
		this.variableMap = variableMap;
		this.processDefinitionId = processDefinitionId;
	}
	
	
}
