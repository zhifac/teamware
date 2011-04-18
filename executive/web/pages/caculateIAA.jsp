<%@ include file="/common/taglibs.jsp"%>
<%@ page import="java.util.*,gleam.executive.webapp.form.*" %>
<head>
<title><fmt:message key="caculateIAA.title"/></title>
  <script type='text/javascript' src='dwr/interface/docServiceDetailDWRManager.js'></script>
  <script type='text/javascript' src='dwr/engine.js'></script>
  <script type='text/javascript' src='dwr/util.js'></script>
<script type='text/javascript'>
      //the following method is not used in this jsp, but worth keeping for a reference.
	function updateAnnoType(){
		var asNames = document.getElementsByTagName("input");
            var myArray = new Array();
            var ass = new Array();
            for(i=0;i<asNames.length;i++){
                if(asNames[i].type=="checkbox"&&asNames[i].checked==true){
                  //alert(asNames[i].type+" " +asNames[i].value+" "+asNames[i].checked);
                  var a=asNames[i].value.toString();
                  ass.push(a);
                }
            }
           
		var docID='<c:out value="${param.documentID}"/>';
		docServiceDetailDWRManager.listSharedAnnotationTypes(docID,ass,loadTotal);
	}

	function updateAnnoType1(){
            var selectedList = document.getElementById("SelectedList");
            //alert(selectedList.value);
            var selected = document.getElementById("SelectedAS");
            var len = selected.length;
            var myArray = new Array();
            var ass = new Array();
            for(i=0;i<len;i++){
              var a=selected.options[i].value.toString();
              if(a=="<Default>"){
                ass.push(null)
              }else{
                ass.push(a);
              }
            }
           
		var docID='<c:out value="${param.documentID}"/>';
		docServiceDetailDWRManager.listSharedAnnotationTypes(docID,ass,loadTotal);
	}
	function loadTotal(data){
		DWRUtil.removeAllOptions("annoType");
		DWRUtil.addOptions("annoType",data);
	}

      function validate(form){
        var selected = form.elements['SelectedAS'];
        var selectedAnnoTypes = form.elements['annoType'];
        var len = selected.length;
        if(len>1 && selectedAnnoTypes.length>0){
          return true;
        }else{
	    alert("You must select at least two annotation sets and move them to the right select box! Also, the selected annotations must have at least one common annotation type.")
          return false;
        }
      }
