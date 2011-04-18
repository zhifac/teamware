<%@ include file="/common/taglibs.jsp"%>
<%@page import="gleam.executive.Constants"%>
<head>

<meta name="menu" content="ProjectMenu"/>

<% 
String from = "";
if(request.getAttribute("from")!=null){
	from = (String)request.getAttribute("from");
}
else {
	from = request.getParameter("from");
}

if(Constants.FORWARD_PROCESSES_FOR_PROCESSDEFINITION.equals(from)){ %>
    <title><fmt:message key="workflowProcessInstancesForProcessDefinitionList.title"/></title>
  	<content tag="heading">
  	<fmt:message key="workflowProcessInstancesForProcessDefinitionList.title" />
    </content>
<% }
else if(Constants.FORWARD_PROCESSES_IN_PROJECT.equals(from)){%>
    <title><fmt:message key="workflowProcessInstancesInProjectList.title"/></title>
	<content tag="heading">
	<fmt:message key="workflowProcessInstancesInProjectList.title" /> "<%=(String)request.getAttribute("projectName") %>"
    </content>
<%}
else if(Constants.FORWARD_SUB_PROCESSES.equals(from)){%>
<title><fmt:message key="workflowSubProcessInstances.title"/></title>
	<content tag="heading">
	<fmt:message key="workflowSubProcessInstances.title" /> "<%=(String)request.getAttribute("name") %>"
    </content>
<%}
else {%>
  <title><fmt:message key="workflowProcessInstancesList.title"/></title>
    <content tag="heading">
	<fmt:message key="workflowProcessInstancesList.title" />
    </content>
<%}%>


</head>
<form action="processInstanceList.html" method="post">
<input type="hidden" id="from" name="from" value="<%=from%>"/>
<input type="hidden" id="method" name="method" value="cancelAll"/>

<c:set var="buttons">
  <% if(Constants.FORWARD_PROCESSES_IN_PROJECT.equals(from)){ %>   
    <input type="button" style="margin-right: 5px"
        onclick="location.href='<c:url value="/loadProject.html?method=load"/>'"
        value="<fmt:message key="menu.workflow.create.process.instance"/>"
        onmouseover="ajax_showTooltip('ajaxtooltip/info/createProcessInstanceBtn.jsp',this);return false" onmouseout="ajax_hideTooltip()"/>   	  
   <input type="button" onclick="location.href='<html:rewrite forward="viewAllProjects" />'"
        value="<fmt:message key="button.backToProjects"/>"
        onmouseover="ajax_showTooltip('ajaxtooltip/info/doneBtn.jsp',this);return false" onmouseout="ajax_hideTooltip()"/>
  <% } else if(Constants.FORWARD_PROCESSES_FOR_PROCESSDEFINITION.equals(from)) { %>   
   <input type="button" style="margin-right: 5px"
        onclick="location.href='<c:url value="/processDefinitionList.html?method=list"/>'"
        value="<fmt:message key="button.backToProcessDefinitionList"/>"
        onmouseover="ajax_showTooltip('ajaxtooltip/info/doneBtn.jsp',this);return false" onmouseout="ajax_hideTooltip()"/>
  <% } else if(Constants.FORWARD_SUB_PROCESSES.equals(from)) { %>   
   <input type="button" style="margin-right: 5px"
        onclick="location.href='<c:url value="/processInstanceList.html?method=listTopProcessesWithTheSameKeyAndName&processInstanceId=${param.id}"/>'"
        value="<fmt:message key="button.backToProcessInstance"/>"
        onmouseover="ajax_showTooltip('ajaxtooltip/info/doneBtn.jsp',this);return false" onmouseout="ajax_hideTooltip()"/>
   <% } else { %>
   <input type="button" style="margin-right: 5px"
        onclick="location.href='<c:url value="/loadProject.html?method=load"/>'"
        value="<fmt:message key="menu.workflow.create.process.instance"/>"
        onmouseover="ajax_showTooltip('ajaxtooltip/info/createProcessInstanceBtn.jsp',this);return false" onmouseout="ajax_hideTooltip()"/>   	  
 
   <input type="button" onclick="location.href='<html:rewrite forward="workflowMenu" />'"
        value="<fmt:message key="button.backToWorkflowMenu"/>"
        onmouseover="ajax_showTooltip('ajaxtooltip/info/doneBtn.jsp',this);return false" onmouseout="ajax_hideTooltip()"/>
  <% } %>
