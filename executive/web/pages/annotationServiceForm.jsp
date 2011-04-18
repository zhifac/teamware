<%@ include file="/common/taglibs.jsp"%>
<head>
<title><fmt:message key="annotationServiceForm.title"/></title>
<content tag="heading"><fmt:message key="annotationServiceForm.heading"/></content>
<script type="text/javascript">
  function checkSC(form){
    var popup = document.getElementById("popup").value;
    if(popup=="true"){
     form.action="popupSaveAnnotationService.html";
    }
  }
</script>
<meta name="menu" content="ResourceMenu"/>
</head>
<% 
String asKey = "";
if (request.getAttribute("asKey")!=null){
	asKey = (String)request.getAttribute("asKey");
}

String asValue = "";
if (request.getAttribute("asValue")!=null){
	asValue = (String)request.getAttribute("asValue");
}

String asExtraMappings = "";
if (request.getAttribute("asExtraMappings")!=null){
	asExtraMappings = (String)request.getAttribute("asExtraMappings");
}

String parameterKey = "";
if (request.getAttribute("parameterKey")!=null){
	parameterKey = (String)request.getAttribute("parameterKey");
}

String parameterValue = "";
if (request.getAttribute("parameterValue")!=null){
	parameterValue = (String)request.getAttribute("parameterValue");
}


%>


<html:form action="saveAnnotationService" method="post" styleId="annotationServiceForm" onsubmit="checkSC(this);return validateAnnotationServiceForm(this)">

<c:set var="buttons">
<html:submit styleClass="button" property="method.save" onclick="bCancel=false" onmouseover="ajax_showTooltip('ajaxtooltip/info/addAnnotationServiceBtn.jsp',this);return false" onmouseout="ajax_hideTooltip()" >
            <fmt:message key="button.save"/>
        </html:submit>
        
        <html:cancel styleClass="button" onclick="bCancel=true" onmouseover="ajax_showTooltip('ajaxtooltip/info/cancelBtn.jsp',this);return false" onmouseout="ajax_hideTooltip()" >
            <fmt:message key="button.cancel"/>
        </html:cancel>
</c:set>

<input type="hidden" name="oldAnnotationServiceName" value="<c:out value="${param.name}"/>"/>
<input type="hidden" name="popup" value="<c:out value="${param.popup}"/>"/>
<html:hidden property="id"/>

<c:out value="${buttons}" escapeXml="false" />
<br/><br/>
<table cellpadding="0" cellspacing="10" width="75%" class="table" >
 <tr>
 
 <td style="vertical-align:top">
        <executive:label styleClass="desc" key="annotationServiceForm.url"/>
        <html:errors property="url"/>
        <html:text property="url" styleId="url" size="50" />
    </td>
    
     <td style="vertical-align:top">
        <executive:label styleClass="desc" key="annotationServiceForm.annotationServiceType"/>
        <html:errors property="annotationServiceType"/>
        <select name="annotationServiceTypeId">
        <option value="1">gas</option>
        </select>
    </td>
        </tr>
    <tr>
    <td style="vertical-align:top">
        <executive:label styleClass="desc" key="annotationServiceForm.name"/>
        <html:errors property="name"/>
        <html:text property="name" styleId="name" size="50"/>
    </td>
 <td style="vertical-align:top">
        <executive:label styleClass="desc" key="annotationServiceForm.description"/>
        <html:errors property="description"/>
        <html:textarea property="description" rows="5" cols="39" />
        </td>
    </tr>
   
        <tr>
        <td style="vertical-align:top">
         <label for"asKey" class="desc">Annotation Set Key</label>
         <input type="text" size="50" name="asKey" value="<%=asKey%>">

         </td>
         <td style="vertical-align:top">
         <label for"asValue" class="desc">Annotation Set Value</label>
         <input type="text" size="50" name="asValue" value="<%=asValue%>">
         </td>
         </tr>
          <tr>
         <td style="vertical-align:top" colspan="2">
         <label for"asExtraMappings" class="desc">Annotation Set Extra Mappings</label>
          <input type="text" size="50" name="asExtraMappings" value="<%=asExtraMappings%>">
         </td>
         </tr>
         <tr>
         <td style="vertical-align:top">
         <label for"parameterKey" class="desc">Parameter Key</label>
         <input type="text" size="50" name="parameterKey" value="<%=parameterKey%>">   
         </td>
         <td style="vertical-align:top">
         <label for"parameterValue" class="desc">Parameter Value</label>
         <input type="text" size="50" name="parameterValue" value="<%=parameterValue%>">   
         
         </td>
        </tr>
        <tr>
         <td style="vertical-align:top" colspan="2">
         <html:checkbox property="canUsePrivateUrls" />
         <label for"canUsePrivateUrls" class="desc">Service can access the private doc service and executive URLs?</label>
         </td>        
        </tr>
   
</table> 
	  
<c:out value="${buttons}" escapeXml="false" />
</html:form>

<script type="text/javascript">
    Form.focusFirstElement($("annotationServiceForm"));
</script>

<html:javascript formName="annotationServiceForm" cdata="false" dynamicJavascript="true" staticJavascript="false"/>
<script type="text/javascript" src="<html:rewrite page="/scripts/validator.jsp"/>"></script>
