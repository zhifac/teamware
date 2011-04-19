/*
 *  testDocServiceWS.java
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
package safe.docservice.test;

import gleam.docservice.AnnotationSetHandle;
import gleam.docservice.CorpusInfo;
import gleam.docservice.DocService;
import gleam.docservice.DocumentInfo;
import gleam.docservice.DocServiceException;
import gleam.util.adapters.MapWrapper;
import gleam.util.cxf.CXFClientUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.cxf.jaxb.JAXBDataBinding;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

import junit.framework.TestCase;

/**
 * Temporary rubbish test for WS.<br>
 * </p>
 * {questions, comments, claims} -> Andrey Shafirin, Julien Nioche
 */
public class testDocServiceWS extends TestCase {
	public static final boolean DEBUG = true;

	public static final String DOC_SERVICE_URL = "http://localhost:8080/docservice/services/docservice";

	protected gleam.docservice.DocService docServiceHandle;

	/**
	 * To be able to run test as normal application
	 */
	public static void main(String[] args) {
		junit.textui.TestRunner.run(testDocServiceWS.class);
	}

	public static byte[] getResource(String name) throws IOException {
		URL u = testDocServiceWS.class.getResource(name);
		URLConnection uc = u.openConnection();
		InputStream is = uc.getInputStream();
		byte[] buf = new byte[uc.getContentLength()];
		is.read(buf);
		return buf;
	}

	protected void setUp() throws Exception {
		super.setUp();
		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		Map<String, Object> props = new HashMap<String, Object>();
    props.put("mtom-enabled", Boolean.TRUE);
    factory.setProperties(props);
    JAXBDataBinding db = new JAXBDataBinding();
    // add IAA classes to data binding
    db.setExtraClass(new Class[] {
        gleam.docservice.iaa.AllWaysFMeasureDetail.class,
        gleam.docservice.iaa.AllWaysKappaDetail.class,
        gleam.docservice.iaa.PairwiseFMeasureDetail.class,
        gleam.docservice.iaa.PairwiseKappaDetail.class});
    factory.setDataBinding(db);
    factory.setServiceClass(DocService.class);
    factory.setAddress(DOC_SERVICE_URL);
    
		this.docServiceHandle =  (DocService)factory.create();
		
		CXFClientUtils.setAllowChunking(this.docServiceHandle, false);
	}

	public void testListCorpora() throws RemoteException {
		CorpusInfo[] corpusInfos = this.docServiceHandle.listCorpora();
		assertNotNull(corpusInfos);
		if (corpusInfos.length > 0) {
			String firstCorpusID = corpusInfos[0].getCorpusID();
			DocumentInfo[] docInfos = this.docServiceHandle.listDocs(firstCorpusID);
			assertNotNull(docInfos);
		}
	}

	public void testGetFeatures() throws DocServiceException {
		CorpusInfo[] corpusInfos = this.docServiceHandle.listCorpora();
		assertNotNull(corpusInfos);
		if (corpusInfos.length > 0) {
			String firstCorpusID = corpusInfos[0].getCorpusID();
			Map features = MapWrapper.unwrap(this.docServiceHandle.getCorpusFeatures(firstCorpusID));
			if (DEBUG) {
				System.out.println("First corpus features: ");
				for (Iterator itr = features.entrySet().iterator(); itr.hasNext();) {
					Map.Entry e = (Entry) itr.next();
					System.out.println("  featureName: '" + e.getKey() + "'  featureValue: '" + e.getValue() + "'");
				}
			}
			DocumentInfo[] docInfos = this.docServiceHandle.listDocs();
			assertNotNull(docInfos);
			String firstDocID = docInfos[0].getDocumentID();
			features = MapWrapper.unwrap(this.docServiceHandle.getDocumentFeatures(firstDocID));
			if (DEBUG) {
				System.out.println("First document features: ");
				for (Iterator itr = features.entrySet().iterator(); itr.hasNext();) {
					Map.Entry e = (Entry) itr.next();
					System.out.println("  featureName: '" + e.getKey() + "'  featureValue: '" + e.getValue() + "'");
				}
			}
		}
	}

