<%@ include file="/common/taglibs.jsp"%>
<head>
<title><fmt:message key="actorForm.title"/></title>
<content tag="heading"><fmt:message key="actorForm.heading"/></content>
<meta name="menu" content="ProjectMenu"/>
</head>
<html:form action="saveActors" method="post" styleId="actorForm" onsubmit="return validateActorForm(this)">
<input type="hidden" name="processInstanceId" value="<c:out value="${param.processInstanceId}"/>"/>
<c:set var="buttons">
 <c:choose>
 <c:when test="${not empty changeAllowed}">
 <html:submit styleClass="button" property="method.save" onmouseover="ajax_showTooltip('ajaxtooltip/info/saveProcessInstanceBtn.jsp',this);return false" onmouseout="ajax_hideTooltip()" >
            <fmt:message key="button.save"/>
        </html:submit>
  
  <html:cancel styleClass="button" onclick="bCancel=true" onmouseover="ajax_showTooltip('ajaxtooltip/info/cancelBtn.jsp',this);return false" onmouseout="ajax_hideTooltip()" >
            <fmt:message key="button.cancel"/>
        </html:cancel>
  </c:when>
  <c:otherwise>
  <input type="button" onClick="history.go(-1)"
        value="<fmt:message key="button.done"/>"
        onmouseover="ajax_showTooltip('ajaxtooltip/info/doneBtn.jsp',this);return false" onmouseout="ajax_hideTooltip()"
        />
  </c:otherwise>
  
  </c:choose>
</c:set>



<c:out value="${buttons}" escapeXml="false" />
<br/><br/>
<table cellpadding="0" cellspacing="10" width="75%" class="table" >
    <tr>
     <td style="vertical-align:top">
        <executive:label styleClass="desc" key="actorForm.managers"/>
        <% if(request.getAttribute("changeAllowed")!=null){  %>
         <html:select property="manager" styleClass="wfSelect">
                    <html:options collection="managerList"
                        property="username" labelProperty="username"/>
                </html:select>
                <% } else {%>
                <ul>
     <li><bean:write name="actorForm" property="manager" /></li>
     </ul>
     <%}%> 
    </td>
    
     
      <% 
    	 if(request.getAttribute("annotatorList")!=null){
    %> 
    <td style="vertical-align:top">
        <executive:label styleClass="desc" key="actorForm.annotators"/>
        <% if(request.getAttribute("changeAllowed")!=null){  %>
         <html:select property="annotators" styleClass="wfSelect" multiple="true" size="10">
                    <html:options collection="annotatorList"
                        property="username" labelProperty="username"/>
                </html:select>
                 <img src="<c:url value="/images/iconHelp.gif"/>" onmouseover="ajax_showTooltip('ajaxtooltip/info/holdCtrlKey.jsp',this);return false" onmouseout="ajax_hideTooltip()" class="icon" />
            <% } else {%>
            <ul>
            <logic:iterate id="ann" name="actorForm" property="annotators">
<li><bean:write name="ann" /></li>
</logic:iterate>
            </ul>
     <%}%> 
    </td>
     <% } %>
    </tr>        
</table>
        <c:out value="${buttons}" escapeXml="false" />
       
    
</html:form>

<script type="text/javascript">
    Form.focusFirstElement($("actorForm"));
</script>

<html:javascript formName="actorForm" cdata="false" dynamicJavascript="true" staticJavascript="false"/>
<script type="text/javascript" src="<html:rewrite page="/scripts/validator.jsp"/>"></script>
