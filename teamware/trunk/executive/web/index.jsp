<%@ include file="/common/taglibs.jsp"%>

<c:if test="${not empty pageContext.request.remoteUser}">
<c:redirect url="/mainMenu.html"/>
</c:if>
<c:if test="${empty pageContext.request.remoteUser}">
<c:redirect url="/login.jsp"/>
</c:if>