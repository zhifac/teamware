/*
 *  DummyDocServiceProxy.java
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
package gleam.docservice.proxy.dummy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gate.creole.annic.Hit;
import gate.AnnotationSet;
import gate.Document;
import gate.Factory;
import gate.creole.ResourceInstantiationException;
import gate.util.InvalidOffsetException;
import gleam.docservice.proxy.AbstractDocServiceProxy;
import gleam.docservice.proxy.DSProxyException;
import gleam.docservice.proxy.IAAAlgorithm;
import gleam.docservice.proxy.IAAResult;
import gleam.docservice.proxy.LRInfo;

/**
 * A dummy implementation of DocServiceProxy which does the simplest
 * possible job and does not require access to a running doc service.
 */
public class DummyDocServiceProxy extends AbstractDocServiceProxy {

  /**
   * Dummy implementation - just creates a single annotation
   * WholeDocument spanning the whole of the document content.
   */
  public void getAnnotationSet(Document doc, String dsAnnotationSetName,
          String localAnnotationSetName, boolean readOnly)
          throws DSProxyException {
    AnnotationSet localAS = null;
    if(localAnnotationSetName == null || localAnnotationSetName.equals("")) {
      localAS = doc.getAnnotations();
    }
    else {
      localAS = doc.getAnnotations(localAnnotationSetName);
    }

    try {
      localAS.add(0l, doc.getContent().size(), "WholeDocument", Factory
              .newFeatureMap());
    }
    catch(InvalidOffsetException ioe) {
      // shouldn't happen...
      throw new DSProxyException(
              "Invalid offset adding WholeDocument annotation", ioe);
    }

    setLockToken(doc, dsAnnotationSetName, "dummyLockToken");
  }

  /**
   * Dummy implementation - always returns the same document.
   */
  @SuppressWarnings("unchecked")
  public Document getDocumentContentOnly(String docID) throws DSProxyException {
    try {
      Document doc = Factory.newDocument(this.getClass().getResource(
              "testDocument.txt"));
      setDocId(doc, docID);
      return doc;
    }
    catch(ResourceInstantiationException rie) {
      throw new DSProxyException("Error creating document", rie);
    }
  }

  @Override
  protected void doRelease(Document doc) throws DSProxyException {
  }

  /**
   * Remove the lock token for this set. We don't actually save the data
   * anywhere.
   */
  public void saveAnnotationSet(Document doc, String localAnnotationSetName,
          String dsAnnotationSetName, boolean keepLock) throws DSProxyException {
    // nothing to do except release the lock
    if(!hasLock(doc, dsAnnotationSetName)) {
      throw new DSProxyException(
              "Attempted to save a non-locked annotation set");
    }
    removeLockToken(doc, dsAnnotationSetName);
  }

  /**
   * Doesn't actually do anything
   */
  public void copyAnnotationSet(String docID, String sourceAnnotationSetName,
          String targetAnnotationSetName) throws DSProxyException {
  }

  /**
   * Set a dummy lock token for this annotation set.
   */
  public void lockAnnotationSet(Document doc, String dsAnnotationSetName)
          throws DSProxyException {
    setLockToken(doc, dsAnnotationSetName, "dummyLockToken");
  }

  /**
   * Return just [null], meaning the document has only the default
   * annotation set.
   */
  public String[] getAnnotationSetNames(Document doc) throws DSProxyException {
    return new String[] {null};
  }

  /**
   * Return just [null], meaning the document has only the default
   * annotation set.
   */
  public String[] getAnnotationSetNames(String docID) throws DSProxyException {
    return new String[] {null};
  }

  /**
   * Always return false.
   */
  public boolean annotationSetNameExists(String docID, String annSetName)
          throws DSProxyException {
    return false;
  }

  /**
   * Always returns false (add failed).
   */
  public boolean addDocumentToCorpus(String corpusID, String documentID)
          throws DSProxyException {
    return false;
  }

  /**
   * Always throws an exception.
   */
  public String createCorpus(String corpusName) throws DSProxyException {
    throw new DSProxyException("DummyDocServiceProxy does not support "
            + "createCorpus method");
  }

  /**
   * Always throws an exception.
   */
  public String createDocument(String documentName, String corpusID,
          byte[] documentXml, String encoding) throws DSProxyException {
    throw new DSProxyException("DummyDocServiceProxy does not support "
            + "createDocument method");
  }

  /**
   * Always throws an exception.
   */
  public void deleteCorpus(String corpusID) throws DSProxyException {
    throw new DSProxyException("DummyDocServiceProxy does not support "
            + "deleteCorpus method");
  }

