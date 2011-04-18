<%@ include file="/common/taglibs.jsp"%>
<%@ page import="java.util.Calendar, gleam.executive.model.AnnotatorGUILaunchBean" %>

<% 
   String userAgent = request.getHeader("user-agent");
   System.out.println("userAgent: " + userAgent);
   
   if(userAgent.indexOf("MSIE 6")==-1){
       response.setHeader("Expires", "0");
   }
   else {
	   response.setHeader("Cache-Control", "private");
   }
   response.setContentType("application/x-java-jnlp-file");
   response.setHeader("Content-disposition", "attachment; filename=annotator-gui.jnlp");
   response.addDateHeader("Date", Calendar.getInstance().getTime().getTime());
   response.addDateHeader("Last-Modified", Calendar.getInstance().getTime().getTime());
%>


<?xml version="1.0" encoding="utf-8"?>
<!-- JNLP File for Administration Tool -->
<% 
	System.out.println("@@@@@@@@@@@ ANNOTATOR GUI params @@@@@@@@@@@@@");
	
	// codebase
	String codebase = application.getAttribute("urlbase") + "/" + application.getAttribute("webappname") + "/app";
	System.out.println("codebase: " + codebase);
	
	String mode = "";
	String debug = "";
	String load_plugins = "";
	String load_ann_schemas = "";
	String autoconnect = "";
	String ontology_url = "";
	String repository_name = "";
	String owlimservice_url = "";
	String docservice_url = "";
	String executiveservice_url = "";
	String doc_id = "";
	String annotationset_name = "";
	String executive_proxy_factory = "";
	String user = "";
	String password = "";
	String select_as = "";
	String select_ann_types = "";
	String enable_oe = "";
	String enable_application_log = "";
	String classes_to_hide = "";
	String classes_to_show = "";
	AnnotatorGUILaunchBean annotatorGUILaunchBean = (AnnotatorGUILaunchBean)request.getAttribute("annotatorGUILaunchBean");
	if(annotatorGUILaunchBean != null) {
		System.out.println("fetch annotatorGUILaunchBean from request! ");
		// mode
		mode = annotatorGUILaunchBean.getMode();
		System.out.println("mode: " + mode);
	
		// debug = true
		debug = annotatorGUILaunchBean.getDebug();
		System.out.println("debug: " + debug);
	
		// load-plugins = Ontology_Tools,gos
		load_plugins = annotatorGUILaunchBean.getPluginCSVList();
		System.out.println("load-plugins: " + load_plugins);
	
		// load-ann-schemas
		load_ann_schemas = annotatorGUILaunchBean.getAnnotationSchemaCSVURLs();
		System.out.println("load-ann-schemas: " + load_ann_schemas);
	
		// autoconnect = false
		autoconnect = annotatorGUILaunchBean.getAutoconnect();
		System.out.println("autoconnect: " + autoconnect);
	
		// ontology-url
		ontology_url = annotatorGUILaunchBean.getOntologyURL();
		System.out.println("ontology-url: " + ontology_url);
	
		// repository-name
		repository_name = annotatorGUILaunchBean.getRepositoryName();
		System.out.println("repository-name: " + repository_name);
	
		// owlimservice-url
		owlimservice_url = annotatorGUILaunchBean.getOwlimServiceURL();
		if(owlimservice_url==null || "".equals(owlimservice_url)) owlimservice_url = codebase + "/../../owlim-service/services/OWLIMService";
		System.out.println("owlimservice-url: " + owlimservice_url);
	
		// docservice-url
		docservice_url = annotatorGUILaunchBean.getDocServiceURL();
		if(docservice_url==null || "".equals(docservice_url)) docservice_url = codebase + "/../../docservice/services/docservice";
		System.out.println("docservice-url: " + docservice_url);
	
		// executiveservice-url
		executiveservice_url = annotatorGUILaunchBean.getExecutiveServiceURL();
		if(executiveservice_url==null || "".equals(executiveservice_url)) executiveservice_url = codebase + "/../../executive/services/ExecutiveCallbackService";
		System.out.println("executiveservice-url: " + executiveservice_url);
	
		// doc-id
		doc_id = annotatorGUILaunchBean.getDocumentId();
		System.out.println("doc-id: " + doc_id);
	
		// annotationset-name
		annotationset_name = annotatorGUILaunchBean.getAnnotationSetName();
		System.out.println("annotationset-name: " + annotationset_name);
	
		// executive-proxy-factory=gleam.executive.proxy.impl.ExecutiveProxyFactoryImpl
		executive_proxy_factory = "gleam.executive.proxy.impl.RobustByDefaultExecutiveProxyFactoryImpl";
		System.out.println("executive-proxy-factory: " + executive_proxy_factory);
	
		// user
		user = annotatorGUILaunchBean.getUserId();
		System.out.println("user: " + user);
	
		// password
		// ommit
		System.out.println("password: " + password);
	
		// select-as
		select_as = annotatorGUILaunchBean.getSelectAS();
		System.out.println("select-as: " + select_as);
	
		// select-ann-types
		select_ann_types = annotatorGUILaunchBean.getSelectAnnTypes();
		System.out.println("select-ann-types: " + select_ann_types);
	
		// enable-oe
		enable_oe = annotatorGUILaunchBean.getEnableOE();
		if(enable_oe == null || "".equals(enable_oe)) 
			enable_oe = "false";
		System.out.println("enable_oe: " + enable_oe);
	
		// enable-application-log
		enable_application_log = annotatorGUILaunchBean.getEnableApplicationLog();
		if(enable_application_log == null || "".equals(enable_application_log)) 
			enable_application_log = "true";
		System.out.println("enable_application_log: " + enable_application_log);
	
	
		// classes_to_hide
		classes_to_hide = annotatorGUILaunchBean.getClassesToHide();
		if(classes_to_hide == null) classes_to_hide = "";
		System.out.println("classes_to_hide: " + classes_to_hide);
	
		// classes_to_show
		classes_to_show = annotatorGUILaunchBean.getClassesToShow();
		if(classes_to_show == null) classes_to_show = "";
		System.out.println("classes_to_show: " + classes_to_show);
		
	} else {
		// mode
		mode = request.getParameter("mode");
		System.out.println("mode: " + mode);
		
		// debug = true
		debug = request.getParameter("debug");
		System.out.println("debug: " + debug);
		
		// load-plugins = Ontology_Tools,gos
		load_plugins = request.getParameter("load-plugins");
		System.out.println("load-plugins: " + load_plugins);
		
		// load-ann-schemas
		load_ann_schemas = request.getParameter("load-ann-schemas");
		System.out.println("load-ann-schemas: " + load_ann_schemas);
		
		// autoconnect = false
		autoconnect = request.getParameter("autoconnect");
		System.out.println("autoconnect: " + autoconnect);
		
		// ontology-url
		ontology_url = request.getParameter("ontology-url");
		System.out.println("ontology-url: " + ontology_url);
		
		// repository-name
		repository_name = request.getParameter("repository-name");
		System.out.println("repository-name: " + repository_name);
		
		// owlimservice-url
		owlimservice_url = request.getParameter("owlimservice-url");
		if(owlimservice_url==null || "".equals(owlimservice_url)) owlimservice_url = codebase + "/../../owlim-service/services/OWLIMService";
		System.out.println("owlimservice-url: " + owlimservice_url);
		
		// docservice-url
		docservice_url = request.getParameter("docservice-url");
		if(docservice_url==null || "".equals(docservice_url)) docservice_url = codebase + "/../../docservice/services/docservice";
		System.out.println("docservice-url: " + docservice_url);
		
		// executiveservice-url
		executiveservice_url = request.getParameter("executiveservice-url");
		if(executiveservice_url==null || "".equals(executiveservice_url)) executiveservice_url = codebase + "/../../executive/services/ExecutiveCallbackService";
		System.out.println("executiveservice-url: " + executiveservice_url);
		
		// doc-id
		doc_id = request.getParameter("doc-id");
		System.out.println("doc-id: " + doc_id);
		
		// annotationset-name
		annotationset_name = request.getParameter("annotationset-name");
		System.out.println("annotationset-name: " + annotationset_name);
		
		// executive-proxy-factory=gleam.executive.proxy.impl.ExecutiveProxyFactoryImpl
		executive_proxy_factory = request.getParameter("executive-proxy-factory");
		if(executive_proxy_factory==null || "".equals(executive_proxy_factory)) executive_proxy_factory = "gleam.executive.proxy.impl.RobustByDefaultExecutiveProxyFactoryImpl";
		System.out.println("executive-proxy-factory: " + executive_proxy_factory);
		
		// user
		user = request.getParameter("user");
		System.out.println("user: " + user);
		
		// password
		password = request.getParameter("password");
		System.out.println("password: " + password);
		
		// select-as
		select_as = request.getParameter("select-as");
		System.out.println("select-as: " + select_as);
		
		// select-ann-types
		select_ann_types = request.getParameter("select-ann-types");
		System.out.println("select-ann-types: " + select_ann_types);
		
		// enable-oe
		enable_oe = request.getParameter("enable-oe");
		if(enable_oe == null || "".equals(enable_oe)) 
			enable_oe = "false";
		System.out.println("enable_oe: " + enable_oe);
		
		// enable-application-log
		enable_application_log = request.getParameter("enable-application-log");
		if(enable_application_log == null || "".equals(enable_application_log)) 
			enable_application_log = "true";
		System.out.println("enable_application_log: " + enable_application_log);
		
		
		// classes_to_hide
		classes_to_hide = request.getParameter("classes-to-hide");
		if(classes_to_hide == null) classes_to_hide = "";
		System.out.println("classes_to_hide: " + classes_to_hide);
		
		// classes_to_show
		classes_to_show = request.getParameter("classes-to-show");
		if(classes_to_show == null) classes_to_show = "";
		System.out.println("classes_to_show: " + classes_to_show);
	}
		
	System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
