<%@ include file="/common/taglibs.jsp"%>
<script type="text/javascript">
function generateDetails(message){
newfcolor = "blue";
newbcolor = "white";
HTMLstring='<HTML>\n';
HTMLstring+='<HEAD>\n';
HTMLstring+='<TITLE>Search Hit Details</TITLE>\n';
HTMLstring+='</HEAD>\n';
HTMLstring+='<BODY bgColor="'+newbcolor+'">\n';
HTMLstring+='<P Style="color:'+newfcolor+'">'+message+'</P>\n';
HTMLstring+='</BODY>\n';
HTMLstring+='</HTML>';

newwindow=window.open();
newdocument=newwindow.document;
newdocument.write(HTMLstring);
newdocument.close();

}
</script>
<title><fmt:message key="annicSearch.title"/></title>
<content tag="heading"><fmt:message key="annicSearch.heading"/></content>
<meta name="menu" content="AnnicSearch"/>
<c:set var="searchButton">
<input type="button" style="margin-right:5px" 
		onclick="location.href='<c:url value="/searchResult.html?searcherID=${searcherID}"/>'" 
		value="<fmt:message key="button.nextSearch"/>"/>
</c:set>

<c:out value="${searchButton}" escapeXml="false"/>

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
         <img src="<c:url value="/images/view.gif"/>" alt="<fmt:message key="icon.information"/>" class="icon" />
        </a>
</display:column>

</display:table>

<c:out value="${searchButton}" escapeXml="false"/>