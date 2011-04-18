<%@ include file="/common/taglibs.jsp"%>
<head>
<title><fmt:message key="corpusDetail.title"/></title>
<content tag="heading"><fmt:message key="corpusDetail.heading"/></content>
<meta name="menu" content="ResourceMenu"/>
</head>
<fmt:message key="corpusDetail.message"/>
<html:form action="saveCorpus" method="post" styleId="corpusForm" onsubmit="return validateCorpusForm(this)">
<ul>

    <li>
        <executive:label styleClass="desc" key="corpusForm.corpusName"/>
        <html:errors property="corpusName"/>
        <html:text property="corpusName" styleId="corpusName" styleClass="text medium" />
    </li>

    <html:hidden property="corpusID"/>
    <li class="buttonBar bottom">
        <html:submit styleClass="button" property="method.save" onclick="bCancel=false" onmouseover="ajax_showTooltip('ajaxtooltip/info/corpusNameBox.jsp',this);return false" onmouseout="ajax_hideTooltip()" >
            <fmt:message key="button.save"/>
        </html:submit>

        <html:cancel styleClass="button" onclick="bCancel=true" onmouseover="ajax_showTooltip('ajaxtooltip/info/cancelBtn.jsp',this);return false" onmouseout="ajax_hideTooltip()" >
            <fmt:message key="button.cancel"/>
        </html:cancel>
    </li>
</ul>
</html:form>

<script type="text/javascript">
    Form.focusFirstElement($("corpusForm"));
</script>

<html:javascript formName="corpusForm" cdata="false" dynamicJavascript="true" staticJavascript="false"/>
<script type="text/javascript" src="<html:rewrite page="/scripts/validator.jsp"/>"></script>