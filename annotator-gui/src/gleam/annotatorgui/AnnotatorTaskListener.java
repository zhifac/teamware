/*
 *  AnnotatorTaskListener.java
 *
 *  Copyright (c) 2006-2006, The University of Sheffield.
 *
 *  Andrey Shafirin, 27/Jun/2006
 */

package gleam.annotatorgui;

/**
 * An interface to handle ExecutiveConnection messages.
 * 
 * @author Andrey Shafirin
 */
public interface AnnotatorTaskListener {
	/**
	 * Called when annotation task changed.
	 */
	void taskChanged(AnnotatorTask newAnnotatorTask);
}
