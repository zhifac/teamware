/*
 * This file is part of GLEAM, licenced under the GNU Lesser General Public
 * Licence.
 *
 * (c) 2006 The University of Sheffield
 * 
 * $Id$
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
