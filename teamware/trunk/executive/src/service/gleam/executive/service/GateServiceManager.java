package gleam.executive.service;

import gleam.gateservice.client.GateServiceClientException;

import java.net.URI;
import java.util.List;
import java.util.Map;


/*
 *  GateServiceManager.java
 *
 *  Copyright (c) 1998-2006, The University of Sheffield.
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 * <p>
 * <a href="GateServiceManager.java.html"><i>View Source</i></a>
 * </p>
 *
 *  @author <a href="agaton@dcs.shef.ac.uk">Milan Agatonovic</a>
 */
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