</script>
<content tag="heading"><fmt:message key="caculateIAA.heading"/> on the document <c:out value="${param.documentName}"/></content><br>
<meta name="menu" content="ResourceMenu"/>
</head>
<p>
<fmt:message key="caculateIAA.message"/>
</p>
<html:form action="caculateIAA" styleId="iAAResultForm" onsubmit="return validate(this);">
<script type='text/javascript'>
      var selectionForm = document.forms['iAAResultForm'];
      var textMoveUp = "Move Up";
	var textMoveDown = "Move Down";
	var textAddColumn = "Add Annotation Set";
	var textRemoveColumn = "Remove Annotation Set";
      var moveOnly = null;
     function addToSelection() {
		var available = selectionForm.elements['Available'];
		var item;
		var idx = available.selectedIndex;
				
		if (idx >= 0 ) {
		  item = available[idx];
		  var val = item.value;
		  var selected = selectionForm.elements['SelectedAS'];
		  var temp = new Option(item.text);
		  temp.value = val;
		  selected.options[selected.options.length] = temp;
		  available[idx] = null;
		  updateTwoWayList();
		}
	}
      function removeFromSelection() {
            
		if ( moveOnly == null ) {
			var moveOnlyField = selectionForm.elements['MoveOnly'];
			moveOnly = moveOnlyField.value.split(',');
		}

		var selected = selectionForm.elements['SelectedAS'];
		var item;

		var idx = selected.selectedIndex;
				
		if (idx >= 0 ) {
			item = selected[idx];
			var val = item.value;
			if ( removeAllowed(val) ) {
				var available = selectionForm.elements['Available'];
				var temp = new Option(item.text);
				temp.value = val;
				available.options[available.options.length] = temp;
				selected[idx] = null;
				updateTwoWayList();
			}
		}
	}
	function moveSelectedUp() {

		var selected = selectionForm.elements['SelectedAS'];
		var idx = selected.selectedIndex;
		if ( idx > 0 ) {
			// one way for NS 4, another for others
			if( typeof( document.layers ) != "undefined" ) {
				var item1 = selected[idx];
				var item2 = selected[idx - 1];
				var temp = new Option();
				temp.value = item2.value;
				temp.text = item2.text;
				selected[idx - 1] = item1;
				selected[idx] = temp;
				updateTwoWayList();
			}
			else {
				var item1 = selected[idx];
				selected[idx] = new Option();
				var item2 = selected[idx - 1];
				selected[idx - 1] = item1;
				selected[idx] = item2;
				updateTwoWayList();
			}
		}
	}
			
	function moveSelectedDown() {
		var selected = selectionForm.elements['SelectedAS'];
		var idx = selected.selectedIndex;
		if ( idx >= 0 && idx < selected.options.length - 1 ) {
			// one way for NS 4, another for others
			if( typeof( document.layers ) != "undefined" ) {
				var item1 = selected[idx];
				var item2 = selected[idx + 1];
				var temp = new Option();
				temp.value = item2.value;
				temp.text = item2.text;
				selected[idx + 1] = item1;
				selected[idx] = temp;
				updateTwoWayList();
			}
			else {
				var item1 = selected[idx];
				selected[idx] = new Option();;
				var item2 = selected[idx + 1];
				selected[idx + 1] = item1;
				selected[idx] = item2;
				updateTwoWayList();
			}
		}
	}
      function removeAllowed(val){
		if ( moveOnly == null ) {
			var moveOnlyField = selectionForm.elements['MoveOnly'];
			moveOnly = moveOnlyField.value.split(',');
		}
		if ( moveOnly != null ) {
			for(var i = 0; i < moveOnly.length;	i++ ) {
				if ( val == moveOnly[i] )
					return false;
				}
			}
			return true;
	}
      function updateTwoWayList() {
		var str = "";
		var options = selectionForm.elements['SelectedAS'].options;
		var i;
		for( i = 0; i < options.length; i++ ) {
			if ( i > 0 )
				str += ",";
				str += options[i].value;
			}
		selectionForm.elements['SelectedList'].value = str;
		str = "";	
		options = selectionForm.elements['Available'].options;
		var i;
		for( i = 0; i < options.length; i++ ) {
			if ( i > 0 )
				str += ",";
				str += options[i].value;
			}
		selectionForm.elements['AvailableList'].value = str;
		//fixMoveIcons();
	}
      function drawMoveSelectedUpAction() {
		document.write( "<a href=\"#\" onclick=\"moveSelectedUp();updateAnnoType1(); return false;\"><img src=\"images/iconMoveUp_20x20.gif\" name=\"SubmitAction.MoveSelectionUp\" alt=\"" + textMoveUp + "\" border=\"0\" width=\"20\" height=\"20\"></a>" );
	}

	function drawMoveSelectedDownAction() {
		document.write( "<a href=\"#\" onclick=\"moveSelectedDown();updateAnnoType1(); return false;\"><img src=\"images/iconMoveDwn_20x20.gif\" name=\"SubmitAction.MoveSelectionDown\" alt=\"" + textMoveDown + "\" border=\"0\" width=\"20\" height=\"20\"></a>" );
	}

	function drawAddToSelectionAction() {
		document.write( "<a href=\"#\" onclick=\"addToSelection();updateAnnoType1(); return false;\"><img src=\"images/iconMoveRt_20x20.gif\" name=\"SubmitAction.AddToSelection\" alt=\"" + textAddColumn + "\" border=\"0\" width=\"20\" height=\"20\"></a>" );
	}

	function drawRemoveFromSelectionAction() {
		document.write( "<a href=\"#\" onclick=\"removeFromSelection();updateAnnoType1(); return false;\"><img src=\"images/iconMoveLt_20x20.gif\" name=\"SubmitAction.RemoveFromSelection\" alt=\"" + textRemoveColumn + "\" border=\"0\" width=\"20\" height=\"20\"></a>" );
	}
