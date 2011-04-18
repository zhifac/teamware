/*
 * This file is part of GLEAM, licenced under the GNU Lesser General Public
 * Licence.
 *
 * (c) 2006 The University of Sheffield
 * 
 * $Id$
 */
package gleam.docservice.proxy;

import java.util.List;
import java.util.Map;

import gate.Document;
import gate.creole.annic.Hit;

/**
 * Interface for a Java proxy to a document service. This is intended to
 * encapsulate the operations typically required when accessing a
 * document service from Java.
 */
public interface DocServiceProxy {
  /**
   * Fetch a document from the doc service. The document returned has no
   * annotations, just the content from the doc service. The document ID
   * must be stored for use in future calls.
   * 
   * @param docID ID of the document to retrieve
   * @return the created document
   * @throws DSProxyException
   */
  public Document getDocumentContentOnly(String docID) throws DSProxyException;

  /**
   * Returns the list of all annotation set names available in the doc
   * service for the given document ID. The returned array includes a
   * <code>null</code> entry to represent the default (unnamed)
   * annotation set.
   * 
   * @param docID ID of the document
   * @return the names of annotation sets that exist on the document
   * @throws DSProxyException
   */
  public String[] getAnnotationSetNames(String docID) throws DSProxyException;

  /**
   * Returns the list of all annotation set names available in the doc
   * service for the given document. These are the names that could
   * currently be passed as the <code>dsAnnotationSetName</code>
   * parameter to {@link #getAnnotationSet} or
   * {@link #lockAnnotationSet}, though there is no guarantee that
   * another user will not remove one of these sets from the persistent
   * document in the meantime. The returned array includes a
   * <code>null</code> entry to represent the default (unnamed)
   * annotation set.
   * 
   * @param doc the document to use. It must have been obtained from a
   *          previous call to {@link #getDocumentContentOnly(String)}
   * @return the names of annotation sets that exist on the document
   * @throws DSProxyException
   */
  public String[] getAnnotationSetNames(Document doc) throws DSProxyException;

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
          throws DSProxyException;

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
          throws DSProxyException;

  /**
   * Lock an annotation set in the remote doc service. Annotation sets
   * which are to be written but do not need to be read first must be
   * locked using this method.
   * 
   * @param doc the document to use. It must have been obtained from a
   *          previous call to {@link #getDocumentContentOnly(String)}.
   * @param dsAnnotationSetName the name of the annotation set to
   *          retrieve from the doc service. This may be
   *          <code>null</code>, in which case the default set will
   *          be retrieved.
   * @throws DSProxyException
   */
  public void lockAnnotationSet(Document doc, String dsAnnotationSetName)
          throws DSProxyException;

