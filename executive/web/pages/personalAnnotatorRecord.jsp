<%@ page import="gleam.executive.workflow.model.*" %>
<%@page import="java.util.SortedMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.Iterator"%>
<%@ include file="/common/taglibs.jsp"%>
<SCRIPT LANGUAGE="JavaScript" SRC="<c:url value='/scripts/table2chart.js'/>"></SCRIPT>



<head>
    <title><fmt:message key="personalAnnotatorRecordOverview.title"/> <%=request.getParameter("username")%></title>
    <content tag="heading"><fmt:message key="personalAnnotatorRecordOverview.heading"/> '<%=request.getParameter("username")%>'</content>
    <meta name="menu" content="ProjectMenu"/>
</head>

<script type="text/javascript">

window.onload=function(){
	setTimeout('AjaxRefresh()',3000);
}
</script>

<% boolean enabled=true; %>

<c:set var="buttons">
     <input type="button" style="margin-right: 2px"
        onclick="location.href='<c:url value="/annotationStatus.html?method=showDetails&id=${param.id}"/>'"
        value="<fmt:message key="button.annotationStatusDetailedView"/>"
        onmouseover="ajax_showTooltip('ajaxtooltip/info/annotationStatusDetailedViewBtn.jsp',this);return false" onmouseout="ajax_hideTooltip()"   
     />
        <input type="button" style="margin-right: 2px"
        onclick="location.href='<c:url value="/annotationStatus.html?method=showOverview&id=${param.id}"/>'"
        value="<fmt:message key="button.annotationStatusOverview"/>"
        onmouseover="ajax_showTooltip('ajaxtooltip/info/annotationStatusOverviewBtn.jsp',this);return false" onmouseout="ajax_hideTooltip()"   
     />
     
      <input type="button" style="margin-right: 2px"
        onclick="location.href='<c:url value="/annotationStatus.html?method=showGlobalAnnotator&id=${param.id}&role=annotator"/>'"
        value="<fmt:message key="button.annotatorRecordOverview"/>"
        onmouseover="ajax_showTooltip('ajaxtooltip/info/annotatorRecordOverviewBtn.jsp',this);return false" onmouseout="ajax_hideTooltip()"   
     />
     
      <input type="button" style="margin-right: 2px"
        onclick="location.href='<c:url value="/processInstanceList.html?method=listTopProcessesWithTheSameKeyAndName&processInstanceId=${param.id}"/>'"
        value="<fmt:message key="button.backToProcessInstance"/>"
        onmouseover="ajax_showTooltip('ajaxtooltip/info/doneBtn.jsp',this);return false" onmouseout="ajax_hideTooltip()"/>
</c:set>


<%
AnnotationMetricMatrix annotationMetricMatrix = (AnnotationMetricMatrix)request.getAttribute("annotationMetricMatrix");
 if(annotationMetricMatrix!=null){
 %>      
    
<div style="width:44%;float:left;margin-top:20px">
<c:out value="${buttons}" escapeXml="false"/>
<p>
</p>

<table cellpadding="0" cellspacing="0" class="table">

  <thead>
    <tr><th scope="col">Status</th><th scope="col">#</th></tr>
  </thead>
  <tbody>
<% 
SortedMap metricMap = annotationMetricMatrix.getMetricMap();
Iterator<Map.Entry<String, AnnotationMetricInfo>> it = metricMap.entrySet().iterator();
while (it.hasNext()) {
        Map.Entry<String, AnnotationMetricInfo> entry = it.next();
        
%> 

<tr>
<td>
<%=entry.getKey() %>
</td>

<td>
<%=entry.getValue().getCount() %>
</td>

</tr>
<% } %>

</tbody>
</table>

<p>
<strong><fmt:message key="annotationStatusOverview.totalTimeAnnotator"/>: </strong>
<%=annotationMetricMatrix.getTimeMetricInfo().getTotalTime()%>
</p>

<p>
<strong><fmt:message key="annotationStatusOverview.averageTimeAnnotator"/>: </strong>
<%=annotationMetricMatrix.getTimeMetricInfo().getAverageTime()%>
<br/><br/>
</p>

<c:out value="${buttons}" escapeXml="false" />
</div>
<div style="float:left;padding:10px">
 <executive:googleChart title="Annotator Record Chart" metricMap="<%=metricMap%>" chart="pie" size="490,300" enabled="<%=enabled%>" />
</div>			        
<% } else { %>
<p>
No data available.
</p>
<% } %>