/*
 *  AnnicGUIListener.java
 *
 *  Copyright (c) 2006-2006, The University of Sheffield.
 *
 *  Niraj Aswani, 09/Jun/2006
 */

package gleam.annic;

/**
 * An interface to handle Annic GUI messages.
 * 
 * @author Niraj Aswani
 */

public interface AnnicGUIListener {

	/**
	 * Called when connection changed.
	 */
	void connectionChanged(Connection newConnection);
}
