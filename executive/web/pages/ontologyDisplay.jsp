<%@ include file="/common/taglibs.jsp"%>
<head>
<title><fmt:message key="ontologyRepositoryData.title"/></title>
<content tag="heading"><fmt:message key="ontologyRepositoryData.heading"/></content>
<meta name="menu" content="ResourceMenu"/>
</head>
<c:set var="buttons">
  <input type="button" style="margin-right: 5px"
        onclick="location.href='ontologyDisplay.html?method=download'"
        value="<fmt:message key="button.download"/>"
        onmouseover="ajax_showTooltip('ajaxtooltip/info/downloadOntology.jsp',this);return false" onmouseout="ajax_hideTooltip()" 
        />
  <input type="button" onclick="location.href='<html:rewrite forward="viewOntologyRepositories"/>'"
        value="<fmt:message key="button.done"/>"
        onmouseover="ajax_showTooltip('ajaxtooltip/info/doneBtn.jsp',this);return false" onmouseout="ajax_hideTooltip()" 
        />
</c:set>

<c:out value="${buttons}" escapeXml="false"/>

<display:table name="ontologyRepositoryData" cellspacing="0" cellpadding="0"
    id="ontologyRepositoryData" pagesize="25" class="table ontologyRepositoryData"
    export="true" requestURI="">
    
    <display:column property="data"/>

</display:table>

<c:out value="${buttons}" escapeXml="false"/>

<script type="text/javascript">
    highlightTableRows("ontologyRepositoryData");
</script>

<html:javascript formName="ontologyRepositoryForm" cdata="false" dynamicJavascript="true" staticJavascript="false"/>
<script type="text/javascript" src="<html:rewrite page="/scripts/validator.jsp"/>"></script>