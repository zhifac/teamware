<%@ include file="/common/taglibs.jsp"%>
<c:if test="${empty pageContext.request.remoteUser}">
<c:redirect url="/login.jsp"/>
</c:if>
<head>
    <title><fmt:message key="menu.support"/></title>
    <content tag="heading"><fmt:message key="menu.support"/></content>
    <meta name="menu" content="SupportMenu"/>
</head>

<div class="separator"></div>

<ul class="glassList">
    <li>
        <a href="<c:url value="/forum.html"/>">
		<fmt:message key="menu.forum"/>
	  </a>
    </li>
  
	   <li>
        <a href="<c:url value="/helpInfo.html"/>">
		<fmt:message key="menu.help"/>
	  </a>
	  </li>
</ul>
