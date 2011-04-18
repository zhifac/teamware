package gleam.executive.workflow.assignment;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.taskmgmt.exe.Assignable;
import gleam.executive.workflow.sm.JbpmHandlerProxy;

public class SetPerformerAssignmentHandler extends JbpmHandlerProxy {

	  private static final long serialVersionUID = 1L;
	  protected final Log log = LogFactory.getLog(getClass());

	  private String inVarPerformer;

	  public void assign(Assignable assignable, ExecutionContext executionContext) {

		  String performer = (String)executionContext.getContextInstance().getVariable(getInVarPerformer());
		  log.debug("Assign performer " +performer);
		  if(performer!=null && !performer.equals("") && performer.indexOf(",")!=-1){
			  String[] poolesActors = performer.split(",");
			  assignable.setPooledActors(poolesActors);
			  assignable.setActorId("");

		  }else{
			  assignable.setPooledActors(new String[]{});
			  assignable.setActorId(performer);
		  }
	  }

	public String getInVarPerformer() {
		return inVarPerformer;
	}

	public void setInVarPerformer(String inVarPerformer) {
		this.inVarPerformer = inVarPerformer;
	}

}
