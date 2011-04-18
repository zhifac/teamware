<%@ include file="/common/taglibs.jsp"%>

<div id="branding">
    
    <h1><img style="vertical-align:middle" src="<c:url value="/images/logo.gif"/>" />&nbsp;&nbsp;&nbsp;&nbsp;<c:out value="${applicationScope.webapptitle}"/></h1>

</div>

<hr />
<%-- Put constants into request scope --%>
<executive:constants scope="request"/>
