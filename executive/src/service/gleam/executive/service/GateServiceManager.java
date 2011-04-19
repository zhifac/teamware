/*
 *  GateServiceManager.java
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
package gleam.executive.service;

import gleam.gateservice.client.GateServiceClientException;

import java.net.URI;
import java.util.List;
import java.util.Map;


public interface GateServiceManager {

  /**
   * Get the list of required parameter names supported by this GaS.
   * @param gateServiceURI the endpoint URI of the service 
   *          which method will be invoked
   * @return an array of parameter names.
   */
  public String[] getRequiredParameterNames(URI gateServiceURI) throws GateServiceClientException;

  /**
   * Get the list of optional parameter names supported by this GaS.
   * @param gateServiceURI the endpoint URI of the service 
   *          which method will be invoked
   * @return an array of parameter names.
   */
  public String[] getOptionalParameterNames(URI gateServiceURI) throws GateServiceClientException;

  /**
   * Get the list of annotation set names required by this service as
   * input. Note that one of the entries in the array may be
   * <code>null</code> if the service takes input from the default
   * annotation set.
   * @param gateServiceURL the endpoint URL of the service 
   *          which method will be invoked
   * @return an array of annotation set names, one of which may be
   *         <code>null</code>.
   */
  public String[] getInputAnnotationSetNames(URI gateServiceURI)
          throws GateServiceClientException;

  /**
   * Get the list of annotation set names output by this service. Note
   * that one of the entries in the array may be <code>null</code> if
   * the service provides output to the default annotation set. Also
   * note that the same annotation set may be named as both an input and
   * an output set.
   * @param gateServiceURI the endpoint URI of the service 
   *          which method will be invoked
   * @return an array of annotation set names, one of which may be
   *         <code>null</code>.
   */
  public String[] getOutputAnnotationSetNames(URI gateServiceURI)
          throws GateServiceClientException;

  /**
   * Start a process running on this GaS.
   * @param gateServiceURI the endpoint URI of the service 
   *          which method will be invoked
   * @param taskID the task ID that will be returned to the executive
   *          callback when the task has completed or failed.
   * @param docId the ID of the document to be processed in this doc
   *          service.
   * @param asMappings mappings for the input and output annotation set
   *          names required by this service. The map keys are the
   *          annotation set names used by the GaS, the values are the
   *          annotation set names in the document service (so you can
   *          map more than one GaS annotation set to the same DS
   *          annotation set, but not the other way around).
   * @param parameterValues parameter values required by this service.
   * @throws GateServiceClientException if an error occurs when calling
   *           the service.
   */
  public void processRemoteDocument(URI gateServiceURI, String taskID,
          String docId, Map<String, String> asMappings,
          Map<String, String> parameterValues)
          throws GateServiceClientException;

  /**
   * Start a process running on this GaS.
   * @param gateServiceURI the endpoint URI of the service 
   *          which method will be invoked
   * @param taskID the task ID that will be returned to the executive
   *          callback when the task has completed or failed.
   * @param docId the ID of the document to be processed in this doc
   *          service.
   * @param asMappings mappings for the input and output annotation set
   *          names required by this service. The map keys are the
   *          annotation set names used by the GaS, the values are the
   *          annotation set names in the document service (so you can
   *          map more than one GaS annotation set to the same DS
   *          annotation set, but not the other way around).
   * @param parameterValues parameter values required by this service.
   * @param canUsePrivateUrls can the service being called use the
   *          private URLs for the doc service and callback service?
   * @throws GateServiceClientException if an error occurs when calling
   *           the service.
   */
  public void processRemoteDocument(URI gateServiceURI, String taskID,
          String docId, Map<String, String> asMappings,
          Map<String, String> parameterValues, boolean canUsePrivateUrls)
          throws GateServiceClientException;


  /**
   * Start a process running on this GaS.
   * @param gateServiceURI the endpoint URI of the service 
   *          which method will be invoked
   * @param taskID the task ID that will be returned to the executive
   *          callback when the task has completed or failed.
   * @param docId the ID of the document to be processed in this doc
   *          service.
   * @param asMappings mappings for the input and output annotation set
   *          names required by this service. The map keys are the
   *          annotation set names used by the GaS, the values are the
   *          annotation set names in the document service (so you can
   *          map more than one GaS annotation set to the same DS
   *          annotation set, but not the other way around).
   * @param parameterValues parameter values required by this service.
   * @throws GateServiceClientException if an error occurs when calling
   *           the service.
   */
  public void processRemoteDocuments(URI gateServiceURI, String taskID,
          List<gleam.gateservice.client.AnnotationTask> tasks,
          Map<String, String> parameterValues)
          throws GateServiceClientException;

  /**
   * Start a process running on this GaS.
   * @param gateServiceURI the endpoint URI of the service 
   *          which method will be invoked
   * @param taskID the task ID that will be returned to the executive
   *          callback when the task has completed or failed.
   * @param parameterValues parameter values required by this service.
   * @param docId the ID of the document to be processed in this doc
   *          service.
   * @param asMappings mappings for the input and output annotation set
   *          names required by this service. The map keys are the
   *          annotation set names used by the GaS, the values are the
   *          annotation set names in the document service (so you can
   *          map more than one GaS annotation set to the same DS
   *          annotation set, but not the other way around).
   * @param canUsePrivateUrls can the service being called use the
   *          private URLs for the doc service and callback service?
   * @throws GateServiceClientException if an error occurs when calling
   *           the service.
   */
  public void processRemoteDocuments(URI gateServiceURI, String taskID,
          List<gleam.gateservice.client.AnnotationTask> tasks,
          Map<String, String> parameterValues, boolean canUsePrivateUrls)
          throws GateServiceClientException;

}

