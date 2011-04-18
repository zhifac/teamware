<%@ include file="/common/taglibs.jsp"%>

<head>
    <title><fmt:message key="resourceList.title"/></title>
    <content tag="heading"><fmt:message key="resourceList.heading"/></content>
    <meta name="menu" content="AdminMenu"/>
</head>

<c:set var="buttons">
    
    <input type="button" style="margin-right: 5px"
        onclick="location.href='<html:rewrite forward="addResource"/>'"
        value="<fmt:message key="button.add"/>" 
        onmouseover="ajax_showTooltip('ajaxtooltip/info/addResourceBtn.jsp',this);return false" onmouseout="ajax_hideTooltip()"/>
    <input type="button" onclick="location.href='<html:rewrite forward="mainMenu" />'"
        value="<fmt:message key="button.done"/>"
        onmouseover="ajax_showTooltip('ajaxtooltip/info/doneBtn.jsp',this);return false" onmouseout="ajax_hideTooltip()"/>
</c:set>

<c:out value="${buttons}" escapeXml="false"/>

<display:table name="resourceList" cellspacing="0" cellpadding="0" requestURI="" 
    defaultsort="3" id="resources" pagesize="50" class="table" export="true" sort="list">
  <c:choose>
  <c:when test="${resources.service.enabled}">
    <display:column sortable="true" titleKey="resourceForm.url" style="width: 25%" media="html">
      <a href="<c:url value="/editResource.html?from=list&method=edit&id=${resources.id}"/>">
        <c:out value="${resources.url}"/>
      </a>
    </display:column>
     <display:column sortable="true" titleKey="resourceForm.url" style="width: 25%" media="pdf csv">
       <c:out value="${resources.url}"/>
    </display:column>
    <display:column property="description" escapeXml="true" sortable="true" titleKey="resourceForm.description" style="width: 40%"/>
    <display:column property="service.name" escapeXml="true" sortable="true" titleKey="serviceForm.name" style="width: 30%"/>
    <display:column style="width: 5%" media="html">
    <img src="<c:url value="/images/active.gif"/>" class="icon" onmouseover="ajax_showTooltip('ajaxtooltip/info/resourceActiveBulb.jsp',this);return false" onmouseout="ajax_hideTooltip()"/>
    </display:column>
    <display:column style="width: 5%" url="/resources.html?method=delete" paramId="id" paramProperty="id" media="html">
    	<img src="<c:url value="/images/delete.gif"/>" onmouseover="ajax_showTooltip('ajaxtooltip/info/deleteResource.jsp',this);return false" onmouseout="ajax_hideTooltip()" onclick="bCancel=true; return confirmDialog('Are you sure to delete the selected resource?')" />
    </display:column>

  </c:when>
  <c:otherwise>
    <display:column sortable="true" titleKey="resourceForm.url" style="width: 25%">
        <c:out value="${resources.url}"/>
    </display:column>
    <display:column property="description" escapeXml="true" sortable="true" titleKey="resourceForm.description" style="width: 40%"/>
    <display:column property="service.name" escapeXml="true" sortable="true" titleKey="serviceForm.name" style="width: 30%"/>

    <display:column style="width: 5%" media="html">
    <img src="<c:url value="/images/inactive.gif"/>" class="icon" onmouseover="ajax_showTooltip('ajaxtooltip/info/resourceInactiveBulb.jsp',this);return false" onmouseout="ajax_hideTooltip()"/>
    </display:column>

  </c:otherwise>
  </c:choose>
    <display:setProperty name="paging.banner.item_name" value="resource"/>
    <display:setProperty name="paging.banner.items_name" value="resources"/>

    <display:setProperty name="export.csv.filename" value="Resource List.xls"/>
    <display:setProperty name="export.pdf.filename" value="Resource List.pdf"/>
   
</display:table>

<c:out value="${buttons}" escapeXml="false" />

<script type="text/javascript">
    highlightTableRows("resources");
</script>