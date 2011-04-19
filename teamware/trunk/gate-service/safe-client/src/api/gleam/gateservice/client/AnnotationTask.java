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
