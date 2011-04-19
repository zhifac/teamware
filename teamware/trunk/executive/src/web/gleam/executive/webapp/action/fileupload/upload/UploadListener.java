/*
 *  UploadListener.java
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by IntelliJ IDEA.
 * 
 * @author Original : plosson on 06-janv.-2006 15:05:44 - Last modified
 *         by $Author: helge $ on $Date: 2006/08/01 03:35:43 $
 * @version 1.0 - Rev. $Revision: 1.2 $
 */
public class UploadListener implements OutputStreamListener

{
  protected transient Log log = LogFactory.getLog(this.getClass());

  private HttpServletRequest request;

  private long delay = 0;

  private long startTime = 0;

  private int totalToRead = 0;

  private int totalBytesRead = 0;

  private int totalFiles = -1;

  private int logCounter = 0;

  public static final int RESET_LOGCOUNTER_COUNT = 100;

  public UploadListener(HttpServletRequest request, long debugDelay) {
    this.request = request;
    this.delay = debugDelay;
    totalToRead = request.getContentLength();
    this.startTime = System.currentTimeMillis();
  }

  public void start() {
    totalFiles++;
    updateUploadInfo("start");
  }

  public void bytesRead(int bytesRead) {
    totalBytesRead = totalBytesRead + bytesRead;
    updateUploadInfo("progress");

    try {
      Thread.sleep(delay);
    }
    catch(InterruptedException e) {
      e.printStackTrace();
    }
  }

  public void error(String message) {
    updateUploadInfo("error");
  }

  public void done() {
    updateUploadInfo("done");
  }

  private long getDelta() {
    return (System.currentTimeMillis() - startTime) / 1000;
  }

  private void updateUploadInfo(String status) {
    logDebug("inside updateUploadInfo ");
    long delta = getDelta();
    logDebug("updateUploadInfo delta =  " + delta);
    request.getSession().setAttribute(
            "uploadInfo",
            new UploadInfo(totalFiles, totalToRead, totalBytesRead, delta,
                    status));
    logDebug("leaving updateUploadInfo ");
    // Manage logcounter
    if(logCounter >= RESET_LOGCOUNTER_COUNT) {
      logCounter = 0;
    }
    else {
      logCounter++;
    }
  }

  private void logDebug(String message) {
    if(logCounter == RESET_LOGCOUNTER_COUNT) {
      if(log.isDebugEnabled()) {
        log.debug(message);
      }
    }/*
       * else { if(log.isTraceEnabled()) { log.trace("message skipped:
       * "+message); } }
       */
  }

}
