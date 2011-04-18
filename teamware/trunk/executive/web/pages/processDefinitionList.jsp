<%@ include file="/common/taglibs.jsp"%>

<head>
<title><fmt:message key="workflow.processDefinitionList.title"/></title>
<content tag="heading"><fmt:message key="workflow.processDefinitionList.heading"/></content>
<meta name="menu" content="ProjectMenu"/>
</head>

<c:set var="buttons">
    <input type="button" style="margin-right: 5px"
        onclick="location.href='<c:url value="/processDefinitionUpload.html"/>'"
        onmouseover="ajax_showTooltip('ajaxtooltip/info/addProcessBtn.jsp',this);return false" onmouseout="ajax_hideTooltip()"
        value="<fmt:message key="button.uploadProcessDefinition"/>"/>

    <input type="button" onclick="location.href='<html:rewrite forward="mainMenu"/>'"
        onmouseover="ajax_showTooltip('ajaxtooltip/info/doneBtn.jsp',this);return false" onmouseout="ajax_hideTooltip()"
        value="<fmt:message key="button.backToMainPage"/>"/>
</c:set>

<c:out value="${buttons}" escapeXml="false"/>

<display:table name="processDefinitionList" cellspacing="0" cellpadding="0" requestURI=""
    defaultsort="1" id="processDefinitions" pagesize="25" class="table" export="true" sort="list">

    
    <display:column property="name" escapeXml="true" sortable="true" titleKey="workflowProcessDefinition.name" style="width: 35%"/>

    <display:column property="version" sortable="true" titleKey="workflowProcessDefinition.version" style="width: 25%" autolink="true"/>

    <display:column style="width: 5%" url="/processDefinitionList.html?method=start" paramId="id" paramProperty="id" media="html">
     <img src="<c:url value="/images/resume.gif"/>" alt="<fmt:message key="icon.start"/>" class="icon" 
     	onmouseover="ajax_showTooltip('ajaxtooltip/info/startProcessBtn.jsp',this);return false" onmouseout="ajax_hideTooltip()"
     />
    </display:column>
    
    <display:column style="width: 5%" paramId="id" paramProperty="id" media="html"
		  url="/processInstanceList.html?method=list">		  
	<img src="<c:url value="/images/open.gif"/>" alt="<fmt:message key="icon.open"/>" class="icon" 
     	onmouseover="ajax_showTooltip('ajaxtooltip/info/viewProcessBtn.jsp',this);return false" onmouseout="ajax_hideTooltip()"
     />
    </display:column>

    <display:column style="width: 5%" url="/download.html?type=process" paramId="id" paramProperty="id" media="html">
    	<img src="<c:url value="/images/download.gif"/>" onmouseover="ajax_showTooltip('ajaxtooltip/info/downloadProcess.jsp',this);return false" onmouseout="ajax_hideTooltip()"/>
    </display:column> 
    
    <display:column style="width: 5%" url="/processDefinitionList.html?method=delete" paramId="id" paramProperty="id" media="html">
     <img src="<c:url value="/images/delete.gif"/>" alt="<fmt:message key="icon.delete"/>" class="icon" 
     	onmouseover="ajax_showTooltip('ajaxtooltip/info/deleteProcessBtn.jsp',this);return false" onmouseout="ajax_hideTooltip()"
     	onclick="bCancel=true; return confirmDialog('Are you sure to delete this version of process definition?')"
     />
    </display:column>
 

    <display:setProperty name="paging.banner.item_name" value="Process Definition"/>
    <display:setProperty name="paging.banner.items_name" value="Process Definitions"/>

</display:table>

<c:out value="${buttons}" escapeXml="false" />

<script type="text/javascript">
    highlightTableRows("processDefinitions");
</script>
