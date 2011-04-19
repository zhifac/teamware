/*
 *  InlineAnnotationService.java
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

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

@WebService(targetNamespace = "http://gate.ac.uk/ns/safe/1.1/gate-service")
public interface InlineAnnotationService {
  /**
   * Get the list of required parameter names supported by this GaS.
   * 
   * @return an array of parameter names.
   */
  @WebMethod
  @WebResult(targetNamespace = "http://gate.ac.uk/ns/safe/1.1/gate-service")
  public List<String> getRequiredParameterNames();

  /**
   * Get the list of optional parameter names supported by this GaS.
   * 
   * @return an array of parameter names.
   */
  @WebMethod
  @WebResult(targetNamespace = "http://gate.ac.uk/ns/safe/1.1/gate-service")
  public List<String> getOptionalParameterNames();

  /**
   * Process a single document, returning the original content with
   * inline markup. The document content is parsed by GATE's normal
   * document format handlers, so the underlying service will only
   * receive annotations in the "Original markups" annotation set.
   * 
   * @param documentContent the textual content of the document to
   *          process. This may be in any format acceptable to GATE,
   *          e.g. XML, HTML or plain text.
   * @param parameterValues values for the GaS parameters. A
   *          {@link GateWebServiceFault} will be thrown if not all
   *          required parameters are specified.
   * @return the document content with added inline markup (the
   *         annotations in the output sets for the given service).
   */
  @WebMethod
  @WebResult(targetNamespace = "http://gate.ac.uk/ns/safe/1.1/gate-service")
  public String annotate(
          @WebParam(name = "documentContent") String documentContent,
          @WebParam(name = "parameterValues") List<ParameterValue> parameterValues)
          throws GateWebServiceFault;
}
