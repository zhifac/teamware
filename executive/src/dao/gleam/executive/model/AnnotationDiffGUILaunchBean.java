/**
 * 
 */
package gleam.executive.model;


/**
 * @author agaton
 *
 */
public class AnnotationDiffGUILaunchBean {

	final static String DEFAULT_DOCSERVICE_URL = "";
	final static String DEFAULT_DOCUMENT_ID = "";
	final static String DEFAULT_AUTOCONNECT = "true";
	
	String docServiceURL;
	String documentId;
	String autoconnect;

	public AnnotationDiffGUILaunchBean(){
		docServiceURL = DEFAULT_DOCSERVICE_URL;
		documentId = DEFAULT_DOCUMENT_ID;
		autoconnect = DEFAULT_AUTOCONNECT;
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

	
	public String getAutoconnect() {
		return autoconnect;
	}


	public void setAutoconnect(String autoconnect) {
		this.autoconnect = autoconnect;
	}

	public String toString() {
		    StringBuilder sb = new StringBuilder()
		            .append("?docservice-url=")
		            .append(docServiceURL)
		            .append("&doc-id=")
		            .append(documentId)
		            .append("&autoconnect=")
		            .append(autoconnect);
		    
		    return sb.toString();
     }

}
