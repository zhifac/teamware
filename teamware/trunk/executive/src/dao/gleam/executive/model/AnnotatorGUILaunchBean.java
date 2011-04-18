/**
 * 
 */
package gleam.executive.model;


/**
 * @author agaton
 *
 */
public class AnnotatorGUILaunchBean {

	final static String DEFAULT_DOCSERVICE_URL = "";
	final static String DEFAULT_DOCUMENT_ID = "";
	final static String DEFAULT_ANNOTATION_SCHEMA_CSV_URLS = "";
	final static String DEFAULT_ANNOTATION_SET_NAME = "";
	final static String DEFAULT_ONTOLOGY_URL = "";
	final static String DEFAULT_MODE = "direct";
	final static String DEFAULT_AUTOCONNECT = "true";
	final static String DEFAULT_USER_ID = "";
	final static String DEFAULT_OWLIM_SERVICE_URL = "";
	final static String DEFAULT_REPOSITORY_NAME = "";
	final static String DEFAULT_PLUGIN_CSV_LIST = "Ontology_Tools,gos";
	final static String DEFAULT_DEBUG = "true";
	final static String DEFAULT_EXECUTIVE_SERVICE_URL = "";
	final static String DEFAULT_SELECT_AS = "";
	final static String DEFAULT_SELECT_ANN_TYPES = "";
	final static String DEFAULT_ENABLE_OE = "false";
	final static String DEFAULT_ENABLE_APPLICATION_LOG = "true";
	final static String DEFAULT_CLASSES_TO_HIDE = "";
	final static String DEFAULT_CLASSES_TO_SHOW = "";

	String docServiceURL;
	String documentId;
	String annotationSchemaCSVURLs;
	String annotationSetName;
	String ontologyURL;
	String mode;
	String autoconnect;
	String userId;
	String owlimServiceURL;
	String repositoryName;
	String pluginCSVList;
	String debug;
	String executiveServiceURL;
	String selectAS;
	String selectAnnTypes;
	String enableOE;
	String enableApplicationLog;
	String classesToHide;
	String classesToShow;
	
	
	

	public AnnotatorGUILaunchBean(){
		docServiceURL = DEFAULT_DOCSERVICE_URL;
		documentId = DEFAULT_DOCUMENT_ID;
		annotationSchemaCSVURLs = DEFAULT_ANNOTATION_SCHEMA_CSV_URLS;
		annotationSetName = DEFAULT_ANNOTATION_SET_NAME;
		ontologyURL = DEFAULT_ONTOLOGY_URL;
		mode = DEFAULT_MODE; 
		autoconnect = DEFAULT_AUTOCONNECT;
		userId = DEFAULT_USER_ID;
		owlimServiceURL = DEFAULT_OWLIM_SERVICE_URL;
		repositoryName = DEFAULT_REPOSITORY_NAME;
		pluginCSVList = DEFAULT_PLUGIN_CSV_LIST;
		debug = DEFAULT_DEBUG;
		executiveServiceURL = DEFAULT_EXECUTIVE_SERVICE_URL;
		selectAS = DEFAULT_SELECT_AS;
		selectAnnTypes = DEFAULT_SELECT_ANN_TYPES;
		enableOE = DEFAULT_ENABLE_OE;
		enableApplicationLog = DEFAULT_ENABLE_APPLICATION_LOG;
		classesToHide = DEFAULT_CLASSES_TO_HIDE;
		classesToShow = DEFAULT_CLASSES_TO_SHOW;
   	}


	public String getAnnotationSchemaCSVURLs() {
		return annotationSchemaCSVURLs;
	}


	public void setAnnotationSchemaCSVURLs(String annotationSchemaCSVURLs) {
		this.annotationSchemaCSVURLs = annotationSchemaCSVURLs;
	}


	public String getAnnotationSetName() {
		return annotationSetName;
	}


	public void setAnnotationSetName(String annotationSetName) {
		this.annotationSetName = annotationSetName;
	}


	public String getAutoconnect() {
		return autoconnect;
	}


