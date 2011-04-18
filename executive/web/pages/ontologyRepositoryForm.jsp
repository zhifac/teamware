<%@ include file="/common/taglibs.jsp"%>
<head></head>
<c:if test="${param.method=='edit'}">
<title><fmt:message key="ontologyRepositoryForm.updateTitle"/></title>
<content tag="heading"><fmt:message key="ontologyRepositoryForm.updateHeading"/></content>
<fmt:message key="ontologyRepositoryForm.updateMessage"/>
</c:if>
<c:if test="${param.method=='Add'}">
<title><fmt:message key="ontologyRepositoryForm.title"/></title>
<content tag="heading"><fmt:message key="ontologyRepositoryForm.heading"/></content>
<fmt:message key="ontologyRepositoryForm.message"/>
</c:if>
<meta name="menu" content="ResourceMenu"/>
</head>
<html:form action="saveOntologyRepository" method="post" styleId="ontologyRepositoryForm" onsubmit="return validateOntologyRepositoryForm(this)">
<ul>
    <li>
        <executive:label styleClass="desc" key="ontologyRepositoryForm.name"/>
        <html:errors property="name"/>
        <html:text property="name" styleId="name" styleClass="text medium"/>
    </li>
    <li>
        <executive:label styleClass="desc" key="ontologyRepositoryForm.ontologyURL"/>
        <html:errors property="ontologyURL"/>
        <html:text property="ontologyURL" styleId="ontologyURL" styleClass="text large"/>
    </li>
  
  <c:if test="${(param.method=='edit')||method!=null}">
    <li>
     <input type="hidden" name="name" value="<c:out value="${param.name}"/>">
     <input type="radio" name="editMode" value="merge" checked="true"><fmt:message key="label.add"/><br>
     <input type="radio" name="editMode" value="replace"><fmt:message key="label.replace"/>
    </li>
  </c:if>

    <li class="buttonBar bottom">
        <html:submit styleClass="button" property="method.save" onclick="bCancel=false" onmouseover="ajax_showTooltip('ajaxtooltip/info/addOntologyBtn.jsp',this);return false" onmouseout="ajax_hideTooltip()" >
            <fmt:message key="button.save"/>
        </html:submit>

        <html:cancel styleClass="button" onclick="bCancel=true" onmouseover="ajax_showTooltip('ajaxtooltip/info/cancelBtn.jsp',this);return false" onmouseout="ajax_hideTooltip()" >
            <fmt:message key="button.cancel"/>
        </html:cancel>
    </li>
</ul>
</html:form>

<script type="text/javascript">
    Form.focusFirstElement($("ontologyRepositoryForm"));
</script>

<html:javascript formName="ontologyRepositoryForm" cdata="false" dynamicJavascript="true" staticJavascript="false"/>
<script type="text/javascript" src="<html:rewrite page="/scripts/validator.jsp"/>"></script>
