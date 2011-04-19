/*
 *  UploadInfo.java
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