  /**
   * Saves an annotation set back to the doc service. The remote
   * annotation set must have been previously obtained in read-write
   * mode via
   * {@link #getAnnotationSet(Document, String, String, boolean)}.
   * The write lock is released if the set is written successfully.
   * 
   * @param doc the document.
   * @param localAnnotationSetName the name of the annotation set on the
   *          document to save. May be null, in which case the default
   *          set is used.
   * @param dsAnnotationSetName the name under which the annotation set
   *          is to be saved in the doc service. May be null, in which
   *          case the set is saved as the default annotation set in the
   *          doc service.
   * @throws DSProxyException
   */
  public void saveAnnotationSet(Document doc, String localAnnotationSetName,
          String dsAnnotationSetName) throws DSProxyException;

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
   * @param keepLock whether to keep (<code>true</code>) or release
   *          (<code>false</code>) the write lock for this set after
   *          saving it.
   * @throws DSProxyException
   */
  public void saveAnnotationSet(Document doc, String localAnnotationSetName,
          String dsAnnotationSetName, boolean keepLock) throws DSProxyException;

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
          String targetAnnotationSetName) throws DSProxyException;

  /**
   * Releases all remaining write locks held for this document.
   * 
   * @param doc
   * @throws DSProxyException
   */
  public void release(Document doc) throws DSProxyException;

  // //// Methods to manipulate documents and corpora //// //

  /**
   * List all the corpora stored in this doc service.
   * 
   * @return an array of {@link LRInfo} objects representing the
   *         corpora. The array may be empty but will not be
   *         <code>null</code>.
   * @throws DSProxyException
   */
  public LRInfo[] listCorpora() throws DSProxyException;

  /**
   * Get the name of the given corpus from the doc service.
   * 
   * @param corpusID the ID of the corpus
   * @return the name of the corpus, or <code>null</code> if there is
   *         no corpus with this ID in the doc service.
   * @throws DSProxyException
   */
  public String getCorpusName(String corpusID) throws DSProxyException;
  
  /**
   * Set the name of the given corpus.
   * 
   * @param corpusID the ID of the corpus
   * @param name the new name for the corpus
   * @throws DSProxyException
   */
  public void setCorpusName(String corpusID, String name) throws DSProxyException;
  
  /**
   * Get the value of the given feature from the given corpus.
   * 
   * @param corpusID the ID of the corpus
   * @param featureName the name of the feature
   * @return the feature value, or null if the feature is not set
   * @throws DSProxyException
   */
  public String getCorpusFeature(String corpusID, String featureName)
          throws DSProxyException;
  
  /**
   * Set the value of a single feature on a corpus.
   * 
   * @param corpusID the ID of the corpus
   * @param featureName the name of the feature
   * @param featureValue the value to set
   */
  public void setCorpusFeature(String corpusID, String featureName,
          String featureValue) throws DSProxyException;

  /**
   * Create an empty corpus in the document service.
   * 
   * @param corpusName the name of the corpus to create
   * @return the ID of the new corpus
   * @throws DSProxyException
   */
  public String createCorpus(String corpusName) throws DSProxyException;

  /**
   * Delete the corpus with the given ID from the document service. The
   * documents contained in the corpus are not deleted.
   * 
   * @param corpusID the ID of the corpus to delete.
   * @throws DSProxyException
   */
  public void deleteCorpus(String corpusID) throws DSProxyException;

  /**
   * List all the documents in the given corpus, or all documents in the
   * doc service if <code>corpusID</code> is <code>null</code>.
   * 
   * @param corpusID the ID of the corpus to list. If <code>null</code>,
   *          all documents are listed, including those that are not in
   *          a corpus.
   * @return an array of {@link LRInfo} objects representing the
   *         documents. The array may be empty but will not be
   *         <code>null</code>.
   * @throws DSProxyException
   */
  public LRInfo[] listDocuments(String corpusID) throws DSProxyException;

  /**
   * Get the name of the given document from the doc service.
   * 
   * @param documentID the ID of the document
   * @return the name of the document, or <code>null</code> if there
   *         is no document with this ID in the doc service.
   * @throws DSProxyException
   */
  public String getDocumentName(String documentID) throws DSProxyException;
  
  /**
   * Set the name of the given document.
   * 
   * @param documentID the ID of the document
   * @throws DSProxyException
   */
  public void setDocumentName(String documentID, String name) throws DSProxyException;

  /**
   * Get the value of the given feature from the given document.
   * 
   * @param documentID the ID of the document
   * @param featureName the name of the feature
   * @return the feature value, or null if the feature is not set
   * @throws DSProxyException
   */
  public String getDocumentFeature(String documentID, String featureName)
          throws DSProxyException;
  
  /**
   * Set the value of a single feature on a document.
   * 
   * @param documentID the ID of the document
   * @param featureName the name of the feature
   * @param featureValue the value to set
   */
  public void setDocumentFeature(String documentID, String featureName,
          String featureValue) throws DSProxyException;

  /**
   * Create a new document in the doc service from the supplied XML
   * content. The content will typically be GATE-format XML, as
   * generated by {@link Document#toXml()} with no arguments.
   * 
   * @param documentName the name for the new document
   * @param corpusID the ID of the corpus in which the new document
   *          should be put. If <code>null</code>, the new document
   *          is not placed in a corpus.
   * @param documentXml the XML content to use when creating the
   *          document.
   * @param encoding TODO
   * @return the ID of the new document in the doc service
   * @throws DSProxyException
   */
  public String createDocument(String documentName, String corpusID,
          byte[] documentXml, String encoding) throws DSProxyException;
  
  
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
  public boolean createAnnotationSet(String docID, String annotationSetName)
      throws DSProxyException;

  /**
   * Add the given document to the given corpus.
   * 
   * @param corpusID the ID of the corpus
   * @param documentID the ID of the document
   * @return <code>true</code> if the removal was successful,
   *         <code>false</code> otherwise. Reasons for failure include
   *         an unrecognised document or corpus ID.
   * @throws DSProxyException
   */
  public boolean addDocumentToCorpus(String corpusID, String documentID)
          throws DSProxyException;

  /**
   * Remove the given document from the given corpus. The document is
   * not deleted from the doc service - this must be done separately by
   * calling {@link #deleteDocument}.
   * 
   * @param corpusID the ID of the corpus
   * @param documentID the ID of the document
   * @return <code>true</code> if the removal was successful,
   *         <code>false</code> otherwise. Reasons for failure include
   *         an unrecognised document or corpus ID, or if this document
   *         was not contained in this corpus.
   * @throws DSProxyException
   */
  public boolean removeDocumentFromCorpus(String corpusID, String documentID)
          throws DSProxyException;

  /**
   * Delete the document with the given ID from the doc service. This
   * also removes the document from any corpora which contain it.
   * 
   * @param documentID the ID of the document to delete.
   * @return <code>true</code> if the removal was successful,
   *         <code>false</code> otherwise.
   * @throws DSProxyException
   */
  public boolean deleteDocument(String documentID) throws DSProxyException;
  
  /**
   * Get the document with the given ID from the doc service. This
   * returns the document in the format of Gate XML
   * 
   * @param documentID the ID of the document to delete.
   * @return byte[]
   * @throws DSProxyException
   */
  public byte[] getDocXML(String docID) throws DSProxyException;
  
  // //// Search-related methods //// //

  /**
   * Initiates an ANNIC search of this doc service, returning the search
   * ID which can be passed to {@link #getNextResults} to obtain the
   * search results.
   * 
   * @param query the ANNIC query string to search for.
   * @param corpusID the corpus in which to search.
   * @param annotationSetId the annotationSet in which to search
   * @param contextWindow the window size to use.
   * @return the search ID to be used to obtain results.
   */
  public String startSearch(String query, String corpusID, String annotationSetID, int contextWindow)
          throws DSProxyException;

  /**
   * Annic while indexing documents, keeps a record of all possible annotation
   * types and their features. These values are stored in the index and used
   * in the ANNIC GUI. This method will return such a map where key is the
   * annotation type and the value is an array of feature names.
   * 
   * @return a map of features where key is (String) feature name and value is
   *         (String) feature value
   */
  public Map<String, List<String>> getAnnotationTypesForAnnic() throws DSProxyException;
  
  /**
   * This method returns the indexed annotation set names
   * @return
   * @throws DSProxyException
   */
  public String[] getIndexedAnnotationSetNames() throws DSProxyException;
  
  /**
   * Returns the next <code>numResults</code> results from the given
   * ANNIC search (or fewer if fewer are available).
   * 
   * @param searchID the ID of the search.
   * @param numResults maximum number of results to return.
   * @return an array of ANNIC results.
   */
  public Hit[] getNextResults(String searchID, int numResults)
          throws DSProxyException;

  /**
   * Delete an annotation set from the doc service representation of
   * this document.
   * 
   * @param doc the document to use. It must have been obtained from a
   *          previous call to {@link #getDocumentContentOnly(String)}
   *          and must have a lock token for
   *          <code>docServiceASName</code>.
   * @param docServiceASName the annotation set to delete.
   */
  public boolean deleteAnnotationSet(Document doc, String docServiceASName)
          throws DSProxyException;

  /**
   * Delete an annotation set from the given document in the doc
   * service.
   * 
   * @param docID the document ID
   * @param asName the name of the annotation set to delete
   * @throws DSProxyException if the given set cannot be locked, or if
   *           the operation fails for any other reason.
   */
  public boolean deleteAnnotationSet(String docID, String asName)
          throws DSProxyException;

  /**
   * Calculate inter-annotator agreement over the given set of documents
   * and annotation set names.
   * 
   * @param docIDs the IDs of the documents to use. Each document should
   *          contain all of the annotation sets named in
   *          <code>asNames</code>.
   * @param asNames the names of the annotation sets to use.
   *          <code>null</code> denotes the default annotation set.
   * @param annotationType the annotation type over which to calculate
   *          agreement.
   * @param featureName the feature name to use when calculating
   *          agreement. If present (i.e. not <code>null</code>) we
   *          collect all values that this feature takes in the
   *          specified annotation sets and calculate the agreement over
   *          each value in turn, then average the results. This is
   *          suitable when the annotations represent, e.g., mentions of
   *          concepts in an ontology. If <code>null</code>, we
   *          simply calculate the score based on the annotation spans
   *          only, which is suitable when the annotations are simple
   *          Person, Disease, etc.
   * @param algorithm the algorithm to use.
   * @throws DSProxyException if an error occurs during the computation.
   */
  public IAAResult calculateIAA(String[] docIDs, String[] asNames,
          String annotationType, String featureName, IAAAlgorithm algorithm)
          throws DSProxyException;
  /**
   * Release a locked annotation set from a document
   * @param taskID
   * @return
   * @throws DSProxyException
   */
  public boolean releaseLock(String taskID) throws DSProxyException;

  /**
   * @see gate.creole.annic.lucene.StatsCalculator#freq(String, String, String, String, String)
   */
  public int getFreq(String corpusToSearchIn,
          String annotationSetToSearchIn, String annotationType,
          String featureName, String value)
          throws DSProxyException;

}
