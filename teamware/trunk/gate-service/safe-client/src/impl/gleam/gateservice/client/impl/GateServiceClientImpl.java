/*
 *  GateServiceClientImpl.java
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
package gleam.gateservice.client.impl;

import gleam.gateservice.client.GateServiceClient;
import gleam.gateservice.client.GateServiceClientException;
import gleam.gateservice.endpoint.AnnotationTask;
import gleam.gateservice.endpoint.AnnotationSetMapping;
import gleam.gateservice.endpoint.GateWebService;
import gleam.gateservice.endpoint.GateWebServiceFault;
import gleam.gateservice.endpoint.ParameterValue;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GateServiceClientImpl implements GateServiceClient {

  private static final Log log = LogFactory.getLog(GateServiceClientImpl.class);

  /**
   * Location of the executive callback service, passed to the GaS to be
   * used for notifications.
   */
  private URI executiveLocation;

  /**
   * The web service stub that makes the actual call to the GaS.
   */
  private GateWebService wsStub;

  GateServiceClientImpl(GateWebService stub, URI callbackURI) {
    this.wsStub = stub;
    this.executiveLocation = callbackURI;
  }

  public String[] getInputAnnotationSetNames()
          throws GateServiceClientException {
    List<String> names = wsStub.getInputAnnotationSetNames();
    return names.toArray(new String[names.size()]);
  }

  public String[] getOptionalParameterNames() throws GateServiceClientException {
    List<String> names = wsStub.getOptionalParameterNames();
    return names.toArray(new String[names.size()]);
  }

  public String[] getOutputAnnotationSetNames()
          throws GateServiceClientException {
    List<String> names = wsStub.getOutputAnnotationSetNames();
    return names.toArray(new String[names.size()]);
  }

  public String[] getRequiredParameterNames() throws GateServiceClientException {
    List<String> names = wsStub.getRequiredParameterNames();
    return names.toArray(new String[names.size()]);
  }

  public void processRemoteDocument(String taskID, URI docServiceLocation,
          String docId, Map<String, String> asMappings,
          Map<String, String> parameterValues)
          throws GateServiceClientException {
    int numASMappings = (asMappings == null) ? 0 : asMappings.size();
    List<AnnotationSetMapping> asMappingsForWS = new ArrayList<AnnotationSetMapping>(numASMappings);
    if(asMappings != null) {
      for(Map.Entry<String, String> mapping : asMappings.entrySet()) {
        AnnotationSetMapping asm = new AnnotationSetMapping();
        asm.setGateServiceASName("".equals(mapping.getKey()) ? null : mapping
                .getKey());
        asm.setDocServiceASName("".equals(mapping.getValue()) ? null : mapping
                .getValue());
        asMappingsForWS.add(asm);
      }
    }

    int numParameterValues = (parameterValues == null) ? 0 : parameterValues
            .size();
    List<ParameterValue> parameterValuesForWS = new ArrayList<ParameterValue>(numParameterValues);
    if(parameterValues != null) {
      for(Map.Entry<String, String> param : parameterValues.entrySet()) {
        ParameterValue pv = new ParameterValue();
        pv.setName(param.getKey());
        pv.setValue(param.getValue());
        parameterValuesForWS.add(pv);
      }
    }

    try {
      wsStub.processRemoteDocument(executiveLocation, taskID,
              docServiceLocation, docId, asMappingsForWS,
              parameterValuesForWS);
    }
    catch(GateWebServiceFault e) {
      log.error("Exception accessing GaS", e);
      throw new GateServiceClientException("Error accessing GaS", e);
    }
  }

  public void processRemoteDocuments(String taskID, URI docServiceLocation,
          List<gleam.gateservice.client.AnnotationTask> tasks,
          Map<String, String> parameterValues)
          throws GateServiceClientException {
    int numTasks = (tasks == null) ? 0 : tasks.size();
    List<AnnotationTask> tasksForWS = new ArrayList<AnnotationTask>(numTasks);
    if(tasks != null) {
      for(gleam.gateservice.client.AnnotationTask task : tasks) {
        Map<String, String> asMappings = task.getAnnotationSetMappings();
        int numASMappings = (asMappings == null) ? 0 : asMappings.size();
        List<AnnotationSetMapping> asMappingsForWS = new ArrayList<AnnotationSetMapping>(numASMappings);
        if(asMappings != null) {
          for(Map.Entry<String, String> mapping : asMappings.entrySet()) {
            AnnotationSetMapping asm = new AnnotationSetMapping();
            asm.setGateServiceASName("".equals(mapping.getKey()) ? null : mapping
                    .getKey());
            asm.setDocServiceASName("".equals(mapping.getValue()) ? null : mapping
                    .getValue());
            asMappingsForWS.add(asm);
          }
        }

        AnnotationTask wsTask = new AnnotationTask(task.getDocID(),
                asMappingsForWS);
        tasksForWS.add(wsTask);
      }
    }
    
    int numParameterValues = (parameterValues == null) ? 0 : parameterValues
            .size();
    List<ParameterValue> parameterValuesForWS = new ArrayList<ParameterValue>(numParameterValues);
    if(parameterValues != null) {
      for(Map.Entry<String, String> param : parameterValues.entrySet()) {
        ParameterValue pv = new ParameterValue();
        pv.setName(param.getKey());
        pv.setValue(param.getValue());
        parameterValuesForWS.add(pv);
      }
    }

    try {
      wsStub.processRemoteDocuments(executiveLocation, taskID,
              docServiceLocation, tasksForWS,
              parameterValuesForWS);
    }
    catch(GateWebServiceFault e) {
      log.error("Exception accessing GaS", e);
      throw new GateServiceClientException("Error accessing GaS", e);
    }
  }
}
