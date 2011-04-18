/*
 *  TestDocserviceAPI.java
 *
 *  Copyright (c) 2006-2006, The University of Sheffield.
 *
 *  Andrey Shafirin, 25/Apr/2006
 *
 *  $Id$
 */

package safe.docservice.test;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

import gleam.docservice.AnnotationSetHandle;
import gleam.docservice.CorpusInfo;
import gleam.docservice.DocServiceException;
import gleam.docservice.DocumentInfo;
import gleam.docservice.LockManager;
import gleam.docservice.MapLockManager;
import gleam.docservice.SerialDocService;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

// We gone use only SerialDocService methods and avoid usage of gate.*
// Web Service clients should be independent from gate classes.
// import gate.*

/**
 * Unit tests to test DocService directly.
 */
public class TestDocserviceAPI extends TestCase {
	public static final boolean DEBUG = false;

	SerialDocService docservice;
	
	LockManager lockManager;

  /** Test suite routine for the test runner */
  public static Test suite() {
    return new TestSuite(TestDocserviceAPI.class);
  }

  public static void main(String[] args) {
		junit.textui.TestRunner.run(TestDocserviceAPI.class);
	}

	public static byte[] getResource(String name) throws IOException {
		URL u = TestDocserviceAPI.class.getClassLoader().getSystemResource(name);
		URLConnection uc = u.openConnection();
		InputStream is = uc.getInputStream();
		byte[] buf = new byte[uc.getContentLength()];
		is.read(buf);
		return buf;
	}

	protected void setUp() throws Exception {
		super.setUp();
		this.docservice = new SerialDocService();
		Properties props = new Properties();
		props.load(this.getClass().getClassLoader().getSystemResourceAsStream("test.properties"));
    //props.list(System.out);
		this.lockManager = new MapLockManager();
		this.lockManager.init();
		this.docservice.setLockManager(this.lockManager);
    this.docservice.setProperties(props);
    this.docservice.init();
	}
	
	protected void tearDown() throws Exception {
	  this.docservice.shutDown();
	  this.lockManager.shutDown();
	}

	public void testListCorpora() {
		CorpusInfo[] corpusInfos = this.docservice.listCorpora();
		assertNotNull(corpusInfos);
		if (corpusInfos.length > 0) {
			String firstCorpus = corpusInfos[0].getCorpusID();
			DocumentInfo[] docInfos = this.docservice.listDocs(firstCorpus);
			assertNotNull(docInfos);
		}
	}

	public void testCreateAndDeleteCorpus() {
		int corpusnum = this.docservice.listCorpora().length;
		String corpusID = this.docservice.createCorpus("test");
		int corpusnum2 = this.docservice.listCorpora().length;
		assertEquals(corpusnum, corpusnum2 - 1);
		this.docservice.deleteCorpus(corpusID);
		int corpusnum3 = this.docservice.listCorpora().length;
		assertEquals(corpusnum3, corpusnum);
	}

	public void testCreateAndDeleteDocument() throws Exception {
		String docPersistID = this.docservice.createDoc("GateTestDocument",
        TestDocservice.getFileContent(new URL(System.getProperty("test.doc.url"))), "UTF-8");
		assertNotNull(docPersistID);
		assertTrue(this.docservice.deleteDoc(docPersistID));
	}

