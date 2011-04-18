<%@ include file="/common/taglibs.jsp"%>
<head>
<title><fmt:message key="corpus.upload.info.title"/></title>
<content tag="heading"><fmt:message key="corpus.upload.info.heading"/></content>
<meta name="menu" content="ResourceMenu"/>

<SCRIPT LANGUAGE="JavaScript">
function addOptionsToTheParentWindowSelectBoxAndClose() {

	var corpusID='<c:out value="${param.corpusID}"/>';
	//alert(corpusID);
	//window.opener.updateList(corpusID);
	window.opener.location.reload();
	//window.opener.reload(corpusID);
	window.close();
	
}



</SCRIPT>

</head>
<div class="separator"></div>

<table class="detail" cellpadding="5">
    <tr>
        <th><fmt:message key="corpusForm.corpusName"/></th>
        <td><c:out value="${param.corpusName}"/></td>
    </tr>
    <c:if test="${not empty param.corpusSize}">
    <tr>
        <th><fmt:message key="corpus.upload.size"/></th>
        <td><c:out value="${param.corpusSize}"/></td>
    </tr>
    <tr>
    	<th><fmt:message key="corpus.upload.uploader"/></th>
    	<td><c:out value="${param.uploader}"/></td>
    </tr>
    </c:if>
    <tr>
        <td></td>
        <td class="buttonBar">
            <c:choose> 
            <c:when test="${not empty param.popup}">
             <input type="button" class="button" name="close" value="Close"
                onclick="addOptionsToTheParentWindowSelectBoxAndClose()" />
            </c:when>
            <c:otherwise> 
            <input type="button" class="button" name="done" value="Done"
                onclick="location.href='<html:rewrite forward="viewCorpora"/>'" />
            <input type="button" class="button" style="width: 120px" value="<fmt:message key='corpus.upload.info.button'/>"
                onclick="location.href='documentsInCorpus.html?corpusID=<c:out value="${param.corpusID}"/>&corpusName=<c:out value="${param.corpusName}"/>'" />
            </c:otherwise> 
</c:choose>  
            
        </td>
    </tr>
</table>
</form>
