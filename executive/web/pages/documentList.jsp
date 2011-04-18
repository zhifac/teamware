<%@ include file="/common/taglibs.jsp"%>
<head>
<title><fmt:message key="documentList.title"/></title>
<content tag="heading">Documents in Corpus: "<c:out value="${param.corpusName}" escapeXml="false"/>"
<br/>

</content>
<meta name="menu" content="ResourceMenu"/>
</head>



<html:form action="documentsInCorpus" method="post" styleId="documentForm">
<input type="hidden" id="method" name="method" value="deleteMultipleDocs"/>
<input type="hidden" id="corpusID" name="corpusID" value='<c:out value="${requestScope['corpusID']}"/>'/>
<input type="hidden" id="corpusName" name="corpusName" value='<c:out value="${requestScope['corpusName']}"/>'/>

<c:set var="buttons">
    <input type="button" style="margin-right: 5px"
        onclick="location.href='<c:url value="/editUploadFile.html?method=edit&corpusID=${requestScope['corpusID']}&corpusName=${requestScope['corpusName']}"/>'"
        value="<fmt:message key="button.uploadDocuments"/>"
        onmouseover="ajax_showTooltip('ajaxtooltip/info/addDocumentBtn.jsp',this);return false" onmouseout="ajax_hideTooltip()"   
     />
    <input type="button" onclick="location.href='<c:url value="/corpora.html"/>'"
    	onmouseover="ajax_showTooltip('ajaxtooltip/info/doneBtn.jsp',this);return false" onmouseout="ajax_hideTooltip()"
        value="<fmt:message key="button.backToCorpusList"/>"
    />
   
</c:set>
<c:out value="${buttons}" escapeXml="false"/>
<display:table name="documentList" cellspacing="0" cellpadding="0"
    id="documentList" pagesize="50" class="table documentList" requestURI=""
    export="true" sort="list" defaultsort="1" decorator="gleam.executive.webapp.displaytag.DocumentListDecorator">

    <display:column style="width: 65%" property="documentName" sortable="true" headerClass="sortable"
         titleKey="documentForm.documentName"/>
     <display:column style="width: 5%" url="/viewDocument.html" paramId="documentId" paramProperty="documentID" media="html">  
         <img src="<c:url value="/images/view.gif"/>" onmouseover="ajax_showTooltip('ajaxtooltip/info/viewDocument.jsp',this);return false" onmouseout="ajax_hideTooltip()" class="icon" />
     </display:column>
     
     <display:column style="width: 5%" media="html" url="/viewDocument.html?method=diff" paramId="documentId" paramProperty="documentID">  
         <img src="<c:url value="/images/annDiff.png"/>" onmouseover="ajax_showTooltip('ajaxtooltip/info/annoDiff.jsp',this);return false" onmouseout="ajax_hideTooltip()" class="icon" />
     </display:column>
    <display:column style="width: 5%" media="html">
        <a href="<c:url value="/caculateIAA.html?method=searchAnnSetNames&show=false&corpusID=${param.corpusID}&corpusName=${param.corpusName}&documentID=${documentList.documentID}&documentName=${documentList.documentName}"/>">
    	<img src="<c:url value="/images/view_completed.gif"/>" onmouseover="ajax_showTooltip('ajaxtooltip/info/iaa.jsp',this);return false" onmouseout="ajax_hideTooltip()" class="icon"/>
    	</a>
    </display:column>
    
    <display:column style="width: 15%" property="checkBox" media="html"
        title="<input type=\"checkbox\" name=\"allbox\" onclick=\"checkAll(document.forms[0])\">  
        <input name=\"toDelete\" id=\"toDelete\" type=\"submit\" value=\"Delete\"
            alt=\"All selected documents will be deleted from the datastore permanently\"
            onmouseover=\"ajax_showTooltip('ajaxtooltip/info/deleteDocuments.jsp',this);return false\" 
            onmouseout=\"ajax_hideTooltip()\"
            onclick=\"bCancel=true;  return anyChecked(document.forms[0]);confirmDialog('Are you sure you want to delete all the selected document(s) from the datastore?')\"/>  
   
    ">
    
     </display:column>

    <display:setProperty name="paging.banner.item_name" value="document"/>
    <display:setProperty name="paging.banner.items_name" value="documents"/>
    <display:setProperty name="export.csv.filename" value="Documents.csv"/>
    <display:setProperty name="export.pdf.filename" value="Documents.pdf"/>
</display:table>
<c:out value="${buttons}" escapeXml="false"/>

</html:form>


<script type="text/javascript">
setMasterCheckbox(document.forms[0], "allbox");
    highlightTableRows("documentList");
</script>
