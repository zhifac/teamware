<%@ include file="/common/taglibs.jsp"%>
<head>
<title><fmt:message key="processInstance.create.title"/></title>
<content tag="heading"><fmt:message key="processInstance.create.heading"/></content>
<meta name="menu" content="ProjectMenu"/>
</head>
<html:form action="loadProject.html" method="post" styleId="projectLoad">
<input type="hidden" name="corpusId" value="<c:out value="${param.corpusId}"/>"/>

<ul>

<li class=info">
<p><fmt:message key="processInstance.createFromProject"/></p>

</li>    
    
    
<li>
        <executive:label styleClass="desc" key="processInstance.project"/>
         <html:select property="projectId" styleClass="wfSelect">
                    <html:options collection="projectList"
                        property="id" labelProperty="name"/>
                </html:select>
         
    </li>


    <li class="buttonBar bottom">
        <p></p>
        <html:submit styleClass="button" property="method.load" onmouseover="ajax_showTooltip('ajaxtooltip/info/selectProjectBtn.jsp',this);return false" onmouseout="ajax_hideTooltip()" >
            <fmt:message key="button.select"/>
        </html:submit>
        
      <html:cancel styleClass="button" onclick="bCancel=true">
            <fmt:message key="button.cancel"/>
        </html:cancel>

    </li>
</ul>
</html:form>

<script type="text/javascript">
    Form.focusFirstElement($("processInstanceForm"));
</script>

<html:javascript formName="processInstanceForm" cdata="false" dynamicJavascript="true" staticJavascript="false"/>
<script type="text/javascript" src="<html:rewrite page="/scripts/validator.jsp"/>"></script>
