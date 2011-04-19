/*
 *  AnnotationTask.java
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
