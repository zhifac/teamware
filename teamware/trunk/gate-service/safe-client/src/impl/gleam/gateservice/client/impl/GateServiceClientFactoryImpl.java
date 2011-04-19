/*
 *  GateServiceClientFactoryImpl.java
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
import gleam.gateservice.client.GateServiceClientFactory;
import gleam.gateservice.endpoint.GateWebService;
import gleam.util.cxf.CXFClientUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.cxf.Bus;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

public class GateServiceClientFactoryImpl implements GateServiceClientFactory,
                                         BeanFactoryAware {

  /**
   * The location of the executive callback service endpoint used for
   * all clients created by this factory. It should not be changed after
   * getGateServiceClient has been called.
   */
  private String executiveLocation;

  /**
   * Should the web service stub use the "chunked" HTTP transfer
   * encoding?
   */
  private boolean useChunkedEncoding = false;

  /**
   * Should the web service stub use GZIP compression of its requests
   * and accept compressed responses?
   */
  private boolean useCompression = true;

  /**
   * If this client factory was created by Spring, this will be the bean
   * factory by which it was created.
   */
  private BeanFactory beanFactory;

  /**
   * Maximum size of the internal cache of client stubs.
   */
  private static final int MAX_CACHE_SIZE = 10;

  /**
   * Cache to store proxies by their URL.
   */
  private Map<String, GateServiceClient> cache = new LinkedHashMap<String, GateServiceClient>(
          5, 0.75f, true) {
    @Override
    protected boolean removeEldestEntry(Entry<String, GateServiceClient> eldest) {
      return this.size() > MAX_CACHE_SIZE;
    }

  };

  public synchronized GateServiceClient getGateServiceClient(URI gasURI)
          throws GateServiceClientException {
    String gasLocation = gasURI.toString();
    GateServiceClient proxy = cache.get(gasLocation);
    if(proxy == null) {
      try {
        JaxWsProxyFactoryBean factory = getFactoryBean();
        factory.setAddress(gasLocation);

        GateWebService gws = (GateWebService)factory.create();

        if(!useChunkedEncoding) {
          CXFClientUtils.setAllowChunking(gws, false);
        }
        if(useCompression) {
          CXFClientUtils.configureForCompression(gws);
        }
        URI executiveURI = new URI(executiveLocation);
        proxy = new GateServiceClientImpl(gws, executiveURI);
        cache.put(gasLocation, proxy);
      }
      catch(URISyntaxException ex) {
        throw new GateServiceClientException(
                "Error creating web service stub for GATE service at "
                        + gasLocation, ex);
      }
    }
    return proxy;
  }

  public void setExecutiveLocation(String executiveLocation) {
    this.executiveLocation = executiveLocation;
  }

  public void setUseChunkedEncoding(boolean useChunked) {
    this.useChunkedEncoding = useChunked;
  }
  
  public void setUseCompression(boolean useCompression) {
    this.useCompression = useCompression;
  }

  /**
   * Get the proxy factory bean.
   * 
   * @return
   */
  private JaxWsProxyFactoryBean getFactoryBean() {
    JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();

    // check for CXF in Spring
    if(beanFactory != null && beanFactory.containsBean("cxf")
            && Bus.class.isAssignableFrom(beanFactory.getType("cxf"))) {
      // we were created from a Spring bean factory that includes CXF
      // (maybe it defines some other services), so use the CXF bus
      // for our proxy too.
      factory.setBus((Bus)beanFactory.getBean("cxf", Bus.class));
    }

    Map<String, Object> props = new HashMap<String, Object>();
    props.put("mtom-enabled", Boolean.TRUE);
    factory.setProperties(props);
    factory.setServiceClass(GateWebService.class);

    return factory;
  }

  /**
   * Called by Spring if we were created from a Spring application
   * context.
   */
  public void setBeanFactory(BeanFactory beanFactory) {
    this.beanFactory = beanFactory;
  }
}
