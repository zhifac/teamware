<%@ include file="/common/taglibs.jsp"%>
<head>
<title><fmt:message key="annoSchemaList.title"/></title>
<content tag="heading"> 
<fmt:message key="annoSchemaList.heading"/>

</content>
<meta name="menu" content="ResourceMenu"/>
</head>


<html:form action="schemas" method="post" styleId="annoSchemaForm">
<input type="hidden" id="method" name="method" value="delete"/>

<c:set var="buttons">
    <input type="button" style="margin-right: 5px"
        onclick="location.href='<html:rewrite forward="addSchema"/>'"
        value="<fmt:message key="button.uploadSchema"/>"
        onmouseover="ajax_showTooltip('ajaxtooltip/info/addSchemaBtn.jsp',this);return false" onmouseout="ajax_hideTooltip()"   
     />
    <input type="button" onclick="location.href='<c:url value="/mainMenu.html"/>'"
    	onmouseover="ajax_showTooltip('ajaxtooltip/info/doneBtn.jsp',this);return false" onmouseout="ajax_hideTooltip()"
        value="<fmt:message key="button.backToMainPage"/>"
    />
</c:set>
<c:out value="${buttons}" escapeXml="false"/>
<display:table name="schemaList" cellspacing="0" cellpadding="0"
    id="schemaList" pagesize="25" class="table" requestURI=""
    sort="list" export="true" defaultsort="1" decorator="gleam.executive.webapp.displaytag.AnnotationSchemaListDecorator">

    <display:column style="width: 60%" titleKey="annoSchemaForm.schemaName" sortable="true" headerClass="sortable" escapeXml="false" media="html">
      <a href="<c:out value='${urlbase}'/>/<c:out value='${instancename}'/>/schemas/<c:out value='${schemaList.name}'/>" onmouseover="ajax_showTooltip('ajaxtooltip/info/downloadSchema.jsp',this);return false" onmouseout="ajax_hideTooltip()" onclick="bCancel=true"  target="_blank">
       <c:out value='${schemaList.name}'/>
      </a>
    </display:column>
    <display:column style="width: 60%" titleKey="annoSchemaForm.schemaName" sortable="true" headerClass="sortable" escapeXml="true" media="csv pdf">
       <c:out value='${schemaList.name}'/>
    </display:column>
    <authz:authorize ifAnyGranted="${processInstancesUsingRoles}">
    <display:column style="width: 25%" title="Projects" property="projects" media="html" sortable="true" headerClass="sortable">
    </display:column>
    <display:column style="width: 25%" title="Projects" property="projectsWithoutLinks" media="pdf csv" headerClass="sortable">
    </display:column>
    </authz:authorize>  
    <display:column style="width: 15%" property="checkBox" media="html"
        title="<input type=\"checkbox\" name=\"allbox\" onclick=\"checkAll(document.forms[0])\">  
        <input name=\"toDelete\" id=\"toDelete\" type=\"submit\" value=\"Delete\"
            alt=\"All the selected schemas above will be deleted permanently\"
            onmouseover=\"ajax_showTooltip('ajaxtooltip/info/deleteSchemas.jsp',this);return false\" 
            onmouseout=\"ajax_hideTooltip()\"
            onclick=\"bCancel=true;  return anyChecked(document.forms[0]);confirmDialog('Are you sure you want to delete all selected schema(s)?')\"/>  
    ">
    </display:column>
    
    <display:setProperty name="paging.banner.item_name" value="schema"/>
    <display:setProperty name="paging.banner.items_name" value="schemas"/>
    <display:setProperty name="export.csv.filename" value="Schemas.csv"/>
    <display:setProperty name="export.pdf.filename" value="Schemas.pdf"/>
</display:table>
<c:out value="${buttons}" escapeXml="false"/>

</html:form>


<script type="text/javascript">
    highlightTableRows("schemaList");
    setMasterCheckbox(document.forms[0], "allbox");
</script>
