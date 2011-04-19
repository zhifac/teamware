/*
 *  AnnotationSetHandle.java
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

package gleam.docservice;

/**
 * This is class contains information about document annotation set.<br>
 * This is a return type of the method
 * {@link DocService#getAnnotationSet(String, String, boolean)}.<br>
 */
public class AnnotationSetHandle {
	/**
	 * Currently contans annotation set data in GATE XML format.
	 */
	private byte[] data;

	/**
	 * This task ID is required to perform modification of annotation set.
	 */
	private String taskID;

	public AnnotationSetHandle() {
	}

	public AnnotationSetHandle(byte[] data, String taskID) {
		this.data = data;
		this.taskID = taskID;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public String getTaskID() {
		return taskID;
	}

	public void setTaskID(String taskID) {
		this.taskID = taskID;
	}

	/*
	 * Class cann't be made immutable, so this method don't needed for now. May be
	 * later The class gleam.docservice.AnnotationSetHandle must contain a default
	 * constructor, which is a requirement for a be an class. The class cannot be
	 * converted into an xml schema type. An xml schema anyType will be used to
	 * define this class in the wsdl file.
	 * 
	 * public boolean equals(Object o) { if (this == o) return true; if (o
	 * instanceof AnnotationSetHandle) { return ((AnnotationSetHandle)
	 * o).getTaskID().equals(this.taskID) && ((AnnotationSetHandle)
	 * o).getData().equals(this.data); } else { return false; } }
	 */
//
	
	public String toString() {
		return "AnnotationSetHandle: TaskID='" + this.taskID + "' data=" + this.data;
	}

}
