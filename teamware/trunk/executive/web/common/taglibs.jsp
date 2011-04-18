<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>

<%@ taglib uri="http://acegisecurity.org/authz" prefix="authz" %>

<%-- Taglibs required by Struts 1.2.9, which can be found in the directory META-INFO/tlds/ in struts.jar --%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-bean-el" prefix="bean-el" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-logic-el" prefix="logic-el" %>
<%@ taglib uri="http://struts.apache.org/tags-nested" prefix="nested" %>

<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>

<%@ taglib uri="http://struts-menu.sf.net/tag-el" prefix="menu" %>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jstl/sql" prefix="sql" %>

<%@ taglib uri="http://www.opensymphony.com/oscache" prefix="cache" %>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator"%>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/page" prefix="page"%>

<%@ taglib uri="/WEB-INF/executive.tld" prefix="executive" %>

<%-- Set all pages that include this page to use XHTML --%>
<%-- Using this Struts html tag in a page tells all other html taglib tags to render themselves as XHTML 1.0. 
  This is useful when composing pages with JSP includes or Tiles. 
  <html:html xhtml="true"> has a similar effect. 
  This tag has no attributes; you use it like this: <html:xhtml/>.
  commentted by Haotian Sun.
--%>
<html:xhtml />
