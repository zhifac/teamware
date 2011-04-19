/*
 *  AbstractDocServiceProxy.java
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
package gleam.docservice.proxy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gate.Document;
import gate.FeatureMap;

/**
 * Abstract class providing convenience methods for subclasses to use
 * for managing locks and the document ID.
 */
public abstract class AbstractDocServiceProxy implements DocServiceProxy {

  /**
   * The feature name used to store the doc-service document ID.
   */
  protected static final String DOC_ID_FEATURE = "gleam.docservice.proxy.docID";

  /**
   * The feature name used to store the map of annotation set lock
   * tokens.
   */
  protected static final String AS_LOCKS_FEATURE = "gleam.docservice.proxy.annotationSetLocks";

  /**
   * Check whether this document holds a lock for the given annotation
   * set name. The set name may be null, denoting the default annotation
   * set.
   * 
   * @param doc the document.
   * @param dsAnnotationSetName the annotation set name in the doc
   *          service.
   * @return
   */
  @SuppressWarnings("unchecked")
  protected boolean hasLock(Document doc, String dsAnnotationSetName) {
    Map<String, String> asLocks = (Map<String, String>)((FeatureMap)doc
            .getFeatures()).get(AS_LOCKS_FEATURE);
    if(asLocks != null) {
      return asLocks.containsKey(dsAnnotationSetName);
    }
    return false;
  }

  /**
   * Fetch the lock token for the given annotation set name. The set
   * name may be null, denoting the default annotation set.
   * 
   * @param doc the document.
   * @param dsAnnotationSetName the annotation set name in the doc
   *          service.
   * @return the lock token, or null if the document has no lock for
   *         this annotation set.
   */
  @SuppressWarnings("unchecked")
  protected String getLockToken(Document doc, String dsAnnotationSetName) {
    Map<String, String> asLocks = (Map<String, String>)((FeatureMap)doc
            .getFeatures()).get(AS_LOCKS_FEATURE);
    if(asLocks != null) {
      return asLocks.get(dsAnnotationSetName);
    }
    return null;
  }

  /**
   * Fetch all lock tokens for the given document. The return value is a
   * {@link Map} from annotation set names to lock tokens. The map may
   * contain a null key, corresponding to the default (unnamed)
   * annotation set.
   * 
   * @param doc the document.
   * @return the lock token mapping.
   */
  @SuppressWarnings("unchecked")
  protected Map<String, String> getLockTokens(Document doc) {
    return (Map<String, String>)doc.getFeatures().get(AS_LOCKS_FEATURE);
  }

  /**
   * Set the lock token for the given annotation set.
   * 
   * @param doc the document
   * @param dsAnnotationSetName the annotation set to lock
   * @param lockToken the lock token to use
   */
  @SuppressWarnings("unchecked")
  protected void setLockToken(Document doc, String dsAnnotationSetName,
          String lockToken) {
    Map<String, String> asLocks = (Map<String, String>)doc.getFeatures().get(
            AS_LOCKS_FEATURE);
    if(asLocks == null) {
      asLocks = new HashMap<String, String>();
      doc.getFeatures().put(AS_LOCKS_FEATURE, asLocks);
    }

    asLocks.put(dsAnnotationSetName, lockToken);
  }

  /**
   * Remove our lock token for the given annotation set. This does not
   * release the lock in the remote doc service, which is the
   * responsibility of the subclass.
   * 
   * @param doc the document.
   * @param dsAnnotationSetName the annotation set name in the doc
   *          service.
   */
  @SuppressWarnings("unchecked")
  protected void removeLockToken(Document doc, String dsAnnotationSetName) {
    Map<String, String> asLocks = (Map<String, String>)((FeatureMap)doc
            .getFeatures()).get(AS_LOCKS_FEATURE);
    if(asLocks != null) {
      asLocks.remove(dsAnnotationSetName);
    }
  }

  /**
   * Removes all lock tokens from the document.
   * 
   * @param doc the document.
   */
  @SuppressWarnings("unchecked")
  protected void removeAllLockTokens(Document doc) {
    doc.getFeatures().remove(AS_LOCKS_FEATURE);
  }

  /**
   * Releases all locks. The actual releasing is done by
   * {@link #doRelease(Document)}, this method also removes the lock
   * tokens.
   */
  public void release(Document doc) throws DSProxyException {
    doRelease(doc);
    removeAllLockTokens(doc);
  }

  /**
   * Release all remaining locks in the remote document service.
   * 
   * @param doc the document.
   * @throws DSProxyException
   */
  protected abstract void doRelease(Document doc) throws DSProxyException;

  /**
   * Annic while indexing documents, keeps a record of all possible annotation
   * types and their features. These values are stored in the index and used
   * in the ANNIC GUI. This method will return such a map where key is the
   * annotation type and the value is an array of feature names.
   * 
   * @return a map of features where key is (String) feature name and value is
   *         (String) feature value
   */
  public abstract Map<String, List<String>> getAnnotationTypesForAnnic() throws DSProxyException;

  /**
   * Returns the doc service document ID for this document.
   */
  protected String getDocId(Document doc) {
    return (String)doc.getFeatures().get(DOC_ID_FEATURE);
  }

  /**
   * Store the ID of this document.
   */
  @SuppressWarnings("unchecked")
  protected void setDocId(Document doc, String id) {
    doc.getFeatures().put(DOC_ID_FEATURE, id);
  }
  
  /**
   * Simple implementation of the three-parameter saveAnnotationSet method
   * which delegates to the four-parameter version.
   */
  public void saveAnnotationSet(Document doc, String localAnnotationSetName,
          String dsAnnotationSetName) throws DSProxyException {
    saveAnnotationSet(doc, localAnnotationSetName, dsAnnotationSetName, false);
  }
}
