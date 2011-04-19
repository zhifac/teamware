/*
 *  DocServiceProxyImpl.java
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
package gleam.docservice.proxy.impl;

import gate.AnnotationSet;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.corpora.DocumentImpl;
import gate.corpora.DocumentStaxUtils;
import gate.creole.ResourceInstantiationException;
import gate.creole.annic.Constants;
import gate.creole.annic.Hit;
import gleam.docservice.AnnotationSetHandle;
import gleam.docservice.CorpusInfo;
import gleam.docservice.DocServiceException;
import gleam.docservice.DocumentInfo;
import gleam.docservice.DocService;
import gleam.docservice.proxy.AbstractDocServiceProxy;
import gleam.docservice.proxy.DSProxyException;
import gleam.docservice.proxy.DocServiceProxy;
import gleam.docservice.proxy.IAAAlgorithm;
import gleam.docservice.proxy.IAAResult;
import gleam.docservice.proxy.LRInfo;
import gleam.docservice.proxy.impl.iaa.AllWaysFMeasureIAAResultImpl;
import gleam.docservice.proxy.impl.iaa.AllWaysKappaIAAResultImpl;
import gleam.docservice.proxy.impl.iaa.PairwiseFMeasureIAAResultImpl;
import gleam.docservice.proxy.impl.iaa.PairwiseKappaIAAResultImpl;
import gleam.util.adapters.MapEntry;
import gleam.util.adapters.MapWrapper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Implementation of {@link DocServiceProxy} which communicates with a
 * remote doc service via SOAP.
 */
public class DocServiceProxyImpl extends AbstractDocServiceProxy {

  private static final Log log = LogFactory.getLog(DocServiceProxyImpl.class);

  /**
   * StAX input factory used to parse annotation set XML retrieved from
   * the doc service.
   */
  private static XMLInputFactory inputFactory = XMLInputFactory.newInstance();

  /**
   * StAX output factory used to write annotation sets as XML to send to
   * the doc service.
   */
  private static XMLOutputFactory outputFactory = XMLOutputFactory
          .newInstance();

  /**
   * The Axis client stub used to access the real doc service.
   */
  private DocService sds;

  /**
   * Map used to keep track of the Futures for tasks that refresh locks.
   */
  private Map<String, ScheduledFuture<?>> keepaliveTasks;

  /**
   * Executor used to make keepalive calls.
   */
  private ScheduledExecutorService keepaliveExecutor;

  /**
   * Interval between lock keepalives. A value of zero means we do not
   * attempt to keep locks alive.
   */
  private int keepaliveInterval = 0;

  /**
   * Creates a proxy for the given doc service.
   * 
   * @param sds the Axis-generated client stub used to access the web
   *          service.
   */
  public DocServiceProxyImpl(DocService sds) {
    this.sds = sds;
  }

  /**
   * Set the keepalive interval. If a non-zero interval is passed, this
   * lazily creates the relevant map and executor.
   */
  public void setKeepaliveInterval(int keepaliveInterval) {
    this.keepaliveInterval = keepaliveInterval;
    if(keepaliveInterval > 0) {
      if(keepaliveTasks == null) {
        keepaliveTasks = new HashMap<String, ScheduledFuture<?>>();
      }
      if(keepaliveExecutor == null) {
        // create an executor that runs its tasks in daemon threads, so
        // as not to prevent the VM exiting
        keepaliveExecutor = Executors
                .newSingleThreadScheduledExecutor(new ThreadFactory() {
                  private ThreadFactory defaultTF = Executors
                          .defaultThreadFactory();

                  public Thread newThread(Runnable r) {
                    Thread t = defaultTF.newThread(r);
                    t.setDaemon(true);
                    return t;
                  }
                });
      }
    }
  }
  
  /**
   * Cancel any periodic tasks that are keeping the given lock alive.
   */
  private void cancelKeepalives(String lockToken) {
    if(keepaliveInterval > 0) {
      ScheduledFuture<?> future = keepaliveTasks.get(lockToken);
      if(future != null) {
        future.cancel(false);
      }
    }
  }

