package gleam.executive.workflow.util;

/**
 * A generic exception that is thrown when name is not declared inside
 * targetproperties declaration of the action handler in JPDL
 */

public class InvalidVariableNameException extends ProcessDefinitionException {


	/**
	 * Constructor for InvalidVariableValueException.
	 *
	 * @param message
	 */
	public InvalidVariableNameException(String message) {
		super(message);
	}

}
