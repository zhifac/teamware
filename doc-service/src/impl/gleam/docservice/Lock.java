/*
 *  Lock.java
 *
 *  Copyright (c) 2006-2006, The University of Sheffield.
 *
 *  Andrey Shafirin, 01/Jun/2006
 *
 *  $Id$
 */

package gleam.docservice;

import java.util.Date;

public class Lock {
	private static int counter = 0;

	private String annSetName, docId;

	private int taskId;

	private long time;

	protected Lock(String annSetName, String docId) {
		this.annSetName = annSetName;
		this.docId = docId;
		this.taskId = counter++;
		this.time = System.currentTimeMillis();
	}

	protected Lock(int taskId, String annSetName, String docId, long time) {
		this.annSetName = annSetName;
		this.docId = docId;
		this.taskId = taskId;
		counter = Math.max(counter, taskId); // some protection
		this.time = time;
	}

	public String getAnnSetName() {
		return annSetName;
	}

	public String getDocId() {
		return docId;
	}

	public int getTaskId() {
		return taskId;
	}

	public long getTime() {
		return time;
	}

	public boolean equals(Object o) {
		if (!(o instanceof Lock)) return false;
		Lock l = (Lock) o;
		// return this.taskId == l.taskId && this.annSetName.equals(l.annSetName) &&
		// this.docId.equals(l.docId);
		return ((this.annSetName == null && l.annSetName == null) || this.annSetName.equals(l.annSetName))
				&& this.docId.equals(l.docId);
	}

	public int hashCode() {
		return ((annSetName == null) ? 0 : annSetName.hashCode()) ^ docId.hashCode();
	}

	public String toString() {
		return "Lock: taskId=" + this.taskId + " annSetName=" + this.annSetName + " docId=" + this.docId + " time=" + time
				+ " [" + new Date(time).toString() + "]";
	}
}
