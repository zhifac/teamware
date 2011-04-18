<%@page import="org.springframework.beans.factory.ListableBeanFactory"%>
<%@page import="org.springframework.web.context.support.WebApplicationContextUtils"%>
<%@page import="gleam.docservice.proxy.*"%>
<html>
  <head><title>Docservice Documents</title></head>
  <body>
    <font face="Verdana">

    <h1 align="center">Current docservice documents</h1>
<%
      // boolean useChunkedEncoding = false;
      String serverURL = request.getScheme() + "://" + application.getAttribute("urlbase") + ":" + request.getServerPort();
	    String docserviceURL = serverURL + request.getContextPath() + "/services/docservice";
   	  try {
        ListableBeanFactory bf = WebApplicationContextUtils.getRequiredWebApplicationContext(application);
        String[] docServiceProxyBeans = bf.getBeanNamesForType(DocServiceProxy.class, false, true);
        if(docServiceProxyBeans.length == 0) {
          throw new IllegalStateException("No doc service proxy found in spring configuration!");
        }
        DocServiceProxy dsp = (DocServiceProxy)bf.getBean(docServiceProxyBeans[0], DocServiceProxy.class);
        // gleam.docservice.client.SerialDocService sds = new gleam.docservice.client.SerialDocServiceServiceLocator().getdocservice(new java.net.URL(docserviceURL));
        // if(!useChunkedEncoding) {
        //   java.util.Hashtable requestHeaders = new java.util.Hashtable();
        //   requestHeaders.put(org.apache.axis.transport.http.HTTPConstants.HEADER_TRANSFER_ENCODING_CHUNKED, Boolean.FALSE);
        //   ((org.apache.axis.client.Stub)sds)._setProperty(org.apache.axis.transport.http.HTTPConstants.REQUEST_HEADERS, requestHeaders);
        // }
        LRInfo[] docInfos = dsp.listDocuments(null);
%>
      <table border="1" align="center">
        <tr>
          <TH colspan="1">Document persistent ID
          <TH colspan="1">Document Name
          <TH colspan="1">Edit
        </tr>
<%
        if(docInfos != null && docInfos.length > 0) {
          String annGUIUrl = serverURL + "/annotator-gui/app/launch.jnlp";
          for(int i = 0; i < docInfos.length; i++) {
          String link = "<a href=\""+ annGUIUrl +
                      "?mode=direct" +
                      "&autoconnect=true" +
                      "&docservice-url=" + docserviceURL +
                      "&doc-id=" + docInfos[i].getID() + "\">" +
                      "<img src=\"gleam.png\"" + " width=\"16\" height=\"16\">" + "</a>";
%>
  	        <tr align="center">
	            <td colspan="1"> <%= docInfos[i].getID() %> </td>
              <td colspan="1"> <%= docInfos[i].getName() %> </td>
              <td colspan="1"> <%= link %> </td>
	          </tr>
<%
          }
        } else {
%>
	        <tr align="center">
	          <td colspan="1"><%= "There are no documents." %></td>
	        </tr>
<%
        }
%>
      </table>
<%
      } catch(Exception ex) {
%>
        <%= ex.getMessage() %>
<%      
      }
%>
  </body>
</html>
    
