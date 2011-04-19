/*
 *  AnnotatorTask.java
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
package gleam.annotatorgui;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.corpora.DocumentStaxUtils;
import gate.corpora.DocumentXmlUtils;
import gate.creole.ResourceInstantiationException;
import gate.creole.ontology.Ontology;
import gate.event.AnnotationEvent;
import gate.event.AnnotationListener;
import gate.event.AnnotationSetEvent;
import gate.event.AnnotationSetListener;
import gleam.annotatorgui.gui.MainFrame;
import gleam.docservice.AnnotationSetHandle;
import gleam.docservice.DocService;
import gleam.docservice.DocServiceException;
import gleam.executive.proxy.ExecutiveProxyException;
import gleam.docservice.util.DocserviceUtil;

import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Iterator;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class AnnotatorTask implements Constants {
	private static final boolean DEBUG = true;

	private ExecutiveConnection connection;

	private DocService docService;

	gleam.executive.proxy.AnnotatorTask task;

	private Document document;

	private Ontology ontology;

	private String taskId;

	private Thread notifier;

	private boolean asModified = false;

	private AnnotationSetListener asListener = new AnnotationSetChangeListener();;

	private AnnotationListener annListener = new AnnotationChangeListener();

	private static XMLOutputFactory outputFactory = XMLOutputFactory
			.newInstance();

	private AnnotatorTask() {
	}

	protected AnnotatorTask(ExecutiveConnection connection,
			gleam.executive.proxy.AnnotatorTask task)
			throws AnnotatorGUIExeption {
		super();
		try {
			this.connection = connection;
			this.task = task;
			URL url = task.getDocServiceLocation().toURL();
			this.docService = AnnotatorGUIUtils.getDocServiceStub(url);
			try {
				String docContent = this.docService.getDocContent(task
						.getDocumentID());
				FeatureMap fm = Factory.newFeatureMap();
				fm.put("stringContent", docContent);
				// don't unpack markup in the document content
				fm.put(Document.DOCUMENT_MARKUP_AWARE_PARAMETER_NAME, Boolean.FALSE);
				this.document = (gate.corpora.DocumentImpl) Factory
						.createResource(DOCUMENT_CLASS_NAME, fm);

				this.document.setName(task.getDocumentID());
				if (DEBUG)
					System.out.println("DEBUG: document name "
							+ this.document.getName());
				this.document.setLRPersistenceId(task.getDocumentID());

				String asName = getAnnotationSetName();
				if (DEBUG)
					System.out.println("DEBUG: AS name "
							+ asName);

				AnnotationSetHandle ash = this.docService.getAnnotationSet(task
						.getDocumentID(), asName, false);

				// although we know the user id, we will place all annotations
				// under the default annotation set for now
				DocserviceUtil.setAnnotationSet(this.document
						.getAnnotations(/* this.connection.getUserId() */), ash
						.getData());

				// lets go through each annotation and add the annotation
				// listener to it
				Iterator<Annotation> itr = this.document
						.getAnnotations(/* this.connection.getUserId() */)
						.iterator();

				while (itr.hasNext()) {
					Annotation ann = (Annotation) itr.next();
					if (DEBUG)
						System.out.println("DEBUG: annotation  " + ann.getId());
					ann.addAnnotationListener(annListener);
				}

				// finally add the annotation set listener
				this.document.getAnnotations(/* this.connection.getUserId() */)
						.addAnnotationSetListener(asListener);
				this.taskId = ash.getTaskID();
				if (DEBUG)
					System.out.println("DEBUG: task.id " + this.taskId);
				if (this.taskId != null) {
					this.notifier = createTaskNotifier(this.docService,
							this.taskId);
					this.notifier.start();
				}
			} catch (DocServiceException e) {
				throw new AnnotatorGUIExeption(
						"An error occured while getting document data from the document dervice at:\n"
								+ task.getDocServiceLocation() + "\n"
								+ "DocID=\"" + task.getDocumentID() + "\"\n\n"
								+ e.getMessage(), e);
			} catch (ResourceInstantiationException e) {
				throw new AnnotatorGUIExeption(
						"An error occured while creating local document.\n"
								+ "DocID=\"" + task.getDocumentID() + "\"\n\n"
								+ e.getMessage(), e);
			} catch (XMLStreamException e) {
				throw new AnnotatorGUIExeption(
						"An error occured while creating local annotation set.\n"
								+ "DocID=\"" + task.getDocumentID() + "\"\n\n"
								+ e.getMessage(), e);
			}
		} catch (MalformedURLException e) {
			throw new AnnotatorGUIExeption(
					"An error occured while connecting to document service at:\n"
							+ task.getDocServiceLocation() + "\n\n"
							+ e.getMessage(), e);
		}
	}

	public ExecutiveConnection getConnection() {
		return connection;
	}

	public DocService getDocService() {
		return docService;
	}

	public Document getDocument() {
		return document;
	}

	public gleam.executive.proxy.AnnotatorTask getTask() {
		return task;
	}

	public String getTaskId() {
		return taskId;
	}

	public boolean isAsModified() {
		return asModified;
	}

	public Ontology getOntology() {
		return ontology;
	}

	public boolean loadOntology() throws MalformedURLException,
			ResourceInstantiationException {
		URI ontologyLocation = task.getOntologyLocation();
		URI owlimServiceURL = task.getOwlimServiceURL();
		String owlimRepository = task.getOwlimRepositoryName();
		if (owlimServiceURL != null && owlimRepository != null) {
			ontology = AnnotatorGUI.getOntology(
					ontologyLocation != null ? ontologyLocation.toString()
							: null, null, owlimServiceURL.toString(),
					owlimRepository);
			return true;
		} else {
			return false;
		}
	}

	public String getAnnotationSetName() {
		String asName = task.getAnnotationSetName();
		if (asName == null) {
			if (DEBUG)
				System.out
						.println("DEBUG: Annotation set name in obtained Task IS NULL. "
								+ "Take userId "
								+ this.connection.getUserId()
								+ " as Annotation Set Name");
			asName = this.connection.getUserId();
		} else {
			if (DEBUG)
				System.out
						.println("DEBUG: "
								+ getClass().getName()
								+ "<init(ExecutiveConnection)>: getting named annotation set '"
								+ asName + "'");
		}
		return asName;
	}

	public void releaseLock() throws AnnotatorGUIExeption {
		try {
			if (taskId != null)
				docService.releaseLock(taskId);
		} catch (Exception e) {
			throw new AnnotatorGUIExeption(
					"An error occured while releasing lock at:\n"
							+ task.getDocServiceLocation() + "\n" + "DocID=\""
							+ task.getDocumentID() + "\"\n\n" + e.getMessage(),
					e);
		}
	}

	public void saveAnnotationSet(boolean keepLock) throws AnnotatorGUIExeption {
		// we get default annotation set from the document
		AnnotationSet annSet = document.getAnnotations(/* this.connection.getUserId() */);
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			XMLStreamWriter xsw = outputFactory.createXMLStreamWriter(baos, "UTF-8");
			xsw.writeStartDocument();
			DocumentStaxUtils.writeAnnotationSet(annSet, xsw, "");
			xsw.close();

			docService.setAnnotationSet(baos.toByteArray(), taskId, keepLock);
			if(!keepLock) {
				if (notifier != null) {
					notifier.interrupt();
					notifier = null;
				}
				taskId = null;
			}
			
			if(asModified) {
				this.connection.getExecutiveProxy().taskSaved(this.task.getTaskID());
			}
			
			this.asModified = false;
		} catch (Exception e) {
			throw new AnnotatorGUIExeption(
					"An error occured while saving annotation set '"
							+ this.connection.getUserId()
							+ "'.\nIn the document: " + task.getDocumentID()
							+ "\n\n" + e.getMessage(), e);
		}
	}

	public boolean finish() throws AnnotatorGUIExeption {
		try {
			saveAnnotationSet(false);
			this.connection.getExecutiveProxy().taskCompleted(this.task.getTaskID());

			System.out.println("Adding document feature with key \"safe.asname." + task.getAnnotationSetName()
					+ "\" and the user who annotated the document \"" + task.getPerformer()
					+ "\"");
			docService.setDocumentFeature(task.getDocumentID(), "safe.asname."+task.getAnnotationSetName(),
					task.getPerformer());
			return true;
		} catch (ExecutiveProxyException e) {
			throw new AnnotatorGUIExeption(
					"An error occured while finishing annotator task'"
							+ task.getTaskID() + "'." + "\n\n" + e.getMessage(),
					e);

		} catch (DocServiceException e) {
				throw new AnnotatorGUIExeption(
						"An error occured while finishing annotator task'"
								+ task.getTaskID() + "'." + "\n\n" + e.getMessage(),
						e);
		}
	}

	public boolean cancel() throws AnnotatorGUIExeption {
		try {
			//saveAnnotationSet(true);
			this.connection.getExecutiveProxy().taskCanceled(
					this.task.getTaskID());
			System.out.println("Cancel Task -> Delete AS name from task " + this.task.getTaskID());
			docService.deleteAnnotationSet(taskId);
			return true;
		} catch (ExecutiveProxyException e) {
			e.printStackTrace();
			throw new AnnotatorGUIExeption(
					"An error occured while canceling annotator task'"
							+ task.getTaskID() + "'." + "\n\n" + e.getMessage(),
					e);
		}
		catch (DocServiceException ex) {
			ex.printStackTrace();
			throw new AnnotatorGUIExeption(
					"An error occured while canceling annotator task'"
							+ task.getTaskID() + "'." + "\n\n" + ex.getMessage(),
					ex);
		}
		
	}

	public void cleanup() throws AnnotatorGUIExeption {
		if (this.notifier != null) {
			this.notifier.interrupt();
			this.notifier = null;
		}
		releaseLock();
		if (this.document != null) {
			this.document.cleanup();
		}
		if (this.ontology != null) {
			Factory.deleteResource(this.ontology);
			this.ontology = null;
		}
	}

	private static synchronized Thread createTaskNotifier(final DocService sds,
			final String docTaskId) {
		return new Thread() {
			public void run() {
				while (!interrupted()) {
					try {
						sleep(10000);
						sds.keepaliveLock(docTaskId);
					} catch (InterruptedException e) {
						break;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		};
	}

	protected void finalize() throws Throwable {
		if (notifier != null) {
			this.notifier.interrupt();
			this.notifier = null;
		}
		super.finalize();
	}

	class AnnotationSetChangeListener implements AnnotationSetListener {
		public void annotationAdded(final AnnotationSetEvent arg0) {
			arg0.getAnnotation().addAnnotationListener(annListener);
			asModified = true;
			MainFrame.getInstance().updateBottomStatus();
		}

		public void annotationRemoved(AnnotationSetEvent arg0) {
			asModified = true;
			MainFrame.getInstance().updateBottomStatus();
		}
	}

	class AnnotationChangeListener implements AnnotationListener {
		public void annotationUpdated(AnnotationEvent arg0) {
			asModified = true;
			MainFrame.getInstance().updateBottomStatus();
		}
	}
}