  /**
   * Release all remaining locks in the remote document service.
   * 
   * @param doc the document.
   * @throws DSProxyException
   */
  @Override
  protected void doRelease(Document doc) throws DSProxyException {
    Map<String, String> locks = getLockTokens(doc);
    if(locks != null) {
      for(Map.Entry<String, String> lock : locks.entrySet()) {
        try {
          sds.releaseLock(lock.getValue());
          cancelKeepalives(lock.getValue());
        }
        catch(Exception e) {
          // just log the failure
          log.info("Error releasing lock for annotation set " + lock.getKey(),
                  e);
        }
      }
    }
  }

  /**
   * Fetch an annotation set from the remote doc service and add it to
   * the given document.
   * 
   * @param doc the document to use. It must have been obtained from a
   *          previous call to {@link #getDocumentContentOnly(String)}.
   * @param dsAnnotationSetName the name of the annotation set to
   *          retrieve from the doc service. This may be
   *          <code>null</code>, in which case the default set will
   *          be retrieved.
   * @param localAnnotationSetName the name under which the annotation
   *          set is to be stored on the local document. This may be
   *          <code>null</code>, in which case the default set is
   *          used. Any existing annotations on the target set are
   *          removed.
   * @param readOnly whether the set should be fetched read-only or
   *          read-write. Sets which are fetched read-only cannot
   *          subsequently be saved.
   * @throws DSProxyException
   */
  public void getAnnotationSet(Document doc, String dsAnnotationSetName,
          String localAnnotationSetName, boolean readOnly)
          throws DSProxyException {
    String docID = getDocId(doc);
    if(docID == null) {
      log.error("No document ID found for document " + doc);
      throw new DSProxyException("Invalid document - document ID not found");
    }

    AnnotationSetHandle asHandle = null;
    Exception thrownException = null;
    try {
      asHandle = sds.getAnnotationSet(docID, dsAnnotationSetName, readOnly);
      AnnotationSet localAS = null;
      if(localAnnotationSetName == null || localAnnotationSetName.equals("")) {
        localAS = doc.getAnnotations();
      }
      else {
        localAS = doc.getAnnotations(localAnnotationSetName);
      }

      if(asHandle.getData() != null) {
        XMLStreamReader xsr = inputFactory.createXMLStreamReader(
                new ByteArrayInputStream(asHandle.getData()), "UTF-8");

        // find the initial AnnotationSet tag
        xsr.nextTag();
        xsr.require(XMLStreamConstants.START_ELEMENT, null, "AnnotationSet");

        // parse the XML
        TreeSet<Integer> allIDs = new TreeSet<Integer>();
        DocumentStaxUtils.readAnnotationSet(xsr, localAS, null, allIDs,
                Boolean.TRUE);

        // try and make sure annotation IDs in this set stay unique
        if(!allIDs.isEmpty()) {
          if(doc instanceof DocumentImpl) {
            Integer maxID = allIDs.last();
            Integer nextAnnotID = ((DocumentImpl)doc).getNextAnnotationId();
            if(nextAnnotID == null || maxID.compareTo(nextAnnotID) >= 0) {
              ((DocumentImpl)doc).setNextAnnotationId(maxID + 1);
            }
          }
        }

        xsr.close();
      }
      // store the lock token, if necessary
      if(!readOnly) {
        setLockToken(doc, dsAnnotationSetName, asHandle.getTaskID());
      }
    }
    catch(XMLStreamException e) {
      thrownException = e;
    }
    catch(Exception e) {
      log.error("Exception accessing doc service", e);
      throw new DSProxyException(
              "Error accessing doc service. Failed method getAnnotationSet for Document: "
                      + docID, e);
    }

    if(thrownException != null) {
      log.error("Exception parsing annotation set XML from doc service "
              + "for document ID " + docID);

      // since the actual remote call originally succeeded, we must
      // explicitly release the lock
      if(asHandle.getTaskID() != null) {
        try {
          sds.releaseLock(asHandle.getTaskID());
        }
        catch(Exception re) {
          // oh well, we tried. Log the failure
          log.debug("Failed to release lock " + asHandle.getTaskID()
                  + " for document " + docID);
        }
      }
      throw new DSProxyException("Error parsing XML from doc service",
              thrownException);
    }

    // start a task to keep the lock alive if necessary
    if(!readOnly && keepaliveInterval > 0) {
      startKeepaliveTask(asHandle.getTaskID());
    }
  }

