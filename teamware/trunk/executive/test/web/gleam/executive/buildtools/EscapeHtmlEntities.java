/*
 *  EscapeHtmlEntities.java
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
package gleam.executive.buildtools;

import java.io.IOException;
import java.io.Reader;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.tools.ant.filters.BaseParamFilterReader;
import org.apache.tools.ant.filters.ChainableReader;

/**
 * @author Mika
 * 
 */
public class EscapeHtmlEntities extends BaseParamFilterReader implements
                                                             ChainableReader {
  public static final String ESCAPE = "escape";

  public static final String UNESCAPE = "unescape";

  /** Data that must be read from, if not null. */
  private String queuedData = null;

  private String mode;

  public EscapeHtmlEntities() {
    super();
  }

  public EscapeHtmlEntities(final Reader rdr) {
    super(rdr);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.io.FilterReader#read()
   */
  public int read() throws IOException {
    int ch = -1;
    if(queuedData != null && queuedData.length() == 0) {
      queuedData = null;
    }
    if(queuedData != null) {
      ch = queuedData.charAt(0);
      queuedData = queuedData.substring(1);
      if(queuedData.length() == 0) {
        queuedData = null;
      }
    }
    else {
      queuedData = readFully();
      if(queuedData == null) {
        ch = -1;
      }
      else {
        queuedData = handleEntities(queuedData);
        return read();
      }
    }
    return ch;
  }

  /**
   * @param queuedData2
   * @return
   */
  private String handleEntities(String queuedData2) {
    if(ESCAPE.equalsIgnoreCase(mode)) {
      return StringEscapeUtils.escapeHtml(queuedData2);
    }
    else {
      return StringEscapeUtils.unescapeHtml(queuedData2);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.tools.ant.filters.ChainableReader#chain(java.io.Reader)
   */
  public Reader chain(Reader rdr) {
    EscapeHtmlEntities filter = new EscapeHtmlEntities(rdr);
    return filter;
  }

  /**
   * @return Returns the mode.
   */
  public String getMode() {
    return mode;
  }

  /**
   * @param mode The mode to set.
   */
  public void setMode(String mode) {
    this.mode = mode;
  }
}
