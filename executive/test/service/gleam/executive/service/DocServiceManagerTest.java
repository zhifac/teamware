package gleam.executive.service;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import gate.Factory;
import gate.Gate;
import gleam.executive.model.Corpus;
import gate.Document;
import gate.creole.annic.Constants;
import gate.creole.annic.Indexer;
import gate.creole.annic.lucene.LuceneIndexer;
import gate.creole.annic.lucene.LuceneSearcher;
import gate.persist.LuceneDataStoreImpl;
import gleam.executive.service.dwr.DocServiceDetailManager;
import gleam.executive.service.dwr.impl.DocServiceDetailManagerImpl;
import gleam.executive.service.impl.DocServiceManagerImpl;
import gleam.executive.util.GATEUtil;
import gleam.docservice.LockManager;
import gleam.docservice.MapLockManager;
import gleam.docservice.SerialDocService;
import gleam.docservice.proxy.IAAAlgorithm;
import gleam.docservice.proxy.dummy.DummyDocServiceProxyFactory;
import gleam.docservice.proxy.iaa.PairwiseFMeasureIAAResult;
import gleam.docservice.proxy.impl.DocServiceProxyImpl;

/* TODO All constants, URLs, filenames keep in properties file */
public class DocServiceManagerTest extends BaseManagerTestCase {
 	private String dsURL = null;

	private DocServiceManager dsManager = null;

	private DocServiceDetailManager dsdManager = null;
	
	private SerialDocService docServiceImpl;
	
	private LockManager lockManager;

	private static String createdCorpusID = null;
	
	private static String createdBigCorpusID = null;

	private ResourceBundle rb;

	private static final String newCorpusName = "testMyGoldenCorpus";
	private static final String bigZipFileURL = "test/service/gleam/executive/service/resource/big.zip";
	private static final String bigCorpusName = "bigCorpus";
	private static final String testZipFileURL = "test/service/gleam/executive/service/resource/data.zip";
	private static final String testData1 = "test/service/gleam/executive/service/resource/testData1.xml";
	private static final String testData2 = "test/service/gleam/executive/service/resource/testData2.xml";
	private static final String testDataIAA = "test/service/gleam/executive/service/resource/ann.xml";
	private static final String testDataStore = "test/service/gleam/executive/service/testDS/LuceneDataStore/";
	private static final String testIndex = "test/service/gleam/executive/service/testDS/LuceneIndex/";

	public DocServiceManagerTest() {
	}

	/**
	 * @return Returns the createdCorpusID.
	 */
	public String getCreatedCorpusID() {
		return createdCorpusID;
	}

	/**
	 * @param createdCorpusID
	 *            The createdCorpusID to set.
	 */
	public void setCreatedCorpusID(String corpusID) {
		createdCorpusID = corpusID;
	}

	protected static boolean started = false;
	protected static boolean dirCreated = false;

