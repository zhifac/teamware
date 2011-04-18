<%@ page import="gleam.executive.workflow.util.*" %>

<%@ include file="/common/taglibs.jsp"%>
<head>
<%if(request.getAttribute("wizard")!=null){%>
<title><fmt:message key="workflowConfigurationStep.title"/></title>
<content tag="heading"><fmt:message key="workflowConfigurationStep.heading"/>: "<%=request.getAttribute("taskName")%>"</content>
<%} else {%>
<title><fmt:message key="workflowTaskInstance.title"/></title>
<content tag="heading"><fmt:message key="workflowTaskInstance.heading"/>: "<%=request.getAttribute("taskName")%>"</content>
<%} %>

<meta name="menu" content="ProjectMenu"/>
	<SCRIPT LANGUAGE="JavaScript" SRC="<c:url value='/scripts/calendar.js'/>"></SCRIPT>
	<SCRIPT LANGUAGE="JavaScript">
	var cal = new CalendarPopup();
	</SCRIPT>
</head>
  
<form method="post" name="fileUploadForm" enctype="multipart/form-data" action="/<%=application.getAttribute("webappname")%>/taskInstanceList.html?method=save" >

<input type="hidden" name="taskInstanceId" value="<%=request.getAttribute("taskInstanceId")%>">
<input type="hidden" name="transition" value="">
<bean:define id="taskInstanceId" name="taskInstanceId" type="java.lang.String" />

<c:set var="buttons">
<%if(request.getAttribute("wizard")!=null){%>
    <logic:greaterThan  name="transitionsNum" value="0">  
      <logic:iterate id="availableTransition" name="taskTransitions">         
        <INPUT TYPE="submit" onclick="document.forms[0].transition.value = '<%= availableTransition%>'" title="<%=availableTransition %>"  VALUE="<%=availableTransition %>" />
      </logic:iterate>
 </logic:greaterThan>
 <%} else { %>
  <logic:greaterThan  name="transitionsNum" value="0">  
      <logic:iterate id="availableTransition" name="taskTransitions">        
        <INPUT TYPE="submit" onclick="return confirmDialog('You are about to finish the task. Are you sure you want to do this?');document.forms[0].transition.value = '<%= availableTransition%>'" title="<%=availableTransition %>"  VALUE="<%=availableTransition %>" />
      </logic:iterate>
 </logic:greaterThan>
 <%} %>
</c:set>

<c:set var="helpButtons">

 <input type="button"
      value="<fmt:message key="button.help"/>"
      onmouseover="ajax_showLocalTooltip('helpContent',this);return false" onmouseout="ajax_hideTooltip()" style="float:right"
      />
      <%if(request.getAttribute("wizard")!=null){%>
 <input type="button"
        value="<fmt:message key="button.quit"/>"
        onmouseover="ajax_showTooltip('ajaxtooltip/info/quitWizardBtn.jsp',this);return false" onmouseout="ajax_hideTooltip()"
        onclick="workflowTemplateQuitConfirmation()" style="float:right"/>
<%} else { %>

   <input type="button" onClick="history.go(-1)"
        value="<fmt:message key="button.done"/>"
        onmouseover="ajax_showTooltip('ajaxtooltip/info/doneBtn.jsp',this);return false" onmouseout="ajax_hideTooltip()"
        style="float:right"/>
<%} %>
</c:set>

<p>
<c:out value="${buttons}" escapeXml="false" />
<c:out value="${helpButtons}" escapeXml="false" />
</p>
<p></p>

