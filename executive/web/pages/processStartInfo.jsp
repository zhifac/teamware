<%@ include file="/common/taglibs.jsp"%>
<head>
<title><fmt:message key="workflowProcessStartInfo.title"/></title>
<content tag="heading"><fmt:message key="workflowProcessStartInfo.heading"/></content>
<meta name="menu" content="ProjectMenu"/>

</head>
<div class="separator"></div>

<table class="detail" cellpadding="5">
    <tr>

        <td>Selected corpus has a big number of documents, so project will take some time to start.
        <br/><br/> Click  <a href="<c:url value="/processInstanceList.html?method=listAll"/>">
		here
	  </a> to go to project list screen. You would need to refresh your browser and project will appear after couple of minutes.
        </td>
    </tr>
</table>

