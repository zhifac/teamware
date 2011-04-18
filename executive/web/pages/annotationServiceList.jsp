<%@ include file="/common/taglibs.jsp"%>

<head>
    <title><fmt:message key="annotationService.title"/></title>
    <content tag="heading"><fmt:message key="annotationService.heading"/></content>
    <meta name="menu" content="ResourceMenu"/>
</head>

<c:set var="buttons">
    
    <input type="button" style="margin-right: 5px"
        onclick="location.href='<html:rewrite forward="addAnnotationService"/>'"
        value="<fmt:message key="button.add"/>" 
        onmouseover="ajax_showTooltip('ajaxtooltip/info/addAnnotationServiceBtn.jsp',this);return false" onmouseout="ajax_hideTooltip()"/>
     
    <input type="button" onclick="location.href='<html:rewrite forward="mainMenu" />'"
        value="<fmt:message key="button.done"/>"
        onmouseover="ajax_showTooltip('ajaxtooltip/info/doneBtn.jsp',this);return false" onmouseout="ajax_hideTooltip()"/>
</c:set>

<c:out value="${buttons}" escapeXml="false"/>

<display:table name="annotationServiceList" cellspacing="0" cellpadding="0" requestURI="" 
    defaultsort="1" id="annotationServices" pagesize="25" class="table" export="true" sort="list">
    
    <display:column style="width: 25%" property="name" escapeXml="true" sortable="true" titleKey="annotationServiceForm.name" url="/editAnnotationService.html" paramId="id" paramProperty="id"/>
    <display:column  style="width: 29%" property="description" escapeXml="true" sortable="true" titleKey="annotationServiceForm.description"/>
	
	<display:column style="width: 40%" property="url" sortable="true" titleKey="annotationServiceForm.url"/>
   
	<display:column style="width: 3%" url="/editAnnotationService.html" paramId="id" paramProperty="id" media="html">
    	<img src="<c:url value="/images/edit.gif"/>" onmouseover="ajax_showTooltip('ajaxtooltip/info/editAnnotationServiceBtn.jsp',this);return false" onmouseout="ajax_hideTooltip()"/>
    </display:column>                
    
	<display:column style="width: 3%" url="/annotationServices.html?method=delete" paramId="id" paramProperty="id" media="html">
    	<img src="<c:url value="/images/delete.gif"/>" onmouseover="ajax_showTooltip('ajaxtooltip/info/deleteAnnotationService.jsp',this);return false" onmouseout="ajax_hideTooltip()" onclick="bCancel=true; return confirmDialog('Are you sure to delete the selected annotation service?')" />
    </display:column>  

    <display:setProperty name="paging.banner.item_name" value="annotation service"/>
    <display:setProperty name="paging.banner.items_name" value="annotation services"/>

    <display:setProperty name="export.csv.filename" value="Annotation Services.csv"/>
    <display:setProperty name="export.pdf.filename" value="Annotation Services.pdf"/>
</display:table>

<c:out value="${buttons}" escapeXml="false" />

<script type="text/javascript">
    highlightTableRows("annotationServices");
</script> 