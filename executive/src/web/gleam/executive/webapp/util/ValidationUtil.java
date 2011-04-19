/*
 *  ValidationUtil.java
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
package gleam.executive.webapp.util;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.validator.Field;
import org.apache.commons.validator.GenericValidator;
import org.apache.commons.validator.ValidatorAction;
import org.apache.commons.validator.util.ValidatorUtils;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.validator.Resources;

/**
 * ValidationUtil Helper class for performing custom validations that
 * aren't already included in the core Struts Validator.
 * 
 * <p>
 * <a href="ValidationUtil.java.html"><i>View Source</i></a>
 * </p>
 * 
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 * @version $Revision: 1.4 $ $Date: 2004-05-15 20:18:10 -0600 (Sat, 15
 *          May 2004) $
 */
public class ValidationUtil {
  // ~ Methods
  // ================================================================

  /**
   * Validates that two fields match.
   * 
   * @param bean
   * @param va
   * @param field
   * @param errors
   * @param request
   * @return boolean
   */
  public static boolean validateTwoFields(Object bean, ValidatorAction va,
          Field field, ActionMessages errors, HttpServletRequest request) {
    String value = ValidatorUtils.getValueAsString(bean, field.getProperty());
    String sProperty2 = field.getVarValue("secondProperty");
    String value2 = ValidatorUtils.getValueAsString(bean, sProperty2);

    if(!GenericValidator.isBlankOrNull(value)) {
      try {
        if(!value.equals(value2)) {
          errors.add(field.getKey(), Resources.getActionMessage(request, va,
                  field));

          return false;
        }
      }
      catch(Exception e) {
        errors.add(field.getKey(), Resources.getActionMessage(request, va,
                field));

        return false;
      }
    }

    return true;
  }
}
