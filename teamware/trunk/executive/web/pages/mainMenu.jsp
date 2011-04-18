<%@ include file="/common/taglibs.jsp"%>
<c:if test="${empty pageContext.request.remoteUser}">
<c:redirect url="/login.jsp"/>
</c:if>
<head>
    <title><fmt:message key="mainMenu.title"/></title>
    <content tag="heading"><fmt:message key="mainMenu.heading"/>&nbsp;<authz:authentication operation="firstName"/>!</content>
    <meta name="menu" content="MainMenu"/>
</head>

<div class="separator"></div>


<ul class="glassList">
<authz:authorize ifAnyGranted="${annotatorRoles}">
<li>
        <a href="<c:url value="${annotatorGUIURL}"/>"><fmt:message key="menu.annotatorGUI"/></a>
    </li>
</authz:authorize> 
    <li>
        <a href="<c:url value="/editProfile.html"/>"><fmt:message key="menu.user"/></a>
    
    </li>
   <authz:authorize ifAnyGranted="${projectAccessRoles}">
     
     <li>
        <a href="<c:url value="/loadProject.html?method=load"/>">
		<fmt:message key="menu.workflow.create.process.instance"/>
	  </a>
	   </li>
	   <!-- 
     <li>
     <a href="<c:url value="/processInstanceList.html?method=listByUser"/>">
		<fmt:message key="menu.workflow.myProcessInstances"/>
	  </a>
	  </li>
	   
	  <li>
        <a href="<c:url value="/projects.html"/>"><fmt:message key="menu.workflow.myProjects"/></a>
     </li>
     -->
</authz:authorize>      
<!-- 
      <authz:authorize ifAnyGranted="curator">
    <c:if test="${pendingTasksNo!='0'}">
    <li>
        <a href="<c:url value="/taskInstanceList.html?method=listByActor"/>">
		<fmt:message key="menu.workflow.myTask.instances"/>
		[<c:out value="${pendingTasksNo}"/>]
	  </a>
    </li>
    </c:if>
    <c:if test="${pendingTasksNo=='0'}">
    <li>
		<fmt:message key="menu.workflow.myTask.instances"/>
		[<c:out value="${pendingTasksNo}"/>]
    </li>
    </c:if>
     <c:if test="${groupTasksNo!='0'}">
    <li>
        <a href="<c:url value="/taskInstanceList.html?method=list"/>">
		<fmt:message key="menu.workflow.groupTask.instances"/>
		[<c:out value="${groupTasksNo}"/>]
	  </a>
    </li>
    </c:if>
    <c:if test="${groupTasksNo=='0'}">
    <li>
		<fmt:message key="menu.workflow.groupTask.instances"/>
		[<c:out value="${groupTasksNo}"/>]
    </li>
    </c:if>

</authz:authorize> 
 -->


</ul>
