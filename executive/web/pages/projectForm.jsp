<%@ include file="/common/taglibs.jsp"%>


<head>
<title><fmt:message key="project.title"/></title>
<content tag="heading"><fmt:message key="project.heading"/></content>
<meta name="menu" content="ProjectMenu"/>
</head>

<fmt:message key="project.message"/>
<html:form action="saveProject" enctype="multipart/form-data" method="post" styleId="projectForm" onsubmit="return validateProjectForm(this)">
<input type="hidden" name="oldProjectName" value="<c:out value="${param.name}"/>"/>
<ul>

    <li>
        <executive:label styleClass="desc" key="projectForm.name"/>
        <html:errors property="name"/>
        <html:text property="name" styleId="name" styleClass="text large" size="20" maxlength="20"/>
    </li>
    
    <li class=info">
     <p><fmt:message key="projectForm.descriptionInfo"/></p>

</li>
    <li>
        <executive:label styleClass="desc" key="projectForm.description"/>
        <html:errors property="description"/>
        <html:textarea property="description" rows="10" cols="50" />
    </li>
    
    <%if(request.getParameter("skipUpload")==null && request.getAttribute("skipUpload")==null ){%>
    <li>
  		<executive:label key="projectForm.file" styleClass="desc"/>
  		<html:errors property="file"/>
  		<html:file styleId="file" property="file" styleClass="file large"/>
  	</li>
  	<li>
  	    <executive:label styleClass="desc" key="projectForm.owner"/>
         <html:select property="userId" styleClass="wfSelect">
                    <html:options collection="managerList"
                        property="id" labelProperty="username"/>
                </html:select>
  	</li>
  	<li>
  		<executive:label key="projectForm.version" styleClass="desc"/>
  		<html:select property="version" styleClass="select">
         <html:option value="0">0</html:option>  
         <html:option value="1">1</html:option>  
         <html:option value="2">2</html:option> 
         <html:option value="3">3</html:option> 
         <html:option value="4">4</html:option> 
         <html:option value="5">5</html:option> 
         <html:option value="6">6</html:option> 
         <html:option value="7">7</html:option> 
         <html:option value="8">8</html:option> 
         <html:option value="9">9</html:option>
         <html:option value="10">10</html:option>
            
         </html:select>
  	</li>
  	 <li>
  		<executive:label key="projectForm.enabled" styleClass="desc"/>
  	<html:checkbox styleClass="checkbox" property="enabled" styleId="enabled"/>
     </li>       
  	
  	<%}%>
      
	  <html:hidden property="id"/>
    <li class="buttonBar bottom">
         <%if(request.getParameter("skipUpload")==null && request.getAttribute("skipUpload")==null){%>
        
        <html:submit styleClass="button" property="method.save" onclick="bCancel=false" onmouseover="ajax_showTooltip('ajaxtooltip/info/editProject.jsp',this);return false" onmouseout="ajax_hideTooltip()" >
            <fmt:message key="button.save"/>
        </html:submit>
        <%} else { %>
       <html:submit styleClass="button" property="method.create" onclick="bCancel=false" onmouseover="ajax_showTooltip('ajaxtooltip/info/addProjectBtn.jsp',this);return false" onmouseout="ajax_hideTooltip()" >
            <fmt:message key="button.go"/>
        </html:submit>
         <%}%>
        <html:cancel styleClass="button" onclick="bCancel=true" onmouseover="ajax_showTooltip('ajaxtooltip/info/cancelBtn.jsp',this);return false" onmouseout="ajax_hideTooltip()" >
            <fmt:message key="button.cancel"/>
        </html:cancel>
    </li>
</ul>
</html:form>

<script type="text/javascript">
    Form.focusFirstElement($("projectForm"));
</script>

<html:javascript formName="projectForm" cdata="false" dynamicJavascript="true" staticJavascript="false"/>
<script type="text/javascript" src="<html:rewrite page="/scripts/validator.jsp"/>"></script>
