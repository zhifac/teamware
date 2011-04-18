package gleam.docservice.util;

import gate.AnnotationSet;
import gate.Document;
import gate.annotation.AnnotationSetImpl;
import gate.corpora.DocumentImpl;
import gate.corpora.DocumentStaxUtils;
import gleam.docservice.AnnotationSetHandle;
import gleam.docservice.DocService;
import gleam.docservice.DocServiceException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

public class DocserviceUtil {

	private static final boolean DEBUG = false;

	private static XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();

	private static XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();

	/**
	 * This method loads a document from the DocService.
	 * 
	 * @param documentID
	 *          document persistent ID
	 * @param asNameSet
	 *          a set of annotation set names to load. If this set is null, all
	 *          annotation sets will be loaded. If this set is empty, document
	 *          will be loaded without annotations. Null value corresponds to
	 *          default annotation set.
	 * @param serialDocService
	 *          a DocService to load document from
	 * @param readOnly
	 *          if true, document will be loaded in read-only mode, without
	 *          locking data on the server
	 */
/*	public static RemoteDocumentWrapper loadDocument(String documentID, Set<String> asNameSet,
			SerialDocService serialDocService, boolean readOnly) throws DocServiceException {
		Map<String, ClientASLock> documentLocks = new HashMap<String, ClientASLock>();
		DocumentInfo dInfo = null;
		Exception ex = null;
		try {
			dInfo = serialDocService.getDocInfo(documentID);
			if (dInfo == null) {
				DocServiceException dse = new DocServiceException();
				dse.setFaultString("Document with ID='" + documentID + "' wasn't found on the server.");
				throw dse;
			}
			String docContent = serialDocService.getDocContent(documentID);
			FeatureMap documentFeatures = Factory.newFeatureMap();
			documentFeatures.putAll(serialDocService.getDocumentFeatures(documentID));
			FeatureMap fm = Factory.newFeatureMap();
			fm.put("stringContent", docContent);
			Document document = (gate.corpora.DocumentImpl) Factory.createResource("gate.corpora.DocumentImpl", fm,
					documentFeatures, dInfo.getDocumentName());
			String[] asNames;
			if (asNameSet == null) {
				asNames = serialDocService.listAnnotationSets(documentID);
			} else if (asNameSet.size() == 0) {
				asNames = new String[0];
			} else {
				asNames = new String[asNameSet.size()];
				asNameSet.toArray(asNames);
			}
			for (int i = 0; i < asNames.length; i++) {
				if (DEBUG)
					System.out.println("DEBUG: " + DocserviceUtil.class.getName() + ".loadDocument(): annotation set '"
							+ asNames[i] + "'...");
				AnnotationSetHandle ash = serialDocService.getAnnotationSet(documentID, asNames[i], readOnly);
				String s = ash.getData();
				if (!readOnly) {
					if (ash.getTaskID() == null || ash.getTaskID().length() == 0) {
						DocServiceException dse = new DocServiceException();
						dse.setFaultString("Invalid task ID for document's named annotation set '" + asNames[i] + "'. Task ID="
								+ ash.getTaskID() + ". Document ID=" + documentID);
						throw dse;
					}
					documentLocks.put(asNames[i], new ClientASLock(ash.getTaskID(), asNames[i], serialDocService));
				}
				DocserviceUtil.setAnnotationSet(document.getAnnotations(asNames[i]), s);
			}
			document.setLRPersistenceId(documentID);
			RemoteDocumentWrapper rdw = RemoteDocumentWrapper.loadDocument(documentID, null, serialDocService, readOnly);
			return rdw;
		} catch (Exception e) {
			e.printStackTrace();
			ex = e;
			try {
				releaseLocks(documentLocks);
			} catch (DocServiceException dse) {
				dse.printStackTrace();
			}
		}
		DocServiceException dse = new DocServiceException();
		dse.setFaultString("Filed to load document. Name: "
				+ ((dInfo == null) ? "unknown" : "'" + dInfo.getDocumentName() + "'") + " ID: "
				+ ((dInfo == null) ? "unknown" : "'" + dInfo.getDocumentID() + "'") + " \nReason:\n"
				+ ((ex == null) ? "unknown" : ex.getMessage()));
		throw dse;
	}
*/
	
