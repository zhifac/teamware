<%@ include file="/common/taglibs.jsp"%>

<head>
    <title><fmt:message key="annotationStatus.title"/></title>
    <content tag="heading"><fmt:message key="annotationStatus.heading"/></content>
    <meta name="menu" content="ProjectMenu"/>
</head>

<script type="text/javascript">


window.onload=function(){
	setTimeout('AjaxRefresh()',3000);
}
</script>


<c:set var="buttons">
           <input type="button" style="margin-right: 5px"
        onclick="location.href='<c:url value="/annotationStatus.html?method=showOverview&id=${param.id}"/>'"
        value="<fmt:message key="button.annotationStatusOverview"/>"
        onmouseover="ajax_showTooltip('ajaxtooltip/info/annotationStatusOverviewBtn.jsp',this);return false" onmouseout="ajax_hideTooltip()"   
     />
    
      <input type="button" style="margin-right: 5px"
        onclick="location.href='<c:url value="/annotationStatus.html?method=showGlobalAnnotator&id=${param.id}&role=annotator"/>'"
        value="<fmt:message key="button.annotatorRecordOverview"/>"
        onmouseover="ajax_showTooltip('ajaxtooltip/info/annotatorRecordOverviewBtn.jsp',this);return false" onmouseout="ajax_hideTooltip()"   
     />
     
        <input type="button" style="margin-right: 5px"
        onclick="location.href='<c:url value="/processInstanceList.html?method=listTopProcessesWithTheSameKeyAndName&processInstanceId=${param.id}"/>'"
        value="<fmt:message key="button.backToProcessInstance"/>"
        onmouseover="ajax_showTooltip('ajaxtooltip/info/doneBtn.jsp',this);return false" onmouseout="ajax_hideTooltip()"/>
        
</c:set>

<c:out value="${buttons}" escapeXml="false"/>
<display:table name="annotationStatusList" cellspacing="0" cellpadding="0" requestURI="" 
    defaultsort="1" id="annotationStatus" pagesize="100" class="table" export="true" sort="list" decorator="gleam.executive.webapp.displaytag.AnnotationStatusListDecorator">
     <display:setProperty name="export.csv.decorator" value="gleam.executive.webapp.displaytag.AnnotationStatusListDecorator"/>
    <display:setProperty name="export.pdf.decorator" value="gleam.executive.webapp.displaytag.AnnotationStatusListDecorator"/>
    <display:column  style="width: 21%" property="documentName" escapeXml="true" sortable="true" titleKey="annotationStatus.documentId" url="/viewDocument.html" paramId="documentId" paramProperty="documentId"/>
    <display:column  style="width: 13%" property="takenByFormatted" sortable="true" media="html" titleKey="annotationStatus.takenByList" escapeXml="false"/>	
	<display:column  style="width: 13%" property="annotatedByFormatted" sortable="true" media="html" titleKey="annotationStatus.annotatedByList" escapeXml="false"/>
	<display:column  style="width: 13%" property="rejectedByFormatted" sortable="true" media="html" titleKey="annotationStatus.rejectedByList" escapeXml="false"/>
	<display:column  style="width: 13%" property="takenByFormattedWithoutLinks" media="pdf csv" titleKey="annotationStatus.takenByList" escapeXml="false"/>	
	<display:column  style="width: 13%" property="annotatedByFormattedWithoutLinks" media="pdf csv" titleKey="annotationStatus.annotatedByList" escapeXml="false"/>
	<display:column  style="width: 13%" property="rejectedByFormattedWithoutLinks" media="pdf csv" titleKey="annotationStatus.rejectedByList" escapeXml="false"/>
	<display:column  style="width: 10%" property="status" escapeXml="true" sortable="true" titleKey="annotationStatus.status"/>
	<display:column  style="width: 11%" property="startDateFormatted" escapeXml="true" sortable="true" titleKey="annotationStatus.startDate"/>
	<display:column  style="width: 11%" property="endDateFormatted" escapeXml="true" sortable="true" titleKey="annotationStatus.endDate" />
	<display:column  style="width: 5%" property="time" escapeXml="true" sortable="true" titleKey="annotationStatus.time"/>
	<display:column style="width: 3%" url="/viewDocument.html?method=diff" paramId="documentId" paramProperty="documentId" media="html">  
    <c:if test="${not empty annotationStatus.endDate}">
     <a href="<c:url value="/viewDocument.html?method=diff&documentId=${annotationStatus.documentId}"/>">
       <img src="<c:url value="/images/annDiff.png"/>" onmouseover="ajax_showTooltip('ajaxtooltip/info/annoDiff.jsp',this);return false" onmouseout="ajax_hideTooltip()" class="icon" />
      </a>
     </c:if>
     </display:column>    
    <display:setProperty name="paging.banner.item_name" value="document"/>
    <display:setProperty name="paging.banner.items_name" value="documents"/>
    <display:setProperty name="export.csv.filename" value="Annotation Status Details.csv"/>
    <display:setProperty name="export.pdf.filename" value="Annotation Status Details.pdf"/>
</display:table>

<c:out value="${buttons}" escapeXml="false" />

 