	public void testCreateAndDeleteCorpus() throws RemoteException {
		int corpusnum = this.docServiceHandle.listCorpora().length;
		String corpusID = this.docServiceHandle.createCorpus("test");
		int corpusnum2 = this.docServiceHandle.listCorpora().length;
		assertEquals(corpusnum, corpusnum2 - 1);
		this.docServiceHandle.deleteCorpus(corpusID);
		int corpusnum3 = this.docServiceHandle.listCorpora().length;
		assertEquals(corpusnum3, corpusnum);
	}

	public void testCreateAndDeleteDocument() throws Exception {
		String docPersistID = this.docServiceHandle.createDoc("GateTestDocument", getResource("GateDocument.xml"), "UTF-8");
		assertNotNull(docPersistID);
		assertTrue(this.docServiceHandle.deleteDoc(docPersistID));
	}

	public void testAddAndRemoveDocCorpus() throws Exception {
		String corpusID = this.docServiceHandle.createCorpus("test");
		int corpusLength = this.docServiceHandle.listDocs(corpusID).length;
		if (DEBUG)
			System.out.println("Created corpus: " + corpusID + " corpusInfo: "
					+ this.docServiceHandle.getCorpusInfo(corpusID));
		String docID = this.docServiceHandle.createDoc("GateTestDocument", getResource("GateDocument.xml"), "UTF-8");
		assertNotNull(docID);

		DocumentInfo[] docInfos;
		if (DEBUG) {
			System.out.println("Created doc: " + docID + " doc info = " + this.docServiceHandle.getDocInfo(docID));
			docInfos = this.docServiceHandle.listDocs();
			System.out.println("DataStore docs:");
			for (int i = 0; i < docInfos.length; i++) {
				System.out.println(docInfos[i]);
			}
			docInfos = this.docServiceHandle.listDocs(corpusID);
			System.out.println("Corpus docs:");
			for (int i = 0; i < docInfos.length; i++) {
				System.out.println(docInfos[i]);
			}
		}

		boolean bool = this.docServiceHandle.addDocumentToCorpus(corpusID, docID);

		if (DEBUG) {
			System.out.println("result of adding doc: " + bool);
			System.out.println("corpusInfo: " + this.docServiceHandle.getCorpusInfo(corpusID));
			docInfos = this.docServiceHandle.listDocs();
			System.out.println("DataStore docs:");
			for (int i = 0; i < docInfos.length; i++) {
				System.out.println(docInfos[i]);
			}
			docInfos = this.docServiceHandle.listDocs(corpusID);
			System.out.println("Corpus docs:");
			for (int i = 0; i < docInfos.length; i++) {
				System.out.println(docInfos[i]);
			}
		}

		assertEquals(corpusLength, this.docServiceHandle.listDocs(corpusID).length - 1);
		assertTrue(this.docServiceHandle.deleteDoc(docID));

		if (DEBUG) {
			docInfos = this.docServiceHandle.listDocs();
			System.out.println("DataStore docs:");
			for (int i = 0; i < docInfos.length; i++) {
				System.out.println(docInfos[i]);
			}
			docInfos = this.docServiceHandle.listDocs(corpusID);
			System.out.println("Corpus docs:");
			for (int i = 0; i < docInfos.length; i++) {
				System.out.println(docInfos[i]);
			}
		}

		assertEquals(corpusLength, this.docServiceHandle.listDocs(corpusID).length);
	}

	public void testGetAnnotationSet() throws Exception {
		String docID = null;
		try {
			if (DEBUG) System.out.println("testGetAnnotationSet...");
			docID = this.docServiceHandle.createDoc("GateTestDocument", getResource("GateDocument.xml"), "UTF-8");
			AnnotationSetHandle ash = this.docServiceHandle.getAnnotationSet(docID, null, true);
			assertNotNull(ash);
			assertNotNull(ash.getData());
			assertNull(ash.getTaskID());
		} finally {
			this.docServiceHandle.deleteDoc(docID);
		}
	}