	public void setAutoconnect(String autoconnect) {
		this.autoconnect = autoconnect;
	}


	public String getDocServiceURL() {
		return docServiceURL;
	}


	public void setDocServiceURL(String docServiceURL) {
		this.docServiceURL = docServiceURL;
	}


	public String getDocumentId() {
		return documentId;
	}


	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}


	public String getMode() {
		return mode;
	}


	public void setMode(String mode) {
		this.mode = mode;
	}


	public String getOntologyURL() {
		return ontologyURL;
	}


	public void setOntologyURL(String ontologyURL) {
		this.ontologyURL = ontologyURL;
	}


	public String getOwlimServiceURL() {
		return owlimServiceURL;
	}


	public void setOwlimServiceURL(String owlimServiceURL) {
		this.owlimServiceURL = owlimServiceURL;
	}


	public String getRepositoryName() {
		return repositoryName;
	}


	public void setRepositoryName(String repositoryName) {
		this.repositoryName = repositoryName;
	}


	public String getUserId() {
		return userId;
	}


	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public String getPluginCSVList() {
		return pluginCSVList;
	}


	public void setPluginCSVList(String pluginCSVList) {
		this.pluginCSVList = pluginCSVList;
	}
	
	public String getSelectAnnTypes() {
		return selectAnnTypes;
	}


	public void setSelectAnnTypes(String selectAnnTypes) {
		this.selectAnnTypes = selectAnnTypes;
	}


	public String getSelectAS() {
		return selectAS;
	}


	public void setSelectAS(String selectAS) {
		this.selectAS = selectAS;
	}
	
	
	public String getDebug() {
		return debug;
	}


	public void setDebug(String debug) {
		this.debug = debug;
	}


	public String getExecutiveServiceURL() {
		return executiveServiceURL;
	}


	public void setExecutiveServiceURL(String executiveServiceURL) {
		this.executiveServiceURL = executiveServiceURL;
	}
	
	public String getEnableOE() {
		return this.enableOE;
	}

	public void setEnableOE(String enableOE) {
		this.enableOE = enableOE;
	}

	public String getEnableApplicationLog() {
		return this.enableApplicationLog;
	}

	public void setEnableApplicationLog(String enableApplicationLog) {
		this.enableApplicationLog = enableApplicationLog;
	}

	
	public String getClassesToHide() {
		return this.classesToHide;
	}

	public void setClassesToHide(String classesToHide) {
		this.classesToHide = classesToHide;
	}
	
	public String getClassesToShow() {
		return this.classesToShow;
	}

	public void setClassesToShow(String classesToShow) {
		this.classesToShow = classesToShow;
	}
	
	public String toString() {
		    StringBuilder sb = new StringBuilder()
		            .append("?docservice-url=")
		            .append(docServiceURL)
		            .append("&doc-id=")
		            .append(documentId)
		            .append("&load-ann-schemas=")
		            .append(annotationSchemaCSVURLs)
		            .append("&annotationset-name=")
		            .append(annotationSetName)
		            .append("&ontology-url=")
		            .append(ontologyURL)
		            .append("&mode=")
		            .append(mode)
		            .append("&autoconnect=")
		            .append(autoconnect)
		            .append("&user=")
		            .append(userId)
		            .append("&owlimservice-url=")
		            .append(owlimServiceURL)
		            .append("&repository-name=")
		            .append(repositoryName)
		            .append("&load-plugins=")
		            .append(pluginCSVList)
		            .append("&debug=")
		            .append(debug)
		            .append("&executiveservice-url=")
		            .append(executiveServiceURL)
		            .append("&select-as=")
		            .append(selectAS)
		            .append("&select-ann-types=")
		            .append(selectAnnTypes)
		            .append("&enable-oe=")
		            .append(enableOE)
		            .append("&enable-application-log=")
		            .append(enableApplicationLog)
		            .append("&classes-to-hide=")
		            .append(classesToHide)
		            .append("&classes-to-show=")
		            .append(classesToShow);

		    return sb.toString();
     }
}
