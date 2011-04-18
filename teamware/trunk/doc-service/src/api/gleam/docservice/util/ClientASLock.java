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
