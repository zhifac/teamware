package gleam.executive.workflow.action.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.taskmgmt.exe.TaskInstance;
import gleam.executive.workflow.sm.JbpmHandlerProxy;

/*
 * <outVarTaskPerformer>taskPerformer</outVarTaskPerformer>
 */

public class LogTaskPerformerActionHandler extends JbpmHandlerProxy {

	protected static final Log log = LogFactory
			.getLog(LogTaskPerformerActionHandler.class);

	String outVarTaskPerformer;

	public void execute(ExecutionContext context) throws Exception {
		log.debug("@@@@@@@@@@@@@@@@@@@@@@@@");
		log.debug("LogTaskPerformerActionHandler START");

		TaskInstance taskInstance = context.getTaskInstance();
		log.debug("obtained  taskInstance " + taskInstance.getName());
		String performer = taskInstance.getActorId();
		log.debug("obtained  performer " + performer);
		context.setVariable(getOutVarTaskPerformer(), performer);

		log.debug("LogTaskPerformerActionHandler END");
		log.debug("@@@@@@@@@@@@@@@@@@@@@@@@");
	}

	public String getOutVarTaskPerformer() {
		return outVarTaskPerformer;
	}

	public void setOutVarTaskPerformer(String outVarTaskPerformer) {
		this.outVarTaskPerformer = outVarTaskPerformer;
	}

}
