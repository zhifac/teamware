<%@ include file="/common/taglibs.jsp"%>

<head>
    <title><fmt:message key="roleList.title"/></title>
    <content tag="heading"><fmt:message key="roleList.heading"/></content>
    <meta name="menu" content="AdminMenu"/>
</head>

<c:set var="buttons">
    
    <input type="button" style="margin-right: 5px"
        onclick="location.href='<html:rewrite forward="addRole"/>'"
        value="<fmt:message key="button.add"/>" 
        onmouseover="ajax_showTooltip('ajaxtooltip/info/addRoleBtn.jsp',this);return false" onmouseout="ajax_hideTooltip()"/>
    <input type="button" onclick="location.href='<html:rewrite forward="mainMenu" />'"
        value="<fmt:message key="button.done"/>"
        onmouseover="ajax_showTooltip('ajaxtooltip/info/doneBtn.jsp',this);return false" onmouseout="ajax_hideTooltip()"/>
</c:set>

<c:out value="${buttons}" escapeXml="false"/>

<display:table name="roleList" cellspacing="0" cellpadding="0" requestURI="" 
    defaultsort="1" id="roles" pagesize="25" class="table" export="true" sort="list">
    
    <display:column property="name" escapeXml="true" sortable="true" titleKey="roleForm.name" style="width: 35%"
        url="/editRole.html?from=list" paramId="name" paramProperty="name"/>
    <display:column property="description" escapeXml="true" sortable="true" titleKey="roleForm.description" style="width: 50%"/>
	
	<display:column style="width: 5%" url="/roles.html?method=delete" paramId="name" paramProperty="name" media="html">
    	<img src="<c:url value="/images/delete.gif"/>" onmouseover="ajax_showTooltip('ajaxtooltip/info/deleteRole.jsp',this);return false" onmouseout="ajax_hideTooltip()" onclick="bCancel=true; return confirmDialog('Are you sure to delete the selected role?')" />
    </display:column>  

    <display:setProperty name="paging.banner.item_name" value="role"/>
    <display:setProperty name="paging.banner.items_name" value="roles"/>

    <display:setProperty name="export.csv.filename" value="Role List.csv"/>
    <display:setProperty name="export.pdf.filename" value="Role List.pdf"/>
</display:table>

<c:out value="${buttons}" escapeXml="false" />

<script type="text/javascript">
    highlightTableRows("roles");
</script>
