/*
 * This file is part of GLEAM, licenced under the GNU Lesser General Public
 * Licence.
 *
 * (c) 2006 The University of Sheffield
 * 
 * $Id$
 */
package gleam.gateservice.endpoint;

import java.util.List;

import javax.annotation.Resource;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;

/**
 * The GaS inline annotation service implementation object. This simply
 * hands off to a handler obtained from Spring.
 */
@WebService(endpointInterface = "gleam.gateservice.endpoint.InlineAnnotationService", targetNamespace = "http://gate.ac.uk/ns/safe/1.1/gate-service")
public class InlineAnnotationServiceImpl implements InlineAnnotationService {

  /**
   * The real handler that does the work. Injected by Spring.
   */
  private InlineAnnotationService endpointHandler;

  public InlineAnnotationService getEndpointHandler() {
    return endpointHandler;
  }

  public void setEndpointHandler(InlineAnnotationService endpointHandler) {
    this.endpointHandler = endpointHandler;
  }

  public List<String> getOptionalParameterNames() {
    return getEndpointHandler().getOptionalParameterNames();
  }

  public List<String> getRequiredParameterNames() {
    return getEndpointHandler().getRequiredParameterNames();
  }

  public String annotate(String documentContent,
          List<ParameterValue> parameterValues) throws GateWebServiceFault {
    return getEndpointHandler().annotate(documentContent, parameterValues);
  }

}
