/*
 *  RemoteDocumentWrapper.java
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
package gleam.docservice.util;

import gate.AnnotationSet;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.corpora.DocumentImpl;
import gate.corpora.DocumentStaxUtils;
import gate.event.AnnotationEvent;
import gate.event.AnnotationListener;
import gate.event.AnnotationSetEvent;
import gate.event.AnnotationSetListener;
import gate.event.DocumentEvent;
import gate.event.DocumentListener;
import gleam.docservice.AnnotationSetHandle;
import gleam.docservice.DocServiceException;
import gleam.docservice.DocumentInfo;
import gleam.docservice.DocService;
import gleam.util.adapters.MapWrapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class RemoteDocumentWrapper {
	private static final boolean DEBUG = false;

	private static XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();

	private static XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();

	private Document document;

	private Map<String, ClientASLock> documentLocks;

	private Set<String> loadedASNames;

	private DocService docService;

	private boolean opened = false;

	private Set<RemoteDocumentListener> remoteDocumentListeners;

	private RemoteDocumentWrapper() {
		this.documentLocks = new HashMap<String, ClientASLock>();
		this.loadedASNames = new HashSet<String>();
		this.remoteDocumentListeners = new HashSet<RemoteDocumentListener>();
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				if (DEBUG)
					System.out.println("DEBUG: " + this.getClass().getName() + ". Document '" + document.getName() + "' ID '"
							+ document.getLRPersistenceId() + "'. Executing shutdown hook...");
				try {
					releaseLocks();
				} catch (Exception e) {
					// stack trace will be printed by releaselocks method
          //e.printStackTrace();
				}
			}
		});
	}

	public Document getDocument() {
		return this.document;
	}

	// public Map<String, ClientASLock> getDocumentLocks() {
	// return this.documentLocks;
	// }

	public void close() throws Exception {
		releaseLocks();
		if(document != null) Factory.deleteResource(document);
		document = null;
		documentLocks = null;
		loadedASNames = null;
		fireDocumentClosed();
	}

	private void releaseLocks() throws RemoteDocumentException {
		if (documentLocks == null || documentLocks.size() == 0) return;
		String s = "";
		for (Iterator<Entry<String, ClientASLock>> itr = documentLocks.entrySet().iterator(); itr.hasNext();) {
			Map.Entry<String, ClientASLock> entry = itr.next();
			ClientASLock l = entry.getValue();
			l.release();
			itr.remove();
		}
		if (s.length() > 0) {
      String ss = "There were exceptions while releasing locks for document. \nDocument name: '"
                 + document.getName() + "' \nID: '" + document.getLRPersistenceId() + "'\n\n" + s;
      throw new RemoteDocumentException(ss);
		}
	}

	/**
	 * This method loads a document from the DocService.
	 * 
	 * @param documentID
	 *          document persistent ID
	 * @param asNamesFilter
	 *          a set of annotation set names to load. If this set is null, all
	 *          annotation sets will be loaded. If this set is empty, document
	 *          will be loaded without annotation sets (but user will be able to
   *          create new ones). Null value in set of names corresponds to
   *          default annotation set.
	 * @param serialDocService
	 *          a DocService to load document from
	 * @param readOnly
	 *          if true, document will be loaded in read-only mode, without
	 *          locking data on the server
	 */
	public static RemoteDocumentWrapper loadDocument(String documentID, Set<String> asNamesFilter,
			DocService docService, boolean readOnly) throws RemoteException, RemoteDocumentException {
		RemoteDocumentWrapper rdw = new RemoteDocumentWrapper();
		rdw.docService = docService;
    DocumentInfo dInfo = docService.getDocInfo(documentID);
    if (dInfo == null) {
      //DocServiceException dse = new DocServiceException();
      //String ss = "Document with ID='" + documentID + "' wasn't found on the server.";
      //dse.setFaultReason(ss);
      //dse.setFaultString(ss);
      //throw dse;
      throw new RemoteDocumentException("Document with ID='" + documentID + "' wasn't found on the server.");
    }
		StringBuffer errText = new StringBuffer();
		try {
			String docContent = docService.getDocContent(documentID);
			FeatureMap documentFeatures = Factory.newFeatureMap();
			documentFeatures.putAll(MapWrapper.unwrap(docService.getDocumentFeatures(documentID)));
			FeatureMap fm = Factory.newFeatureMap();
			// MUST load without unpacking markup
			fm.put(Document.DOCUMENT_MARKUP_AWARE_PARAMETER_NAME, Boolean.FALSE);
			fm.put("stringContent", docContent);
			rdw.document = (gate.corpora.DocumentImpl) Factory.createResource("gate.corpora.DocumentImpl", fm,
					documentFeatures, dInfo.getDocumentName());
			rdw.document.setLRPersistenceId(dInfo.getDocumentID());
			String[] asNames;
			if (asNamesFilter == null) {
				asNames = docService.listAnnotationSets(documentID);
			} else if (asNamesFilter.size() == 0) {
				asNames = new String[0];
			} else {
				asNames = new String[asNamesFilter.size()];
				asNamesFilter.toArray(asNames);
			}
			for (int i = 0; i < asNames.length; i++) {
				rdw.loadAS(asNames[i], readOnly);
			}
			rdw.document.setLRPersistenceId(documentID);
			rdw.opened = true;
			return rdw;
		} catch (Exception e) {
			e.printStackTrace();
			errText.append(e.getMessage());
			try {
				rdw.releaseLocks();
			} catch (Exception e1) {
        errText.append("\n\n" + e1.getMessage());
			}
		}
    String ss = "Failed to load document. Name: "
      + ((dInfo == null) ? "unknown" : "'" + dInfo.getDocumentName() + "'") + " ID: "
      + ((dInfo == null) ? "unknown" : "'" + dInfo.getDocumentID() + "'") + " \nReason:\n"
      + errText;
    throw new RemoteDocumentException(ss);
	}

	/**
	 * Modification map:<br>
	 * There are 7 possible states for every annotation set of the document.<br>
	 * LocalAnnSet - LocalAnnSetLock - RemoteAnnSet<br>
	 * Here is action map for all states<br>
	 * 0 - means there is no ann set<br>
	 * 1 - means there is an ann set<br>
	 * ---------------------------<br>
	 * <p>
	 * 001<br>
	 * There are 2 possible scenario for this result:<br>
	 * 1) AS wasn't loaded. AS was created on the server by other client.<br>
	 * Action: if allowLoadingOtherAS=true LOAD AS, else DO NOTHING<br>
	 * 2) AS was loaded in read-only mode, then deleted on the client side.<br>
	 * Action: if allowLoadingOtherAS=true LOAD AS, else DO NOTHING (and remove
	 * this AS name from loadedASNames!)<br>
	 * </p>
	 * <p>
	 * 010<br>
	 * 1) AS was loaded in write mode. AS deleted on the client side. AS deleted
	 * or didn't created on the server due to software error.<br>
	 * Action: RELEASE LOCK<br>
	 * </p>
	 * <p>
	 * 011<br>
	 * 1) AS was loaded in write mode. AS deleted on the client side.<br>
	 * Action: DELETE AS on server<br>
	 * </p>
	 * <p>
	 * 100<br>
	 * There are 2 possible scenario for this result:<br>
	 * 1) AS wasn't loaded. AS was created on the client side.<br>
	 * Action: if annotationset is not empty, CREATE AS on the server and get AS
	 * lock.<br>
	 * 2) AS was loaded in read-only mode. AS deleted on the server by other
	 * client.<br>
	 * Action: DELETE AS on the client<br>
	 * </p>
	 * <p>
	 * 101<br>
	 * There are 2 possible scenario for this result:<br>
	 * 1) AS wasn't loaded. AS was created on the client side. AS with the same
	 * name exists on the server. Action: add an error to error list, DO NOTHING<br>
	 * 2) AS was loaded in read-only mode.<br>
	 * Action: if retakeReadOnlyASinRW=true try to reload AS in read mode, if AS
	 * locked or retakeReadOnlyASinRW=false just UPDATE AS on the client<br>
	 * </p>
	 * <p>
	 * 110<br>
	 * 1) AS was loaded in write mode. AS deleted on the server due to software
	 * error. Action: RELEASE LOCK, CREATE AS on the server<br>
	 * </p>
	 * <p>
	 * 111<br>
	 * 1) AS was loaded in write mode. AS exists on the server. Action: UPDATE AS
	 * on the server<br>
	 * </p>
	 * 
	 * @param allowLoadingOtherAS
	 *          if true, new annotation sets (added on the server since last
	 *          document load) will be loaded
	 * @param retakeReadOnlyASinRW
	 *          if true, will try to take read-only AS in read mode
	 * @throws RemoteException
	 * @throws DocServiceException
	 */
	public void sync(boolean allowLoadingOtherAS, boolean retakeReadOnlyASinRW) throws DocServiceException,
			RemoteException {
		if (!opened) throw new IllegalStateException("Remote document is closed!");
		// make sure that document text hasn't been changed
		checkDocContent();
		String errors = "";
		// ----------------------------------
		// PROCESS ANNOTATION SETS
		// ----------------------------------
		Set<String> localASNames = new HashSet<String>();
		if (this.document.getNamedAnnotationSets() != null) localASNames.addAll(this.document.getAnnotationSetNames());
		if (this.document.getAnnotations().size() > 0) localASNames.add(null);
		Set<String> remoteASNames = new HashSet<String>(Arrays.asList(this.docService
				.listAnnotationSets((String) this.document.getLRPersistenceId())));
		// collect all names in one place
		Set<String> allNames = new HashSet<String>();
		allNames.addAll(localASNames);
		allNames.addAll(remoteASNames);
		allNames.addAll(this.documentLocks.keySet());
		// building modofication map
		Map<String, Integer> modificationMap = new HashMap<String, Integer>();
		Iterator<String> itr = allNames.iterator();
		while (itr.hasNext()) {
			String name = itr.next();
			modificationMap.put(name, new Integer(((localASNames.contains(name)) ? 4 : 0)
					+ ((this.documentLocks.keySet().contains(name)) ? 2 : 0) + ((remoteASNames.contains(name)) ? 1 : 0)));
		}
		// processing modification map
		Iterator<Map.Entry<String, Integer>> mItr = modificationMap.entrySet().iterator();
		while (mItr.hasNext()) {
			Map.Entry<String, Integer> entry = mItr.next();
			String name = entry.getKey();
			Integer v = entry.getValue();
			switch (v) {
			case 1: // 001
				try {
					if (allowLoadingOtherAS) {
						loadAS(name, false);
					} else if (loadedASNames.contains(name)) {
						loadedASNames.remove(name);
					}
				} catch (XMLStreamException e) {
					e.printStackTrace();
					errors += "An error occured while parsing XML for annotation set '" + name + "'. Error: " + e.getMessage()
							+ "\r\n";
				} catch (Exception e) {
					e.printStackTrace();
					errors += "An error occured while loading annotation set '" + name + "'. Error: " + e.getMessage() + "\r\n";
				}
				break;
			case 2: // 010
				errors += "Internal error! Annotation set is deleted on the client and on the server.\nBut there is a lock for this annotation set '"
						+ name + "'.\r\nLock will be released.";
				try {
					documentLocks.get(name).release();
				} catch (Exception e) {
					e.printStackTrace();
					errors += "An error occured while releasing lock for annotation set '" + name + "'. "
							+ documentLocks.get(name) + "Error: " + e.getMessage() + "\r\n";
				}
				documentLocks.remove(name);
				break;
			case 3: // 011
				try {
					docService.deleteAnnotationSet(documentLocks.get(name).getTaskId());
				} catch (Exception e) {
					e.printStackTrace();
					errors += "An error occured while deleting annotation set '" + name + "'. Error: " + e.getMessage() + "\r\n";
				}
				try {
					documentLocks.get(name).release();
					documentLocks.remove(name);
				} catch (Exception e) {
					e.printStackTrace();
					errors += "An error occured while releasing lock for annotation set '" + name + "'. "
							+ documentLocks.get(name) + "Error: " + e.getMessage() + "\r\n";
				}
				break;
			case 4: // 100
				if (loadedASNames.contains(name)) {
					if (name != null) {
						document.removeAnnotationSet(name);
					} else {
						document.getAnnotations().clear();
					}
				} else {
					if (document.getAnnotations(name).size() > 0) {
						// create annotation set on the server if local annotation set is
						// not empty
						try {
							String taskId = docService.getAnnotationSetLock((String) document.getLRPersistenceId(), name);
							ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
							XMLStreamWriter xsw = xmlOutputFactory.createXMLStreamWriter(outputStream, "UTF-8");
							DocumentStaxUtils.writeAnnotationSet(document.getAnnotations(name), xsw, "");
							xsw.close();
							docService.setAnnotationSet(outputStream.toByteArray(), taskId, true);
							documentLocks.put(name, new ClientASLock(taskId, name, docService));
						} catch (Exception e) {
							e.printStackTrace();
							errors += "An error occured while creating annotation set '" + name + "'. " + "Error: " + e.getMessage();
						}
					}
				}
				break;
			case 5: // 101
				if (loadedASNames.contains(name)) {
					try {
						if (retakeReadOnlyASinRW) {
							loadAS(name, false);
						} else {
							loadAS(name, true);
						}
					} catch (XMLStreamException e) {
						e.printStackTrace();
						errors += "An error occured while parsing XML for annotation set '" + name + "'. Error: " + e.getMessage()
								+ "\r\n";
					} catch (Exception e) {
						e.printStackTrace();
						errors += "An error occured while loading annotation set '" + name + "'. Error: " + e.getMessage() + "\r\n";
					}
				} else {
					errors += "Internal error! The name of new local annotation set '" + name
							+ "' conflicts with existing annotation set name on the server." + "Annotation set is NOT created.\r\n";
				}
				break;
			case 6: // 110
				errors += "Internal error! There is a lock for annotation set '" + name
						+ "', but this annotation set has been deleted on the server." + "Will try to recreate.\r\n";
				try {
					documentLocks.get(name).release();
					documentLocks.remove(name);
				} catch (Exception e) {
					e.printStackTrace();
					errors += "An error occured while releasing lock for annotation set '" + name + "'. "
							+ documentLocks.get(name) + "Error: " + e.getMessage() + "\r\n";
				}
				try {
					String taskId = docService.getAnnotationSetLock((String) document.getLRPersistenceId(), name);
          ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
          XMLStreamWriter xsw = xmlOutputFactory.createXMLStreamWriter(outputStream, "UTF-8");
					DocumentStaxUtils.writeAnnotationSet(document.getAnnotations(name), xsw, "");
					xsw.close();
					docService.setAnnotationSet(outputStream.toByteArray(), taskId, true);
					documentLocks.put(name, new ClientASLock(taskId, name, docService));
				} catch (Exception e) {
					e.printStackTrace();
					errors += "An error occured while creating annotation set '" + name + "'. " + "Error: " + e.getMessage();
				}
				break;
			case 7: // 111
				try {
          ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
          XMLStreamWriter xsw = xmlOutputFactory.createXMLStreamWriter(outputStream, "UTF-8");
					DocumentStaxUtils.writeAnnotationSet(document.getAnnotations(name), xsw, "");
					xsw.close();
					docService.setAnnotationSet(outputStream.toByteArray(), documentLocks.get(name).getTaskId(), true);
				} catch (Exception e) {
					e.printStackTrace();
					errors += "An error occured while updating annotation set '" + name + "'. " + "Error: " + e.getMessage();
				}
				break;
			default:
				errors += "Internal error! Modification Map is corrupted. Bad entry: key='" + name + "' value=" + v + "\r\n";
				break;
			}
		}
		if (errors.length() > 0) {
			throw new DocServiceException(errors);
		} else {
			documentModified = false;
			fireDocumentSynchronized();
		}
	}

	private void checkDocContent() throws DocServiceException, RemoteException {
		if (!opened) throw new IllegalStateException("Remote document is closed!");
		String remoteDocText = this.docService.getDocContent((String) this.document.getLRPersistenceId());
		if (!remoteDocText.equals(this.document.getContent().toString())) {
			if (DEBUG) {
				System.out.println("DEBUG: " + RemoteDocumentWrapper.class.getName()
						+ ".checkDocContent(): local doc content: '"
						+ this.document.getContent().toString().replaceAll("\n", "/n\n").replaceAll("\r", "/r\r") + "'");
				System.out.println("DEBUG: " + RemoteDocumentWrapper.class.getName()
						+ ".checkDocContent(): remote doc content: '"
						+ remoteDocText.replaceAll("\n", "/n\n").replaceAll("\r", "/r\r") + "'");
			}
			throw new DocServiceException("Can't synchronize document. Document texts are different at the server and client sides.");
		}
	}

	private void loadAS(String name, boolean readOnly) throws IOException, XMLStreamException, RemoteDocumentException {
		if (DEBUG)
			System.out.println("DEBUG: " + RemoteDocumentWrapper.class.getName() + ".loadAS('" + name + "') called.");
		try {
      AnnotationSetHandle ash = docService.getAnnotationSet((String) document.getLRPersistenceId(), name, readOnly);
      byte[] s = ash.getData();
      if (!readOnly) {
      	if (ash.getTaskID() == null || ash.getTaskID().length() == 0) {
          String ss = "Invalid task ID for document's annotation set '" + name + "'. Task ID=" + ash.getTaskID()
          + ". Document ID=" + (String) document.getLRPersistenceId();
          throw new RemoteDocumentException(ss);
      	}
      	this.documentLocks.put(name, new ClientASLock(ash.getTaskID(), name, this.docService));
      }
      AnnotationSet as = this.document.getAnnotations(name);
      DocserviceUtil.setAnnotationSet(as, s);
      as.addAnnotationSetListener(annSetListener);
      loadedASNames.add(name);
    }
    catch(DocServiceException e) {
      throw new RemoteDocumentException("Error fetching annotation set from doc service", e);
    }
	}

	// ==============================================
	// watching for document changes
	// ==============================================
	private boolean documentModified = false;

	AnnotationListener annListener = new AnnotationListener() {
		public void annotationUpdated(AnnotationEvent arg0) {
			documentModified = true;
			fireDocumentChanged();
		}
	};

	AnnotationSetListener annSetListener = new AnnotationSetListener() {
		public void annotationAdded(final AnnotationSetEvent arg0) {
			documentModified = true;
			arg0.getAnnotation().addAnnotationListener(annListener);
			fireDocumentChanged();
		}

		public void annotationRemoved(AnnotationSetEvent arg0) {
			documentModified = true;
			arg0.getAnnotation().removeAnnotationListener(annListener);
			fireDocumentChanged();
		}
	};

	DocumentListener docListener = new DocumentListener() {
		public void annotationSetAdded(DocumentEvent arg0) {
			documentModified = true;
			document.getAnnotations(arg0.getAnnotationSetName()).addAnnotationSetListener(annSetListener);
			fireDocumentChanged();
		}

		public void annotationSetRemoved(DocumentEvent arg0) {
			documentModified = true;
			document.getAnnotations(arg0.getAnnotationSetName()).removeAnnotationSetListener(annSetListener);
			fireDocumentChanged();
		}

		public void contentEdited(DocumentEvent arg0) {
			documentModified = true;
			fireDocumentChanged();
		}
	};

	public boolean isDocumentModified() {
		return documentModified;
	}

	// external listeners
	public boolean addRemoteDocumentListener(RemoteDocumentListener rdl) {
		return remoteDocumentListeners.add(rdl);
	}

	public boolean removeRemoteDocumentListener(RemoteDocumentListener rdl) {
		return remoteDocumentListeners.remove(rdl);
	}

	public Set<RemoteDocumentListener> getRemoteDocumentListeners() {
		return Collections.unmodifiableSet(remoteDocumentListeners);
	}
	
	private void fireDocumentChanged() {
		Iterator<RemoteDocumentListener> itr = remoteDocumentListeners.iterator();
		while(itr.hasNext()) {
			itr.next().documentModified();
		}
	}

	private void fireDocumentClosed() {
		Iterator<RemoteDocumentListener> itr = remoteDocumentListeners.iterator();
		while(itr.hasNext()) {
			itr.next().documentClosed();
		}
	}

	private void fireDocumentSynchronized() {
		Iterator<RemoteDocumentListener> itr = remoteDocumentListeners.iterator();
		while(itr.hasNext()) {
			itr.next().documentSynchronized();
		}
	}
}
