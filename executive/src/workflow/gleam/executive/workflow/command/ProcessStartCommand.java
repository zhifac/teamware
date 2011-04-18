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
