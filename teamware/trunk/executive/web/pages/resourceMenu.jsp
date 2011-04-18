<%@ include file="/common/taglibs.jsp"%>
<c:if test="${empty pageContext.request.remoteUser}">
<c:redirect url="/login.jsp"/>
</c:if>
<head>
    <title><fmt:message key="menu.resources"/></title>
    <content tag="heading"><fmt:message key="menu.resources"/></content>
    <meta name="menu" content="ResourceMenu"/>
</head>

<div class="separator"></div>

<ul class="glassList">
    
     <li>
        <a href="<c:url value="/corpora.html"/>">
		<fmt:message key="menu.docService"/>
	  </a>
    </li>
    <li>
        <a href="<c:url value="/annotationServices.html"/>">
		<fmt:message key="menu.annotationServices"/>
	  </a>
    </li>
    <li>
        <a href="<c:url value="/schemas.html"/>">
		<fmt:message key="menu.schemas"/>
	  </a>
	  </li>
	  
</ul>