%>

<jnlp
  spec="1.5+"
  codebase="<%= codebase %>"
  >
  <information>
  <title>AnnotatorGUI</title>
  <vendor>GATE team - NLP Group - University of Sheffield</vendor>
  <description>The part of the GATE Teamware project</description>
  <icon href="images/gleam.gif"/>
  </information>
  <security>
      <all-permissions/>
  </security>
  <resources>
  <j2se version="1.6+" java-vm-args="-Xmx1024M" max-heap-size="768m"/>
  <extension name="Activation" href="activation.jnlp"/>
  <extension name="Mail" href="mail.jnlp"/>
  <jar href="annotator-gui.jar"/>
  <jar href="executive-proxy-api.jar"/>
  <jar href="executive-proxy-impl.jar"/>
  <jar href="executive-callback-service-api.jar"/>
  <jar href="docservice-proxy-api.jar"/>
  <jar href="docservice-proxy-impl.jar"/>
  <jar href="docservice-api.jar"/>
  <jar href="safe-common.jar"/>
  <jar href="commons-logging-1.1.jar"/>
  <jar href="commons-lang.jar"/>
  <jar href="commons-compress-1.0.jar"/>
  <jar href="commons-io.jar"/>
  <jar href="dom4j-1.6.1.jar"/>
  <jar href="gate.jar"/>
  <jar href="gate-asm.jar"/>
  <jar href="jdom-1.0.jar"/>
  <jar href="junit-4.1.jar"/>
  <jar href="lubm.jar"/>
  <jar href="nekohtml-1.9.14.jar"/>
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
  <jar href="wstx-asl-3.2.8.jar"/>
  <jar href="xercesImpl.jar"/>
  <jar href="xstream-1.3.1.jar"/>
  <jar href="plugins.jar"/>
  <jar href="cxf-2.2.4.jar"/>
  <jar href="FastInfoset-1.2.7.jar"/>
  <jar href="jaxb-xjc-2.0.jar"/>
  <jar href="jaxb-impl-2.1.jar"/>
  <jar href="neethi-2.0.4.jar"/>
  <jar href="saaj-impl-1.3.2.jar"/>
  <jar href="spring.jar"/>
  <jar href="xml-resolver-1.2.jar"/>
  <jar href="XmlSchema-1.4.5.jar"/>
  <jar href="wsdl4j-1.6.2.jar"/>
  <jar href="log4j-1.2.15.jar"/>
  <property name="sid" value="$\{cookie['JSESSIONID'].value\}"/>
  <property name="serviceHost" value="<%=application.getAttribute("urlbase")%>"/>
  <property name="context" value="/executive"/>
  <!--
  <property name="debug" value="true"/>
  <property name="sitecfg" value="<%=codebase%>/gate.xml"/>
  <property name="debug" value="true"/>
  <property name="load-plugins" value="Ontology_Tools,gos,Schema_Annotation_Editor"/>
  <property name="load-ann-schemas" value="${load-ann-schemas}"/>
  <property name="mode" value="${mode}>"/>
  <property name="annotationset-name" value="${annotationset-name}"/>
  <property name="autoconnect" value="${autoconnect}"/>
  <property name="ontology-url" value="${ontology-url}"/>
  <property name="repository-name" value="${repository-name}"/>
  <property name="docservice-url" value="<%=codebase %>/../../docservice/services/docservice"/>
  <property name="owlimservice-url" value="<%=codebase %>/../../owlim-service/services/OWLIMService"/>
  <property name="executiveservice-url" value="<%=codebase %>/../../executive/services/ExecutiveCallbackService"/>
  <property name="doc-id" value="${doc-id}"/>
  <property name="executive-proxy-factory" value="gleam.executive.proxy.impl.ExecutiveProxyFactoryImpl"/>
  <property name="select-as" value="${select-as}"/>
  <property name="select-ann-types" value="${select-ann-types}"/>
 --> 
