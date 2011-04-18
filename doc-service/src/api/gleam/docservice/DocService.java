/*
 *  DocService.java
 *
 *  Copyright (c) 2006-2006, The University of Sheffield.
 *
 *  Andrey Shafirin, 25/Apr/2006
 *
 *  $Id$
 */

package gleam.docservice;

import gleam.util.adapters.MapWrapper;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.ws.ResponseWrapper;

/**
 * GLEAM document service interface.
 */
@WebService(targetNamespace = "http://gate.ac.uk/ns/safe/1.1/doc-service")
public interface DocService {
  /**
   * Lists corpuses in the doc-service.
   * 
   * @return array of persistent IDs for for the corpuses that are stored in
   *         the underlying serial datastore or null in case of failure
   */
  @WebMethod
  @WebResult(targetNamespace = "http://gate.ac.uk/ns/safe/1.1/doc-service")
  @ResponseWrapper(className = "gleam.docservice.CorpusInfoArrayResponse")
  public CorpusInfo[] listCorpora();

  /**
   * @param corpusID
   *            persistent ID of the document
   * @return some information about a corpus with given persistent ID
   */
  @WebMethod
  @WebResult(targetNamespace = "http://gate.ac.uk/ns/safe/1.1/doc-service")
  public CorpusInfo getCorpusInfo(@WebParam(name = "corpusID") String corpusID);

  /**
   * Creates document corpus in the underlying serial datastore.
   * 
   * @param corpusName
   *            the name for the new corpus
   * @return persistent ID for the new corpus or null in case of failure
   */
  @WebMethod
  @WebResult(targetNamespace = "http://gate.ac.uk/ns/safe/1.1/doc-service")
  public String createCorpus(@WebParam(name = "corpusName") String corpusName);

  /**
   * Lists documents in the doc-service.
   * 
   * @return array of persistent IDs for for the documents that are stored in
   *         the underlying serial datastore or null in case of failure
   */
  @WebMethod
  @WebResult(targetNamespace = "http://gate.ac.uk/ns/safe/1.1/doc-service")
  @ResponseWrapper(className = "gleam.docservice.DocumentInfoArrayResponse")
  public DocumentInfo[] listDocs();

  /**
   * Lists documents contained in the particular corpus .
   * 
   * @param corpusID
   *            corpus persistent ID
   * @return array of persistent IDs for for the documents that are stored in
   *         the underlying serial datastore and belong to particular corpus
   *         or null in case of failure
   */
  @WebMethod(operationName = "listDocsInCorpus")
  @WebResult(targetNamespace = "http://gate.ac.uk/ns/safe/1.1/doc-service")
  @ResponseWrapper(className = "gleam.docservice.DocumentInfoArrayResponse")
  public DocumentInfo[] listDocs(@WebParam(name = "corpusID") String corpusID);

  /**
   * @param corpusID
   *            persistent ID of the corpus
   * @return a map of features where key is (String) feature name and value is
   *         (String) feature value
   */
  @WebMethod
  @WebResult(targetNamespace = "http://gate.ac.uk/ns/safe/1.1/doc-service")
  public MapWrapper<String, String> getCorpusFeatures(
          @WebParam(name = "corpusID") String corpusID) throws DocServiceException;

  /**
   * @param documentID
   *            persistent ID of the corpus
   * @param a
   *            map of features where key is (String) feature name and value
   *            is (String) feature value
   */
  @WebMethod
  @WebResult(targetNamespace = "http://gate.ac.uk/ns/safe/1.1/doc-service")
  public void setCorpusFeatures(@WebParam(name = "corpusID") String corpusID,
          @WebParam(name = "features") MapWrapper<String, String> features)
      throws DocServiceException;
  
  /**
   * Get the current value of a single feature of the given corpus.
   * 
   * @param corpusID the ID of the corpus
   * @param featureName the name of the feature
   * @return the value of the feature, or null if the feature is not set
   */
  @WebMethod
  @WebResult(targetNamespace = "http://gate.ac.uk/ns/safe/1.1/doc-service")
  public String getCorpusFeature(@WebParam(name = "corpusID") String corpusID,
          @WebParam(name = "featureName") String featureName) throws DocServiceException;
  
