<%@page import="org.springframework.beans.factory.ListableBeanFactory"%>
<%@page import="org.springframework.web.context.support.WebApplicationContextUtils"%>
<%@page import="gleam.docservice.LockManager"%>
<%@page import="gleam.docservice.Lock"%>
<%@page import="gleam.docservice.proxy.*"%>
<% ListableBeanFactory bf = WebApplicationContextUtils.getRequiredWebApplicationContext(application); %>
<!-- %@taglib uri="http://java.sun.com/jstl/sql" prefix="sql" % -->
<!-- %@taglib uri="http://java.sun.com/jstl/core" prefix="c" % -->

<html>
  <head><title>Docservice locks</title></head>
<%
  // String serverURL = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
  // String docserviceURL = serverURL + request.getContextPath() + "/services/docservice";
  try {
    // gleam.docservice.client.SerialDocService sds = new gleam.docservice.client.SerialDocServiceServiceLocator().getdocservice(new java.net.URL(docserviceURL));
    String[] docServiceProxyBeans = bf.getBeanNamesForType(DocServiceProxy.class, false, true);
    if(docServiceProxyBeans.length == 0) {
      throw new IllegalStateException("No doc service proxy found in spring configuration!");
    }
    DocServiceProxy dsp = (DocServiceProxy)bf.getBean(docServiceProxyBeans[0], DocServiceProxy.class);
    String taskId = request.getParameter("taskID");
    
    if(taskId!=null){
    try{
      dsp.releaseLock(taskId);
    }catch(Exception e){
%>
    <%= e.getMessage() %>
<%
    }
  }
  }catch(Exception ex){
%>
  <%= ex.getMessage() %>
<%
  }
%>

  <body>
    <font face="Verdana">
    <h1 align="center">Current docservice locks</h1>
    <table border="1" align="center">
      <tr>
        <TH colspan="1">Task ID
        <TH colspan="1">AnnotationSet Name
        <TH colspan="1">Document persistent ID
        <TH colspan="1">Last time accessed
        <TH colspan="1">min:sec ago
        <TH colspan="1">Release Lock
      </tr>
      <%
      String[] lockManagerBeans = bf.getBeanNamesForType(LockManager.class, false, true);
      if(lockManagerBeans.length == 0) {
        throw new IllegalStateException("No lock manager found in spring configuration!");
      }
      LockManager lm = (LockManager)bf.getBean(lockManagerBeans[0], LockManager.class);
      
      %>
      <tr align="center">
        <td colspan="5"><%= "Lock timeout: " + (lm.getDeadLockCleanupTimeout()/60000) + " min" %></td>
      </tr>
      <%
      Lock[] locks = lm._getAllLocks();
      if(locks != null && locks.length > 0) {
        for(int i=0;i<locks.length;i++) {
      %>
          <tr align="right">
             <td><%= locks[i].getTaskId() %></td>
             <%
               String asName = locks[i].getAnnSetName();
               asName = (asName == null)?" ":asName;
             %>
             <td><%= asName %></td>
             <td><%= locks[i].getDocId() %></td>
             <td><%= new java.util.Date(locks[i].getTime()).toString() %></td>
             <%
               long x = (java.lang.System.currentTimeMillis() - locks[i].getTime()) / 1000;
             %>
             <td>
               <%= x/60 + ":" + (x-(x/60)*60) %></td>
                         <td><a href="viewlocks.jsp?taskID=<%=locks[i].getTaskId()%>">release</a></td>
          </tr>
    <%
        }
      } else {
    %>
          <tr align="center">
            <td colspan="5"><%= "There are no locks." %></td>
          </tr>
    <%
      }
      %>
    </table>
  </body>
</html>


<!-- html>
  <head><title>Docservice locks</title></head>
  <body>
    <font face="Verdana">
    <sql:query var="rs" dataSource="jdbc/docservicedb"
      sql="select task_id, ann_set_name, doc_pers_id, timestamp from locks"/>
    <table border="1" align="center">
      <caption>Current docservice locks</caption>
      <tr>
        <TH colspan="1">Task ID
        <TH colspan="1">AnnotationSet Name
        <TH colspan="1">Document persistent ID
        <TH colspan="1">Timestamp [System.currentTimeMillis()]
      </tr>
      <c:forEach items="${rs.rows}" var="row">
        <tr align="right">
          <td><c:out value="${row.task_id}" /></td>
          <td><c:out value="${row.ann_set_name}"/></td>
          <td><c:out value="${row.doc_pers_id}"/></td>
          <td><c:out value="${row.timestamp}"/></td>
        </tr>
      </c:forEach>
    </table>
  </body>
</html -->
