<%@ include file="/common/taglibs.jsp"%>
<head>
<script type="text/javascript">
function generateDetails(message){

newfcolor = 'blue';
newbcolor = 'white';
HTMLstring='<HTML>\n';
HTMLstring+='<HEAD>\n';
HTMLstring+='<TITLE>Search Hit Details</TITLE>\n';
HTMLstring+='</HEAD>\n';
HTMLstring+='<BODY bgColor="'+newbcolor+'">\n';
HTMLstring+='<P Style="color:'+newfcolor+'">';
HTMLstring+=message;
HTMLstring+='</P>\n';
HTMLstring+='</BODY>\n';
HTMLstring+='</HTML>';

newwindow=window.open();
newdocument=newwindow.document;
newdocument.write(HTMLstring);
newdocument.close();

}

function doSubmit(action)
{
 document.myform.actionString.value = action;
 document.myform.submit();
}

</script>
<title><fmt:message key="annicSearch.title"/></title>
<content tag="heading"><fmt:message key="annicSearch.heading"/></content>
<meta name="menu" content="ResourceMenu"/>
</head>
<c:set var="searchButton">
<input type="button" style="margin-right:5px" 
		onclick="location.href='<c:url value="/searchResult.html?searcherID=${searcherID}&query=${param.query}&contextWindow=${param.contextWindow}&corpusID=${corpusID}"/>'" 
		 onmouseover="ajax_showTooltip('ajaxtooltip/info/annicNextSearchBtn.jsp',this);return false" onmouseout="ajax_hideTooltip()"
		value="<fmt:message key="button.nextSearch"/>"/>
</c:set>

<c:set var="resultTable">
<display:table name="annicList" cellspacing="0" cellpadding="0"
    id="searchResult" pagesize="50" class="table searchResult"
    export="true" requestURI="">
    
<display:column style="width: 20%" property="documentName" sortable="true" headerClass="sortable"
         titleKey="annicSearch.documentName"/>
<display:column style="width: 25%" property="leftContext" sortable="true" headerClass="sortable"
         titleKey="annicSearch.leftContext"/>
<display:column style="width: 25%" property="pattern" sortable="true" headerClass="sortable"
         titleKey="annicSearch.pattern"/>
<display:column style="width: 25%" property="rightContext" sortable="true" headerClass="sortable"
         titleKey="annicSearch.rightContext"/>
<display:column style="width: 5%" titleKey="annicSearch.details">  
        <a href="javascript:generateDetails('<c:out value="${searchResult.detailsTable}"/>')">
         <img src="<c:url value="/images/view.gif"/>" onmouseover="ajax_showTooltip('ajaxtooltip/info/annicDetails.jsp',this);return false" onmouseout="ajax_hideTooltip()" class="icon" />
        </a>
</display:column>

</display:table>
</c:set>

<html:form action="searchResult" method="post" styleId="annicSearch">
<input type="hidden" name="corpusID" value="<c:out value="${param.corpusID}"/>"/>
<ul>
    <li class="info">
        <fmt:message key="annicSearch.message"/>
    </li>
    <li>
        <executive:label key="annicSearchResultForm.query" styleClass="desc"/>
        <html:errors property="query"/>
        <html:text property="query" styleId="query" styleClass="text medium"
          onmouseover="ajax_showTooltip('ajaxtooltip/info/annicSearchBtn.jsp',this);return false" onmouseout="ajax_hideTooltip()"
        />
    </li>
    <li>
        <executive:label key="annicSearchResultForm.contextWindow" styleClass="desc"/>
        <html:errors property="contextWindow"/>
         <html:select property="contextWindow" styleId="contextWindow" styleClass="select">
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
    <li class="buttonBar">
        <html:submit styleClass="button" onclick="bCancel=false"  onmouseover="ajax_showTooltip('ajaxtooltip/info/annicSearchBtn.jsp',this);return false" onmouseout="ajax_hideTooltip()">
            <fmt:message key="button.search"/>
        </html:submit>
        <html:cancel styleClass="button" onclick="bCancel=true"  onmouseover="ajax_showTooltip('ajaxtooltip/info/doneBtn.jsp',this);return false" onmouseout="ajax_hideTooltip()">
            <fmt:message key="button.done"/>
        </html:cancel>
    </li>
</ul>
<c:if test="${param.query!=null}">
	<c:out value="${searchButton}" escapeXml="false"/>
	<c:out value="${resultTable}" escapeXml="false"/>
	<c:out value="${searchButton}" escapeXml="false"/>
</c:if>

</html:form>