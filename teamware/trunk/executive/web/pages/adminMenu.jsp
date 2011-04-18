<%@ include file="/common/taglibs.jsp"%>
<c:if test="${empty pageContext.request.remoteUser}">
<c:redirect url="/login.jsp"/>
</c:if>
<head>
    <title><fmt:message key="menu.admin.heading"/></title>
    <content tag="heading"><fmt:message key="menu.admin.heading"/></content>
    <meta name="menu" content="AdminMenu"/>
</head>

<div class="separator"></div>

<ul class="glassList">
    
     <li>
        <a href="<c:url value="/users.html"/>">
		<fmt:message key="menu.admin.users"/>
	  </a>
    </li>
    
    <li>
        <a href="<c:url value="/activeUsers.html"/>">
		<fmt:message key="mainMenu.activeUsers"/>
	  </a>
    </li>
    <authz:authorize ifAnyGranted="superadmin">
    <li>
        <a href="<c:url value="/roles.html"/>">
		<fmt:message key="menu.admin.roles"/>
	  </a>
	  </li>
	  
	  <li>
        <a href="<c:url value="/services.html"/>">
		<fmt:message key="menu.admin.services"/>
	  </a>
	  </li>
	  
	   <li>
        <a href="<c:url value="/resources.html"/>">
		<fmt:message key="menu.admin.resources"/>
	  </a>
    </li>
    </authz:authorize> 
      <li>
        <a href="<c:url value="/clickstreams.jsp"/>">
		<fmt:message key="menu.clickstream"/>
	  </a>
    </li>
</ul>