</resources>
  
  
<application-desc main-class="gleam.annotatorgui.AnnotatorGUI">
	<argument>debug=<%=debug%></argument>
	<argument>sitecfg=<%=codebase%>/gate.xml</argument>
	<argument>load-plugins=<%=load_plugins%></argument>
	<argument>load-ann-schemas=<%=load_ann_schemas%></argument>
	<argument>enable-oe=<%=enable_oe%></argument>
	<argument>enable-application-log=<%=enable_application_log%></argument>
	<argument>classes-to-hide=<%=classes_to_hide%></argument>
	<argument>classes-to-show=<%=classes_to_show%></argument>
	<argument>mode=<%=mode%></argument>
	<argument>autoconnect=<%=autoconnect%></argument>
	<argument>ontology-url=<%=ontology_url%></argument>
	<argument>repository-name=<%=repository_name%></argument>
	<argument>owlimservice-url=<%=owlimservice_url%></argument>
	<argument>doc-id=<%=doc_id%></argument>
	<argument>docservice-url=<%=docservice_url%></argument>
	<argument>annotationset-name=<%=annotationset_name%></argument>
	<argument>executiveservice-url=<%=executiveservice_url%></argument>
	<argument>executive-proxy-factory=<%=executive_proxy_factory%></argument>
	<argument>user=<%=user%></argument>
	<argument>password=<%=password%></argument>
	<argument>select-as=<%=select_as%></argument>
	<argument>select-ann-types=<%=select_ann_types%></argument>
</application-desc>

</jnlp>
