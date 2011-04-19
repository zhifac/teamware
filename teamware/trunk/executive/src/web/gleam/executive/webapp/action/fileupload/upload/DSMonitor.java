/*
 *  DSMonitor.java
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
import uk.ltd.getahead.dwr.WebContextFactory;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Original : plosson on 06-janv.-2006 12:19:08 - Last modified
 *         by $Author: helge $ on $Date: 2006/08/01 02:06:36 $
 * @version 1.0 - Rev. $Revision: 1.1 $
 */
public class DSMonitor {
  protected transient Log log = LogFactory.getLog(this.getClass());

  public DSInfo getDSInfo() {
    log.debug("inside getDSInfo() ");
    HttpServletRequest req = WebContextFactory.get().getHttpServletRequest();
    log.debug("[getDSInfo] - got the req ");
    if(req.getSession().getAttribute("dsInfo") != null) {
      log.debug("[getDSInfo] - return dsInfo from session ");
      return (DSInfo)req.getSession().getAttribute("dsInfo");
    }
    else {
      log.debug("[getDSInfo] - return dsInfo ");
      return new DSInfo();
    }
  }
}