  /**
   * Start a task that will make periodic keepalive calls to the doc
   * service for the given lock token.
   */
  private void startKeepaliveTask(final String lockToken) {
    ScheduledFuture<?> future = keepaliveExecutor.scheduleWithFixedDelay(
            new Runnable() {
              public void run() {
                sds.keepaliveLock(lockToken);
              }
            }, keepaliveInterval, keepaliveInterval, TimeUnit.SECONDS);

    keepaliveTasks.put(lockToken, future);
  }

  /**
   * Fetch the content of the given document from the remote doc service
   * and return a GATE Document containing that content and no
   * annotations.
   * 
   * @param docID the ID of the document in the remote service.
   */
  @SuppressWarnings("unchecked")
  public Document getDocumentContentOnly(String docID) throws DSProxyException {
    try {
      String docContent = sds.getDocContent(docID);
      FeatureMap docParams = Factory.newFeatureMap();
      docParams
              .put(Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME, docContent);
      docParams.put(Document.DOCUMENT_MARKUP_AWARE_PARAMETER_NAME,
              Boolean.FALSE);
      Document doc = (Document)Factory.createResource(
              "gate.corpora.DocumentImpl", docParams);
      setDocId(doc, docID);
      return doc;
    }
    catch(ResourceInstantiationException rie) {
      log.error("Error instantiating DocumentImpl", rie);
      throw new DSProxyException("Error creating document", rie);
    }
    catch(Exception e) {
      log.error("Remote exception accessing doc service", e);
      throw new DSProxyException(
              "Error accessing doc service. Failed method getDocumentContentOnly for Document: "
                      + docID, e);
    }
  }

  /**
   * Saves an annotation set back to the doc service. The remote
   * annotation set must have been previously obtained in read-write
   * mode via
   * {@link #getAnnotationSet(Document, String, String, boolean)}.
   * 
   * @param doc the document.
   * @param localAnnotationSetName the name of the annotation set on the
   *          document to save. May be null, in which case the default
   *          set is used.
   * @param dsAnnotationSetName the name under which the annotation set
   *          is to be saved in the doc service. May be null, in which
   *          case the set is saved as the default annotation set in the
   *          doc service.
   * @param keepLock should we keep or release the write lock after
   *          saving the annotation set?
   * @throws DSProxyException
   */
  public void saveAnnotationSet(Document doc, String localAnnotationSetName,
          String dsAnnotationSetName, boolean keepLock) throws DSProxyException {
    String docID = "";
    try {
      docID = getDocId(doc);
      if(docID == null) {
        log.error("No document ID found for document " + doc);
        throw new DSProxyException("Invalid document - document ID not found");
      }

      if(!hasLock(doc, dsAnnotationSetName)) {
        throw new DSProxyException("No lock held for annotation set "
                + dsAnnotationSetName + " by document " + docID);
      }

      AnnotationSet localAS = null;
      if(localAnnotationSetName == null || localAnnotationSetName.equals("")) {
        localAS = doc.getAnnotations();
      }
      else {
        localAS = doc.getAnnotations(localAnnotationSetName);
      }

      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      XMLStreamWriter xsw = outputFactory.createXMLStreamWriter(outputStream,
              "UTF-8");
      xsw.writeStartDocument();
      DocumentStaxUtils.writeAnnotationSet(localAS, null, xsw, "");
      xsw.close();

      String lockToken = getLockToken(doc, dsAnnotationSetName);
      sds.setAnnotationSet(outputStream.toByteArray(), lockToken, keepLock);
      if(!keepLock) {
        removeLockToken(doc, dsAnnotationSetName);
        cancelKeepalives(lockToken);
      }
    }
    catch(XMLStreamException e) {
      log.error("Exception creating annotation set XML for doc service", e);
      throw new DSProxyException("Exception creating XML for doc service", e);
    }
    catch(Exception e) {
      log.error("Exception communicating with doc service", e);
      throw new DSProxyException(
              "Error accessing doc service. Failed method saveAnnotationSet for Document: "
                      + docID, e);

    }
  }

