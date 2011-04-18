package gleam.annotatorgui;

import gate.Document;
import gate.Factory;
import gate.creole.ResourceInstantiationException;
import gate.creole.ontology.Ontology;
import gleam.annotatorgui.gui.MainFrame;
import gleam.docservice.DocService;
import gleam.docservice.util.RemoteDocumentListener;
import gleam.docservice.util.RemoteDocumentWrapper;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class DocserviceConnection extends Connection implements Constants {
  private static final boolean DEBUG = false;
  private String ontologyUrlString;
  private Ontology ontology;
  private String docserviceUrlString;
  private String owlimServiceUrlString;
  private String owlimRepositoryName;
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
   * @throws AnnotatorGUIExeption
   */
  public DocserviceConnection(String docserviceUrlString, final String docId, String annotationSetName)
    throws AnnotatorGUIExeption {
    this.docserviceUrlString = docserviceUrlString;
    this.annotationSetName = annotationSetName;
    this.docId = docId;

    try {
      URL url = new URL(this.docserviceUrlString);
      this.serialDocService = AnnotatorGUIUtils.getDocServiceStub(url);
    } catch(MalformedURLException e) {
      throw new AnnotatorGUIExeption(
        "An error occured while connecting to Document Service at:\n"
          + this.docserviceUrlString + "\n\n" + e.getMessage(), e);
    }
  }

  public String getDocserviceUrlString() {
    return docserviceUrlString;
  }

  /**
   * Gets the ontology URL String
   * @return
   */
  public String getOntologyUrlString() {
    return ontologyUrlString;
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

  public Ontology getOntology() {
    return ontology;
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

  /**
   * Loads the Ontology from the given url string
   * @param urlString
   * @param owlimServiceUrlString
   * @param owlimRepositoryName
   * @return
   * @throws AnnotatorGUIExeption
   */
  public Ontology loadOntology(String urlString, String owlimServiceUrlString, String owlimRepositoryName)
    throws AnnotatorGUIExeption {
    try {
      this.ontology =
        AnnotatorGUI.getOntology(urlString, AnnotatorGUI.getProperties()
          .getProperty(ONTOLOGY_TYPE_PARAMETER_NAME), owlimServiceUrlString, owlimRepositoryName);
      this.ontologyUrlString = urlString;
      this.owlimRepositoryName = owlimRepositoryName;
      this.owlimServiceUrlString = owlimServiceUrlString;
      return this.ontology;
    } catch(MalformedURLException e) {
      throw new AnnotatorGUIExeption("Invalid ontology url: " + urlString, e);
    } catch(ResourceInstantiationException e) {
      throw new AnnotatorGUIExeption(
        "An error occured while loading the ontology (" + urlString + ")", e);
    }
  }

  public Document loadDocument() throws AnnotatorGUIExeption {
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
          false);
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
      throw new AnnotatorGUIExeption(e.getMessage(), e);
    }
  }

  public void saveDocument() throws AnnotatorGUIExeption {
    if(this.remoteDocument != null) {
      try {
        this.remoteDocument.sync(false, false);
      } catch(Exception e) {
        throw new AnnotatorGUIExeption(e.getMessage(), e);
      }
    }
  }

  public void cleanup() throws AnnotatorGUIExeption {
    if(this.remoteDocument != null) {
      try {
        this.remoteDocument.close();
      } catch(Exception e) {
        throw new AnnotatorGUIExeption(e.getMessage(), e);
      }
    }
    if(this.ontology != null) {
      Factory.deleteResource(this.ontology);
      this.ontology = null;
    }
  }

  /**
   * Gets the OWLIMServiceURLString
   *
   * @return
   */
  public String getOwlimServiceUrlString() {
    return owlimServiceUrlString;
  }

  public String getOwlimRepositoryName() {
    return owlimRepositoryName;
  }

  public void setAnnotatorTask(AnnotatorTask annotatorTask)
  throws AnnotatorGUIExeption {

  }

}
