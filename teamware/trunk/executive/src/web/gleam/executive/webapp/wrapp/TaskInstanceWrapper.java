package gleam.executive.webapp.wrapp;

import gleam.executive.workflow.core.JbpmTaskInstance;
import gleam.executive.workflow.util.JPDLConstants;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.taskmgmt.exe.TaskInstance;

//TODO Javadoc
public class TaskInstanceWrapper {
	private long id = 0;

	private String name = null;

	private String description = null;
	
	private String documentId = null;

	private String processInstanceKey = null;

	private String state = null;

	private Date create = null;

	private Date start = null;

	private Date end = null;

	private Date dueDate = null;

	private int priority;

	private String actorName = null;

	private Set pooledActors = null;

	private static Log log = LogFactory.getLog(TaskInstanceWrapper.class);

	public TaskInstanceWrapper(TaskInstance taskInstance) {
		this.name = taskInstance.getName();
		this.id = taskInstance.getId();
		
		if (taskInstance.getProcessInstance().getSuperProcessToken() != null) {
			log.debug("get the name from the parent process");
			ProcessInstance parentProcessInstance = taskInstance.getProcessInstance()
			.getSuperProcessToken().getProcessInstance();
			this.processInstanceKey = (String) parentProcessInstance.getContextInstance().getVariables().get(JPDLConstants.PROCESS_INSTANCE_NAME);

		} else {
			this.processInstanceKey = (String) taskInstance.getProcessInstance().getContextInstance().getVariables().get(JPDLConstants.PROCESS_INSTANCE_NAME);
		}

		this.description = taskInstance.getDescription();
		if(taskInstance instanceof JbpmTaskInstance){
			this.documentId = ((JbpmTaskInstance)taskInstance).getDocumentId();	
		}
		
		this.create = taskInstance.getCreate();
		if(taskInstance.getStart()!=null){
		   this.start = taskInstance.getStart();
		}
		else {
			this.start = taskInstance.getCreate();
		}
		this.end = taskInstance.getEnd();
		this.dueDate = taskInstance.getDueDate();
		this.actorName = taskInstance.getActorId();
		if (taskInstance.getPooledActors() != null
				&& taskInstance.getPooledActors().size() > 0) {
			this.pooledActors = taskInstance.getPooledActors();
		} else {
			this.pooledActors = null;
		}
		if (taskInstance.isBlocking())
			this.state = "Blocked";
		else if (taskInstance.isCancelled())
			this.state = "Canceled";
		else if (taskInstance.isOpen())
			this.state = "Pending";
		else if (taskInstance.hasEnded())
			this.state = "Completed";
		else if (taskInstance.isSuspended())
			this.state = "Suspended";
		this.priority = taskInstance.getPriority();
	}

	public Date getCreate() {
		return create;
	}

	public void setCreate(Date create) {
		this.create = create;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
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

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	/**
	 * @return Returns the actorName.
	 */
	public String getActorName() {
		return actorName;
	}

	/**
	 * @param actorName
	 *            The actorName to set.
	 */
	public void setActorName(String actorName) {
		this.actorName = actorName;
	}

	public String getProcessInstanceKey() {
		return processInstanceKey;
	}

	public void setProcessInstanceKey(String processInstanceKey) {
		this.processInstanceKey = processInstanceKey;
	}

	public Set getPooledActors() {
		return pooledActors;
	}

	public void setPooledActors(Set pooledActors) {
		this.pooledActors = pooledActors;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}
	
	public String getDocumentId() {
		return documentId;
	}

	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

}