	protected void setUp() throws Exception {
		if (!dirCreated) {
			dirCreated = true;
			try {
				File dsLocation = new File(testDataStore);
				if (dsLocation.exists()) {
					deleteDSDir(dsLocation);
				} else {
					dsLocation.mkdirs();
				}
				File dsIndex = new File(testIndex);
				if (dsIndex.exists()) {
					deleteDSDir(dsIndex);
				} else {
					dsIndex.mkdirs();
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.out
						.println("Failed to create test directories for DataStroe");
			}
		}
		super.setUp();
		String className = this.getClass().getName();
		try {
			rb = ResourceBundle.getBundle(className);
		} catch (MissingResourceException mre) {
			log.warn("No resource bundle found for: " + className);
		}
		
    if (!started) {
      started = true;
      // initialize GATE
      Gate.setGateHome(new File(rb.getString("gate.home")));
      Gate.init();
      
      // populate the initial DS
      LuceneDataStoreImpl ds = (LuceneDataStoreImpl) Factory
          .createDataStore("gate.persist.LuceneDataStoreImpl",
              new File(testDataStore).toURL().toString());

      Indexer indexer = new LuceneIndexer(new File(testIndex).toURL());
      Map parameters = new HashMap();
      parameters.put(Constants.INDEX_LOCATION_URL, new File(testIndex)
          .toURL());
      parameters.put(Constants.BASE_TOKEN_ANNOTATION_TYPE, "syllable");
      // parameters.put(Constants.INDEX_UNIT_ANNOTATION_TYPE, "");
      parameters.put(Constants.FEATURES_TO_EXCLUDE, new ArrayList());
      parameters.put(Constants.FEATURES_TO_INCLUDE, new ArrayList());
      List<String> setsToInclude = new ArrayList<String>();
      setsToInclude.add("Key");
      parameters.put(Constants.ANNOTATION_SETS_NAMES_TO_INCLUDE,
          setsToInclude);
      parameters.put(Constants.ANNOTATION_SETS_NAMES_TO_EXCLUDE,
          new ArrayList<String>());
      parameters.put(Constants.CREATE_TOKENS_AUTOMATICALLY, new Boolean(
          true));
      ds.setIndexer(indexer, parameters);
      ds.setSearcher(new LuceneSearcher());
      ds.open();
      Document testDoc1 = Factory
          .newDocument(new File(testData1).toURL());
      Document testDoc2 = Factory
          .newDocument(new File(testData2).toURL());
      Document testDocIAA = Factory.newDocument(new File(testDataIAA)
          .toURL());
      testDocIAA.setName("ann");
      gate.Corpus testCorpus = Factory.newCorpus("testIAACorpus");
      gate.Corpus persistCorpus = (gate.Corpus) ds
          .adopt(testCorpus, null);
      persistCorpus.add(testDoc1);
      persistCorpus.add(testDoc2);
      persistCorpus.add(testDocIAA);
      ds.sync(persistCorpus);
      persistCorpus.sync();
      ds.close();
    }
		
		docServiceImpl = new SerialDocService();
		Properties dsProps = new Properties();
		dsProps.setProperty(SerialDocService.DATASTORE_LOCATION_PARAMETER_NAME,
				new File(testDataStore).toURI().toString());
		dsProps.setProperty(
				SerialDocService.DATASTORE_INDEX_LOCATION_PARAMETER_NAME,
				new File(testIndex).toURI().toString());
		dsProps.setProperty(
				SerialDocService.BASE_TOKEN_ANNOTATION_TYPE_PARAMETER_NAME,
				"syllable");
		dsProps.setProperty(
				SerialDocService.INDEX_UNIT_ANNOTATION_TYPE_PARAMETER_NAME, "");
		dsProps.setProperty(
				SerialDocService.AS_NAMES_TO_INCLUDE_PARAMETER_NAME,
				Constants.DEFAULT_ANNOTATION_SET_NAME);
		dsProps.setProperty(
				SerialDocService.AS_NAMES_TO_EXCLUDE_PARAMETER_NAME, "");
//		dsProps.setProperty(SerialDocService.GATE_HOME_PARAMETER_NAME, rb
//				.getString("gate.home"));
		dsProps.setProperty(SerialDocService.DEBUG_PARAMETER_NAME, "true");
		//dsProps.setProperty(SerialDocService.DEBUG_DETAILS_PARAMETER_NAME, "true");


		docServiceImpl.setProperties(dsProps);
		lockManager = new MapLockManager();
		lockManager.init();
		docServiceImpl.setLockManager(lockManager);
		docServiceImpl.init();
		
		DocServiceProxyImpl dsProxy = new DocServiceProxyImpl(docServiceImpl);
		DummyDocServiceProxyFactory proxyFactory = new DummyDocServiceProxyFactory();
		proxyFactory.setProxy(dsProxy);
		dsURL = "local:///docservice";
		dsManager = new DocServiceManagerImpl(dsURL, dsURL, null, null, null, proxyFactory, null);
		dsManager.setDocServiceProxy(dsProxy);
		dsdManager = new DocServiceDetailManagerImpl();
		dsdManager.setDocServiceManager(dsManager);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		docServiceImpl.shutDown();
		lockManager.shutDown();
		dsManager = null;
		lockManager = null;
	}

	public void testListCorpora() throws Exception {
		log.debug("Enter testListCorpora .......");
		List<Corpus> corpora = dsManager.listCorpora();
		assertTrue(corpora != null);
		for (int i = 0; i < corpora.size(); i++) {
			Corpus corpus = corpora.get(i);
			System.out.println(corpus.getCorpusID() + "  "
					+ corpus.getCorpusName());
		}
		assertEquals(corpora.get(0).getCorpusName(), "testIAACorpus");
	}

	public void testCreateCorpus() throws Exception {
		log.debug("Enter testCreateCorups .......");
		String corpusID = dsManager.createCorpus(newCorpusName);
		this.setCreatedCorpusID(corpusID);
		assertTrue(createdCorpusID != null);
		log.debug("The new created corpus ID is " + createdCorpusID);
	}

	public void testCreateDocIntoCorpus() throws Exception {
		log.debug("Enter testCreateCorups .......");
		ZipFile zipFile = new ZipFile(new File(testZipFileURL));
		Enumeration entries = zipFile.entries();
		while (entries.hasMoreElements()) {
			ZipEntry ze = (ZipEntry) entries.nextElement();
			String path = ze.getName();
			if (isValidZipFile(ze)) {
				URL entryURL = null;
				if (path.charAt(0) != '/') {
					entryURL = new URL("jar:"
							+ new File(testZipFileURL).toURL() + "!/" + path);
				} else {
					entryURL = new URL("jar:"
							+ new File(testZipFileURL).toURL() + "!" + path);
				}
				System.out.println(entryURL);
				//Document doc = Factory.newDocument(entryURL, "UTF-8");
				byte[] docXml = GATEUtil.getDocumentXml(entryURL, GATEUtil.DEFAULT_ENCODING, GATEUtil.DEFAULT_MARKUP_AWARE);
				String docName = path.substring(path.lastIndexOf("/") + 1);
				dsManager.createDocIntoCorpus(docName, this
						.getCreatedCorpusID(), docXml, GATEUtil.DEFAULT_ENCODING);
			}
		}
	}

	public void testGetCorpusName() throws Exception {
		String corpusName = dsManager.getCorpusName(this.getCreatedCorpusID());
		assertEquals(corpusName, newCorpusName);
	}

	public void testListAllDocuments() throws Exception {
		List<gleam.executive.model.Document> documents = dsManager
				.listDocuments();
		assertTrue(documents != null);
	}

	public void testListDocumentsFromCorpus() throws Exception {
		List<gleam.executive.model.Document> documents = dsManager
				.listDocuments(this.getCreatedCorpusID());
		assertTrue(documents != null);
		// The following should be commented when running from night build
		System.out.println("Test List docuemnts from a specified corpus!");
		for (int i = 0; i < documents.size(); i++) {
			gleam.executive.model.Document document = documents.get(i);
			System.out.println(document.getDocumentID() + "  "
					+ document.getDocumentName());
		}
		assertEquals(documents.get(0).getDocumentName(), "testDocument1.xml");
	}

	public void testGetSearcherID() throws Exception {
		String searcherID = dsManager.getSearcherID("{noun}", this
				.getCreatedCorpusID(), "Key", 4);
		assertEquals(searcherID, "0");
	}

	public void testListAnnotationSetNames() throws Exception {
		List<gleam.executive.model.Document> documents = dsManager
				.listDocuments(this.getCreatedCorpusID());
		for (int i = 0; i < documents.size(); i++) {
			gleam.executive.model.Document document = documents.get(i);
			List list = dsManager.listAnnotationSetNames(document
					.getDocumentID());
			log.debug("list size: "+list.size());
			assertEquals(list.contains("Key"), true);
			assertEquals(list.contains("Original markups"), true);
			assertEquals(list.contains("Tok"), true);
			assertEquals(list.contains("Res"), true);
			//assertEquals(list.get(0), "Key");
			//assertEquals(list.get(1), "Original markups");
			//assertEquals(list.get(2), "Tok");
			//assertEquals(list.get(3), "Res");
		}
	}

	public void testDeleteAnnotationSet() throws Exception {
		List<gleam.executive.model.Document> documents = dsManager
				.listDocuments(this.getCreatedCorpusID());
		gleam.executive.model.Document document = documents.get(0);
		assertTrue(dsManager.deleteAnnotationSet(document.getDocumentID(),
				"Tok"));

	}

	public void testListSharedAnnotationTypes() throws Exception {
		List<gleam.executive.model.Document> documents = dsManager
				.listDocuments(this.getCreatedCorpusID());
		for (int i = 0; i < documents.size(); i++) {
			gleam.executive.model.Document document = documents.get(i);
			List list = dsdManager.listSharedAnnotationTypes(document
					.getDocumentID(), "Key", "Res");
			System.out.println("The number of shared annotation types is "
					+ list.size());
			assertEquals(list.get(0), "verb");
			assertEquals(list.get(1), "noun");
		}
	}

	public void testCaculateIAA() throws Exception {
		System.out.println();
		System.out.println();
		System.out.println("Entering testCaculateIAA.........");
		List<gleam.executive.model.Corpus> corpora = dsManager.listCorpora();
		String iaaCorpusID = null;
		for (int i = 0; i < corpora.size(); i++) {
			Corpus iaaCorpus = corpora.get(i);
			System.out.println(iaaCorpus.getCorpusName());
			if (iaaCorpus.getCorpusName().equals("testIAACorpus")) {
				iaaCorpusID = iaaCorpus.getCorpusID();
			}
		}
		System.out.println("IaaCorpusID is " + iaaCorpusID);
		List<gleam.executive.model.Document> documents = dsManager
				.listDocuments(iaaCorpusID);
		for (int i = 0; i < documents.size(); i++) {
			gleam.executive.model.Document document = documents.get(i);
			System.out.println("docName is " + document.getDocumentName());
			if (document.getDocumentName().equals("ann")) {
				String docIAAId = document.getDocumentID();
				String[] docIDs = new String[1];
				docIDs[0] = docIAAId;
				String[] asNames = new String[3];
				asNames[1] = "annotator1";
				asNames[0] = "annotator2";
				asNames[2] = "annotator3";
				String annotationType = "OPINION_SRC";
				String featureName = "type";
				PairwiseFMeasureIAAResult result = (PairwiseFMeasureIAAResult) dsManager
						.calculateIAA(docIDs, asNames, annotationType,
								featureName, IAAAlgorithm.PAIRWISE_F_MEASURE);
				String[] res = result.getLabelValues();
				System.out.println(result.getFMeasureForPair("annotator1",
						"annotator2").f1Lenient()
						+ " "
						+ result.getKeyASName("annotator1", "annotator2")
						+ " "
						+ result.getResponseASName("annotator1", "annotator2"));
				System.out.println(result.getFMeasureForPair("annotator1",
						"annotator3").f1()
						+ " "
						+ result.getKeyASName("annotator1", "annotator2")
						+ " "
						+ result.getResponseASName("annotator1", "annotator2"));
				System.out.println(result.getFMeasureForPair("annotator2",
						"annotator3").f1());
				System.out.println("The agreement is ++++++++++++++++"
						+ result.getAgreement());
				for (int j = 0; j < res.length; j++) {
					System.out.println(res[j]);
				}
			}

		}
		System.out.println("Finish testIaaCalculate.........");

	}

	public void testRemoveDocumentFromCorpus() throws Exception {
		List<gleam.executive.model.Document> documents = dsManager
				.listDocuments(this.getCreatedCorpusID());
		for (int i = 0; i < documents.size(); i++) {
			gleam.executive.model.Document document = documents.get(i);
			dsManager.removeDocumentFromCorpus(this.getCreatedCorpusID(),
					document.getDocumentID());
			log.debug("Document " + document.getDocumentName()
					+ " has been removed from the corpus "
					+ dsManager.getCorpusName(this.getCreatedCorpusID()));
		}

	}

	public void testDeleteCorpus() throws Exception {
		dsManager.deleteCorpus(this.getCreatedCorpusID());
		// assertNull(dsManager.getCorpusName(this.getCreatedCorpusID()));
	}

	public boolean isValidZipFile(ZipEntry ze) {
		String path = ze.getName();
		if (!ze.isDirectory() && path.endsWith(".xml")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Deletes all files and subdirectories under dir DataStore Home at the end
	 * of the test.
	 * 
	 * @param dir
	 * @return true if all deletions were successful. If a deletion fails, the
	 *         method stops attempting to delete and returns false
	 */
	public boolean deleteDSDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				System.out.println("$$$$$$$$$$ " + children[i]);
				boolean success = deleteDSDir(new File(dir, children[i]));
				System.out.println(success);
				if (!success) {
					return false;
				}
			}
		}

		// The directory is now empty so delete it
		return dir.delete();
	}
	