  /**
   * Set the value of a single feature on a corpus.
   * 
   * @param corpusID the ID of the corpus
   * @param featureName the name of the feature
   * @param featureValue the value to set
   */
  @WebMethod
  @WebResult(targetNamespace = "http://gate.ac.uk/ns/safe/1.1/doc-service")
  public void setCorpusFeature(@WebParam(name = "corpusID") String corpusID,
          @WebParam(name = "featureName") String featureName,
          @WebParam(name = "featureValue") String featureValue) throws DocServiceException;
  
  /**
   * Changes the name of the given corpus.
   * 
   * @param corpusID the ID of the corpus whose name is to be changed.
   * @param name the new name.
   * @throws DocServiceException if an error occurs setting the name.
   */
  @WebMethod
  @WebResult(targetNamespace = "http://gate.ac.uk/ns/safe/1.1/doc-service")
  public void setCorpusName(@WebParam(name = "corpusID") String corpusID,
          @WebParam(name = "name") String name) throws DocServiceException;

  /**
   * @param documentID
   *            persistent ID of the document
   * @return a map of features where key is (String) feature name and value is
   *         (String) feature value
   */
  @WebMethod
  @WebResult(targetNamespace = "http://gate.ac.uk/ns/safe/1.1/doc-service")
  public MapWrapper<String, String> getDocumentFeatures(
          @WebParam(name="documentID") String documentID)
      throws DocServiceException;

  /**
   * @param documentID
   *            persistent ID of the document
   * @param a
   *            map of features where key is (String) feature name and value
   *            is (String) feature value
   */
  @WebMethod
  @WebResult(targetNamespace = "http://gate.ac.uk/ns/safe/1.1/doc-service")
  public void setDocumentFeatures(@WebParam(name = "documentID") String documentID,
          @WebParam(name = "features") MapWrapper<String, String> features)
      throws DocServiceException;
  
  /**
   * Get the current value of a single feature of the given document.
   * 
   * @param documentID the ID of the document
   * @param featureName the name of the feature
   * @return the value of the feature, or null if the feature is not set
   */
  @WebMethod
  @WebResult(targetNamespace = "http://gate.ac.uk/ns/safe/1.1/doc-service")
  public String getDocumentFeature(@WebParam(name = "documentID") String documentID,
          @WebParam(name = "featureName") String featureName) throws DocServiceException;
  
  /**
   * Set the value of a single feature on a document.
   * 
   * @param documentID the ID of the document
   * @param featureName the name of the feature
   * @param featureValue the value to set
   */
  @WebMethod
  @WebResult(targetNamespace = "http://gate.ac.uk/ns/safe/1.1/doc-service")
  public void setDocumentFeature(@WebParam(name = "documentID") String documentID,
          @WebParam(name = "featureName") String featureName,
          @WebParam(name = "featureValue") String featureValue) throws DocServiceException;
  
  /**
   * Changes the name of the given document.
   * 
   * @param documentID the ID of the document whose name is to be changed.
   * @param name the new name.
   * @throws DocServiceException if an error occurs setting the name.
   */
  @WebMethod
  @WebResult(targetNamespace = "http://gate.ac.uk/ns/safe/1.1/doc-service")
  public void setDocumentName(@WebParam(name = "documentID") String documentID,
          @WebParam(name = "name") String name) throws DocServiceException;

  /**
   * Deletes a corpus with a given persistent ID.
   * 
   * @param corpusID
   *            corpus persistent ID
   */
  @WebMethod
  @WebResult(targetNamespace = "http://gate.ac.uk/ns/safe/1.1/doc-service")
  public void deleteCorpus(@WebParam(name = "corpusID") String corpusID);

