/*
 *  GateServicePR.java
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
package gleam.gateservice.pr;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

import gate.AnnotationSet;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.Resource;
import gate.TextualDocument;
import gate.corpora.DocumentStaxUtils;
import gate.creole.AbstractLanguageAnalyser;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import gleam.gateservice.endpoint.AnnotationSetData;
import gleam.gateservice.endpoint.GateWebService;
import gleam.gateservice.endpoint.GateWebServiceFault;
import gleam.gateservice.endpoint.ParameterValue;
import gleam.util.cxf.CXFClientUtils;

public class GateServicePR extends AbstractLanguageAnalyser {

  /**
   * StAX input factory used to parse annotation set XML retrieved from
   * the doc service.
   */
  private static XMLInputFactory inputFactory = XMLInputFactory.newInstance();

  /**
   * StAX output factory used to write the document to XML.
   */
  private static XMLOutputFactory outputFactory =
          XMLOutputFactory.newInstance();

  /**
   * Parameter values to pass to the remote GaS.
   */
  protected FeatureMap gasParameterValues = Factory.newFeatureMap();

  /**
   * The location of the web service endpoint.
   */
  private URL serviceLocation;

  /**
   * Optional timeout (in ms) to use when contacting the service. If not
   * specified the default timeout provided by Axis is used.
   */
  private Integer httpTimeout;

  /**
   * Should the web service stub use the "chunked" HTTP transfer
   * encoding?
   */
  private Boolean useChunkedEncoding = Boolean.FALSE;
  
  /**
   * Should the web service stub use GZIP compression of its requests,
   * and support compressed responses?
   */
  private Boolean useCompression = Boolean.TRUE;

  /**
   * Client stub used to talk to the remote service.
   */
  private GateWebService remoteGas;

  /**
   * Cache for the required parameter names from the GaS.
   */
  private List<String> requiredParameters;

  /**
   * Cache for the optional parameter names from the GaS.
   */
  private List<String> optionalParameters;

  /**
   * Initialise this GaSPR. We create a client stub and query the
   * service to get its required and optional parameter names.
   */
  @Override
  public Resource init() throws ResourceInstantiationException {
    if(serviceLocation == null) {
      throw new ResourceInstantiationException("No serviceLocation specified");
    }
    
    // workaround to enable loading of CXF from the GateClassLoader
    ClassLoader oldContextCL = Thread.currentThread().getContextClassLoader();
    Thread.currentThread().setContextClassLoader(Gate.getClassLoader());

    // create the client stub
    JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
    Map<String, Object> props = new HashMap<String, Object>();
    props.put("mtom-enabled", Boolean.TRUE);
    factory.setProperties(props);
    factory.setServiceClass(GateWebService.class);
    factory.setAddress(serviceLocation.toExternalForm());

    remoteGas = (GateWebService)factory.create();
    
    Thread.currentThread().setContextClassLoader(oldContextCL);

    // set HTTP properties if necessary
    if(httpTimeout != null) {
      CXFClientUtils.setTimeout(remoteGas, httpTimeout);
    }
    CXFClientUtils.setAllowChunking(remoteGas, useChunkedEncoding == null
            || !useChunkedEncoding.booleanValue());
    
    if(useCompression != null && useCompression.booleanValue()) {
      CXFClientUtils.configureForCompression(remoteGas);
    }

    // check that the service is alive, and get the parameter names for
    // sanity checking in execute()
    requiredParameters = remoteGas.getRequiredParameterNames();
    optionalParameters = remoteGas.getOptionalParameterNames();

    return this;
  }

  /**
   * Pass the current document and parameter values to the GaS, and
   * integrate the returned annotation sets into the document.
   */
  @SuppressWarnings("unchecked")
  @Override
  public void execute() throws ExecutionException {
    checkParameters();

    List<ParameterValue> params = getParametersForService();

    String encoding = "UTF-8";
    if(document instanceof TextualDocument) {
      encoding = ((TextualDocument)document).getEncoding();
    }
    
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    byte[] documentData = null;
    try {
      XMLStreamWriter xsw = outputFactory.createXMLStreamWriter(baos, encoding);
      xsw.writeStartDocument();
      DocumentStaxUtils.writeDocument(document, xsw, "");
      xsw.close();
      documentData = baos.toByteArray();
    }
    catch(XMLStreamException e) {
      throw new ExecutionException("Error writing document to XML", e);
    }
      
    List<AnnotationSetData> returnedSets = null;
    try {
      returnedSets = remoteGas.processDocument(documentData, encoding, params);
    }
    catch(GateWebServiceFault e) {
      throw (ExecutionException)new ExecutionException(
              "GaS error processing document").initCause(e);
    }

    /*
     * If we specify not to return anything as a result of service
     * execution, the value of the retunedSets will be null
     */
    if(returnedSets == null) return;

    for(AnnotationSetData set : returnedSets) {
      AnnotationSet annotationSet = null;
      if(set.getName() == null || set.getName().equals("")) {
        annotationSet = document.getAnnotations();
      }
      else {
        annotationSet = document.getAnnotations(set.getName());
      }

      // save the old annotations so we can restore them if anything
      // goes wrong
      Set savedAnnots = new HashSet(annotationSet);

      // remove old annotations from the set, ready to replace them with
      // the new ones
      annotationSet.clear();

      try {
        XMLStreamReader xsr =
                inputFactory.createXMLStreamReader(new ByteArrayInputStream(set.getXmlData()), "UTF-8");

        // find the initial AnnotationSet tag
        xsr.nextTag();
        xsr.require(XMLStreamConstants.START_ELEMENT, null, "AnnotationSet");

        // parse the XML
        DocumentStaxUtils.readAnnotationSet(xsr, annotationSet, null,
                new HashSet(), Boolean.TRUE);

        xsr.close();
      }
      catch(XMLStreamException e) {
        // restore the old contents of the annotation set
        annotationSet.clear();
        annotationSet.addAll(savedAnnots);
        throw (ExecutionException)new ExecutionException(
                "Error parsing annotations returned from GaS "
                        + "for annotation set named " + set.getName())
                .initCause(e);
      }
      catch(Exception e) {
        // restore the old contents of the annotation set
        annotationSet.clear();
        annotationSet.addAll(savedAnnots);
        throw (ExecutionException)new ExecutionException(
                "Error parsing annotations returned from GaS "
                        + "for annotation set named " + set.getName())
                .initCause(e);
      }
    }
  }

  private List<ParameterValue> getParametersForService() throws ExecutionException {
    List<ParameterValue> paramsList = new ArrayList<ParameterValue>();
    // required parameters - we know these are all set
    if(requiredParameters != null) {
      for(String name : requiredParameters) {
        Object value = gasParameterValues.get(name);
        if(!(value instanceof String)) {
          throw new ExecutionException("Value for parameter " + name
                  + " is not a String");
        }
        ParameterValue pv = new ParameterValue();
        pv.setName(name);
        pv.setValue((String)value);
        paramsList.add(pv);
      }
    }

    // optional parameters - some of these may not be set
    if(optionalParameters != null) {
      for(String name : optionalParameters) {
        if(gasParameterValues.containsKey(name)) {
          Object value = gasParameterValues.get(name);
          if(value != null && !(value instanceof String)) {
            throw new ExecutionException("Value for parameter " + name
                    + " is not a String");
          }
          ParameterValue pv = new ParameterValue();
          pv.setName(name);
          pv.setValue((String)value);
          paramsList.add(pv);
        }
      }
    }

    return paramsList;
  }

  /**
   * Check that all required parameters have values set in the parameter
   * map.
   * 
   * @throws ExecutionException if any of the required parameters are
   *           missing.
   */
  protected void checkParameters() throws ExecutionException {
    if(requiredParameters != null) {
      for(String name : requiredParameters) {
        if(!gasParameterValues.containsKey(name)) {
          throw new ExecutionException("Required parameter " + name
                  + " is not set.");
        }
      }
    }
  }

  // //// parameter setters and getters //// //

  public FeatureMap getGasParameterValues() {
    return gasParameterValues;
  }

  public void setGasParameterValues(FeatureMap gasParameterValues) {
    this.gasParameterValues = gasParameterValues;
  }

  public URL getServiceLocation() {
    return serviceLocation;
  }

  public void setServiceLocation(URL serviceLocation) {
    this.serviceLocation = serviceLocation;
  }

  public Integer getHttpTimeout() {
    return httpTimeout;
  }

  public void setHttpTimeout(Integer timeout) {
    this.httpTimeout = timeout;
  }

  public Boolean getUseChunkedEncoding() {
    return useChunkedEncoding;
  }

  public void setUseChunkedEncoding(Boolean useChunkedEncoding) {
    this.useChunkedEncoding = useChunkedEncoding;
  }

  public Boolean getUseCompression() {
    return useCompression;
  }

  public void setUseCompression(Boolean useCompression) {
    this.useCompression = useCompression;
  }

}
