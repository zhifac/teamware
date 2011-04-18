<%@ include file="/common/taglibs.jsp"%>
<%@page import="gleam.executive.Constants"%>
<head>

<% 
String method = request.getParameter("method");


if("listAll".equals(method)){ %>
<c:set var="forward" value="viewAllProjects"/>    
<title><fmt:message key="allProjectList.title"/></title>
    <content tag="heading">
	<fmt:message key="allProjectList.heading" />
	</content>

<% }
else {%>
<c:set var="forward" value="viewAllProjects"/>    
<title><fmt:message key="myProjectList.title"/></title>    
    <content tag="heading">
	<fmt:message key="myProjectList.heading" />
	</content>

<%}%>

<meta name="menu" content="ProjectMenu"/>	  
</head>
  

<c:set var="buttons">
    <input type="button" style="margin-right: 5px"
        onclick="location.href='<html:rewrite forward="addProject"/>'"
        value="<fmt:message key="button.createNew"/>" 
        onmouseover="ajax_showTooltip('ajaxtooltip/info/addProjectBtn.jsp',this);return false" onmouseout="ajax_hideTooltip()"/>
    <input type="button" onclick="location.href='<html:rewrite forward="workflowMenu" />'"
        value="<fmt:message key="button.backToWorkflowMenu"/>"
        onmouseover="ajax_showTooltip('ajaxtooltip/info/doneBtn.jsp',this);return false" onmouseout="ajax_hideTooltip()"/>
</c:set>

<c:out value="${buttons}" escapeXml="false"/>

<display:table name="projectList" cellspacing="0" cellpadding="0" requestURI="" 
    defaultsort="1" id="projects" pagesize="25" class="table" export="true" sort="list" decorator="gleam.executive.webapp.displaytag.ProjectDecorator">
    
    
   <display:column style="width: 27%" media="html" sortable="true" titleKey="projectForm.name" headerClass="sortable">
      <c:choose>
       <c:when test="${projects.enabled}">
      <a href="<c:url value="/processInstanceList.html?method=listTopProcessesWithTheSameKeyAndName&projectName=${projects.name}"/>&from=listTopProcessesWithTheSameKeyAndName" onmouseover="ajax_showTooltip('ajaxtooltip/info/viewProcessBtn.jsp',this);return false" onmouseout="ajax_hideTooltip()">
        <c:out value='${projects.name}'/>
      </a>
    </c:when>
      <c:otherwise>
         <c:out value="${projects.name}"/>
      </c:otherwise>
       </c:choose>

    </display:column>
    <display:column style="width: 27%" media="pdf csv" sortable="true" titleKey="projectForm.name" headerClass="sortable">
         <c:out value="${projects.name}"/>
    </display:column>
   
    <display:column  style="width: 15%" property="description" escapeXml="true" sortable="true" titleKey="projectForm.description"/>
	<display:column  style="width: 13%" property="user.username" escapeXml="true" sortable="true" titleKey="projectForm.owner"/>
	<display:column style="width: 13%" property="lastUpdate" sortable="true" titleKey="projectForm.lastUpdate"/>
	<authz:authorize ifAnyGranted="superadmin">
	<display:column style="width: 2%" property="version" sortable="true" title="V"/>
    <display:column style="width: 3%" title="" media="html">
    <a href="<c:url value="/editProject.html?id=${projects.id}&name=${projects.name}"/>">
         <img src="<c:url value="/images/edit.gif"/>" onmouseover="ajax_showTooltip('ajaxtooltip/info/editProject.jsp',this);return false" onmouseout="ajax_hideTooltip()"/>
     </a>
    </display:column>
     <display:column style="width: 3%" media="html">
       <c:choose>
       <c:when test="${projects.enabled}">
          <a href="<c:url value="/download.html?type=project&id=${projects.id}"/>">
         <img src="<c:url value="/images/download.gif"/>" onmouseover="ajax_showTooltip('ajaxtooltip/info/downloadProject.jsp',this);return false" onmouseout="ajax_hideTooltip()"/>
   </a>
    </c:when>
      <c:otherwise>
         &nbsp;
      </c:otherwise>
       </c:choose>
     </display:column>
    </authz:authorize> 
   

    <display:column style="width: 3%" property="link" title="" media="html" />
      <display:column style="width: 3%" media="html">
       <c:choose>
       <c:when test="${projects.enabled}">
   <img src="<c:url value="/images/iconInformation.gif"/>" onmouseover="ajax_showTooltip('<c:url value="ajaxtooltip/view/projectToTable.jsp?projectId=${projects.id}"/>',this);return false" onmouseout="ajax_hideTooltip()"/>
    </c:when>
      <c:otherwise>
         &nbsp;
      </c:otherwise>
       </c:choose>
     </display:column>    
	<display:column style="width: 3%" media="html">
    	<a href="<c:url value="/projects.html?method=delete&id=${projects.id}&forward=${forward}"/>">
         
    	<img src="<c:url value="/images/delete.gif"/>" onmouseover="ajax_showTooltip('ajaxtooltip/info/deleteProject.jsp',this);return false" onmouseout="ajax_hideTooltip()" onclick="bCancel=true; return confirmDialog('You are about to delete the selected workflow template. You will also delete all projects that are using this workflow. This action is irreversible. Are you sure you want to do this?')" />
       </a>
    </display:column>  

    <display:setProperty name="paging.banner.item_name" value="workflow template"/>
    <display:setProperty name="paging.banner.items_name" value="workflow templates"/>
    <display:setProperty name="export.csv.filename" value="Worklfow Templates.csv"/>
    <display:setProperty name="export.pdf.filename" value="Workflow Templates.pdf"/>
</display:table>

<c:out value="${buttons}" escapeXml="false" />

<script type="text/javascript">
    highlightTableRows("projects");
</script>
 