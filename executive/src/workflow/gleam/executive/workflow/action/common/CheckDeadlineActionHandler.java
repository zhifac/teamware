package gleam.executive.workflow.action.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.graph.exe.ExecutionContext;
import gleam.executive.workflow.sm.JbpmHandlerProxy;

public class CheckDeadlineActionHandler extends JbpmHandlerProxy {

	private static final long serialVersionUID = 1L;
	protected final Log log = LogFactory.getLog(getClass());

	public void execute(ExecutionContext executionContext) throws Exception {

		log.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		log.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		log.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		log.debug(">>>>> TASK STARTED >>>>>>>>>>>>>>>>>>>>>");
		log.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		log.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		log.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		/*
		TaskInstance ti = executionContext.getTaskInstance();
		Date deadline = ti.getDueDate();

		if(deadline!=null){
			// check if it is not date before current date
			if(deadline.getTime()<System.currentTimeMillis()){
				// cancel proces instance and all its tasks
				//ti.end("End");
				System.out.println(">>>>> FIRE ACTION");
				//JbpmWorkflowUtil.sendEmail(process, ti);

			}
		}
		*/
	}
}
