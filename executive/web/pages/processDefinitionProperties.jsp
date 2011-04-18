<%@ include file="/common/taglibs.jsp"%>



<head>
<title><fmt:message key="workflowSwimlane.title"/></title>
<content tag="heading">
	<fmt:message key="workflowSwimlane.heading"/>
	for Process <c:out value="${processDefinition}" escapeXml="false"/>
</content>
<meta name="menu" content="ProjectMenu"/>
</head>


<html:form action="processDefinitionProperties.html?method=save" method="post">
<input type="hidden" name="processDefinitionId" value="<%=request.getAttribute("processDefinitionId")%>">

<c:set var="buttons">
    <input type="button" onclick="location.href='<html:rewrite forward="processDefinitionList"/>'"
        value="<fmt:message key="button.backToProcessDefinitionList"/>"
        onmouseover="ajax_showTooltip('ajaxtooltip/info/doneBtn.jsp',this);return false" onmouseout="ajax_hideTooltip()"
     />
     <INPUT TYPE ="submit" onclick="" title='<fmt:message key="button.createInstance"/>'  VALUE='<fmt:message key="button.createInstance"/>' 
		onmouseover="ajax_showTooltip('ajaxtooltip/info/createInstanceBtn.jsp',this);return false" onmouseout="ajax_hideTooltip()"
	>
</c:set>


<p>
<c:out value="${buttons}" escapeXml="false"/>
</p>
<table cellpadding="0" class="table" cellspacing="0" id="swimlaneList">
<thead>
<tr>
<th><fmt:message key="workflowSwimlane.id"/></th>
<th><fmt:message key="workflowSwimlane.name"/></th>
<th><fmt:message key="workflowSwimlane.possibleActors"/></th>
</tr>
</thead>
<tbody>
<% int i = 0 ; 
String cls;%>
<logic:present name="processDefPropertiesForm" property="swimlanes">
<logic:iterate  name="processDefPropertiesForm" property="swimlanes" id="swimlane" indexId="indx" >
<c:set var="swimlaneId" value="${swimlane.id}"/>
<% if (i++ % 2 == 0) {
	cls = "odd";
} else {
	cls = "even";
} %>
<tr class="<%=cls%>">
	<td   style="width: 5%">		
		<bean:write name="swimlane" property="id"/>
	</td>
	<td   style="width: 35%">		
		<bean:write name="swimlane" property="name"/>
	</td>
	<td style="width: 60%">
		<select name="performer_<c:out value="${swimlaneId}" escapeXml="false" />" multiple="multiple" size="7" indexed="true" >
			<logic:iterate  name="swimlane" property="possiblePerformers" id="performer" >
				<bean:define id="pName" name="performer" property="username"/>
				<%boolean displayed=false;%>
				<logic:iterate name="swimlane" property="inPooledActors" id="actor" >
					<bean:define id="ppName" name="actor" property="username"/>
					<%if(pName.equals(ppName)){%>		 
						<option value="<bean:write name="performer" property="username"/>" selected>
							<bean:write name="performer" property="username"/>
						</option>
					<%displayed=true;}%>
				</logic:iterate>
				<%if(!displayed){%>
					<bean:define id="actorName" name="swimlane" property="actor"/>
					<%if(pName.equals(actorName)){%>
						<option value="<bean:write name="performer" property="username"/>" selected>
							<bean:write name="performer" property="username"/>
						</option>
					<%}else{%>
						<option value="<bean:write name="performer" property="username"/>" >
							<bean:write name="performer" property="username"/>
						</option>
					<%}%>
				<%}%>
				</logic:iterate>	
		</select>
	</td>	
</tr>

</logic:iterate>
</logic:present>
<tr>
<td colspan="2" style="text:align:right">
<fmt:message key="workflowProcessInstance.key"/>:
</td>
<td>
<input type="text" name="key" value="">
</td>
</tr>
</tbody>

</table>

	
	<c:out value="${buttons}" escapeXml="false"/>

</html:form>