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

