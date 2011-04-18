/*
 * This file is part of SAFE, licenced under the GNU Lesser General Public
 * Licence.
 *
 * (c) 2006 The University of Sheffield
 * 
 * $Id$
 */
package gleam.executive.proxy.impl;

import gleam.executive.proxy.ExecutiveProxy;
import gleam.executive.proxy.ExecutiveProxyException;
import gleam.executive.proxy.ExecutiveProxyFactory;
import gleam.executive.service.callback.ExecutiveCallbackService;
import gleam.util.cxf.CXFClientUtils;
import gleam.util.logging.TraceWrapper;
import gleam.util.retry.RetryWrapper;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.cxf.Bus;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.springframework.beans.factory.BeanFactory;

/**
 * Implementation of an {@link ExecutiveProxyFactory} that communicates
 * with a callback web service on a running executive.
 */
public class ExecutiveProxyFactoryImpl implements ExecutiveProxyFactory {

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
  private Map<String, ExecutiveProxy> cache = new LinkedHashMap<String, ExecutiveProxy>(
          5, 0.75f, true) {
    @Override
    protected boolean removeEldestEntry(Entry<String, ExecutiveProxy> eldest) {
      return this.size() > MAX_CACHE_SIZE;
    }

  };

  /**
   * Should the web service stub use the "chunked" HTTP transfer
   * encoding?
   */
  private boolean useChunkedEncoding = false;

  public void setUseChunkedEncoding(boolean useChunked) {
    this.useChunkedEncoding = useChunked;
  }

  /**
   * Should the web service stub compress its requests using GZIP and
   * accept compressed responses?
   */
  private boolean useCompression = false;

  public void setUseCompression(boolean useCompression) {
    this.useCompression = useCompression;
  }

  /**
   * Should the proxies trace method calls in the log?
   */
  private boolean traceCalls = false;

  public void setTraceCalls(boolean traceCalls) {
    this.traceCalls = traceCalls;
  }

  /**
   * Should method parameters be included in the call trace? Only
   * relevant if traceCalls is true.
   */
  private boolean traceMethodParams = false;

  public void setTraceMethodParams(boolean traceMethodParams) {
    this.traceMethodParams = traceMethodParams;
  }

  /**
   * Timeout value for web service connections.
   */
  private long timeout = -1;

  public void setTimeout(long timeout) {
    this.timeout = timeout;
  }

  /**
   * Number of automatic retries to attempt if the service throws an
   * exception.
   */
  private int autoRetryCount = 0;

  public void setAutoRetryCount(int autoRetryCount) {
    this.autoRetryCount = autoRetryCount;
  }

  /**
   * Minimum number of milliseconds to wait for the first retry. Only
   * used if autoRetryCount > 0.
   */
  private int minRetryWait = -1;

  public void setMinRetryWait(int minRetryWait) {
    this.minRetryWait = minRetryWait;
  }

  /**
   * Maximum number of milliseconds to wait for the first retry. Only
   * used if autoRetryCount > 0.
   */
  private int maxRetryWait = -1;

  public void setMaxRetryWait(int maxRetryWait) {
    this.maxRetryWait = maxRetryWait;
  }

  /**
   * Backoff multiplier used for multiple retries. Only used if
   * autoRetryCount > 0.
   */
  private float retryBackoffMultiplier = -1f;

  public void setRetryBackoffMultiplier(float retryBackoffMultiplier) {
    this.retryBackoffMultiplier = retryBackoffMultiplier;
  }

  /**
   * The types of exceptions that should cause a retry. Only used if
   * autoRetryCount > 0.
   */
  private Class<? extends Throwable>[] retryExceptionTypes = null;
  
  public void setRetryExceptionTypes(
          Class<? extends Throwable>... retryExceptionTypes) {
    this.retryExceptionTypes = retryExceptionTypes;
  }

  /**
   * Get a {@link DocServiceProxy} for the doc service at the given URL.
   * The URL is first looked up in the cache of already-generated
   * proxies, and if a proxy is found it is returned. If there is no
   * proxy in the cache for this URL a new one is generated and stored.
   */
  public ExecutiveProxy getExecutiveProxy(URI executiveURI)
          throws ExecutiveProxyException {
    String executiveLocation = executiveURI.toString();
    ExecutiveProxy proxy = cache.get(executiveLocation);
    if(proxy == null) {
      JaxWsProxyFactoryBean factory = getFactoryBean();
      factory.setAddress(executiveLocation);

      ExecutiveCallbackService ecs = (ExecutiveCallbackService)factory.create();

      if(!useChunkedEncoding) {
        CXFClientUtils.setAllowChunking(ecs, false);
      }
      if(useCompression) {
        CXFClientUtils.configureForCompression(ecs);
      }
      if(timeout >= 0) {
        CXFClientUtils.setTimeout(ecs, timeout);
      }

      // if we are using auto-retry, wrap up the stub in a retry wrapper
      if(autoRetryCount > 0) {
        RetryWrapper rw = new RetryWrapper(ecs);
        rw.setNumRetries(autoRetryCount);
        if(minRetryWait >= 0) {
          rw.setMinWait(minRetryWait);
        }
        if(maxRetryWait >= 0) {
          rw.setMaxWait(maxRetryWait);
        }
        if(retryBackoffMultiplier > 0) {
          rw.setBackoffMultiplier(retryBackoffMultiplier);
        }
        rw.setRetryExceptionTypes(retryExceptionTypes);
        ecs = rw.getProxy(ExecutiveCallbackService.class);
      }
      
      proxy = new ExecutiveProxyImpl(ecs);
      
      // if we are tracing, wrap up the proxy in a trace wrapper
      if(traceCalls) {
        proxy = TraceWrapper.getTraceWrapper(proxy, ExecutiveProxy.class,
                traceMethodParams, false);
      }
      cache.put(executiveLocation, proxy);
    }
    return proxy;
  }

  /**
   * Creates a new proxy factory bean, configured with the CXF bus from
   * Spring if one is available.
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

    factory.setServiceClass(ExecutiveCallbackService.class);

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
