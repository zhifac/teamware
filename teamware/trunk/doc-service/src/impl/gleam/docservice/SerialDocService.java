/*
 *  SerialDocService.java
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

package gleam.docservice;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Corpus;
import gate.DataStore;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.LanguageResource;
import gate.corpora.DocumentStaxUtils;
import gate.corpora.SerialCorpusImpl;
import gate.creole.ResourceInstantiationException;
import gate.creole.annic.Constants;
import gate.creole.annic.Hit;
import gate.creole.annic.Indexer;
import gate.creole.annic.Parser;
import gate.creole.annic.SearchException;
import gate.creole.annic.Searcher;
import gate.creole.annic.apache.lucene.search.IndexSearcher;
import gate.creole.annic.lucene.LuceneIndexer;
import gate.creole.annic.lucene.LuceneSearcher;
import gate.creole.annic.lucene.StatsCalculator;
import gate.iaaplugin.IaaCalculation;
import gate.persist.LuceneDataStoreImpl;
import gate.persist.PersistenceException;
import gleam.docservice.iaa.AllWaysFMeasureDetail;
import gleam.docservice.iaa.AllWaysKappaDetail;
import gleam.docservice.iaa.FMeasure;
import gleam.docservice.iaa.FMeasureDetailForAnnotator;
import gleam.docservice.iaa.FMeasureDetailForAnnotatorPairs;
import gleam.docservice.iaa.FMeasureDetailForLabel;
import gleam.docservice.iaa.KappaDetailForAnnotator;
import gleam.docservice.iaa.KappaDetailForAnnotatorPairs;
import gleam.docservice.iaa.PairwiseFMeasureDetail;
import gleam.docservice.iaa.PairwiseKappaDetail;
import gleam.util.adapters.MapWrapper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.jws.WebService;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Implementation of a DocService based on GATE serial datastore.<br>
 * <br>
 * </p>
 * Takes parameters from application context.<br>
 * </p>
 * Holds a connection to a SerialDataStore.
 * http://www.gate.ac.uk/GateExamples/doc/java2html/sheffield/examples/DataStoreApplication.java.html<br>
 * </p>
 * {questions, comments, claims} -> Andrey Shafirin, Julien Nioche
 * Updated for CXF by Ian Roberts
 */
@WebService(endpointInterface = "gleam.docservice.DocService", targetNamespace = "http://gate.ac.uk/ns/safe/1.1/doc-service")
public class SerialDocService implements DocService {
	protected final Log log = LogFactory.getLog(getClass());


	/**
	 * Parameter name for location of serial datastore. Method
	 * {@link #init(Object)} will look for a parameter with this name in the
	 * context.
	 */
	public static final String DATASTORE_LOCATION_PARAMETER_NAME = "DSLocation";

	/**
	 * Parameter name for location of index of searchable datastore. Method
	 * {@link #init(Object)} will look for a parameter with this name in the
	 * context.
	 */
	public static final String DATASTORE_INDEX_LOCATION_PARAMETER_NAME = "ds-index-location";

	/**
	 * Parameter name for the names of annotation sets to be indexed in
	 * searchable datastore. Method {@link #init(Object)} will look for a
	 * parameter with this name in the context.<br>
	 * Default datastore will be used if this parameter undefined or empty
	 * string.
	 */
	public static final String AS_NAMES_TO_INCLUDE_PARAMETER_NAME = "indexed-as-names-to-include";

	/**
	 * Parameter name for the names of annotation sets to be excluded in
	 * searchable datastore. Method {@link #init(Object)} will look for a
	 * parameter with this name in the context.<br>
	 * Default datastore will be used if this parameter undefined or empty
	 * string.
	 */
	public static final String AS_NAMES_TO_EXCLUDE_PARAMETER_NAME = "indexed-as-names-to-exclude";

	/**
	 * Parameter name for the features to be excluded in searchable datastore.
	 * Method {@link #init(Object)} will look for a parameter with this name in
	 * the context.<br>
	 * SpaceToken and Split are by default removed
	 */
	public static final String FEATURES_TO_EXCLUDE_PARAMETER_NAME = "features-to-exclude";

	/**
	 * Parameter name for the features to be excluded in searchable datastore.
	 * Method {@link #init(Object)} will look for a parameter with this name in
	 * the context.<br>
	 * SpaceToken and Split are by default removed
	 */
	public static final String FEATURES_TO_INCLUDE_PARAMETER_NAME = "features-to-include";

	/**
	 * create tokens automatically parameter name - this parameter indicates
	 * what to do when there are no base tokens provided in the document.
	 */
	public static final String CREATE_TOKENS_AUTOMATICALLY_PARAMETER_NAME = "create-tokens-automatically";
	
	/**
	 * Parameter name for the number of milliseconds delay after a sync
	 * before we start indexing a document.
	 */
	public static final String INDEX_DELAY_PARAMETER_NAME = "index-delay";

	public static final String INDEXED_AS_NAME_DEFAULT = gate.creole.annic.Constants.DEFAULT_ANNOTATION_SET_NAME;

	public static final String DEBUG_PARAMETER_NAME = "debug";

	public static final String DEBUG_DETAILS_PARAMETER_NAME = "debug-details";

	/**
	 * Fully qualified class name for GATE's serial datasore. Used in calls to
	 * GATE's Factory.
	 */
	public static final String SERIAL_DATASTORE_CLASS_NAME = "gate.persist.SerialDataStore";

	/**
	 * Fully qualified class name for annic searchable datasore. Used in calls
	 * to GATE's Factory.
	 */
	public static final String SEARCHABLE_DATASTORE_CLASS_NAME = "gleam.docservice.DocServiceInternalDataStore";

	public static final String BASE_TOKEN_ANNOTATION_TYPE_PARAMETER_NAME = "base-token-annotation-type";

	public static final String BASE_TOKEN_ANNOTATION_TYPE_DEFAULT = "Token";

	public static final String INDEX_UNIT_ANNOTATION_TYPE_PARAMETER_NAME = "index-unit-annotation-type";

	public static final String INDEX_UNIT_ANNOTATION_TYPE_DEFAULT = "Sentence";

	/**
	 * JVM used memory threshold after which chache will be partially reduced by
	 * unloading documents with oldest last-access-time Values for threshold
	 * between 0 and 1 Reccomended values 0.5 - 0.9, default 0.9
	 */
	public static final String CACHE_CLEANUP_THRESHOLD_PARAMETER_NAME = "cache-cleanup-threshold";

	protected Properties properties = null;

	// private static String dataStoreLocation;

	/**
	 * Internal reference to the data store.
	 */
	private DocServiceInternalDataStore datastore;

  /**
   * Fully qualified class name for GATE's serial corpus language resource.
   * Used in calls to GATE's Factory.
   */
  protected String corpusClassName = "gate.corpora.SerialCorpusImpl";

  /**
   * Fully qualified class name for GATE's corpus language resource. Used in
   * calls to GATE's Factory.
   */
  protected String transientCorpusClassName = "gate.corpora.CorpusImpl";

  /**
   * Fully qualified class name for GATE's document language resource. Used in
   * calls to GATE's Factory.
   */
  protected String documentClassName = "gate.corpora.DocumentImpl";

  /**
	 * Cache for the language resources. Two goals: speed up the processing of
	 * client commands TODO: remove later... and prevents the temporary problem
	 * with listeners
	 */
	private Map lrCache = new ConcurrentHashMap();

	private MemoryManager memoryManager;
	
	protected LockManager lockManager;

	//private Object LOCK = new Object();

	protected static XMLInputFactory inputFactory = XMLInputFactory.newInstance();

	protected static XMLOutputFactory outputFactory = XMLOutputFactory
			.newInstance();

	public static final String SEARCHER_TIMEOUT = "searcher-cleanup-timeout";

	private Map<String, SearcherEntry> searchers = new HashMap<String, SearcherEntry>();

	private static int searcherIDCounter = 0;

	private SearcherCleaner searcherCleaner;
	
	/**
	 * This is a map that stores the ReentrantReadWriteLock objects
	 * for each LR ID.  It is a map to Object rather than to
	 * ReentrantReadWriteLock, as we use a two-phase algorithm when
	 * creating the lock objects that first involves storing a
	 * CountDownLatch into the map, then creating the real lock
	 * if necessary and then storing a soft reference to that.
	 */
	private ConcurrentHashMap<String, Object> rwLocksByLrID = new ConcurrentHashMap<String, Object>();
	
	/**
	 * Reference queue used to manage GC-expiry of the lock objects
	 * held by soft reference in the rwLocksByLrID map.
	 */
	private ReferenceQueue<ReentrantReadWriteLock> rwLocksRefQueue = new ReferenceQueue<ReentrantReadWriteLock>();
	
	/**
	 * Map of LR ID to CountDownLatch used to prevent two threads
	 * from trying to load the same document from the datastore at
	 * the same time.
	 */
	private ConcurrentHashMap<String, CountDownLatch> lrLoadLatches =
	        new ConcurrentHashMap<String, CountDownLatch>();

	public SerialDocService() {}
	
	/**
	 * Hook to allow the LockManager to be created by Spring.
	 */
	public void setLockManager(LockManager lm) {
	  if(lockManager != null) {
	    throw new IllegalStateException("Lock manager already set");
	  }
	  lockManager = lm;
	}

	/**
	 * Hook to allow the properties to be set by Spring.
	 */
	public void setProperties(Properties p) {
	  if(properties != null) {
	    throw new IllegalStateException("Properties have already been set");
	  }
	  properties = p;
	}

	/**
	 * Get the class name used for storing persistent corpora in the
	 * datastore.
	 */
	public String getCorpusClassName() {
    return corpusClassName;
  }

	/**
	 * Set the class name used for storing persistent corpora in the
	 * datastore (default "gate.corpora.SerialCorpusImpl").
	 */
  public void setCorpusClassName(String corpusClassName) {
    this.corpusClassName = corpusClassName;
  }

  /**
   * Get the class name used for creating transient corpora.
   */
  public String getTransientCorpusClassName() {
    return transientCorpusClassName;
  }

  /**
   * Set the class name used for creating transient corpora
   * (default "gate.corpora.CorpusImpl").
   */
  public void setTransientCorpusClassName(String transientCorpusClassName) {
    this.transientCorpusClassName = transientCorpusClassName;
  }

  /**
   * Get the class name used for storing documents in the
   * datastore.
   */
  public String getDocumentClassName() {
    return documentClassName;
  }

  /**
   * Set the class name used for storing documents in the
   * datastore (default "gate.corpora.DocumentImpl").
   */
  public void setDocumentClassName(String documentClassName) {
    this.documentClassName = documentClassName;
  }

