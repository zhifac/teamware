/*
 *  JnlpResponseWrapper.java
 *
 *  Copyright (c) 2006-2011, The University of Sheffield.
 *
 *  This file is part of GATE Teamware (see http://gate.ac.uk/teamware/), 
 *  and is free software, licenced under the GNU Affero General Public License,
 *  Version 3, November 2007 (also included with this distribution as file 
 *  LICENCE-AGPL3.html).
 *
 *  A commercial licence is also available for organisations whose business
 *  models preclude the adoption of open source and is subject to a licence
 *  fee charged by the University of Sheffield. Please contact the GATE team
 *  (see http://gate.ac.uk/g8/contact) if you require a commercial licence.
 *
 *  $Id$
 */
package gate.teamware.richui.annotationdiffgui.filters;

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
