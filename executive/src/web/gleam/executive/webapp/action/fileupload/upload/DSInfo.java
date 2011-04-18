package gleam.executive.webapp.action.fileupload.upload;

public class DSInfo {
	private long totalDocs = 0;

	private long docsAdded = 0;

	private long elapsedTime = 0;

	private String status = "";

	private int fileIndex = 0;

	public DSInfo() {
	}

	public DSInfo(int fileIndex, long totalDocs, long docsAdded,
			long elapsedTime, String status) {
		this.fileIndex = fileIndex;
		this.totalDocs = totalDocs;
		this.docsAdded = docsAdded;
		this.elapsedTime = elapsedTime;
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public long getElapsedTime() {
		return elapsedTime;
	}

	public void setElapsedTime(long elapsedTime) {
		this.elapsedTime = elapsedTime;
	}

	public boolean isInProgress() {
		return "progress".equals(status) || "start".equals(status);
	}

	public int getFileIndex() {
		return fileIndex;
	}

	public void setFileIndex(int fileIndex) {
		this.fileIndex = fileIndex;
	}

	public String toString() {
		return "[DSInfo]\n" + " totalDocs= " + totalDocs + "\n"
				+ " docsAdded= " + docsAdded + "\n" + " elapsedTime= "
				+ elapsedTime + "\n" + " status= '" + status + "'\n"
				+ " fileIndex= " + fileIndex + "\n" + "[/ UploadInfo]\n";
	}

	public long getDocsAdded() {
		return docsAdded;
	}

	public void setDocsAdded(long docsAdded) {
		this.docsAdded = docsAdded;
	}

	public long getTotalDocs() {
		return totalDocs;
	}

	public void setTotalDocs(long totalDocs) {
		this.totalDocs = totalDocs;
	}
}