  /**
	 * Opens and returns underlying serial datastore. Checks that the datastore
	 * is available.
	 * 
	 * @return reference to underlying serial datastore or null in case of
	 *         failure
	 */
	protected DocServiceInternalDataStore getDataStore() {
		if (datastore != null)
			return datastore;
		try {
			log.debug(".getDataStore(): opening datastore '"
						+ properties
								.getProperty(DATASTORE_LOCATION_PARAMETER_NAME)
						+ "'");
			datastore = (DocServiceInternalDataStore) Factory.openDataStore(
					SEARCHABLE_DATASTORE_CLASS_NAME, properties
							.getProperty(DATASTORE_LOCATION_PARAMETER_NAME));
			String delayProperty = properties.getProperty(INDEX_DELAY_PARAMETER_NAME);
			if(delayProperty != null) {
			  try {
			    datastore.setIndexDelay(Long.parseLong(delayProperty));
			  }
			  catch(NumberFormatException e) {
			    log.warn(".getDataStore(): "
			            + INDEX_DELAY_PARAMETER_NAME
			            + " parameter could not be parsed as a number. "
			            + "Using default value of "
			            + datastore.getIndexDelay()
			            + "ms instead");
			  }
			}
			return datastore;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Opens and returns underlying serial datastore. Checks that the datastore
	 * is available.
	 * 
	 * @return reference to underlying serial datastore or null in case of
	 *         failure
	 */
	private DataStore createDataStore() {
		if (datastore != null)
			return datastore;
		try {
			boolean ok = true;
			if (properties.getProperty(DATASTORE_LOCATION_PARAMETER_NAME) == null
					&& properties.getProperty(
							DATASTORE_INDEX_LOCATION_PARAMETER_NAME).length() == 0) {
				log.error("ERROR! Datastore index location undefined!");
				ok = false;
			}
			if (properties.getProperty(DATASTORE_INDEX_LOCATION_PARAMETER_NAME) == null
					&& properties.getProperty(
							DATASTORE_INDEX_LOCATION_PARAMETER_NAME).length() == 0) {
				log.error("ERROR! Datastore index location undefined!");
				ok = false;
			}
			if (properties
					.getProperty(BASE_TOKEN_ANNOTATION_TYPE_PARAMETER_NAME) == null
					&& properties.getProperty(
							BASE_TOKEN_ANNOTATION_TYPE_PARAMETER_NAME).length() == 0) {
				log.error("ERROR! Base token annotation type undefined!");
				ok = false;
			}
			if (properties
					.getProperty(INDEX_UNIT_ANNOTATION_TYPE_PARAMETER_NAME) == null
					&& properties.getProperty(
							INDEX_UNIT_ANNOTATION_TYPE_PARAMETER_NAME).length() == 0) {
				log.error("ERROR! Index unit annotation type undefined!");
				ok = false;
			}

			String propValue = properties
					.getProperty(CREATE_TOKENS_AUTOMATICALLY_PARAMETER_NAME);
			boolean createTokensAutomatically = true;
			if (propValue != null && propValue.length() > 0) {
				createTokensAutomatically = Boolean.parseBoolean(propValue);
			}

			// find out annotation sets to be excluded
			String annotSetsToExclude = properties
					.getProperty(AS_NAMES_TO_EXCLUDE_PARAMETER_NAME);
			List<String> annotSetsToExcludeList = new ArrayList<String>();
			if (annotSetsToExclude != null
					&& annotSetsToExclude.trim().length() > 0) {
				String[] annotSets = annotSetsToExclude.split("[ ;]+");
				annotSetsToExcludeList = Arrays.asList(annotSets);
			}

			// find out annotation sets to be included
			List<String> annotSetsToIncludeList = new ArrayList<String>();
			String annotSetsToInclude = properties
					.getProperty(AS_NAMES_TO_INCLUDE_PARAMETER_NAME);
			if (annotSetsToInclude != null
					&& annotSetsToInclude.trim().length() > 0) {
				String[] annotSets = annotSetsToInclude.split("[ ;]+");
				annotSetsToIncludeList = Arrays.asList(annotSets);
			}

			// find out features to be excluded
			String featsToExclude = properties
					.getProperty(FEATURES_TO_EXCLUDE_PARAMETER_NAME);
			List<String> featsToExcludeList = new ArrayList<String>();
			if (featsToExclude != null && featsToExclude.trim().length() > 0) {
				String[] feats = featsToExclude.split("[ ;]+");
				featsToExcludeList = Arrays.asList(feats);
			}

			// find out features to be included
			List<String> featsToIncludeList = new ArrayList<String>();
			String featsToInclude = properties
					.getProperty(FEATURES_TO_INCLUDE_PARAMETER_NAME);
			if (featsToInclude != null && featsToInclude.trim().length() > 0) {
				String[] feats = featsToInclude.split("[ ;]+");
				featsToIncludeList = Arrays.asList(feats);
			}

			if (!ok)
				return null;

			log.debug("Creating new datastore:\n"
								+ "DS location: '"
								+ properties
										.getProperty(DATASTORE_LOCATION_PARAMETER_NAME)
								+ "'\n"
								+ "DS index location: '"
								+ properties
										.getProperty(DATASTORE_INDEX_LOCATION_PARAMETER_NAME)
								+ "'\n"
								+ "DS base token annotation type: '"
								+ properties
										.getProperty(BASE_TOKEN_ANNOTATION_TYPE_PARAMETER_NAME)
								+ "'\n"
								+ "DS create tokens automatically: '"
								+ properties
										.getProperty(CREATE_TOKENS_AUTOMATICALLY_PARAMETER_NAME)
								+ "'\n"
								+ "DS index unit annotation type: '"
								+ properties
										.getProperty(INDEX_UNIT_ANNOTATION_TYPE_PARAMETER_NAME)
								+ "'\n"
								+ "DS annotation sets to be excluded: '"
								+ properties
										.getProperty(AS_NAMES_TO_EXCLUDE_PARAMETER_NAME)
								+ "'\n"
								+ "DS annotation sets to be included: '"
								+ properties
										.getProperty(AS_NAMES_TO_INCLUDE_PARAMETER_NAME)
								+ "'\n"
								+ "DS features to be excluded: '"
								+ properties
										.getProperty(FEATURES_TO_EXCLUDE_PARAMETER_NAME)
								+ "'\n"
								+ "DS features to be included: '"
								+ properties
										.getProperty(FEATURES_TO_INCLUDE_PARAMETER_NAME)

								+ "'\n");
			 File dsLocation = new File(new URL(properties
			 .getProperty(DATASTORE_LOCATION_PARAMETER_NAME)).getFile());
			
	
			dsLocation.mkdirs();
			datastore = (DocServiceInternalDataStore) Factory.createDataStore(
					SEARCHABLE_DATASTORE_CLASS_NAME, properties
							.getProperty(DATASTORE_LOCATION_PARAMETER_NAME));
			Indexer indexer = new LuceneIndexer(new URL(properties
					.getProperty(DATASTORE_INDEX_LOCATION_PARAMETER_NAME)));
			Map parameters = new HashMap();
			parameters
					.put(
							gate.creole.annic.Constants.INDEX_LOCATION_URL,
							new URL(
									properties
											.getProperty(DATASTORE_INDEX_LOCATION_PARAMETER_NAME)));
			parameters
					.put(
							gate.creole.annic.Constants.BASE_TOKEN_ANNOTATION_TYPE,
							properties
									.getProperty(BASE_TOKEN_ANNOTATION_TYPE_PARAMETER_NAME));
			parameters
					.put(
							gate.creole.annic.Constants.INDEX_UNIT_ANNOTATION_TYPE,
							properties
									.getProperty(INDEX_UNIT_ANNOTATION_TYPE_PARAMETER_NAME));
			parameters
					.put(
							gate.creole.annic.Constants.ANNOTATION_SETS_NAMES_TO_EXCLUDE,
							annotSetsToExcludeList);

			parameters
					.put(
							gate.creole.annic.Constants.ANNOTATION_SETS_NAMES_TO_INCLUDE,
							annotSetsToIncludeList);

			parameters.put(gate.creole.annic.Constants.FEATURES_TO_EXCLUDE,
					featsToExcludeList);

			parameters.put(gate.creole.annic.Constants.FEATURES_TO_INCLUDE,
					featsToIncludeList);
			parameters.put(
					gate.creole.annic.Constants.CREATE_TOKENS_AUTOMATICALLY,
					new Boolean(createTokensAutomatically));

			((LuceneDataStoreImpl) datastore).setIndexer(indexer, parameters);
			((LuceneDataStoreImpl) datastore).setSearcher(new LuceneSearcher());
			datastore.open();
	    String delayProperty = properties.getProperty(INDEX_DELAY_PARAMETER_NAME);
      if(delayProperty != null) {
        try {
          datastore.setIndexDelay(Long.parseLong(delayProperty));
        }
        catch(NumberFormatException e) {
          log.warn(INDEX_DELAY_PARAMETER_NAME
                  + " parameter could not be parsed as a number. "
                  + "Using default value of "
                  + datastore.getIndexDelay()
                  + "ms instead");
        }
      }
			// this.datastore.setAutoSaving(true);
			return datastore;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Get the current ReadWriteLock object for the given LR ID.
	 * If no lock currently exists, one is created.
	 * 
	 * @param lrID the LR ID
	 * @return the ReentrantReadWriteLock for this LR
	 */
	protected ReentrantReadWriteLock getReadWriteLock(String lrID) {
	  log.debug(".getReadWriteLock(lrID=" + lrID + ") called");
	  processRefQueue();
	  CountDownLatch myLatch = new CountDownLatch(1);
	  while(true) {
	    // see if there is already a lock in the map for this LR ID.
	    // If not, we atomically insert our latch.
  	  Object latchOrLock = rwLocksByLrID.putIfAbsent(lrID, myLatch);
  	  if(latchOrLock == null) {
  	    // there was nothing already in the map for this LR ID, so
  	    // we create our own lock object, then notify any other
  	    // threads that are waiting for us.
  	    log.debug(".getReadWriteLock: no lock found for LR ID "
  	              + lrID + ", creating one");
  	    
  	    ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
  	    SoftReference<ReentrantReadWriteLock> lockRef =
  	            new LabelledSoftReference<String, ReentrantReadWriteLock>(lrID, rwLock,
  	                    rwLocksRefQueue);
  	    rwLocksByLrID.put(lrID, lockRef);
  	    myLatch.countDown();
  	    return rwLock;
  	  }
  	  else if(latchOrLock instanceof SoftReference) {
  	    ReentrantReadWriteLock lock = ((SoftReference<ReentrantReadWriteLock>)latchOrLock).get();
  	    if(lock == null) {
  	      // soft reference has been cleared by the GC, remove it from the
  	      // map and go around the outer loop again. We only remove if it
  	      // is still mapped to the same value - if not, another thread
  	      // has got in before us
  	      log.debug(".getReadWriteLock: cleared soft reference found for LR ID "
                    + lrID + ", removing");
  	      
  	      rwLocksByLrID.remove(lrID, latchOrLock);
  	    }
  	    else {
    	    // there is an existing lock in the map for this LR ID, so
    	    // use that
          log.debug(".getReadWriteLock: found existing lock for LR ID "
                    + lrID);
          
    	    return lock;
  	    }
  	  }
  	  else {
  	    // latchOrLock instanceof CountDownLatch - some other thread is
  	    // in the process of creating a lock for this lrID, so wait at
  	    // the other thread's latch for it to finish.
  	    boolean passedAwait = false;
  	    while(!passedAwait) {
    	    try {
            log.debug(".getReadWriteLock: another thread is creating a "
                      + "lock for LR ID "
                      + lrID + ", waiting");
            
            ((CountDownLatch)latchOrLock).await();
            passedAwait = true;
          }
          catch(InterruptedException e) {
            // do nothing, go around the loop again
          }
  	    }
  	    // here, the other thread has finished creating its lock, so go
  	    // round the loop again trying to fetch the other thread's lock
  	    // from the map.
  	  }
	  }
	}
	
	/**
	 * Remove entries from the RW lock cache that have been cleared and
	 * enqueued by the garbage collector.
	 */
	private void processRefQueue() {
	  LabelledSoftReference<String, ReentrantReadWriteLock> ref = null;
	  while((ref = (LabelledSoftReference<String, ReentrantReadWriteLock>)
	          rwLocksRefQueue.poll()) != null) {
	    // safety check - only remove entries that have the mapping
	    // we expect.  If the label (LR ID) is mapped to something
	    // other than ref then another thread has done something
	    // before we got here, so leave that alone.
	    if(rwLocksByLrID.remove(ref.getLabel(), ref)) {
	      log.debug(".processRefQueue: cleared lock for LR ID "
                  + ref.getLabel());
	      
	    }
	  }
	}

	/**
	 * Returns a language resource from underlying SerialDatastore. This methods
	 * redirects to the GATE Factory, thus its functionality is 'most common
	 * function' of the Factory.createResource and GATE's SerialDataStore
	 * abilities. Callers should acquire the relevant read or write lock before
	 * calling this method.
	 * 
	 * @param lrClass
	 *            fully qualified Java class name of the language resource
	 * @param lrID
	 *            persistent ID for the resource
	 * 
	 * @return language resource if it found or null otherwise
	 */
	protected LanguageResource getLR(String lrClass, String lrID)
			throws ResourceInstantiationException {
		log.debug(".getLR(lrClass=" + lrClass + ", lrID=" + lrID
					+ ") called. lrCache.size=" + lrCache.size());
		// LanguageResource lr = (LanguageResource) lrCache.get(lrID);
		CountDownLatch myLatch = new CountDownLatch(1);
		CountDownLatch otherLatch = null;
    try {
  		while((otherLatch = lrLoadLatches.putIfAbsent(lrID, myLatch)) != null) {
  		  boolean passedAwait = false;
  		  while(!passedAwait) {
    		  try {
    		    log.debug(".getLR: another thread is working on this LR ID (latch = "
    		              + otherLatch + "), waiting...");
    		    
            otherLatch.await();
            passedAwait = true;
          }
          catch(InterruptedException e) {
            // do nothing, go around the loop again
          }
  		  }
  		}
  		
  		// we know that anybody else who is simultaneously trying
  		// to load the same LR is now waiting on our latch
		
		  LanguageResource lr = null;
  		LRData lrData = (LRData)lrCache.get(lrID);
  		if (lrData != null) {
  			lr = lrData.getLr();
        if (lrClass.equals(documentClassName)) {
          log.debug(".getLR(..): returning document with ID=\"" + lrID
              + "\" from cache. Default annotation set size="
              + ((Document) lr).getAnnotations().size());
        }
  		}
  		else {
  			FeatureMap fm = Factory.newFeatureMap();
  			fm.put(DataStore.LR_ID_FEATURE_NAME, lrID);
  			fm.put(DataStore.DATASTORE_FEATURE_NAME, getDataStore());
  			// tell the factory to load the resource with the specified ID
  			// from the specified datastore
  			log.debug(".getLR(..): loading resource \"" + lrClass
  						+ "\" with ID=\"" + lrID + "\"");
  			
  			lr = (LanguageResource) Factory.createResource(lrClass, fm);
  			if (lrClass.equals(documentClassName)) {
  				log.debug(".getLR(..): loaded GATE document. Default annotation set size="
  								+ ((Document) lr).getAnnotations().size());
  			}
  			lrCache.put(lrID, new LRData(lr));
  
  			if (lrClass.equals(documentClassName)) {
  				Document d = (Document) ((LRData) lrCache.get(lrID)).getLr();
            log.debug(".getLR(..): Document stored in cache contains default annotation set size="
  								+ d.getAnnotations().size());
  			}
  		}
      return lr;
		}
		finally {
      // release anyone waiting for us to load the LR
      lrLoadLatches.remove(lrID, myLatch);
      myLatch.countDown();
		}

	}

	// ================================
	// corpus operations
	// ================================

	public CorpusInfo[] listCorpora() {
		log.debug(".listCorpora() called");
		try {
			List<String> corpusIDs = getDataStore().getLrIds(
					corpusClassName);
			CorpusInfo[] result = new CorpusInfo[corpusIDs.size()];
			for (int i = 0; i < corpusIDs.size(); i++) {
			  ReentrantReadWriteLock lock = getReadWriteLock(corpusIDs.get(i));
			  lock.readLock().lock();
			  SerialCorpusImpl corpus = (SerialCorpusImpl) getLR(
							corpusClassName, corpusIDs.get(i));
			  lock.readLock().unlock();
				result[i] = new CorpusInfo(corpusIDs.get(i), corpus.getName(), corpus.size());
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}

	public CorpusInfo getCorpusInfo(String corpusID) {
		log.debug(".getCorpusInfo(" + corpusID + ") called");
		try {
			Corpus corpus = (Corpus) getLR(corpusClassName,
					corpusID);
			if (corpus == null)
				return null;
			CorpusInfo cInfo = new CorpusInfo((String) corpus
					.getLRPersistenceId(), corpus.getName());
			return cInfo;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public DocumentInfo[] listDocs(String corpusID) {
		log.debug(".listDocs(" + corpusID + ") called");
		ReentrantReadWriteLock lock = getReadWriteLock(corpusID);
		lock.readLock().lock();
		try {
			SerialCorpusImpl corpus = (SerialCorpusImpl) getLR(
					corpusClassName, corpusID);
			if (corpus == null)
				return null;
			DocumentInfo[] result = new DocumentInfo[corpus.size()];
			for (int i = 0; i < corpus.size(); i++) {
				result[i] = new DocumentInfo((String) corpus
						.getDocumentPersistentID(i), corpus
						.getDocumentName(i));
			}
			return result;
		} catch (ResourceInstantiationException e) {
			e.printStackTrace();
			return null;
		}
		finally {
		  lock.readLock().unlock();
		}
	}

	public MapWrapper<String, String> getCorpusFeatures(String corpusID) throws DocServiceException {
		log.debug(".getCorpusFeatures(" + corpusID + ") called");
    ReentrantReadWriteLock lock = getReadWriteLock(corpusID);
    lock.readLock().lock();
    try {
      return MapWrapper.wrap(getLRFeatures(corpusClassName, corpusID));
    }
    finally {
      lock.readLock().unlock();
    }
	}

	public void setCorpusFeatures(String corpusID, MapWrapper<String, String> featuresWrapper)
			throws DocServiceException {
	  Map<String, String> features = MapWrapper.unwrap(featuresWrapper);
		log.debug(".setCorpusFeatures(" + corpusID + ") called");
		FeatureMap fm = Factory.newFeatureMap();
		fm.putAll(features);
		ReentrantReadWriteLock lock = getReadWriteLock(corpusID);
		lock.writeLock().lock();
		try {
			LanguageResource lr = getLR(corpusClassName, corpusID);
			lr.setFeatures(fm);
			getDataStore().sync(lr);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DocServiceException(e);
		}
		finally {
		  lock.writeLock().unlock();
		}
	}
	
	public String getCorpusFeature(String corpusID, String featureName)
          throws DocServiceException {
    log.debug(".getCorpusFeature(" + corpusID + ", " + featureName + ") called");
    ReentrantReadWriteLock lock = getReadWriteLock(corpusID);
    lock.readLock().lock();
    try {
      return getLRFeature(corpusClassName, corpusID, featureName);
    }
    finally {
      lock.readLock().unlock();
    }
  }

  public void setCorpusFeature(String corpusID, String featureName,
          String featureValue) throws DocServiceException {
    log.debug(".setCorpusFeature(" + corpusID + ", " + featureName + ") called");
    ReentrantReadWriteLock lock = getReadWriteLock(corpusID);
    lock.readLock().lock();
    try {
      setLRFeature(corpusClassName, corpusID, featureName, featureValue);
    }
    finally {
      lock.readLock().unlock();
    }
  }

  public void setCorpusName(String corpusID, String name) throws DocServiceException {
    log.debug(".setCorpusName(" + corpusID + ", \"" + name + "\") called");
    ReentrantReadWriteLock lock = getReadWriteLock(corpusID);
    lock.writeLock().lock();
    try {
      setLRName(corpusClassName, corpusID, name);
    }
    finally {
      lock.writeLock().unlock();
    }
	}

	public MapWrapper<String, String> getDocumentFeatures(String documentID)
			throws DocServiceException {
		log.debug(".getDocumentFeatures(" + documentID + ") called");
		ReentrantReadWriteLock lock = getReadWriteLock(documentID);
		lock.readLock().lock();
		try {
		  return MapWrapper.wrap(getLRFeatures(documentClassName, documentID));
		}
		finally {
		  lock.readLock().unlock();
		}
	}

	public void setDocumentFeatures(String documentID, MapWrapper<String, String> featuresWrapper)
			throws DocServiceException {
	  Map<String, String> features = MapWrapper.unwrap(featuresWrapper);
		log.debug(".setDocumentFeatures(" + documentID + ") called");
		FeatureMap fm = Factory.newFeatureMap();
		fm.putAll(features);
		ReentrantReadWriteLock lock = getReadWriteLock(documentID);
		lock.writeLock().lock();
		try {
			LanguageResource lr = getLR(documentClassName, documentID);
			lr.setFeatures(fm);
			getDataStore().sync(lr);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DocServiceException(e);
		}
		finally {
		  lock.writeLock().unlock();
		}
	}
	
  public String getDocumentFeature(String documentID, String featureName)
          throws DocServiceException {
    log.debug(".getDocumentFeature(" + documentID + ", " + featureName + ") called");
    ReentrantReadWriteLock lock = getReadWriteLock(documentID);
    lock.readLock().lock();
    try {
      return getLRFeature(documentClassName, documentID, featureName);
    }
    finally {
      lock.readLock().unlock();
    }
  }

  public void setDocumentFeature(String documentID, String featureName,
          String featureValue) throws DocServiceException {
    log.debug(".setDocumentFeature(" + documentID + ", " + featureName + ") called");
    ReentrantReadWriteLock lock = getReadWriteLock(documentID);
    lock.readLock().lock();
    try {
      setLRFeature(documentClassName, documentID, featureName, featureValue);
    }
    finally {
      lock.readLock().unlock();
    }
  }
	
	public void setDocumentName(String documentID, String name) throws DocServiceException {
    log.debug(".setDocumentName(" + documentID + ", \"" + name + "\") called");
    ReentrantReadWriteLock lock = getReadWriteLock(documentID);
    lock.writeLock().lock();
    try {
      setLRName(documentClassName, documentID, name);
    }
    finally {
      lock.writeLock().unlock();
    }
	}
	
	/**
	 * Set the name of an LR (document or corpus).
	 */
	protected void setLRName(String lrClass, String id, String name) throws DocServiceException {
	  try {
	    LanguageResource lr = getLR(lrClass, id);
	    if(lr == null) {
	      throw new DocServiceException("No such LR: " + id);
	    }
	    lr.setName(name);
	    getDataStore().sync(lr);
    } catch (Exception e) {
      e.printStackTrace();
      throw new DocServiceException(e);
    }
	}

	protected Map<String, String> getLRFeatures(String lrClass, String lrID)
			throws DocServiceException {
		try {
			LanguageResource lr = getLR(lrClass, lrID);
			if (lr == null)
				return null;
			FeatureMap fm = lr.getFeatures();
			Map<String, String> mapToReturn = new HashMap<String, String>(fm.size());
			for(Object key : fm.keySet()) {
			  if(key instanceof String && fm.get(key) instanceof String) {
			    mapToReturn.put((String)key, (String)fm.get(key));
			  }
			  else {
			    throw new DocServiceException("LR " + lrID + " has a feature name or value which is not a String");
			  }
			}
			return mapToReturn;
		} catch (ResourceInstantiationException e) {
			e.printStackTrace();
			throw new DocServiceException(e);
		}
	}
	
  protected String getLRFeature(String lrClass, String lrID, String featureName)
          throws DocServiceException {
    try {
      LanguageResource lr = getLR(lrClass, lrID);
      if (lr == null)
        return null;
      FeatureMap fm = lr.getFeatures();
      Object value = fm.get(featureName);
      if(value == null || value instanceof String) {
        return (String)value;
      }
      else {
        throw new DocServiceException("Feature " + featureName + " of LR "
                + lrID + " has a value which is not a String");
      }
    } catch (ResourceInstantiationException e) {
      e.printStackTrace();
      throw new DocServiceException(e);
    }
  }

  /**
   * Set a feature on an LR.  The calling thread must hold the write lock
   * for the given LR before calling this method.
   */
  protected void setLRFeature(String lrClass, String lrID, String featureName,
          String value) throws DocServiceException {
    try {
      LanguageResource lr = getLR(lrClass, lrID);
      if (lr != null) {
        FeatureMap fm = lr.getFeatures();
        fm.put(featureName, value);
        getDataStore().sync(lr);
      }
    } catch (ResourceInstantiationException e) {
      e.printStackTrace();
      throw new DocServiceException(e);
    } catch (PersistenceException e) {
      e.printStackTrace();
      throw new DocServiceException(e);
    }
  }
	
	public String createCorpus(String corpusName) {
		log.debug(".createCorpus(" + corpusName + ") called");
		try {
			Corpus corpus = (Corpus) Factory
					.createResource(transientCorpusClassName);
			Corpus persistentCorpus = (Corpus) getDataStore().adopt(corpus,
					null);
			Factory.deleteResource(corpus);
			persistentCorpus.setName(corpusName);
			String corpusID = getDataStore().constructPersistenceId(corpusName);
			persistentCorpus.setLRPersistenceId(corpusID);
			ReentrantReadWriteLock lock = getReadWriteLock(corpusID);
			lock.writeLock().lock();
			try {
  			getDataStore().sync(persistentCorpus);
  			if (lrCache.containsKey(persistentCorpus.getLRPersistenceId())) {
  				throw new Exception(
  						"Internal error. Duplicate language resource ID for new resource.");
  			}
  			lrCache.put(persistentCorpus.getLRPersistenceId(), new LRData(
  					persistentCorpus));
  			return (String) persistentCorpus.getLRPersistenceId();
			}
			finally {
			  lock.writeLock().unlock();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public boolean addDocumentToCorpus(String corpusID, String documentID)
			throws DocServiceException {
		log.debug(".addDocumentToCorpus(corpusID=" + corpusID
					+ ", docID=" + documentID + ") called");
		// need to lock the corpus for writing and the document for reading
		ReentrantReadWriteLock corpusLock = getReadWriteLock(corpusID);
		corpusLock.writeLock().lock();
		try {
		  ReentrantReadWriteLock docLock = getReadWriteLock(documentID);
		  docLock.readLock().lock();
			try {

				// lets obtain the already persisted corpus
				Corpus c = (Corpus) getLR(corpusClassName, corpusID);
				Document d = (Document) getLR(documentClassName, documentID);
				if (c == null || d == null)
					return false;
				DocumentInfo[] docInfos = listDocs((String) c
						.getLRPersistenceId());
				for (int i = 0; i < docInfos.length; i++) {
					if (docInfos[i].getDocumentID().equals(
							d.getLRPersistenceId())) {
						log.debug(".addDocumentToCorpus(): Document hasn't been added to corpus. Corpous '"
											+ c.getLRPersistenceId()
											+ "' already contains doc '"
											+ d.getLRPersistenceId() + "'");
						return false;
					}
				}
				log.debug("corp size before add: " + c.size());
				boolean result = c.add(d);
				log.debug("corp size after add: " + c.size());
				getDataStore().sync(c);
				log.debug("corp size after sync: " + c.size());

				// persistent corpus unload the document

				int index = ((SerialCorpusImpl) c).findDocument(d);
				if (index > -1) {
					// unload it without synchronizing it
					((SerialCorpusImpl) c).unloadDocument(index, false);

					// and we take it out from the cache
					lrCache.remove(d.getLRPersistenceId());

					// finally remove doc from memory
					Factory.deleteResource(d);
				}
				return result;
			} catch (Exception e) {
				e.printStackTrace();
				throw new DocServiceException(e);
			}
			finally {
			  docLock.readLock().unlock();
			}
		}
		finally {
		  corpusLock.writeLock().unlock();
		}
	}

	public boolean removeDocumentFromCorpus(String corpusID, String documentID)
			throws DocServiceException {
		log.debug(".removeDocumentFromCorpus(corpusID=" + corpusID
					+ ", docID=" + documentID + ") called");
    // need to lock the corpus for writing and the document for reading
    ReentrantReadWriteLock corpusLock = getReadWriteLock(corpusID);
    corpusLock.writeLock().lock();
    try {
      ReentrantReadWriteLock docLock = getReadWriteLock(documentID);
      docLock.readLock().lock();
			try {

				// obtain the persistant corpus, this will be returned from the
				// cache
				Corpus c = (Corpus) getLR(corpusClassName, corpusID);

				// obtain the document, it will be created using the Factory
				// method
				// and thus will be made available in cache
				Document d = (Document) getLR(documentClassName, documentID);

				if (c == null || d == null)
					return false;
				log.debug("corp size before remove: " + c.size());
				// int docIdx =
				// ((SerialCorpusImpl)c).getDocumentPersistentIDs().indexOf(documentID);
				// if(docIdx != -1) ((SerialCorpusImpl)c).get(docIdx);

				// lets remove it from the corpus
				boolean result = c.remove(d);

				log.debug("corp size after remove: " + c.size());

				// and then synchronize the corpus
				getDataStore().sync(c);

				log.debug("corp size after sync: " + c.size());

				return result;
			} catch (Exception e) {
				e.printStackTrace();
				throw new DocServiceException(e);
			}
			finally {
			  docLock.readLock().unlock();
			}
		}
    finally {
      corpusLock.writeLock().unlock();
    }
	}

	public void deleteCorpus(String corpusID) {
		log.debug(".deleteCorpus(" + corpusID + ") called");
		ReentrantReadWriteLock lock = getReadWriteLock(corpusID);
		lock.writeLock().lock();
		try {
			getDataStore().delete(corpusClassName, corpusID);
			if (lrCache.containsKey(corpusID))
				lrCache.remove(corpusID);
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
		  lock.writeLock().unlock();
		}
	}

	// =================================
	// document operations
	// =================================

	public DocumentInfo[] listDocs() {
		log.debug(".listDocs() called");
		try {
			List<String> docIDs = getDataStore().getLrIds(
					documentClassName);
			DocumentInfo[] result = new DocumentInfo[docIDs.size()];
			for (int i = 0; i < docIDs.size(); i++) {
			  String docName = getDataStore().getLrName(docIDs.get(i));
				result[i] = new DocumentInfo(docIDs.get(i), docName);
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String createDoc(String docName, byte[] docContent, String encoding) {
		log.debug(".createDoc(docName=" + docName + ", docContent.length="
					+ docContent.length + ") called");
		
		try {
			Document doc = createDocumentFromXML(docContent, encoding);
			doc.setName(docName);

			
			// Faktory & DocumentImpl methods can change CRLF in doc text
			// producing
			// inconsistence between client and server
			// don't have a time to go into the discussions and changes for
			// repositioning and CRLF fixing...
			// just fix it BACK... if content wasn't a XML...
			// TODO: here is only dirty fix
			// if (!docContent.startsWith("<")) {
			// doc.setContent(new DocumentContentImpl(docContent));
			// }

			
			// lets adopt the document and create its persistent ID for locking
			Document persistentDoc = (Document) getDataStore().adopt(doc,
					null);
			String docPersistenceID = getDataStore().constructPersistenceId(docName);
			doc.setLRPersistenceId(docPersistenceID);
			ReentrantReadWriteLock lock = getReadWriteLock(docPersistenceID);
			lock.writeLock().lock();
			try {
  			// and we synchronize it
  			getDataStore().sync(persistentDoc);
  
  			// here we have a persistant ID for this document
  			// the document is stored in DATAStore but not as part of the
  			// corpus
  
  			
  			if (lrCache.containsKey(persistentDoc.getLRPersistenceId())) {
  				throw new Exception(
  						"Internal error. Duplicate language resource ID for new resource.");
  			}
  			lrCache.put(persistentDoc.getLRPersistenceId(), new LRData(
  					persistentDoc));
  			return docPersistenceID;
			}
			finally {
			  lock.writeLock().unlock();
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			return null;
		}
	}

	/**
	 * Create a document from its XML representation in the given encoding.
	 * 
	 * @param docContent the XML representation as bytes
	 * @param encoding the character encoding of these bytes
	 * @return a newly created GATE document
	 * @throws Exception if any error occurs creating the document
	 */
  protected Document createDocumentFromXML(byte[] docContent, String encoding)
          throws Exception {
    FeatureMap fm = Factory.newFeatureMap();
    String docContentString = new String(docContent, encoding);
    fm.put("stringContent", docContentString);
    fm.put("mimeType", "text/xml");
    Document doc = (gate.corpora.DocumentImpl) Factory
    		.createResource(documentClassName, fm);
    return doc;
  }

	public String createDoc(String docName, String corpusID,
			byte[] docXmlContent, String encoding) throws DocServiceException {
		log.debug(".createDoc(docName=" + docName + ",corpusID="
					+ corpusID + ", docXmlContent="
					+ docXmlContent 
					+ ") called");
		String docID = createDoc(docName, docXmlContent, encoding);
		addDocumentToCorpus(corpusID, docID);
		return docID;
	}

	public synchronized boolean deleteDoc(String docID)
			throws DocServiceException {
		log.debug(".deleteDoc(" + docID + ") called");
		ReentrantReadWriteLock lock = getReadWriteLock(docID);
		lock.writeLock().lock();
		try {
			if (lrCache.containsKey(docID)) {
				// Document d = (Document) lrCache.get(docID);
				Document d = (Document) ((LRData) lrCache.get(docID))
						.getLr();

				if (lockManager._getLock(
						d.getAnnotations().getName(),
						(String) d.getLRPersistenceId()) != null) {
					throw new DocServiceException(
							"Can't delete document '"
									+ docID
									+ "'. Document default annotation set is locked.");
				}
				if (d.getNamedAnnotationSets() != null
						&& d.getNamedAnnotationSets().size() > 0) {
					Set asNames = d.getAnnotationSetNames();
					Iterator itr = asNames.iterator();
					while (itr.hasNext()) {
						String asName = (String) itr.next();
						AnnotationSet as = (AnnotationSet) d
								.getAnnotations(asName);
						if (lockManager._getLock(
								as.getName(),
								(String) as.getDocument()
										.getLRPersistenceId()) != null) {
							throw new DocServiceException(
									"Can't delete document '"
											+ docID
											+ "'. Document annotation set '"
											+ asName + "' is locked.");
						}
					}
				}
			}

			// remove the document from any corpora that contain it
			List<String> corpusIDs = getDataStore().getLrIds(
              corpusClassName);
      for (int i = 0; i < corpusIDs.size(); i++) {
        boolean corpusContainsDoc = false;
        SerialCorpusImpl corpus = null;
        ReentrantReadWriteLock corpusLock = getReadWriteLock(corpusIDs.get(i));
        corpusLock.readLock().lock();
        try {
          corpus = (SerialCorpusImpl) getLR(
                  corpusClassName, corpusIDs.get(i));
          corpusContainsDoc = corpus.getDocumentPersistentIDs().contains(docID);
        }
        finally {
          // must unlock the read lock here, as removeDocumentFromCorpus
          // needs to acquire the write lock.
          corpusLock.readLock().unlock();
        }
        if (corpusContainsDoc)
          removeDocumentFromCorpus((String) corpus
              .getLRPersistenceId(), docID);
      }
      
			getDataStore().delete(documentClassName, docID);
			if (lrCache.containsKey(docID))
				lrCache.remove(docID);
			return true;
		} catch (DocServiceException e) {
				e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		finally {
		  lock.writeLock().unlock();
		}
	}

	public DocumentInfo getDocInfo(String docID) {
		log.debug(".getDocInfo(" + docID + ") called");
		ReentrantReadWriteLock lock = getReadWriteLock(docID);
		lock.readLock().lock();
		try {
			Document d = (Document) getLR(documentClassName, docID);
			if (d == null)
				return null;
			return new DocumentInfo((String) d.getLRPersistenceId(), d
					.getName());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		finally {
		  lock.readLock().unlock();
		}
	}

	public byte[] getDocXML(String docID) {
		log.debug(".getDocXML(" + docID + ") called");
		ReentrantReadWriteLock lock = getReadWriteLock(docID);
		lock.readLock().lock();
		try {
			Document d = (Document) getLR(documentClassName, docID);
			return documentToUTF8XML(d);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		finally {
		  lock.readLock().unlock();
		}
	}

	/**
	 * Return the UTF-8 XML representation of the given document in
	 * a byte array.
	 * 
	 * @param d the document
	 * @return the XML representation
	 * @throws Exception if an error occurs producing the representation.
	 */
  protected byte[] documentToUTF8XML(Document d) throws Exception {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    XMLStreamWriter xsw = outputFactory.createXMLStreamWriter(baos, "UTF-8");
    xsw.writeStartDocument();
    DocumentStaxUtils.writeDocument(d, xsw, "");
    xsw.close();
    return baos.toByteArray();
  }

	public String getDocContent(String docID) {
		log.debug(".getDocContent(" + docID + ") called");
		ReentrantReadWriteLock lock = getReadWriteLock(docID);
		lock.readLock().lock();
		try {
			Document d = (Document) getLR(documentClassName, docID);
			return d.getContent().toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		finally {
		  lock.readLock().unlock();
		}
	}

	public String[] listAnnotationSets(String docID) throws DocServiceException {
		log.debug(".listAnnotationSets(" + docID + ") called");
		ReentrantReadWriteLock lock = getReadWriteLock(docID);
		lock.readLock().lock();
		try {
			Document d = (Document) getLR(documentClassName, docID);
			String[] result;
			if (d.getNamedAnnotationSets() != null) {
				result = new String[d.getAnnotationSetNames().size() + 1];
				d.getAnnotationSetNames().toArray(result);
			} else {
				result = new String[1];
			}
			result[result.length - 1] = null;
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DocServiceException(
					"An exception occured whyle getting annotation set names.",
					e);
		}
		finally {
		  lock.readLock().unlock();
		}
	}

	public boolean annotationSetNameExists(String docID, String annSetName)
			throws DocServiceException {
	  // no locking required, listAnnotationSets locks for read internally
		log.debug(".annotationSetNameExists(docId=" + docID
					+ ", annSetname=" + annSetName + ") called");
		if (annSetName == null)
			return true;
		String[] names = listAnnotationSets(docID);
		for (String n : names) {
			if (annSetName.equals(n))
				return true;
		}
		return false;
	}

	public synchronized String getAnnotationSetLock(String docID,
			String annSetName) throws DocServiceException {
		log.debug(".getAnnotationSetLock(docID=" + docID
					+ ", annSetName=" + annSetName + ")");
		
		ReentrantReadWriteLock lock = getReadWriteLock(docID);
		lock.readLock().lock();
		try {
		  return getAnnotationSetLock(docID, annSetName, false).taskID;
		}
		finally {
		  lock.readLock().unlock();
		}
	}

	public synchronized AnnotationSetHandle getAnnotationSet(String docID,
			String annSetName, boolean readOnly) throws DocServiceException {
		log.debug(".getAnnotationSet(docID=" + docID + ", annSetName="
					+ annSetName + ", readOnly=" + readOnly + ")");
		
		ReentrantReadWriteLock lock = getReadWriteLock(docID);
		lock.readLock().lock();
		try {
			AnnotationSetAndTaskID asl = getAnnotationSetLock(docID,
					annSetName, readOnly);
			log.debug(".getAnnotationSet: doc.hashCode()="
								+ ((asl.getAnnotationSet().getDocument() == null) ? 0
										: asl.getAnnotationSet()
												.getDocument().hashCode()));
			
			AnnotationSetHandle ash = new AnnotationSetHandle();
			ash.setTaskID(asl.getTaskID());
			// StringBuffer sb = new StringBuffer();
			// DocumentXmlUtils.annotationSetToXml(asl.getAnnotationSet(),
			// sb);
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			XMLStreamWriter xsw = outputFactory
					.createXMLStreamWriter(outputStream, "UTF-8");
			DocumentStaxUtils.writeAnnotationSet(asl.getAnnotationSet(),
					annSetName, xsw, "");
			xsw.close();
			log.debug(".getAnnotationSet: annotation set: "
						+ asl.getAnnotationSet());
			log.debug(".getAnnotationSet: annotation set XML length: "
						+ outputStream.toByteArray().length);
			
			// ash.setData(sb.toString());
			ash.setData(outputStream.toByteArray());
			log.debug(".getAnnotationSet: " + ash.toString());
			
			return ash;
		} catch (DocServiceException e) {
				e.printStackTrace();
			throw e;
		} catch (Throwable e) {
				e.printStackTrace();
			throw new DocServiceException(
					"An exception occured whyle getting annotation set.", e);
		}
		finally {
		  lock.readLock().unlock();
		}
	}

	/**
	 * This method does no locking - callers are expected to acquire a
	 * read lock on the given doc ID before calling this method.
	 */
	protected AnnotationSetAndTaskID getAnnotationSetLock(
			String docID, String annSetName, boolean readOnly)
			throws DocServiceException {
		Document d;
		try {
			d = (Document) getLR(documentClassName, docID);
		} catch (ResourceInstantiationException e) {
				e.printStackTrace();
			throw new DocServiceException(e.getClass().getName()
					+ " occured whyle getting document with docID=" + docID
					+ " for annSetName=" + annSetName + ". \n"
					+ e.getMessage(), e);
		}
		AnnotationSet annSet;
		if (annSetName == null) {
			annSet = d.getAnnotations();
			// for RW check if existing annotation set is locked
			try {
				if (!readOnly
						&& lockManager._getLock(null,
								(String) d.getLRPersistenceId()) != null) {
					throw new DocServiceException(
							"Can't get default annotation set in Read-Write mode."
									+ "\nAnnotation set is locked."
									+ "\nDocument '" + docID + "'");
				}
			} catch (Exception e) {
					e.printStackTrace();
				if (!(e instanceof DocServiceException)) {
					throw new DocServiceException(
							"An exception occured whyle getting annotation set lock for docID="
									+ docID + ", annSetName=" + annSetName
									+ ".", e);
				}
			}
		} else {
			if (d.getNamedAnnotationSets() != null
					&& d.getAnnotationSetNames().contains(annSetName)) {
				// annotation set with given name exists
				annSet = d.getAnnotations(annSetName);
				log.debug(".getAnnotationSetLock(String, String, boolean): annotation set with given name exists...");
				
				// for RW check if existing annotation set is locked
				try {
					if (!readOnly) {
						Lock lock = lockManager._getLock(
									annSetName,
									(String) d.getLRPersistenceId());						
						if(lock != null)
							// FIXME: provide some neat solution for resource locking! JIRA:TMW-7
							throw new DocServiceException("LOCK");
					}
				} catch (Exception e) {
						e.printStackTrace();
					if (!(e instanceof DocServiceException)) {
						throw new DocServiceException(
								"An exception occured whyle getting annotation set lock for docID="
										+ docID + ", annSetName="
										+ annSetName + ".", e);
					}
				}
			} else {
				// annotation set with given name doesn't exists
				log.debug(".getAnnotationSetLock(String, String, boolean): annotation set with given name doesn't exist, returning empty set...");
				
				// return an empty set without creating it on the document.
				// see EmptyAnnotationSet JavaDoc for the reason
				annSet = EmptyAnnotationSet.getInstance();
			}
		}
		if (!readOnly) {
			Lock l;
			try {
				l = lockManager._createLock(annSetName,
						(String) d.getLRPersistenceId());
			}
			catch(DocServiceException e) {
				throw new DocServiceException("LOCK");
			}
			catch (Exception e) {
					e.printStackTrace();
				throw new DocServiceException(
						"An exception occured whyle creating annotation set lock for docID="
								+ docID + ", annSetName=" + annSetName
								+ ".", e);
			}
			log.debug(".getAnnotationSetLock(String, String, boolean): created lock: "
								+ String.valueOf(l.getTaskId()));
			
			return new AnnotationSetAndTaskID(annSet, String.valueOf(l
					.getTaskId()));
		} else {
			return new AnnotationSetAndTaskID(annSet, null);
		}
	}

	public synchronized boolean setAnnotationSet(byte[] xmlContent,
			String taskID, boolean keepLock) throws DocServiceException {
		log.debug(".setAnnotationSet(xmlContent=\"...\", taskID.taskID="
							+ taskID + ", keepLock=" + keepLock + ")");
		
		try {
			Lock l = lockManager._getLock(
					Integer.valueOf(taskID).intValue());
			if(l == null) {
        throw new DocServiceException("Invalid lock: '" + taskID
                + "'");			  
			}
			log.debug(".setAnnotationSet: lockID=" + l);
			ReentrantReadWriteLock lock = getReadWriteLock(l.getDocId());
			lock.writeLock().lock();
			try {
  			Document d = (Document) getLR(documentClassName, l.getDocId());
  			AnnotationSet annSet = (l.getAnnSetName() == null) ? d
  					.getAnnotations() : d.getAnnotations(l.getAnnSetName());
  			// load and add annotations to the doc
  			InputStream inputStream = new ByteArrayInputStream(xmlContent);
  			XMLStreamReader xsr = inputFactory
  					.createXMLStreamReader(inputStream, "UTF-8");
  			log.debug("AnnSet BEFORE set: " + annSet);
  			
  			xsr.nextTag();
  			annSet.clear();
  			DocumentStaxUtils.readAnnotationSet(xsr, annSet, null,
  					new HashSet(), Boolean.valueOf(true));
  			log.debug(".setAnnotationSet(xmlContent=\"...\", taskID.taskID="
  								+ taskID + ", keepLock=" + keepLock
  								+ "): ann set size=" + annSet.size());
  			
  			getDataStore().sync(d);
  			if (!keepLock) {
  				lockManager._releaseLock(
  						Integer.valueOf(taskID).intValue());
  			} else {
  				lockManager._refreshLock(
  						Integer.valueOf(taskID).intValue());
  			}
  			return true;
			}
			finally {
			  lock.writeLock().unlock();
			}
		} catch (DocServiceException e) {
				e.printStackTrace();
			throw e;
		} catch (IOException e) {
				e.printStackTrace();
			throw new DocServiceException(
					"An exception occured whyle parsing annotation set XML.",
					e);
		} catch (PersistenceException e) {
				e.printStackTrace();
			throw new DocServiceException(
					"An exception occured whyle saving the updated document.",
					e);
		} catch (gate.security.SecurityException e) {
				e.printStackTrace();
			throw new DocServiceException(
					"An exception occured whyle saving the updated document.",
					e);
		} catch (Throwable e) {
				e.printStackTrace();
			throw new DocServiceException(
					"An exception occured whyle saving the updated document.",
					e);
		}
	}
	
	public boolean createAnnotationSet(String docID, String annotationSetName) 
	        throws DocServiceException {
		log.debug(".createAnnotationSet(docID=" + docID + ", asName="
          + annotationSetName + ") called");

    try {
      Lock l = lockManager._getLock(annotationSetName, docID);
      if(l != null) {
    	  log.debug("annotation  set is locked for writing by someone else");
    	  return false;
      }
      else {
    	  log.debug("annotation  set is NOT locked for writing by someone else");
      }
    }
    catch(Exception e) {
      e.printStackTrace();
      throw new DocServiceException(
              "An exception occurred checking lock status", e);
    }
    
	  ReentrantReadWriteLock lock = getReadWriteLock(docID);
	  lock.writeLock().lock();
	  try {
	    Document d;
	    try {
	      d = (Document) getLR(documentClassName, docID);
	      log.debug("documentName: "+ d.getName());
	    } catch (ResourceInstantiationException e) {
	        e.printStackTrace();
	      throw new DocServiceException(e.getClass().getName()
	          + " occured whyle getting document with docID=" + docID
	          + " for annSetName=" + annotationSetName + ". \n"
	          + e.getMessage(), e);
	    }
	    if(d.getAnnotationSetNames()!=null && d.getAnnotationSetNames().contains(annotationSetName)) {
	    	log.debug("annotation set already exists");
	        return false;
	    }
	    else {
	      log.debug("create AS with name: "+annotationSetName);	
	      // create the annotation set
	      AnnotationSet newSet = d.getAnnotations(annotationSetName);
	      log.debug("created newSet name: "+newSet.getName());
	      getDataStore().sync(d);
	      log.debug("ddddddddddddddddddddddddddddddd 5");
	      return true;
	    }
	  }
	  catch (PersistenceException e) {
        e.printStackTrace();
      return false;
    }
	  finally {
	    lock.writeLock().unlock();
	  }
	}

	public boolean deleteAnnotationSet(String taskID)
			throws DocServiceException {
		log.debug(".deleteAnnotationSet(taskID=" + taskID + ") called");
		try {
			Lock l = lockManager._getLock(
					Integer.valueOf(taskID).intValue());
			if(l == null) {
			  throw new DocServiceException("Invalid task ID " + taskID);
			}
			ReentrantReadWriteLock lock = getReadWriteLock(l.getDocId());
			lock.writeLock().lock();
			try {
  			Document d = (Document) getLR(documentClassName, l.getDocId());
  			AnnotationSet annSet = (l.getAnnSetName() == null) ? d
  					.getAnnotations() : d.getAnnotations(l.getAnnSetName());
  			if (annSet == null)
  				throw new DocServiceException("Invalid lock: '" + taskID
  						+ "'");
  			annSet.getDocument().removeAnnotationSet(annSet.getName());
  			getDataStore().sync(d);
  			return true;
			}
			finally {
			  lock.writeLock().unlock();
			}
		} catch (Exception e) {
				e.printStackTrace();
			throw new DocServiceException(
					"An exception occured whyle deleting the annotation set.",
					e);
		}
	}

	public void copyAnnotationSet(String docID, String sourceAnnotationSetName,
			String targetAnnotationSetName) throws DocServiceException {
		log.debug(".copyAnnotationSet(docID=" + docID
					+ ", sourceAnnotationSetName="
					+ sourceAnnotationSetName
					+ ", targetAnnotationSetName="
					+ targetAnnotationSetName + ")");
		
		ReentrantReadWriteLock lock = getReadWriteLock(docID);
		lock.writeLock().lock();
		try {
  		AnnotationSet sourceAS = getAnnotationSetLock(docID,
  				sourceAnnotationSetName, false).getAnnotationSet();
  		AnnotationSetAndTaskID astid = getAnnotationSetLock(docID,
  				targetAnnotationSetName, true);
  		AnnotationSet targetAS = astid.getAnnotationSet();
  		// do copy
  		targetAS.clear();
  		Iterator<Annotation> annItr = sourceAS.iterator();
  		while (annItr.hasNext()) {
  			Annotation ann = annItr.next();
  			targetAS.add(ann.getStartNode(), ann.getEndNode(), ann
  					.getType(), ann.getFeatures());
  		}
  		// release lock and save
  		try {
  			lockManager._releaseLock(
  					Integer.valueOf(astid.getTaskID()).intValue());
  			Document d = (Document) getLR(documentClassName, docID);
  			getDataStore().sync(d);
  		} catch (Exception e) {
  				e.printStackTrace();
  			throw new DocServiceException(e.getClass().getName()
  					+ " occured whyle synchronizing document with docID="
  					+ docID + ". \n" + e.getMessage(), e);
  		}
		}
		finally {
		  lock.writeLock().unlock();
		}
	}

	/**
	 * Annic while indexing documents, keeps a record of all possible annotation
	 * types and their features. These values are stored in the index and used
	 * in the ANNIC GUI. This method will return such a map where key is the
	 * annotation type and the value is a list of strings.
	 * 
	 * @return a map of features where key is (String) feature name and value is
	 *         (String) feature value
	 */
	public MapWrapper<String, String[]> getAnnotationTypesForAnnic() throws DocServiceException {
		log.debug(".getAnnotationTypesForAnnic() called");
		if (!(getDataStore() instanceof LuceneDataStoreImpl)) {
			throw new DocServiceException(
					"Underlying datastore doesn't support this operation.");
		}
		Searcher searcher = ((LuceneDataStoreImpl) getDataStore())
				.getSearcher();

		Map data = searcher.getAnnotationTypesMap();
		Map<String, String[]> toReturn = new HashMap<String, String[]>();
		if (data != null) {
			Iterator iter = data.keySet().iterator();
			while (iter.hasNext()) {
				String key = (String) iter.next();
				String values[] = new String[0];
				Object o = data.get(key);
				List<String> features = (List<String>) o;
				if (features != null) {
	        toReturn.put(key, features.toArray(new String[features.size()]));
				}
				else {
				  toReturn.put(key, new String[0]);
				}
			}
		}
		return MapWrapper.wrap(toReturn);
	}

	/**
	 * This method returns the indexed annotation set names
	 * 
	 * @return
	 * @throws DSProxyException
	 */
	public String[] getIndexedAnnotationSetNames() throws DocServiceException {
		log.debug(".getIndexedAnnotationSetNames() called");
		if (!(getDataStore() instanceof LuceneDataStoreImpl)) {
			throw new DocServiceException(
					"Underlying datastore doesn't support this operation.");
		}
		try {
			Searcher searcher = ((LuceneDataStoreImpl) getDataStore())
					.getSearcher();
			String[] data = searcher.getIndexedAnnotationSetNames();
			return data;
		} catch (SearchException se) {
			throw new DocServiceException(se);
		}
	}

	public String search(String query, MapWrapper<String, Object> parametersWrapper)
			throws DocServiceException {
	  Map<String, Object> parameters = MapWrapper.unwrap(parametersWrapper);
	  log.debug(".search(query=" + query + ") called");
		if (!(getDataStore() instanceof LuceneDataStoreImpl)) {
			throw new DocServiceException(
					"Underlying datastore doesn't support this operation.");
		}
		Searcher searcher = ((LuceneDataStoreImpl) getDataStore())
				.getSearcher();
		if (parameters == null)
			parameters = new HashMap<String, Object>();
		if (!parameters
				.containsKey(gate.creole.annic.Constants.INDEX_LOCATIONS)) {
			String indexLocation = new File(
					((URL) ((LuceneDataStoreImpl) getDataStore())
							.getIndexer()
							.getParameters()
							.get(
									gate.creole.annic.Constants.INDEX_LOCATION_URL))
							.getFile()).getAbsolutePath();
			ArrayList indexLocations = new ArrayList();
			indexLocations.add(indexLocation);
			parameters.put(gate.creole.annic.Constants.INDEX_LOCATIONS,
					indexLocations);
		}
		if (!parameters
				.containsKey(gate.creole.annic.Constants.CONTEXT_WINDOW)) {
			parameters.put(gate.creole.annic.Constants.CONTEXT_WINDOW,
					new Integer(4));
		}
		if (!parameters.containsKey(gate.creole.annic.Constants.CORPUS_ID)) {
			parameters.put(gate.creole.annic.Constants.CORPUS_ID, null);
		}

		if (!parameters
				.containsKey(gate.creole.annic.Constants.ANNOTATION_SET_ID)) {
			parameters.put(gate.creole.annic.Constants.ANNOTATION_SET_ID,
					null);
		}

		log.debug(".search(query=" + query + ") parameters:");
		log.debug(gate.creole.annic.Constants.CORPUS_ID + "='"
					+ parameters.get(gate.creole.annic.Constants.CORPUS_ID)
					+ "'");
		log.debug(gate.creole.annic.Constants.ANNOTATION_SET_ID
							+ "='"
							+ parameters
									.get(gate.creole.annic.Constants.ANNOTATION_SET_ID)
							+ "'");
		log.debug(gate.creole.annic.Constants.CONTEXT_WINDOW
							+ "='"
							+ parameters
									.get(gate.creole.annic.Constants.CONTEXT_WINDOW)
							+ "'");
		log.debug(gate.creole.annic.Constants.INDEX_LOCATIONS
							+ "='"
							+ parameters
									.get(gate.creole.annic.Constants.INDEX_LOCATIONS)
							+ "'");
		
		try {
			boolean success = searcher.search(query, parameters);
			if (success) {
				String sId = String.valueOf(searcherIDCounter++);
				searchers.put(sId, new SearcherEntry(searcher));
				log.debug(".search: returning searcherID=" + sId);
				
				return sId;
			} else {
				log.debug(".search: can't create searcher. Returning NULL");
				
				return null;
			}
		} catch (SearchException e) {
			e.printStackTrace();
			throw new DocServiceException(e);
		}
	}

	public byte[] getNextSearchResults(String searcherId, int numberOfRecords)
			throws DocServiceException {
		log.debug(".getNextSearchResults(searcherId=" + searcherId
					+ ", numOfRecords requested =" + numberOfRecords
					+ ") called");
		if (!searchers.containsKey(searcherId))
			throw new DocServiceException("Searcher doesn't exist. ID="
					+ searcherId);
		if (numberOfRecords < -1)
			throw new DocServiceException(
					"Number of records requested must be > -1");
		if (numberOfRecords == 0) {
			// refresh
			searchers.get(searcherId).refresh();
			return null;
		}
		try {
			Hit[] hits = searchers.get(searcherId).getSearcher().next(
					numberOfRecords);
			if (hits == null)
				return null;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			OutputStreamWriter osw = new OutputStreamWriter(baos, "UTF-8");
			osw.write(Parser.toXML(hits));
			osw.close();
			return baos.toByteArray();
		} catch (SearchException e) {
			e.printStackTrace();
			throw new DocServiceException(e);
		} catch (IOException e) {
		  e.printStackTrace();
      throw new DocServiceException(e);
    }
	}

	public boolean releaseLock(String taskID) {

			try {
				Lock l = lockManager._getLock(
						Integer.valueOf(taskID).intValue());
				log.debug(".releaseLock(taskID="
						+ taskID
						+ ") called. "
						+ ((l == null) ? "Non existing lock ID." : l
								.toString()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		
		try {
			return lockManager._releaseLock(
					Integer.valueOf(taskID).intValue());
		} catch (Exception e) {
				e.printStackTrace();
			return false;
		}
	}

	public boolean keepaliveLock(String taskID) {
		
			try {
				Lock l = lockManager._getLock(
						Integer.valueOf(taskID).intValue());
				/*
				 * System.out.println("DEBUG: " + this.getClass().getName() +
				 * ".keepaliveLock(taskID=" + taskID + ") called. " + ((l ==
				 * null) ? "Non existing lock ID." : l.toString()));
				 */
			} catch (Exception e) {
				e.printStackTrace();
			}
		
		try {
			return (lockManager._refreshLock(
					Integer.valueOf(taskID).intValue()) == null);
		} catch (Exception e) {
				e.printStackTrace();
			return false;
		}
	}

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
	public IAAResult calculateIAA(String[] docIDs, String[] asNames,
			String annotationType, String featureName, String algorithm)
			throws DocServiceException {
		// start with some sanity checks
		if (docIDs == null || docIDs.length < 1) {
			throw new DocServiceException(
					"At least one doc ID must be specified");
		}

		if (asNames == null || asNames.length < 2) {
			throw new DocServiceException("At least two annotation set names "
					+ "must be specified");
		}

		if (annotationType == null) {
			throw new DocServiceException("Annotation type must be specified");
		}

		// load the documents and annotation sets
		AnnotationSet[][] annotationSets = new AnnotationSet[docIDs.length][asNames.length];
		for (int indexDoc = 0; indexDoc < docIDs.length; indexDoc++) {
			Document doc = null;
			ReentrantReadWriteLock lock = getReadWriteLock(docIDs[indexDoc]);
			lock.readLock().lock();
			try {
				doc = (Document) getLR(documentClassName,
						docIDs[indexDoc]);
				for (int indexAS = 0; indexAS < asNames.length; indexAS++) {
					// take copies of the annotation sets (.get()) so we
					// don't have
					// to either hold the lock while processing or risk
					// someone else
					// changing the sets while we use them
					if (asNames[indexAS] == null
							|| asNames[indexAS].equals("")) {
						annotationSets[indexDoc][indexAS] = doc
								.getAnnotations().get();
					} else {
						annotationSets[indexDoc][indexAS] = doc
								.getAnnotations(asNames[indexAS]).get();
					}
				}
			} catch (ResourceInstantiationException rie) {
				throw new DocServiceException("Error loading document "
						+ docIDs[indexDoc], rie);
			}
			finally {
			  lock.readLock().unlock();
			}
		}

		String[] labelValues = null;
		if (featureName != null) {
			// using labels, so build label list
			labelValues = IaaCalculation.collectLabels(annotationSets,
					featureName).toArray(new String[0]);
		}

		if ("all-ways-f-measure".equals(algorithm)) {
			return calculateAllWaysFMeasureIAA(docIDs, asNames, annotationType,
					featureName, annotationSets, labelValues);
		} else if ("all-ways-kappa".equals(algorithm)) {
			return calculateAllWaysKappaIAA(docIDs, asNames, annotationType,
					featureName, annotationSets, labelValues);
		} else if ("pairwise-f-measure".equals(algorithm)) {
			return calculatePairwiseFMeasureIAA(docIDs, asNames,
					annotationType, featureName, annotationSets, labelValues);
		} else if ("pairwise-kappa".equals(algorithm)) {
			return calculatePairwiseKappaIAA(docIDs, asNames, annotationType,
					featureName, annotationSets, labelValues);
		} else {
			throw new DocServiceException("Unsupported IAA algorithm: "
					+ algorithm);
		}
	}

	/**
	 * Copy values from a GATE IAA FMeasure object into the object we return.
	 */
	private FMeasure getFMeasureValues(gate.iaaplugin.FMeasure source) {
		FMeasure fm = new FMeasure();
		fm.setCorrect(source.correct);
		fm.setPartiallyCorrect(source.partialCor);
		fm.setMissing(source.missing);
		fm.setSpurious(source.spurious);

		fm.setPrecision(source.precision);
		fm.setRecall(source.recall);
		fm.setF1(source.f1);

		fm.setPrecisionLenient(source.precisionLenient);
		fm.setRecallLenient(source.recallLenient);
		fm.setF1Lenient(source.f1Lenient);

		return fm;
	}

	/**
	 * Calculate the all-ways F-measure IAA score.
	 * 
	 * @param annotationSets
	 *            TODO
	 * 
	 * @see #calculateIAA
	 */
	private IAAResult calculateAllWaysFMeasureIAA(String[] docIDs,
			String[] asNames, String annotationType, String featureName,
			AnnotationSet[][] annotationSets, String[] labelValues)
			throws DocServiceException {
		// All ways f-measure takes the first annotation set as the key
		// and subsequent ones as responses
		AnnotationSet[] keyAnnotationSets = new AnnotationSet[annotationSets.length];
		for (int i = 0; i < annotationSets.length; i++) {
			keyAnnotationSets[i] = annotationSets[0][i];
		}

		AnnotationSet[][] responseAnnotationSets = new AnnotationSet[annotationSets.length][asNames.length - 1];
		for (int i = 0; i < annotationSets.length; i++) {
			for (int j = 1; j < asNames.length; j++) {
				responseAnnotationSets[i][j - 1] = annotationSets[i][j];
			}
		}

		IaaCalculation iaaCalc = null;
		if (featureName == null) {
			iaaCalc = new IaaCalculation(annotationType,
					responseAnnotationSets, 0);
		} else {
			iaaCalc = new IaaCalculation(annotationType, featureName,
					labelValues, responseAnnotationSets, 0);
		}

		// do the calculation
		iaaCalc.allwayIaaFmeasure(keyAnnotationSets);

		// construct results
		AllWaysFMeasureDetail detail = new AllWaysFMeasureDetail();
		FMeasure overallF = getFMeasureValues(iaaCalc.fMeasureOverall);
		detail.setOverallFMeasure(overallF);

		FMeasureDetailForAnnotator[] detailForAnnotators = new FMeasureDetailForAnnotator[asNames.length - 1];
		detail.setDetailForAnnotators(detailForAnnotators);
		for (int i = 0; i < detailForAnnotators.length; i++) {
			detailForAnnotators[i] = new FMeasureDetailForAnnotator();
			detailForAnnotators[i].setAnnotationSetName(asNames[i + 1]);
			detailForAnnotators[i]
					.setOverallFMeasure(getFMeasureValues(iaaCalc.fMeasuresPairwise[i]));
			if (featureName != null) {
				// using labels
				FMeasureDetailForLabel[] detailForLabels = new FMeasureDetailForLabel[labelValues.length];
				detailForAnnotators[i].setDetailForLabels(detailForLabels);
				for (int j = 0; j < detailForLabels.length; j++) {
					detailForLabels[j] = new FMeasureDetailForLabel();
					detailForLabels[j].setLabelValue(labelValues[j]);
					detailForLabels[j]
							.setFMeasure(getFMeasureValues(iaaCalc.fMeasuresPairwiseLabel[i][j]));
				}
			}
		}

		return new IAAResult(overallF.getF1(), labelValues, detail);
	}

	/**
	 * Calculate the all-ways kappa IAA score.
	 * 
	 * @param annotationSets
	 *            TODO
	 * 
	 * @see #calculateIAA
	 */
	private IAAResult calculateAllWaysKappaIAA(String[] docIDs,
			String[] asNames, String annotationType, String featureName,
			AnnotationSet[][] annotationSets, String[] labelValues)
			throws DocServiceException {
		IaaCalculation iaaCalc = null;
		if (featureName == null) {
			iaaCalc = new IaaCalculation(annotationType, annotationSets, 0);
		} else {
			iaaCalc = new IaaCalculation(annotationType, featureName,
					labelValues, annotationSets, 0);
		}

		// do the calculation
		iaaCalc.allwayIaaKappa();

		// construct results
		AllWaysKappaDetail detail = new AllWaysKappaDetail();
		detail
				.setOverallObservedAgreement(iaaCalc.contingencyOverall.observedAgreement);
		detail.setOverallKappaDF(iaaCalc.contingencyOverall.kappaDF);
		detail.setOverallKappaSC(iaaCalc.contingencyOverall.kappaSC);

		return new IAAResult(iaaCalc.contingencyOverall.kappaDF, labelValues,
				detail);
	}

	/**
	 * Calculate the pairwise F-measure IAA score.
	 * 
	 * @param annotationSets
	 *            TODO
	 * 
	 * @see #calculateIAA
	 */
	private IAAResult calculatePairwiseFMeasureIAA(String[] docIDs,
			String[] asNames, String annotationType, String featureName,
			AnnotationSet[][] annotationSets, String[] labelValues)
			throws DocServiceException {
		IaaCalculation iaaCalc = null;
		if (featureName == null) {
			iaaCalc = new IaaCalculation(annotationType, annotationSets, 0);
		} else {
			iaaCalc = new IaaCalculation(annotationType, featureName,
					labelValues, annotationSets, 0);
		}

		// do the calculation
		iaaCalc.pairwiseIaaFmeasure();

		// construct results
		PairwiseFMeasureDetail detail = new PairwiseFMeasureDetail();
		FMeasure overallF = getFMeasureValues(iaaCalc.fMeasureOverall);
		detail.setOverallFMeasure(overallF);

		FMeasureDetailForAnnotatorPairs[] detailForPairs = new FMeasureDetailForAnnotatorPairs[asNames.length];
		detail.setDetailForPairs(detailForPairs);
		int pairsIndex = 0;
		for (int ann1 = 0; ann1 < asNames.length; ann1++) {
			detailForPairs[ann1] = new FMeasureDetailForAnnotatorPairs();
			detailForPairs[ann1].setKeyAnnotationSetName(asNames[ann1]);
			FMeasureDetailForAnnotator[] detailForResponses = new FMeasureDetailForAnnotator[asNames.length
					- ann1 - 1];
			detailForPairs[ann1].setDetailForResponses(detailForResponses);
			for (int ann2 = ann1 + 1, responsesIndex = 0; ann2 < asNames.length; ann2++, responsesIndex++) {
				detailForResponses[responsesIndex] = new FMeasureDetailForAnnotator();
				detailForResponses[responsesIndex]
						.setAnnotationSetName(asNames[ann2]);
				detailForResponses[responsesIndex]
						.setOverallFMeasure(getFMeasureValues(iaaCalc.fMeasuresPairwise[pairsIndex]));

				if (featureName != null) {
					// using labels
					FMeasureDetailForLabel[] detailForLabels = new FMeasureDetailForLabel[labelValues.length];
					detailForResponses[responsesIndex]
							.setDetailForLabels(detailForLabels);
					for (int label = 0; label < detailForLabels.length; label++) {
						detailForLabels[label] = new FMeasureDetailForLabel();
						detailForLabels[label]
								.setLabelValue(labelValues[label]);
						detailForLabels[label]
								.setFMeasure(getFMeasureValues(iaaCalc.fMeasuresPairwiseLabel[pairsIndex][label]));
					}
				}
				pairsIndex++;
			}
		}

		return new IAAResult(overallF.getF1(), labelValues, detail);
	}

	/**
	 * Calculate the pairwise kappa IAA score.
	 * 
	 * @param annotationSets
	 *            TODO
	 * 
	 * @see #calculateIAA
	 */
	private IAAResult calculatePairwiseKappaIAA(String[] docIDs,
			String[] asNames, String annotationType, String featureName,
			AnnotationSet[][] annotationSets, String[] labelValues)
			throws DocServiceException {
		IaaCalculation iaaCalc = null;
		if (featureName == null) {
			iaaCalc = new IaaCalculation(annotationType, annotationSets, 0);
		} else {
			iaaCalc = new IaaCalculation(annotationType, featureName,
					labelValues, annotationSets, 0);
		}

		// do the calculation
		iaaCalc.pairwiseIaaKappa();

		// construct results
		PairwiseKappaDetail detail = new PairwiseKappaDetail();
		detail
				.setOverallObservedAgreement(iaaCalc.contingencyOverall.observedAgreement);
		detail.setOverallKappaCohen(iaaCalc.contingencyOverall.kappaCohen);
		detail.setOverallKappaPi(iaaCalc.contingencyOverall.kappaPi);

		KappaDetailForAnnotatorPairs[] detailForPairs = new KappaDetailForAnnotatorPairs[asNames.length];
		detail.setDetailForPairs(detailForPairs);
		int pairsIndex = 0;
		for (int ann1 = 0; ann1 < asNames.length; ann1++) {
			detailForPairs[ann1] = new KappaDetailForAnnotatorPairs();
			detailForPairs[ann1].setKeyAnnotationSetName(asNames[ann1]);
			KappaDetailForAnnotator[] detailForResponses = new KappaDetailForAnnotator[asNames.length
					- ann1 - 1];
			detailForPairs[ann1].setDetailForResponses(detailForResponses);
			for (int ann2 = ann1 + 1, responsesIndex = 0; ann2 < asNames.length; ann2++, responsesIndex++) {
				detailForResponses[responsesIndex] = new KappaDetailForAnnotator();
				detailForResponses[responsesIndex]
						.setAnnotationSetName(asNames[ann2]);
				detailForResponses[responsesIndex]
						.setObservedAgreement(iaaCalc.contingencyTables[pairsIndex].observedAgreement);
				detailForResponses[responsesIndex]
						.setKappaCohen(iaaCalc.contingencyTables[pairsIndex].kappaCohen);
				detailForResponses[responsesIndex]
						.setKappaPi(iaaCalc.contingencyTables[pairsIndex].kappaPi);
				detailForResponses[responsesIndex]
						.setConfusionMatrix(iaaCalc.contingencyTables[pairsIndex].confusionMatrix);

				float[] positiveAgreements = new float[iaaCalc.contingencyTables[pairsIndex].sAgreements.length];
				float[] negativeAgreements = new float[iaaCalc.contingencyTables[pairsIndex].sAgreements.length];
				for (int agreementsIndex = 0; agreementsIndex < positiveAgreements.length; agreementsIndex++) {
					positiveAgreements[agreementsIndex] = iaaCalc.contingencyTables[pairsIndex].sAgreements[agreementsIndex][0];
					negativeAgreements[agreementsIndex] = iaaCalc.contingencyTables[pairsIndex].sAgreements[agreementsIndex][1];
				}

				detailForResponses[responsesIndex]
						.setSpecificAgreementsPositive(positiveAgreements);
				detailForResponses[responsesIndex]
						.setSpecificAgreementsNegative(negativeAgreements);

				pairsIndex++;
			}
		}

		return new IAAResult(iaaCalc.contingencyOverall.kappaCohen,
				labelValues, detail);
	}
  
  /**
   * @see gate.creole.annic.lucene.StatsCalculator#freq(String, String, String, String, String)
   */
  public int freq(String corpusToSearchIn,
          String annotationSetToSearchIn, String annotationType,
          String featureName, String value) {

    URL indexLocationURL = (URL) ((LuceneDataStoreImpl) getDataStore())
      .getIndexer().getParameters().get(Constants.INDEX_LOCATION_URL);
    String indexLocation = new File(indexLocationURL.getFile()).getAbsolutePath();
    IndexSearcher indexSearcher;
    try { // open the IndexSearcher
      indexSearcher = new IndexSearcher(indexLocation);
    } catch (IOException e) {
      e.printStackTrace();
      return -1;
    }
    int result;
    try { // use and close the IndexSearcher
      result = StatsCalculator.freq(indexSearcher,
        corpusToSearchIn, annotationSetToSearchIn,
        annotationType, featureName, value);
      indexSearcher.close();
    }
    catch (IOException ioe) {
      ioe.printStackTrace();
      return -1;
    }
    catch(SearchException se) {
      se.printStackTrace();
      return -1;
    }
    return result;
  }


	/**
	 * Called by the JAX-RPC runtime system (e.g. AXIS) automaticaly when the
	 * instance is created.<br>
	 * Should be called after creation and before any usage of methods if
	 * created out of JAX-RPC runtime system.<br>
	 * </p>
	 * Performs initialisation of Gate and opens serial datastore. Creates empty
	 * serial datastore if it doesn't exists.<br>
	 * <ul>
	 * Takes following parameters from the application context or JVM
	 * properties:<br>
	 * <li>name: {@link #GATE_HOME_PARAMETER_NAME}, value: location of
	 * gate.home default value is defined by <webapp home>/WEB-INF or user.dir
	 * <li>name: {@link #DATASTORE_LOCATION_PARAMETER_NAME}, value: location
	 * of SerialDataStore
	 * </ul>
	 * </p>
	 */
	public void init() throws Exception {
	  if(properties != null) {
	    
			// DS location
			if (properties.getProperty(DATASTORE_LOCATION_PARAMETER_NAME) == null) {
				log.error("ERROR! Datastore location not defined! Docservice not initialized properly!");
				throw new DocServiceException("Property " + DATASTORE_LOCATION_PARAMETER_NAME + " missing");
			}
			
			// index location
			if (properties.getProperty(DATASTORE_INDEX_LOCATION_PARAMETER_NAME) == null) {
				log.error("ERROR! Datastore index location not defined! Docservice not initialized properly!");
        throw new DocServiceException("Property " + DATASTORE_INDEX_LOCATION_PARAMETER_NAME + " missing");
			}
		
			// base token annotation type
			if (properties.getProperty(BASE_TOKEN_ANNOTATION_TYPE_PARAMETER_NAME) == null) {
				properties.setProperty(BASE_TOKEN_ANNOTATION_TYPE_PARAMETER_NAME, BASE_TOKEN_ANNOTATION_TYPE_DEFAULT);
			}

			// base unit
			if (properties.getProperty(INDEX_UNIT_ANNOTATION_TYPE_PARAMETER_NAME) == null) {
				properties.setProperty(INDEX_UNIT_ANNOTATION_TYPE_PARAMETER_NAME, INDEX_UNIT_ANNOTATION_TYPE_DEFAULT);
			}

			// AS names to not index
      if (properties.getProperty(AS_NAMES_TO_EXCLUDE_PARAMETER_NAME) == null) {
        properties.setProperty(AS_NAMES_TO_EXCLUDE_PARAMETER_NAME, "");
      }

			// AS names to index
      if (properties.getProperty(AS_NAMES_TO_INCLUDE_PARAMETER_NAME) == null) {
        properties.setProperty(AS_NAMES_TO_INCLUDE_PARAMETER_NAME, "");
      }

			// features to exclude
      if (properties.getProperty(FEATURES_TO_EXCLUDE_PARAMETER_NAME) == null) {
        properties.setProperty(FEATURES_TO_EXCLUDE_PARAMETER_NAME, "SpaceToken;Split");
      }

      // features to include
      if (properties.getProperty(FEATURES_TO_INCLUDE_PARAMETER_NAME) == null) {
        properties.setProperty(FEATURES_TO_INCLUDE_PARAMETER_NAME, "");
      }

			// should we create tokens automatically
      if (properties.getProperty(CREATE_TOKENS_AUTOMATICALLY_PARAMETER_NAME) == null) {
        properties.setProperty(CREATE_TOKENS_AUTOMATICALLY_PARAMETER_NAME, "true");
      }

      // init() NO LONGER STARTS UP GATE.  This is done by Spring before creating the SerialDocService      

			if (searcherCleaner == null) {
				searcherCleaner = new SearcherCleaner();
				String timeout = properties.getProperty(SEARCHER_TIMEOUT);
				try {
					if (timeout != null && timeout.length() > 0)
						searcherCleaner.setTimeout(Long.valueOf(timeout)
								.longValue());
				} catch (NumberFormatException e) {
					log.error("Invalid format of parameter '"
							+ SEARCHER_TIMEOUT + "'. Value: '" + timeout
							+ "'");
					e.printStackTrace();
				}
				searcherCleaner.start();
				log.debug("SearcherCleaner initialized. Timeout="
						+ searcherCleaner.getTimeout());
			}

			if (memoryManager == null) {
				memoryManager = new MemoryManager();
				String timeout = properties.getProperty(CACHE_CLEANUP_THRESHOLD_PARAMETER_NAME);
				try {
					if (timeout != null && timeout.length() > 0)
						memoryManager.setCleanupTreshold(Double.valueOf(
								timeout).doubleValue());
				} catch (NumberFormatException e) {
					log.error("Invalid format of parameter '"
							+ CACHE_CLEANUP_THRESHOLD_PARAMETER_NAME
							+ "'. Value: '" + timeout + "'");
					e.printStackTrace();
				}
				memoryManager.start();
				log.debug("Memory manager initialized. Threshold="
						+ memoryManager.getCleanupTreshold());
			}
	  }
	  else {
	    throw new DocServiceException("Properties must be set before calling init()");
	  }

		// Create empty serial datastore if it doesn't exist
		File dsLocation = new File(new URL(properties
				.getProperty(DATASTORE_LOCATION_PARAMETER_NAME)).getFile());
		if (!dsLocation.exists()) {
			// Create empty datastore if it doesn't exists
			createDataStore();
		}
	}
	
	public void shutDown() throws Exception {
	  if(searcherCleaner != null) {
	    searcherCleaner.interrupt();
	  }
	  if(memoryManager != null) {
	    memoryManager.interrupt();
	  }
	  if(datastore != null) {
	    datastore.close();
	  }
	}


	protected class LRData {
		private LanguageResource lr;

		private long timeAccessed;

		public LRData(LanguageResource lr) {
			this.lr = lr;
			this.timeAccessed = System.currentTimeMillis();
		}

		public LanguageResource getLr() {
			this.timeAccessed = System.currentTimeMillis();
			return lr;
		}

		public long getTimeAccessed() {
			return timeAccessed;
		}

	}

	protected class AnnotationSetAndTaskID {
		private AnnotationSet annotationSet;

		private String taskID;

		public AnnotationSetAndTaskID(AnnotationSet annotationSet, String taskID) {
			this.annotationSet = annotationSet;
			this.taskID = taskID;
		}

		public AnnotationSet getAnnotationSet() {
			return annotationSet;
		}

		public String getTaskID() {
			return taskID;
		}

	}

	protected class MemoryManager extends Thread {
		private double cleanupTreshold = 0.9;

		public double getCleanupTreshold() {
			return cleanupTreshold;
		}

		public void setCleanupTreshold(double cleanupTreshold) {
			this.cleanupTreshold = cleanupTreshold;
		}

		public void run() {
			log.debug(": memory manager started. Cleanup treshold is: "
						+ cleanupTreshold);
			long lastLowMemoryReportTime = 0L;
			long lowMemoryReportPeriod = 300000L; // 5 min
			while (!interrupted()) {
				try {
					try {
					  sleep(1000);
					}
					catch(InterruptedException ie) {
					  break;
					}
					if (lrCache == null)
						continue;
					double used = Runtime.getRuntime().totalMemory()
							- Runtime.getRuntime().freeMemory();
					if ((used / Runtime.getRuntime().maxMemory()) < cleanupTreshold)
						continue;
					if (lrCache.size() == 0) {
						if ((System.currentTimeMillis() - lastLowMemoryReportTime) > lowMemoryReportPeriod) {
							log.debug("memory usage treshold ("
											+ cleanupTreshold
											+ ") exceeded. Used memory: "
											+ (Runtime.getRuntime()
													.totalMemory() - Runtime
													.getRuntime().freeMemory())
											+ " from total max memory: "
											+ Runtime.getRuntime().maxMemory()
											+ "\nBut there is no documents in cache. Available memory for Docservice is critically low."
											+ "\nMax JVM memory or memory usage treshold needs to be ajusted");
							lastLowMemoryReportTime = System
									.currentTimeMillis();
						}
						continue;
					} else {
						log.debug("memory usage treshold ("
											+ cleanupTreshold
											+ ") exceeded."
											+ " Used memory: "
											+ (Runtime.getRuntime()
													.totalMemory() - Runtime
													.getRuntime().freeMemory())
											+ " from total max memory: "
											+ Runtime.getRuntime().maxMemory()
											+ " Docs in cache: "
											+ ((lrCache == null) ? "0" : String
													.valueOf(lrCache.size()))
											+ " Will try to unload some docs from cache.");
						Object[] o = lrCache.entrySet().toArray();
						Arrays.sort(o, new Comparator() {
							public int compare(Object o1, Object o2) {
								long l1 = ((LRData) ((Map.Entry) o1)
										.getValue()).getTimeAccessed();
								long l2 = ((LRData) ((Map.Entry) o2)
										.getValue()).getTimeAccessed();
								if (l1 < l2)
									return -1;
								if (l1 > l2)
									return 1;
								return 0;
							}
						});
						for (int i = 0; i <= o.length / 4; i++) {
							String key = (String) ((Map.Entry) o[i])
									.getKey();
							// grab the write lock before freeing the LR, to be sure nobody else
							// is using it at the same time.
							ReentrantReadWriteLock lock = getReadWriteLock(key);
							lock.writeLock().lock();
							try {
								log.debug("unloading language resource ID='"
  												+ key + "' from cache.");
  							Factory.deleteResource(((LRData) lrCache
  									.get(key)).getLr());
  							lrCache.remove(key);
							}
							finally {
							  lock.writeLock().unlock();
							}
						}
						//System.gc();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	protected class SearcherEntry {
		private long timeAccessed = System.currentTimeMillis();

		private Searcher searcher;

		private SearcherEntry() {
		}

		public SearcherEntry(Searcher searcher) {
			super();
			this.searcher = searcher;
		}

		public Searcher getSearcher() {
			return searcher;
		}

		public long getTimeAccessed() {
			return timeAccessed;
		}

		public void refresh() {
			this.timeAccessed = System.currentTimeMillis();
		}
	}

	/**
	 * This class releases locks older than {@link #timeout}.
	 */
	protected class SearcherCleaner extends Thread {
		/** Amont of time after which a sercher considered as dead. */
		private long timeout = 60000L;

		public long getTimeout() {
			return timeout;
		}

		public void setTimeout(long timeout) {
			this.timeout = timeout;
		}

		public void run() {
			while (!interrupted()) {
				try {
					try {
					  sleep(10000);
					}
					catch(InterruptedException ie) {
					  break;
					}
					if (searchers == null)
						continue;
					synchronized (searchers) {
						long currTime = System.currentTimeMillis();
						Iterator<Map.Entry<String, SearcherEntry>> itr = searchers
								.entrySet().iterator();
						while (itr.hasNext()) {
							Map.Entry<String, SearcherEntry> e = itr.next();
							if ((currTime - e.getValue().getTimeAccessed()) > timeout) {
								log.debug("SerialDocService ["
												+ new Date().toString()
												+ "]: Searcher timeout exceeded ("
												+ timeout
												/ 1000
												+ " sec) Clearing dead searcher with ID="
												+ e.getKey());
								itr.remove();
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
