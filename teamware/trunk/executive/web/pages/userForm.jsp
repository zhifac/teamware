<%@ include file="/common/taglibs.jsp"%>

<head>
    <title><fmt:message key="userProfile.title"/></title>
    <content tag="heading"><fmt:message key="userProfile.heading"/></content>
    
    <% 
    if(request.getParameter("from") == null){%>
    <meta name="menu" content="MainMenu"/>
    <% } else { %>
    <meta name="menu" content="AdminMenu"/>
    <% } %>
</head>

  <c:if test="${param.from=='list'}">
    <div>
     <input type="button" style="margin-right: 5px"
        onclick="location.href='<html:rewrite forward="viewUsers"/>'"
        value="<fmt:message key="button.return"/>"/>
    </div>
  </c:if>
<html:form action="saveUser" styleId="userForm" onsubmit="return validateUserForm(this)">

<html:hidden property="id"/>
<html:hidden property="version"/>
<html:hidden property="salt"/>
<html:hidden property="iterations"/>
<input type="hidden" name="from" value="<c:out value="${param.from}"/>"/>

<c:if test="${cookieLogin == 'true'}">
    <html:hidden property="password"/>
    <html:hidden property="confirmPassword"/>
</c:if>


<ul>
    <li class="buttonBar right">
        <%-- So the buttons can be used at the bottom of the form --%>
        <c:set var="buttons">
            <html:submit styleClass="button" property="method.save" onclick="bCancel=false">
                <fmt:message key="button.save"/>
            </html:submit>

            <html:cancel styleClass="button" onclick="bCancel=true">
                <fmt:message key="button.cancel"/>
            </html:cancel>
        </c:set>
        <%--
        <c:out value="${buttons}" escapeXml="false"/>
        --%>
    </li>
    <li class="info">
        <c:choose>
            <c:when test="${param.from == 'list'}">
                <p><fmt:message key="userProfile.admin.message"/></p>
            </c:when>
            <c:otherwise>
                <p><fmt:message key="userProfile.message"/></p>
            </c:otherwise>
        </c:choose>
    </li>
    <li>
        <executive:label styleClass="desc" key="userForm.username"/>
        <html:errors property="username"/>
        <html:text styleClass="text large" property="username" styleId="username"/>
    </li>
    <c:if test="${cookieLogin != 'true'}">
    <li>
        <div>
            <div class="left">
                <executive:label styleClass="desc" key="userForm.password"/>
                <html:errors property="password"/>
                <html:password styleClass="text medium" property="password" onchange="passwordChanged(this)"
                    styleId="password" redisplay="true"/>
            </div>
            <div>
                <executive:label styleClass="desc" key="userForm.confirmPassword"/>
                <html:errors property="confirmPassword"/>
                <html:password styleClass="text medium" property="confirmPassword" styleId="confirmPassword" redisplay="true"/>
            </div>
        </div>
    </li>
    </c:if>
    <li>
        <executive:label styleClass="desc" key="userForm.passwordHint"/>
        <html:errors property="passwordHint"/>
        <html:text styleClass="text large" property="passwordHint" styleId="passwordHint"/>
    </li>
    <li>
        <div>
            <div class="left">
                <executive:label styleClass="desc" key="userForm.firstName"/>
                <html:errors property="firstName"/>
                <html:text styleClass="text medium" property="firstName" styleId="firstName" maxlength="50"/>
            </div>
            <div>
                <executive:label styleClass="desc" key="userForm.lastName"/>
                <html:errors property="lastName"/>
                <html:text styleClass="text medium" property="lastName" styleId="lastName" maxlength="50"/>
            </div>
        </div>
    </li>
    <li>
        <div>
            <div class="left">
                <executive:label styleClass="desc" key="userForm.email"/>
                <html:errors property="email"/>
                <html:text styleClass="text medium" property="email" styleId="email"/>
            </div>
            <div>
                <executive:label styleClass="desc" key="userForm.phoneNumber"/>
                <html:errors property="phoneNumber"/>
                <html:text styleClass="text medium" property="phoneNumber" styleId="phoneNumber"/>
            </div>
        </div>
    </li>
    <li>
        <executive:label styleClass="desc" key="userForm.website"/>
        <html:errors property="website"/>
        <html:text styleClass="text large" property="website" styleId="website"/>
    </li>
    <li>
        <label class="desc"><fmt:message key="userForm.addressForm.address"/></label>
        <div class="group">
            <div>
                <html:errors property="addressForm.address"/>
                <p><executive:label key="userForm.addressForm.address"/></p>
                <html:text styleClass="text large" property="addressForm.address"
                    styleId="addressForm.address"/>
                
            </div>
            <div class="left">
            <html:errors property="addressForm.city"/>
                <p><executive:label key="userForm.addressForm.city"/></p>
                <html:text styleClass="text medium" property="addressForm.city"
                    styleId="addressForm.city"/>
                
            </div>
            <div>
            <html:errors property="addressForm.province"/>
                <p><executive:label key="userForm.addressForm.province"/></p>
                <html:text styleClass="text small" property="addressForm.province"
                    styleId="addressForm.province" size="2"/>
                
            </div>
            <div class="left">
             <html:errors property="addressForm.postalCode"/>
                <p><executive:label key="userForm.addressForm.postalCode"/></p>
                <html:text styleClass="text medium" property="addressForm.postalCode"
                    styleId="addressForm.postalCode"/>
               
            </div>
            <div>
            <html:errors property="addressForm.country"/>
                <p><executive:label key="userForm.addressForm.country"/></p>
                <executive:country name="countries" toScope="page"/>
                <html:select property="addressForm.country" styleClass="select">
                    <html:option value=""/>
                    <html:options collection="countries"
                        property="value" labelProperty="label"/>
                </html:select>
                
            </div>
        </div>
    </li>
