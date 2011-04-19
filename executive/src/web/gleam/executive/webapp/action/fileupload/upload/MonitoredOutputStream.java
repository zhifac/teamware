/*
 *  MonitoredOutputStream.java
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

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by IntelliJ IDEA.
 * 
 * @author Original : plosson on 05-janv.-2006 10:44:18 - Last modified
 *         by $Author: helge $ on $Date: 2006/08/07 03:29:54 $
 * @version 1.0 - Rev. $Revision: 1.2 $
 */
public class MonitoredOutputStream extends OutputStream {
  protected transient Log log = LogFactory.getLog(this.getClass());

  private OutputStream target;

  private OutputStreamListener listener;

  public MonitoredOutputStream(OutputStream target,
          OutputStreamListener listener) {
    // log.debug("inside MonitoredOutputStream constructor ");
    this.target = target;
    this.listener = listener;
    this.listener.start();
    // log.debug("leaving MonitoredOutputStream contructor ");
  }

  public void write(byte b[], int off, int len) throws IOException {
    target.write(b, off, len);
    listener.bytesRead(len - off);
  }

  public void write(byte b[]) throws IOException {
    target.write(b);
    listener.bytesRead(b.length);
  }

  public void write(int b) throws IOException {
    target.write(b);
    listener.bytesRead(1);
  }

  public void close() throws IOException {
    target.close();
    listener.done();
  }

  public void flush() throws IOException {
    target.flush();
  }
}
