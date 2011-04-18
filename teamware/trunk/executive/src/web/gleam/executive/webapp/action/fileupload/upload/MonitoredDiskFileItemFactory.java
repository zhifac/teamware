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
