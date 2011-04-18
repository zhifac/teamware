<%@ include file="/common/taglibs.jsp"%>
<c:if test="${empty pageContext.request.remoteUser}">
<c:redirect url="/login.jsp"/>
</c:if>
<head>
    <title><fmt:message key="menu.support"/></title>
    <content tag="heading"><fmt:message key="menu.forum"/></content>
    <meta name="menu" content="SupportMenu"/>
</head>

<div class="separator"></div>

<ul class="glassList">
    <li>
       It seems that Forum is not installed. please check the documentation.
    </li>
</ul>