	/**
	 * Modification map:<br>
	 * There are 7 possible states for every annotation set of the document.<br>
	 * LocalAnnSet - LocalAnnSetLock - RemoteAnnSet<br>
	 * Here is action map for all states<br>
	 * 0 - means there is no ann set<br>
	 * 1 - means there is an ann set<br>
	 * ---------------------------<br>
	 * 001 - LOAD<br>
	 * 010 - ERROR, release lock (we have a lock , but locked ann set has been
	 * deleted on the server, that shouldn't be possible<br>
	 * 011 - DELETE (ann set has been deleted locally, so delete this set on the
	 * server)<br>
	 * 100 - CREATE (ann set has been created locally)<br>
	 * 101 - ERROR, do nothing (ann set has been created locally and ann set with
	 * the same name exists on the server)<br>
	 * 110 - ERROR, release lock, CREATE (we have a lock, but locked ann set has
	 * been deleted. It shouldn't be possible.<br>
	 * 111 - UPDATE (ann set exists localy and on the server and we have a lock)<br>
	 * 
	 * @param doc
	 *          a document to be saved
	 * @param docLocks
	 *          a map of document locks where key is an annotationSetName and
	 *          value is a ClientASLock
	 * @param sds
	 *          an instance of document service
	 * @param asNames
	 *          set of annotation set names to synchronize. All annotation sets
	 *          will be synchronized if it's null or empty.
	 * @throws RemoteException
	 * @throws DocServiceException
	 */
	public static void saveDocument(Document doc, Map<String, ClientASLock> docLocks, DocService sds,
			Set<String> asNames) throws DocServiceException, RemoteException {
		// make sure that document text hasn't been changed
		checkDocContent(doc, sds);
		String errors = "";
		// ----------------------------------
		// PROCESS ANNOTATION SETS
		// ----------------------------------
		Set<String> localASNames = new HashSet<String>();
		Set<String> remoteASNames;
		if (asNames == null || asNames.size() == 0) {
			if (doc.getNamedAnnotationSets() != null) localASNames.addAll(doc.getAnnotationSetNames());
			remoteASNames = new HashSet<String>(Arrays.asList(sds.listAnnotationSets((String) doc.getLRPersistenceId())));
		} else {
			localASNames.addAll(asNames);
			remoteASNames = new HashSet<String>();
			remoteASNames.addAll(asNames);
		}
		// collect all names in one place
		Set<String> allNames = new HashSet<String>();
		allNames.addAll(localASNames);
		allNames.addAll(remoteASNames);
		allNames.addAll(docLocks.keySet());
		// default annotation set will be processed separately
		allNames.remove(null);
		// building modofication map
		Map<String, Integer> modificationMap = new HashMap<String, Integer>();
		Iterator<String> itr = allNames.iterator();
		while (itr.hasNext()) {
			String name = itr.next();
			modificationMap.put(name, new Integer(((localASNames.contains(name)) ? 4 : 0)
					+ ((docLocks.keySet().contains(name)) ? 2 : 0) + ((remoteASNames.contains(name)) ? 1 : 0)));
		}
		// processing modification map
		Iterator<Map.Entry<String, Integer>> mItr = modificationMap.entrySet().iterator();
		while (mItr.hasNext()) {
			Map.Entry<String, Integer> entry = mItr.next();
			String name = entry.getKey();
			Integer v = entry.getValue();
			switch (v) {
			case 1: // 001 - LOAD
				// annotation set exists only on the server and we don't have a lock,
				// load this annotation set
				AnnotationSetHandle ash = null;
				try {
					ash = sds.getAnnotationSet((String) doc.getLRPersistenceId(), name, false);
					if (ash != null && (ash.getTaskID() == null || ash.getTaskID().length() == 0))
						errors += "Invalid task ID for document's named annotation set '" + name + "'. Task ID=" + ash.getTaskID()
								+ ". Document ID=" + doc.getLRPersistenceId() + "\r\n";
					setAnnotationSet(((Document) doc).getAnnotations(name), ash.getData());
					docLocks.put(name, new ClientASLock(ash.getTaskID(), name, sds));
				} catch (XMLStreamException e) {
					e.printStackTrace();
					errors += "An error occured while parsing XML for annotation set '" + name + "'. Error: " + e.getMessage()
							+ "\r\n";
				} catch (Exception e) {
					e.printStackTrace();
					errors += "An error occured while loading annotation set '" + name + "'. Error: " + e.getMessage() + "\r\n";
				}
				break;
			case 2: // 010 - do nothing
				errors += "Internal error! There is a lock for annotation set '" + name
						+ "', but locked ann set has been deleted on the server, that shouldn't be possible.\r\n";
				try {
					docLocks.get(name).release();
				} catch (Exception e) {
					e.printStackTrace();
					errors += "An error occured while releasing lock for annotation set '" + name + "'. " + docLocks.remove(name)
							+ "Error: " + e.getMessage() + "\r\n";
				}
				docLocks.remove(name);
				break;
			case 3: // 011 - DELETE
				// annotation set exists only on the server and we a lock
				// (!safe but...) delete this annotation set on the server
				try {
					sds.deleteAnnotationSet(docLocks.get(name).getTaskId());
				} catch (Exception e) {
					e.printStackTrace();
					errors += "An error occured while deleting annotation set '" + name + "'. Error: " + e.getMessage() + "\r\n";
				}
				try {
					docLocks.get(name).release();
					docLocks.remove(name);
				} catch (Exception e) {
					e.printStackTrace();
					errors += "An error occured while releasing lock for annotation set '" + name + "'. " + docLocks.remove(name)
							+ "Error: " + e.getMessage() + "\r\n";
				}
				break;
			case 4: // 100 - CREATE
			{
				if (doc.getAnnotations(name).size() > 0) {
					// create annotation set on the server if local annotation set is
					// not empty
					try {
						String taskId = sds.getAnnotationSetLock((String) doc.getLRPersistenceId(), name);
						ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
						XMLStreamWriter xsw = xmlOutputFactory.createXMLStreamWriter(outputStream, "UTF-8");
						DocumentStaxUtils.writeAnnotationSet(doc.getAnnotations(name), xsw, "");
						xsw.close();
						sds.setAnnotationSet(outputStream.toByteArray(), taskId, true);
						docLocks.put(name, new ClientASLock(taskId, name, sds));
					} catch (Exception e) {
						e.printStackTrace();
						errors += "An error occured while creating annotation set '" + name + "'. " + "Error: " + e.getMessage();
					}
				}
				break;
			}
			case 5: // 101 - do nothing
				errors += "Internal error! The name of new local annotation set '" + name
						+ "' conflicts with existing annotation set name on the server." + "Annotation set is NOT created.\r\n";
				break;
			case 6: // 110 - CREATE
				errors += "Internal error! There is a lock for annotation set '" + name
						+ "', but this annotation set has been deleted on the server." + "Will try to recreate.\r\n";
				if (doc.getAnnotations(name).size() > 0) {
					// create annotation set on the server if local annotation set is
					// not empty
					try {
						String taskId = sds.getAnnotationSetLock((String) doc.getLRPersistenceId(), name);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            XMLStreamWriter xsw = xmlOutputFactory.createXMLStreamWriter(outputStream, "UTF-8");
						DocumentStaxUtils.writeAnnotationSet(doc.getAnnotations(name), xsw, "");
						xsw.close();
						sds.setAnnotationSet(outputStream.toByteArray(), taskId, true);
						docLocks.put(name, new ClientASLock(taskId, name, sds));
					} catch (Exception e) {
						e.printStackTrace();
						errors += "An error occured while creating annotation set '" + name + "'. " + "Error: " + e.getMessage();
					}
				} else {
					// just release old lock if local annotation set is empty
					try {
						docLocks.get(name).release();
						docLocks.remove(name);
					} catch (Exception e) {
						e.printStackTrace();
						errors += "An error occured while releasing lock for annotation set '" + name + "'. "
								+ docLocks.remove(name) + "Error: " + e.getMessage() + "\r\n";
					}
				}
				break;
			case 7: // 111 - UPDATE
			{
				if (doc.getAnnotations(name).size() > 0) {
					// update remote annotation set
					try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            XMLStreamWriter xsw = xmlOutputFactory.createXMLStreamWriter(outputStream, "UTF-8");
						DocumentStaxUtils.writeAnnotationSet(doc.getAnnotations(name), xsw, "");
						xsw.close();
						sds.setAnnotationSet(outputStream.toByteArray(), docLocks.get(name).getTaskId(), true);
					} catch (Exception e) {
						e.printStackTrace();
						errors += "An error occured while updating annotation set '" + name + "'. " + "Error: " + e.getMessage();
					}
				} else {
					// delete remote annotation set if local annotation set is empty
					try {
						sds.deleteAnnotationSet(docLocks.get(name).getTaskId());
						docLocks.get(name).release();
						docLocks.remove(name);
					} catch (Exception e) {
						e.printStackTrace();
						errors += "An error occured while deleting annotation set '" + name + "'. Error: " + e.getMessage()
								+ "\r\n";
					}
				}
				break;
			}
			default:
				errors += "Internal error! Modification Map is corrupted. Bad entry: key='" + name + "' value=" + v + "\r\n";
				break;
			}
		}

