/*
 *  AnnotationDiffGUIException.java
 *
 *  Copyright (c) 2006-2006, The University of Sheffield.
 *
 *  Andrey Shafirin, 09/Jun/2006
 */

package gleam.annotationdiffgui;

/**
 * Exception signalling a problem with the Annotator GUI.
 * 
 * @author Andrey Shafirin
 */
public class AnnotationDiffGUIException extends Exception {
	public AnnotationDiffGUIException() {
		super();
	}

	public AnnotationDiffGUIException(String message) {
		super(message);
	}

	public AnnotationDiffGUIException(Throwable cause) {
		super(cause);
	}

	public AnnotationDiffGUIException(String message, Throwable cause) {
		super(message, cause);
	}
}
