/*
 *  GateWebServiceImpl.java
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
package gleam.gateservice.endpoint;

import java.net.URI;
import java.util.List;

import javax.jws.WebService;

/**
 * The GaS endpoint implementation object. This simply hands off to a
 * handler obtained from Spring.
 */
@WebService(endpointInterface = "gleam.gateservice.endpoint.GateWebService", targetNamespace = "http://gate.ac.uk/ns/safe/1.1/gate-service")
public class GateWebServiceImpl implements GateWebService {

  /**
   * The real handler that does the work. Injected by Spring.
   */
  private GateWebService endpointHandler;

  public void setEndpointHandler(GateWebService endpointHandler) {
    this.endpointHandler = endpointHandler;
  }

  public GateWebService getEndpointHandler() {
    return endpointHandler;
  }

  public void processRemoteDocument(URI executiveLocation, String taskId,
          URI docServiceLocation, String docId,
          List<AnnotationSetMapping> annotationSets,
          List<ParameterValue> parameterValues) throws GateWebServiceFault {
    getEndpointHandler().processRemoteDocument(executiveLocation, taskId,
            docServiceLocation, docId, annotationSets, parameterValues);
  }

  public void processRemoteDocuments(URI executiveLocation, String taskId,
          URI docServiceLocation, List<AnnotationTask> tasks,
          List<ParameterValue> parameterValues) throws GateWebServiceFault {
    getEndpointHandler().processRemoteDocuments(executiveLocation, taskId,
            docServiceLocation, tasks, parameterValues);
  }

  public List<String> getInputAnnotationSetNames() {
    return getEndpointHandler().getInputAnnotationSetNames();
  }

  public List<String> getOutputAnnotationSetNames() {
    return getEndpointHandler().getOutputAnnotationSetNames();
  }

  public List<String> getOptionalParameterNames() {
    return getEndpointHandler().getOptionalParameterNames();
  }

  public List<String> getRequiredParameterNames() {
    return getEndpointHandler().getRequiredParameterNames();
  }

  public List<AnnotationSetData> processDocument(byte[] documentXml,
          String encoding, List<ParameterValue> parameterValues)
          throws GateWebServiceFault {
    return getEndpointHandler().processDocument(documentXml, encoding,
            parameterValues);
  }

}
