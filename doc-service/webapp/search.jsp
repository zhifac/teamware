<%@page import="org.springframework.beans.factory.ListableBeanFactory"%>
<%@page import="org.springframework.web.context.support.WebApplicationContextUtils"%>
<%@page import="gleam.docservice.proxy.*"%>
<html>
  <head><title>Search Documents</title></head>
  <body>
    <font face="Verdana">
    <h1 align="center">Search docservice documents</h1>
<%
    // boolean useChunkedEncoding = false;
    String query = (String) request.getParameter("query");
    String corpusID = (String) request.getParameter("corpusID");
    String contextWindow = (String) request.getParameter("contextWindow");
    try{
      // String dsURL = request.getScheme() + "://" + request.getServerName() + ":"
      //              + request.getServerPort() + request.getContextPath() + "/services/docservice";
   	  // gleam.docservice.client.SerialDocService sds = new gleam.docservice.client.SerialDocServiceServiceLocator().getdocservice(new java.net.URL(dsURL));
      ListableBeanFactory bf = WebApplicationContextUtils.getRequiredWebApplicationContext(application);
      String[] docServiceProxyBeans = bf.getBeanNamesForType(DocServiceProxy.class, false, true);
      if(docServiceProxyBeans.length == 0) {
        throw new IllegalStateException("No doc service proxy found in spring configuration!");
      }
   	  DocServiceProxy dsp = (DocServiceProxy)bf.getBean(docServiceProxyBeans[0], DocServiceProxy.class);
      // if(!useChunkedEncoding) {
      //   java.util.Hashtable requestHeaders = new java.util.Hashtable();
      //   requestHeaders.put(org.apache.axis.transport.http.HTTPConstants.HEADER_TRANSFER_ENCODING_CHUNKED, Boolean.FALSE);
      //   ((org.apache.axis.client.Stub)sds)._setProperty(org.apache.axis.transport.http.HTTPConstants.REQUEST_HEADERS, requestHeaders);
      // }

   	  LRInfo[] cInfos = dsp.listCorpora();
%>
      <form action="search.jsp" method="post">
        <table align="center">
          <tr>
            <td> Query: </td>
            <td> <input type="text" name="query" value="<%= ((query==null)?"":query) %>" size="60"><br> </td>
          </tr>
          <tr>
            <td> CorpusID: </td>
            <td>
              <select name="corpusID" size="1" single>
                <option value="" <%= ((corpusID==null || corpusID.length()==0)?"selected":"") %> >All DataStore Corpora</option>
<%              for(int i=0; i<cInfos.length; i++) { %>                
                <option value="<%= cInfos[i].getCorpusID() %>" <%= ((corpusID!=null && corpusID.equals(cInfos[i].getID()))?"selected":"") %>>
                  <%= cInfos[i].getName() %>
                </option>
<%              } %>                
              </select>
            </td>
          </tr>
          <tr>
            <td> Context Window: </td>
            <td> <input type="text" name="contextWindow" value="<%= ((contextWindow==null)?"4":contextWindow) %>" size="60"><br> </td>
          </tr>
          <tr>
            <td><input type="submit" name="submitButton" value="Search"></td>
          </tr>
        </table>
      </form>
<%
      if(query != null && query.length() > 0) {
    	  //java.util.HashMap searchParams = new java.util.HashMap();
        int contextWindowInt = Integer.parseInt(contextWindow);
        if(corpusID == null || corpusID.length() == 0) {
          corpusID = null;
        }
        String sId = dsp.startSearch(query, corpusID, null, contextWindowInt);
        if(sId != null) {
    	    gate.creole.annic.Hit[] hits = dsp.getNextResults(sId, -1);
        
%>
	        <p></p>
	        <table border="1" align="center">
	          <tr align="center"> <td colspan="4"> <b>Search results ( <%= hits.length %> ) </b> </td> </tr>
            <tr>
	            <TH colspan="1">Document ID</TH>
	            <TH colspan="1">Left Context</TH>
	            <TH colspan="1">Pattern Text</TH>
	            <TH colspan="1">Right Context</TH>
	          </tr>
<%
            for(int i = 0; i < hits.length; i++) {
	    	      gate.creole.annic.Pattern ap = (gate.creole.annic.Pattern) hits[i];
	    	      gate.creole.annic.PatternAnnotation[] pAnnots = ap.getPatternAnnotations();
%>
              <tr>
                <td> <%= ap.getDocumentID() %> </td>
                <td> <%= ap.getPatternText(ap.getLeftContextStartOffset(), ap.getStartOffset()) %> </td>
                <td>
                  <b style="color:black;background-color:#ff9999">
                    <%= ap.getPatternText(ap.getStartOffset(), ap.getEndOffset()) %>
                  </b>
                </td>
                <td> <%= ap.getPatternText(ap.getEndOffset(), ap.getRightContextEndOffset()) %> </td>
              </tr>
<%
            }
%>
          </table>
<%
        } else {
%>
          <p>Search returns no results.</p>
<%
        }
      }
%>
<%
    } catch(Exception ex) {
%>
      <p>
        <b style="color:red"> <%= ex.getMessage() %> </b>
      </p>
      <p>
<%
        java.io.StringWriter sw = new java.io.StringWriter();
			  java.io.PrintWriter pw = new java.io.PrintWriter(sw);
				ex.printStackTrace(pw);
%>
        <%= sw.toString().replace("\n", "<br>") %>
      </p>
<%      
    }
    //<b style="color:black;background-color:#ff9999">text</b>
%>
  </body>
</html>