  /**
   * Creates a document from its complete GATE XML representation.
   * 
   * @param docName
   *            a document name
   * @param docXmlContent
   *            document content in Gate XML format. The same as for 'Save As
   *            Xml...' menu item for document in GATE GUI
   * @param encoding
   *            the encoding in which the XML is saved.
   * @return document persistence ID or <b>null</b> if document aren't
   *         created
   */
  @WebMethod
  @WebResult(targetNamespace = "http://gate.ac.uk/ns/safe/1.1/doc-service")
  public String createDoc(@WebParam(name = "docName") String docName,
          @WebParam(name = "docXmlContent") byte[] docXmlContent,
          @WebParam(name = "encoding") String encoding);

  /**
   * Creates a document from its complete GATE XML representation and puts it
   * into given corpus.<br>
   * This is shortcut method.
   * 
   * @param docName
   *            a document name
   * @param corpusID
   *            corpus for the new document
   * @param docXmlContent -
   *            document content in Gate XML format. The same as for 'Save As
   *            Xml...' menu item for document in GATE GUI
   * @param encoding
   *            the encoding in which the XML is saved.
   * @return document persistence ID or <b>null</b> if document aren't
   *         created
   */
  @WebMethod(operationName = "createDocInCorpus")
  @WebResult(targetNamespace = "http://gate.ac.uk/ns/safe/1.1/doc-service")
  public String createDoc(@WebParam(name = "docName") String docName,
          @WebParam(name = "corpusID") String corpusID,
          @WebParam(name = "docXmlContent") byte[] docXmlContent,
          @WebParam(name = "encoding") String encoding) throws DocServiceException;

  /**
   * Deletes a document.
   * 
   * @param docID
   *            persistent ID of the document
   * @return true if operationwas succeed, false otherwise
   */
  @WebMethod
  @WebResult(targetNamespace = "http://gate.ac.uk/ns/safe/1.1/doc-service")
  public boolean deleteDoc(@WebParam(name = "docID") String docID) throws DocServiceException;

  /**
   * Returns document info.
   * 
   * @param docID
   *            persistent ID of the document
   * @return information about a document with given ID or null if document
   *         with given ID doesn't exists or if exception occured
   */  
  @WebMethod
  @WebResult(targetNamespace = "http://gate.ac.uk/ns/safe/1.1/doc-service")
  public DocumentInfo getDocInfo(@WebParam(name = "docID") String docID);

  /**
   * Returns annotation sets names of given document.
   * 
   * @param docID
   *            persistent ID of the document
   * @return annotation sets names. Includes <code>null</code> value which
   *         corresponds to default annotation set.
   */
  @WebMethod
  @WebResult(targetNamespace = "http://gate.ac.uk/ns/safe/1.1/doc-service")
  @ResponseWrapper(className = "gleam.docservice.StringArrayResponse")
  public String[] listAnnotationSets(@WebParam(name = "docID") String docID) throws DocServiceException;

  /**
   * Tests if annotation set with particular name exists in particular
   * document.
   * 
   * @param docID
   *            persistent ID of the document
   * @param annSetName
   *            a name of annotation set to test. <code>null</code> means
   *            default annotation set (currently always exist in any
   *            document)
   * @return <code>true</code> if annotation set with given name exists in
   *         given document, <code>false</code> otherwise.
   */
  @WebMethod
  @WebResult(targetNamespace = "http://gate.ac.uk/ns/safe/1.1/doc-service")
  public boolean annotationSetNameExists(@WebParam(name = "docID") String docID,
          @WebParam(name = "annSetName") String annSetName)
      throws DocServiceException;

  /**
   * Returns document text.
   * 
   * @param docID
   *            persistent ID of the document
   * @return document text
   */
  @WebMethod
  @WebResult(targetNamespace = "http://gate.ac.uk/ns/safe/1.1/doc-service")
  public String getDocContent(@WebParam(name = "docID") String docID);

