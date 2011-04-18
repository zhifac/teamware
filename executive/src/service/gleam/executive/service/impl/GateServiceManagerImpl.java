package gleam.executive.service.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import gleam.executive.service.DocServiceManager;
import gleam.executive.service.GateServiceManager;
import gleam.gateservice.client.GateServiceClientException;
import gleam.gateservice.client.GateServiceClientFactory;
/*
 * GateServiceManagerImpl.java
 *
 * Copyright (c) 1998-2007, The University of Sheffield.
 *
 * This file is part of GATE (see http://gate.ac.uk/), and is free software,
 * licenced under the GNU Library General Public License, Version 2, June 1991
 * (in the distribution as file licence.html, and also available at
 * http://gate.ac.uk/gate/licence.html).
 *
 * <p>
 * <a href="GateServiceManagerImpl.java.html"><i>View Source</i></a>
 * </p>
 *
 *  @author <a href="M.Agatonovic@dcs.shef.ac.uk">Milan Agatonovic</a>
 *
 */
public class GateServiceManagerImpl extends BaseManager implements
		GateServiceManager {
	private GateServiceClientFactory gateServiceClientFactory;
	
	private GateServiceClientFactory privateGateServiceClientFactory;

	private DocServiceManager docServiceManager;
	
	public GateServiceManagerImpl(DocServiceManager dsManager,
  GateServiceClientFactory gsClientFactory) {
    this(dsManager, gsClientFactory, gsClientFactory);
  }

  public GateServiceManagerImpl(DocServiceManager dsManager,
			GateServiceClientFactory gsClientFactory,
			GateServiceClientFactory privateClientFactory) {
	  this.docServiceManager = dsManager;
		this.gateServiceClientFactory = gsClientFactory;
		this.privateGateServiceClientFactory = privateClientFactory;
	}

	/**
   * @see gleam.executive.service.GateServiceManager#processRemoteDocument
   *      (String, URI, String, Map, Map)
   */
  public void processRemoteDocument(URI gateServiceURI, String taskID,
  		String docId, Map<String, String> asMappings,
  		Map<String, String> parameterValues)
  		throws GateServiceClientException {
    processRemoteDocument(gateServiceURI, taskID, docId, asMappings,
            parameterValues, false);
  }

  /**
	 * @see gleam.executive.service.GateServiceManager#processRemoteDocument
	 *      (String, URI, String, Map, Map, boolean)
	 */
	public void processRemoteDocument(URI gateServiceURI, String taskID,
			String docId, Map<String, String> asMappings,
			Map<String, String> parameterValues, boolean canUsePrivateUrls)
			throws GateServiceClientException {
		log.debug("Call processRemoteDocument for endpoint " + gateServiceURI);
		// choose the right doc service URL and client factory depending
		// on whether the service can use private URLs
    String docServiceUriString = null;
    GateServiceClientFactory clientFactory = null;
    if(canUsePrivateUrls) {
      docServiceUriString = docServiceManager.getPrivateDocServiceURL();
      clientFactory = privateGateServiceClientFactory;
    }
    else {
      docServiceUriString = docServiceManager.getDocServiceURL();
      clientFactory = gateServiceClientFactory;
    }
		URI docServiceURI = null;
		try {
		  docServiceURI = new URI(docServiceUriString);
		}
		catch(URISyntaxException use) {
		  throw new GateServiceClientException(
		          "Invalid URI received from doc service manager", use);
		}
		clientFactory.getGateServiceClient(gateServiceURI)
				.processRemoteDocument(taskID, docServiceURI, docId,
						asMappings, parameterValues);
	}

	public void processRemoteDocuments(URI gateServiceURI, String taskID,
	        List<gleam.gateservice.client.AnnotationTask> tasks,
	        Map<String, String> parameterValues)
       throws GateServiceClientException {
    processRemoteDocuments(gateServiceURI, taskID, tasks, parameterValues,
            false);
  }

  public void processRemoteDocuments(URI gateServiceURI, String taskID,
			List<gleam.gateservice.client.AnnotationTask> tasks,
			Map<String, String> parameterValues, boolean canUsePrivateUrls)
			throws GateServiceClientException {
		log.debug("Call processRemoteDocument for endpoint " + gateServiceURI);
    // choose the right doc service URL and client factory depending
    // on whether the service can use private URLs
		String docServiceUriString = null;
		GateServiceClientFactory clientFactory = null;
		if(canUsePrivateUrls) {
		  docServiceUriString = docServiceManager.getPrivateDocServiceURL();
		  clientFactory = privateGateServiceClientFactory;
		}
		else {
		  docServiceUriString = docServiceManager.getDocServiceURL();
		  clientFactory = gateServiceClientFactory;
		}
    URI docServiceURI = null;
    try {
      docServiceURI = new URI(docServiceUriString);
    }
    catch(URISyntaxException use) {
      throw new GateServiceClientException(
              "Invalid URI received from doc service manager", use);
    }
		clientFactory.getGateServiceClient(gateServiceURI)
				.processRemoteDocuments(taskID, docServiceURI, tasks,
						parameterValues);
	}

	public String[] getRequiredParameterNames(URI gateServiceURI)
			throws GateServiceClientException {
	  // doesn't matter which client factory we use as this call does not
	  // involve any callbacks
		return gateServiceClientFactory.getGateServiceClient(gateServiceURI)
				.getRequiredParameterNames();
	}

	public String[] getOptionalParameterNames(URI gateServiceURI)
			throws GateServiceClientException {
    // doesn't matter which client factory we use as this call does not
    // involve any callbacks
		return gateServiceClientFactory.getGateServiceClient(gateServiceURI)
				.getOptionalParameterNames();
	}

	public String[] getInputAnnotationSetNames(URI gateServiceURI)
			throws GateServiceClientException {
    // doesn't matter which client factory we use as this call does not
    // involve any callbacks
		return gateServiceClientFactory.getGateServiceClient(gateServiceURI)
				.getInputAnnotationSetNames();
	}

	public String[] getOutputAnnotationSetNames(URI gateServiceURI)
			throws GateServiceClientException {
    // doesn't matter which client factory we use as this call does not
    // involve any callbacks
		return gateServiceClientFactory.getGateServiceClient(gateServiceURI)
				.getOutputAnnotationSetNames();
	}
}