	public void testModifyAnnotationSet() throws Exception {
		String docID = null;
		try {
			if (DEBUG) System.out.println("testModifyAnnotationSet...");
			docID = this.docServiceHandle.createDoc("GateTestDocument", getResource("GateDocument.xml"), "UTF-8");
			assertNotNull(docID);
			AnnotationSetHandle ash = this.docServiceHandle.getAnnotationSet(docID, null, false);
			assertNotNull(ash);
			assertNotNull(ash.getData());
			assertNotNull(ash.getTaskID());
			assertTrue(this.docServiceHandle.setAnnotationSet(ash.getData(), ash.getTaskID(), false));
		} finally {
			this.docServiceHandle.deleteDoc(docID);
		}
	}

	public void testLocking() throws Exception {
		if (DEBUG) System.out.println("testLocking...");
		String docID = this.docServiceHandle.createDoc("GateTestDocument", getResource("GateDocument.xml"), "UTF-8");
		assertNotNull(docID);
		AnnotationSetHandle ash = this.docServiceHandle.getAnnotationSet(docID, null, false);
		assertNotNull(ash);
		assertNotNull(ash.getData());
		assertNotNull(ash.getTaskID());
		try {
			this.docServiceHandle.getAnnotationSet(docID, null, false);
			// an exception must be thrown
			fail();
		} catch (Throwable e) {
			// DocServiceException exception must be thrown
			// this is a remote test where we don't have direct access to an
			// exception (seems it's wrapped in some another way, not like in
			// RMI or CORBA)
			// temporarily we will check DocServiceException by message
			assertTrue(e.getMessage().startsWith(DocServiceException.class.getName()));
		} finally {
			this.docServiceHandle.releaseLock(ash.getTaskID());
			this.docServiceHandle.deleteDoc(docID);
		}
	} /*
		 * public void test1() { try { System.out.println("");
		 * System.out.println("listCorpora()..."); System.out.println("---");
		 * String[] corpusList = docServiceHandle.listCorpora();
		 * System.out.println("corpusList (size:" + corpusList.length + ") = " +
		 * corpusList); if (corpusList.length > 0) { String firstCorpus =
		 * corpusList[0]; System.out.println("firstCorpus = " + firstCorpus);
		 * String[] docsids = docServiceHandle.listDocs(firstCorpus);
		 * System.out.println("docsids (size:" + docsids.length + ") = " + docsids); }
		 * 
		 * System.out.println("");
		 * System.out.println("createCorpus('testCorpus_WS')...");
		 * System.out.println("---"); String corpusID =
		 * docServiceHandle.createCorpus("testCorpus_WS");
		 * System.out.println("created corpus: '" + corpusID + "'");
		 * 
		 * System.out.println(""); System.out.println("listCorpora()...");
		 * System.out.println("---"); corpusList = docServiceHandle.listCorpora();
		 * System.out.println("corpusList (size:" + corpusList.length + ") = " +
		 * corpusList); if (corpusList.length > 0) { String firstCorpus =
		 * corpusList[0]; System.out.println("firstCorpus = " + firstCorpus);
		 * String[] docsids = docServiceHandle.listDocs(firstCorpus);
		 * System.out.println("docsids (size:" + docsids.length + ") = " + docsids); }
		 * 
		 * System.out.println("");
		 * System.out.println("deleteCorpus('testCorpus_WS')...");
		 * System.out.println("---"); docServiceHandle.deleteCorpus(corpusID);
		 * corpusList = docServiceHandle.listCorpora();
		 * System.out.println("corpusList (size:" + corpusList.length + ") = " +
		 * corpusList); } catch (Exception e) { e.printStackTrace(); } }
		 * 
		 * public void test2() { System.out.println("========= test 2 ==========");
		 * try { System.out.println(""); System.out.println("createDocument()...");
		 * URL u = this.getClass().getResource("GateDocument.xml"); URLConnection uc =
		 * u.openConnection(); InputStream is = uc.getInputStream(); byte[] buf =
		 * new byte[uc.getContentLength()]; is.read(buf); String s = new
		 * String(buf); // System.out.println(s); String docPersistID =
		 * docServiceHandle.createDoc(s); System.out.println("docPersistID = " +
		 * docPersistID); System.out.println("========================"); } catch
		 * (Exception e) { e.printStackTrace(); } }
		 */
}
