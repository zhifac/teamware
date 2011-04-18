/* Licence:
 *   Use this however/wherever you like, just don't blame me if it breaks anything.
 *
 * Credit:
 *   If you're nice, you'll leave this bit:
 *
 *   Class by Pierre-Alexandre Losson -- http://www.telio.be/blog
 *   email : plosson@users.sourceforge.net
 */
/*
 *  Changed for Part 2, by Ken Cochrane
 *  http://KenCochrane.net , http://CampRate.com , http://PopcornMonsters.com
 */
package gleam.executive.webapp.action.fileupload.upload;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Original : plosson on 06-janv.-2006 12:19:14 - Last modified
 *         by $Author: helge $ on $Date: 2006/08/01 02:06:36 $
 * @version 1.0 - Rev. $Revision: 1.1 $
 */
public class UploadInfo {
  private long totalSize = 0;

  private long bytesRead = 0;

  private long elapsedTime = 0;

  private String status = "";

  private int fileIndex = 0;

  public UploadInfo() {
  }

  public UploadInfo(int fileIndex, long totalSize, long bytesRead,
          long elapsedTime, String status) {
    this.fileIndex = fileIndex;
    this.totalSize = totalSize;
    this.bytesRead = bytesRead;
    this.elapsedTime = elapsedTime;
    this.status = status;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public long getTotalSize() {
    return totalSize;
  }

  public void setTotalSize(long totalSize) {
    this.totalSize = totalSize;
  }

  public long getBytesRead() {
    return bytesRead;
  }

  public void setBytesRead(long bytesRead) {
    this.bytesRead = bytesRead;
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
    return "[UploadInfo]\n" + " totalSize= " + totalSize + "\n"
            + " bytesRead= " + bytesRead + "\n" + " elapsedTime= "
            + elapsedTime + "\n" + " status= '" + status + "'\n"
            + " fileIndex= " + fileIndex + "\n" + "[/ UploadInfo]\n";
  }
}