</c:set>
<c:out value="${buttons}" escapeXml="false"/>

<display:table name="processInstanceList" cellspacing="0" cellpadding="0" requestURI=""
    defaultsort="1" id="processInstances" pagesize="25" class="table" export="true" sort="list" 
    decorator="gleam.executive.webapp.displaytag.ProcessInstanceListDecorator">
    <display:setProperty name="export.csv.decorator" value="gleam.executive.webapp.displaytag.ProcessInstanceListDecorator"/>
<display:setProperty name="export.pdf.decorator" value="gleam.executive.webapp.displaytag.ProcessInstanceListDecorator"/>
 
    <display:column style="width:10%" property="name" escapeXml="true" sortable="true" titleKey="workflowProcessInstance.name"/>
    <display:column style="width:10%" property="key" escapeXml="true" sortable="true" titleKey="workflowProcessInstance.key"/>
    <display:column  style="width: 10%" property="corpusName" sortable="true" media="html" titleKey="workflowProcessInstance.corpus"/>	
    <display:column  style="width: 10%" property="corpusNameWithoutLinks" media="pdf csv" titleKey="workflowProcessInstance.corpus"/>	
    <display:column  style="width: 9%" property="username" sortable="true"  titleKey="workflowProcessInstance.manager"/>	   
    <display:column style="width:11%" property="start" sortable="true" titleKey="workflowProcessInstance.start" autolink="true"/>
    <display:column style="width:11%" property="end" sortable="true" titleKey="workflowProcessInstance.end" autolink="true"/>
    <display:column style="width:8%" property="status" sortable="true" titleKey="workflowProcessInstance.status" autolink="true"/>
    
    <display:column style="width: 2%" paramId="id" paramProperty="id" media="html">
    <c:choose> 
    <c:when test="${processInstances.parentId==0}">
     <a href="<c:url value="/processInstanceList.html?method=listSubProcessesForProcessInstance&id=${processInstances.id}&from=${from}"/>">
        <img src="<c:url value="/images/sub.png"/>" alt="<fmt:message key="icon.sub"/>" class="icon" 
    	    onmouseover="ajax_showTooltip('ajaxtooltip/info/viewSubprocesses.jsp',this);return false" onmouseout="ajax_hideTooltip()"
    	  />
      </a>
     </c:when>
     <c:otherwise>
      <a href="<c:url value="/taskInstanceList.html?method=listTasksForProcessInstance&processInstanceId=${processInstances.id}"/>">
       <img src="<c:url value="/images/open.gif"/>" alt="<fmt:message key="icon.open"/>" class="icon" 
     	onmouseover="ajax_showTooltip('ajaxtooltip/info/taskListForProcessInstanceBtn.jsp',this);return false" onmouseout="ajax_hideTooltip()"
     />
      </a>
     </c:otherwise>
     </c:choose> 
    </display:column>

    <display:column style="width: 2%" paramId="id" paramProperty="id" media="html">
    <c:choose>
     <c:when test="${processInstances.running && processInstances.parentId==0}">
     <a href="<c:url value="/processInstanceList.html?method=suspend&id=${processInstances.id}&from=${from}" />">
        <img src="<c:url value="/images/suspend.gif"/>" alt="<fmt:message key="icon.suspend"/>" class="icon" 
    	    onmouseover="ajax_showTooltip('ajaxtooltip/info/suspendInstanceBtn.jsp',this);return false" onmouseout="ajax_hideTooltip()"
    	  />
      </a>
     </c:when> 
    <c:when test="${processInstances.suspended && processInstances.parentId==0}">
     <a href="<c:url value="/processInstanceList.html?method=resume&id=${processInstances.id}&from=${from}"/>">
        <img src="<c:url value="/images/resume.gif"/>" alt="<fmt:message key="icon.resume"/>" class="icon" 
    	    onmouseover="ajax_showTooltip('ajaxtooltip/info/resumeInstanceBtn.jsp',this);return false" onmouseout="ajax_hideTooltip()"
    	  />
      </a>
     </c:when>
      <c:when test="${processInstances.parentId!=0 && processInstances.name!='review'}">
     <a href="<c:url value="/annotationStatus.html?method=showOverview&id=${processInstances.id}"/>">
        <img src="<c:url value="/images/monitor.gif"/>" alt="<fmt:message key="icon.monitor"/>" class="icon" 
    	    onmouseover="ajax_showTooltip('ajaxtooltip/info/monitorInstanceBtn.jsp',this);return false" onmouseout="ajax_hideTooltip()"
    	  />
      </a>
     </c:when>
     </c:choose> 
    </display:column>
      <display:column style="width: 2%" paramId="id" paramProperty="id" media="pdf csv">
    <c:choose>
     <c:when test="${processInstances.running && processInstances.parentId==0}">
      <fmt:message key="status.running"/>
     </c:when> 
    <c:when test="${processInstances.suspended && processInstances.parentId==0}">
     <fmt:message key="status.suspended"/>
     </c:when>
      <c:when test="${processInstances.ended && processInstances.parentId==0}">
     <fmt:message key="status.completed"/>
     </c:when>
     </c:choose> 
    </display:column>

    <display:column style="width:2%" paramId="processInstanceId" paramProperty="id" media="html">	
		<c:choose>   
	   <c:when test="${processInstances.parentId==0}">
     <a href="<c:url value="/editActors.html?processInstanceId=${processInstances.id}&from=${from}"/>">
       <img src="<c:url value="/images/users.gif"/>" alt="<fmt:message key="icon.open"/>" class="icon" 
     	onmouseover="ajax_showTooltip('ajaxtooltip/info/editProcessInstanceUsersBtn.jsp',this);return false" onmouseout="ajax_hideTooltip()"
     />
      </a>
      
     </c:when>
     </c:choose> 
      </display:column>  
     <display:column style="width:2%" paramId="processInstanceId" paramProperty="id" media="html">	
		<c:choose> 
      <c:when test="${processInstances.parentId==0 && !processInstances.ended}">
      
     <a href="<c:url value="/processInstanceList.html?method=end&id=${processInstances.id}&from=${from}"/>">
        <img src="<c:url value="/images/end.png"/>" alt="<fmt:message key="icon.end"/>" class="icon" 
    	    onmouseover="ajax_showTooltip('ajaxtooltip/info/endInstanceBtn.jsp',this);return false" onmouseout="ajax_hideTooltip()"
    	  />
      </a>
      
     </c:when>
     </c:choose> 
     
     	  	  
    </display:column>    
    
    <authz:authorize ifAnyGranted="superadmin">
    <display:column style="width: 1%" property="version" media="html" title="V" sortable="true"/>
    </authz:authorize> 
    
    
    <% if(!Constants.FORWARD_SUB_PROCESSES.equals(from)){ %>
    <display:column style="width: 11%" property="checkBox" media="html"
        title="<input type=\"checkbox\" name=\"allbox\" onclick=\"checkAll(document.forms[0])\">  
        <input name=\"toDelete\" id=\"toDelete\" type=\"submit\" value=\"Delete\"
            alt=\"All the selected items above will be deleted from the datastore permanently\"
            onmouseover=\"ajax_showTooltip('ajaxtooltip/info/deleteInstancesBtn.jsp',this);return false\" 
            onmouseout=\"ajax_hideTooltip()\"
            onclick=\"bCancel=true;  return anyChecked(document.forms[0]);confirmDialog('Are you sure you want to delete all the selected instance(s)?')\"/>  
   
    ">
    </display:column>
     <display:setProperty name="paging.banner.item_name" value="Project"/>
     <display:setProperty name="paging.banner.items_name" value="Projects"/>
     <display:setProperty name="export.csv.filename" value="Projects.csv"/>
     <display:setProperty name="export.pdf.filename" value="Projects.pdf"/>
    <% } else {%>
    <display:setProperty name="paging.banner.item_name" value="Process"/>
    <display:setProperty name="paging.banner.items_name" value="Processes"/>
    <display:setProperty name="export.csv.filename" value="Processes.csv"/>
    <display:setProperty name="export.pdf.filename" value="Processes.pdf"/>
    <% } %>
    

</display:table>
<c:out value="${buttons}" escapeXml="false" />
 
</form>


<script type="text/javascript">
    highlightTableRows("processInstances");
</script>
