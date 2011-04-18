/*
 *  testSaveDoc.java
 *
 *  Copyright (c) 2006-2006, The University of Sheffield.
 *
 *  Andrey Shafirin, 23/May/2006
 *
 *  $Id$
 */

package safe.docservice.test;

import gate.AnnotationSet;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.corpora.DocumentXmlUtils;
import gleam.docservice.LockManager;
import gleam.docservice.MapLockManager;
import gleam.docservice.SerialDocService;

import java.util.Properties;

import junit.framework.TestCase;

/**
 * Unit test to test DocService directly. Made as separate test to be fast
 * because has been used many times.
 */
public class testSaveDoc extends TestCase {
	public static final boolean DEBUG = false;

	SerialDocService docservice;
	
	LockManager lockManager;

	public static void main(String[] args) {
		junit.textui.TestRunner.run(testSaveDoc.class);
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

	public void testSaveDoc1() throws Exception {
		Gate.init();
		FeatureMap params = Factory.newFeatureMap();
		params.put("stringContent", "test test test");
		Document doc = (gate.corpora.DocumentImpl) Factory.createResource("gate.corpora.DocumentImpl", params);

		AnnotationSet annSet = doc.getAnnotations();
		FeatureMap fm = Factory.newFeatureMap();
		annSet.add(new Long(1), new Long(3), "Person", fm);
		fm = Factory.newFeatureMap();
		annSet.add(new Long(5), new Long(7), "Location", fm);

		AnnotationSet annSet1 = doc.getAnnotations("qwe");
		fm = Factory.newFeatureMap();
		annSet1.add(new Long(2), new Long(4), "Person1", fm);
		fm = Factory.newFeatureMap();
		annSet1.add(new Long(6), new Long(8), "Location1", fm);

		String docPersId = this.docservice.createDoc("GateTestDocument", doc.getContent().toString().getBytes("UTF-8"), "UTF-8");
		doc.setLRPersistenceId(docPersId);

		String lock = this.docservice.getAnnotationSet(docPersId, null, false).getTaskID();
		//String xml = (String) ClassRipper.invokeMethod(doc, "annotationSetToXml", new Object[] { annSet });
		StringBuffer sb = new StringBuffer();
		DocumentXmlUtils.annotationSetToXml(annSet, sb);
		
		String lock1 = this.docservice.getAnnotationSet(docPersId, "qwe", false).getTaskID();
		//String xml1 = (String) ClassRipper.invokeMethod(doc, "annotationSetToXml", new Object[] { annSet1 });
		StringBuffer sb1 = new StringBuffer();
		DocumentXmlUtils.annotationSetToXml(annSet, sb1);

		this.docservice.setAnnotationSet(sb.toString().getBytes("UTF-8"), lock, false);
		this.docservice.setAnnotationSet(sb1.toString().getBytes("UTF-8"), lock1, false);
	}
}

