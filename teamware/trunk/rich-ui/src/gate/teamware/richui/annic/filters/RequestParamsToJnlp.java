package gate.teamware.richui.annic.filters;

import gate.teamware.richui.annic.Constants;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.util.Enumeration;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class RequestParamsToJnlp implements Filter, Constants {
	public static boolean DEBUG = true;

	private FilterConfig filterConfig = null;

	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig;
	}

	public void destroy() {
		this.filterConfig = null;
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {
    if (DEBUG) {
      System.out.println("DEBUG: RequestParamsToJnlp.doFilter(): request = " + request);
    }
		Enumeration en = request.getParameterNames();
		if (en.hasMoreElements()) {
			PrintWriter out = response.getWriter();
			JnlpResponseWrapper responseWrapper = new JnlpResponseWrapper((HttpServletResponse) response);
			chain.doFilter(request, responseWrapper);
			// Get response from servlet
			String xmlContent = responseWrapper.toString();
			try {
				for (; en.hasMoreElements();) {
					String key = (String) en.nextElement();
					String value = request.getParameter(key);
					xmlContent = addParam(xmlContent, key, value);
				}
				response.setContentLength(xmlContent.length());
				out.write(xmlContent);
			} catch (Exception ex) {
				ex.printStackTrace();
				out.println(ex.toString());
				out.write(responseWrapper.toString());
			}
		} else {
			chain.doFilter(request, response);
		}
	}

	/**
	 * Transforms a given xmlContent(JNLP) to add a given parameter value to JNLP.<br>
	 * Name of the XSL style sheet for the parameter should be
	 * "add-request-param-" + parameterName + ".xsl".<br>
	 */
	private String addParam(String xmlContent, String paramName, String paramValue) throws TransformerException {
		String styleSheet = "add-request-param-" + paramName + ".xsl";
		if (DEBUG) {
			System.out.println("DEBUG: RequestParamsToJnlp.addParam(): paramName = " + paramName + ", paramValue ="
					+ paramValue + ", styleSheet = " + styleSheet);
		}
		String stylePath = filterConfig.getServletContext().getRealPath(styleSheet);
		try {
			Source styleSource = new StreamSource(stylePath);
			if (styleSource == null) {
				System.out.println("WARNING: Filed to load transformation style sheet. (" + styleSheet + ")");
				return xmlContent;
			} else {
				StringReader sr = new StringReader(xmlContent);
				Source xmlSource = new StreamSource((Reader) sr);

				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer(styleSource);
				transformer.setParameter(paramName, paramValue);
				CharArrayWriter caw = new CharArrayWriter();
				StreamResult result = new StreamResult(caw);
				transformer.transform(xmlSource, result);
				return caw.toString();
			}
		} catch (Exception e) {
			System.out.println("WARNING: Filed to load transformation style sheet. (" + styleSheet + ")");
			return xmlContent;
		}
	}
}