  /**
   * Returns document text.
   * 
   * @param docID
   *            persistent ID of the document
   * @return document representation in GATE's XML format.  This is
   *            always encoded in UTF-8, and may be GZipped.
   */
  @WebMethod
  @WebResult(targetNamespace = "http://gate.ac.uk/ns/safe/1.1/doc-service")
  public byte[] getDocXML(@WebParam(name = "docID") String docID);

  /**
   * Adds document to the corpus.
   * 
   * @param corpusID
   *            corpus persistent ID
   * @param documentID
   *            document persistent ID
   * @return true if given document has been successfully removed, false
   *         othervide
   */
  @WebMethod
  @WebResult(targetNamespace = "http://gate.ac.uk/ns/safe/1.1/doc-service")
  public boolean addDocumentToCorpus(@WebParam(name = "corpusID") String corpusID,
          @WebParam(name = "documentID") String documentID)
      throws DocServiceException;

  /**
   * Removes document from the corpus.
   * 
   * @param corpusID
   *            corpus persistent ID
   * @param documentID
   *            document persistent ID
   * @return true if given document has been successfully removed, false
   *         othervise
   */
  @WebMethod
  @WebResult(targetNamespace = "http://gate.ac.uk/ns/safe/1.1/doc-service")
  public boolean removeDocumentFromCorpus(@WebParam(name = "corpusID") String corpusID,
          @WebParam(name = "documentID") String documentID)
      throws DocServiceException;

  /**
   * This method gets annotation set. New annotation set will be created, if
   * annotation set with given name are not present in the document. To avoid
   * spontaneous creation of new annotation sets, An annotation set will be
   * created only if annotation set requested in RW mode.
   * 
   * @param docID
   * @param annotationSetName
   *            the name of annotation set to get. Set this parameter to null
   *            if you want to get default annotation set.
   * @param readOnly
   *            <br>
   *            'true' - means you can't update this annotation set, multiple
   *            users can take the same annotation set<br>
   *            'false' - allows you update annotation set using 'taskID'
   * @return 2 values as array of String.<br>
   *         1-st is annotation set in XML format produced by
   *         {@link gate.Document#toXml(Set)}<br>
   *         2-nd is taskID, will be required for updating annotation set. Can
   *         be null. In this case annotation set can't be updated.
   *         {@link #setAnnotationSet(String, String, boolean)}
   */
  @WebMethod
  @WebResult(targetNamespace = "http://gate.ac.uk/ns/safe/1.1/doc-service")
  public AnnotationSetHandle getAnnotationSet(@WebParam(name = "docID") String docID,
      @WebParam(name = "annotationSetName") String annotationSetName,
      @WebParam(name = "readOnly") boolean readOnly)
      throws DocServiceException;

  /**
   * This method gets annotation set lock without sending annotation set to
   * the client. May be useful for updating annotation set if you don't care
   * about previous content
   * 
   * @param docID
   * @param annotationSetName
   *            the name of annotation set to get. Set this parameter to null
   *            if you want to get default annotation set.
   * @return annotation set taskID
   */
  @WebMethod
  @WebResult(targetNamespace = "http://gate.ac.uk/ns/safe/1.1/doc-service")
  public String getAnnotationSetLock(@WebParam(name = "docID") String docID,
          @WebParam(name = "annotationSetName") String annotationSetName)
      throws DocServiceException;

  /**
   * Updates an annotation set associated with a given task ID with new data
   * represented as GATE XML format.<br>
   * Automaticaly release the lock and makes taskID useless.
   * 
   * @param xmlContent
   *            annotation set in GATE XML format
   * @param taskID
   *            previously obtained taskID associated with a locked annotation
   *            set
   * @param keepLock
   *            tells the docservice whether to keep annotation set lock.
   *            <b>true</b> - keep lock <b>false</b> - release lock
   * @return true if given annotation set has been successfully modified,
   *         false othervise
   * 
   * TODO: Add ability to rollback in case of an error. Now we can damage
   * annotation set.
   */
  @WebMethod
  @WebResult(targetNamespace = "http://gate.ac.uk/ns/safe/1.1/doc-service")
  public boolean setAnnotationSet(@WebParam(name = "xmlContent") byte[] xmlContent,
          @WebParam(name = "taskID") String taskID,
          @WebParam(name = "keepLock") boolean keepLock) throws DocServiceException;

