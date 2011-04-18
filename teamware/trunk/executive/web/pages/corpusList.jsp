<%@ include file="/common/taglibs.jsp"%>
<head>
<title><fmt:message key="corpusList.title"/></title>
<content tag="heading"><fmt:message key="corpusList.heading"/></content>
<meta name="menu" content="ResourceMenu"/>
</head>
<c:set var="buttons">
    <input type="button" style="margin-right: 5px"
        onclick="location.href='<html:rewrite forward="addCorpus"/>'"
        value="<fmt:message key="button.add"/>"
        onmouseover="ajax_showTooltip('ajaxtooltip/info/addCorpusBtn.jsp',this);return false" onmouseout="ajax_hideTooltip()" 
        />
    <input type="button" style="margin-right: 5px"
        onclick="location.href='<c:url value="/datastore.html?method=downloadDS&disableGzip=true"/>'"
        value="<fmt:message key="button.downloadDS"/>"
        onmouseover="ajax_showTooltip('ajaxtooltip/info/downloadDS.jsp',this);return false" onmouseout="ajax_hideTooltip()" 
        />

</c:set>

<c:out value="${buttons}" escapeXml="false"/>

<html:form action="corpora" method="post" styleId="corporaForm">
<input type="hidden" id="method" name="method" value="deleteMultipleCorpora"/>

<display:table name="corpusList" cellspacing="0" cellpadding="0"
    id="corpusList" pagesize="50" class="table"
    export="true" requestURI="" sort="list" defaultsort="1" decorator="gleam.executive.webapp.displaytag.CorpusListDecorator">

    <display:column sortable="true" headerClass="sortable"  titleKey="corpusForm.corpusName" media="html">
    <a id="<c:out value="${corpusList.corpusName}"/>" href="<c:url value="/documentsInCorpus.html?method=search&corpusID=${corpusList.corpusID}&corpusName=${corpusList.corpusName}"/>">
      <c:out value="${corpusList.corpusName}"/>    
    </a>
    </display:column>  
     <display:column sortable="true" headerClass="sortable"  titleKey="corpusForm.corpusName" media="pdf csv">
     <c:out value="${corpusList.corpusName}"/>    
    </display:column>  
    <display:column sortable="true" style="width: 3%" headerClass="sortable"  titleKey="corpusForm.numberOfDocuments">
     <c:out value="${corpusList.numberOfDocuments}"/>    
    </display:column>
    <display:column sortable="true" headerClass="sortable" titleKey="corpus.upload.uploader">
    	<c:out value="${corpusList.uploader}"/>
    </display:column>


<%--The link to rename the corpus name, which can be commented out when the datastore supports the feature
    <display:column style="width: 5%" media="html csv excel xml">
    <a id="<c:out value="${corpusList.corpusName}"/>" href="<c:url value="/corpora.html?method=edit&corpusID=${corpusList.corpusID}&corpusName=${corpusList.corpusName}"/>">
    <img src="<c:url value="/images/edit.gif"/>" onmouseover="ajax_showTooltip('ajaxtooltip/info/editCorpusBtn.jsp',this);return false" onmouseout="ajax_hideTooltip()" class="icon" />
    </a>
    </display:column>
--%>
 <authz:authorize ifAnyGranted="${processInstancesUsingRoles}">
    <display:column style="width: 25%" title="Projects" property="projects" media="html" sortable="true" headerClass="sortable">
    </display:column>
     <display:column style="width: 25%" title="Projects" property="projectsWithoutLinks" media="pdf csv" sortable="true" headerClass="sortable">
    </display:column>
    <display:column style="width: 3%" property="startProjectLink" title="" media="html"/>  
   </authz:authorize>   
    <display:column style="width: 3%" media="html">
    <a id="<c:out value="annic${corpusList.corpusName}"/>" href="<c:url value="/annicgui.html?docservice-url=${docServiceURL}&corpus-id=${corpusList.corpusID}&autoconnect=true"/>">
    <img src="<c:url value="/images/annic.png"/>" onmouseover="ajax_showTooltip('ajaxtooltip/info/annicSearch.jsp',this);return false" onmouseout="ajax_hideTooltip()" class="icon" />
    </a>
    </display:column>
    <display:column style="width: 3%" media="html">
    <a id="<c:out value="download${corpusList.corpusName}"/>" href="<c:url value="/corpus.html?method=downloadCorpus&corpusID=${corpusList.corpusID}&disableGzip=true"/>">
    <img src="<c:url value="/images/download.gif"/>" onmouseover="ajax_showTooltip('ajaxtooltip/info/downloadCorpus.jsp',this);return false" onmouseout="ajax_hideTooltip()" onclick="bCancel=true" />
    </a>
    </display:column>
    
    <display:column style="width: 15%" property="checkBox" media="html"
        title="<input type=\"checkbox\" name=\"allbox\" onclick=\"checkAll(document.forms[0])\">  
        <input name=\"toDelete\" id=\"toDelete\" type=\"submit\" value=\"Delete\"
            alt=\"All selected corpora will be deleted from the datastore permanently\"
            onmouseover=\"ajax_showTooltip('ajaxtooltip/info/deleteCorpus.jsp',this);return false\" 
            onmouseout=\"ajax_hideTooltip()\"
            onclick=\"bCancel=true;  return anyChecked(document.forms[0]) && confirmDialog('Are you sure you want to delete all the selected corpora from the datastore?')\"/>  
    ">
    
     </display:column>
    
    <display:setProperty name="paging.banner.item_name" value="corpus"/>
    <display:setProperty name="paging.banner.items_name" value="corpora"/>
    <display:setProperty name="export.csv.filename" value="Corpora.csv"/>
    <display:setProperty name="export.pdf.filename" value="Corpora.pdf"/>
</display:table>
</html:form>
<c:out value="${buttons}" escapeXml="false"/>

<script type="text/javascript">
setMasterCheckbox(document.forms[0], "allbox");
    highlightTableRows("corpusList");
</script>
