<%@ include file="/common/taglibs.jsp"%>
<%@page import="gleam.executive.Constants"%>
<head>
<content tag="heading"><fmt:message key="workflow.taskInstanceList.heading"/></content>
<% 
String method = request.getParameter("method");


if(Constants.MY_TASK_INSTANCES_METHOD.equals(method)){ %>
    <title><fmt:message key="menu.workflow.myTask.instances"/></title>
    <content tag="heading">
	<fmt:message key="menu.workflow.myTask.instances" />
    </content>
<% }
else if(Constants.ALL_TASK_INSTANCES_METHOD.equals(method)){ %>
    <title><fmt:message key="menu.workflow.groupTask.instances"/></title>
  	<content tag="heading">
  	<fmt:message key="menu.workflow.groupTask.instances" />
    </content>
<% } %>

<meta name="menu" content="ProjectMenu"/>



</head>
<c:set var="buttons">
    <input type="button" onclick="window.location.reload()"
        value="<fmt:message key="button.refresh"/>"/>
     <input type="button" onClick="history.go(-1)"
        value="<fmt:message key="button.done"/>"
        onmouseover="ajax_showTooltip('ajaxtooltip/info/doneBtn.jsp',this);return false" onmouseout="ajax_hideTooltip()"
        />
</c:set>

<c:out value="${buttons}" escapeXml="false"/>
<display:table name="taskInstanceList" cellspacing="0" cellpadding="0" requestURI=""
    defaultsort="1" id="taskInstances" pagesize="50" sort="list" class="table" export="true">
    	
    <display:column style="width: 15%" headerClass="sortable" sortable="true" titleKey="workflowTaskInstance.name" media="html">
     <a href="<c:url value="/taskInstanceList.html?method=view&id=${taskInstances.id}"/>"  onmouseover="ajax_showTooltip('ajaxtooltip/info/viewTaskInstance.jsp',this);return false" onmouseout="ajax_hideTooltip()">
       <c:out value='${taskInstances.name}'/>
      </a>
    </display:column>
    <display:column style="width: 15%" headerClass="sortable" sortable="true" titleKey="workflowTaskInstance.name" media="pdf csv">
     <c:out value='${taskInstances.name}'/>
    </display:column>
    <display:column style="width: 20%" property="processInstanceKey" escapeXml="true" sortable="true" titleKey="workflowTaskInstance.process"/>
    <display:column style="width: 11%" property="start" sortable="true" titleKey="workflowTaskInstance.start"/>
    <display:column style="width: 11%" property="end" sortable="true" titleKey="workflowTaskInstance.end"/>
    <display:column style="width: 10%" property="state" sortable="true" titleKey="workflowTaskInstance.state"/>
    <display:column style="width: 11%" property="actorName" sortable="true" titleKey="workflowTaskInstance.actorName"/>
	
	<display:column style="width: 6%" paramId="id" paramProperty="id" media="html">
    <c:if test="${taskInstances.state=='Pending'}">
    <c:choose> 
    <c:when test="${taskInstances.actorName!='' &&  taskInstances.actorName!=null}">
     <c:if test="${taskInstances.actorName==pageContext.request.remoteUser}">
     <a href="<c:url value="/taskInstanceList.html?method=start&id=${taskInstances.id}"/>">
       <img src="<c:url value="/images/start.gif"/>" alt="<fmt:message key="icon.start"/>" class="icon" 
        onmouseover="ajax_showTooltip('ajaxtooltip/info/executeTaskInstance.jsp',this);return false" onmouseout="ajax_hideTooltip()"/>
      </a> 
        <c:if test="${taskInstances.pooledActors!=null}"> 
          <a href="<c:url value="/taskInstanceList.html?method=reject&id=${taskInstances.id}"/>">
          <img src="<c:url value="/images/reject.gif"/>" alt="<fmt:message key="icon.reject"/>" class="icon" 
          onmouseover="ajax_showTooltip('ajaxtooltip/info/rejectTaskInstance.jsp',this);return false" onmouseout="ajax_hideTooltip()"/>
          </a>
        </c:if>
      </c:if>
      <c:if test="${taskInstances.actorName!=pageContext.request.remoteUser}">
      <authz:authorize ifAnyGranted="${processInstanceManagingRoles}">
        <a href="<c:url value="/taskInstanceList.html?method=start&id=${taskInstances.id}"/>">
       <img src="<c:url value="/images/start.gif"/>" alt="<fmt:message key="icon.start"/>" class="icon" 
        onmouseover="ajax_showTooltip('ajaxtooltip/info/executeTaskInstance.jsp',this);return false" onmouseout="ajax_hideTooltip()"/>
      </a> 
       <c:if test="${taskInstances.pooledActors!=null}"> 
          <a href="<c:url value="/taskInstanceList.html?method=reject&id=${taskInstances.id}"/>">
          <img src="<c:url value="/images/reject.gif"/>" alt="<fmt:message key="icon.reject"/>" class="icon" 
          onmouseover="ajax_showTooltip('ajaxtooltip/info/rejectTaskInstance.jsp',this);return false" onmouseout="ajax_hideTooltip()"/>
          </a>
        </c:if>
      </authz:authorize> 
      </c:if>
     </c:when>
     <c:otherwise>
     <c:if test="${taskInstances.priority!=1}"> 
     <a href="<c:url value="/taskInstanceList.html?method=accept&id=${taskInstances.id}"/>">
        <img src="<c:url value="/images/accept.gif"/>" alt="<fmt:message key="icon.accept"/>" class="icon" 
    	    onmouseover="ajax_showTooltip('ajaxtooltip/info/acceptTaskInstance.jsp',this);return false" onmouseout="ajax_hideTooltip()"
    	  />
      </a>
      </c:if>
       <c:if test="${taskInstances.priority==1}"> 
      <authz:authorize ifAnyGranted="${processInstanceManagingRoles}">
        <a href="<c:url value="/taskInstanceList.html?method=end&id=${taskInstances.id}"/>">
       <img src="<c:url value="/images/end.png"/>" alt="<fmt:message key="icon.end"/>" class="icon" 
        onmouseover="ajax_showTooltip('ajaxtooltip/info/endTaskBtn.jsp',this);return false" onmouseout="ajax_hideTooltip()"/>
      </a> 
       </authz:authorize>
      </c:if>
     </c:otherwise>
     </c:choose> 
     </c:if>
    </display:column>
    
    <display:column style="width: 6%" paramId="id" paramProperty="id" media="pdf csv">
    <c:if test="${taskInstances.state=='Pending'}">
    <fmt:message key="status.pending"/>
     </c:if>
    </display:column>
    <display:setProperty name="paging.banner.item_name" value="Task"/>
    <display:setProperty name="paging.banner.items_name" value="Tasks"/>
</display:table>

<c:out value="${buttons}" escapeXml="false" />

<script type="text/javascript">
    highlightTableRows("taskInstances");
</script>