<table cellpadding="2" cellspacing="2" width="100%" border="0">
<tr><td valign="top" width="50%">
<table cellpadding="0" class="table" cellspacing="0" id="taskVariables">
	<logic:iterate id="formParam" name="taskFormParameters">
	<bean:define id="label" name="formParam" property="label" type="java.lang.String" />
	<bean:define id="required" name="formParam" property="required" type="java.lang.Boolean" />
	<bean:define id="readOnly" name="formParam" property="readOnly" type="java.lang.Boolean" />
	
	    <tr>   
	       <% if(WorkflowUtil.isHidden(label)) { // check if it is file %>
      		  <input type="hidden" name="<bean:write name="formParam" property="label" />" value="<%=WorkflowUtil.getLabel(label)%>"/>      
	       <% } else if(WorkflowUtil.isFile(label)) { // check if it is file %>
      		  <td><%=WorkflowUtil.getLabel(label)%>:<%if(required) { %><span class="req">*</span><%}%></td>
	       	  <td><input type="file" name="file1" class="file large" /></td>
	       <% }else if(WorkflowUtil.isLabel(label)){  // check if it label %>
       		  <td><%=WorkflowUtil.getLabel(label)%>:</td>
       		    <td>
       		    <c:choose>
       				<c:when test="${formParam.value == null || formParam.value == ''}">
   				      	N/A
   				     </c:when>
	       			<c:otherwise>
			           <bean:write name="formParam" property="value"/>
			        </c:otherwise>
   				</c:choose>
       		  </td>  
       	   <% }else if(WorkflowUtil.isSection(label)){  // check if it label %>
       		  <td colspan="2"><strong><%=WorkflowUtil.getLabel(label)%>:<strong></td> 	  
	       <% }else if(WorkflowUtil.isTextArea(label)){  // check if it is text area %>
       		  <td><%=WorkflowUtil.getLabel(label)%>:<%if(required) { %><span class="req">*</span><% } %></td>
       		  <td>
       		  <bean:define id="textareaValue" name="formParam" property="value" type="java.lang.String"/>
       		  <executive:customTextarea name="<%=label%>" value="<%=textareaValue%>" readOnly="<%=readOnly%>" rows="3" cols="20"/>
       		  </td>     
	       <% }else if(WorkflowUtil.isCombo(label)) { // check if it is combo box 
     	       String[] methodSignatureArray = WorkflowUtil.getComboBoxMethodSignature(label);
     	   %>
      		  <td><%=WorkflowUtil.getLabel(label)%>:<%if(required) { %><span class="req">*</span><% } %></td>
              <td>
         	    <c:choose>
       				<c:when test="${formParam.value != null}">
   				      	<bean:define id="selectedOption" name="formParam" property="value" type="java.lang.String"/>
   				      <executive:customSelect name="<%=label%>" task="<%=taskInstanceId%>" multiple="<%=String.valueOf(WorkflowUtil.isMultiBox(label))%>" method="<%=methodSignatureArray[0]%>" params="<%=methodSignatureArray[1]%>" selected="<%=selectedOption%>" clazz="wfSelect" readOnly="<%=readOnly%>" />
			        </c:when>
	       			<c:otherwise>
			           <executive:customSelect name="<%=label%>" task="<%=taskInstanceId%>" multiple="<%=String.valueOf(WorkflowUtil.isMultiBox(label))%>" method="<%=methodSignatureArray[0]%>" params="<%=methodSignatureArray[1]%>" clazz="wfSelect" readOnly="<%=readOnly%>"/>
			        </c:otherwise>
   				</c:choose>
				</td>	
			<% }else if(WorkflowUtil.isLink(label)) {
			    // check if it is link 	
	  			// String linkParamsString = WorkflowUtil.getLinkParamsString(label);%>
	  			<td><%=WorkflowUtil.getLabel(label)%>:</td>
       		    <td>
       		    <c:choose>
       				<c:when test="${formParam.value == null || formParam.value == ''}">
   				      	N/A
   				     </c:when>
	       			<c:otherwise>
			           <bean:write name="formParam" property="value"/>
			        </c:otherwise>
   				</c:choose>
       		  </td>  
         	<% }else if(WorkflowUtil.isURL(label)) { 
         	String urlParamsString = WorkflowUtil.getURLParamsString(label);
         	String urlPath = WorkflowUtil.getURLPath(label);
         	%>
  			<td><%=WorkflowUtil.getLabel(label)%>:</td>
	            <td>
	         	  <executive:customURL name="<%=WorkflowUtil.getLabel(label)%>" path="<%=urlPath%>" params="<%=urlParamsString%>" task="<%=taskInstanceId%>"/>
       		  </td>	
         	<% }else if(WorkflowUtil.isPopUp(label)) { // check if it is Popup 
         	 String popupPath = WorkflowUtil.getPopupPath(label);%>
     	 
  			<td><%=WorkflowUtil.getLabel(label)%>:</td>
            <td>
               <executive:customPopup name="<%=WorkflowUtil.getLabel(label)%>" path="<%=popupPath%>" />
			        
            
         	 </td>	
            <% }else if(WorkflowUtil.isCheckBox(label)) { // check if it is CheckBox %>
  			<td><%=WorkflowUtil.getLabel(label)%>:<%if(required) { %><span class="req">*</span><% } %></td>
            <td>
             <c:choose>
       				<c:when test="${formParam.value == 'on'}">
   				       <executive:customCheckbox name="<%=label%>" value="on" readOnly="<%=readOnly%>" />
			       </c:when>
	       			<c:otherwise>
			           <executive:customCheckbox name="<%=label%>" readOnly="<%=readOnly%>" />
			        </c:otherwise>
   				</c:choose>
              
         	</td>	
         	<% }else{ %>
	       		<td><bean:write name="formParam" property="label"/>: <%if(required) { %><span class="req">*</span><%}%></td>
				<c:if test="${formParam.booleanValue == true}">	
				<td>
					<c:choose>
	       				<c:when test="${formParam.booleanValue == true}">
		         				<input type="radio"  name="<bean:write name="formParam" property="label" />" CHECKED value="true"> True &nbsp;
								<input type="radio"  name="<bean:write name="formParam" property="label"/>" value="false">False</td>   
	        			</c:when>
		       			<c:otherwise>
			        			<input type="radio"  name="<bean:write name="formParam" property="label" />" value="true"> True &nbsp;
								<input type="radio"  name="<bean:write name="formParam" property="label" />" CHECKED value="false">False</td>   
		        		</c:otherwise>
	   				</c:choose>
			    </c:if>   
			    <c:if test="${formParam.booleanValue != true && formParam.dateValue != true}">	 
			       <td><input type="text" name="<bean:write name="formParam" property="label"/>" size="30"  maxlength="150"   value="<bean:write name="formParam" property="value"/>" ></td>
			    </c:if> 
			    <c:if test="${formParam.dateValue == true}">	
			       <td><input type="text" name="<bean:write name="formParam" property="label"/>" size="10"  maxlength="10"   value="<bean:write name="formParam" property="value"/>" >
				       <A HREF="#"
							   onClick="cal.select(document.forms['variblesForm'].<bean:write name="formParam" property="label"/> ,'anchor1','MM/dd/yyyy'); return false;"
							   NAME="anchor1" ID="anchor1">select
					 </A>
			       </td>
			    </c:if> 
		    <%} %>
	   </tr>

	   <div id="helpContent" style="display:none">
	   <h3><%=request.getAttribute("taskName")%></h3>
	   <%=request.getAttribute("taskDescription")%>
	   </div>
	</logic:iterate>
</table>
<c:out value="${buttons}" escapeXml="false" />
</td>
<td valign="top">
<executive:processimage task="<%=taskInstanceId%>" />
</td>
</tr>
</table>


</form>