  /**
   * Copies annotation set.
   * 
   * @param docID
   * @param sourceAnnotationSetName the name of annotation set to copy.
   *          Set this parameter to null if you want to copy default
   *          annotation set.
   * @param targetAnnotationSetName the name of annotation set to copy
   *          to. Set this parameter to null if you want to copy to
   *          default annotation set.
   */
  public void copyAnnotationSet(String docID, String sourceAnnotationSetName,
          String targetAnnotationSetName) throws DSProxyException {
    try {
      sds.copyAnnotationSet(docID, sourceAnnotationSetName,
              targetAnnotationSetName);
    }
    catch(Exception e) {
      log.error("Exception communicating with doc service", e);
      throw new DSProxyException(
              "Error accessing doc service. Failed method copyAnnotationSet for Document: "
                      + docID, e);

    }
  }

  /**
   * Lock an annotation set in the remote doc service.
   * 
   * @param doc the document to use. It must have been obtained from a
   *          previous call to {@link #getDocumentContentOnly(String)}.
   * @param dsAnnotationSetName the name of the annotation set to
   *          retrieve from the doc service. This may be
   *          <code>null</code>, in which case the default set will
   *          be retrieved.
   */
  public void lockAnnotationSet(Document doc, String dsAnnotationSetName)
          throws DSProxyException {
    String docID = "";
    try {
      docID = getDocId(doc);
      if(docID == null) {
        log.error("No document ID found for document " + doc);
        throw new DSProxyException("Invalid document - document ID not found");
      }

      String lockToken = sds.getAnnotationSetLock(docID, dsAnnotationSetName);

      setLockToken(doc, dsAnnotationSetName, lockToken);
      if(keepaliveInterval > 0) {
        startKeepaliveTask(lockToken);
      }
    }
    catch(Exception e) {
      log.error("Remote exception accessing doc service", e);
      throw new DSProxyException(
              "Error accessing doc service. Failed method lockAnnotationSet for Document: "
                      + docID, e);

    }
  }

  /**
   * List the available annotation set names for this document.
   */
  public String[] getAnnotationSetNames(Document doc) throws DSProxyException {
    String docID = getDocId(doc);
    if(docID == null) {
      log.error("No document ID found for document " + doc);
      throw new DSProxyException("Invalid document - document ID: " + docID
              + " not found");
    }

    return getAnnotationSetNames(docID);
  }

  /**
   * List the available annotation set names for the document with this
   * ID.
   */
  public String[] getAnnotationSetNames(String docID) throws DSProxyException {
    try {
      return sds.listAnnotationSets(docID);
    }
    catch(Exception e) {
      log.error("Remote exception accessing doc service", e);
      throw new DSProxyException(
              "Error accessing doc service. Failed method getAnnotationSetNames for Document: "
                      + docID, e);

    }
  }

  /**
   * Tests if annotation set with particular name exists in particular
   * document.
   * 
   * @param docID persistent ID of the document
   * @param annSetName a name of annotation set to test.
   *          <code>null</code> means default annotation set
   *          (currently always exist in any document)
   * @return <code>true</code> if annotation set with given name
   *         exists in given document, <code>false</code> otherwise.
   */
  public boolean annotationSetNameExists(String docID, String annSetName)
          throws DSProxyException {
    try {
      return sds.annotationSetNameExists(docID, annSetName);
    }
    catch(Exception e) {
      log.error("Remote exception accessing doc service", e);
      throw new DSProxyException(
              "Error accessing doc service. Failed method annotationSetNameExists for Document: "
                      + docID, e);

    }
  }

  // //// Document and Corpus manipulation methods //// //

  /**
   * Add a document to a corpus.
   */
  public boolean addDocumentToCorpus(String corpusID, String documentID)
          throws DSProxyException {
    try {
      return sds.addDocumentToCorpus(corpusID, documentID);
    }
    catch(Exception e) {
      log.error("Remote exception accessing doc service", e);
      throw new DSProxyException(
              "Error accessing doc service. Failed method addDocumentToCorpus for Document: "
                      + documentID, e);

    }
  }
  
