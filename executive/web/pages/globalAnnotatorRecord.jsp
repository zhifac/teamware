<%@ page import="gleam.executive.workflow.model.*" %>
<%@ page import="gleam.executive.workflow.util.WorkflowUtil" %>
<%@ page import="java.util.SortedMap"%>
<%@ page import="java.util.Map"%>
<%@ page import="java.util.Iterator"%>
<%@ include file="/common/taglibs.jsp"%>

<%@page import="java.util.ArrayList"%>
<%@page import="gleam.executive.workflow.util.JPDLConstants"%>
<%@page import="java.util.List"%><SCRIPT LANGUAGE="JavaScript" SRC="<c:url value='/scripts/table2chart.js'/>"></SCRIPT>



<head>
    <title><fmt:message key="globalAnnotatorRecordOverview.title"/></title>
    <content tag="heading"><fmt:message key="globalAnnotatorRecordOverview.heading"/></content>
    <meta name="menu" content="ProjectMenu"/>
</head>

<script type="text/javascript">

window.onload=function(){
	setTimeout('AjaxRefresh()',3000);
}
</script>

<% boolean enabled=true; %>

<c:set var="buttons">
     <input type="button" style="margin-right: 5px"
        onclick="location.href='<c:url value="/annotationStatus.html?method=showDetails&id=${param.id}"/>'"
        value="<fmt:message key="button.annotationStatusDetailedView"/>"
        onmouseover="ajax_showTooltip('ajaxtooltip/info/annotationStatusDetailedViewBtn.jsp',this);return false" onmouseout="ajax_hideTooltip()"   
     />
        <input type="button" style="margin-right: 5px"
        onclick="location.href='<c:url value="/annotationStatus.html?method=showOverview&id=${param.id}"/>'"
        value="<fmt:message key="button.annotationStatusOverview"/>"
        onmouseover="ajax_showTooltip('ajaxtooltip/info/annotationStatusOverviewBtn.jsp',this);return false" onmouseout="ajax_hideTooltip()"   
     />
     
      <input type="button" style="margin-right: 5px"
        onclick="location.href='<c:url value="/processInstanceList.html?method=listTopProcessesWithTheSameKeyAndName&processInstanceId=${param.id}"/>'"
        value="<fmt:message key="button.backToProcessInstance"/>"
        onmouseover="ajax_showTooltip('ajaxtooltip/info/doneBtn.jsp',this);return false" onmouseout="ajax_hideTooltip()"/>
</c:set>

     <%
	AnnotationMetricMatrix annotationMetricMatrix = (AnnotationMetricMatrix)request.getAttribute("annotationMetricMatrix");
    SortedMap metricMap = annotationMetricMatrix.getMetricMap();
    if(metricMap!=null && metricMap.size() > 0){
%> 
    
<div style="width:40%;float:left;margin-top:20px">
<c:out value="${buttons}" escapeXml="false"/>



<p>
<strong><fmt:message key="annotationStatus.role"/></strong>: <%=request.getParameter("role")%>
<br/><br/>
</p>

<table cellpadding="0" cellspacing="0" class="table">

  <thead>
    <tr><th scope="col">Name</th>
    <th scope="col">#</th>
    <th scope="col">Total Time</th>
    <th scope="col">Average Time</th></tr>
  </thead>
  <tbody>
<%
	

Iterator<Map.Entry<String, AnnotationMetricInfo>> it = metricMap.entrySet().iterator();
while (it.hasNext()) {
        Map.Entry<String, AnnotationMetricInfo> entry = it.next();
%> 

<tr>
<td>
<%
List<String> list = new ArrayList<String>();
list.add(entry.getKey());
String result = WorkflowUtil.collectionToFormattedCSVString(request.getContextPath(), list, JPDLConstants.ANNOTATOR_RECORD_LINK_FORMAT, request.getParameter("id"));
%>		
<%=result %>
</td>

<td>
<%=entry.getValue().getCount() %>
</td>
<td>
<%=entry.getValue().getTimeMetricInfo().getTotalTime() %>
</td>
<td>
<%=entry.getValue().getTimeMetricInfo().getAverageTime() %>
</td>
</tr>
<% } %>
</tbody>
</table>


<c:out value="${buttons}" escapeXml="false" />

</div>
<div style="float:left;padding:10px">
 <executive:googleChart title="All Annotator Record Chart" metricMap="<%=metricMap%>" chart="pie" size="500,300" enabled="<%=enabled%>" />
</div>			        
<%  }
    else {
%>
<c:out value="${buttons}" escapeXml="false" />
<p>
</p>
<fmt:message key="globalAnnotatorRecordOverview.notExist"/>
<% }%>
