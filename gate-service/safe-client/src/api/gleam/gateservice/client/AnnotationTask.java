package gleam.gateservice.client;

import java.util.HashMap;
import java.util.Map;

/**
 * Java Bean encapsulating a single annotation task (a document ID and a
 * Map saying which annotation set from the doc service should be used
 * for each AS name expected by the GaS).
 */
public class AnnotationTask {
  /**
   * The ID of the document to process.
   */
  private String docID;

  /**
   * The annotation set mappings for this task. Keys are the annotation
   * set names the GaS expects, and values are the annotation set names
   * in the doc service to which these map.
   */
  private Map<String, String> annotationSetMappings;

  public AnnotationTask(String docID) {
    this.docID = docID;
  }

  public AnnotationTask(String docID, Map<String, String> annotationSetMappings) {
    this.docID = docID;
    this.annotationSetMappings = annotationSetMappings;
  }

  public String getDocID() {
    return docID;
  }

  public void setDocID(String docID) {
    this.docID = docID;
  }

  public Map<String, String> getAnnotationSetMappings() {
    return annotationSetMappings;
  }

  public void setAnnotationSetMappings(Map<String, String> annotationSetMappings) {
    this.annotationSetMappings = annotationSetMappings;
  }

  /**
   * Add a single annotation set mapping.
   * 
   * @param gasASName the annotation set name expected by the GaS
   * @param docServiceASName the annotation set from the doc service to
   *          use for this name
   */
  public void addAnnotationSetMapping(String gasASName, String docServiceASName) {
    if(annotationSetMappings == null) {
      annotationSetMappings = new HashMap<String, String>();
    }
    annotationSetMappings.put(gasASName, docServiceASName);
  }
}