  /**
   * Creates annotation set in doc. In case of successful operation, returns true, otherwise returns false
   */
  public boolean createAnnotationSet(String docID, String annotationSetName)
          throws DSProxyException {
    try {
      return sds.createAnnotationSet(docID, annotationSetName);
    }
    catch(Exception e) {
      e.printStackTrace();
      throw new DSProxyException(
              "Error accessing doc service. Failed method createAnnotationSet for Document: "
                      + docID + " and AS: "+annotationSetName, e);

    }
  }
  

  /**
   * Create a new empty corpus in the doc service.
   */
  public String createCorpus(String corpusName) throws DSProxyException {
    try {
      return sds.createCorpus(corpusName);
    }
    catch(Exception e) {
      log.error("Remote exception accessing doc service", e);
      throw new DSProxyException(
              "Error accessing doc service. Failed method createCorpus for Corpus: "
                      + corpusName, e);

    }
  }

  /**
   * Create a document from XML content. If a corpus ID is specified
   * (i.e. not <code>null</code>) the document is added to the given
   * corpus.
   */
  public String createDocument(String documentName, String corpusID,
          byte[] documentXml, String encoding) throws DSProxyException {
    try {
      if(corpusID == null) {
        // create document outside of any corpus
        return sds.createDoc(documentName, documentXml, encoding);
      }
      else {
        // create document and add to corpus in one go
        return sds.createDoc(documentName, corpusID, documentXml, encoding);
      }
    }
    catch(Exception e) {
      log.error("Remote exception accessing doc service", e);
      throw new DSProxyException("Error occured while creating document "
              + documentName + ". Probably document has invalid markup.", e);
    }
  }

  /**
   * Delete a corpus. The documents contained in the corpus are not
   * deleted.
   */
  public void deleteCorpus(String corpusID) throws DSProxyException {
    try {
      sds.deleteCorpus(corpusID);
    }
    catch(Exception e) {
      log.error("Remote exception accessing doc service", e);
      throw new DSProxyException(
              "Error accessing doc service. Failed method deleteCorpus for Corpus ID: "
                      + corpusID, e);

    }
  }

  /**
   * Delete a document. This also removes the document from any corpora.
   */
  public boolean deleteDocument(String documentID) throws DSProxyException {
    try {
      return sds.deleteDoc(documentID);
    }
    catch(Exception e) {
      log.error("Remote exception accessing doc service", e);
      throw new DSProxyException(
              "Error accessing doc service. Failed method deleteDocument for Document: "
                      + documentID, e);

    }
  }

  /**
   * Remove a document from a corpus without deleting it from the doc
   * service.
   */
  public boolean removeDocumentFromCorpus(String corpusID, String documentID)
          throws DSProxyException {
    try {
      return sds.removeDocumentFromCorpus(corpusID, documentID);
    }
    catch(Exception e) {
      log.error("Remote exception accessing doc service", e);
      throw new DSProxyException(
              "Error accessing doc service. Failed method removeDocumentFromCorpus for Document: "
                      + documentID, e);

    }
  }

  /**
   * List the corpora in this doc service.
   */
  public LRInfo[] listCorpora() throws DSProxyException {
    // must be final to access from inner class
    final CorpusInfo[] corpusInfoFromService;
    try {
      corpusInfoFromService = sds.listCorpora();
    }
    catch(Exception e) {
      log.error("Remote exception accessing doc service", e);
      throw new DSProxyException(
              "Error accessing doc service. Failed method listCorpora.", e);

    }

    if(corpusInfoFromService == null || corpusInfoFromService.length == 0) {
      return new LRInfo[0];
    }
    else {
      LRInfo[] lrInfo = new LRInfo[corpusInfoFromService.length];
      for(int i = 0; i < lrInfo.length; i++) {
        // copy to a final for use from inner class
        final int index = i;
        lrInfo[index] = new LRInfo() {
          public String getID() {
            return corpusInfoFromService[index].getCorpusID();
          }

          public String getName() {
            return corpusInfoFromService[index].getCorpusName();
          }
          
          public int getSize() {
            return corpusInfoFromService[index].getNumberOfDocuments();
          }

        };
      }
      return lrInfo;
    }
  }

