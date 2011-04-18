package gleam.executive.webapp.action;

import gate.Document;
import gate.Factory;
import gate.Gate;
import gate.creole.annic.Indexer;
import gate.creole.annic.lucene.LuceneIndexer;
import gate.creole.annic.lucene.LuceneSearcher;
import gate.creole.annic.Constants;
import gate.persist.LuceneDataStoreImpl;
import gleam.executive.model.Corpus;
import gleam.executive.service.DocServiceManager;
import gleam.executive.service.dwr.DocServiceDetailManager;
import gleam.executive.service.dwr.impl.DocServiceDetailManagerImpl;
import gleam.executive.service.impl.DocServiceManagerImpl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import gleam.docservice.LockManager;
import gleam.docservice.MapLockManager;
import gleam.docservice.SerialDocService;
import gleam.docservice.proxy.dummy.DummyDocServiceProxyFactory;
import gleam.docservice.proxy.impl.DocServiceProxyImpl;


public class DocServiceActionTest extends BaseStrutsTestCase {
  private String dsURL = null;

  private DocServiceManager dsManager = null;

  private DocServiceDetailManager dsdManager = null;

  private SerialDocService docServiceImpl;
  
  private LockManager lockManager;

  private ResourceBundle rb;
  
  private static final String newCorpusName = "testMyGoldenCorpus";

  private static final String testZipFileURL =  
                              "test/service/gleam/executive/service/resource/data.zip";
  private static final String testData1 =  
                              "test/service/gleam/executive/service/resource/testData1.xml";
  private static final String testData2 =  
                              "test/service/gleam/executive/service/resource/testData2.xml";
  private static final String testDataStore="test/service/gleam/executive/service/testDS/LuceneDataStore/";
  private static final String testIndex="test/service/gleam/executive/service/testDS/LuceneIndex/";

  protected static boolean started=false;
  protected static boolean dirCreated=false;
  public DocServiceActionTest(String name) {
    super(name);
  }

  protected void setUp() throws Exception {
   
    if(!dirCreated){
      dirCreated=true;
      try{
        File dsLocation = new File(testDataStore);
        if(dsLocation.exists()){
          deleteDSDir(dsLocation);
        }else{
          dsLocation.mkdirs();
        }
        File dsIndex = new File(testIndex);
        if(dsIndex.exists()){
          deleteDSDir(dsIndex);
        }else{
          dsIndex.mkdirs();
        }
      }catch(Exception e){
        e.printStackTrace();
        System.out.println("Failed to create test directories for DataStroe");
      }
    }
    super.setUp();
    getMockRequest().setUserRole("admin");
    String className = this.getClass().getName();
    try {
      rb = ResourceBundle.getBundle(className);
    }
    catch(MissingResourceException mre) {
      log.warn("No resource bundle found for: " + className);
    }
    if(!started){
      started=true;
      // initialize GATE
      Gate.setGateHome(new File(rb.getString("gate.home")));
      Gate.init();
      
      // populate the initial DS
      LuceneDataStoreImpl ds = (LuceneDataStoreImpl)
                                Factory.createDataStore("gate.persist.LuceneDataStoreImpl",new File(testDataStore).toURL().toString());
      
      Indexer indexer = new LuceneIndexer(new File(testIndex).toURL());
      Map parameters = new HashMap();  
      parameters.put(Constants.INDEX_LOCATION_URL, new File(testIndex).toURL());  
      parameters.put(Constants.BASE_TOKEN_ANNOTATION_TYPE, "syllable");  
      //parameters.put(Constants.INDEX_UNIT_ANNOTATION_TYPE, "");  
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
      //parameters.put(Constants.ANNOTATION_SET_NAME, "Res");
      ds.setIndexer(indexer, parameters);  
      ds.setSearcher(new LuceneSearcher());
      ds.open();
      Document testDoc1 = Factory.newDocument(new File(testData1).toURL());
      Document testDoc2 = Factory.newDocument(new File(testData2).toURL());
      gate.Corpus testCorpus = Factory.newCorpus("testCorpus");
      gate.Corpus persistCorpus=(gate.Corpus)ds.adopt(testCorpus,null);
      persistCorpus.add(testDoc1);
      persistCorpus.add(testDoc2);
      ds.sync(persistCorpus);
      persistCorpus.sync();
      ds.close();
    }

    docServiceImpl = new SerialDocService();
    Properties dsProps = new Properties();
    dsProps.setProperty(SerialDocService.DATASTORE_LOCATION_PARAMETER_NAME,new File(testDataStore).toURI().toString());
    dsProps.setProperty(SerialDocService.DATASTORE_INDEX_LOCATION_PARAMETER_NAME,new File(testIndex).toURI().toString());
    dsProps.setProperty(SerialDocService.BASE_TOKEN_ANNOTATION_TYPE_PARAMETER_NAME,"syllable");
    dsProps.setProperty(SerialDocService.INDEX_UNIT_ANNOTATION_TYPE_PARAMETER_NAME,"");
    dsProps.setProperty(SerialDocService.AS_NAMES_TO_INCLUDE_PARAMETER_NAME,Constants.DEFAULT_ANNOTATION_SET_NAME);
    
    docServiceImpl.setProperties(dsProps);
    lockManager = new MapLockManager();
    lockManager.init();
    docServiceImpl.setLockManager(lockManager);
    docServiceImpl.init();
    
    DocServiceProxyImpl dsProxy = new DocServiceProxyImpl(docServiceImpl);
    DummyDocServiceProxyFactory proxyFactory = new DummyDocServiceProxyFactory();
    proxyFactory.setProxy(dsProxy);
    dsURL="local:///docservice";
    dsManager = new DocServiceManagerImpl(dsURL, dsURL, null, null,null, proxyFactory, null);
    dsManager.setDocServiceProxy(dsProxy);
    dsdManager = new DocServiceDetailManagerImpl();
    dsdManager.setDocServiceManager(dsManager);
    //((ConfigurableBeanFactory)ctx.getAutowireCapableBeanFactory()).registerSingleton("docServiceManager", dsManager);
  }
  
