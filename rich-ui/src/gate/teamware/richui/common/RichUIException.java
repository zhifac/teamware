/*
 *  AnnotatorGUIExeption.java
 *
 *  Copyright (c) 2006-2006, The University of Sheffield.
 *
 *  Andrey Shafirin, 09/Jun/2006
 */

package gate.teamware.richui.common;

/**
 * Exception signalling a problem with the Annotator GUI.
 * 
 * @author Andrey Shafirin
 */
public class RichUIException extends Exception {
	public RichUIException() {
		super();
	}

	public RichUIException(String message) {
		super(message);
	}

	public RichUIException(Throwable cause) {
		super(cause);
	}

	public RichUIException(String message, Throwable cause) {
		super(message, cause);
	}
}
