package gate.teamware.richui.annotatorgui.filters;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class JnlpResponseWrapper extends HttpServletResponseWrapper {
	private CharArrayWriter output;

	public JnlpResponseWrapper(HttpServletResponse response) {
		super(response);
		this.output = new CharArrayWriter();
	}

	public PrintWriter getWriter() {
		return new PrintWriter(output);
	}

	public ServletOutputStream getOutputStream() throws IOException {
		return new ServletOutputStream() {
			public void write(int arg0) throws IOException {
				output.write(arg0);
			}
		};
	}

	public String toString() {
		return output.toString();
	}
}