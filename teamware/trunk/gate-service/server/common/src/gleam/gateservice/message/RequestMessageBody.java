/*
 *  RequestMessageBody.java
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
package gleam.gateservice.message;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * This class represents the content of the message passed from the
 * endpoint to the workers, and is intended to be used as the body of a
 * JMS ObjectMessage. Subclasses define the specific behaviour for GATE
 * and GLEAM mode messages.
 */
public class RequestMessageBody implements Serializable {
  static final long serialVersionUID = 1093102835495306063L;
  
  /**
   * Values of the parameters passed to the GaS.
   */
  protected Map<String, Object> parameterValues = new HashMap<String, Object>();

  public Map<String, Object> getParameterValues() {
    return parameterValues;
  }

  public void addParameterValue(String key, Object value) {
    parameterValues.put(key, value);
  }
  
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(this.getClass().getName());
    sb.append(": parameterValues = ");
    sb.append(parameterValues.toString());
    
    return sb.toString();
  }
}
