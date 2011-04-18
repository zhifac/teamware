/*
 *  AnnotatorGUIListener.java
 *
 *  Copyright (c) 2006-2006, The University of Sheffield.
 *
 *  Andrey Shafirin, 09/Jun/2006
 */

package gleam.annotatorgui;

/**
 * An interface to handle Annotator GUI messages.
 * 
 * @author Andrey Shafirin
 */
import gate.Document;

public interface AnnotatorGUIListener {

	/**
	 * Called when connection changed.
	 */
	void connectionChanged(Connection newConnection);
}
