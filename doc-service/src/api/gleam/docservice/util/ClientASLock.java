/*
 *  ClientASLock.java
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
package gleam.docservice.util;

import gleam.docservice.DocService;

public class ClientASLock {
	private String taskId, annSetName;

	private Thread notifier;
	private final DocService docService;

	public ClientASLock(final String taskId, String annSetName, DocService sds) {
		this.taskId = taskId;
		this.annSetName = annSetName;
		this.docService = sds;
		this.notifier = new Thread() {
			public void run() {
				while (!interrupted()) {
					try {
						sleep(30000);
						docService.keepaliveLock(taskId);
					} catch (InterruptedException e) {
						break;
					}
				}
			}
		};
		notifier.start();
	}

	public void release() {
		notifier.interrupt();
		docService.releaseLock(taskId);
	}

	public String getAnnSetName() {
		return annSetName;
	}

	public String getTaskId() {
		return taskId;
	}

	public boolean equals(Object o) {
		if (!(o instanceof ClientASLock)) return false;
		return ((ClientASLock) o).annSetName.equals(annSetName) && ((ClientASLock) o).taskId.equals(taskId);
	}

	public String toString() {
		return "ClientLock: AnnSet name=" + annSetName + " task ID=" + taskId + " notifier is "
				+ ((notifier.isAlive()) ? "alive" : "dead");
	}
}
