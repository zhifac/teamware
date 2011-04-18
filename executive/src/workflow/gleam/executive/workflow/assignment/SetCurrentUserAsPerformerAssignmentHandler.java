package gleam.executive.workflow.assignment;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.taskmgmt.def.AssignmentHandler;
import org.jbpm.taskmgmt.exe.Assignable;
import gleam.executive.workflow.sm.JbpmHandlerProxy;

public class SetCurrentUserAsPerformerAssignmentHandler extends JbpmHandlerProxy {

	  private static final long serialVersionUID = 1L;
	  protected final Log log = LogFactory.getLog(getClass());


	  public void assign(Assignable assignable, ExecutionContext executionContext) {
          log.debug("@@@@@ ASSIGNMENT HANDLER START");
		  String performer = (String)executionContext.getContextInstance().getVariable("performer");
          log.debug("performer: "+performer);
          if(performer!=null && !performer.equals("")){
			  assignable.setActorId(performer);
			  //assignable.setPooledActors(new String[]{});
		  }
	  }
}
