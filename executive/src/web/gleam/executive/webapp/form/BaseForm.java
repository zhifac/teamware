/*
 *  BaseForm.java
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
package gleam.executive.webapp.form;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.MessageResources;
import org.apache.struts.validator.ValidatorForm;

/**
 * Base ActionForm bean. Used to give child classes readable
 * representation of their properties using toString() method.
 * </p>
 * 
 * <p>
 * Also has a validate() method to cancel validation on cancel actions.
 * </p>
 * 
 * <p>
 * <a href="BaseForm.java.html"><i>View Source</i></a>
 * </p>
 * 
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 * @version $Revision: 1.7 $ $Date: 2005-04-11 15:09:49 -0600 (Mon, 11
 *          Apr 2005) $
 */
public class BaseForm extends ValidatorForm implements Serializable {
  private static final long serialVersionUID = 3257005453799404851L;

  public String toString() {
    return ToStringBuilder.reflectionToString(this,
            ToStringStyle.MULTI_LINE_STYLE);
  }

  public boolean equals(Object o) {
    return EqualsBuilder.reflectionEquals(this, o);
  }

  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }

  /**
   * This validation method is designed to be a parent of all other
   * Form's validate methods - this allows the cancel and delete buttons
   * to bypass validation.
   * 
   * @param mapping The <code>ActionMapping</code> used to select this
   *          instance
   * @param request The servlet request we are processing
   * @return <code>ActionErrors</code> object that encapsulates any
   *         validation errors
   */
  public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
    // Identify the request parameter containing the method name
    String parameter = mapping.getParameter();

    if(parameter != null) {
      // Identify the method name to be dispatched to.
      String method = request.getParameter(parameter);
      MessageResources resources = (MessageResources)request
              .getAttribute(Globals.MESSAGES_KEY);

      // Identify the localized message for the cancel button
      String cancel = resources.getMessage("button.cancel");
      String delete = resources.getMessage("button.delete");

      // if message resource matches the cancel button then no
      // need to validate
      if((method != null)
              && (method.equalsIgnoreCase(cancel) || method
                      .equalsIgnoreCase(delete))) {
        return null;
      }
    }

    // perform regular validation
    return super.validate(mapping, request);
  }
}