</script>
<input type="hidden" name="AvailableList" value="">
<input type="hidden" name="SelectedList" value="">
<input type="hidden" name="MoveOnly" value="">
<p></p>
<c:set var="buttons">
    <html:submit styleClass="button" property="method.save" onclick="bCancel=false" onmouseover="ajax_showTooltip('ajaxtooltip/info/iaa.jsp',this);return false" onmouseout="ajax_hideTooltip()">
                <fmt:message key="button.iaa"/>
         </html:submit>
	   <input type="button" onclick="location.href='<c:url value="/documentsInCorpus.html?corpusID=${param.corpusID}&corpusName=${param.corpusName}&method=search"/>'"
        	value="<fmt:message key="button.done"/>"
       onmouseover="ajax_showTooltip('ajaxtooltip/info/doneBtn.jsp',this);return false" onmouseout="ajax_hideTooltip()" 	
       />
</c:set>

<c:out value="${buttons}" escapeXml="false"/>
<p></p>
<table id="iaaTable">
       <tr>
		<td>Available Annotation Sets</td><td></td><td>Annotation Sets to Compare</td><td></td>
       </tr>
	 <tr>
        <td>
         <select name="Available" size="7" id="Available" style="width: 200px">
         <c:forEach var="asName" items="${availableASNames}">
            <option value='<c:out value="${asName.label}"/>'><c:out value="${asName.label}"/></option>
	   </c:forEach>
         </select>
        </td>
        <td align="center">
           <script type="text/javascript">
           <!--
		  drawAddToSelectionAction();	
		// -->
	     </script>
           <br><br>
           <script type="text/javascript">
           <!--
		  drawRemoveFromSelectionAction();	
		// -->
	     </script>
        </td>
  
        <td><select name="SelectedAS" size="7" id="SelectedAS" style="width: 200px"></select></td>
        <td align="left">
           <script type="text/javascript">
           <!--
		  drawMoveSelectedUpAction();	
		// -->
	     </script>
           <br>
           <script type="text/javascript">
           <!--
		  drawMoveSelectedDownAction();
		// -->
	     </script>
        </td>
      </tr>
    <tr><td colspan="3"><p></p></td></tr>  
	<tr>
	<td>
	   <executive:label key="caculateIAA.annoType"/>
	   <html:errors property="annoType"/>
	</td>
	<td>
         <html:select property="annoType" styleId="annoType" styleClass="select">
		<html:options collection="annoTypes" property="annoType" labelProperty="annoType"/>
         </html:select>
	</td>
      <td><executive:label key="caculateIAA.algorithm"/>
         <html:select property="algorithm" styleId="algorithm" styleClass="select">
           <html:options collection="iaaAlgorithms" property="algorithm" labelProperty="algorithm"/>
         </html:select>
      </td>
	</tr>
      <tr>
        <td><executive:label key="caculateIAA.feature"/></td>
        <td><html:text property="feature" styleId="feature"/></td>
      </tr>
      
     
</table>
<p></p>
<c:out value="${buttons}" escapeXml="false"/>
<p></p>
<input type="hidden" name="documentID" value="<c:out value="${param.documentID}"/>"/>
<input type="hidden" name="documentName" value="<c:out value="${param.documentName}"/>"/>
<input type="hidden" name="corpusID" value="<c:out value="${param.corpusID}"/>"/>
<input type="hidden" name="corpusName" value="<c:out value="${param.corpusName}"/>"/>
<input type="hidden" name="show" value="true"/>
<input type="hidden" name="method" value="searchAnnSetNames"/>
</html:form>



