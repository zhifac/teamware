/*
 *  WorkflowException.java
 *
 *  Copyright (c) 2006-2011, The University of Sheffield.
 *
 *  This file is part of GATE Teamware (see http://gate.ac.uk/teamware/), 
 *  and is free software, licenced under the GNU Affero General Public License,
 *  Version 3, November 2007 (also included with this distribution as file 
 *  LICENCE-AGPL3.html).
 *
 *  A commercial licence is also available for organisations whose business
 *  models preclude the adoption of open source and is subject to a licence
 *  fee charged by the University of Sheffield. Please contact the GATE team
 *  (see http://gate.ac.uk/g8/contact) if you require a commercial licence.
 *
 *  $Id$
 */
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