  /**
   * Deletes annotation set.
   * 
   * @param taskID
   *            previously obtained taskID associated with a locked annotation
   *            set
   * @return true if given annotation set has been successfully deleted, false
   *         othervise
   */
  @WebMethod
  @WebResult(targetNamespace = "http://gate.ac.uk/ns/safe/1.1/doc-service")
  public boolean deleteAnnotationSet(@WebParam(name = "taskID") String taskID)
      throws DocServiceException;
  
  /**
   * Creates an empty annotation set, so long as a set with this name does not
   * already exist on the document.
   * 
   * @param docID the ID of the document
   * @param annotationSetName the name of the annotation set to create.
   *           Must not be <code>null</code>.
   * @return true if the given annotation set has been successfully created,
   *         false otherwise (i.e. the set already existed, was already
   *         locked for writing by another user, or does not exist but there
   *         was a problem creating it).
   */
  @WebMethod
  @WebResult(targetNamespace = "http://gate.ac.uk/ns/safe/1.1/doc-service")
  public boolean createAnnotationSet(@WebParam(name = "docID") String docID,
          @WebParam(name = "annotationSetName") String annotationSetName)
      throws DocServiceException;

  /**
   * Copies annotation set.
   * 
   * @param docID
   * @param sourceAnnotationSetName
   *            the name of annotation set to copy. Set this parameter to null
   *            if you want to copy default annotation set.
   * @param targetAnnotationSetName
   *            the name of annotation set to copy to. Set this parameter to
   *            null if you want to copy to default annotation set.
   */
  @WebMethod
  @WebResult(targetNamespace = "http://gate.ac.uk/ns/safe/1.1/doc-service")
  public void copyAnnotationSet(@WebParam(name = "docID") String docID,
          @WebParam(name = "sourceAnnotationSetName") String sourceAnnotationSetName,
          @WebParam(name = "targetAnnotationSetName") String targetAnnotationSetName)
        throws DocServiceException;

  /**
   * Performs a search in stored documens.
   * 
   * @param query
   *            a string in annic format. Read annic documentation for
   *            details.
   * @param parameters
   *            search parameters for annic searcher. Read annic documentation
   *            for details.
   * @return ID of searcher to retrieve results with
   *         {@link #getNextSearchResults(String, int)} or <code>null</code>
   *         if searcher can't be created.
   */
  @WebMethod
  @WebResult(targetNamespace = "http://gate.ac.uk/ns/safe/1.1/doc-service")
  public String search(@WebParam(name = "query") String query,
          @WebParam(name = "parameters") MapWrapper<String, Object> parameters)
      throws DocServiceException;

  /**
   * Retrieves a results of search for a given searcher.
   * 
   * @param searcherId
   *            ID of searcher
   * @param numberOfRecords
   *            a number of records to retrieve. <code>-1</code> means all
   *            records. <code>0</code> can be used to refresh searcher and
   *            avoid cleaning it up.
   * @return xml encoded results of search, encoded in UTF-8.
   */
  @WebMethod
  @WebResult(targetNamespace = "http://gate.ac.uk/ns/safe/1.1/doc-service")
  public byte[] getNextSearchResults(@WebParam(name = "searcherId") String searcherId,
          @WebParam(name = "numberOfRecords") int numberOfRecords)
      throws DocServiceException;

  /**
   * Release the resource locked. Makes taskID unusable. Resource associated
   * with this taskID can't be changed.
   * 
   * @param taskID
   *            task ID (lock ID) to release
   * @return true if lock was found and released, false otherwise
   */
  @WebMethod
  @WebResult(targetNamespace = "http://gate.ac.uk/ns/safe/1.1/doc-service")
  public boolean releaseLock(@WebParam(name = "taskID") String taskID);

