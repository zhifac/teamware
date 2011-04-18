<%@ include file="/common/taglibs.jsp"%>

<head>
    <title><fmt:message key="serviceList.title"/></title>
    <content tag="heading"><fmt:message key="serviceList.heading"/></content>
    <meta name="menu" content="AdminMenu"/>
</head>

<c:set var="buttons">
         <html:submit styleClass="button" property="method.save" onclick="bCancel=false" onmouseover="ajax_showTooltip('ajaxtooltip/info/updateServiceBtn.jsp',this);return false" onmouseout="ajax_hideTooltip()" >
            <fmt:message key="button.save"/>
        </html:submit>
    <input type="button" onclick="location.href='<html:rewrite forward="mainMenu" />'"
        value="<fmt:message key="button.done"/>"
        onmouseover="ajax_showTooltip('ajaxtooltip/info/doneBtn.jsp',this);return false" onmouseout="ajax_hideTooltip()"/>
</c:set>

 <html:form action="saveService" method="post" styleId="serviceForm">
<c:out value="${buttons}" escapeXml="false"/>

<display:table name="serviceList" cellspacing="0" cellpadding="0" requestURI="" 
    defaultsort="1" id="services" pagesize="25" class="table" export="true" sort="list">
   
    <display:column property="name" escapeXml="true" sortable="true" titleKey="serviceForm.name" style="width: 35%"/>

     <display:column titleKey="serviceForm.enabled" style="width: 10%" media="html">
       <c:choose>
       <c:when test="${services.enabled}">
         <input type="checkbox" name="<c:out value="${services.id}"/>" checked="checked">
       </c:when>
       <c:otherwise>
	   <input type="checkbox" name="<c:out value="${services.id}"/>">
       </c:otherwise>
       </c:choose>
     </display:column>
    <display:setProperty name="paging.banner.item_name" value="service"/>
    <display:setProperty name="paging.banner.items_name" value="services"/>

    <display:setProperty name="export.csv.filename" value="Service List.csv"/>
    <display:setProperty name="export.pdf.filename" value="Service List.pdf"/>
</display:table>

<c:out value="${buttons}" escapeXml="false" />

</html:form>
<script type="text/javascript">
    highlightTableRows("services");
</script>