<c:choose>
    <c:when test="${param.from == 'list' or param.method == 'Add'}">
    <li>
        <fieldset>
            <legend><fmt:message key="userProfile.accountSettings"/></legend>
            <html:checkbox styleClass="checkbox" property="enabled" styleId="enabled"/>
            <label for="enabled" class="choice"><fmt:message key="userForm.enabled"/></label>
           </fieldset>
    </li>
    <li>
        <fieldset>
            <legend><fmt:message key="userProfile.assignRoles"/></legend>
            <c:forEach var="role" items="${availableRoles}">
                <html-el:multibox styleClass="checkbox" property="userRoles" styleId="${role.label}">
                    <c:out value="${role.value}"/>
                </html-el:multibox>
                <label class="choice" for="<c:out value="${role.label}"/>">
                    <c:out value="${role.label}"/>
                </label>
            </c:forEach>
        </fieldset>
    </li>
    </c:when>
    <c:when test="${not empty userForm.username}">
    <li>
        <strong><fmt:message key="userForm.roles"/>:</strong>

        <c:forEach var="role" items="${userForm.roles}" varStatus="status">
            <c:out value="${role.name}"/><c:if test="${!status.last}">,</c:if>
            <input type="hidden" name="userRoles" value="<c:out value="${role.name}"/>"/>
        </c:forEach>
        <html:hidden property="enabled"/>
        <html:hidden property="accountExpired"/>
        <html:hidden property="accountLocked"/>
        <html:hidden property="credentialsExpired"/>
    </li>
    </c:when>
</c:choose>
    <li class="buttonBar bottom">
        <c:out value="${buttons}" escapeXml="false"/>
    </li>
</ul>
</html:form>

<script type="text/javascript">
    Form.focusFirstElement($('userForm'));
    highlightFormElements();

    function passwordChanged(passwordField) {
        var origPassword = "<c:out value="${userForm.password}"/>";
        if (passwordField.value != origPassword) {
            createFormElement("input", "hidden",
                              "encryptPass", "encryptPass",
                              "true", passwordField.form);
        }
    }
</script>

<html:javascript formName="userForm" cdata="false" dynamicJavascript="true" staticJavascript="false"/>
<script type="text/javascript" src="<c:url value="/scripts/validator.jsp"/>"></script>

