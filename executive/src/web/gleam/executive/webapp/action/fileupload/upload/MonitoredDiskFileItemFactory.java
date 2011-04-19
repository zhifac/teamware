/*
 *  MonitoredDiskFileItemFactory.java
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

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * 
 * @author Original : plosson on 05-janv.-2006 10:46:26 - Last modified
 *         by $Author: helge $ on $Date: 2006/08/07 03:29:54 $
 * @version 1.0 - Rev. $Revision: 1.2 $
 */
public class MonitoredDiskFileItemFactory extends DiskFileItemFactory {
  protected transient Log log = LogFactory.getLog(this.getClass());

  private OutputStreamListener listener = null;

  public MonitoredDiskFileItemFactory(OutputStreamListener listener) {
    super();
    log.debug("inside MonitoredDiskFileItemFactory constructor (listener) ");
    this.listener = listener;
  }

  public MonitoredDiskFileItemFactory(int sizeThreshold, File repository,
          OutputStreamListener listener) {
    super(sizeThreshold, repository);
    log.debug("inside MonitoredDiskFileItemFactory constructor ");
    this.listener = listener;
  }

  public FileItem createItem(String fieldName, String contentType,
          boolean isFormField, String fileName) {
    // log.debug("inside MonitoredDiskFileItemFactory createItem ");
    return new MonitoredDiskFileItem(fieldName, contentType, isFormField,
            fileName, getSizeThreshold(), getRepository(), listener);
  }
}
