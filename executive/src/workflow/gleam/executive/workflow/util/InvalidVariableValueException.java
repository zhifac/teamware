package gleam.executive.workflow.util;

/**
 * A generic exception that is thrown when the invalid variable value is detected inside Action Handler
 */

public class InvalidVariableValueException extends ProcessDefinitionException {


	/**
	 * Constructor for InvalidVariableValueException.
	 *
	 * @param message
	 */
	public InvalidVariableValueException(String message) {
		super(message);
	}

}