  /**
   * Always returns false (failed).
   */
  public boolean deleteDocument(String documentID) throws DSProxyException {
    return false;
  }

  /**
   * Always returns false (failed).
   */
  public boolean removeDocumentFromCorpus(String corpusID, String documentID)
          throws DSProxyException {
    return false;
  }

  /**
   * Always returns an empty array.
   */
  public LRInfo[] listCorpora() throws DSProxyException {
    return new LRInfo[0];
  }

  /**
   * Always returns an empty array.
   */
  public LRInfo[] listDocuments(String corpusID) throws DSProxyException {
    return new LRInfo[0];
  }

  /**
   * Always returns null.
   */
  public String getCorpusName(String corpusID) throws DSProxyException {
    return null;
  }

  /**
   * Always throws an exception
   */
  public void setCorpusName(String corpusID, String name)
          throws DSProxyException {
    throw new DSProxyException("DummyDocServiceProxy does not support "
            + "setCorpusName method");
  }

  /**
   * Always returns null.
   */
  public String getCorpusFeature(String corpusID, String featureName)
          throws DSProxyException {
    return null;
  }

  /**
   * Always throws an exception
   */
  public void setCorpusFeature(String corpusID, String featureName,
          String featureValue) throws DSProxyException {
    throw new DSProxyException("DummyDocServiceProxy does not support "
            + "setCorpusFeature method");
  }

  /**
   * Always returns null.
   */
  public String getDocumentName(String documentID) throws DSProxyException {
    return null;
  }

  /**
   * Always throws an exception
   */
  public void setDocumentName(String documentID, String name)
          throws DSProxyException {
    throw new DSProxyException("DummyDocServiceProxy does not support "
            + "setDocumentName method");
  }

  /**
   * Always returns null.
   */
  public String getDocumentFeature(String documentID, String featureName)
          throws DSProxyException {
    return null;
  }

  /**
   * Always throws an exception
   */
  public void setDocumentFeature(String documentID, String featureName,
          String featureValue) throws DSProxyException {
    throw new DSProxyException("DummyDocServiceProxy does not support "
            + "setDocumentFeature method");
  }

  /**
   * Always returns an empty array.
   */
  public Hit[] getNextResults(String searchID, int numResults)
          throws DSProxyException {
    return new Hit[0];
  }

  /**
   * Annic while indexing documents, keeps a record of all possible
   * annotation types and their features. These values are stored in the
   * index and used in the ANNIC GUI. This method will return such a map
   * where key is the annotation type and the value is an array of
   * feature names.
   * 
   * @return a map of features where key is (String) feature name and
   *         value is (String) feature value. this implementation return
   *         an empty map.
   */
  public Map<String, List<String>> getAnnotationTypesForAnnic()
          throws DSProxyException {
    return new HashMap<String, List<String>>();
  }

  /**
   * This method returns the indexed annotation set names
   * 
   * @return
   * @throws DSProxyException
   */
  public String[] getIndexedAnnotationSetNames() throws DSProxyException {
    return new String[0];
  }

  /**
   * Always returns null.
   */
  public String startSearch(String query, String corpusID,
          String annotationSetID, int contextWindow) throws DSProxyException {
    return null;
  }

  public boolean deleteAnnotationSet(Document doc, String docServiceASName)
          throws DSProxyException {
    throw new DSProxyException("Operation not implemented");
  }

  public boolean deleteAnnotationSet(String docID, String asName)
          throws DSProxyException {
    throw new DSProxyException("Operation not implemented");
  }

  public IAAResult calculateIAA(String[] docIDs, String[] asNames,
          String annotationType, String featureName, IAAAlgorithm algorithm)
          throws DSProxyException {
    throw new DSProxyException("Operation not implemented");
  }

  public boolean releaseLock(String taskID) throws DSProxyException {
    throw new DSProxyException("DummyDocServiceProxy does not support "
            + "releaseLock method");
  }

  /**
   * This method returns the document in the format of GATE XML with the
   * given document id.
   */
  public byte[] getDocXML(String docID) throws DSProxyException {
    return null;
  }

  /**
   * @see gate.creole.annic.lucene.StatsCalculator#freq(String, String,
   *      String, String, String)
   */
  public int getFreq(String corpusToSearchIn, String annotationSetToSearchIn,
          String annotationType, String featureName, String value)
          throws DSProxyException {
    return -1;
  }

  public boolean createAnnotationSet(String docID, String annotationSetName)
          throws DSProxyException {

    return true;
  }

}