<c:if test="${param.show=='true'&&param.algorithm=='pairwise-f-measure'}">
<c:set var="pairwiseResultTable">
<p>
<b><fmt:message key="caculateIAA.overallfmeasure"/></b>
</p>
<display:table name="iaaPairwiseFMeasure" id="iaaPairwiseFMeasure" cellspacing="0" cellpadding="0"
	pagesize="50" class="table iaaPairwiseFMeasure" export="false" requestURI="">
  <display:column style="width: 10%" property="precision" titleKey="caculateIAA.precision"/>
  <display:column style="width: 10%" property="recall" titleKey="caculateIAA.recall"/>
  <display:column style="width: 10%" property="f1" titleKey="caculateIAA.f1"/>
  <display:column style="width: 10%" property="precisionLenient" titleKey="caculateIAA.precisionLenient"/>
  <display:column style="width: 10%" property="recallLenient" titleKey="caculateIAA.recallLenient"/>
  <display:column style="width: 10%" property="f1Lenient" titleKey="caculateIAA.f1Lenient"/>
  <display:column style="width: 10%" property="correct" titleKey="caculateIAA.correct"/>
  <display:column style="width: 10%" property="partiallyCorrect" titleKey="caculateIAA.partiallyCorrect"/>
  <display:column style="width: 10%" property="missing" titleKey="caculateIAA.missing"/>
  <display:column style="width: 10%" property="spurious" titleKey="caculateIAA.spurious"/>
  <display:setProperty name="paging.banner.placement" value="none" />
</display:table>

<%int k=0;int u=0;int v=0;%>
<p>
<b><fmt:message key="caculateIAA.sf"/></b>
</p>
<display:table name="iaaResultList" cellspacing="0" cellpadding="0"
    id="iaaResultList" pagesize="50" class="table iaaResultList" export="false" requestURI="">
         
	<display:column style="width: 20%" property="keyASName" titleKey="caculateIAA.tsf"/>
	<c:forEach var="asName" items="${iaaASNames}" >
         <%String st=(String)pageContext.getAttribute("asName");%>
	   <display:column title='<%=st%>' escapeXml="false">
             <a href="#<%=k%>"><c:out value="${iaaResultList.strictvalues[asName]}" /></a>
           </display:column>
           <c:if test="${!empty iaaResultList.strictvalues[asName]}">
           <%k++;%>
         </c:if>
	</c:forEach>
      <display:setProperty name="paging.banner.placement" value="none" />
</display:table>
<p>
<b><fmt:message key="caculateIAA.lf"/></b>
</p>
<display:table name="iaaResultList" cellspacing="0" cellpadding="0"
    id="iaaResultList" pagesize="50" class="table iaaResultList" export="false" requestURI="">
         
	<display:column style="width: 20%" property="keyASName" titleKey="caculateIAA.tlf"/>
	<c:forEach var="asName" items="${iaaASNames}" >
         <%String lt=(String)pageContext.getAttribute("asName");%>
	   <display:column title='<%=lt%>' escapeXml="false">
            <a href="#<%=u%>"><c:out value="${iaaResultList.lenientValues[asName]}" /></a>
         </display:column>
         <c:if test="${!empty iaaResultList.strictvalues[asName]}">
           <%u++;%>
         </c:if>
	</c:forEach>
      <display:setProperty name="paging.banner.placement" value="none" />
