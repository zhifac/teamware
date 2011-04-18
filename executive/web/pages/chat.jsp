<%@ include file="/common/taglibs.jsp"%>

<head>
    <title><fmt:message key="chat.title"/></title>
    <content tag="heading"><fmt:message key="chat.heading"/></content>
    <!-- dwr-engine and utils -->
    <%-- Note! engine.jsp is a tweaked version of the engine.js that supports urlrewriting for DWR requests.
    This means that the upload mechanism also works if cookies is disabled.
    For details see "Support for DWR without cookies" http://getahead.ltd.uk/dwr/fixes --%>
  <script type='text/javascript' src="<c:url value='/dwr/interface/JavascriptChat.js' />"></script>
 <meta name="menu" content="SupportMenu"/>
</head>

<p><fmt:message key="chat.message"/>: <b><c:out value='${pageContext.request.remoteUser}' /></b></p>


<div id="chatlog"></div>
<br/>

<hr/>
<br/>
<p>
  <textarea id="text" rows="10" cols="80"></textarea>
  </p>
  <p>
  <input type="button" value="Send" onclick='sendMessage("<c:out value='${pageContext.request.remoteUser}' />")'/>
  </p>
