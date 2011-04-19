/*
 *  DocServiceProxyFactoryImpl.java
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
package gleam.docservice.proxy.impl;

import gleam.docservice.DocService;
import gleam.docservice.proxy.DSProxyException;
import gleam.docservice.proxy.DocServiceProxy;
import gleam.docservice.proxy.DocServiceProxyFactory;
import gleam.util.cxf.CXFClientUtils;
import gleam.util.logging.TraceWrapper;

import java.net.URI;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.cxf.Bus;
import org.apache.cxf.jaxb.JAXBDataBinding;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

public class DocServiceProxyFactoryImpl implements DocServiceProxyFactory,
                                       BeanFactoryAware {

  /**
   * Maximum size of the internal cache of client stubs.
   */
  private static final int MAX_CACHE_SIZE = 10;

  /**
   * Cache to store proxies by their URL.
   */
  private Map<String, DocServiceProxy> cache = new LinkedHashMap<String, DocServiceProxy>(
          5, 0.75f, true) {
    @Override
    protected boolean removeEldestEntry(Entry<String, DocServiceProxy> eldest) {
      return this.size() > MAX_CACHE_SIZE;
    }

  };

  /**
   * Should the proxies trace method calls in the log?
   */
  private boolean traceCalls = false;

  /**
   * Should method parameters be included in the call trace? Only
   * relevant if traceCalls is true.
   */
  private boolean traceMethodParams = false;

  /**
   * Should the web service stub use the "chunked" HTTP transfer
   * encoding? Defaults to false.
   */
  private boolean useChunkedEncoding = false;

  /**
   * Should the web service stub use GZIP compression of requests and
   * handle compressed responses?
   */
  private boolean useCompression = true;

  /**
   * HTTP timeout for web service calls.
   */
  private long timeout = -1;

  /**
   * Interval between keepalive calls, in seconds. Default is 0, meaning
   * no keepalives.
   */
  private int keepaliveInterval = 0;

  /**
   * If this client factory was created by Spring, this will be the bean
   * factory by which it was created.
   */
  private BeanFactory beanFactory;

  public void setTraceCalls(boolean trace) {
    this.traceCalls = trace;
  }

  public void setTraceMethodParameters(boolean trace) {
    this.traceMethodParams = trace;
  }

  public void setUseChunkedEncoding(boolean useChunked) {
    this.useChunkedEncoding = useChunked;
  }

  public void setUseCompression(boolean useCompression) {
    this.useCompression = useCompression;
  }

  public void setTimeout(long timeout) {
    this.timeout = timeout;
  }

  public void setKeepaliveInterval(int keepaliveInterval) {
    this.keepaliveInterval = keepaliveInterval;
  }

  /**
   * Get a {@link DocServiceProxy} for the doc service at the given URL.
   * The URL is first looked up in the cache of already-generated
   * proxies, and if a proxy is found it is returned. If there is no
   * proxy in the cache for this URL a new one is generated and stored.
   */
  public DocServiceProxy getDocServiceProxy(URI docServiceURI)
          throws DSProxyException {
    String docServiceLocation = docServiceURI.toString();
    DocServiceProxy proxy = cache.get(docServiceLocation);
    if(proxy == null) {
      JaxWsProxyFactoryBean factory = getFactoryBean();
      factory.setAddress(docServiceLocation);

      DocService ds = (DocService)factory.create();

      if(!useChunkedEncoding) {
        CXFClientUtils.setAllowChunking(ds, false);
      }
      if(useCompression) {
        CXFClientUtils.configureForCompression(ds);
      }
      if(timeout >= 0) {
        CXFClientUtils.setTimeout(ds, timeout);
      }
      proxy = new DocServiceProxyImpl(ds);
      if(keepaliveInterval > 0) {
        ((DocServiceProxyImpl)proxy).setKeepaliveInterval(keepaliveInterval);
      }
      // Wrap the proxy up for tracing if required
      if(traceCalls) {
        proxy = TraceWrapper.getTraceWrapper(proxy, DocServiceProxy.class,
                traceMethodParams, false);
      }
      cache.put(docServiceLocation, proxy);
    }
    return proxy;
  }

  /**
   * Creates a new proxy factory bean, configured to use MTOM and with
   * the CXF bus from Spring if one is available. The data binding is
   * configured with the concrete IAA classes.
   * 
   * @return
   */
  protected JaxWsProxyFactoryBean getFactoryBean() {
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
    JAXBDataBinding db = new JAXBDataBinding();
    // add IAA classes to data binding
    db.setExtraClass(new Class[] {
        gleam.docservice.iaa.AllWaysFMeasureDetail.class,
        gleam.docservice.iaa.AllWaysKappaDetail.class,
        gleam.docservice.iaa.PairwiseFMeasureDetail.class,
        gleam.docservice.iaa.PairwiseKappaDetail.class});
    factory.setDataBinding(db);
    factory.setServiceClass(DocService.class);

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
