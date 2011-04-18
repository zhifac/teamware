/*
 *  AnnotatorGUIExeption.java
 *
 *  Copyright (c) 2006-2006, The University of Sheffield.
 *
 *  Andrey Shafirin, 09/Jun/2006
 */

package gleam.annotatorgui;

/**
 * Exception signalling a problem with the Annotator GUI.
 * 
 * @author Andrey Shafirin
 */
public class AnnotatorGUIExeption extends Exception {
	public AnnotatorGUIExeption() {
		super();
	}

	public AnnotatorGUIExeption(String message) {
		super(message);
	}

	public AnnotatorGUIExeption(Throwable cause) {
		super(cause);
	}

	public AnnotatorGUIExeption(String message, Throwable cause) {
		super(message, cause);
	}
}
