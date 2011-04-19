/*
 *  MonitoredDiskFileItem.java
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

import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by IntelliJ IDEA.
 * 
 * @author Original : plosson on 05-janv.-2006 10:46:33 - Last modified
 *         by $Author: helge $ on $Date: 2006/08/07 03:29:54 $
 * @version 1.0 - Rev. $Revision: 1.2 $
 */
public class MonitoredDiskFileItem extends DiskFileItem {

  private static final long serialVersionUID = 7008629320881116411L;

  protected transient Log log = LogFactory.getLog(this.getClass());

  private MonitoredOutputStream mos = null;

  private OutputStreamListener listener;

  public MonitoredDiskFileItem(String fieldName, String contentType,
          boolean isFormField, String fileName, int sizeThreshold,
          File repository, OutputStreamListener listener) {
    super(fieldName, contentType, isFormField, fileName, sizeThreshold,
            repository);
    log.debug("inside MonitoredDiskFileItem constructor ");
    this.listener = listener;
  }

  public OutputStream getOutputStream() throws IOException {
    // log.debug("inside getOutputStream() ");
    if(mos == null) {
      mos = new MonitoredOutputStream(super.getOutputStream(), listener);
    }
    // log.debug("leaving getOutputStream() ");
    return mos;
  }
}
