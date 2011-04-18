<%@ include file="/common/taglibs.jsp"%>

<head>
    <title><fmt:message key="userList.title"/></title>
    <content tag="heading"><fmt:message key="userList.heading"/></content>
    <meta name="menu" content="AdminMenu"/>
</head>

<c:set var="buttons">
    <input type="button" style="margin-right: 5px"
        onclick="location.href='<html:rewrite forward="addUser"/>'"
        value="<fmt:message key="button.add"/>"/>
        
    <input type="button" style="margin-right: 5px"
        onclick="location.href='<c:url value="/bulkUpload.html"/>'"
        value="<fmt:message key="button.bulkUpload"/>"
        onmouseover="ajax_showTooltip('ajaxtooltip/info/bulkUpload.jsp',this);return false" onmouseout="ajax_hideTooltip()"   
     />    

    <html:submit style="margin-right: 5px" property="method.enable" onclick="bCancel=false">
                <fmt:message key="button.update"/>
    </html:submit>
    
     <html:submit style="margin-right: 5px" property="method.delete" onclick="bCancel=true; return confirmDialog('You are about to delete the selected users. This action is irreversible. Are you sure you want to do this?')">
                <fmt:message key="button.delete"/>
    </html:submit>
    
    <input type="button" onclick="location.href='<c:url value="/aMenu.html"/>'"
        value="<fmt:message key="button.backToAdminMenu"/>"/>
</c:set>


<html:form action="users" styleId="userForm" method="post">
<c:out value="${buttons}" escapeXml="false"/>
<display:table name="userList" cellspacing="0" cellpadding="0" requestURI="" 
     id="users" pagesize="50" class="table" export="true" sort="list" decorator="gleam.executive.webapp.displaytag.UserListDecorator">
    <display:column sortable="true" titleKey="userForm.username" media="html">
    <c:choose>
     <c:when test="${users.id!=1}">
     <a href="<c:url value="/editUser.html?from=list&username=${users.username}"/>">
       <c:out value="${users.username}"/>
      </a>
     </c:when>
     <c:otherwise>
        <c:out value="${users.username}"/>
      </c:otherwise>
     </c:choose> 
     </display:column>
      <display:column sortable="true" titleKey="userForm.username" media="csv pdf">
        <c:out value="${users.username}"/>
     </display:column>
    <display:column escapeXml="true" titleKey="userForm.password" media="csv" style="width: 8%"/>
    <display:column property="firstName" escapeXml="true" sortable="true" titleKey="userForm.firstName" style="width: 12%"/>
    <display:column property="lastName" escapeXml="true" sortable="true" titleKey="userForm.lastName" style="width: 12%"/>
    <display:column property="email" sortable="true" titleKey="userForm.email" style="width: 20%" autolink="true" media="html"/>
    <display:column property="email" titleKey="userForm.email" style="width: 20%" media="csv pdf"/>
    <display:column sortable="true" titleKey="userForm.roles" style="width: 25%">
      <c:forEach var="role" items="${users.roles}" varStatus="status"><c:out value="${role.name}"/><c:if test="${!status.last}">,</c:if></c:forEach>
    </display:column>
    <display:column property="enableCheckBox" titleKey="userForm.enabled" style="width: 8%" media="html"/>
   
   <display:column style="width: 7%" property="deleteCheckBox" media="html"
        titleKey="userForm.delete"/>  
    <display:setProperty name="paging.banner.item_name" value="user"/>
    <display:setProperty name="paging.banner.items_name" value="users"/>

    <display:setProperty name="export.csv.filename" value="Users.csv"/>
    <display:setProperty name="export.pdf.filename" value="Users.pdf"/>
</display:table>

<c:out value="${buttons}" escapeXml="false" />
</html:form>

<script type="text/javascript">
    highlightTableRows("users");
</script>