</display:table>
<p>
<b><fmt:message key="caculateIAA.of"/></b>
</p>
<display:table name="iaaResultList" cellspacing="0" cellpadding="0"
    id="iaaResultList" pagesize="50" class="table iaaResultList" export="false">
         
	<display:column style="width: 20%" property="keyASName" titleKey="caculateIAA.tof"/>
	<c:forEach var="asName" items="${iaaASNames}" >
         <%String ot=(String)pageContext.getAttribute("asName");%>
	   <display:column title='<%=ot%>' escapeXml="false">
           <a href="#<%=v%>"><c:out value="${iaaResultList.otherValues[asName]}" /></a>
          </display:column>
         <c:if test="${!empty iaaResultList.strictvalues[asName]}">
           <%v++;%>
         </c:if>
	</c:forEach>
      <display:setProperty name="paging.banner.placement" value="none" />
</display:table>
<p>&nbsp;</p>

<%List iaaList = (List)request.getAttribute("iaaResultList");
  int j=0;
  for(int i=0;i<iaaList.size();i++){
    IAAResultForm iForm = (IAAResultForm)iaaList.get(i);
    String keyASName = iForm.getKeyASName();
    
%>
  <c:forEach var="responseAS" items="${iaaASNames}" >
    <%String resASName =(String)pageContext.getAttribute("responseAS");
      Map<String, List<LabelValueDetailForm>> m = iForm.getLabelDetails();
      if (m.get(resASName) != null) {
         List<LabelValueDetailForm> l =(List<LabelValueDetailForm>) m.get(resASName);
         request.setAttribute("labelValueList",l);
%>
       <display:table id="labelValueList" name="labelValueList" cellspacing="0" cellpadding="0"
           pagesize="50" class="table iaaResultList" export="false">
        <display:column property="labelValue" style="width: 20%" titleKey="caculateIAA.label"/>
        <display:column property="strictValues" style="width: 20%" titleKey="caculateIAA.tsf"/>
        <display:column property="lenientValues" style="width: 20%" titleKey="caculateIAA.tlf"/>
        <display:column property="otherValues" style="width: 20%" titleKey="caculateIAA.tof"/>
        <display:caption><a name="<%=j%>">Details for <%=keyASName%> against <%=resASName%></a></display:caption>
        <display:setProperty name="paging.banner.placement" value="none" />
      </display:table>
      
<%j++;
      }
    %>
  </c:forEach>
<%
  }
%>

</c:set>
  <c:out value="${pairwiseResultTable}" escapeXml="false"/>
</c:if>

<c:if test="${param.show=='true'&&param.algorithm=='all-ways-f-measure'}">
<c:set var="allwaysResultTable">
<%int u=0;
 String[] asNames = (String[])request.getAttribute("iaaASNames");
 String keyASName = asNames[0];
%>
<p>
<b><fmt:message key="caculateIAA.overallfmeasure"/></b>
</p>
<display:table name="iaaAllwaysFMeasure" id="iaaAllwaysFMeasure" cellspacing="0" cellpadding="0"
	pagesize="50" class="table iaaAllwaysFMeasure" export="false" requestURI="">
  <display:column style="width: 10%" property="precision" titleKey="caculateIAA.precision"/>
  <display:column style="width: 10%" property="recall" titleKey="caculateIAA.recall"/>
  <display:column style="width: 10%" property="f1" titleKey="caculateIAA.f1"/>
  <display:column style="width: 10%" property="precisionLenient" titleKey="caculateIAA.precisionLenient"/>
  <display:column style="width: 10%" property="recallLenient" titleKey="caculateIAA.recallLenient"/>
  <display:column style="width: 10%" property="f1Lenient" titleKey="caculateIAA.f1Lenient"/>
  <display:column style="width: 10%" property="correct" titleKey="caculateIAA.correct"/>
  <display:column style="width: 10%" property="partiallyCorrect" titleKey="caculateIAA.partiallyCorrect"/>
  <display:column style="width: 10%" property="missing" titleKey="caculateIAA.missing"/>
  <display:column style="width: 10%" property="spurious" titleKey="caculateIAA.spurious"/>
  <display:setProperty name="paging.banner.placement" value="none" />
