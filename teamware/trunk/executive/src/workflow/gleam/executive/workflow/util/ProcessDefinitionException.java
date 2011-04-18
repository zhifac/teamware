package gleam.executive.workflow.util;

/**
 * A generic exception that is thrown when name is not declared inside
 * targetproperties declaration of the action handler in JPDL
 */

public class ProcessDefinitionException extends WorkflowException {


	/**
	 * Constructor for AsyncServiceExecutionException.
	 *
	 * @param message
	 */
	public ProcessDefinitionException(String message) {
		super(message);
	}

}
