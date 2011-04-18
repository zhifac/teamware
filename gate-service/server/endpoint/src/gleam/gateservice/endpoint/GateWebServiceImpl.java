/*
 * This file is part of GLEAM, licenced under the GNU Lesser General Public
 * Licence.
 *
 * (c) 2006 The University of Sheffield
 * 
 * $Id$
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
