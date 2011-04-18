/*
 * This file is part of GLEAM, licenced under the GNU Lesser General Public
 * Licence.
 *
 * (c) 2006 The University of Sheffield
 * 
 * $Id$
 */
package gleam.gateservice.endpoint;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * JAXB class encapsulating a document ID/annotation set mappings
 * pair for processRemoteDocuments.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "annotationTask", namespace = "http://gate.ac.uk/ns/safe/1.1/gate-service",
        propOrder = {"docId", "annotationSets"})
public class AnnotationTask {
  protected String docId;

  protected List<AnnotationSetMapping> annotationSets;
  
  public AnnotationTask() {
  }
  
  public AnnotationTask(String docId, List<AnnotationSetMapping> annotationSets) {
    this.docId = docId;
    this.annotationSets = annotationSets;
  }

  public String getDocId() {
    return docId;
  }

  public void setDocId(String docId) {
    this.docId = docId;
  }

  /**
   * This accessor method returns a reference to the live list, not a
   * snapshot. Therefore any modification you make to the returned list
   * will be present inside the JAXB object.
   */
  public List<AnnotationSetMapping> getAnnotationSets() {
    if(annotationSets == null) {
      annotationSets = new ArrayList<AnnotationSetMapping>();
    }
    return annotationSets;
  }
  
  public void setAnnotationSets(List<AnnotationSetMapping> annotationSets) {
    this.annotationSets = annotationSets;
  }
}
