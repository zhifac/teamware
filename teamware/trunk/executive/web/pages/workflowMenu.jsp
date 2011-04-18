<%@ include file="/common/taglibs.jsp"%>
<c:if test="${empty pageContext.request.remoteUser}">
<c:redirect url="/login.jsp"/>
</c:if>
<head>
    <title><fmt:message key="menu.workflowService"/></title>
    <content tag="heading"><fmt:message key="menu.workflowService"/></content>
    <meta name="menu" content="ProjectMenu"/>
</head>

<div class="separator"></div>

<ul class="glassList">
   <authz:authorize ifAnyGranted="${processDefinitionManagingRoles}">
    
    <li>
        <a href="<c:url value="/processDefinitionList.html?method=list"/>">
		<fmt:message key="menu.workflow.process.definitions"/>
	  </a>
	  
    </li>
    
</authz:authorize> 

<p></p>
 <authz:authorize ifAnyGranted="${projectRoles}">
    
    <li>
        <a href="<c:url value="/projects.html?method=listAll"/>">
		<fmt:message key="menu.workflow.allProjects"/>
	  </a>
    </li>
   
</authz:authorize> 
<!-- 
 <authz:authorize ifAnyGranted="${projectUsingRoles}">
    
    <li>
        <a href="<c:url value="/projects.html"/>">
		<fmt:message key="menu.workflow.myProjects"/>
	  </a>
    </li>
    
   
</authz:authorize> 
 -->
<p></p>
   <authz:authorize ifAnyGranted="${processInstanceManagingRoles}">
     <li>
        <a href="<c:url value="/loadProject.html?method=load"/>">
		<fmt:message key="menu.workflow.create.process.instance"/>
	  </a>
    </li>
    <li>
        <a href="<c:url value="/processInstanceList.html?method=listAll"/>">
		<fmt:message key="menu.workflow.allProcessInstances"/>
	  </a>
    </li>
   
</authz:authorize> 
 <!-- 
 <authz:authorize ifAnyGranted="${processInstancesUsingRoles}">
    
    <li>
        <a href="<c:url value="/processInstanceList.html?method=listByUser"/>">
		<fmt:message key="menu.workflow.myProcessInstances"/>
	  </a>
    </li>
   
</authz:authorize>
 
<p></p>
   <authz:authorize ifAnyGranted="${taskInstanceExecutingRoles}">
    
    <li>
        <a href="<c:url value="/taskInstanceList.html?method=listByActor"/>">
		<fmt:message key="menu.workflow.myTask.instances"/>
	  </a>
    </li>
    
     <li>
        <a href="<c:url value="/taskInstanceList.html?method=list"/>">
		<fmt:message key="menu.workflow.groupTask.instances"/>
	  </a>
    </li>
    
   
</authz:authorize> 

--> 

</ul>