  public void tearDown() throws Exception {
    super.tearDown();
    docServiceImpl.shutDown();
    lockManager.shutDown();
    dsManager = null;
    dsdManager = null;
    lockManager = null;
  }
  
/*  
  public void testEnterCorporaPage() throws Exception {
    setRequestPathInfo("/corpora");
    addRequestParameter("method", "search");
    actionPerform();
    verifyForward("mainMenu");
    assertTrue(getRequest().getAttribute(gleam.executive.Constants.CORPUS_LIST) == null);
  }
  */
  
  public void testEnterCreateCorpusPage() throws Exception {
    setRequestPathInfo("/addCorpus");
    addRequestParameter("method", "Add");
    actionPerform();
    verifyForward("edit");
  }

  public void testEnterDocumentListPage() throws Exception {
    setRequestPathInfo("/documentsInCorpus");
    addRequestParameter("method", "search");
    List<Corpus> list = dsManager.listCorpora();
    Iterator<Corpus> it = list.iterator();
    while(it.hasNext()) {
      Corpus corpus = it.next();
      String corpusID = corpus.getCorpusID();
      if(corpusID.startsWith("myTestCorpus")) {
        addRequestParameter("corpusID", corpusID);
        actionPerform();
        verifyForward("list");
        assertTrue(getRequest().getAttribute(gleam.executive.Constants.DOCUMENT_LIST) != null);
        verifyNoActionErrors();
      }
    }
  }

  public void testEnterAddDocumentsPage() throws Exception {
    setRequestPathInfo("/selectZipFile");
    List<Corpus> list = dsManager.listCorpora();
    Iterator<Corpus> it = list.iterator();
    while(it.hasNext()) {
      Corpus corpus = it.next();
      String corpusID = corpus.getCorpusID();
      if(corpusID.startsWith("myTestCorpus")) {
        addRequestParameter("corpusID", corpusID);
        actionPerform();
        verifyNoActionErrors();
      }
    }
  }

  public void testDeleteCorpus() throws Exception {
    setRequestPathInfo("/corpora");
    addRequestParameter("method", "delete");
    List<Corpus> list = dsManager.listCorpora();
    Iterator<Corpus> it = list.iterator();
    while(it.hasNext()) {
      Corpus corpus = it.next();
      String corpusID = corpus.getCorpusID();
      if(corpusID.startsWith("myTestCorpus")) {
        addRequestParameter("corpusID", corpusID);
        actionPerform();
        verifyForward("search");
        verifyNoActionErrors();
      }
    }
  }
  
  public boolean deleteDSDir(File dir) {
    if (dir.isDirectory()) {
        String[] children = dir.list();
        for (int i=0; i<children.length; i++) {
            System.out.println("$$$$$$$$$$ "+children[i]);
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
}