</display:table>
<p>
<b><fmt:message key="caculateIAA.fmeasureForResponse"/> <%=keyASName%></b>
</p>
<display:table name="iaaAllwaysResultList" cellspacing="0" cellpadding="0"
    id="iaaAllwaysResultList" pagesize="50" class="table iaaResultList" export="false" requestURI="">
    <display:column style="width: 20%" title="Response">
      <a href="#<%=u%>"><c:out value="${iaaAllwaysResultList.allwaysResponse}" /></a>
      <c:if test="${!empty iaaAllwaysResultList.allwaysResponse}">
           <%u++;%>
      </c:if>
    </display:column>
    <display:column style="width: 20%" property="allwaysStrictvalues" titleKey="caculateIAA.tsf"/>
    <display:column style="width: 20%" property="allwaysLenientValues" titleKey="caculateIAA.tlf"/>
    <display:column style="width: 20%" property="allwaysOtherValues" titleKey="caculateIAA.tof"/>
    <display:setProperty name="paging.banner.placement" value="none" />
</display:table>

<%List iaaList = (List)request.getAttribute("iaaResultList");
  int j=0;
  for(int i=0;i<iaaList.size();i++){
    IAAResultForm iForm = (IAAResultForm)iaaList.get(i);
%>
  <c:forEach var="responseAS" items="${iaaAllwaysResponseList}" >
    <%String resASName =(String)pageContext.getAttribute("responseAS");
      Map<String, List<LabelValueDetailForm>> m = iForm.getLabelDetails();
      if (m.get(resASName) != null) {
         List<LabelValueDetailForm> l =(List<LabelValueDetailForm>) m.get(resASName);
         request.setAttribute("labelValueList",l);
%>
       <display:table id="labelValueList" name="labelValueList" cellspacing="0" cellpadding="0"
           pagesize="50" class="table labelValueList" export="false">
        <display:column property="labelValue" style="width: 20%" titleKey="caculateIAA.label"/>
        <display:column property="strictValues" style="width: 20%" titleKey="caculateIAA.tsf"/>
        <display:column property="lenientValues" style="width: 20%" titleKey="caculateIAA.tlf"/>
        <display:column property="otherValues" style="width: 20%" titleKey="caculateIAA.tof"/>
        <display:caption><a name="<%=j%>">Details for <%=keyASName%> against <%=resASName%></a></display:caption>
        <display:setProperty name="paging.banner.placement" value="none" />
      </display:table>
      
<%j++;
      }
    %>
  </c:forEach>
<%
  }
%>

</c:set>
	<c:out value="${allwaysResultTable}" escapeXml="false"/>
</c:if>

<c:if test="${param.show=='true'&&param.algorithm=='pairwise-kappa'}">
<c:set var="pairwisekappaResultTable">
<p>
<b><fmt:message key="caculateIAA.kappaOverall"/></b>
</p>
<display:table name="pairwiseKappaOverallList" id="pairwiseKappaOverallList" cellspacing="0" cellpadding="0"
	pagesize="50" class="table pairwiseKappaOverallList" export="false" requestURI="">
  <display:column style="width: 30%" property="kappaCohen" titleKey="caculateIAA.kappaCohen"/>
  <display:column style="width: 30%" property="kappaPi" titleKey="caculateIAA.kappaPi"/>
  <display:column style="width: 30%" property="observedAgreement" titleKey="caculateIAA.observedAgreement"/>
  <display:setProperty name="paging.banner.placement" value="none" />
</display:table>

