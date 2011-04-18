<%@ include file="/common/taglibs.jsp" %>
<%@page import="java.util.TimeZone"%>
    <div id="divider"><div></div></div>
    <span class="left">&copy; 2007-2011 <a href="http://gate.ac.uk">GATE team</a> (AGPL licence) |
    <a target="_blank" href="http://gate.ac.uk/events.html">GATE News</a> |
    <a target="_blank" href="http://www.gate.ac.uk/teamware">GATE Teamware</a> |
    <a href="helpInfo.html">HELP</a> |
    Time zone: <strong><%=TimeZone.getDefault().getDisplayName(true, TimeZone.SHORT) %></strong>|
    <% if(request.getRemoteUser()!=null){ %>
    <fmt:message key="user.status"/> <strong><authz:authentication operation="username"/></strong>
       <a href="<c:url value="/logout.jsp"/>">Logout</a>
    </span>
    <%}%>
