/*
 *  DocServiceManager.java
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
package gleam.executive.service;

import gleam.docservice.proxy.DocServiceProxy;
import gleam.docservice.proxy.IAAAlgorithm;
import gleam.docservice.proxy.IAAResult;
import gleam.executive.model.AnnicSearchResult;
import gleam.executive.model.AnnotationDifferResult;
import gleam.executive.model.AnnotationDifferScores;
import gleam.executive.model.Corpus;
import gleam.executive.model.Document;

import java.util.List;


public interface DocServiceManager {
  /**
   * The method to retrieve Gate Document from docServiceProxy
   * @param dsURL
   * @return gate.Document
   * @throws SafeManagerException
   */
  public gate.Document getGateDocument(String dsURL)
          throws SafeManagerException;

  /**
   * The method to get the Annic Searcher ID
   * @param query
   * @param corpusID
   * @param annotationSetID
   * @param contextWindow
   * @return searchID in the format of type String
   * @throws SafeManagerException
   */
  public String getSearcherID(String query, String corpusID, String annotationSetID,
          int contextWindow) throws SafeManagerException;

  /**
   * The method to retrieve next specified number of Annic Search result in the format of String
   * @param searchID
   * @param numberOfResults
   * @return List a list of populated AnnicSearchResult
   * @throws SafeManagerException
   */
  public List<AnnicSearchResult> getAnnicSearchResults(String searchID, int numberOfResults)
          throws SafeManagerException;

  /**
   * The method to list all the corpora on a given docService
   * @return List, which contains a set of CorpusInfo
   * @throws SafeManagerException
   */
  public List<Corpus> listCorpora() throws SafeManagerException;

  /**
   * Retrieve a CorpusInfo by corpus id
   * @param corpusID
   * @return CorpusInfo
   * @throws SafeManagerException
   */
  public String getCorpusName(String corpusID) throws SafeManagerException;

  /**
   * Set the name of a corpus
   * @param corpusID
   * @param name
   * @throws SafeManagerException
   */
  public void setCorpusName(String corpusID, String name) throws SafeManagerException;

  /**
   * Get the value of the given feature from the given corpus.
   *
   * @param corpusID the ID of the corpus
   * @param featureName the name of the feature
   * @return the feature value, or null if the feature is not set
   */
  public String getCorpusFeature(String corpusID, String featureName)
          throws SafeManagerException;

  /**
   * Set the value of a single feature on a corpus.
   *
   * @param corpusID the ID of the corpus
   * @param featureName the name of the feature
   * @param featureValue the value to set
   */
  public void setCorpusFeature(String corpusID, String featureName,
          String featureValue) throws SafeManagerException;

  /**
   * The method to list all the documents on a given docService
   * @return List, which contains a list of gleam.executive.model.Document
   * @throws SafeManagerException
   */
  public List<Document> listDocuments() throws SafeManagerException;

  /**
   * Retrieve a list of documents for a specified corpus
   * @param corpusID
   * @return List, which contains a list of gleam.executive.model.Document
   * @throws SafeManagerException
   */
  public List<Document> listDocuments(String corpusID)
          throws SafeManagerException;

  /**
   * Retrieve a gleam.executive.model.Document by its id
   * @param documentID
   * @return gleam.executive.model.Document
   * @throws SafeManagerException
   */
  public Document getDocument(String documentID) throws SafeManagerException;

  /**
   * Get the value of the given feature from the given document.
   *
   * @param documentID the ID of the document
   * @param featureName the name of the feature
   * @return the feature value, or null if the feature is not set
   */
  public String getDocumentFeature(String documentID, String featureName)
          throws SafeManagerException;

  /**
   * Set the value of a single feature on a document.
   *
   * @param documentID the ID of the document
   * @param featureName the name of the feature
   * @param featureValue the value to set
   */
  public void setDocumentFeature(String documentID, String featureName,
          String featureValue) throws SafeManagerException;

  /**
   * Create a GATE corpus in the underlying Lucene Searchable DataStore.
   * @param corpusName
   * @return persistent ID for the new corpus
   * @throws SafeManagerException
   */
  public String createCorpus(String corpusName) throws SafeManagerException;

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
   *
   */
  public boolean createAnnotationSet(String docID, String annotationSetName) throws SafeManagerException;

  /**
   * Delete a corpus from the DataStore by specifying its ID.
   * @param corpusID
   * @throws SafeManagerException
   */
  public void deleteCorpus(String corpusID) throws SafeManagerException;

  /**
   * Delete a document from the DataStore permanentlly by given its ID.
   * @param docID
   * @return true if successfully deleted or false if not.
   * @throws SafeManagerException
   */
  public boolean deleteDocument(String docID) throws SafeManagerException;

  /**
   * Add a document to the specified corpus
   * @param corpusID
   * @param documentID
   * @return true if successully added or false in case not.
   * @throws SafeManagerException
   */
  public boolean addDocumentToCorpus(String corpusID, String documentID)
          throws SafeManagerException;

  /**
   * Remove a document from the corpus where it exists.
   * @param corpusID
   * @param documentID
   * @return true if removed successfully.
   * @throws SafeManagerException
   */
  public boolean removeDocumentFromCorpus(String corpusID, String documentID)
          throws SafeManagerException;

  /**
   * Create a document from its complete GATE XML representation and puts it into given corpus.
   * @param docName
   * @param corpusID
   * @param docXmlContent
   * @return document persistence ID.
   * @throws SafeManagerException
   */
  public String createDocIntoCorpus(String docName, String corpusID,
          byte[] docXmlContent, String encoding) throws SafeManagerException;

  /**
   * @param docServiceProxy
   */
  public void setDocServiceProxy(DocServiceProxy docServiceProxy);

  /**
   * @return String docService URL
   */
  public String getDocServiceURL();

  /**
   * @return the "private" doc service URL that can be used by services
   * deployed on the same server as this application.
   */
  public String getPrivateDocServiceURL();
  
  /**
   * @return String annotatorGUI URL
   */
  public String getAnnotatorGUIURL();
  
  /**
   * @return the URL at which the pooled-mode annotator gui is found.
   */
  public String getPoolModeAnnotatorGUIURL();

  /**
   * @return String annotation diff GUI URL
   */
  public String getAnnotationDiffGUIURL();

  /**
   * Retrieve a list of annotation set names that exist in the given document.
   * @param docID
   * @return List
   * @throws SafeManagerException
   */
  public List listAnnotationSetNames(String docID) throws SafeManagerException;

  /**
   * Retrieve a list of AnnotationDifferResult that are used to populate the table.
   * @param docID
   * @return List
   * @throws SafeManagerException
   */
  public List<AnnotationDifferResult> listAnnSetNames(String docID)
          throws SafeManagerException;

  /**
   * Retrieve a list of common annotation types on a given pair of annotation sets from a document
   * @param docID
   * @param keyAnnoSetName
   * @param resAnnoSetName
   * @return List
   * @throws SafeManagerException
   */
  public List<String> listSharedAnnotationTypes(String docID, String... asNames) throws SafeManagerException;

  /**
   * Retrieve a list of annotation types on a given annotation set from a document
   * @param docID
   * @param annoSetName
   * @return List
   * @throws SafeManagerException
   */
  public List<AnnotationDifferResult> listAnnotationTypesForSingleAnnotationSet(String docID,
          String annoSetName) throws SafeManagerException;

  /**
   * Retrieve a list of annotation types on a given annotation set from a document
   * @param docID
   * @param annoSetName
   * @return List
   * @throws SafeManagerException
   */
  public List<AnnotationDifferResult> listSharedAnnoTypes(String docID,
          String keyAnnoSetName,String resAnnoSetName) throws SafeManagerException;

  /**
   * Retrieve AnnotationDiffer result
   * @param docID
   * @param keyAnnoSetName
   * @param resAnnoSetName
   * @param annoType
   * @return List
   * @throws SafeManagerException
   */
  public List<AnnotationDifferResult> getAnnoDifferResult(String docID,
          String keyAnnoSetName, String resAnnoSetName, String annoType)
          throws SafeManagerException;

  /**
   * @return AnnotationDifferScores
   */
  public AnnotationDifferScores getAnnDiffScores();


  /**
   * Checks if annotation set with particular name exists in particular document.
   *
   * @param docID
   *          persistent ID of the document
   * @param annSetName
   *          a name of annotation set to test.
   *          <code>null</code> means default annotation set
   *          (currently always exist in any document)
   * @return <code>true</code> if annotation set with given name exists
   *          in given document, <code>false</code> otherwise.
   */
  public boolean annotationSetNameExists(String docID, String annSetName) throws SafeManagerException;

  /**
   * Delete an annotation set from the given document in the doc
   * service.
   *
   * @param docID the document ID
   * @param asName the name of the annotation set to delete
   * @throws SafeManagerException if the given set cannot be locked, or if
   *           the operation fails for any other reason.
   */
  public boolean deleteAnnotationSet(String docID, String asName) throws SafeManagerException;

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
  public boolean deleteAnnotationSet(gate.Document doc, String docServiceASName) throws SafeManagerException;

  /**
   * Release a locked annotation set from a specific document.
   * @param taskID
   * @return
   * @throws SafeManagerException
   */
  public boolean releaseLock(String taskID) throws SafeManagerException;

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
   */
  public IAAResult calculateIAA(String[] docIDs, String[] asNames,
          String annotationType, String featureName, IAAAlgorithm algorithm) throws SafeManagerException;

  /**
   * Get the document in the format of GATE XML with a given doc id.
   * @param docID
   * @return
   * @throws SafeManagerException
   */
  public byte[] getDocXML(String docID) throws SafeManagerException;

}
