/*
 *  testLockManager.java
 *
 *  Copyright (c) 2006-2006, The University of Sheffield.
 *
 *  Andrey Shafirin, 23/May/2006
 *
 *  $Id$
 */

package safe.docservice.test;

import java.util.Properties;
import gleam.docservice.AnnotationSetHandle;
import gleam.docservice.DocServiceException;
import gleam.docservice.LockManager;
import gleam.docservice.MapLockManager;
import gleam.docservice.SerialDocService;
import junit.framework.TestCase;

/**
 * Unit test to test DocService directly. Made as separate test to be fast
 * because has been used many times.
 */
public class testLockManager extends TestCase {
	public static final boolean DEBUG = false;

	SerialDocService docservice;
	
	LockManager lockManager;

	public static void main(String[] args) {
		junit.textui.TestRunner.run(testLockManager.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
		this.docservice = new SerialDocService();
		Properties props = new Properties();
		this.lockManager = new MapLockManager();
		this.lockManager.init();
		this.docservice.setLockManager(lockManager);
		this.docservice.setProperties(props);
		this.docservice.init();
	}
	
	protected void tearDown() throws Exception {
	  this.docservice.shutDown();
	  this.lockManager.shutDown();
	}

	public void testLocking() throws Exception {
		if (DEBUG) System.out.println("testLocking...");
		String docID = this.docservice.createDoc("GateTestDocument", "a b c d e f\n".getBytes("UTF-8"), "UTF-8");
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
