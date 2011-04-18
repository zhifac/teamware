/*
 *  AnnotationSetHandle.java
 *
 *  Copyright (c) 2006-2006, The University of Sheffield.
 *
 *  Andrey Shafirin, 11/May/2006
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
