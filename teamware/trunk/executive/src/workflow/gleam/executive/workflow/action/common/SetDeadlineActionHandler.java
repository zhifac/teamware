package gleam.executive.workflow.action.common;

import java.util.Date;

import org.jbpm.context.exe.ContextInstance;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.taskmgmt.exe.TaskInstance;
import gleam.executive.workflow.sm.JbpmHandlerProxy;

public class SetDeadlineActionHandler extends JbpmHandlerProxy {
	  private static final long serialVersionUID = 1L;

	  public void execute(ExecutionContext executionContext) throws Exception {
		  ContextInstance ci = executionContext.getContextInstance();
		  Date deadline = (Date)ci.getVariable("deadline");
		  TaskInstance currentTaskInstance = executionContext.getTaskInstance();
		  if(deadline!= null && currentTaskInstance != null)
			  currentTaskInstance.setDueDate(deadline);
	  }
}
