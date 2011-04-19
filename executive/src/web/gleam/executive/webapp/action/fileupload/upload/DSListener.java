/*
 *  DSListener.java
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
public class DSListener {

  protected transient Log log = LogFactory.getLog(this.getClass());

  private HttpServletRequest request;

  private long delay = 0;

  private long startTime = 0;

  private int totalDocs = 0;

  private int docsAdded = 0;

  private int totalFiles = -1;

  private int logCounter = 0;

  private String corpusId = null;

  public static final int RESET_LOGCOUNTER_COUNT = 100;

  public DSListener(HttpServletRequest request,
		            long debugDelay) {
	log.debug("constructed DSListener ");
	this.request = request;
    this.delay = debugDelay;
    this.totalDocs = (Integer) request.getAttribute("totalDocs");
    log.debug("totalDocs "+totalDocs);
    this.corpusId = request.getParameter("corpusID");
    log.debug("corpusId "+corpusId);
    //this.docServiceManager=docServiceManager;
    this.startTime = System.currentTimeMillis();
  }

  public void start() {
    totalFiles++;
    log.debug("started DSListener totalFiles "+totalFiles);
    updateDSInfo("start");
  }

  public void addDoc() throws Exception{
	//List documentList = docServiceManager.listDocuments(corpusId);
	//docsAdded  =  documentList.size();
	 docsAdded ++;
	log.debug("addDoc docsAdded "+docsAdded);
    updateDSInfo("progress");

    try {
      Thread.sleep(delay);
    }
    catch(InterruptedException e) {
      e.printStackTrace();
    }
  }

  public void error(String message) {
	  updateDSInfo("error");
  }

  public void done() {
	  updateDSInfo("done");
  }

  private long getDelta() {
    return (System.currentTimeMillis() - startTime) / 1000;
  }

  private void updateDSInfo(String status) {
    logDebug("inside updateDSInfo ");
    long delta = getDelta();
    logDebug("updateDSInfo delta =  " + delta);
    request.getSession().setAttribute(
            "dsInfo",
            new DSInfo(totalFiles, totalDocs, docsAdded, delta,
                    status));
    logDebug("leaving updateDSInfo ");
    // Manage logcounter
    if(logCounter >= RESET_LOGCOUNTER_COUNT) {
      logCounter = 0;
    }
    else {
      logCounter++;
    }
  }

  private void logDebug(String message) {
    //if(logCounter == RESET_LOGCOUNTER_COUNT) {
      if(log.isDebugEnabled()) {
        log.debug(message);
      }
    //}
      /*
       * else { if(log.isTraceEnabled()) { log.trace("message skipped:
       * "+message); } }
       */
  }

}