  /**
   * List the documents in the given corpus, or all documents in the
   * service if corpusID is <code>null</code>.
   */
  public LRInfo[] listDocuments(String corpusID) throws DSProxyException {
    // must be final to access from inner class
    final DocumentInfo[] documentInfoFromService;
    try {
      if(corpusID == null) {
        documentInfoFromService = sds.listDocs();
      }
      else {
        documentInfoFromService = sds.listDocs(corpusID);
      }
    }
    catch(Exception e) {
      log.error("Remote exception accessing doc service", e);
      throw new DSProxyException(
              "Error accessing doc service. Failed method listDocuments for Corpus ID: "
                      + corpusID, e);

    }

    if(documentInfoFromService == null || documentInfoFromService.length == 0) {
      return new LRInfo[0];
    }
    else {
      LRInfo[] lrInfo = new LRInfo[documentInfoFromService.length];
      for(int i = 0; i < lrInfo.length; i++) {
        // copy to a final for use from inner class
        final int index = i;
        lrInfo[index] = new LRInfo() {
          public String getID() {
            return documentInfoFromService[index].getDocumentID();
          }

          public String getName() {
            return documentInfoFromService[index].getDocumentName();
          }
          
          public int getSize() {
            return 0;
          }

        };
      }
      return lrInfo;
    }
  }

  /**
   * Get the name of a corpus in the doc service. Returns
   * <code>null</code> if the given ID is invalid.
   */
  public String getCorpusName(String corpusID) throws DSProxyException {
    try {
      CorpusInfo ci = sds.getCorpusInfo(corpusID);
      if(ci == null) {
        return null;
      }
      else {
        return ci.getCorpusName();
      }
    }
    catch(Exception e) {
      log.error("Remote exception accessing doc service", e);
      throw new DSProxyException(
              "Error accessing doc service. Failed method getCorpusName for Corpus ID: "
                      + corpusID, e);

    }
  }

  /**
   * Set the name of the given corpus.
   */
  public void setCorpusName(String corpusID, String name)
          throws DSProxyException {
    try {
      sds.setCorpusName(corpusID, name);
    }
    catch(Exception e) {
      log.error("Remote exception accessing doc service", e);
      throw new DSProxyException(
              "Error accessing doc service. Failed method setCorpusName for Corpus ID: "
                      + corpusID, e);

    }
  }

  /**
   * Get the name of a document in the doc service. Returns
   * <code>null</code> if the given ID is invalid.
   */
  public String getDocumentName(String documentID) throws DSProxyException {
    try {
      DocumentInfo di = sds.getDocInfo(documentID);
      if(di == null) {
        return null;
      }
      else {
        return di.getDocumentName();
      }
    }
    catch(Exception e) {
      log.error("Remote exception accessing doc service", e);
      throw new DSProxyException(
              "Error accessing doc service. Failed method getDocumentName for Document ID: "
                      + documentID, e);

    }
  }

  /**
   * Set the name of the given document.
   */
  public void setDocumentName(String documentID, String name)
          throws DSProxyException {
    try {
      sds.setDocumentName(documentID, name);
    }
    catch(Exception e) {
      log.error("Remote exception accessing doc service", e);
      throw new DSProxyException(
              "Error accessing doc service. Failed method setDocumentName for Document: "
                      + documentID, e);

    }
  }
  
  public String getCorpusFeature(String corpusID, String featureName)
          throws DSProxyException {
    try {
      return sds.getCorpusFeature(corpusID, featureName);
    }
    catch(Exception e) {
      log.error("Remote exception accessing doc service", e);
      throw new DSProxyException(
              "Error accessing doc service. Failed method getCorpusFeature for Corpus: "
                      + corpusID, e);

    }
  }

