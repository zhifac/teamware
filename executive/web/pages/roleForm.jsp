<%@ include file="/common/taglibs.jsp"%>
<head>
<title><fmt:message key="role.title"/></title>
<content tag="heading"><fmt:message key="role.heading"/></content>
<meta name="menu" content="AdminMenu"/>
</head>
<fmt:message key="role.message"/>
<html:form action="saveRole" method="post" styleId="roleForm" onsubmit="return validateRoleForm(this)">
<input type="hidden" name="oldRoleName" value="<c:out value="${param.name}"/>"/>
<ul>

    <li>
        <executive:label styleClass="desc" key="roleForm.name"/>
        <html:errors property="name"/>
        <html:text property="name" styleId="name" styleClass="text medium"/>
    </li>
    <li>
        <executive:label styleClass="desc" key="roleForm.description"/>
        <html:errors property="description"/>
        <html:text property="description" styleId="description" styleClass="text medium"/>
    </li>
      
	  <html:hidden property="id"/>
    <li class="buttonBar bottom">
        <html:submit styleClass="button" property="method.save" onclick="bCancel=false" onmouseover="ajax_showTooltip('ajaxtooltip/info/addRoleBtn.jsp',this);return false" onmouseout="ajax_hideTooltip()" >
            <fmt:message key="button.save"/>
        </html:submit>

        <html:cancel styleClass="button" onclick="bCancel=true" onmouseover="ajax_showTooltip('ajaxtooltip/info/cancelBtn.jsp',this);return false" onmouseout="ajax_hideTooltip()" >
            <fmt:message key="button.cancel"/>
        </html:cancel>
    </li>
</ul>
</html:form>

<script type="text/javascript">
    Form.focusFirstElement($("roleForm"));
</script>

<html:javascript formName="roleForm" cdata="false" dynamicJavascript="true" staticJavascript="false"/>
<script type="text/javascript" src="<html:rewrite page="/scripts/validator.jsp"/>"></script>
