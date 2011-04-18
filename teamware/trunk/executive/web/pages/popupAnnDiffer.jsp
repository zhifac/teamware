<%@ include file="/common/taglibs.jsp"%>
<head>
<title><fmt:message key="annDiff.title"/></title>
  <script type='text/javascript' src='dwr/interface/docServiceDetailDWRManager.js'></script>
  <script type='text/javascript' src='dwr/engine.js'></script>
  <script type='text/javascript' src='dwr/util.js'></script>
  <script>
	function updateAnnoType(){
		var keyAnnSetName = document.getElementById("keyAnnoSetName").value;
		var resAnnSetName = document.getElementById("resAnnoSetName").value;
		var asArray= new Array();
        asArray[0]=keyAnnSetName;
        asArray[1]=resAnnSetName;
		var docID='<c:out value="${param.documentID}"/>';
		docServiceDetailDWRManager.listSharedAnnotationTypes(docID,asArray,loadTotal);
	}

	function loadTotal(data){
		DWRUtil.removeAllOptions("annoType");
		DWRUtil.addOptions("annoType",data);
	}
  </script>
<meta name="menu" content="ResourceMenu"/>

</head>  
<h1><fmt:message key="annDiff.heading"/> on the document <c:out value="${param.documentName}"/></h1>
<fmt:message key="annDiff.message"/>
<html:form action="popupAnnDiffer" styleId="annotationDifferResultForm" onsubmit="return validateAnnotationDifferResultForm(this);">
<input type="hidden" name="documentID" value="<c:out value="${param.documentID}"/>"/>
<input type="hidden" name="documentName" value="<c:out value="${param.documentName}"/>"/>
<input type="hidden" name="corpusID" value="<c:out value="${param.corpusID}"/>"/>
<input type="hidden" name="show" value="true"/>
<input type="hidden" name="method" value="searchAnnSetNames"/>
<table>
	<tr>
	<td>
	   <executive:label key="annDiff.annSet1"/>
	</td>
	<td>
	    <html:select property="keyAnnoSetName" styleClass="select" styleId="keyAnnoSetName" onchange="updateAnnoType()">
          	<html:options collection="annoSetNames" property="keyAnnoSetName" labelProperty="keyAnnoSetName"/>
          </html:select>
	</td>
	<tr>
	<tr>
	<td>
	   <executive:label key="annDiff.annSet2"/>
	</td>
	<td>
	    <html:select property="resAnnoSetName" styleClass="select" styleId="resAnnoSetName" onchange="updateAnnoType()">
             <html:options collection="annoSetNames" property="keyAnnoSetName" labelProperty="keyAnnoSetName"/>
          </html:select>
	</td>
	</tr>
	<tr>
	<td>
	   <executive:label key="annotationDifferResultForm.annoType"/>
	   <html:errors property="annoType"/>
	</td>
	<td>
         <html:select property="annoType" styleId="annoType" styleClass="select">
			<html:options collection="annoTypes" property="annoType" labelProperty="annoType"/>
         </html:select>
	</td>
	</tr>
	<tr>
	<td colspan="2" align="center">
	   <html:submit styleClass="button" property="method.save" onclick="bCancel=false" onmouseover="ajax_showTooltip('ajaxtooltip/info/doDiffBtn.jsp',this);return false" onmouseout="ajax_hideTooltip()">
                <fmt:message key="button.annDiff"/>
         </html:submit>
	   <input type="button" onclick="javascript:window.close()"
        	value="<fmt:message key="button.close"/>"
            onmouseover="ajax_showTooltip('ajaxtooltip/info/closeBtn.jsp',this);return false" 
            onmouseout="ajax_hideTooltip()"/>
	</td>
	</tr>
</table>
</html:form>

<c:set var="resultTable">
<table cellpadding="5" border="0">
	<tr>
		<th align="left">&nbsp;</th>
		<th align="center">Document</th>
		<th align="left">Annotation Set</th>
		<th align="left">Annotation Type</th>
	</tr>
	<tr>
		<th align="left">Key</th>
		<td align="left"><c:out value="${param.documentName}" escapeXml="false"/></td>
		<td align="center"><c:out value="${param.keyAnnoSetName}" escapeXml="false"/></td>
		<td align="center"><c:out value="${param.annoType}" escapeXml="false"/></td>
      </tr>
	<tr>
		<th align="left">Response</th>
		<td align="left"><c:out value="${param.documentName}" escapeXml="false"/></td>
		<td align="center"><c:out value="${param.resAnnoSetName}" escapeXml="false"/></</td>
		<td align="center"><c:out value="${param.annoType}" escapeXml="false"/></td>
      </tr>
</table>

<display:table name="annoDifferScores" cellspacing="0" cellpadding="0"
    id="annoDifferScores" pagesize="50" class="table annoDifferScores" export="false" requestURI="">   
	<display:column style="width: 20%" property="recall" titleKey="annDiff.recall"/>
	<display:column style="width: 20%" property="precision" titleKey="annDiff.precision"/>
	<display:column style="width: 20%" property="FMeasure" titleKey="annDiff.fmeasure"/>
	<display:column style="width: 10%" property="correct" titleKey="annDiff.correct"/>
	<display:column style="width: 10%" property="partCorrect" titleKey="annDiff.partCorrect"/>
	<display:column style="width: 10%" property="missing" titleKey="annDiff.missing"/>
	<display:column style="width: 10%" property="spurious" titleKey="annDiff.spurious"/>
</display:table>

<display:table name="annoDifferList" cellspacing="0" cellpadding="0"
    id="annoDifferList" pagesize="100" class="table annoDifferScores" export="true" requestURI="">   
	<display:column style="width: 5%" property="startKey" titleKey="annDiff.start" sortable="true" headerClass="sortable"/>
	<display:column style="width: 5%" property="endKey" titleKey="annDiff.end" sortable="true" headerClass="sortable"/>
	<display:column style="width: 10%" property="keyString" titleKey="annDiff.key" sortable="true" headerClass="sortable"/>
	<display:column style="width: 25%" property="keyFeature" titleKey="annDiff.features" sortable="true" headerClass="sortable"/>
	<display:column style="width: 10%;text-align:center" property="markString" titleKey="annDiff.mark" sortable="true" headerClass="sortable"/>
	<display:column style="width: 5%" property="startRes" titleKey="annDiff.start" sortable="true" headerClass="sortable"/>
	<display:column style="width: 5%" property="endRes" titleKey="annDiff.end" sortable="true" headerClass="sortable"/>
	<display:column style="width: 10%" property="resString" titleKey="annDiff.response" sortable="true" headerClass="sortable"/>
	<display:column style="width: 25%" property="resFeature" titleKey="annDiff.features" sortable="true" headerClass="sortable"/>
</display:table>
</c:set>
<c:if test="${param.show=='true'}">
  <c:out value="${resultTable}" escapeXml="false"/>
</c:if>

<html:javascript formName="annotationDifferResultForm" cdata="false" dynamicJavascript="true" staticJavascript="false"/>
<script type="text/javascript" src="<html:rewrite page="/scripts/validator.jsp"/>"></script>
