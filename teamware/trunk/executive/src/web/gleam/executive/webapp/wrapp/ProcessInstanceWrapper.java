package gleam.executive.webapp.wrapp;

import gleam.executive.workflow.util.JPDLConstants;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.graph.exe.Comment;
import org.jbpm.graph.exe.ProcessInstance;
import org.springframework.util.StringUtils;

// TODO Javadoc
public class ProcessInstanceWrapper {
	private long id = 0;

	private String name = "";

	private String corpusId = "N/A";
	
	private String username = "";

	private String key = "";

	private Date start = null;

	private Date end = null;

	private String status = "Started";

	private boolean suspended;

	private boolean ended;

	private long parentId = 0;
	
	private Set<String> annotationSchemaNames;
	
	private Set<String> actors;
	
	private Integer version;

	
	private static Log log = LogFactory.getLog(ProcessInstanceWrapper.class);

	public ProcessInstanceWrapper(ProcessInstance processInstance) {
		this.id = processInstance.getId();
		Map variableMap =  processInstance.getContextInstance().getVariables();
		this.corpusId = (String) variableMap.get(JPDLConstants.CORPUS_ID);
		String annotationSchemaCSVNames = (String) variableMap.get(JPDLConstants.ANNOTATION_SCHEMA_CSV_URLS);
		this.annotationSchemaNames = StringUtils.commaDelimitedListToSet(annotationSchemaCSVNames);
		String annotatorCSVList = (String) variableMap.get(JPDLConstants.ANNOTATOR_CSV_LIST);
		//String curatorCSVList = (String) variableMap.get(JPDLConstants.CURATOR_CSV_LIST);
        if(annotatorCSVList!=null){
		this.actors = StringUtils.commaDelimitedListToSet(annotatorCSVList);
        }
        else {
        	this.actors = new HashSet<String>();
        }
        /*
        if(curatorCSVList!=null){
        	Set<String> curators = StringUtils.commaDelimitedListToSet(curatorCSVList);
        	this.actors.addAll(curators);	
        }
        */
		
		this.name = (String) variableMap.get(JPDLConstants.PROCESS_INSTANCE_NAME);
		this.username = (String) variableMap.get(JPDLConstants.INITIATOR);
		if (this.name == null || "".equals(this.name)) {
			

		log
		.debug("there is no variable processInstanceName. get the name from the process definition");
        this.name = processInstance.getProcessDefinition().getName();

			
			
		}
		this.key = processInstance.getKey();
		if (this.key == null || "".equals(this.key)) {
			// get the key from the parent process (probably recursion would be
			// needed)
			if (processInstance.getSuperProcessToken() != null) {
				log.debug("get the key from the parent process");
				this.key = processInstance.getSuperProcessToken()
						.getProcessInstance().getKey();
			} else {
				log.debug("leave the key empty");
			}
		}
		this.start = processInstance.getStart();
		this.end = processInstance.getEnd();
		this.suspended = processInstance.isSuspended();
		this.ended = processInstance.hasEnded();
		if (processInstance.isSuspended())
			this.status = "suspended";
		else if (processInstance.hasEnded())
			this.status = "completed";
		else
			this.status = "started";
		if (processInstance.getSuperProcessToken() != null) {
			// set parentId and key in case of subprocess
			parentId = processInstance.getSuperProcessToken()
					.getProcessInstance().getId();
			if (this.key == null || "".equals(this.key)) {
				this.key = processInstance.getSuperProcessToken()
						.getProcessInstance().getKey();
			}
		}
		
		this.version = processInstance.getProcessDefinition().getVersion();

	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public boolean isRunning() {
		return !(isSuspended() || isEnded());
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public boolean isEnded() {
		return ended;
	}

	public void setEnded(boolean ended) {
		this.ended = ended;
	}

	public boolean isSuspended() {
		return suspended;
	}

	public void setSuspended(boolean suspended) {
		this.suspended = suspended;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public long getParentId() {
		return parentId;
	}

	public void setParentId(long parentId) {
		this.parentId = parentId;
	}

	public String getCorpusId() {
		return corpusId;
	}

	public void setCorpusId(String corpusId) {
		this.corpusId = corpusId;
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public Set<String> getAnnotationSchemaNames() {
		return annotationSchemaNames;
	}

	public void setAnnotationSchemaNames(Set<String> annotationSchemaNames) {
		this.annotationSchemaNames = annotationSchemaNames;
	}

	public Set<String> getActors() {
		return actors;
	}

	public void setActors(Set<String> actors) {
		this.actors = actors;
	}

	
	 public String toString() {
		    return this.name;
		  }

}