		// --------------------------------------------
		// PROCESS DEFAULT ANNOTATION SET
		// --------------------------------------------
		if (asNames == null || asNames.size() == 0 || asNames.contains(null)) {
			if (!docLocks.containsKey(null)) {
				try {
					// there is no lock for default annotation set, so we will try to
					// create it on the server if local default annotation set is not
					// empty
					if (doc.getAnnotations().size() > 0) {
						// try to create annotation set on the server if local annotation
						// set is not empty but check before
						AnnotationSetHandle ash = sds.getAnnotationSet((String) doc.getLRPersistenceId(), null, true);
						AnnotationSet tempAS = ((Document) doc).getAnnotations("___tempDefSet___");
						setAnnotationSet(tempAS, ash.getData());
						int size = tempAS.size();
						doc.removeAnnotationSet("___tempDefSet___");
						if (size > 0) {
							errors += "Error! The default annotation set already exists on the server."
									+ " Perhaps it was created after you downloaded this document."
									+ " Default annotation set is NOT created.\r\n";
							// setAnnotationSet(((Document) doc).getAnnotations(),
							// ash.getData());
							// this.docLocks.put(null, new ClientLock(ash.getTaskID(), null));
						} else {
							// remote default annotation set is empty, so we will create
							// default
							// annotation set on the server (local default annotation set is
							// not
							// empty at this moment)
							String taskId = sds.getAnnotationSetLock((String) doc.getLRPersistenceId(), null);
	            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	            XMLStreamWriter xsw = xmlOutputFactory.createXMLStreamWriter(outputStream, "UTF-8");
							DocumentStaxUtils.writeAnnotationSet(doc.getAnnotations(), xsw, "");
							xsw.close();
							sds.setAnnotationSet(outputStream.toByteArray(), taskId, true);
							docLocks.put(null, new ClientASLock(taskId, null, sds));
						}
					} else {
						// there is no lock for default annotation set and
						// local default annotation set is empty, so we will try to load
						// remote default annotattion set if it's not empty
						AnnotationSetHandle ash = sds.getAnnotationSet((String) doc.getLRPersistenceId(), null, true);
						AnnotationSet tempAS = ((Document) doc).getAnnotations("___tempDefSet___");
						setAnnotationSet(tempAS, ash.getData());
						int size = tempAS.size();
						doc.removeAnnotationSet("___tempDefSet___");
						if (size > 0) {
							// load default annotation set
							String taskId = sds.getAnnotationSetLock((String) doc.getLRPersistenceId(), null);
							if (taskId != null) {
								setAnnotationSet(((Document) doc).getAnnotations(), ash.getData());
								docLocks.put(null, new ClientASLock(ash.getTaskID(), null, sds));
							} else {
								errors += "Can't load default annotation set. Annotation set is locked by other user.\r\n";
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					errors += "An error occured while synchronizing default annotation set. Error: " + e.getMessage();
				}
			} else {
				// there is a lock for default annotation set, so we will try to
				// synchronize it
				if (doc.getAnnotations().size() > 0) {
					// update remote annotation set
					try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            XMLStreamWriter xsw = xmlOutputFactory.createXMLStreamWriter(outputStream, "UTF-8");
						DocumentStaxUtils.writeAnnotationSet(doc.getAnnotations(), xsw, "");
						xsw.close();
						sds.setAnnotationSet(outputStream.toByteArray(), docLocks.get(null).getTaskId(), true);
					} catch (Exception e) {
						e.printStackTrace();
						errors += "An error occured while updating default annotation set. Error: " + e.getMessage();
					}
				} else {
					// delete remote annotation set if local annotation set is empty
					try {
						sds.deleteAnnotationSet(docLocks.get(null).getTaskId());
						docLocks.get(null).release();
						docLocks.remove(null);
					} catch (Exception e) {
						e.printStackTrace();
						errors += "An error occured while deleting default annotation set. Error: " + e.getMessage() + "\r\n";
					}
				}
			}
		}
		if (errors.length() > 0) {
			throw new DocServiceException(errors);
		}
	}

	public static void setAnnotationSet(AnnotationSet annSet, byte[] xmlData) throws XMLStreamException {
		// load and add annotations to the doc
		SAXParserFactory f = SAXParserFactory.newInstance();
		f.setValidating(false);
		f.setNamespaceAware(true);
		InputStream inputStream = new ByteArrayInputStream(xmlData);
		XMLStreamReader xsr = xmlInputFactory.createXMLStreamReader(inputStream, "UTF-8");
		xsr.nextTag();
		annSet.clear();
    TreeSet<Integer> allIDs = new TreeSet<Integer>();
		DocumentStaxUtils.readAnnotationSet(xsr, annSet, null, allIDs, new Boolean(true));
    // try and make sure annotation IDs in this set stay unique
    if(!allIDs.isEmpty() && annSet instanceof AnnotationSetImpl) {
      Document doc = ((AnnotationSetImpl)annSet).getDocument();
      if(doc instanceof DocumentImpl) {
        Integer maxID = allIDs.last();
        Integer nextAnnotID = ((DocumentImpl)doc).getNextAnnotationId();
        if(nextAnnotID == null || maxID.compareTo(nextAnnotID) >= 0) {
          ((DocumentImpl)doc).setNextAnnotationId(maxID + 1);
        }
      }
    }
	}

	private static void checkDocContent(Document doc, DocService sds) throws DocServiceException, RemoteException {
		String remoteDocText = sds.getDocContent((String) doc.getLRPersistenceId());
		if (!remoteDocText.equals(doc.getContent().toString())) {
			if (DEBUG) {
				System.out.println("DEBUG: " + DocserviceUtil.class.getName() + ".checkDocContent(): local doc content: '"
						+ doc.getContent().toString().replaceAll("\n", "/n\n").replaceAll("\r", "/r\r") + "'");
				System.out.println("DEBUG: " + DocserviceUtil.class.getName() + ".checkDocContent(): remote doc content: '"
						+ remoteDocText.replaceAll("\n", "/n\n").replaceAll("\r", "/r\r") + "'");
			}
			throw new DocServiceException("Can't synchronize document. Document texts are different at the server and client sides.");
		}
	}

	private static void releaseLocks(Map<String, ClientASLock> locks) throws DocServiceException {
		if (locks == null || locks.size() == 0) {
			if (DEBUG)
				System.out.println("DEBUG: " + DocserviceUtil.class.getName() + ".releaseDocLocks(): there is no any locks");
			return;
		}
		String s = "";
		for (Iterator<Entry<String, ClientASLock>> itr = locks.entrySet().iterator(); itr.hasNext();) {
			Map.Entry<String, ClientASLock> entry = itr.next();
			ClientASLock l = entry.getValue();
			try {
				l.release();
				itr.remove();
			} catch (Exception e) {
				String exText = "Exception occured while releasing document lock: " + l.toString();
				e.printStackTrace();
				s += exText + "\n";
			}
		}
		if (s.length() > 0) {
			throw new DocServiceException("There are exceptions while releasing document's locks:\n\n" + s);
		}
	}
}
