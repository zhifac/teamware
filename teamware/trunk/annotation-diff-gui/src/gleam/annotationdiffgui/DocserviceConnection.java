package gleam.annotationdiffgui;

import gate.Document;
import gate.Factory;
import gate.creole.ResourceInstantiationException;
import gate.creole.ontology.Ontology;
import gleam.annotationdiffgui.gui.MainFrame;
import gleam.docservice.DocService;
import gleam.docservice.util.RemoteDocumentListener;
import gleam.docservice.util.RemoteDocumentWrapper;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class DocserviceConnection extends Connection implements Constants {
  private static final boolean DEBUG = false;
  private String docserviceUrlString;
  private DocService serialDocService;
  private String docId;
  private RemoteDocumentWrapper remoteDocument;
  private String annotationSetName = "";

  private DocserviceConnection() {
  }

  /**
   * Constructor
   * @param docserviceUrlString
   * @param docId
   * @param annotationSetName
   *          annotation set name to load. If == "" (empty string) - all
   *          annotation sets will be loaded. NULL corresponds to default
   *          annotation set.
   * @throws AnnotationDiffGUIException
   */
  public DocserviceConnection(String docserviceUrlString, final String docId)
    throws AnnotationDiffGUIException {
    this.docserviceUrlString = docserviceUrlString;
    this.docId = docId;

    try {
      URL url = new URL(this.docserviceUrlString);
      this.serialDocService = AnnotationDiffGUIUtils.getDocServiceStub(url);
    } catch(MalformedURLException e) {
      throw new AnnotationDiffGUIException(
        "An error occured while connecting to Document Service at:\n"
          + this.docserviceUrlString + "\n\n" + e.getMessage(), e);
    }
  }

  public String getDocserviceUrlString() {
    return docserviceUrlString;
  }

  public String getDocId() {
    return docId;
  }

  public DocService getSerialDocService() {
    return serialDocService;
  }

  public Document getDocument() {
    return remoteDocument.getDocument();
  }

  public String getAnnotationSetName() {
    return annotationSetName;
  }

  public boolean isDocumentAnnotationsModified() {
    return remoteDocument.isDocumentModified();
  }

  public String getConnectionStatus() {
    String serviceLocation = docserviceUrlString;
    try {
      URL url = new URL(serviceLocation);
      serviceLocation = url.getHost() + url.getPath();
    } catch(MalformedURLException e) {
    }
    return "Connected in DIRECT mode: " + docId;
  }

  public Document loadDocument() throws AnnotationDiffGUIException {
    if(this.remoteDocument != null) return this.remoteDocument.getDocument();
    try {
      Set<String> asNames = new HashSet<String>();
      if(annotationSetName == null) {
        // we will load only default annotation set
        asNames.add(null);
      } else if(annotationSetName.length() == 0) {
        // we will load all annotation sets
        // asNames.addAll(Arrays.asList(this.serialDocService.listAnnotationSets(this.docId)));
        asNames = null;
      } else {
        asNames.add(annotationSetName);
      }
      this.remoteDocument =
        RemoteDocumentWrapper.loadDocument(docId, asNames, serialDocService,
          true);
      this.remoteDocument
        .addRemoteDocumentListener(new RemoteDocumentListener() {
          public void documentClosed() {
          }

          public void documentModified() {
            MainFrame.getInstance().updateBottomStatus();
          }

          public void documentSynchronized() {
            MainFrame.getInstance().updateBottomStatus();
          }
        });
      return remoteDocument.getDocument();
    } catch(Exception e) {
      throw new AnnotationDiffGUIException(e.getMessage(), e);
    }
  }

  public void saveDocument() throws AnnotationDiffGUIException {
    if(this.remoteDocument != null) {
      try {
        this.remoteDocument.sync(false, false);
      } catch(Exception e) {
        throw new AnnotationDiffGUIException(e.getMessage(), e);
      }
    }
  }

  public void cleanup() throws AnnotationDiffGUIException {
    if(this.remoteDocument != null) {
      try {
        this.remoteDocument.close();
      } catch(Exception e) {
        throw new AnnotationDiffGUIException(e.getMessage(), e);
      }
    }
  }

}
