<html>
  <head><title>Docservice locks</title></head>
  <body>
    <font face="Verdana">
    <h1 align="center">Application init parameters</h1>
    <table border="1" align="center">
      <tr>
        <TH colspan="1">Name
        <TH colspan="1">Value
      </tr>
      <%
        for (java.util.Enumeration paramNames = application.getInitParameterNames(); paramNames.hasMoreElements();) {
          String pName = (String) paramNames.nextElement();
      %>
			    <tr align="right">
			      <td><%= pName %></td>
			      <td><%= application.getInitParameter(pName) %></td>
          </tr>
      <%
        }
      %>
    </table>
  </body>
</html>
