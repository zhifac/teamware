/*
 *  InlineAnnotationServiceImpl.java
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
