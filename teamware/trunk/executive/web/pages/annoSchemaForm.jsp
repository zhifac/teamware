<%@ include file="/common/taglibs.jsp"%>
<head>
<title><fmt:message key="annoSchemaForm.title"/></title>
<content tag="heading"><fmt:message key="annoSchemaForm.heading"/></content>
<meta name="menu" content="ResourceMenu"/>

<script type="text/javascript">
  function checkSC(form){
    var popup = document.getElementById("popup").value;
    if(popup=="true"){
     form.action="popupSaveSchema.html";
    }
  }
</script>
</head>
<html:form action="saveSchema" method="post" styleId="annoSchemaForm"
    enctype="multipart/form-data" onsubmit="checkSC(this); return validateAnnoSchemaForm(this)">
     <input type="hidden" name="popup" value="<c:out value="${param.popup}"/>"/>
 
<ul>
    <li class="info">
        <fmt:message key="annoSchemaForm.message"/>
    </li>
    <li>
        <executive:label key="annoSchemaForm.file" styleClass="desc"/>
        <html:file property="file" styleClass="file large" styleId="file" size="80"/>
    </li>
    <li>
        <executive:label key="annoSchemaForm.optionalFile" styleClass="desc"/>
        <html:file property="file1" styleClass="file large" styleId="file1" size="80"/>
    </li>
    <li>
        <executive:label key="annoSchemaForm.optionalFile" styleClass="desc"/>
        <html:file property="file2" styleClass="file large" styleId="file2" size="80"/>
    </li>
    <li>
        <executive:label key="annoSchemaForm.optionalFile" styleClass="desc"/>
        <html:file property="file3" styleClass="file large" styleId="file3" size="80"/>
    </li>
    <li>
        <executive:label key="annoSchemaForm.optionalFile" styleClass="desc"/>
        <html:file property="file4" styleClass="file large" styleId="file4" size="80"/>
    </li>
    <li class="buttonBar">
        <html:submit styleClass="button" onclick="bCancel=false">
            <fmt:message key="button.upload"/>
        </html:submit>
        <c:if test="${param.popup!='true'}">
        <html:cancel styleClass="button" onclick="bCancel=true">
            <fmt:message key="button.cancel"/>
        </html:cancel>
        </c:if>
    </li>
</ul>
</html:form>

<script type="text/javascript">
    Form.focusFirstElement($('annoSchemaForm'));
    highlightFormElements();
</script>
<script type="text/javascript" src="<c:url value="/scripts/validator.jsp"/>"></script>
