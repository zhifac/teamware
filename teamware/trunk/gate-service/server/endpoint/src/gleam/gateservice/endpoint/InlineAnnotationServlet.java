package gleam.gateservice.endpoint;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * This servlet provides a simple HTTP POST interface to the
 * InlineAnnotationService. It takes GaS parameter values from the query
 * string provided to the servlet request and takes the text of the
 * document to process from the HTTP POST data. The processed document
 * with inline markup is returned as the HTTP response.
 */
public class InlineAnnotationServlet extends HttpServlet {

  private static final String DEFAULT_ENDPOINT_HANDLER_BEAN_NAME = "endpointHandler";
  
  private InlineAnnotationService endpointHandler;

  /**
   * A GET request simply returns a plain text message giving the
   * required and optional parameter names.
   */
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException {
    response.setContentType("text/plain; charset=UTF-8");
    PrintWriter writer = response.getWriter();
    writer.println("Required parameters:");
    for(String param : endpointHandler.getRequiredParameterNames()) {
      writer.println(param);
    }
    writer.println();
    writer.println("Optional parameters:");
    for(String param : endpointHandler.getOptionalParameterNames()) {
      writer.println(param);
    }
    writer.flush();
  }

  /**
   * Process a POST request, taking any specified parameters from the
   * request parameters (i.e. from the HTTP query string) and the
   * document content from the POST data. If the endpoint handler throws
   * an exception, we return a 500 response code.
   */
  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException {
    if(request.getContentType() == null
            || !request.getContentType().startsWith("text/")) {
      returnError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, request
              .getContentType()
              + " is not a valid content type.\n"
              + "Only text/* documents can be processed.", request, response);
      return;
    }

    Map<String, String[]> params = request.getParameterMap();

    // check that all required parameters were specified
    for(String requiredParamName : endpointHandler.getRequiredParameterNames()) {
      if(!params.containsKey(requiredParamName)) {
        returnError(
                HttpServletResponse.SC_BAD_REQUEST,
                "No value supplied for required parameter " + requiredParamName,
                request, response);
        return;
      }
    }

    // construct a List of ParameterValues from the request parameters
    List<ParameterValue> paramValues = new ArrayList<ParameterValue>();
    for(Map.Entry<String, String[]> param : params.entrySet()) {
      if(param.getValue() != null) {
        for(String value : param.getValue()) {
          ParameterValue pv = new ParameterValue();
          pv.setName(param.getKey());
          pv.setValue(value);
          paramValues.add(pv);
        }
      }
    }

    BufferedReader in = request.getReader();
    StringBuilder sb = null;
    if(request.getContentLength() >= 0) {
      // content length is in bytes and string builder size is in
      // characters, but we'll never have more characters than bytes
      sb = new StringBuilder(request.getContentLength());
    }
    else {
      sb = new StringBuilder();
    }

    char[] buffer = new char[16384];
    int charsRead = 0;
    while((charsRead = in.read(buffer)) >= 0) {
      sb.append(buffer, 0, charsRead);
    }

    String docContent = sb.toString();

    // make the call and return the output to the user with the same
    // content type as the original request
    try {
      String output = endpointHandler.annotate(docContent, paramValues);

      response.setContentType(request.getContentType());
      PrintWriter pw = response.getWriter();

      pw.write(output);
    }
    catch(GateWebServiceFault gwsf) {
      StringWriter stackTrace = new StringWriter();
      PrintWriter stackTracePW = new PrintWriter(stackTrace);
      gwsf.printStackTrace(stackTracePW);
      returnError(
              HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
              "Exception occurred during processing:\n" + stackTrace.toString(),
              request, response);
    }
  }

  private void returnError(int statusCode, String message,
          HttpServletRequest request, HttpServletResponse response)
          throws IOException {
    response.setStatus(statusCode);
    response.setContentType("text/plain; charset=UTF-8");
    PrintWriter pw = response.getWriter();
    pw.println(message);
    pw.flush();
  }

  /**
   * Fetch the endpoint handler bean from the Spring context.
   */
  @Override
  public void init() throws ServletException {
    String endpointHandlerBeanName = getInitParameter("endpointHandlerBeanName");
    if(endpointHandlerBeanName == null) {
      endpointHandlerBeanName = InlineAnnotationServlet.DEFAULT_ENDPOINT_HANDLER_BEAN_NAME;
    }

    BeanFactory bf = WebApplicationContextUtils
            .getRequiredWebApplicationContext(getServletContext());
    endpointHandler = (InlineAnnotationService)bf.getBean(
            endpointHandlerBeanName, InlineAnnotationService.class);

    if(endpointHandler == null) {
      throw new ServletException("Could not find handler bean named \""
              + endpointHandlerBeanName + "\"");
    }
  }
}
