<%@ include file="/common/taglibs.jsp"%>

<head>
<title><fmt:message key="processInstanceForm.title"/></title>
<content tag="heading"><fmt:message key="processInstanceForm.heading"/></content>
<meta name="menu" content="ProjectMenu"/>
  <script type='text/javascript' src="<c:url value='/dwr/interface/docServiceDetailDWRManager.js'/>"></script>
   <script type="text/javascript" src="<c:url value='/dwr/engine.js' />"></script>
        <script type="text/javascript" src="<c:url value='/dwr/util.js' />"></script>
 
<SCRIPT LANGUAGE="JavaScript">

function reload(itemToSelect){
	
	var myDropdownList=document.getElementById("corpusIdBox");
	for (iLoop = 0; iLoop< myDropdownList.options.length; iLoop++)
    {    
      if (myDropdownList.options[iLoop].value == itemToSelect)
      {
        // Item is found. Set its selected property, and exit the loop
        myDropdownList.options[iLoop].selected = true;
        break;
      }
    }
}
  function updateList(itemToSelect){
	    
		docServiceDetailDWRManager.listCorpora(loadTotal);
		alert(itemToSelect);
		var myDropdownList=document.getElementById("corpusId");
		for (iLoop = 0; iLoop< myDropdownList.options.length; iLoop++)
	    {    
	      if (myDropdownList.options[iLoop].value == itemToSelect)
	      {
	        // Item is found. Set its selected property, and exit the loop
	        myDropdownList.options[iLoop].selected = true;
	        break;
	      }
	    }
		return true;
	}

	function loadTotal(data){
		alert("before remove");
		DWRUtil.removeAllOptions("corpusIdBox");
		alert("before add");
		DWRUtil.addOptions("corpusIdBox",data);
	}  
</SCRIPT>
</head>

<html:form action="saveProcessInstance" method="post" styleId="processInstanceForm" onsubmit="return validateProcessInstanceForm(this)">
<input type="hidden" name="projectId" value="<c:out value="${param.projectId}"/>"/>

<c:set var="buttons">

        <html:submit styleClass="button" property="method.save" onmouseover="ajax_showTooltip('ajaxtooltip/info/saveAndStartInstanceBtn.jsp',this);return false" onmouseout="ajax_hideTooltip()" >
            <fmt:message key="button.saveAndStart"/>
        </html:submit>
        
      <input type="button" class="button" onclick="location.href='<html:rewrite forward="viewAllProjects" />'"
        value="<fmt:message key="button.backToProjects"/>"
        onmouseover="ajax_showTooltip('ajaxtooltip/info/doneBtn.jsp',this);return false" onmouseout="ajax_hideTooltip()"/>

</c:set>



<c:out value="${buttons}" escapeXml="false" />
<br/><br/>
<table cellpadding="0" cellspacing="10" width="90%" class="table">
    <tr>
    <td style="vertical-align:top">
    <executive:label styleClass="desc" key="processInstanceForm.name"/>
        <html:errors property="name"/>
        <html:text property="name" styleId="name" styleClass="text medium" size="20" maxlength="50"/>
	<br/><br/>
    <executive:label styleClass="desc" key="processInstanceForm.corpus"/>
         <html:select styleId="corpusIdBox" property="corpusId" styleClass="wfSelect">
                    <html:options collection="corpusList"
                        property="corpusID" labelProperty="corpusName"/>
                </html:select>
         <br/><br/>   <br/><br/>     
         <executive:customPopup name="Add Corpus" path="/popupEditUploadCorpus.html?method=Add" />           
    </td>
        
    
    <td style="vertical-align:top">
        <executive:label styleClass="desc" key="processInstanceForm.managers"/>
         <html:select property="manager" styleClass="wfSelect">
                    <html:options collection="managerList"
                        property="username" labelProperty="username"/>
                </html:select>
            
    </td>

    <% if(request.getAttribute("annotatorList")!=null){
    %>    
    <td style="vertical-align:top">
        <executive:label styleClass="desc" key="processInstanceForm.annotators"/>
         <html:select property="annotators" styleClass="wfSelect" multiple="true" size="10">
                    <html:options collection="annotatorList"
                        property="username" labelProperty="username"/>
                </html:select>
                <img src="<c:url value="/images/iconHelp.gif"/>" onmouseover="ajax_showTooltip('ajaxtooltip/info/holdCtrlKey.jsp',this);return false" onmouseout="ajax_hideTooltip()" class="icon" />
            
    </td>
    <% } %>
    </tr>        
</table>

    <c:out value="${buttons}" escapeXml="false" />
</html:form>

<script type="text/javascript">
    Form.focusFirstElement($("processInstanceForm"));
</script>

<html:javascript formName="processInstanceForm" cdata="false" dynamicJavascript="true" staticJavascript="false"/>
<script type="text/javascript" src="<html:rewrite page="/scripts/validator.jsp"/>"></script>