  public void setCorpusFeature(String corpusID, String featureName,
          String featureValue) throws DSProxyException {
    try {
      sds.setCorpusFeature(corpusID, featureName, featureValue);
    }
    catch(Exception e) {
      log.error("Remote exception accessing doc service", e);
      throw new DSProxyException(
              "Error accessing doc service. Failed method setCorpusFeature for Corpus: "
                      + corpusID, e);
    }  
  }
  
  public String getDocumentFeature(String documentID, String featureName)
          throws DSProxyException {
    try {
      return sds.getDocumentFeature(documentID, featureName);
    }
    catch(Exception e) {
      log.error("Remote exception accessing doc service", e);
      throw new DSProxyException(
            "Error accessing doc service. Failed method getDocumentFeature for Document: "
                    + documentID, e);
      
    }
  }

  public void setDocumentFeature(String documentID, String featureName,
          String featureValue) throws DSProxyException {
    try {
      sds.setDocumentFeature(documentID, featureName, featureValue);
    }
    catch(Exception e) {
      log.error("Remote exception accessing doc service", e);
      throw new DSProxyException(
            "Error accessing doc service. Failed method setDocumentFeature for Document: "
                    + documentID, e);
    }  
  }

  // //// Search-related methods //// //

  /**
   * Annic while indexing documents, keeps a record of all possible
   * annotation types and their features. These values are stored in the
   * index and used in the ANNIC GUI. This method will return such a map
   * where key is the annotation type and the value is an array of
   * feature names.
   * 
   * @return a map of features where key is (String) feature name and
   *         value is (String) feature value
   */
  public Map<String, List<String>> getAnnotationTypesForAnnic()
          throws DSProxyException {
    try {
      MapWrapper<String, String[]> mapFromServer = this.sds
              .getAnnotationTypesForAnnic();
      if(mapFromServer == null || mapFromServer.getEntries() == null) {
        return Collections.emptyMap();
      }
      Map<String, List<String>> mapToReturn = new HashMap<String, List<String>>();
      for(MapEntry<String, String[]> entry : mapFromServer.getEntries()) {
        List<String> value = null;
        if(entry.getValue() == null) {
          value = Collections.emptyList();
        }
        else {
          value = Arrays.asList(entry.getValue());
        }
        mapToReturn.put(entry.getKey(), value);
      }
      return mapToReturn;
    }
    catch(Exception re) {
      log.error("Remote exception accessing doc service", re);
      throw new DSProxyException(
              "Error accessing doc service. Failed method getAnnotationTypesForAnnic.",
              re);
    }
  }

  /**
   * This method returns the indexed annotation set names
   * 
   * @return
   * @throws DSProxyException
   */
  public String[] getIndexedAnnotationSetNames() throws DSProxyException {
    try {
      return this.sds.getIndexedAnnotationSetNames();
    }
    catch(Exception re) {
      log.error("Remote exception accessing doc service", re);
      throw new DSProxyException(
              "Error accessing doc service. Failed method getIndexedAnnotationSetNames.",
              re);
    }
  }

  /**
   * Start an ANNIC search, returning the search ID that can be passed
   * to {@link #getNextResults} to get results.
   */
  public String startSearch(String query, String corpusID,
          String annotationSetID, int contextWindow) throws DSProxyException {
    try {
      HashMap<String, Object> searchParams = new HashMap<String, Object>();
      searchParams.put(gate.creole.annic.Constants.CONTEXT_WINDOW, Integer
              .valueOf(contextWindow));
      if(corpusID == null || corpusID.length() == 0) {
        searchParams.put(Constants.CORPUS_ID, null);
      }
      else {
        searchParams.put(Constants.CORPUS_ID, corpusID);
      }

      if(annotationSetID == null || annotationSetID.length() == 0) {
        searchParams.put(Constants.ANNOTATION_SET_ID, null);
      }
      else {
        searchParams.put(Constants.ANNOTATION_SET_ID, annotationSetID);
      }

      return sds.search(query, MapWrapper.wrap(searchParams));
    }
    catch(Exception e) {
      log.error("Remote exception accessing doc service", e);
      throw new DSProxyException(
              "Error accessing doc service. Failed method startSearch for query: "
                      + query, e);

    }
  }

