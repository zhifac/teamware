<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@ include file="/common/taglibs.jsp"%>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
    <head>
        <%@ include file="/common/meta.jsp" %>
         <c:set var="currentMenu" scope="request"><decorator:getProperty property="meta.menu"/></c:set>
       <c:set var="velocityCount" scope="request" value="${1}"/>
        <title><decorator:title/> | <fmt:message key="webapp.name"/></title>

        <link rel="stylesheet" type="text/css" media="all" href="<c:url value='/styles/${appConfig["csstheme"]}/theme.css'/>" />
        <link rel="stylesheet" type="text/css" media="print" href="<c:url value='/styles/${appConfig["csstheme"]}/print.css'/>" />
        <link rel="stylesheet" type="text/css" media="screen" href="<c:url value='/ajaxtooltip/css/ajax-tooltip.css'/>" />
       
        <script type="text/javascript" src="<c:url value='/scripts/prototype.js'/>"></script>
        <script type="text/javascript" src="<c:url value='/scripts/scriptaculous.js'/>"></script>
        <script type="text/javascript" src="<c:url value='/scripts/global.js'/>"></script>
        <script type="text/javascript" src="<c:url value='/ajaxtooltip/js/ajax.js'/>"></script>
        <script type="text/javascript" src="<c:url value='/ajaxtooltip/js/ajax-tooltip.js'/>"></script>
        <script type="text/javascript" src="<c:url value='/ajaxtooltip/js/ajax-dynamic-content.js'/>"></script>
        <script type="text/javascript" src="<c:url value='/scripts/javascript-chat.js' />"> </script>
        <script type="text/javascript" src="<c:url value='/dwr/engine.js' />"></script>
        <script type="text/javascript" src="<c:url value='/dwr/util.js' />"></script>
   
        <decorator:head/>
    </head>
<body<decorator:getProperty property="body.id" writeEntireProperty="true"/><decorator:getProperty property="body.class" writeEntireProperty="true"/> onload="init()">

    <div id="page">
        <div id="header" class="clearfix">
            <jsp:include page="/common/header.jsp"/>
        </div>

        <div id="content" class="clearfix">
            <div id="main">
                <%@ include file="/common/messages.jsp" %>
                <h1><decorator:getProperty property="page.heading"/></h1>
                <decorator:body/>
            </div>

            <div id="nav">
                <div class="wrapper">
                    <h2 class="accessibility">Navigation</h2>
                    <jsp:include page="/common/menu.jsp"/>
                </div>
                <hr />
            </div><!-- end nav -->
        </div>

        <div id="footer" class="clearfix">
            <jsp:include page="/common/footer.jsp"/>
        </div>
    </div>
</body>
</html>