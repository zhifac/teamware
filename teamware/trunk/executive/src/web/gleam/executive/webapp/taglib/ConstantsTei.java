/*
 *  ConstantsTei.java
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
package gleam.executive.webapp.taglib;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.VariableInfo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import gleam.executive.Constants;

/**
 * Implementation of <code>TagExtraInfo</code> for the <b>constants</b>
 * tag, identifying the scripting object(s) to be made visible.
 * 
 * @author Matt Raible
 * @version $Revision: 1.5 $ $Date: 2006-01-29 22:00:13 -0700 (Sun, 29
 *          Jan 2006) $
 */
public class ConstantsTei extends TagExtraInfo {
  private final Log log = LogFactory.getLog(ConstantsTei.class);

  /**
   * Return information about the scripting variables to be created.
   */
  public VariableInfo[] getVariableInfo(TagData data) {
    // loop through and expose all attributes
    List<VariableInfo> vars = new ArrayList<VariableInfo>();

    try {
      String clazz = data.getAttributeString("className");

      if(clazz == null) {
        clazz = Constants.class.getName();
      }

      Class c = Class.forName(clazz);

      // if no var specified, get all
      if(data.getAttributeString("var") == null) {
        Field[] fields = c.getDeclaredFields();

        AccessibleObject.setAccessible(fields, true);

        for(int i = 0; i < fields.length; i++) {
          String type = fields[i].getType().getName();
          vars.add(new VariableInfo(fields[i].getName(), ((fields[i].getType()
                  .isArray())
                  ? type.substring(2, type.length() - 1) + "[]"
                  : type), true, VariableInfo.AT_END));
        }
      }
      else {
        String var = data.getAttributeString("var");
        String type = c.getField(var).getType().getName();
        vars.add(new VariableInfo(c.getField(var).getName(), ((c.getField(var)
                .getType().isArray()) ? type.substring(2, type.length() - 1)
                + "[]" : type), true, VariableInfo.AT_END));
      }
    }
    catch(Exception cnf) {
      log.error(cnf.getMessage());
      cnf.printStackTrace();
    }

    return (VariableInfo[])vars.toArray(new VariableInfo[] {});
  }
}
