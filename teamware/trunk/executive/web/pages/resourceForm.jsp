<%@ include file="/common/taglibs.jsp"%>
<head>
<title><fmt:message key="resource.title"/></title>
<content tag="heading"><fmt:message key="resource.heading"/></content>
<meta name="menu" content="AdminMenu"/>
</head>
<fmt:message key="resource.message"/>
<html:form action="saveResource" method="post" styleId="resourceForm" onsubmit="return validateResourceForm(this)">
<input type="hidden" name="from" value="<c:out value="${param.from}"/>"/>
<html:hidden property="id"/>
<ul>

    <li>
        <executive:label styleClass="desc" key="resourceForm.url"/>
        <html:errors property="url"/>
        <html:text property="url" styleId="url" styleClass="text medium"/>
    </li>
    
    <li>
        <executive:label styleClass="desc" key="resourceForm.description"/>
        <html:errors property="description"/>
        <html:text property="description" styleId="description" styleClass="text medium"/>
    </li>
<c:choose>
    <c:when test="${param.from == 'list' and param.method == 'edit'}">
    <li>
        <fieldset>
            <legend><fmt:message key="resourceForm.assignRoles"/></legend>
            <c:forEach var="role" items="${allRoles}">
                <html-el:multibox styleClass="checkbox" property="resourceRoles" styleId="${role.label}">
                    <c:out value="${role.value}"/>
                </html-el:multibox>
                <label class="choice" for="<c:out value="${role.label}"/>">
                    <c:out value="${role.label}"/>
                </label>
            </c:forEach>
        </fieldset>
    </li>
    <li>
      <fieldset>
        <legend><fmt:message key="serviceForm.assignService"/></legend>
           <html:hidden property="serviceForm.name"/>
           <html:hidden property="serviceForm.enabled"/>
           <html:hidden property="serviceForm.id"/>
           <html-el:select styleClass="select" property="service_id" >
	        <c:forEach var="service" items="${availableServices}">
                <html-el:option value="${service.value}">
	          <label class="option" for="<c:out value="${service.label}"/>">
                  <c:out value="${service.label}"/>
                </label>
                </html-el:option>
              </c:forEach>
           </html-el:select>
      </fieldset>
    </li>
    </c:when>
    <c:when test="${param.from == 'list' and param.method == 'Add'}">
      <li>
        <fieldset>
            <legend><fmt:message key="resourceForm.assignRoles"/></legend>
            <c:forEach var="role" items="${allRoles}">
                <html-el:multibox styleClass="checkbox" property="resourceRoles" styleId="${role.label}">
                    <c:out value="${role.value}"/>
                </html-el:multibox>
                <label class="choice" for="<c:out value="${role.label}"/>">
                    <c:out value="${role.label}"/>
                </label>
            </c:forEach>
        </fieldset>
    </li>  
    <li>
      <fieldset>
        <legend>Assign Service</legend>
           <input type="hidden" name="serviceForm.name">
           <input type="hidden" name="serviceForm.enabled">
           <input type="hidden" name="serviceForm.id">
           <html-el:select styleClass="select" property="service_id" >
	        <c:forEach var="service" items="${availableServices}">
                <html-el:option value="${service.value}">
	          <label class="option" for="<c:out value="${service.label}"/>">
                  <c:out value="${service.label}"/>
                </label>
                </html-el:option>
              </c:forEach>
           </html-el:select>
      </fieldset>
    </li>
    </c:when>
    <c:when test="${not empty resourceForm.id}">
    <li>
        <strong><fmt:message key="resourceForm.assignRoles"/>:</strong>

        <c:forEach var="role" items="${resourceForm.roles}" varStatus="status">
            <c:out value="${role.name}"/><c:if test="${!status.last}">,</c:if>
            <input type="hidden" name="resourceRoles" value="<c:out value="${role.name}"/>"/>
        </c:forEach>
    </li>
    
    </c:when>
</c:choose>
	  
    <li class="buttonBar bottom">
        <html:submit styleClass="button" property="method.save" onclick="bCancel=false" onmouseover="ajax_showTooltip('ajaxtooltip/info/addResourceBtn.jsp',this);return false" onmouseout="ajax_hideTooltip()" >
            <fmt:message key="button.save"/>
        </html:submit>

        <html:cancel styleClass="button" onclick="bCancel=true" onmouseover="ajax_showTooltip('ajaxtooltip/info/cancelBtn.jsp',this);return false" onmouseout="ajax_hideTooltip()" >
            <fmt:message key="button.cancel"/>
        </html:cancel>
    </li>
</ul>
</html:form>

<script type="text/javascript">
    Form.focusFirstElement($("resourceForm"));
</script>

<html:javascript formName="resourceForm" cdata="false" dynamicJavascript="true" staticJavascript="false"/>
<script type="text/javascript" src="<html:rewrite page="/scripts/validator.jsp"/>"></script>