  /**
   * Get the next <code>numResults</code> results from the given
   * search and return them.
   */
  public Hit[] getNextResults(String searchID, int numResults)
          throws DSProxyException {
    try {
      String searchResultsXML = new String(sds.getNextSearchResults(searchID,
              numResults), "UTF-8");
      return gate.creole.annic.Parser.fromXML(searchResultsXML);
    }
    catch(DocServiceException e) {
      log.error("Remote exception accessing doc service", e);
      throw new DSProxyException(
              "Error accessing doc service. Failed method getNextResults for searchID: "
                      + searchID, e);

    }
    catch(Exception e) {
      log.error("Exception parsing ANNIC search results", e);
      throw new DSProxyException("Error parsing ANNIC search results", e);
    }
  }

  public boolean deleteAnnotationSet(Document doc, String docServiceASName)
          throws DSProxyException {
    String lock = getLockToken(doc, docServiceASName);
    if(lock == null) {
      throw new DSProxyException("Cannot delete annotation set which "
              + "has not been locked");
    }

    boolean success = false;
    try {
      success = sds.deleteAnnotationSet(lock) && sds.releaseLock(lock);
    }
    catch(Exception e) {
      log.error("Remote exception accessing doc service", e);
      throw new DSProxyException(
              "Error accessing doc service. Failed method deleteAnnotationSet for Annotation Set Name: "
                      + docServiceASName, e);

    }

    removeLockToken(doc, docServiceASName);
    cancelKeepalives(lock);
    return success;
  }

  public boolean deleteAnnotationSet(String docID, String asName)
          throws DSProxyException {
    try {
      String lock = sds.getAnnotationSetLock(docID, asName);
      return sds.deleteAnnotationSet(lock) && sds.releaseLock(lock);
    }
    catch(Exception e) {
      log.error("Remote exception accessing doc service", e);
      throw new DSProxyException(
              "Error accessing doc service. Failed method deleteAnnotationSet for Document ID: "
                      + docID, e);

    }
  }

  public IAAResult calculateIAA(String[] docIDs, String[] asNames,
          String annotationType, String featureName, IAAAlgorithm algorithm)
          throws DSProxyException {
    try {
      gleam.docservice.IAAResult resultFromServer = sds.calculateIAA(docIDs,
              asNames, annotationType, featureName, algorithm.algorithmName());

      switch(algorithm) {
        case ALL_WAYS_F_MEASURE:
          return new AllWaysFMeasureIAAResultImpl(resultFromServer);

        case ALL_WAYS_KAPPA:
          return new AllWaysKappaIAAResultImpl(resultFromServer);

        case PAIRWISE_F_MEASURE:
          return new PairwiseFMeasureIAAResultImpl(resultFromServer);

        case PAIRWISE_KAPPA:
          return new PairwiseKappaIAAResultImpl(resultFromServer);
      }
    }
    catch(Exception e) {
      log.error("Remote exception accessing doc service", e);
      throw new DSProxyException("Error accessing doc service", e);
    }

    return null;
  }

  public boolean releaseLock(String taskID) throws DSProxyException {
    try {
      return sds.releaseLock(taskID);
    }
    catch(Exception e) {
      log.error("Remote exception accessing doc service", e);
      throw new DSProxyException(
              "Error accessing doc service. Failed method releaseLock for task ID: "
                      + taskID, e);

    }
  }

  public byte[] getDocXML(String docID) throws DSProxyException {
    try {
      return sds.getDocXML(docID);
    }
    catch(Exception e) {
      log.error("Remote exception accessing doc service", e);
      throw new DSProxyException(
              "Error accessing doc service. Failed method getDocXML for doc ID: "
                      + docID, e);
    }
  }

  public int getFreq(String corpusToSearchIn, String annotationSetToSearchIn,
          String annotationType, String featureName, String value)
          throws DSProxyException {
    try {
      return sds.freq(corpusToSearchIn, annotationSetToSearchIn,
              annotationType, featureName, value);
    }
    catch(Exception e) {
      log.error("Remote exception accessing doc service", e);
      throw new DSProxyException(
              "Error accessing doc service. Failed method getFreq", e);
    }
  }

}
