/*
 *  ExpressionEvaluator.java
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
package gleam.executive.webapp.taglib.struts.menu.el;

import org.apache.taglibs.standard.lang.support.ExpressionEvaluatorManager;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;


/**
 * Utility class to help with the evaluation of JSTL Expression Language.  It
 * mainly encapsulates the calls to ExpressionEvaluationManager to ease the
 * use of this class.
 * 
 */
public class ExpressionEvaluator {
    //~ Instance fields ========================================================

    private PageContext context;
    private Tag tag;

    //~ Constructors ===========================================================

    public ExpressionEvaluator(Tag tag, PageContext context) {
        this.tag = tag;
        this.context = context;
    }

    //~ Methods ================================================================

    /**
     * Evaluate expression in attrValue.
     *
     * @return evaluate expression of attrValue, null if attrValue is null.
     */
    public Object eval(String attrName, String attrValue, Class returnClass)
    throws JspException {
        Object result = null;

        if (attrValue != null) {
            result =
                ExpressionEvaluatorManager.evaluate(attrName, attrValue,
                                                    returnClass, tag, context);
        }

        return result;
    }

    public String evalString(String attrName, String attrValue)
    throws JspException {
        return (String) eval(attrName, attrValue, String.class);
    }

    public boolean evalBoolean(String attrName, String attrValue)
    throws JspException {
        Boolean rtn = (Boolean) eval(attrName, attrValue, Boolean.class);

        if (rtn != null) {
            return rtn.booleanValue();
        } else {
            return false;
        }
    }

    public long evalLong(String attrName, String attrValue)
    throws JspException {
        Long rtn = (Long) eval(attrName, attrValue, Long.class);

        if (rtn != null) {
            return rtn.longValue();
        } else {
            return -1L;
        }
    }

    public int evalInt(String attrName, String attrValue)
    throws JspException {
        Integer rtn = (Integer) eval(attrName, attrValue, Integer.class);

        if (rtn != null) {
            return rtn.intValue();
        } else {
            return -1;
        }
    }
}