	public void testCreateBigCorpus() throws Exception {
		log.debug("Enter testCreateBigCorpus .......");
		String corpusID = dsManager.createCorpus(bigCorpusName);
		createdBigCorpusID = corpusID;
		assertTrue(createdBigCorpusID != null);
		log.debug("The created big corpus ID is " + createdBigCorpusID);
	}

	public void testCreateDocIntoBigCorpus() throws Exception {
		log.debug("Enter testCreateBigCorpus .......");
		ZipFile zipFile = new ZipFile(new File(bigZipFileURL));
		Enumeration entries = zipFile.entries();
		while (entries.hasMoreElements()) {
			ZipEntry ze = (ZipEntry) entries.nextElement();
			String path = ze.getName();
			if (isValidZipFile(ze)) {
				URL entryURL = null;
				if (path.charAt(0) != '/') {
					entryURL = new URL("jar:"
							+ new File(bigZipFileURL).toURL() + "!/" + path);
				} else {
					entryURL = new URL("jar:"
							+ new File(bigZipFileURL).toURL() + "!" + path);
				}

				//Document doc = Factory.newDocument(entryURL, "UTF-8");
				byte[] docXml = GATEUtil.getDocumentXml(entryURL, GATEUtil.DEFAULT_ENCODING, GATEUtil.DEFAULT_MARKUP_AWARE);
				String docName = path.substring(path.lastIndexOf("/") + 1);
				dsManager.createDocIntoCorpus(docName, createdBigCorpusID, docXml, GATEUtil.DEFAULT_ENCODING);
			}
		}
	}

	public void testDeleteBigCorpus() throws Exception {
		dsManager.deleteCorpus(createdBigCorpusID);
	}

}
