/*
 *  AnnicGUIExeption.java
 *
 *  Copyright (c) 2006-2006, The University of Sheffield.
 *
 *  Andrey Shafirin, 09/Jun/2006
 */

package gleam.annic;

/**
 * Exception signalling a problem with the Annotator GUI.
 * 
 * @author Andrey Shafirin
 */
public class AnnicGUIExeption extends Exception {
	public AnnicGUIExeption() {
		super();
	}

	public AnnicGUIExeption(String message) {
		super(message);
	}

	public AnnicGUIExeption(Throwable cause) {
		super(cause);
	}

	public AnnicGUIExeption(String message, Throwable cause) {
		super(message, cause);
	}
}
