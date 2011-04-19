/*
 *  DocserviceConnection.java
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
import gate.AnnotationSet;
import gate.Document;
import gate.Factory;
import gate.creole.ResourceInstantiationException;
import gate.creole.ontology.Ontology;
import gate.event.AnnotationEvent;
import gate.event.AnnotationListener;
import gate.event.AnnotationSetEvent;
import gate.event.AnnotationSetListener;
import gate.event.DocumentEvent;
import gate.event.DocumentListener;
import gate.teamware.richui.annotatorgui.gui.MainFrame;
import gate.teamware.richui.common.RichUIException;
import gate.teamware.richui.common.RichUIUtils;
import gleam.docservice.proxy.DocServiceProxy;
import gleam.docservice.proxy.DocServiceProxyFactory;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class DocserviceConnection extends Connection implements Constants, 
                                     AnnotationListener, AnnotationSetListener,
                                     DocumentListener {
  private static final boolean DEBUG = false;
  private String ontologyUrlString;
  private Ontology ontology;
  private String docserviceUrlString;
  private String owlimServiceUrlString;
  private String owlimRepositoryName;
  private DocServiceProxy dsProxy;
  private String docId;
  private String annotationSetName = "";
  private Document document;
  
  private boolean documentModified = false;
  private Set<String> loadedAnnotationSets;

  /**
   * Constructor
   * @param docserviceUrlString
   * @param docId
   * @param annotationSetName
   *          annotation set name to load. If == "" (empty string) - all
   *          annotation sets will be loaded. NULL corresponds to default
   *          annotation set.
   * @throws RichUIException
   */
  public DocserviceConnection(String docserviceUrlString, final String docId, String annotationSetName)
    throws RichUIException {
    this.docserviceUrlString = docserviceUrlString;
    this.annotationSetName = annotationSetName;
    this.docId = docId;

    try {
      URI uri = new URI(this.docserviceUrlString);
      this.dsProxy = RichUIUtils.getDocServiceProxy(uri);
    } catch(URISyntaxException e) {
      throw new RichUIException(
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

  public Document getDocument() {
    return document;
  }

  public Ontology getOntology() {
    return ontology;
  }

  public String getAnnotationSetName() {
    return annotationSetName;
  }

  public boolean isDocumentAnnotationsModified() {
    return documentModified;
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
   * @throws RichUIException
   */
  public Ontology loadOntology(String urlString, String owlimServiceUrlString, String owlimRepositoryName)
    throws RichUIException {
    try {
      this.ontology =
        AnnotatorGUI.getOntology(urlString, AnnotatorGUI.getProperties()
          .getProperty(ONTOLOGY_TYPE_PARAMETER_NAME), owlimServiceUrlString, owlimRepositoryName);
      this.ontologyUrlString = urlString;
      this.owlimRepositoryName = owlimRepositoryName;
      this.owlimServiceUrlString = owlimServiceUrlString;
      return this.ontology;
    } catch(MalformedURLException e) {
      throw new RichUIException("Invalid ontology url: " + urlString, e);
    } catch(ResourceInstantiationException e) {
      throw new RichUIException(
        "An error occured while loading the ontology (" + urlString + ")", e);
    }
  }

  public Document loadDocument() throws RichUIException {
    if(this.document == null) {
      try {
        this.document = dsProxy.getDocumentContentOnly(docId);
        loadedAnnotationSets = new HashSet<String>();
        if(annotationSetName == null) {
          // we will load only default annotation set
          loadedAnnotationSets.add(null);
        } else if(annotationSetName.length() == 0) {
          // we will load all annotation sets
          loadedAnnotationSets.addAll(Arrays.asList(this.dsProxy.getAnnotationSetNames(this.document)));
        } else {
          loadedAnnotationSets.add(annotationSetName);
        }
        for(String name : loadedAnnotationSets) {
          this.dsProxy.getAnnotationSet(this.document, name, name, false);
          AnnotationSet theSet = null;
          if(name == null || "".equals(name)) {
            theSet = this.document.getAnnotations();
          }
          else {
            theSet = this.document.getAnnotations(name);
          }
          theSet.addAnnotationSetListener(this);
          for(Annotation a : theSet) {
            a.addAnnotationListener(this);
          }
        }
      } catch(Exception e) {
        throw new RichUIException(e.getMessage(), e);
      }
    }
    return this.document;
  }

  public void saveDocument() throws RichUIException {
    if(this.document != null) {
      try {
        for(String asName : loadedAnnotationSets) {
          this.dsProxy.saveAnnotationSet(this.document, asName, asName, true);
        }
        // reset the modified flag
        this.documentModified = false;
        MainFrame.getInstance().updateBottomStatus();
      } catch(Exception e) {
        throw new RichUIException(e.getMessage(), e);
      }
    }
  }

  public void cleanup() throws RichUIException {
    if(this.document != null) {
      try {
        this.dsProxy.release(this.document);
        this.document.removeDocumentListener(this);
        Factory.deleteResource(this.document);
      } catch(Exception e) {
        throw new RichUIException(e.getMessage(), e);
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
  throws RichUIException {

  }


  // listener implementation

  public void annotationSetAdded(DocumentEvent e) {
    this.document.getAnnotations(e.getAnnotationSetName()).addAnnotationSetListener(this);
    this.documentModified = true;
    MainFrame.getInstance().updateBottomStatus();
  }

  public void annotationSetRemoved(DocumentEvent e) {
    // cannot remove this object as listener, as we can't get the
    // annotation set in question as it has already been removed
    // from the document...
    this.documentModified = true;
    MainFrame.getInstance().updateBottomStatus();
  }

  public void contentEdited(DocumentEvent e) {
    this.documentModified = true;
    MainFrame.getInstance().updateBottomStatus();
  }
  
  public void annotationUpdated(AnnotationEvent e) {
    this.documentModified = true;
    MainFrame.getInstance().updateBottomStatus();
  }

  public void annotationAdded(AnnotationSetEvent e) {
    e.getAnnotation().addAnnotationListener(this);
    this.documentModified = true;
    MainFrame.getInstance().updateBottomStatus();
  }

  public void annotationRemoved(AnnotationSetEvent e) {
    e.getAnnotation().removeAnnotationListener(this);
    this.documentModified = true;
    MainFrame.getInstance().updateBottomStatus();
  }
}
