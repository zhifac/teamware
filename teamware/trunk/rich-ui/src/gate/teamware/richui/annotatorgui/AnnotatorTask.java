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
package gate.teamware.richui.annotatorgui;

import gate.Annotation;
import gate.Document;
import gate.Factory;
import gate.creole.ResourceInstantiationException;
import gate.creole.ontology.Ontology;
import gate.event.AnnotationEvent;
import gate.event.AnnotationListener;
import gate.event.AnnotationSetEvent;
import gate.event.AnnotationSetListener;
import gate.teamware.richui.annotatorgui.gui.MainFrame;
import gate.teamware.richui.common.RichUIException;
import gate.teamware.richui.common.RichUIUtils;
import gleam.docservice.proxy.DSProxyException;
import gleam.docservice.proxy.DocServiceProxy;
import gleam.executive.proxy.ExecutiveProxyException;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.Iterator;

import javax.xml.stream.XMLOutputFactory;

public class AnnotatorTask implements Constants {
	private static final boolean DEBUG = true;

	private ExecutiveConnection connection;

	private DocServiceProxy dsProxy;

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
			throws RichUIException {
		super();
		this.connection = connection;
		this.task = task;
		URI uri = task.getDocServiceLocation();
		this.dsProxy = RichUIUtils.getDocServiceProxy(uri);
		try {
		  this.document = dsProxy.getDocumentContentOnly(task.getDocumentID());
			this.document.setName(task.getDocumentID());
			if (DEBUG)
				System.out.println("DEBUG: document name "
						+ this.document.getName());

			String asName = getAnnotationSetName();
			if (DEBUG)
				System.out.println("DEBUG: AS name "
						+ asName);

			this.dsProxy.getAnnotationSet(this.document, asName, null, false);

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
		} catch (DSProxyException e) {
			throw new RichUIException(
					"An error occured while getting document data from the document dervice at:\n"
							+ task.getDocServiceLocation() + "\n"
							+ "DocID=\"" + task.getDocumentID() + "\"\n\n"
							+ e.getMessage(), e);
		}
	}

	public ExecutiveConnection getConnection() {
		return connection;
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

	public void releaseLock() throws RichUIException {
		try {
			if (document != null)
				dsProxy.release(document);
		} catch (Exception e) {
			throw new RichUIException(
					"An error occured while releasing lock at:\n"
							+ task.getDocServiceLocation() + "\n" + "DocID=\""
							+ task.getDocumentID() + "\"\n\n" + e.getMessage(),
					e);
		}
	}

	public void saveAnnotationSet(boolean keepLock) throws RichUIException {
		try {
			dsProxy.saveAnnotationSet(document, null, getAnnotationSetName(), keepLock);
			if(asModified) {
				this.connection.getExecutiveProxy().taskSaved(this.task.getTaskID());
			}
			this.asModified = false;
		} catch (Exception e) {
			throw new RichUIException(
					"An error occured while saving annotation set '"
							+ this.connection.getUserId()
							+ "'.\nIn the document: " + task.getDocumentID()
							+ "\n\n" + e.getMessage(), e);
		}
	}

	public boolean finish() throws RichUIException {
		try {
			saveAnnotationSet(false);
			this.connection.getExecutiveProxy().taskCompleted(this.task.getTaskID());

			System.out.println("Adding document feature with key \"safe.asname." + task.getAnnotationSetName()
					+ "\" and the user who annotated the document \"" + task.getPerformer() + "\"");
			dsProxy.setDocumentFeature(task.getDocumentID(), "safe.asname."+task.getAnnotationSetName(),
					task.getPerformer());
			return true;
		} catch (ExecutiveProxyException e) {
			throw new RichUIException(
					"An error occured while finishing annotator task'"
							+ task.getTaskID() + "'." + "\n\n" + e.getMessage(),
					e);

		} catch (DSProxyException e) {
				throw new RichUIException(
						"An error occured while finishing annotator task'"
								+ task.getTaskID() + "'." + "\n\n" + e.getMessage(),
						e);
		}
	}

	public boolean cancel() throws RichUIException {
		try {
			//saveAnnotationSet(true);
			this.connection.getExecutiveProxy().taskCanceled(
					this.task.getTaskID());
			System.out.println("Cancel Task -> Delete AS name from task " + this.task.getTaskID());
			dsProxy.deleteAnnotationSet(document, getAnnotationSetName());
			return true;
		} catch (ExecutiveProxyException e) {
			e.printStackTrace();
			throw new RichUIException(
					"An error occured while canceling annotator task'"
							+ task.getTaskID() + "'." + "\n\n" + e.getMessage(),
					e);
		}
		catch (DSProxyException ex) {
			ex.printStackTrace();
			throw new RichUIException(
					"An error occured while canceling annotator task'"
							+ task.getTaskID() + "'." + "\n\n" + ex.getMessage(),
					ex);
		}
		
	}

	public void cleanup() throws RichUIException {
		if (this.notifier != null) {
			this.notifier.interrupt();
			this.notifier = null;
		}
		releaseLock();
		if (this.document != null) {
			Factory.deleteResource(this.document);
			this.document = null;
		}
		if (this.ontology != null) {
			Factory.deleteResource(this.ontology);
			this.ontology = null;
		}
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