<%int k=0;%>
<p>
<b><fmt:message key="caculateIAA.kappaAbbreviation"/></b>
</p>
<display:table name="iaaResultList" cellspacing="0" cellpadding="0"
    id="iaaResultList" pagesize="50" class="table iaaResultList" export="false" requestURI="">
         
	<display:column style="width: 20%" property="keyASName" titleKey="caculateIAA.titleValues"/>
	<c:forEach var="asName" items="${iaaASNames}" >
         <%String st=(String)pageContext.getAttribute("asName");%>
	   <display:column title='<%=st%>' escapeXml="false">
             <a href="#<%=k%>"><c:out value="${iaaResultList.kappavalues[asName]}" /></a>
           </display:column>
           <c:if test="${!empty iaaResultList.kappavalues[asName]}">
           <%k++;%>
         </c:if>
	</c:forEach>
      <display:setProperty name="paging.banner.placement" value="none" />
</display:table>


<%List iaaList = (List)request.getAttribute("iaaResultList");
  int j=0;
  for(int i=0;i<iaaList.size();i++){
    IAAResultForm iForm = (IAAResultForm)iaaList.get(i);
    String keyASName = iForm.getKeyASName();
    
%>
  <c:forEach var="responseAS" items="${iaaASNames}" >
    <%String resASName =(String)pageContext.getAttribute("responseAS");
      Map<String, List<ConfusionMatrixRowForm>> m = iForm.getConfusionMatrices();
      if (m.get(resASName) != null) {
         List<ConfusionMatrixRowForm> cm =(List<ConfusionMatrixRowForm>) m.get(resASName);
         request.setAttribute("confusionMatrix",cm);
%>
<!-- print a title "Confusion matrix for ann1 against ann2" -->
       <display:table id="confusionMatrix" name="confusionMatrix" cellspacing="0" cellpadding="0"
           pagesize="50" class="table confusionMatrix" export="false">
           <display:column property="keyLabel" />
           <display:column title="NONE">
              <%ConfusionMatrixRowForm cmrf = (ConfusionMatrixRowForm)pageContext.getAttribute("confusionMatrix");%>
              <%= cmrf.getEntries().get(null) %>
		</display:column>
           <c:forEach var="label" items="${iaaLabelValues}">
             <% String t = (String)pageContext.getAttribute("label"); %>
             <display:column title="<%=t%>">
               <c:out value="${confusionMatrix.entries[label]}"/>
             </display:column>
           </c:forEach>
           <display:caption>
		 <a name="<%=j%>">Details for <%=keyASName%> against <%=resASName%></a>
	     </display:caption>
           <display:setProperty name="paging.banner.placement" value="none" />
       </display:table>
       
       <display:table id="confusionMatrix" name="confusionMatrix" cellspacing="0" cellpadding="0"
           pagesize="50" class="table confusionMatrix" export="false">
         <display:column property="keyLabel" />
         <display:column property="specificAgreementPositive" />
         <display:column property="specificAgreementNegative" />
         <display:setProperty name="paging.banner.placement" value="none" />
       </display:table>
<%j++;
      }
    %>
  </c:forEach>
<%
  }
%>
</c:set>
	<c:out value="${pairwisekappaResultTable}" escapeXml="false"/>
</c:if>

<c:if test="${param.show=='true'&&param.algorithm=='all-ways-kappa'}">
<c:set var="allwaysKappaResultTable">
  <p>
  <b><fmt:message key="caculateIAA.allwaysKappaOverall"/></b>
 </p>
  <display:table name="allwaysKappaOverallList" id="allwaysKappaOverallList" cellspacing="0" cellpadding="0"
	pagesize="50" class="table allwaysKappaOverallList" export="false" requestURI="">
  <display:column style="width: 30%" property="kappaSC" titleKey="caculateIAA.kappaSC"/>
  <display:column style="width: 30%" property="kappaDF" titleKey="caculateIAA.kappaDF"/>
  <display:column style="width: 30%" property="observedAgreement" titleKey="caculateIAA.observedAgreement"/>
  <display:setProperty name="paging.banner.placement" value="none" />
</display:table>
</c:set>
	<c:out value="${allwaysKappaResultTable}" escapeXml="false"/>
</c:if>

<script type="text/javascript" src="<html:rewrite page="/scripts/validator.jsp"/>"></script>