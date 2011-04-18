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
 * @author Original : plosson on 04-janv.-2006 9:59:27 - Last modified
 *         by $Author: helge $ on $Date: 2006/08/01 02:06:36 $
 * @version 1.0 - Rev. $Revision: 1.1 $
 */
public interface OutputStreamListener {
  public void start();

  public void bytesRead(int bytesRead);

  public void error(String message);

  public void done();
}
