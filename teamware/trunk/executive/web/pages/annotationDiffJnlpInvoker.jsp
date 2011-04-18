<%@ include file="/common/taglibs.jsp"%>
<%@ page
	import="java.util.Calendar,gleam.executive.model.AnnotationDiffGUILaunchBean"%>
<%
	String userAgent = request.getHeader("user-agent");
	System.out.println("userAgent: " + userAgent);

	if (userAgent.indexOf("MSIE 6") == -1) {
		response.setHeader("Expires", "0");
	} else {
		response.setHeader("Cache-Control", "private");
	}
	response.setContentType("application/x-java-jnlp-file");
	response.setHeader("Content-disposition", "attachment; filename=annotation-diff.jnlp");
	response.addDateHeader("Date", Calendar.getInstance().getTime()
			.getTime());
	response.addDateHeader("Last-Modified", Calendar.getInstance()
			.getTime().getTime());
%>


<?xml version="1.0" encoding="utf-8"?>
<!-- JNLP File for Administration Tool -->
<%
	System.out
			.println("@@@@@@@@@@@ ANNOTATION DIFF GUI params @@@@@@@@@@@@@");

	// codebase
	String codebase = application.getAttribute("urlbase") + "/" + application.getAttribute("webappname") + "/app";
	System.out.println("codebase: " + codebase);

	String debug = "";
	String docservice_url = "";
	String doc_id = "";
	String autoconnect = "";

	AnnotationDiffGUILaunchBean annotationDiffGUILaunchBean = (AnnotationDiffGUILaunchBean) request
			.getAttribute("annotationDiffGUILaunchBean");

	if (annotationDiffGUILaunchBean != null) {
		System.out
				.println("fetched annotationDiffGUILaunchBean from request ");
		// docservice-url
		docservice_url = annotationDiffGUILaunchBean.getDocServiceURL();
		if (docservice_url == null || "".equals(docservice_url))
			docservice_url = codebase
					+ "/../../docservice/services/docservice";
		System.out.println("docservice-url: " + docservice_url);

		// doc-id
		doc_id = annotationDiffGUILaunchBean.getDocumentId();
		System.out.println("doc-id: " + doc_id);

		// autoconnect = true
		autoconnect = annotationDiffGUILaunchBean.getAutoconnect();
		System.out.println("autoconnect: " + autoconnect);
	} else {
		// debug = true
		debug = request.getParameter("debug");
		System.out.println("debug: " + debug);

		// docservice-url
		docservice_url = request.getParameter("docservice-url");
		if (docservice_url == null || "".equals(docservice_url))
			docservice_url = codebase
					+ "/../../docservice/services/docservice";
		System.out.println("docservice-url: " + docservice_url);

		// doc-id
		doc_id = request.getParameter("doc-id");
		System.out.println("doc-id: " + doc_id);

		// autoconnect = true
		autoconnect = request.getParameter("autoconnect");
		System.out.println("autoconnect: " + autoconnect);
	}
	System.out
			.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
%>

<jnlp spec="1.5+" codebase="<%=codebase%>">
<information>
<title>AnnotationDiffGUI</title>
<vendor>
GATE team - NLP Group - University of Sheffield
</vendor>
<description>
The part of the GATE Teamware project
</description>
<icon href="images/gleam.gif" />
</information>
<security>
<all-permissions />
</security>
<resources>
<j2se version="1.6+" java-vm-args="-Xmx1024M" max-heap-size="768m" />
<extension name="Activation" href="activation.jnlp" />
<extension name="Mail" href="mail.jnlp" />
<jar href="annotation-diff-gui.jar" />
<jar href="docservice-proxy-api.jar" />
<jar href="docservice-proxy-impl.jar" />
<jar href="docservice-api.jar" />
<jar href="safe-common.jar" />
  <jar href="commons-logging-1.1.jar"/>
  <jar href="commons-lang.jar"/>
  <jar href="commons-compress-1.0.jar"/>
  <jar href="commons-io.jar"/>
  <jar href="dom4j-1.6.1.jar"/>
<jar href="gate.jar" />
<jar href="gate-asm.jar" />
<jar href="jdom-1.0.jar" />
<jar href="junit-4.1.jar" />
<jar href="lubm.jar" />
<jar href="nekohtml-1.9.14.jar" />
  <jar href="fontbox-1.1.0.jar"/>
  <jar href="jempbox-1.1.0.jar"/>
  <jar href="pdfbox-1.1.0.jar"/>
  <jar href="poi-3.6.jar"/>
  <jar href="poi-ooxml-3.6.jar"/>
  <jar href="poi-ooxml-schemas-3.6.jar"/>
  <jar href="poi-scratchpad-3.6.jar"/>
  <jar href="tika-core-0.7.jar"/>
  <jar href="tika-parsers-0.7.jar"/>
  <jar href="xmlbeans-2.3.0.jar"/>
<jar href="wstx-asl-3.2.8.jar" />

<jar href="xercesImpl.jar" />
<jar href="xstream-1.3.1.jar" />
<jar href="cxf-2.2.4.jar" />
<jar href="FastInfoset-1.2.7.jar" />
<jar href="jaxb-xjc-2.0.jar" />
<jar href="jaxb-impl-2.1.jar" />
<jar href="neethi-2.0.4.jar" />
<jar href="saaj-impl-1.3.2.jar" />
<jar href="spring.jar" />
<jar href="xml-resolver-1.2.jar" />
<jar href="XmlSchema-1.4.5.jar" />
<jar href="wsdl4j-1.6.2.jar" />
<jar href="log4j-1.2.15.jar" />

<property name="sid" value="$\{cookie['JSESSIONID'].value\}" />
<property name="serviceHost" value="<%=application.getAttribute("urlbase")%>" />
<property name="context" value="/executive" />
<property name="autoconnect" value="${autoconnect}" />
</resources>


<application-desc main-class="gleam.annotationdiffgui.AnnotationDiffGUI">
<argument>
debug=<%=debug%></argument>
<argument>
sitecfg=<%=codebase%>/gate.xml
</argument>
<argument>
doc-id=<%=doc_id%></argument>
<argument>
docservice-url=<%=docservice_url%></argument>
<argument>
autoconnect=<%=autoconnect%></argument>
</application-desc>

</jnlp>