	public void testAddAndRemoveDocCorpus() throws Exception {
		String corpusID = this.docservice.createCorpus("test");
		int corpusLength = this.docservice.listDocs(corpusID).length;
		if (DEBUG)
			System.out.println("Created corpus: " + corpusID + " corpusInfo: " + this.docservice.getCorpusInfo(corpusID));
		String docID = this.docservice.createDoc("GateTestDocument",
        TestDocservice.getFileContent(new URL(System.getProperty("test.doc.url"))), "UTF-8");
		assertNotNull(docID);

		DocumentInfo[] docInfos;
		if (DEBUG) {
			System.out.println("Created doc: " + docID + " doc info = " + this.docservice.getDocInfo(docID));
			docInfos = this.docservice.listDocs();
			System.out.println("DataStore docs:");
			for (int i = 0; i < docInfos.length; i++) {
				System.out.println(docInfos[i]);
			}
			docInfos = this.docservice.listDocs(corpusID);
			System.out.println("Corpus docs:");
			for (int i = 0; i < docInfos.length; i++) {
				System.out.println(docInfos[i]);
			}
		}

		boolean bool = this.docservice.addDocumentToCorpus(corpusID, docID);

		if (DEBUG) {
			System.out.println("After add doc: " + bool);
			System.out.println("corpusInfo: " + this.docservice.getCorpusInfo(corpusID));
			docInfos = this.docservice.listDocs();
			System.out.println("DataStore docs:");
			for (int i = 0; i < docInfos.length; i++) {
				System.out.println(docInfos[i]);
			}
			docInfos = this.docservice.listDocs(corpusID);
			System.out.println("Corpus docs:");
			for (int i = 0; i < docInfos.length; i++) {
				System.out.println(docInfos[i]);
			}
		}

		assertEquals(corpusLength, this.docservice.listDocs(corpusID).length - 1);
		assertTrue(this.docservice.deleteDoc(docID));

		if (DEBUG) {
			System.out.println("After delete doc: " + bool);
			docInfos = this.docservice.listDocs();
			System.out.println("DataStore docs:");
			for (int i = 0; i < docInfos.length; i++) {
				System.out.println(docInfos[i]);
			}
			docInfos = this.docservice.listDocs(corpusID);
			System.out.println("Corpus docs:");
			for (int i = 0; i < docInfos.length; i++) {
				System.out.println(docInfos[i]);
			}
		}
		assertEquals(corpusLength, this.docservice.listDocs(corpusID).length);
	}

	public void testGetAnnotationSet() throws Exception {
		String docID = null;
		AnnotationSetHandle ash = null;
		try {
			if (DEBUG) System.out.println("testGetAnnotationSet...");
			docID = this.docservice.createDoc("GateTestDocument",
		        TestDocservice.getFileContent(new URL(System.getProperty("test.doc.url"))), "UTF-8");
			ash = this.docservice.getAnnotationSet(docID, null, true);
			assertNotNull(ash);
			assertNotNull(ash.getData());
			assertNull(ash.getTaskID());
		} finally {
			if(ash != null)
				this.docservice.releaseLock(ash.getTaskID());
			this.docservice.deleteDoc(docID);
		}
	}

	public void testModifyAnnotationSet() throws Exception {
		String docID = null;
		AnnotationSetHandle ash = null;
		try {
			if (DEBUG) System.out.println("testModifyAnnotationSet...");
			docID = this.docservice.createDoc("GateTestDocument",
          TestDocservice.getFileContent(new URL(System.getProperty("test.doc.url"))), "UTF-8");
			assertNotNull(docID);
			ash = this.docservice.getAnnotationSet(docID, null, false);
			assertNotNull(ash);
			assertNotNull(ash.getData());
			assertNotNull(ash.getTaskID());
			assertTrue(this.docservice.setAnnotationSet(ash.getData(), ash.getTaskID(), false));
			
		} finally {
			if(ash != null)
				this.docservice.releaseLock(ash.getTaskID());
			this.docservice.deleteDoc(docID);
		}
	}

	public void testLocking() throws Exception {
		if (DEBUG) System.out.println("testLocking...");
		String docID = this.docservice.createDoc("GateTestDocument",
        TestDocservice.getFileContent(new URL(System.getProperty("test.doc.url"))), "UTF-8");
		assertNotNull(docID);
		AnnotationSetHandle ash = this.docservice.getAnnotationSet(docID, null, false);
		assertNotNull(ash);
		assertNotNull(ash.getData());
		assertNotNull(ash.getTaskID());
		try {
			this.docservice.getAnnotationSet(docID, null, false);
			// an exception must be thrown
			fail();
		} catch (Throwable e) {
			// DocServiceException exception must be thrown
			assertEquals(e.getClass(), DocServiceException.class);
		} finally {
			this.docservice.releaseLock(ash.getTaskID());
			this.docservice.deleteDoc(docID);
		}
	}
}
