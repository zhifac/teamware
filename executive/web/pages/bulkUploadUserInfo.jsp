<%@ include file="/common/taglibs.jsp"%>


<%@page import="java.util.Iterator"%>
<%@page import="java.util.List"%>

<head>
<title><fmt:message key="bulkUploadInfo.title"/></title>

    <% List<String> uploadedUsers = (List)request.getAttribute("uploadedUsers");
    if(uploadedUsers.size()>0){
    %>
<content tag="heading"><fmt:message key="bulkUploadInfo.heading"/></content>
<%} %>
<meta name="menu" content="AdminMenu"/>
</head>

<div class="separator"></div>

    <ol>
    <% 
Iterator<String> it = uploadedUsers.iterator();
while(it.hasNext()){
%>
    
    <li>
       <%=it.next() %> 
    </li>
    <%}%>
 </ol>   

<p class="ButtonBar">
     <input type="button" class="button" name="done" value="Done"
                onclick="location.href='<html:rewrite forward="viewUsers"/>'" />
            <input type="button" class="button" style="width: 120px" value="Upload Another"
                onclick="location.href='<c:url value="/bulkUpload.html"/>'" />
</p>                
