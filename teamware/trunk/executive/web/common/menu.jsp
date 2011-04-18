<%@ include file="/common/taglibs.jsp"%>

<executive:useMenuDisplayer name="Velocity" config="WEB-INF/classes/cssHorizontalMenu.vm" permissions="menusAdapter">
<ul id="primary-nav" class="menuList">
    <li class="pad">&nbsp;</li>
    <c:if test="${empty pageContext.request.remoteUser}"><li><a href="<c:url value="/login.jsp"/>" class="current"><fmt:message key="login.title"/></a></li></c:if>
    <executive:displayMenu name="MainMenu"/>
    <executive:displayMenu name="ResourceMenu"/>
    <executive:displayMenu name="ProjectMenu"/>
    <executive:displayMenu name="SupportMenu"/>
    <executive:displayMenu name="AdminMenu"/>
</ul>
</executive:useMenuDisplayer>