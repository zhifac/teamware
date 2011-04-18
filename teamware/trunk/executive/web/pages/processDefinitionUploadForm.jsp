<%@ include file="/common/taglibs.jsp"%>

<head>
<title><fmt:message key="workflow.processDefinitionUpload.title"/></title>
<content tag="heading"><fmt:message key="workflow.processDefinitionUpload.heading"/></content>
<meta name="menu" content="ProjectMenu"/>
</head>
<!--
    The most important part is to declare your form's enctype to be "multipart/form-data",
    and to have a form:file element that maps to your ActionForm's FormFile property
-->

<%--  The dynamicJavascript and staticJavascript attributes default to true, but if dynamicJavascript is set to true  and staticJavascript is set to false then only the dynamic JavaScript will be rendered. If dynamicJavascript is set to false  and staticJavascript is set to true then only the static JavaScript will be rendered which can then be put in separate JSP page so the browser can cache the static JavaScript. From: http://struts.apache.org/userGuide/struts-html.html#javascript --%>
<html:javascript formName="processDefinitionUploadForm" cdata="false"
                 dynamicJavascript="true" staticJavascript="false"/>
<script type="text/javascript" src="<html:rewrite page="/scripts/validator.jsp"/>"></script>

<html:form action="processDefinitionUpload" method="post" styleId="processDefinitionUploadForm"
    enctype="multipart/form-data" onsubmit="return validateProcessDefinitionUploadForm(this)">
<ul>
    <li class="info">
        <fmt:message key="workflow.processDefinitionUpload.message"/>
    </li>

    <li>
        <executive:label key="processDefinitionUploadForm.file" styleClass="desc"/>
	  <html:errors property="file"/>
        <html:file property="file" styleClass="file large" styleId="file" />
    </li>
    <li class="buttonBar">
        <html:submit styleClass="button" onclick="bCancel=false" onmouseover="ajax_showTooltip('ajaxtooltip/info/uploadProcessBtn.jsp',this);return false" onmouseout="ajax_hideTooltip()">
            <fmt:message key="button.upload"/>
        </html:submit>
        <input type="button" onclick="location.href='processDefinitionList.html?method=list'" value="<fmt:message key="button.cancel"/>" 
        	onmouseover="ajax_showTooltip('ajaxtooltip/info/cancelBtn.jsp',this);return false" onmouseout="ajax_hideTooltip()"
        />
    </li>
</ul>
</html:form>

