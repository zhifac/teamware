package gleam.executive.workflow.util;

/**
 * A generic exception that is thrown when error occurs in Workflow Engine
 */

public class WorkflowException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2913751419904337119L;

	/**
	 * Constructor for WorkflowException.
	 * 
	 * @param message
	 */
	public WorkflowException(String message) {
		super(message);
	}

	/**
	 * Constructor for WorkflowException.
	 * 
	 * @param Exception
	 */
	public WorkflowException(Throwable cause) {
		super(cause);
	}

	public WorkflowException(String message, Throwable cause) {
		super(message, cause);
	}
}