  /**
   * Prevents given task ID (lock ID) from automatical release by the
   * {@link LockManager} as a dead ID.<br>
   * Client DocService need to call this method with interval less than
   * {@link LockManager.DeadLocksCleaner#timeout} to keep the resource
   * updatable.
   * 
   * @param taskID
   *            task ID (lock ID) to refresh
   * @return true if lock was found and refreshed, false otherwise
   */
  @WebMethod
  @WebResult(targetNamespace = "http://gate.ac.uk/ns/safe/1.1/doc-service")
  public boolean keepaliveLock(@WebParam(name = "taskID") String taskID);

  /**
   * Calculate inter-annotator agreement over a set of annotations.
   * 
   * @param docIDs
   *            the IDs of the documents to use. Each document should contain
   *            all of the annotation sets named in <code>asNames</code>.
   * @param asNames
   *            the names of the annotation sets to use. <code>null</code>
   *            denotes the default annotation set.
   * @param annotationType
   *            the annotation type over which to calculate agreement.
   * @param featureName
   *            the feature name to use when calculating agreement. If present
   *            (i.e. not <code>null</code>) we collect all values that
   *            this feature takes in the specified annotation sets and
   *            calculate the agreement over each value in turn, then average
   *            the results. This is suitable when the annotations represent,
   *            e.g., mentions of concepts in an ontology. If
   *            <code>null</code>, we simply calculate the score based on
   *            the annotation spans only, which is suitable when the
   *            annotations are simple Person, Disease, etc.
   * @param algorithm
   *            the algorithm to use. Supported algorithms include
   *            "pairwise-f-measure", "all-ways-f-measure", "pairwise-kappa",
   *            "all-ways-kappa". Certain algorithms treat the other
   *            parameters in special ways, for example all-ways-f-measure
   *            treats the first annotation set name as a gold-standard and
   *            scores the other sets against this one.
   * @return the result of the IAA calculation. The detail field of the result
   *         object contains the full details of the calculation result, and
   *         its type and contents depend on the algorithm chosen.
   */
  @WebMethod
  @WebResult(targetNamespace = "http://gate.ac.uk/ns/safe/1.1/doc-service")
  public IAAResult calculateIAA(@WebParam(name = "docIDs") String[] docIDs,
          @WebParam(name = "asNames") String[] asNames,
          @WebParam(name = "annotationType") String annotationType,
          @WebParam(name = "featureName") String featureName,
          @WebParam(name = "algorithm") String algorithm)
      throws DocServiceException;

  /**
   * Annic while indexing documents, keeps a record of all possible annotation
   * types and their features. These values are stored in the index and used
   * in the ANNIC GUI. This method will return such a map where key is the
   * annotation type and the value is a list of strings.
   * 
   * @return a map of features where key is (String) feature name and value is
   *         (String) feature value
   */
  @WebMethod
  @WebResult(targetNamespace = "http://gate.ac.uk/ns/safe/1.1/doc-service")
  public MapWrapper<String, String[]> getAnnotationTypesForAnnic() throws DocServiceException;
  
  /**
   * This method returns the indexed annotation set names
   * 
   * @return
   * @throws DSProxyException
   */
  @WebMethod
  @WebResult(targetNamespace = "http://gate.ac.uk/ns/safe/1.1/doc-service")
  @ResponseWrapper(className = "gleam.docservice.StringArrayResponse")
  public String[] getIndexedAnnotationSetNames() throws DocServiceException;

  /**
   * @see gate.creole.annic.lucene.StatsCalculator#freq(String, String, String, String, String)
   */
  @WebMethod
  @WebResult(targetNamespace = "http://gate.ac.uk/ns/safe/1.1/doc-service")
  public int freq(@WebParam(name = "corpusToSearchIn") String corpusToSearchIn,
                  @WebParam(name = "annotationSetToSearchIn") String annotationSetToSearchIn,
                  @WebParam(name = "annotationType") String annotationType,
                  @WebParam(name = "featureName") String featureName,
                  @WebParam(name = "value") String value);

}
